package com.sailing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.dto.CoachScheduleDTO;
import com.sailing.dto.UserInfoDTO;
import com.sailing.entity.CoachSchedule;
import com.sailing.entity.SysUser;
import com.sailing.mapper.CoachScheduleMapper;
import com.sailing.mapper.SysUserMapper;
import com.sailing.service.CoachScheduleService;
import com.sailing.vo.CoachScheduleVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/coaches")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCoachController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private CoachScheduleMapper coachScheduleMapper;

    @Autowired
    private CoachScheduleService coachScheduleService;

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

    private CoachScheduleVO convertToScheduleVO(CoachSchedule schedule, Map<Long, SysUser> userMap) {
        CoachScheduleVO vo = new CoachScheduleVO();
        BeanUtils.copyProperties(schedule, vo);
        vo.setTimeSlotName(getTimeSlotName(schedule.getTimeSlot()));
        vo.setOnDutyName(getOnDutyName(schedule.getIsOnDuty()));
        if (userMap != null && schedule.getCoachId() != null && userMap.containsKey(schedule.getCoachId())) {
            vo.setCoachName(userMap.get(schedule.getCoachId()).getRealName());
        }
        return vo;
    }

    private UserInfoDTO convertToUserInfoDTO(SysUser user) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRealName(user.getRealName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        return dto;
    }

    @GetMapping("/list")
    public Result<List<UserInfoDTO>> getCoachList() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getRole, SysUser.ROLE_COACH);
        List<SysUser> users = sysUserMapper.selectList(wrapper);
        List<UserInfoDTO> dtoList = users.stream()
                .map(this::convertToUserInfoDTO)
                .collect(Collectors.toList());
        return Result.success(dtoList);
    }

    @GetMapping("/schedules")
    public Result<IPage<CoachScheduleVO>> getSchedulePage(
            @RequestParam(required = false) Long coachId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Integer pageNumber = page != null ? page : pageNum;
        Page<CoachSchedule> pageParam = new Page<>(pageNumber, pageSize);
        IPage<CoachSchedule> schedulePage = coachScheduleService.getSchedulePage(pageParam, coachId, startDate, endDate);
        List<CoachSchedule> schedules = schedulePage.getRecords();
        Set<Long> coachIds = schedules.stream()
                .map(CoachSchedule::getCoachId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = null;
        if (!coachIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(coachIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        }
        IPage<CoachScheduleVO> voPage = new Page<>(schedulePage.getCurrent(), schedulePage.getSize(), schedulePage.getTotal());
        final Map<Long, SysUser> finalUserMap = userMap;
        voPage.setRecords(schedules.stream()
                .map(s -> convertToScheduleVO(s, finalUserMap))
                .collect(Collectors.toList()));
        return Result.success(voPage);
    }

    @PostMapping("/schedules")
    public Result<Void> createSchedule(@Valid @RequestBody CoachScheduleDTO coachScheduleDTO) {
        try {
            coachScheduleService.addSchedule(coachScheduleDTO);
        } catch (BusinessException e) {
            if (e.getMessage().contains("已有排班")) {
                LambdaQueryWrapper<CoachSchedule> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(CoachSchedule::getCoachId, coachScheduleDTO.getCoachId());
                wrapper.eq(CoachSchedule::getScheduleDate, coachScheduleDTO.getScheduleDate());
                wrapper.eq(CoachSchedule::getTimeSlot, coachScheduleDTO.getTimeSlot());
                CoachSchedule exist = coachScheduleMapper.selectOne(wrapper);
                if (exist != null) {
                    coachScheduleDTO.setId(exist.getId());
                    coachScheduleService.updateSchedule(coachScheduleDTO);
                }
            } else {
                throw e;
            }
        }
        return Result.success();
    }

    @PutMapping("/schedules/{id}")
    public Result<Void> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody CoachScheduleDTO coachScheduleDTO) {
        coachScheduleDTO.setId(id);
        coachScheduleService.updateSchedule(coachScheduleDTO);
        return Result.success();
    }

    @DeleteMapping("/schedules/{id}")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        coachScheduleService.deleteSchedule(id);
        return Result.success();
    }
}
