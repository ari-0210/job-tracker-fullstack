import { ref } from "vue";
import type { Job } from "@/types/job";

export function useJobForm(initialData = {}) {
  const formModel = ref<Job>({
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
