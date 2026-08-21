package com.jobpilot.jobpilotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobpilot.jobpilotserver.model.entity.BlacklistedCompany;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BlacklistedCompanyMapper extends BaseMapper<BlacklistedCompany> {
}