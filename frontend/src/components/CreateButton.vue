<template>
  <n-button type="primary" @click="openCreateModal">Create New Job</n-button>

  <n-modal v-model:show="showModal">
    <n-card
      style="width: 600px"
      title="Create New Submissions/创建新事项"
      :bordered="false"
      size="huge"
      role="dialog"
      aria-modal="true"
    >
      <n-form>
        <n-form-item label="Recipient">
          <n-input
            v-model:value="newJobData.company"
            placeholder="输入事项接收方"
          />
        </n-form-item>
        <n-form-item label="Title">
          <n-input
            v-model:value="newJobData.title"
            placeholder="输入事项标题"
          />
        </n-form-item>
        <n-form-item label="ReminderDate">
          <n-date-picker
            v-model:formatted-value="newJobData.reminderDate"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="'09:00:00'"
            style="width: 100%"
          />
        </n-form-item>

        <n-form-item label="Status">
          <n-radio-group
            v-model:value="newJobData.status"
            name="jobstatus_create"
          >
            <n-space>
              <n-radio
                v-for="option in statusOptions"
                :key="option.value"
                :value="option.value"
                :label="option.label"
                >{{ option.label }}
              </n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="Tags">
          <n-input
            v-model:value="newJobData.tags"
            placeholder="输入标签 (空格分离)"
          />
        </n-form-item>
      </n-form>

      <template #footer>
        <n-button @click="handleCancelCreate">Cancel</n-button>
        <n-button type="primary" @click="handleCreateJob">Create Job</n-button>
      </template>
    </n-card>
  </n-modal>
</template>

<script setup>
import { ref, defineEmits } from "vue";
import { createJob } from "@/api/job";

const showModal = ref(false);

const getDefaultNewJob = () => ({
  company: "",
  title: "",
  reminderDate: null,
  status: "DRAFT",
  tags: "",
});
const newJobData = ref(getDefaultNewJob());

const statusOptions = ref([
  { value: "DRAFT", label: "草稿" },
  { value: "APPLIED", label: "已投递" },
  { value: "INTERVIEWING", label: "面试中" },
  { value: "COMPLETED", label: "已完成" },
]);

const emit = defineEmits(["job-created"]);

const openCreateModal = () => {
  newJobData.value = getDefaultNewJob();
  showModal.value = true;
};
const handleCreateJob = async () => {
  if (!newJobData.value.company || !newJobData.value.title) {
    alert("Company and Title are required!");
    return;
  }

  try {
    await createJob(newJobData.value);

    console.log("Job created successfully:", newJobData.value);
    showModal.value = false;

    emit("job-created");
  } catch (error) {
    console.error("Failed to create job:", error);
    if (error.response) {
      // learning:打印后端返回的具体错误数据
      console.error("Backend response data:", error.response);
      alert("Error creating job: " + JSON.stringify(error.response));
    } else if (error.request) {
      console.error("No response received from server:", error.request);
    } else {
      console.error("Error setting up request:", error.message);
    }
  }
};

const handleCancelCreate = () => {
  showModal.value = false;
};
</script>
