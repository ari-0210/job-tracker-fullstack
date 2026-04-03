export interface StatsSummary {
  totalCount: number;
  statusCounts: Record<string, number>;
  next7DaysCount: number;
  thisMonthCount: number;
}
