package com.jobpilot.jobpilotserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jobpilot.jobpilotserver.mapper.InterviewMapper;
import com.jobpilot.jobpilotserver.model.entity.Interview;
import com.jobpilot.jobpilotserver.service.InterviewService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 面试管理与复盘记录业务实现类
 * <p>
 * 负责维护求职者的面试行程、面试轮次、复盘笔记 (Debrief) 以及面试状态的流转管理。
 */
@Service
public class InterviewServiceImpl extends ServiceImpl<InterviewMapper, Interview> implements InterviewService {

    /**
     * 条件查询面试记录列表
     *
     * @param keyword 关键词搜索（支持岗位名称、企业名称模糊匹配）
     * @param status 面试状态筛选（如：待面试、已完成、通过、未通过等）
     * @return 按面试时间降序排列的面试记录列表
     */
    @Override
    public List<Interview> getInterviews(String keyword, String status) {
        LambdaQueryWrapper<Interview> wrapper = new LambdaQueryWrapper<>();
        // 岗位名或公司名模糊查询
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(Interview::getJobTitle, keyword)
                    .or().like(Interview::getCompanyName, keyword));
        }
        // 面试状态精确匹配
        if (StringUtils.hasText(status)) {
            wrapper.eq(Interview::getStatus, status);
        }
        // 默认按面试时间倒序排列
        wrapper.orderByDesc(Interview::getInterviewTime);
        return list(wrapper);
    }

    /**
     * 保存或更新面试记录
     * <p>
     * 若未提供主键 ID 则视为新增记录，自动生成 UUID 并维护创建/更新时间戳。
     *
     * @param interview 面试实体
     * @return 是否保存/更新成功
     */
    @Override
    public boolean saveOrUpdateInterview(Interview interview) {
        if (!StringUtils.hasText(interview.getId())) {
            interview.setId(UUID.randomUUID().toString());
            interview.setCreatedAt(LocalDateTime.now());
        }
        interview.setUpdatedAt(LocalDateTime.now());
        return saveOrUpdate(interview);
    }

    /**
     * 更新指定面试记录的状态
     *
     * @param id 面试记录 ID
     * @param status 目标状态
     * @return 是否更新成功
     */
    @Override
    public boolean updateStatus(String id, String status) {
        Interview interview = getById(id);
        if (interview != null) {
            interview.setStatus(status);
            interview.setUpdatedAt(LocalDateTime.now());
            return updateById(interview);
        }
        return false;
    }

    /**
     * 删除指定面试记录
     *
     * @param id 面试记录 ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteInterview(String id) {
        return removeById(id);
    }
}

