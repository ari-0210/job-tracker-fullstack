import type { JobStatus } from "@/types/job";

interface StatusOption {
  label: string;
  value: JobStatus;
}

export const STATUS_OPTIONS: StatusOption[] = [
  { value: "DRAFT", label: "草稿" },
  { value: "APPLIED", label: "已投递" },
  { value: "INTERVIEWING", label: "面试中" },
  { value: "COMPLETED", label: "已完成" },
  { value: "REJECTED", label: "被拒绝" },
];

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
