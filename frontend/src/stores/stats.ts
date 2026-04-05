import { defineStore } from "pinia";
import { ref } from "vue";
import { getStatsSummary, getUrgentJobs } from "@/api/stats";
import type { StatsSummary } from "@/types/stats";
import type { Job } from "@/types/job";

export const useStatsStore = defineStore("stats", () => {
  const summary = ref<StatsSummary>({
    totalCount: 0,
    statusCounts: {},
    next7DaysCount: 0,
    thisMonthCount: 0,
  });
  const loading = ref(false);

  const fetchSummary = async () => {
    loading.value = true;
    try {
      const res = await getStatsSummary();
      summary.value = res.data;
    } catch (error) {
      console.error("Failed to fetch stats:", error);
    } finally {
      loading.value = false;
    }
  };

  const urgentJobs = ref<Job[]>([]); // learn;存储紧迫事项列表

  const fetchUrgentJobs = async () => {
    const res = await getUrgentJobs();
    urgentJobs.value = res.data.data;
  };

  return { summary, loading, fetchSummary, fetchUrgentJobs, urgentJobs };
});
