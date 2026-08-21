package com.jobpilot.jobpilotserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jobpilot.jobpilotserver.model.entity.Resume;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResumeMapper extends BaseMapper<Resume> {
}
