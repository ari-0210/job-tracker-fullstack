<template>
  <n-pagination
    v-if="!jobStore.loading && !jobStore.fetchError && jobStore.total > 0"
    v-model:page="jobStore.queryParams.page"
    v-model:page-size="jobStore.queryParams.size"
    :item-count="jobStore.total"
    show-size-picker
    :page-sizes="[10, 20, 30, 40]"
    show-quick-jumper
    :disabled="jobStore.loading"
    class="mt-5 flex justify-end"
  />
</template>

<script setup lang="ts">
import { useJobStore } from "@/stores/job";
import { watch } from "vue";
const jobStore = useJobStore();

watch(
  [() => jobStore.queryParams.page, () => jobStore.queryParams.size],
  () => {
    jobStore.fetchJobs();
  },
);
</script>
