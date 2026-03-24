import { defineStore } from "pinia";
import { ref } from "vue";
import { getStatsSummary } from "@/api/stats";
import type { StatsSummary } from "@/types/stats";

export const useStatsStore = defineStore("stats", () => {
  const summary = ref<StatsSummary>({
    totalCount: 0,
    statusCounts: {},
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

  return { summary, loading, fetchSummary };
});
