<template>
  <n-card>
    <div v-if="jobStore.loading" class="loading-container">
      <n-spin size="large" description="正在同步日程..." />
    </div>
    <FullCalendar :key="jobStore.jobs.length" :options="calendarOptions" />
  </n-card>
</template>

<script setup lang="ts">
import FullCalendar from "@fullcalendar/vue3";
import type { CalendarOptions } from "@fullcalendar/core";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import { reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useJobStore } from "@/stores/job";
import { STATUS_MAP } from "@/constants/job";

const router = useRouter();
const jobStore = useJobStore();

const handleEventClick = (info: any) => {
  const jobId = info.event.id;
  router.push({
    name: "home",
    query: { editId: jobId },
  });
};

const calendarOptions = reactive<CalendarOptions>({
  plugins: [dayGridPlugin, interactionPlugin],
  initialView: "dayGridMonth",
  events: async (_info, successCallback, failureCallback) => {
    try {
      await jobStore.fetchJobs();

      const events = jobStore.jobs
        .filter((job) => job.id !== undefined)
        .map((job) => {
          const config = STATUS_MAP[job.status] || { color: "#999" };
          return {
            id: String(job.id),
            title: `${job.company} - ${config.label}`,
            start: job.deadline,
            display: "block",
            backgroundColor: config.color,
            borderColor: "transparent",
            textColor: "#ffffff",
            extendedProps: { ...job },
          };
        });
      successCallback(events);
    } catch (error) {
      console.error("日历数据加载失败", error);
      failureCallback(error as Error);
    }
  },
  eventClick: handleEventClick,
  locale: "zh-cn", // learn;设置中文
  headerToolbar: {
    left: "prev,next today",
    center: "title",
    right: "dayGridMonth,dayGridWeek",
  },
});

onMounted(async () => {
  // learn;只有当 store 里没数据时才去后端抓，避免重复请求
  if (jobStore.jobs.length === 0) {
    await jobStore.fetchJobs();
  }
});
</script>

<style scoped lang="css">
:deep(.fc-event-title) {
  white-space: normal;
  font-size: 1.1rem !important;
  font-weight: 600 !important;
  padding: 2px 4px;
}

:deep(.fc-daygrid-event) {
  border-radius: 6px !important;
  margin-top: 2px !important;
  cursor: pointer;
  transition: all 0.2s ease-in-out;
}
:deep(.fc-daygrid-event:hover) {
  transform: scale(1.03);
  z-index: 100; /* learn;确保放大时不会被其他格子遮挡 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  filter: brightness(110%);
}
</style>
