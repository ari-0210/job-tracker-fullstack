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
          <th style="width: 60px; text-align: center" @click.stop>
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
      <tbody hoverable clickable>
        <tr v-if="jobStore.jobs.length === 0 && !jobStore.loading">
          <td colspan="9" style="text-align: center">
            No submissions found(没有事项).
          </td>
        </tr>
        <tr
          v-for="job in jobStore.jobs"
          :key="job.id"
          @click="handleOpenDetail(job.id)"
        >
          <td style="text-align: center" @click.stop>
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
            <n-button @click.stop="jobStore.openForm('edit', job)"
              >编辑</n-button
            >
            <n-button
              type="error"
              @click.stop="confirmDelete(job.id!)"
              :disabled="jobStore.loading"
              :loading="jobStore.loading"
              >Delete</n-button
            >
          </td>
        </tr>
      </tbody>
    </n-table>
    <JobDetail
      v-model:show="showDrawer"
      :job-id="selectedJobId"
      @open-edit-form="handleParentOpenEditForm"
    />
    <JobPagination />
  </div>
</template>

<script setup lang="ts">
import { onMounted, watch, ref, computed } from "vue";
import { useJobStore } from "@/stores/job";
import { getStatusLabel, getStatusType } from "@/constants/job";
import JobPagination from "./JobPagination.vue";
import JobDetail from "./JobDetail.vue";

/** * 事项生命周期多维列表展现及批量收割主控组件.
 * @description 内聚了当前页高防全选/半选状态推导、批量级联下线物理收割、行级详情抽屉抽离激活响应。
 * * @author Ari
 */
const jobStore = useJobStore();
/** 拦截并暂存当前组件内发生的特定网络会话崩塌异常 */
const fetchError = ref(null);

// ==========================================
// 模态详情Drawer流转控制
// ==========================================
/** 驱动右侧详情看板Drawer显隐开关 */
const showDrawer = ref(false);
/** 传递给详情Drawer,用于记录主键 ID */
const selectedJobId = ref<number | null>(null);
/**
 * 唤醒侧边栏Drawer并下发目标行唯一主键.
 * @param {number | undefined} id - 点击行的 Job 主键自增 ID
 */
const handleOpenDetail = (id: number | undefined) => {
  if (id === undefined) return;
  selectedJobId.value = id;
  showDrawer.value = true; 
};
/**
 * 唤醒侧边栏Drawer并下发目标行唯一主键.
 * @param {number } editId - 传入的 Job 主键自增 ID
 */
const handleParentOpenEditForm = (editId: number) => {
  const targetJob = jobStore.jobs.find((j) => j.id === Number(editId));
  jobStore.openForm("edit", targetJob);
};

// ==========================================
// 高防型批量勾选与删除算法
// ==========================================
/** * 利用 ES6 Set 结构无缝托管全大盘被勾选的 Job ID 字典.
 * <p>规避普通 Array 的 push 重复堆叠风险，全面优化 entries.has() 的时间复杂度至 O(1)。</p>
 */
const selectedJobIds = ref(new Set<number>()); // 使用 Set 存储选中的 job ID，方便添加、删除和检查存在性

/**
 * 调度单体行记录的即时物理核销.
 * @param {number} id - 行级目标主键
 */
const confirmDelete = async (id: number) => {
  if (confirm("确定删除此事项吗？")) {
    await jobStore.removeJob(id);
    // 体验闭环：单条删完后，如果原先勾选了它，要在 Set 中擦除
    selectedJobIds.value.delete(id);
  }
};
/**
 * 触发批量删除.
 * <p>将 Set 集合强转为标准物理线性 Array 数组打入 Pinia Actions 异步通道。</p>
 */
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

/**
 * 提取并提纯当前页视窗内可见的所有合法 ID 集合.
 * @type {ComputedRef<number[]>} 过滤脏 undefined 数据，专为全选/反选算力提供底层支撑
 */
const currentDisplayedJobIds = computed<number[]>(() => {
  // 过滤掉可能存在的 undefined id，确保返回的是纯 number 数组
  return jobStore.jobs
    .map((job) => job.id)
    .filter((id): id is number => id !== undefined);
});

/**
 * 计算主复选框的状态 (全选、部分选、全不选) - 仅针对当前页.
 * @type {ComputedRef<boolean | 'indeterminate'>}
 * 返回 true 代表当前页全歼勾选；false 代表全空；'indeterminate' 激活组件库特有的半选视觉状态
 */

const masterCheckboxState = computed(() => {
  if (currentDisplayedJobIds.value.length === 0) {
    return false; 
  }
  
  const allOnPageSelected = currentDisplayedJobIds.value.every((id) =>
    selectedJobIds.value.has(id),
  );
  if (allOnPageSelected) {
    return true; 
  }
  
  const someOnPageSelected = currentDisplayedJobIds.value.some((id) =>
    selectedJobIds.value.has(id),
  );
  if (someOnPageSelected) {
    return "indeterminate"; 
  }
  return false; 
});
/**
 * 处理主复选框（全选/全不选当前页）的变化
 * @param {boolean} checked - 触发后的最新真假勾选值
 */

const handleMasterCheckboxChange = (checked: boolean) => {
  if (checked) {
    
    currentDisplayedJobIds.value.forEach((id) => selectedJobIds.value.add(id));
  } else {
    
    currentDisplayedJobIds.value.forEach((id) =>
      selectedJobIds.value.delete(id),
    );
  }
};
/**
 * 处理独立接管复选框的选中.
 */
const handleRowCheckboxChange = (jobId: number, isChecked: boolean) => {
  if (isChecked) {
    selectedJobIds.value.add(jobId);
  } else {
    selectedJobIds.value.delete(jobId);
  }
};


watch(
  () => jobStore.queryParams.page,
  () => jobStore.fetchJobs(),
);
// 全局挂载生命周期首屏唤醒
onMounted(jobStore.fetchJobs);
</script>
