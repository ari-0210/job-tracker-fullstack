import type { JobStatus } from "@/types/job";

export const STATUS_MAP: Record<JobStatus, { label: string; color: string }> = {
  DRAFT: { label: "草稿", color: "#ee6666" },
  APPLIED: { label: "已投递", color: "#fac858" },
  INTERVIEWING: { label: "面试中", color: "#73c0de" },
  COMPLETED: { label: "已完成", color: "#91cc75" },
  REJECTED: { label: "被拒绝", color: "#5470c6" },
} as const;

export const STATUS_OPTIONS = Object.entries(STATUS_MAP).map(([key, info]) => ({
  value: key,
  label: info.label,
  color: info.color,
}));

export const getStatusLabel = (statusValue: string) => {
  const option = STATUS_OPTIONS.find((opt) => opt.value === statusValue);
  return option ? option.label : statusValue || "未定义状态";
};

export const STATUS_TYPE_MAP: Record<
  string,
  "default" | "info" | "warning" | "success" | "error"
> = {
  DRAFT: "default", // 灰色
  APPLIED: "info", // 蓝色
  INTERVIEWING: "warning", // 橙色
  COMPLETED: "success", // 绿色
  REJECTED: "error", // 红色
};

export const getStatusType = (status: string) => {
  return STATUS_TYPE_MAP[status] || "default";
};
