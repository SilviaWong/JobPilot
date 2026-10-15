package com.jobpilot.jobpilotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobpilot.jobpilotserver.model.entity.JobStatusLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface JobStatusLogMapper extends BaseMapper<JobStatusLog> {
}

