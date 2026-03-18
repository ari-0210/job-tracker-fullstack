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

<script setup>
import { ref } from "vue";
import CreateButton from "@/components/CreateButton.vue";
import JobList from "@/components/JobList.vue";
import JobSearch from "@/components/JobSearch.vue";
import JobForm from "@/components/JobForm.vue";
import { useJobStore } from "@/stores/job";
const jobStore = useJobStore();
</script>
