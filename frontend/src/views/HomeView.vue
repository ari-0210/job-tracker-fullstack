<template>
  <div class="home">
    <create-button />
    <job-list />
  </div>
</template>

<script setup>
import { ref } from "vue";
import CreateButton from "@/components/CreateButton.vue";
import JobList from "@/components/JobList.vue";
</script>
