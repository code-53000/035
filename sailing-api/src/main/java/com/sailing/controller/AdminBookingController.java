package com.sailing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.common.SecurityUser;
import com.sailing.dto.BookingApproveDTO;
import com.sailing.dto.BookingQueryDTO;
import com.sailing.entity.SailingBooking;
import com.sailing.service.SailingBookingService;
import com.sailing.vo.BookingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/bookings")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    @Autowired
    private SailingBookingService sailingBookingService;

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        return securityUser.getUserId();
    }

    @GetMapping
    public Result<IPage<BookingVO>> getBookingPage(
            BookingQueryDTO bookingQueryDTO,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<BookingVO> page = new Page<>(pageNum, pageSize);
        IPage<BookingVO> result = sailingBookingService.getBookingPage(page, bookingQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BookingVO> getBookingDetail(@PathVariable Long id) {
        BookingVO bookingVO = sailingBookingService.getBookingById(id);
        return Result.success(bookingVO);
    }

    @PutMapping("/{id}/approve")
    public Result<Void> approveBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingApproveDTO bookingApproveDTO) {
        Long approveBy = getCurrentUserId();
        bookingApproveDTO.setId(id);
        sailingBookingService.approveBooking(bookingApproveDTO, approveBy);
        return Result.success();
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancelBooking(@PathVariable Long id) {
        SailingBooking booking = sailingBookingService.getBookingEntityById(id);
        if (booking == null) {
            throw new BusinessException("预约记录不存在");
        }
        if (SailingBooking.STATUS_COMPLETED.equals(booking.getBookingStatus())
                || SailingBooking.STATUS_CANCELLED.equals(booking.getBookingStatus())) {
            throw new BusinessException("当前状态无法取消");
        }
        booking.setBookingStatus(SailingBooking.STATUS_CANCELLED);
        sailingBookingService.updateBookingById(booking);
        return Result.success();
    }
}
