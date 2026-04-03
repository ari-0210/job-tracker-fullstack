<template>
  <div>
    <n-space style="margin-bottom: 10px" v-if="selectedJobIds.size > 0">
      <n-button
        type="error"
        @click="handleBulkDelete"
        :loading="jobStore.loading"
      >
        Delete ({{ selectedJobIds.size }})
      </n-button>
    </n-space>
    <div v-if="jobStore.loading">事项加载中……</div>
    <div v-if="fetchError && !jobStore.loading">Error loading submissions</div>
    <n-table v-if="!jobStore.loading && !fetchError" :single-line="false">
      <thead>
        <tr>
          <th style="width: 60px; text-align: center">
            <n-checkbox
              :checked="masterCheckboxState === true"
              :indeterminate="masterCheckboxState === 'indeterminate'"
              @update:checked="handleMasterCheckboxChange"
              title="Select All(全选)"
            />
          </th>
          <th>Recipient</th>
          <th>Title</th>
          <th>Deadline</th>
          <th>Status</th>
          <th>Tags</th>
          <th>ReminderDate</th>
          <th>UpdateDate</th>
          <th>Operation</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="jobStore.jobs.length === 0 && !jobStore.loading">
          <td colspan="9" style="text-align: center">
            No submissions found(没有事项).
          </td>
        </tr>
        <tr v-for="job in jobStore.jobs" :key="job.id">
          <td style="text-align: center">
            <n-checkbox
              :checked="selectedJobIds.has(job.id!)"
              @update:checked="
                (checkedValue: boolean) =>
                  handleRowCheckboxChange(job.id!, checkedValue)
              "
            />
          </td>
          <td>{{ job.company }}</td>
          <td>{{ job.title }}</td>
          <td>{{ job.deadline }}</td>
          <td>
            <n-tag :type="getStatusType(job.status)" round>{{
              getStatusLabel(job.status)
            }}</n-tag>
          </td>
          <td>{{ job.tags }}</td>
          <td>{{ job.reminderDate }}</td>
          <td>{{ job.updateDate }}</td>
          <td>
            <n-button @click="jobStore.openForm('edit', job)">编辑</n-button>
            <n-button type="error" @click="confirmDelete(job.id!)"
              >Delete</n-button
            >
          </td>
        </tr>
      </tbody>
    </n-table>

    <JobPagination />
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref, computed } from "vue";
import { useJobStore } from "@/stores/job";

import { getStatusLabel, getStatusType } from "@/constants/job";
import JobPagination from "./JobPagination.vue";

const jobStore = useJobStore();

const fetchError = ref(null);

// learn;---批量选中和批量删除----
const selectedJobIds = ref(new Set<number>()); // 使用 Set 存储选中的 job ID，方便添加、删除和检查存在性

//learn;删除
const confirmDelete = async (id: number) => {
  if (confirm("确定删除此事项吗？")) {
    await jobStore.removeJob(id);
  }
};

const handleBulkDelete = async () => {
  if (selectedJobIds.value.size === 0) {
    alert("Please select at least one record to delete(请选择至少一项删除)");
    return;
  }
  if (confirm(`删除 ${selectedJobIds.value.size} 项?`)) {
    await jobStore.bulkDelete(Array.from(selectedJobIds.value));
    selectedJobIds.value.clear();
  }
};

// learn;计算当前页所有 job 的 ID (用于全选逻辑)
const currentDisplayedJobIds = computed<number[]>(() => {
  // learn;过滤掉可能存在的 undefined id，确保返回的是纯 number 数组
  return jobStore.jobs
    .map((job) => job.id)
    .filter((id): id is number => id !== undefined);
});

// learn;计算主复选框的状态 (全选、部分选、全不选) - 仅针对当前页
const masterCheckboxState = computed(() => {
  if (currentDisplayedJobIds.value.length === 0) {
    return false; // 当前页没有数据，全选框不选中
  }
  // learn;检查当前页是否所有项都被选中
  const allOnPageSelected = currentDisplayedJobIds.value.every((id) =>
    selectedJobIds.value.has(id),
  );
  if (allOnPageSelected) {
    return true; // 当前页全选
  }
  // 检查当前页是否至少有一项被选中 (但不是全部)
  const someOnPageSelected = currentDisplayedJobIds.value.some((id) =>
    selectedJobIds.value.has(id),
  );
  if (someOnPageSelected) {
    return "indeterminate"; // 当前页部分选中，显示为半选状态
  }
  return false; // 当前页全不选
});

// 处理主复选框（全选/全不选当前页）的变化
const handleMasterCheckboxChange = (checked: boolean) => {
  if (checked) {
    // 如果主复选框被勾选 (变为全选状态)
    currentDisplayedJobIds.value.forEach((id) => selectedJobIds.value.add(id));
  } else {
    // 如果主复选框被取消勾选 (变为全不选状态)
    currentDisplayedJobIds.value.forEach((id) =>
      selectedJobIds.value.delete(id),
    );
  }
};

const handleRowCheckboxChange = (jobId: number, isChecked: boolean) => {
  if (isChecked) {
    selectedJobIds.value.add(jobId);
  } else {
    selectedJobIds.value.delete(jobId);
  }
};

// learn;监听页码改变
watch(
  () => jobStore.queryParams.page,
  () => jobStore.fetchJobs(),
);

onMounted(jobStore.fetchJobs);
</script>
