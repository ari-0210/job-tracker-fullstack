<template>
  <n-modal
    v-model:show="show"
    :title="mode === 'add' ? '新增事项' : '修改事项'"
  >
    <n-form>
      <n-form-item label="公司">
        <n-input v-model:value="formModel.company" />
      </n-form-item>
      <n-button @click="handleSubmit">提交</n-button>
    </n-form>
  </n-modal>
</template>

<script setup>
import { useJobForm } from "@/composables/useJobForm";

const props = defineProps(["mode", "editData"]); // 接收模式和初始数据
const { formModel, resetForm } = useJobForm(props.editData);

const handleSubmit = () => {
  if (props.mode === "add") {
    // 调用新增接口
  } else {
    // 调用更新接口
  }
};
</script>
