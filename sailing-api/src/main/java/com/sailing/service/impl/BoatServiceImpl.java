package com.sailing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sailing.common.BusinessException;
import com.sailing.dto.BoatDTO;
import com.sailing.dto.BoatQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.mapper.BoatMapper;
import com.sailing.service.BoatService;
import com.sailing.vo.BoatVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoatServiceImpl extends ServiceImpl<BoatMapper, Boat> implements BoatService {

    @Override
    public void addBoat(BoatDTO boatDTO) {
        LambdaQueryWrapper<Boat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Boat::getCode, boatDTO.getCode());
        Long count = baseMapper.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("船号已存在");
        }
        Boat boat = new Boat();
        BeanUtils.copyProperties(boatDTO, boat);
        if (!StringUtils.hasText(boat.getStatus())) {
            boat.setStatus(Boat.STATUS_AVAILABLE);
        }
        baseMapper.insert(boat);
    }

    @Override
    public void updateBoat(BoatDTO boatDTO) {
        Boat exist = baseMapper.selectById(boatDTO.getId());
        if (exist == null) {
            throw new BusinessException("船只不存在");
        }
        if (StringUtils.hasText(boatDTO.getCode()) && !boatDTO.getCode().equals(exist.getCode())) {
            LambdaQueryWrapper<Boat> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Boat::getCode, boatDTO.getCode());
            wrapper.ne(Boat::getId, boatDTO.getId());
            Long count = baseMapper.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("船号已存在");
            }
        }
        Boat boat = new Boat();
        BeanUtils.copyProperties(boatDTO, boat);
        baseMapper.updateById(boat);
    }

    @Override
    public void deleteBoat(Long id) {
        Boat exist = baseMapper.selectById(id);
        if (exist == null) {
            throw new BusinessException("船只不存在");
        }
        baseMapper.deleteById(id);
    }

    @Override
    public BoatVO getBoatById(Long id) {
        Boat boat = baseMapper.selectById(id);
        if (boat == null) {
            throw new BusinessException("船只不存在");
        }
        return convertToVO(boat);
    }

    @Override
    public IPage<BoatVO> getBoatPage(Page<BoatVO> page, BoatQueryDTO queryDTO) {
        LambdaQueryWrapper<Boat> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO != null) {
            if (StringUtils.hasText(queryDTO.getKeyword())) {
                wrapper.and(w -> w.like(Boat::getName, queryDTO.getKeyword()).or().like(Boat::getCode, queryDTO.getKeyword()));
            } else {
                if (StringUtils.hasText(queryDTO.getName())) {
                    wrapper.like(Boat::getName, queryDTO.getName());
                }
                if (StringUtils.hasText(queryDTO.getCode())) {
                    wrapper.like(Boat::getCode, queryDTO.getCode());
                }
            }
            if (StringUtils.hasText(queryDTO.getBoatType())) {
                wrapper.eq(Boat::getBoatType, queryDTO.getBoatType());
            }
            if (StringUtils.hasText(queryDTO.getStatus())) {
                wrapper.eq(Boat::getStatus, queryDTO.getStatus());
            }
        }
        wrapper.orderByDesc(Boat::getCreateTime);
        IPage<Boat> boatPage = baseMapper.selectPage(new Page<>(page.getCurrent(), page.getSize()), wrapper);
        IPage<BoatVO> voPage = new Page<>(boatPage.getCurrent(), boatPage.getSize(), boatPage.getTotal());
        voPage.setRecords(boatPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList()));
        return voPage;
    }

    @Override
    public List<BoatVO> getAvailableBoatList() {
        LambdaQueryWrapper<Boat> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Boat::getStatus, Boat.STATUS_AVAILABLE);
        wrapper.orderByAsc(Boat::getCode);
        List<Boat> boats = baseMapper.selectList(wrapper);
        return boats.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<Boat> list() {
        return baseMapper.selectList(null);
    }

    private BoatVO convertToVO(Boat boat) {
        BoatVO vo = new BoatVO();
        BeanUtils.copyProperties(boat, vo);
        vo.setStatusName(getStatusDesc(boat.getStatus()));
        vo.setBoatTypeName(getBoatTypeDesc(boat.getBoatType()));
        return vo;
    }

    private String getStatusDesc(String status) {
        switch (status) {
            case Boat.STATUS_AVAILABLE:
                return "可用";
            case Boat.STATUS_MAINTENANCE:
                return "维护中";
            case Boat.STATUS_RETIRED:
                return "已报废";
            default:
                return "未知";
        }
    }

    private String getBoatTypeDesc(String boatType) {
        switch (boatType) {
            case "SAILBOAT":
                return "帆船";
            case "KAYAK":
                return "皮划艇";
            case "PADDLEBOARD":
                return "桨板";
            case "MOTORBOAT":
                return "摩托艇";
            case "CATAMARAN":
                return "双体船";
            default:
                return "未知";
        }
    }
}
