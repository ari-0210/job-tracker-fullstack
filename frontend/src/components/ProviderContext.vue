<script setup lang="ts">
import { useMessage, useDialog, useNotification } from "naive-ui";

// 在组件初始化时挂载
window.$message = useMessage();
window.$dialog = useDialog();
window.$notification = useNotification();
</script>

<template>
  <div />
</template>
