package com.sailing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.dto.BookingApplyDTO;
import com.sailing.dto.BookingApproveDTO;
import com.sailing.dto.BookingQueryDTO;
import com.sailing.entity.SailingBooking;
import com.sailing.vo.BookingVO;

public interface SailingBookingService {

    void applyBooking(BookingApplyDTO bookingApplyDTO);

    void approveBooking(BookingApproveDTO bookingApproveDTO, Long approveBy);

    void cancelBooking(Long id, Long memberId);

    void completeBooking(Long id);

    BookingVO getBookingById(Long id);

    IPage<BookingVO> getBookingPage(Page<BookingVO> page, BookingQueryDTO queryDTO);

    SailingBooking getBookingEntityById(Long id);

    void updateBookingById(SailingBooking booking);
}
