package com.sailing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.common.SecurityUser;
import com.sailing.dto.BookingQueryDTO;
import com.sailing.dto.SailingRecordDTO;
import com.sailing.entity.CoachSchedule;
import com.sailing.entity.SailingBooking;
import com.sailing.service.CoachScheduleService;
import com.sailing.service.SailingBookingService;
import com.sailing.service.SailingRecordService;
import com.sailing.vo.BookingVO;
import com.sailing.vo.CoachScheduleVO;
import com.sailing.vo.SailingRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/coach")
@PreAuthorize("hasAnyRole('COACH','ADMIN')")
public class CoachController {

    @Autowired
    private CoachScheduleService coachScheduleService;

    @Autowired
    private SailingBookingService sailingBookingService;

    @Autowired
    private SailingRecordService sailingRecordService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.getUserId();
    }

    private String getTimeSlotName(String timeSlot) {
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

    private String getOnDutyName(Integer isOnDuty) {
        return CoachSchedule.ON_DUTY_YES.equals(isOnDuty) ? "值班" : "休息";
    }

    private CoachScheduleVO convertToScheduleVO(CoachSchedule schedule) {
        CoachScheduleVO vo = new CoachScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        vo.setTimeSlotName(getTimeSlotName(schedule.getTimeSlot()));
        vo.setOnDutyName(getOnDutyName(schedule.getIsOnDuty()));
        return vo;
    }

    @GetMapping("/schedule")
    public Result<List<CoachScheduleVO>> getMySchedule(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Long coachId = getCurrentUserId();
        List<CoachSchedule> schedules = coachScheduleService.getCoachSchedule(coachId, startDate, endDate);
        List<CoachScheduleVO> voList = schedules.stream()
                .map(this::convertToScheduleVO)
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @GetMapping("/bookings")
    public Result<IPage<BookingVO>> getMyBookings(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long coachId = getCurrentUserId();
        BookingQueryDTO queryDTO = new BookingQueryDTO();
        queryDTO.setCoachId(coachId);
        queryDTO.setBookingStatus(status);
        queryDTO.setStartDate(startDate);
        queryDTO.setEndDate(endDate);
        Page<BookingVO> page = new Page<>(pageNum, pageSize);
        IPage<BookingVO> result = sailingBookingService.getBookingPage(page, queryDTO);
        return Result.success(result);
    }

    @PutMapping("/bookings/{id}/confirm")
    public Result<Void> confirmBooking(@PathVariable Long id) {
        Long coachId = getCurrentUserId();
        SailingBooking booking = sailingBookingService.getBookingEntityById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        booking.setBookingStatus(SailingBooking.STATUS_APPROVED);
        if (booking.getCoachId() == null) {
            booking.setCoachId(coachId);
        }
        sailingBookingService.updateBookingById(booking);
        return Result.success();
    }

    @PostMapping("/records")
    public Result<Long> createSailingRecord(@Valid @RequestBody SailingRecordDTO sailingRecordDTO) {
        Long coachId = getCurrentUserId();
        sailingRecordDTO.setCoachId(coachId);
        sailingRecordService.addRecord(sailingRecordDTO, coachId);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<IPage<SailingRecordVO>> getMySailingRecords(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long coachId = getCurrentUserId();
        Page<SailingRecordVO> page = new Page<>(pageNum, pageSize);
        IPage<SailingRecordVO> result = sailingRecordService.getRecordPage(page, null, coachId, null, startDate, endDate);
        return Result.success(result);
    }
}
