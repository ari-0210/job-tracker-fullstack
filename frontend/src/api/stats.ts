import apiClient from "./client";
import type { StatsSummary } from "@/types/stats";
import type { Job, Result } from "@/types/job";
export const getStatsSummary = () => {
  return apiClient.get<StatsSummary>("/stats/summary");
};

export const getUrgentJobs = () => {
  return apiClient.get<Result<Job[]>>("/stats/urgent");
};
