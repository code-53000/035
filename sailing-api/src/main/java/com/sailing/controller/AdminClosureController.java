package com.sailing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.common.SecurityUser;
import com.sailing.dto.ClosureRecordDTO;
import com.sailing.entity.ClosureRecord;
import com.sailing.entity.SysUser;
import com.sailing.mapper.ClosureRecordMapper;
import com.sailing.mapper.SysUserMapper;
import com.sailing.service.ClosureRecordService;
import com.sailing.vo.ClosureRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/closures")
@PreAuthorize("hasRole('ADMIN')")
public class AdminClosureController {

    @Autowired
    private ClosureRecordService closureRecordService;

    @Autowired
    private ClosureRecordMapper closureRecordMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

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

    private String getStatusName(Integer status) {
        return ClosureRecord.STATUS_ACTIVE.equals(status) ? "生效" : "已取消";
    }

    private ClosureRecordVO convertToVO(ClosureRecord record, Map<Long, SysUser> userMap) {
        ClosureRecordVO vo = new ClosureRecordVO();
        BeanUtils.copyProperties(record, vo);
        vo.setTimeSlotName(getTimeSlotName(record.getTimeSlot()));
        vo.setStatusName(getStatusName(record.getStatus()));
        if (userMap != null && record.getCreatedBy() != null && userMap.containsKey(record.getCreatedBy())) {
            vo.setCreatedByName(userMap.get(record.getCreatedBy()).getRealName());
        }
        return vo;
    }

    @GetMapping
    public Result<IPage<ClosureRecordVO>> getClosurePage(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<ClosureRecord> wrapper = new LambdaQueryWrapper<>();
        if (startDate != null) {
            wrapper.ge(ClosureRecord::getClosureDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(ClosureRecord::getClosureDate, endDate);
        }
        if (status != null) {
            wrapper.eq(ClosureRecord::getStatus, status);
        }
        wrapper.orderByDesc(ClosureRecord::getClosureDate);
        Integer pageNumber = page != null ? page : pageNum;
        Page<ClosureRecord> pageParam = new Page<>(pageNumber, pageSize);
        IPage<ClosureRecord> recordPage = closureRecordMapper.selectPage(pageParam, wrapper);
        List<ClosureRecord> records = recordPage.getRecords();
        Set<Long> userIds = records.stream()
                .map(ClosureRecord::getCreatedBy)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        Map<Long, SysUser> userMap = null;
        if (!userIds.isEmpty()) {
            List<SysUser> users = sysUserMapper.selectBatchIds(userIds);
            userMap = users.stream()
                    .collect(Collectors.toMap(SysUser::getId, Function.identity()));
        }
        IPage<ClosureRecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        final Map<Long, SysUser> finalUserMap = userMap;
        voPage.setRecords(records.stream()
                .map(r -> convertToVO(r, finalUserMap))
                .collect(Collectors.toList()));
        return Result.success(voPage);
    }

    @PostMapping
    public Result<Void> createClosure(@Valid @RequestBody ClosureRecordDTO closureRecordDTO) {
        Long createdBy = getCurrentUserId();
        closureRecordService.addClosureRecord(closureRecordDTO, createdBy);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateClosure(
            @PathVariable Long id,
            @Valid @RequestBody ClosureRecordDTO closureRecordDTO) {
        ClosureRecord exist = closureRecordService.getById(id);
        if (exist == null) {
            throw new BusinessException("封场记录不存在");
        }
        closureRecordDTO.setId(id);
        ClosureRecord record = new ClosureRecord();
        BeanUtils.copyProperties(closureRecordDTO, record);
        closureRecordMapper.updateById(record);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteClosure(@PathVariable Long id) {
        Long updatedBy = getCurrentUserId();
        closureRecordService.cancelClosure(id, updatedBy);
        return Result.success();
    }
}
