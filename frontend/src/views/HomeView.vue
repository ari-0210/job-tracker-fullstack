<template>
  <div class="home">
    <create-button @job-created="handleJobWasCreated" />
    <job-list ref="jobListReference" />
  </div>
</template>

<script setup>
import { ref } from "vue";
import CreateButton from "@/components/CreateButton.vue";
import JobList from "@/components/JobList.vue";

const jobListReference = ref(null);

const handleJobWasCreated = () => {
  console.log(
    "App.vue: Received job-created event. Attempting to refresh job list."
  );
  if (
    jobListReference.value &&
    typeof jobListReference.value.fetchJobs === "function"
  ) {
    jobListReference.value.fetchJobs();
  } else {
    if (!jobListReference.value) {
      console.warn(
        'App.vue: jobListReference is null. Ensure <job-list ref="jobListReference" /> is correct and component has mounted.'
      );
    } else if (typeof jobListReference.value.fetchJobs !== "function") {
      console.warn(
        "App.vue: fetchJobs method is not exposed on JobList component. Did you use defineExpose in JobList.vue?"
      );
    }
  }
};
</script>
