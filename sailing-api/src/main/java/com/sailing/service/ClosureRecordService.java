package com.sailing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.dto.ClosureRecordDTO;
import com.sailing.entity.ClosureRecord;

import java.time.LocalDate;
import java.util.List;

public interface ClosureRecordService {

    void addClosureRecord(ClosureRecordDTO closureRecordDTO, Long createdBy);

    void cancelClosure(Long id, Long updatedBy);

    ClosureRecord getById(Long id);

    IPage<ClosureRecord> getClosureRecordPage(Page<ClosureRecord> page, LocalDate startDate, LocalDate endDate);

    List<ClosureRecord> getClosureRecordList(LocalDate startDate, LocalDate endDate);

    List<ClosureRecord> getClosuresByDate(LocalDate date);

    boolean isDateClosed(LocalDate date, String timeSlot);
}
