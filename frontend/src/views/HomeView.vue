<template>
  <div class="home">
    <create-button />
    <job-search />
    <job-list />
    <job-form
      v-model:show="jobStore.isFormShow"
      :mode="jobStore.formMode"
      :initialData="jobStore.currentJob"
      @save="jobStore.saveJob"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import CreateButton from "@/components/CreateButton.vue";
import JobList from "@/components/JobList.vue";
import JobSearch from "@/components/JobSearch.vue";
import JobForm from "@/components/JobForm.vue";
import { useJobStore } from "@/stores/job";
import { useRoute } from "vue-router";
const jobStore = useJobStore();
const route = useRoute();

onMounted(async () => {
  const editId = route.query.editId;
  if (editId) {
    // learn;确保数据已经加载（如果 jobStore.jobs 是空的，需要先 fetch）
    if (jobStore.jobs.length === 0) {
      await jobStore.fetchJobs();
    }
    // learn;找到对应的 Job
    const targetJob = jobStore.jobs.find((j) => j.id === Number(editId));
    if (targetJob) {
      jobStore.currentJob = { ...targetJob };
      jobStore.formMode = "edit";
      jobStore.isFormShow = true;
    }
  }
});
</script>
