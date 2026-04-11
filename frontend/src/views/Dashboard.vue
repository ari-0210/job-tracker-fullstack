<template>
  <n-space vertical size="large" class="p-6">
    <n-grid :cols="4" :x-gap="12">
      <n-gi>
        <n-card>
          <n-statistic label="总事项数" :value="statsStore.summary.totalCount">
            <template #prefix>📊</template>
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card class="bg-red-50/50 border-red-100">
          <n-statistic
            label="7天内截止"
            :value="statsStore.summary.next7DaysCount"
          >
            <template #prefix>🚨</template>
            <template #suffix
              ><span class="text-xs text-gray-400">项</span></template
            >
          </n-statistic>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card class="bg-red-50/50 border-red-100">
          <n-statistic
            label="本月内截止"
            :value="statsStore.summary.thisMonthCount"
          >
            <template #prefix>❕</template>
            <template #suffix
              ><span class="text-xs text-gray-400">项</span></template
            >
          </n-statistic>
        </n-card>
      </n-gi>
    </n-grid>
    <n-grid :cols="2" :x-gap="12">
      <n-gi>
        <n-card title="申请状态分布">
          <div style="height: 400px; width: 100%">
            <p v-if="statsStore.loading">加载中...</p>
            <div id="chart-container" v-else style="width: 100%; height: 100%">
              <v-chart
                v-if="statsStore.summary.totalCount > 0"
                :option="chartOption"
                autoresize
              /><n-empty v-else description="提交一些事项后再来看看吧" />
            </div>
          </div>
        </n-card>
      </n-gi>
      <n-gi>
        <n-card title="⚠️紧迫事项 (未来7天截止)">
          <n-list hoverable clickable v-if="statsStore.urgentJobs.length > 0">
            <n-list-item
              v-for="job in statsStore.urgentJobs"
              :key="job.id"
              @click="handleJobClick(job)"
            >
              <div class="flex justify-between items-center">
                <div>
                  <div class="font-medium text-gray-800">
                    {{ job.company }}
                  </div>
                  <div class="text-sm text-gray-500">{{ job.title }}</div>
                </div>
                <div class="text-right">
                  <div class="text-red-500 font-mono font-bold">
                    截止：{{ job.deadline.replace("T", " ") }}
                  </div>
                  <n-tag
                    :type="job.status === 'INTERVIEWING' ? 'info' : 'warning'"
                    size="small"
                  >
                    {{ job.status }}
                  </n-tag>
                </div>
              </div>
            </n-list-item>
          </n-list>
          <n-empty v-else description="目前没有紧迫的任务，Relax！" />
        </n-card>
      </n-gi>
    </n-grid>
    <n-card><job-calendar></job-calendar></n-card>
  </n-space>
</template>

<script setup lang="ts">
import { useStatsStore } from "@/stores/stats";
import { useJobStore } from "@/stores/job";
import { onMounted, computed } from "vue";
import { getStatusLabel } from "@/constants/job";
import { useRouter } from "vue-router";
import type { Job } from "@/types/job";
import JobCalendar from "@/components/JobCalendar.vue";
const statsStore = useStatsStore();
const jobStore = useJobStore();

const chartOption = computed(() => {
  // learn; 先准备好数据数组
  const dataArray = Object.entries(statsStore.summary.statusCounts).map(
    ([key, count]) => {
      return { value: count, name: getStatusLabel(key) };
    },
  );

  // learn; ECharts 配置
  return {
    tooltip: { trigger: "item", formatter: "{b}: {c} ({d}%)" },
    legend: { bottom: "5%", left: "center" },
    color: ["#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de"],
    series: [
      {
        name: "申请状态",
        type: "pie",
        radius: ["40%", "70%"],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: "#fff", borderWidth: 2 },
        data: dataArray,
      },
    ],
  };
});

const router = useRouter();
const handleJobClick = (job: Job) => {
  router.push({
    name: "home",
    query: { editId: job.id }, // learn;携带想要编辑的任务 ID
  });
};
onMounted(async () => {
  if (jobStore.jobs.length === 0) {
    await jobStore.fetchJobs();
  }
  statsStore.fetchSummary();
  statsStore.fetchUrgentJobs();
});
</script>
