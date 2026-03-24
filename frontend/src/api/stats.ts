import apiClient from "./client";
import type { StatsSummary } from "@/types/stats";

export const getStatsSummary = () => {
  return apiClient.get<StatsSummary>("/stats/summary");
};
