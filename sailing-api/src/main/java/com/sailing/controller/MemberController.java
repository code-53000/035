package com.sailing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.common.SecurityUser;
import com.sailing.dto.BoatQueryDTO;
import com.sailing.dto.BookingApplyDTO;
import com.sailing.dto.BookingQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.entity.CoachSchedule;
import com.sailing.entity.SailingBooking;
import com.sailing.service.BoatService;
import com.sailing.service.BookingValidationService;
import com.sailing.service.SailingBookingService;
import com.sailing.service.SailingRecordService;
import com.sailing.vo.AvailableSlotVO;
import com.sailing.vo.BoatVO;
import com.sailing.vo.BookingVO;
import com.sailing.vo.SailingRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/member")
@PreAuthorize("hasAnyRole('MEMBER','COACH','ADMIN')")
public class MemberController {

    @Autowired
    private BoatService boatService;

    @Autowired
    private SailingBookingService sailingBookingService;

    @Autowired
    private BookingValidationService bookingValidationService;

    @Autowired
    private SailingRecordService sailingRecordService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.getUserId();
    }

    @GetMapping("/boats")
    public Result<IPage<BoatVO>> getAvailableBoats(BoatQueryDTO boatQueryDTO) {
        boatQueryDTO.setStatus(Boat.STATUS_AVAILABLE);
        Page<BoatVO> page = new Page<>(boatQueryDTO.getPageNum(), boatQueryDTO.getPageSize());
        IPage<BoatVO> result = boatService.getBoatPage(page, boatQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/boats/{id}")
    public Result<BoatVO> getBoatDetail(@PathVariable Long id) {
        BoatVO boatVO = boatService.getBoatById(id);
        return Result.success(boatVO);
    }

    @GetMapping("/bookings/available-slots")
    public Result<List<AvailableSlotVO>> getAvailableSlots(
            @RequestParam LocalDate date,
            @RequestParam Long boatId) {
        String[] slots = {CoachSchedule.TIME_SLOT_MORNING, CoachSchedule.TIME_SLOT_AFTERNOON, CoachSchedule.TIME_SLOT_FULLDAY};
        String[] slotNames = {"上午", "下午", "全天"};
        List<AvailableSlotVO> result = new ArrayList<>();
        for (int i = 0; i < slots.length; i++) {
            String slot = slots[i];
            AvailableSlotVO vo = new AvailableSlotVO();
            vo.setDate(date);
            vo.setTimeSlot(slot);
            vo.setTimeSlotName(slotNames[i]);
            try {
                SailingBooking booking = new SailingBooking();
                booking.setBoatId(boatId);
                booking.setBookingDate(date);
                booking.setTimeSlot(slot);
                bookingValidationService.validateBooking(booking);
                vo.setAvailable(true);
            } catch (BusinessException e) {
                vo.setAvailable(false);
                vo.setReason(e.getMessage());
            }
            result.add(vo);
        }
        return Result.success(result);
    }

    @PostMapping("/bookings")
    public Result<Long> applyBooking(@Valid @RequestBody BookingApplyDTO bookingApplyDTO) {
        Long memberId = getCurrentUserId();
        bookingApplyDTO.setMemberId(memberId);
        sailingBookingService.applyBooking(bookingApplyDTO);
        return Result.success();
    }

    @GetMapping("/bookings")
    public Result<IPage<BookingVO>> getMyBookings(BookingQueryDTO bookingQueryDTO) {
        Long memberId = getCurrentUserId();
        bookingQueryDTO.setMemberId(memberId);
        Page<BookingVO> page = new Page<>(bookingQueryDTO.getPageNum(), bookingQueryDTO.getPageSize());
        IPage<BookingVO> result = sailingBookingService.getBookingPage(page, bookingQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/bookings/{id}")
    public Result<BookingVO> getBookingDetail(@PathVariable Long id) {
        Long memberId = getCurrentUserId();
        BookingVO bookingVO = sailingBookingService.getBookingById(id);
        if (!memberId.equals(bookingVO.getMemberId())) {
            throw new BusinessException("无权查看他人的预约");
        }
        return Result.success(bookingVO);
    }

    @PutMapping("/bookings/{id}/cancel")
    public Result<Void> cancelMyBooking(@PathVariable Long id) {
        Long memberId = getCurrentUserId();
        SailingBooking booking = sailingBookingService.getBookingEntityById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (!memberId.equals(booking.getMemberId())) {
            throw new BusinessException("无权取消他人的预约");
        }
        if (!SailingBooking.STATUS_PENDING.equals(booking.getBookingStatus())
                && !SailingBooking.STATUS_APPROVED.equals(booking.getBookingStatus())) {
            throw new BusinessException("只有待审批或已通过状态的预约才能取消");
        }
        sailingBookingService.cancelBooking(id, memberId);
        return Result.success();
    }

    @GetMapping("/records")
    public Result<IPage<SailingRecordVO>> getMyRecords(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Long memberId = getCurrentUserId();
        Integer pageNumber = page != null ? page : pageNum;
        Page<SailingRecordVO> pageParam = new Page<>(pageNumber, pageSize);
        IPage<SailingRecordVO> result = sailingRecordService.getRecordPage(pageParam, memberId, null, null, startDate, endDate);
        return Result.success(result);
    }
}
