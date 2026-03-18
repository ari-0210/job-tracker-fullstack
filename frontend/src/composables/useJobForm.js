import { ref } from "vue";

export function useJobForm(initialData = {}) {
  const formModel = ref({
    company: "",
    title: "",
    reminderDate: null,
    status: "DRAFT",
    tags: "",
    ...initialData, // learn;如果是修改，会覆盖默认值
  });

  const resetForm = () => {
    formModel.value = {
      company: "",
      title: "",
      reminderDate: null,
      status: "DRAFT",
      tags: "",
    };
  };

  return { formModel, resetForm };
}
