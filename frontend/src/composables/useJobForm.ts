import { ref } from "vue";
import type { Job } from "@/types/job";

export function useJobForm(initialData = {}) {
  const formModel = ref<Job>({
    company: "",
    title: "",
    reminderDate: null,
    status: "DRAFT",
    tags: "",
    deadline: new Date().toISOString().split("T")[0] + " 23:59:59",
    ...initialData,
  });

  const resetForm = () => {
    formModel.value = {
      company: "",
      title: "",
      reminderDate: null,
      status: "DRAFT",
      tags: "",
      deadline: new Date().toISOString().split("T")[0] + " 23:59:59",
    };
  };

  return { formModel, resetForm };
}
