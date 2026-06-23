import apiClient from "./client";
import type { StatsSummary } from "@/types/stats";
import type { Job, Result } from "@/types/job";

/**
 * 看板图表宏观指标提取.
 * <p>本模块接口底层挂载 Cache-Aside 高速缓存拦截，平滑支撑大面积聚合计算呈现.</p>
 * * @author Ari
 * * @returns {Promise<StatsSummary>} 包含本月截止、7天危急、状态占比等核心维度的聚合分析
 */
export const getStatsSummary = () => {
  return apiClient.get<Result<StatsSummary>>("/stats/summary");
};
/**
 * 快速捕捉迫近时间红线的前 5 条最危急紧急事项.
 * * @returns {Promise<Result<Job[]>>} 严格按照 Deadline 倒序切割放行的危急事项实体集合
 */
export const getUrgentJobs = () => {
  return apiClient.get<Result<Job[]>>("/stats/urgent");
};
