export interface Result<T> {
  code: number;
  message: string;
  data: T; 
}

/** 进度状态类型 */
export type JobStatus =
  | "DRAFT"
  | "APPLIED"
  | "INTERVIEWING"
  | "COMPLETED"
  | "REJECTED";

export interface Job {
  /** 事项唯一自增主键（新建时前端没有，后端落库后自动生成） */
  id?: number; 
  /** 目标接收方（如：公司名称/招聘单位） */
  company: string;
  /** 事项标题或职位（如：完成报告） */
  title: string; 
  /** 当前事项的生命周期状态/进度（DRAFT \| APPLIED 等） */
  status: JobStatus;
  /** 用户设置的提醒通知日期（ISO字符串或未设置时为 null）,可选 */
  reminderDate: string | null; 
  /** 事项截止的绝对时间红线 */
  deadline: string; 
  /** 归属的多维标签（如：工作、生活，多个标签可用逗号隔开） */
  tags?: string; 
  /** 创建时间 */
  applyDate?: string;
  /** 最新一次更新时间 */
  updateDate?: string;
  /** 备注,如:购物清单内容 */
  notes?: string;
}

export interface JobQueryParams {
  /** 状态筛选 */
  status?: JobStatus;
  /** 查询关键字 */
  keyword?: string;
  /** 当前页码 */
  page?: number;
  /** 当前页数 */
  size?: number;
}
export interface SpringPage<T> {
  /**承载Job[] 数组 */
  content: T[]; //
  /**后端物理总条数 */
  totalElements: number;
  /** 总页数 */
  totalPages: number;
  /** 每页条数 */
  size: number;
  /** 当前页码 */
  number: number;
}
