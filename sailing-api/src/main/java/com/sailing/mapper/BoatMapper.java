package com.sailing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sailing.entity.Boat;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoatMapper extends BaseMapper<Boat> {
}
