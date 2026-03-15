import { ref } from "vue";

export function useJobForm(initialData = {}) {
  const formModel = ref({
    company: "",
    title: "",
    reminderDate: null,
    status: "DRAFT",
    tags: "",
    ...initialData, // learning:如果是修改，会覆盖默认值
  });

  const statusOptions = [
    { value: "DRAFT", label: "草稿" },
    { value: "APPLIED", label: "已投递" },
    { value: "INTERVIEWING", label: "面试中" },
    { value: "COMPLETED", label: "已完成" },
  ];

  const resetForm = () => {
    formModel.value = {
      company: "",
      title: "",
      reminderDate: null,
      status: "DRAFT",
      tags: "",
    };
  };

  return { formModel, statusOptions, resetForm };
}
