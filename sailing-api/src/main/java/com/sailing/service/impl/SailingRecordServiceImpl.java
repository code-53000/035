package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.dto.SailingRecordDTO;
import com.sailing.entity.Boat;
import com.sailing.entity.SailingBooking;
import com.sailing.entity.SailingRecord;
import com.sailing.entity.SysUser;
import com.sailing.mapper.BoatMapper;
import com.sailing.mapper.SailingBookingMapper;
import com.sailing.mapper.SailingRecordMapper;
import com.sailing.mapper.SysUserMapper;
import com.sailing.service.SailingBookingService;
import com.sailing.service.SailingRecordService;
import com.sailing.vo.SailingRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SailingRecordServiceImpl extends ServiceImpl<SailingRecordMapper, SailingRecord> implements SailingRecordService {

    @Autowired
    private SailingBookingMapper sailingBookingMapper;

    @Autowired
    private SailingBookingService sailingBookingService;

    @Autowired
    private BoatMapper boatMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void addRecord(SailingRecordDTO sailingRecordDTO, Long createdBy) {
        SailingRecord record = new SailingRecord();
        BeanUtils.copyProperties(sailingRecordDTO, record);
        record.setCreatedBy(createdBy);
        baseMapper.insert(record);
        if (record.getBookingId() != null) {
            SailingBooking booking = sailingBookingMapper.selectById(record.getBookingId());
            if (booking != null && SailingBooking.STATUS_APPROVED.equals(booking.getBookingStatus())) {
                booking.setBookingStatus(SailingBooking.STATUS_COMPLETED);
                sailingBookingMapper.updateById(booking);
            }
        }
    }

    @Override
    public SailingRecordVO getRecordById(Long id) {
        SailingRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("出海记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    public IPage<SailingRecordVO> getRecordPage(Page<SailingRecordVO> page, Long memberId, Long coachId, Long boatId,
                                                LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<SailingRecord> wrapper = new LambdaQueryWrapper<>();
        if (memberId != null) {
            wrapper.eq(SailingRecord::getMemberId, memberId);
        }
        if (coachId != null) {
            wrapper.eq(SailingRecord::getCoachId, coachId);
        }
        if (boatId != null) {
            wrapper.eq(SailingRecord::getBoatId, boatId);
        }
        if (startDate != null) {
            wrapper.ge(SailingRecord::getSailDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(SailingRecord::getSailDate, endDate);
        }
        wrapper.orderByDesc(SailingRecord::getCreateTime);
        IPage<SailingRecord> recordPage = baseMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        IPage<SailingRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        voPage.setRecords(recordPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    private SailingRecordVO convertToVO(SailingRecord record) {
        SailingRecordVO vo = new SailingRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setTimeSlotName(getTimeSlotDesc(record.getTimeSlot()));
        loadUserNames(vo, record);
        loadBoatInfo(vo, record);
        return vo;
    }

    private void loadUserNames(SailingRecordVO vo, SailingRecord record) {
        Set<Long> userIds = new HashSet<>();
        if (record.getMemberId() != null) {
            userIds.add(record.getMemberId());
        }
        if (record.getCoachId() != null) {
            userIds.add(record.getCoachId());
        }
        if (record.getCreatedBy() != null) {
            userIds.add(record.getCreatedBy());
        }
        if (userIds.isEmpty()) {
            return;
        }
        List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
        if (users == null || users.isEmpty()) {
            return;
        }
        Map<Long, SysUser> userMap = users.stream()
                .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        if (record.getMemberId() != null && userMap.containsKey(record.getMemberId())) {
            SysUser member = userMap.get(record.getMemberId());
            vo.setMemberName(member.getRealName());
            vo.setMemberPhone(member.getPhone());
        }
        if (record.getCoachId() != null && userMap.containsKey(record.getCoachId())) {
            SysUser coach = userMap.get(record.getCoachId());
            vo.setCoachName(coach.getRealName());
        }
    }

    private void loadBoatInfo(SailingRecordVO vo, SailingRecord record) {
        if (record.getBoatId() == null) {
            return;
        }
        Boat boat = boatMapper.selectById(record.getBoatId());
        if (boat != null) {
            vo.setBoatName(boat.getName());
        }
    }

    private String getTimeSlotDesc(String timeSlot) {
        switch (timeSlot) {
            case "MORNING":
                return "上午";
            case "AFTERNOON":
                return "下午";
            case "FULLDAY":
                return "全天";
            default:
                return "未知";
        }
    }
}
