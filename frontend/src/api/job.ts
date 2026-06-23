import apiClient from "./client";
import type { Job, JobQueryParams, Result, SpringPage } from "@/types/job";
/**
 * 申请事项生命周期流转核心数据交互代理层.
 * @author Ari
 */
export const jobApi = {
  /**
   * 分页多维组合条件安全检索.
   * <p>支持前端传递模糊检索关键字(公司/标签等)，page 从 0 开启，后端已实施 Min/Max 封顶防御。</p>
   * @param {JobQueryParams} queryParams - 复合型过滤与分页查询对象载荷
   * @returns {Promise<Result<SpringPage<Job>>>}
   */
  getJobs: (queryParams: JobQueryParams) => {
    return apiClient.get<Result<SpringPage<Job>>>("/jobs", {
      params: queryParams,
    });
  },

  /**
   * 登记并持久化一条全新的申请事项.
   * @param {Job} jobData - 包含核心数据（如公司名称、职位标题、截止日期）的契约对象
   * @returns {Promise<Result<Job>>}
   */
  createJob: (jobData: Job) => {
    return apiClient.post<Result<Job>>("/jobs", jobData);
  },

  /**
   * 触发高一致性全字段/局部属性局部修改更新.
   * @param {number} id - 待操作的目标记录主键 ID
   * @param {Job} jobData - 变动后的全新增量值模型
   * @returns {Promise<Result<Job>>}
   */
  updateJob: (id: number, jobData: Job) => {
    return apiClient.put<Result<Job>>(`/jobs/${id}`, jobData);
  },

  /**
   * 执行单个事项的物理级联销毁.
   * @param {number} id - 期望彻底拔除的事项主键 ID
   * @returns {Promise<Result<null>>}
   */
  deleteJob: (id: number) => {
    return apiClient.delete<Result<null>>(`/jobs/${id}`);
  },

  /**
   * 强并发高防型批量物理清理接口.
   * @param {number[]} ids - 待收割清除的事项主键 ID 链表集合
   * @returns {Promise<Result<null>>}
   */
  deleteMultipleJobs: (ids: number[]) => {
    return apiClient.post<Result<null>>("/jobs/batch-delete", { ids: ids });
  },
};
