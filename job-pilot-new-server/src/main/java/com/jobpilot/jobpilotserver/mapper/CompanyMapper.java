package com.jobpilot.jobpilotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobpilot.jobpilotserver.model.entity.Company;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper extends BaseMapper<Company> {
}