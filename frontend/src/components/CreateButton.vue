<template>
  <n-button type="primary" @click="openCreateModal">Create New Job</n-button>

  <n-modal v-model:show="showModal">
    <n-card
      style="width: 600px"
      title="Create New Job"
      :bordered="false"
      size="huge"
      role="dialog"
      aria-modal="true"
    >
      <n-form>
        <n-form-item label="Company">
          <n-input
            v-model:value="newJobData.company"
            placeholder="Input Company"
          />
        </n-form-item>
        <n-form-item label="Position">
          <n-input
            v-model:value="newJobData.position"
            placeholder="Input Position"
          />
        </n-form-item>
        <n-form-item label="ApplyDate">
          <n-date-picker
            v-model:formatted-value="newJobData.applyDate"
            type="date"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
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
              />
            </n-space>
          </n-radio-group>
        </n-form-item>
        <n-form-item label="Tags">
          <n-input
            v-model:value="newJobData.tags"
            placeholder="Input tags (comma separated)"
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
  position: "",
  apply_date: null,
  status: "5",
  tags: "",
});
const newJobData = ref(getDefaultNewJob());

const statusOptions = ref([
  { value: "1", label: "Applied" },
  { value: "2", label: "Interviewing" },
  { value: "3", label: "Offered" },
  { value: "4", label: "Rejected" },
  { value: "5", label: "Pending" },
]);

const emit = defineEmits(["job-created"]);

const openCreateModal = () => {
  newJobData.value = getDefaultNewJob();
  showModal.value = true;
};
const handleCreateJob = async () => {
  if (!newJobData.value.company || !newJobData.value.position) {
    alert("Company and Position are required!");
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
      // 打印后端返回的具体错误数据
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
