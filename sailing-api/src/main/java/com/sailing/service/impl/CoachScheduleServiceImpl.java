package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.dto.CoachScheduleDTO;
import com.sailing.entity.CoachSchedule;
import com.sailing.mapper.CoachScheduleMapper;
import com.sailing.service.CoachScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
public class CoachScheduleServiceImpl extends ServiceImpl<CoachScheduleMapper, CoachSchedule> implements CoachScheduleService {

    @Override
    public void addSchedule(CoachScheduleDTO coachScheduleDTO) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getCoachId, coachScheduleDTO.getCoachId());
        wrapper.eq(CoachSchedule::getScheduleDate, coachScheduleDTO.getScheduleDate());
        wrapper.eq(CoachSchedule::getTimeSlot, coachScheduleDTO.getTimeSlot());
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该教练此日期时段已有排班");
        }
        CoachSchedule schedule = new CoachSchedule();
        BeanUtils.copyProperties(coachScheduleDTO, schedule);
        if (schedule.getIsOnDuty() == null) {
            schedule.setIsOnDuty(CoachSchedule.ON_DUTY_YES);
        }
        baseMapper.insert(schedule);
    }

    @Override
    public void updateSchedule(CoachScheduleDTO coachScheduleDTO) {
        CoachSchedule exist = baseMapper.selectById(coachScheduleDTO.getId());
        if (exist == null) {
            throw new BusinessException("排班记录不存在");
        }
        if (coachScheduleDTO.getCoachId() != null && coachScheduleDTO.getScheduleDate() != null
                && StringUtils.hasText(coachScheduleDTO.getTimeSlot())) {
            LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoachSchedule::getCoachId, coachScheduleDTO.getCoachId());
            wrapper.eq(CoachSchedule::getScheduleDate, coachScheduleDTO.getScheduleDate());
            wrapper.eq(CoachSchedule::getTimeSlot, coachScheduleDTO.getTimeSlot());
            wrapper.ne(CoachSchedule::getId, coachScheduleDTO.getId());
            Long count = baseMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("该教练此日期时段已有排班");
            }
        }
        CoachSchedule schedule = new CoachSchedule();
        BeanUtils.copyProperties(coachScheduleDTO, schedule);
        baseMapper.updateById(schedule);
    }

    @Override
    public void deleteSchedule(Long id) {
        CoachSchedule exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("排班记录不存在");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public CoachSchedule getScheduleById(Long id) {
        CoachSchedule schedule = baseMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException("排班记录不存在");
        }
        return schedule;
    }

    @Override
    public IPage<CoachSchedule> getSchedulePage(Page<CoachSchedule> page, Long coachId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        if (coachId != null) {
            wrapper.eq(CoachSchedule::getCoachId, coachId);
        }
        if (startDate != null) {
            wrapper.ge(CoachSchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(CoachSchedule::getScheduleDate, endDate);
        }
        wrapper.orderByAsc(CoachSchedule::getScheduleDate, CoachSchedule::getCoachId);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public List<CoachSchedule> getScheduleByDate(LocalDate scheduleDate) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getScheduleDate, scheduleDate);
        wrapper.orderByAsc(CoachSchedule::getCoachId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CoachSchedule> getCoachSchedule(Long coachId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        if (coachId != null) {
            wrapper.eq(CoachSchedule::getCoachId, coachId);
        }
        if (startDate != null) {
            wrapper.ge(CoachSchedule::getScheduleDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(CoachSchedule::getScheduleDate, endDate);
        }
        wrapper.orderByAsc(CoachSchedule::getScheduleDate);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public boolean isCoachOnDuty(Long coachId, LocalDate scheduleDate, String timeSlot) {
        LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CoachSchedule::getCoachId, coachId);
        wrapper.eq(CoachSchedule::getScheduleDate, scheduleDate);
        wrapper.eq(CoachSchedule::getIsOnDuty, CoachSchedule.ON_DUTY_YES);
        wrapper.and(w -> w.eq(CoachSchedule::getTimeSlot, CoachSchedule.TIME_SLOT_FULLDAY)
                .or().eq(CoachSchedule::getTimeSlot, timeSlot));
        Long count = baseMapper.selectCount(wrapper);
        return count > 0;
    }
}
