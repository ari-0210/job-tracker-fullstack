// 搜索框与按钮 searchQuery 状态, handleSearch 函数
<template>
  <n-space style="margin-bottom: 20px; display: flex">
    <n-input
      v-model:value="jobStore.queryParams.keyword"
      placeholder="Please input keywords for searching(请输入查找关键字)"
      clearable
      style="flex-grow: 1; max-width: 800px"
      @keyup.enter="jobStore.handleSearch"
    />
    <n-button
      type="primary"
      @click="jobStore.handleSearch"
      :loading="jobStore.loading"
    >
      Search
    </n-button>
  </n-space>
</template>

<script setup lang="ts">
import { useJobStore } from "@/stores/job";

const jobStore = useJobStore();
</script>
