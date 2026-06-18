package com.sailing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.Result;
import com.sailing.dto.BoatQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.service.BoatService;
import com.sailing.vo.BoatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boats")
@PreAuthorize("isAuthenticated()")
public class BoatController {

    @Autowired
    private BoatService boatService;

    @GetMapping
    public Result<IPage<BoatVO>> getBoats(BoatQueryDTO boatQueryDTO) {
        boatQueryDTO.setStatus(Boat.STATUS_AVAILABLE);
        Page<BoatVO> page = new Page<>(boatQueryDTO.getPageNum(), boatQueryDTO.getPageSize());
        IPage<BoatVO> result = boatService.getBoatPage(page, boatQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BoatVO> getBoatDetail(@PathVariable Long id) {
        BoatVO boatVO = boatService.getBoatById(id);
        return Result.success(boatVO);
    }
}
