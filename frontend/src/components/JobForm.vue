<template>
  <n-modal :show="show" @update:show="(v: boolean) => emit('update:show', v)">
    <n-card
      style="width: 600px"
      :title="mode === 'add' ? '新增事项' : '编辑事项'"
      :bordered="false"
      size="huge"
      role="dialog"
      aria-modal="true"
    >
      <n-form :model="formModel">
        <n-form-item label="Recipient(接收方)">
          <n-input
            v-model:value="formModel.company"
            placeholder="Input Recipient(请输入接收方)"
          />
        </n-form-item>
        <n-form-item label="Title">
          <n-input
            v-model:value="formModel.title"
            placeholder="Input Title(请输入事项标题)"
          />
        </n-form-item>
        <n-form-item label="ReminderDate">
          <n-date-picker
            v-model:formatted-value="formModel.reminderDate"
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            :default-time="'09:00:00'"
          />
        </n-form-item>
        <n-form-item label="Status">
          <n-radio-group v-model:value="formModel.status" name="jobstatus">
            <n-space>
              <n-radio
                v-for="option in STATUS_OPTIONS"
                :key="option.value"
                :value="option.value"
              >
                {{ option.label }}
              </n-radio>
            </n-space>
          </n-radio-group>
        </n-form-item>

        <n-form-item label="tags">
          <n-input
            v-model:value="formModel.tags"
            placeholder="Input tags(请输入标签)"
          />
        </n-form-item>
      </n-form>
      <template #footer>
        <n-space justify="end">
          <n-button @click="emit('update:show', false)">取消</n-button>
          <n-button type="primary" :loading="loading" @click="handleSave"
            >保存</n-button
          >
        </n-space>
      </template>
    </n-card>
  </n-modal>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { STATUS_OPTIONS } from "@/constants/job";
import type { Job } from "@/types/job";

const props = defineProps<{
  show: boolean;
  mode: "add" | "edit";
  initialData: Job | null;
  loading?: boolean;
}>();

const emit = defineEmits<{
  (e: "update:show", value: boolean): void;
  (e: "save", data: Job): void;
}>();

const formModel = ref<Job>({
  company: "",
  title: "",
  reminderDate: null,
  status: "DRAFT",
  tags: "",
});

// learn：当弹窗打开时，根据 initialData 初始化表单
watch(
  () => props.show,
  (isShowing) => {
    if (isShowing) {
      if (props.mode === "edit" && props.initialData) {
        formModel.value = { ...props.initialData }; // 编辑模式：克隆
      } else {
        formModel.value = {
          company: "",
          title: "",
          reminderDate: null,
          status: "DRAFT",
          tags: "",
        };
      }
    }
  },
);

const handleSave = () => {
  emit("save", { ...formModel.value });
};
</script>
