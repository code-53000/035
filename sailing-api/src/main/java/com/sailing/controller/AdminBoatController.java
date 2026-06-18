package com.sailing.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.common.BusinessException;
import com.sailing.common.Result;
import com.sailing.dto.BoatDTO;
import com.sailing.dto.BoatQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.service.BoatService;
import com.sailing.vo.BoatVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/boats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBoatController {

    @Autowired
    private BoatService boatService;

    @GetMapping
    public Result<IPage<BoatVO>> getBoatPage(BoatQueryDTO boatQueryDTO) {
        Page<BoatVO> page = new Page<>(boatQueryDTO.getPageNum(), boatQueryDTO.getPageSize());
        IPage<BoatVO> result = boatService.getBoatPage(page, boatQueryDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result<BoatVO> getBoatDetail(@PathVariable Long id) {
        BoatVO boatVO = boatService.getBoatById(id);
        return Result.success(boatVO);
    }

    @PostMapping
    public Result<Void> createBoat(@Valid @RequestBody BoatDTO boatDTO) {
        boatService.addBoat(boatDTO);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> updateBoat(@PathVariable Long id, @Valid @RequestBody BoatDTO boatDTO) {
        boatService.updateBoat(boatDTO);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateBoatStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        BoatVO boatVO = boatService.getBoatById(id);
        if (boatVO == null) {
            throw new BusinessException("船只不存在");
        }
        BoatDTO boatDTO = new BoatDTO();
        boatDTO.setName(boatVO.getName());
        boatDTO.setCode(boatVO.getCode());
        boatDTO.setBoatType(boatVO.getBoatType());
        boatDTO.setCapacity(boatVO.getCapacity());
        boatDTO.setStatus(status);
        boatDTO.setDescription(boatVO.getDescription());
        boatService.updateBoat(boatDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteBoat(@PathVariable Long id) {
        boatService.deleteBoat(id);
        return Result.success();
    }
}
