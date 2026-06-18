package com.sailing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.dto.SailingRecordDTO;
import com.sailing.vo.SailingRecordVO;

import java.time.LocalDate;

public interface SailingRecordService {

    void addRecord(SailingRecordDTO sailingRecordDTO, Long createdBy);

    SailingRecordVO getRecordById(Long id);

    IPage<SailingRecordVO> getRecordPage(Page<SailingRecordVO> page, Long memberId, Long coachId, Long boatId,
                                         LocalDate startDate, LocalDate endDate);
}
