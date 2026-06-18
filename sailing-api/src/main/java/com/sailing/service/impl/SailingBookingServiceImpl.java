package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.dto.BookingApplyDTO;
import com.sailing.dto.BookingApproveDTO;
import com.sailing.dto.BookingQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.entity.SailingBooking;
import com.sailing.entity.SysUser;
import com.sailing.mapper.BoatMapper;
import com.sailing.mapper.SailingBookingMapper;
import com.sailing.mapper.SysUserMapper;
import com.sailing.service.BookingValidationService;
import com.sailing.service.SailingBookingService;
import com.sailing.vo.BookingVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SailingBookingServiceImpl extends ServiceImpl<SailingBookingMapper, SailingBooking> implements SailingBookingService {

    @Autowired
    private BookingValidationService bookingValidationService;

    @Autowired
    private BoatMapper boatMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional
    public void applyBooking(BookingApplyDTO bookingApplyDTO) {
        SailingBooking booking = new SailingBooking();
        BeanUtils.copyProperties(bookingApplyDTO, booking);
        booking.setBookingStatus(SailingBooking.STATUS_PENDING);
        booking.setApplyTime(LocalDateTime.now());
        bookingValidationService.validateBooking(booking);
        baseMapper.insert(booking);
    }

    @Override
    @Transactional
    public void approveBooking(BookingApproveDTO bookingApproveDTO, Long approveBy) {
        SailingBooking booking = baseMapper.selectById(bookingApproveDTO.getId());
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!SailingBooking.STATUS_PENDING.equals(booking.getBookingStatus())) {
            throw new BusinessException("只有待审批状态的预约才能审批");
        }
        if (!SailingBooking.STATUS_APPROVED.equals(bookingApproveDTO.getBookingStatus())
                && !SailingBooking.STATUS_REJECTED.equals(bookingApproveDTO.getBookingStatus())) {
            throw new BusinessException("审批状态不正确");
        }
        if (SailingBooking.STATUS_APPROVED.equals(bookingApproveDTO.getBookingStatus())) {
            bookingValidationService.validateBooking(booking);
        }
        booking.setBookingStatus(bookingApproveDTO.getBookingStatus());
        booking.setApproveTime(LocalDateTime.now());
        booking.setApproveBy(approveBy);
        if (StringUtils.hasText(bookingApproveDTO.getRemark())) {
            booking.setRemark(bookingApproveDTO.getRemark());
        }
        baseMapper.updateById(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id, Long memberId) {
        SailingBooking booking = baseMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!booking.getMemberId().equals(memberId)) {
            throw new BusinessException("无权取消他人的预约");
        }
        if (SailingBooking.STATUS_COMPLETED.equals(booking.getBookingStatus())
                || SailingBooking.STATUS_CANCELLED.equals(booking.getBookingStatus())
                || SailingBooking.STATUS_REJECTED.equals(booking.getBookingStatus())) {
            throw new BusinessException("当前状态无法取消");
        }
        booking.setBookingStatus(SailingBooking.STATUS_CANCELLED);
        baseMapper.updateById(booking);
    }

    @Override
    @Transactional
    public void completeBooking(Long id) {
        SailingBooking booking = baseMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!SailingBooking.STATUS_APPROVED.equals(booking.getBookingStatus())) {
            throw new BusinessException("只有已通过状态的预约才能完成");
        }
        booking.setBookingStatus(SailingBooking.STATUS_COMPLETED);
        baseMapper.updateById(booking);
    }

    @Override
    public BookingVO getBookingById(Long id) {
        SailingBooking booking = baseMapper.selectById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        return convertToVO(booking);
    }

    @Override
    public IPage<BookingVO> getBookingPage(Page<BookingVO> page, BookingQueryDTO queryDTO) {
        LambdaQueryWrapper<SailingBooking> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            if (queryDTO.getMemberId() != null) {
                wrapper.eq(SailingBooking::getMemberId, queryDTO.getMemberId());
            }
            if (queryDTO.getCoachId() != null) {
                wrapper.eq(SailingBooking::getCoachId, queryDTO.getCoachId());
            }
            if (queryDTO.getBoatId() != null) {
                wrapper.eq(SailingBooking::getBoatId, queryDTO.getBoatId());
            }
            if (StringUtils.hasText(queryDTO.getBookingStatus())) {
                wrapper.eq(SailingBooking::getBookingStatus, queryDTO.getBookingStatus());
            }
            if (queryDTO.getStartDate() != null) {
                wrapper.ge(SailingBooking::getBookingDate, queryDTO.getStartDate());
            }
            if (queryDTO.getEndDate() != null) {
                wrapper.le(SailingBooking::getBookingDate, queryDTO.getEndDate());
            }
        }
        wrapper.orderByDesc(SailingBooking::getCreateTime);
        IPage<SailingBooking> bookingPage = baseMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        List<SailingBooking> bookings = bookingPage.getRecords();
        IPage<BookingVO> voPage = new Page<>(bookingPage.getCurrent(), bookingPage.getSize(), bookingPage.getTotal());
        voPage.setRecords(bookings.stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public SailingBooking getBookingEntityById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public void updateBookingById(SailingBooking booking) {
        baseMapper.updateById(booking);
    }

    private BookingVO convertToVO(SailingBooking booking) {
        BookingVO vo = new BookingVO();
        BeanUtils.copyProperties(booking, vo);
        vo.setBookingStatus(booking.getBookingStatus());
        vo.setStatusName(getBookingStatusDesc(booking.getBookingStatus()));
        vo.setTimeSlotName(getTimeSlotDesc(booking.getTimeSlot()));
        loadUserNames(vo, booking);
        loadBoatInfo(vo, booking);
        return vo;
    }

    private void loadUserNames(BookingVO vo, SailingBooking booking) {
        Set<Long> userIds = new HashSet<>();
        if (booking.getMemberId() != null) {
            userIds.add(booking.getMemberId());
        }
        if (booking.getCoachId() != null) {
            userIds.add(booking.getCoachId());
        }
        if (booking.getApproveBy() != null) {
            userIds.add(booking.getApproveBy());
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
        if (booking.getMemberId() != null && userMap.containsKey(booking.getMemberId())) {
            SysUser member = userMap.get(booking.getMemberId());
            vo.setMemberName(member.getRealName());
            vo.setMemberPhone(member.getPhone());
        }
        if (booking.getCoachId() != null && userMap.containsKey(booking.getCoachId())) {
            SysUser coach = userMap.get(booking.getCoachId());
            vo.setCoachName(coach.getRealName());
        }
    }

    private void loadBoatInfo(BookingVO vo, SailingBooking booking) {
        if (booking.getBoatId() == null) {
            return;
        }
        Boat boat = boatMapper.selectById(booking.getBoatId());
        if (boat != null) {
            vo.setBoatName(boat.getName());
        }
    }

    private String getBookingStatusDesc(String status) {
        switch (status) {
            case SailingBooking.STATUS_PENDING:
                return "待审批";
            case SailingBooking.STATUS_APPROVED:
                return "已通过";
            case SailingBooking.STATUS_REJECTED:
                return "已拒绝";
            case SailingBooking.STATUS_CANCELLED:
                return "已取消";
            case SailingBooking.STATUS_COMPLETED:
                return "已完成";
            default:
                return "未知";
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
