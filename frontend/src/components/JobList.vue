<template>
  <div>
    <n-space style="margin-bottom: 20px; display: flex">
      <n-input
        v-model:value="searchQuery"
        placeholder="Please input keywords for searching(请输入查找关键字)"
        clearable
        style="flex-grow: 1; max-width: 800px"
        @keyup.enter="handleSearch"
      />
      <n-button type="primary" @click="handleSearch" :loading="isLoading">
        Search
      </n-button>
    </n-space>
    <n-space style="margin-bottom: 10px" v-if="selectedJobIds.size > 0">
      <n-button type="error" @click="handleBulkDelete" :loading="isLoading">
        Delete ({{ selectedJobIds.size }})
      </n-button>
    </n-space>
    <div v-if="isLoading">Loading jobs...(工作加载中……)</div>
    <div v-if="fetchError && !isLoading">
      Error loading jobs: {{ fetchError.message }}
    </div>
    <n-table v-if="!isLoading && !fetchError" :single-line="false">
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
          <th>CreateDate</th>
          <th>status</th>
          <th>Tags</th>
          <th>ReminderDate</th>
          <th>UpdateDate</th>
          <th>Operation</th>
        </tr>
      </thead>
      <tbody>
        <tr v-if="jobs.length === 0 && !isLoading">
          <td colspan="9" style="text-align: center">No jobs found.</td>
        </tr>
        <tr v-for="job in jobs" :key="job.id">
          <td style="text-align: center">
            <n-checkbox
              :checked="selectedJobIds.has(job.id)"
              @update:checked="
                (checkedValue) => handleRowCheckboxChange(job.id, checkedValue)
              "
            />
          </td>
          <td>{{ job.company }}</td>
          <td>{{ job.title }}</td>
          <td>{{ job.applyDate }}</td>
          <td>{{ getStatusText(job.status) }}</td>
          <td>{{ job.tags }}</td>
          <td>{{ job.reminderDate }}</td>
          <td>{{ job.updateDate }}</td>
          <td>
            <n-button @click="openUpdateModal(job)">Update</n-button>
            <n-button type="error" @click="confirmDelete(job.id)"
              >Delete</n-button
            >
          </td>
        </tr>
      </tbody>
    </n-table>
    <n-pagination
      v-if="!isLoading && !fetchError && totalJobsCount > 0"
      v-model:page="currentPage"
      v-model:page-size="currentPageSize"
      :item-count="totalJobsCount"
      show-size-picker
      :page-sizes="[10, 20, 30, 40]"
      show-quick-jumper
      :disabled="isLoading"
      style="margin-top: 20px; display: flex; justify-content: flex-end"
    />
  </div>
  <n-modal v-model:show="showModal">
    <n-card
      style="width: 600px"
      title="Update Submission(更新事项)"
      :bordered="false"
      size="huge"
      role="dialog"
      aria-modal="true"
    >
      <n-form v-if="editingJob">
        <n-form-item label="Recipient(接收方)">
          <n-input
            v-model:value="editingJob.company"
            placeholder="Input Recipient(请输入接收方)"
          />
        </n-form-item>
        <n-form-item label="Title">
          <n-input
            v-model:value="editingJob.title"
            placeholder="Input Title(请输入事项标题)"
          />
        </n-form-item>
        <n-form-item label="ReminderDate">
          <n-date-picker
            v-model:formatted-value="editingJob.reminderDate"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="'09:00:00'"
          />
        </n-form-item>
        <n-form-item label="Status">
          <n-radio-group v-model:value="editingJob.status" name="jobstatus">
            <n-space>
              <n-radio
                v-for="option in statusOptions"
                :key="option.value"
                :value="option.value"
                :label="option.label"
                >{{ option.label }}</n-radio
              >
            </n-space>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="tags">
          <n-input
            v-model:value="editingJob.tags"
            placeholder="Input tags(请输入标签)"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-button @click="handleCancelUpdate">Cancel</n-button>
        <n-button type="primary" @click="handleUpdateJob"
          >Save Changes(保存变更)</n-button
        >
      </template>
    </n-card>
  </n-modal>
</template>

<script setup>
import { ref, onMounted, watch, computed, defineExpose } from "vue";
import { getJobs, deleteJob, updateJob, deleteMultipleJobs } from "@/api/job";
const jobs = ref([]);
const isLoading = ref(false);
const fetchError = ref(null);

const confirmDelete = async (id) => {
  if (
    confirm(
      "Are you sure you want to delete this submission?你确定要删除这个事项吗?",
    )
  ) {
    await deleteJob(id);
    console.log("has deleted(已经删除):", id);
    fetchJobs();
  }
};

// learning:.find 会遍历数组，寻找第一个满足条件的项。
// learning:opt => opt.value === statusStr 表示：当选项里的 value 等于 后端传来的字符串时，返回这个选项对象。
// learning:如果找到了(option 为真)，就返回它的 label；
// learning:如果 statusStr 也是空的，就显示 "无数据"。
const getStatusText = (statusStr) => {
  const option = statusOptions.value.find((opt) => opt.value === statusStr);
  return option ? option.label : statusStr || "无数据";
};

const showModal = ref(false);
const editingJob = ref(null);
const statusOptions = ref([
  { value: "DRAFT", label: "草稿" },
  { value: "APPLIED", label: "已投递" },
  { value: "INTERVIEWING", label: "面试中" },
  { value: "COMPLETED", label: "已完成" },
]);

function openUpdateModal(job) {
  try {
    editingJob.value = structuredClone(job);
    showModal.value = true;
  } catch (e) {
    console.error("Failed to clone job object:", e);
    editingJob.value = JSON.parse(JSON.stringify(job));
    showModal.value = true;
  }
}

const handleUpdateJob = async () => {
  if (!editingJob.value || editingJob.value.id == null) {
    console.error("No job selected for update or job ID is missing.");
    return;
  }

  try {
    // learning:直接拷贝，不需要任何 parseInt 或 isNaN 检查
    const payload = { ...editingJob.value };

    console.log("Sending update payload:", payload);
    await updateJob(payload.id, payload);

    showModal.value = false;
    fetchJobs();
  } catch (error) {
    console.error("Failed to update job:", error);
    if (error.response) {
      console.error(
        "Backend response data for UPDATE failure:",
        error.response.data,
      );
      alert("Error updating job: " + JSON.stringify(error.response.data)); // 弹窗提示
    } else if (error.request) {
      console.error("No response received for update:", error.request);
    } else {
      console.error("Error setting up update request:", error.message);
    }
  }
};

const handleCancelUpdate = () => {
  showModal.value = false;
  editingJob.value = null;
};

// 分页
const currentPage = ref(1);
const currentPageSize = ref(10); // 默认每页条数
const totalJobsCount = ref(0); // 从后端获取的总条目数

const searchQuery = ref(""); // 存储搜索输入框的值
// learning:修改 fetchJobs 以包含分页参数
const fetchJobs = async (searchKeyword = searchQuery.value) => {
  isLoading.value = true;
  fetchError.value = null;
  try {
    const params = {
      page: currentPage.value - 1,
      size: currentPageSize.value,
    };
    if (searchKeyword && searchKeyword.trim() !== "") {
      params.search = searchKeyword.trim(); // 后端接收的参数名是 'search'
    }
    console.log("Fetching jobs with params:", params);
    const response = await getJobs(params); // getJobs 需要能传递 params
    console.warn(
      "Raw response data from backend:",
      JSON.stringify(response.data),
    );
    // 根据后端实际返回结构来解析数据和总数
    // 后端返回 Spring Page 对象 { content: [], totalElements: N }
    if (response.data && response.data.content) {
      jobs.value = response.data.content;
      totalJobsCount.value = response.data.totalElements;
      console.warn(
        "Jobs loaded:",
        jobs.value.length,
        "Total:",
        totalJobsCount.value,
      );
    } else {
      jobs.value = [];
      totalJobsCount.value = 0;
      console.error(
        "Unexpected response structure from backend in production:",
        response.data,
      );
    }
  } catch (err) {
    console.error("Failed to fetch jobs (production build):", err);
    if (err.response) {
      console.error(
        "Backend error response data (production build):",
        err.response.data,
      );
    }
    fetchError.value = err;
    jobs.value = [];
    totalJobsCount.value = 0;
  } finally {
    isLoading.value = false;
    console.warn("fetchJobs finished. isLoading:", isLoading.value);
  }
};

const handleSearch = () => {
  // 从第一页开始显示结果
  if (currentPage.value !== 1) {
    currentPage.value = 1; // 这会触发下面的 watch(currentPage, ...) 来调用 fetchJobs
  } else {
    // 如果当前已在第一页，直接用新的搜索词获取数据
    fetchJobs(searchQuery.value);
  }
};
// learning:监听 currentPage 的变化，当它变动时重新获取数据
watch(currentPage, (newPage, oldPage) => {
  if (newPage !== oldPage) {
    // 确保值真的变了
    fetchJobs();
  }
});

// learning:监听 currentPageSize 的变化
watch(currentPageSize, (newSize, oldSize) => {
  if (newSize !== oldSize) {
    // 当每页数量变化时回到第一页
    if (currentPage.value !== 1) {
      currentPage.value = 1; // 这会自动触发上面对 currentPage 的 watch，进而调用 fetchJobs
    } else {
      fetchJobs(); // 如果已经在第一页，直接用新的 pageSize 获取数据
    }
  }
});

// learning:批量删除相关的状态
const selectedJobIds = ref(new Set()); // 使用 Set 存储选中的 job ID，方便添加、删除和检查存在性

// 计算当前页所有 job 的 ID (用于全选逻辑)
const currentDisplayedJobIds = computed(() => {
  return jobs.value.map((job) => job.id);
});

// 计算主复选框的状态 (全选、部分选、全不选) - 仅针对当前页
const masterCheckboxState = computed(() => {
  if (currentDisplayedJobIds.value.length === 0) {
    return false; // 当前页没有数据，全选框不选中
  }
  // 检查当前页是否所有项都被选中
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

//批量删除相关的处理函数

// 处理主复选框（全选/全不选当前页）的变化
const handleMasterCheckboxChange = (checked) => {
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

const handleRowCheckboxChange = (jobId, isChecked) => {
  if (isChecked) {
    selectedJobIds.value.add(jobId);
  } else {
    selectedJobIds.value.delete(jobId);
  }
};

const handleBulkDelete = async () => {
  if (selectedJobIds.value.size === 0) {
    alert("Please select at least one record to delete");
    return;
  }

  if (
    confirm(
      `Are you sure you want to delete ${selectedJobIds.value.size} record(s)?`,
    )
  ) {
    try {
      const idsToDelete = Array.from(selectedJobIds.value); // 将 Set 转换为数组

      console.log("调用后端API批量删除以下ID:", idsToDelete);
      await deleteMultipleJobs(idsToDelete);

      alert(` Have deleted ${idsToDelete.length} record(s)`);

      selectedJobIds.value.clear(); // 清空已选中的ID
      fetchJobs();
    } catch (error) {
      console.error("批量删除失败:", error);
      alert("批量删除失败，请查看控制台获取更多信息。");
    }
  }
};

defineExpose({ fetchJobs });
onMounted(fetchJobs);
</script>
