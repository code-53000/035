package com.sailing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sailing.dto.BoatDTO;
import com.sailing.dto.BoatQueryDTO;
import com.sailing.entity.Boat;
import com.sailing.vo.BoatVO;

import java.util.List;

public interface BoatService {

    void addBoat(BoatDTO boatDTO);

    void updateBoat(BoatDTO boatDTO);

    void deleteBoat(Long id);

    BoatVO getBoatById(Long id);

    IPage<BoatVO> getBoatPage(Page<BoatVO> page, BoatQueryDTO queryDTO);

    List<BoatVO> getAvailableBoatList();

    List<Boat> list();
}
