//learn;申请类型
export type JobStatus =
  | "DRAFT"
  | "APPLIED"
  | "INTERVIEWING"
  | "COMPLETED"
  | "REJECTED";

export interface Job {
  id?: number; // learn;问号表示可选（新建時沒有 ID，後端返回才有）
  company: string; // learn;接收方
  title: string; // learn;标题
  status: JobStatus; // learn;进度
  reminderDate: string | null; // learn;提醒日期（ISO 字符串或 null）
  tags?: string; // learn;标签
  applyDate?: string; // learn;创建时间
  updateDate?: string; // learn;更新时间
}

export interface JobQueryParams {
  status?: JobStatus; //learn; 状态筛选
  keyword?: string; // learn;关键字
  page?: number; // learn;当前页码
  size?: number; // learn;当前页数
}
