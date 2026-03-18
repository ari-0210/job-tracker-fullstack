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

// learn;导出函数
export const getStatusLabel = (statusValue: string) => {
  const option = STATUS_OPTIONS.find((opt) => opt.value === statusValue);
  return option ? option.label : statusValue || "未定义状态";
};
