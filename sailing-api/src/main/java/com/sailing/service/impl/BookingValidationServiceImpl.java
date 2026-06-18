package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sailing.common.BusinessException;
import com.sailing.entity.Boat;
import com.sailing.entity.ClosureRecord;
import com.sailing.entity.CoachSchedule;
import com.sailing.entity.SailingBooking;
import com.sailing.mapper.BoatMapper;
import com.sailing.mapper.ClosureRecordMapper;
import com.sailing.mapper.CoachScheduleMapper;
import com.sailing.mapper.SailingBookingMapper;
import com.sailing.service.BookingValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class BookingValidationServiceImpl implements BookingValidationService {

    @Autowired
    private BoatMapper boatMapper;

    @Autowired
    private ClosureRecordMapper closureRecordMapper;

    @Autowired
    private CoachScheduleMapper coachScheduleMapper;

    @Autowired
    private SailingBookingMapper sailingBookingMapper;

    private final List<ValidationStep> validationSteps;

    public BookingValidationServiceImpl() {
        this.validationSteps = new ArrayList<>();
        this.validationSteps.add(new BoatStatusValidationStep());
        this.validationSteps.add(new ClosureValidationStep());
        this.validationSteps.add(new BoatBookingConflictValidationStep());
        this.validationSteps.add(new CoachAvailabilityValidationStep());
    }

    @Override
    public void validateBooking(SailingBooking booking) {
        for (ValidationStep step : validationSteps) {
            step.validate(booking);
        }
    }

    private class BoatStatusValidationStep implements ValidationStep {
        @Override
        public void validate(SailingBooking booking) {
            Boat boat = boatMapper.selectById(booking.getBoatId());
            if (boat == null) {
                throw new BusinessException("船只不存在");
            }
            if (!Boat.STATUS_AVAILABLE.equals(boat.getStatus())) {
                String desc = "未知";
                switch (boat.getStatus()) {
                    case Boat.STATUS_MAINTENANCE:
                        desc = "维护中";
                        break;
                    case Boat.STATUS_RETIRED:
                        desc = "已报废";
                        break;
                }
                throw new BusinessException("船只当前状态为" + desc + "，不可预约");
            }
        }
    }

    private class ClosureValidationStep implements ValidationStep {
        @Override
        public void validate(SailingBooking booking) {
            LambdaQueryWrapper<ClosureRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ClosureRecord::getClosureDate, booking.getBookingDate());
            wrapper.eq(ClosureRecord::getStatus, ClosureRecord.STATUS_ACTIVE);
            wrapper.and(w -> w.eq(ClosureRecord::getTimeSlot, ClosureRecord.TIME_SLOT_FULLDAY)
                    .or().eq(ClosureRecord::getTimeSlot, booking.getTimeSlot()));
            List<ClosureRecord> closures = closureRecordMapper.selectList(wrapper);
            if (!CollectionUtils.isEmpty(closures)) {
                StringBuilder reasons = new StringBuilder();
                for (ClosureRecord cr : closures) {
                    if (reasons.length() > 0) {
                        reasons.append("；");
                    }
                    reasons.append(cr.getReason());
                }
                throw new BusinessException("该时段已封场：" + reasons.toString());
            }
        }
    }

    private class BoatBookingConflictValidationStep implements ValidationStep {
        @Override
        public void validate(SailingBooking booking) {
            LambdaQueryWrapper<SailingBooking> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SailingBooking::getBoatId, booking.getBoatId());
            wrapper.eq(SailingBooking::getBookingDate, booking.getBookingDate());
            wrapper.eq(SailingBooking::getTimeSlot, booking.getTimeSlot());
            wrapper.notIn(SailingBooking::getBookingStatus,
                    Arrays.asList(SailingBooking.STATUS_CANCELLED, SailingBooking.STATUS_REJECTED));
            if (booking.getId() != null) {
                wrapper.ne(SailingBooking::getId, booking.getId());
            }
            Long count = sailingBookingMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("该船只此时段已有未取消的预约");
            }
        }
    }

    private class CoachAvailabilityValidationStep implements ValidationStep {
        @Override
        public void validate(SailingBooking booking) {
            if (booking.getCoachId() == null) {
                return;
            }
            LambdaQueryWrapper<CoachSchedule> scheduleWrapper = new LambdaQueryWrapper<>();
            scheduleWrapper.eq(CoachSchedule::getCoachId, booking.getCoachId());
            scheduleWrapper.eq(CoachSchedule::getScheduleDate, booking.getBookingDate());
            scheduleWrapper.eq(CoachSchedule::getIsOnDuty, CoachSchedule.ON_DUTY_YES);
            scheduleWrapper.and(w -> w.eq(CoachSchedule::getTimeSlot, CoachSchedule.TIME_SLOT_FULLDAY)
                    .or().eq(CoachSchedule::getTimeSlot, booking.getTimeSlot()));
            Long scheduleCount = coachScheduleMapper.selectCount(scheduleWrapper);
            if (scheduleCount == 0) {
                throw new BusinessException("该教练此时段未排班或不值班");
            }
            LambdaQueryWrapper<SailingBooking> bookingWrapper = new LambdaQueryWrapper<>();
            bookingWrapper.eq(SailingBooking::getCoachId, booking.getCoachId());
            bookingWrapper.eq(SailingBooking::getBookingDate, booking.getBookingDate());
            bookingWrapper.eq(SailingBooking::getTimeSlot, booking.getTimeSlot());
            bookingWrapper.notIn(SailingBooking::getBookingStatus,
                    Arrays.asList(SailingBooking.STATUS_CANCELLED, SailingBooking.STATUS_REJECTED));
            if (booking.getId() != null) {
                bookingWrapper.ne(SailingBooking::getId, booking.getId());
            }
            Long bookingCount = sailingBookingMapper.selectCount(bookingWrapper);
            if (bookingCount > 0) {
                throw new BusinessException("该教练此时段已有未取消的预约");
            }
        }
    }
}
