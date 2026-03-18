import { defineStore } from "pinia";
import { ref } from "vue";
import type { Job, JobQueryParams } from "@/types/job";
import { jobApi } from "@/api/job";

export const useJobStore = defineStore("job", () => {
  // learn;--- 1. 列表状态 ---
  const jobs = ref<Job[]>([]);
  const total = ref(0);
  const loading = ref(false);
  const queryParams = ref<JobQueryParams>({
    keyword: "",
    page: 1,
    size: 10,
  });

  // learn;--- 3. 核心 Actions ---

  // learn;获取列表
  const fetchJobs = async () => {
    loading.value = true;
    try {
      const params = {
        ...queryParams.value,
        // learn;使用逻辑或 || 确保即使 page 是 undefined，也会按 1 计算
        page: (queryParams.value.page || 1) - 1,
        size: queryParams.value.size || 10,
      };
      const res = await jobApi.getJobs(params);
      // learn;对齐后端 Spring Page 结构
      jobs.value = res.data.content;
      total.value = res.data.totalElements;
    } finally {
      loading.value = false;
    }
  };

  //learn; --- 弹窗控制状态 ---
  const isFormShow = ref(false);
  const formMode = ref<"add" | "edit">("add");
  const currentJob = ref<Job | null>(null);

  // learn;控制弹窗开关
  const openForm = (mode: "add" | "edit", jobData: Job | null = null) => {
    formMode.value = mode;
    // learn;如果是编辑模式，克隆一份数据防止直接修改列表
    currentJob.value = jobData ? { ...jobData } : null;
    isFormShow.value = true;
  };

  const closeForm = () => {
    isFormShow.value = false;
    currentJob.value = null;
  };

  //learn;搜索
  const handleSearch = async () => {
    queryParams.value.page = 1;
    await fetchJobs();
  };

  // learn;统一保存逻辑 (根据 mode 自动判断调用哪个 API)
  const saveJob = async (formData: Job) => {
    loading.value = true;
    try {
      if (formMode.value === "edit" && formData.id) {
        await jobApi.updateJob(formData.id, formData);
      } else {
        await jobApi.createJob(formData);
      }
      closeForm();
      await fetchJobs(); // 保存成功后刷新列表
    } finally {
      loading.value = false;
    }
  };

  // learn;删除逻辑
  const removeJob = async (id: number) => {
    if (confirm("确定要删除吗？")) {
      await jobApi.deleteJob(id);
      await fetchJobs();
    }
  };

  // learn;批量删除
  const bulkDelete = async (ids: number[]) => {
    await jobApi.deleteMultipleJobs(ids);
    await fetchJobs();
  };

  return {
    jobs,
    total,
    loading,
    queryParams,
    isFormShow,
    formMode,
    currentJob,
    // 导出方法
    fetchJobs,
    bulkDelete,
    handleSearch,
    openForm,
    closeForm,
    saveJob,
    removeJob,
  };
});
