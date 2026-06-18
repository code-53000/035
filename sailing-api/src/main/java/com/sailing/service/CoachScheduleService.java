package com.sailing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.dto.CoachScheduleDTO;
import com.sailing.entity.CoachSchedule;

import java.time.LocalDate;
import java.util.List;

public interface CoachScheduleService {

    void addSchedule(CoachScheduleDTO coachScheduleDTO);

    void updateSchedule(CoachScheduleDTO coachScheduleDTO);

    void deleteSchedule(Long id);

    CoachSchedule getScheduleById(Long id);

    IPage<CoachSchedule> getSchedulePage(Page<CoachSchedule> page, Long coachId, LocalDate startDate, LocalDate endDate);

    List<CoachSchedule> getScheduleByDate(LocalDate scheduleDate);

    List<CoachSchedule> getCoachSchedule(Long coachId, LocalDate startDate, LocalDate endDate);

    boolean isCoachOnDuty(Long coachId, LocalDate scheduleDate, String timeSlot);
}
