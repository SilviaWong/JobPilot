package com.jobpilot.jobpilotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InterviewMapper extends BaseMapper<Interview> {
}