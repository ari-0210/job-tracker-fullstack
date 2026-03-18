<template>
  <n-button type="primary" @click="jobStore.openForm('add')">
    新建事项
  </n-button>
</template>

<script setup lang="ts">
import { useJobStore } from "@/stores/job";

const jobStore = useJobStore();
</script>
