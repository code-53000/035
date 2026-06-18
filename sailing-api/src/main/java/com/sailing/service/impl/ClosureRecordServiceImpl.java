package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.dto.ClosureRecordDTO;
import com.sailing.entity.ClosureRecord;
import com.sailing.mapper.ClosureRecordMapper;
import com.sailing.service.ClosureRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class ClosureRecordServiceImpl extends ServiceImpl<ClosureRecordMapper, ClosureRecord> implements ClosureRecordService {

    @Override
    public void addClosureRecord(ClosureRecordDTO closureRecordDTO, Long createdBy) {
        ClosureRecord record = new ClosureRecord();
        BeanUtils.copyProperties(closureRecordDTO, record);
        record.setCreatedBy(createdBy);
        record.setStatus(ClosureRecord.STATUS_ACTIVE);
        baseMapper.insert(record);
    }

    @Override
    public void cancelClosure(Long id, Long updatedBy) {
        ClosureRecord exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("封场记录不存在");
        }
        if (ClosureRecord.STATUS_CANCELLED.equals(exist.getStatus())) {
            throw new BusinessException("封场记录已取消");
        }
        exist.setStatus(ClosureRecord.STATUS_CANCELLED);
        baseMapper.updateById(exist);
    }

    @Override
    public ClosureRecord getById(Long id) {
        ClosureRecord record = baseMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("封场记录不存在");
        }
        return record;
    }

    @Override
    public IPage<ClosureRecord> getClosureRecordPage(Page<ClosureRecord> page, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ClosureRecord> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(ClosureRecord::getClosureDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ClosureRecord::getClosureDate, endDate);
        }
        wrapper.orderByDesc(ClosureRecord::getClosureDate);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ClosureRecord> getClosureRecordList(LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<ClosureRecord> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(ClosureRecord::getClosureDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ClosureRecord::getClosureDate, endDate);
        }
        wrapper.eq(ClosureRecord::getStatus, ClosureRecord.STATUS_ACTIVE);
        wrapper.orderByAsc(ClosureRecord::getClosureDate);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<ClosureRecord> getClosuresByDate(LocalDate date) {
        LambdaQueryWrapper<ClosureRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClosureRecord::getClosureDate, date);
        wrapper.eq(ClosureRecord::getStatus, ClosureRecord.STATUS_ACTIVE);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean isDateClosed(LocalDate date, String timeSlot) {
        List<ClosureRecord> closures = getClosuresByDate(date);
        if (CollectionUtils.isEmpty(closures)) {
            return false;
        }
        for (ClosureRecord cr : closures) {
            if (ClosureRecord.TIME_SLOT_FULLDAY.equals(cr.getTimeSlot())) {
                return true;
            }
            if (cr.getTimeSlot().equals(timeSlot)) {
                return true;
            }
        }
        return false;
    }
}
