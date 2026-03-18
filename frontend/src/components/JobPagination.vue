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
    style="margin-top: 20px; display: flex; justify-content: flex-end"
  />
</template>

<script setup lang="ts">
import { useJobStore } from "@/stores/job";
const jobStore = useJobStore();
</script>
