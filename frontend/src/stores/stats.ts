import { defineStore } from "pinia";
import { ref } from "vue";
import { getStatsSummary, getUrgentJobs } from "@/api/stats";
import type { StatsSummary } from "@/types/stats";
import type { Job } from "@/types/job";
/**
 * 宏观统计看板与急迫事项流转中心状态仓库 (Pinia Store).
 * <p>本仓库深度服务于系统可视化看盘，集中式削平图表元数据的异步拉取抖动，保障大盘渲染时序高度闭环.</p>
 * @author Ari
 */
export const useStatsStore = defineStore("stats", () => {
  /** * 综合统计指标结构体。
   * 内置强安全防御初始化，确保在网络异步回流前，视图层具备干净的零基水位线，杜绝白屏溃败。
   */
  const summary = ref<StatsSummary>({
    totalCount: 0,
    statusCounts: {},
    next7DaysCount: 0,
    thisMonthCount: 0,
  });
  /** 控制仪表盘图表骨架屏(Skeleton)或全屏加载动画的异步全局状态锁 */
  const loading = ref(false);

  /**
   * 触发图表大屏核心宏观指标的异步清洗拉取.
   * <p>底层对接 Cache-Aside 缓存防线，平滑支撑月度统计、7天紧迫度、以及状态分布的图表多维聚变展现。</p>
   */
  const fetchSummary = async () => {
    loading.value = true;
    try {
      const res = await getStatsSummary();
      summary.value = res.data.data;
    } catch (error) {
      console.error("Failed to fetch stats:", error);
    } finally {
      loading.value = false;
    }
  };
  /** 承载当前在线用户迫近截止红线的前 5 条高危急迫事项数据集(安全防御空数组初始化) */
  const urgentJobs = ref<Job[]>([]); 
  /**
   * 极速捕获并流转当前用户最高危的紧急代办事项集锦.
   * <p>交互闭环：数据拉取成功后就地执行状态覆写，实时驱动首页急迫看板进行极速视图刷新。</p>
   */
  const fetchUrgentJobs = async () => {
    const res = await getUrgentJobs();
    urgentJobs.value = res.data.data;
  };

  return { summary, loading, fetchSummary, fetchUrgentJobs, urgentJobs };
});
