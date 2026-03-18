import apiClient from "./client";
import type { Job, JobQueryParams } from "@/types/job";

export const jobApi = {
  getJobs: (queryParams: JobQueryParams) => {
    // learn;使用 apiClient 实例发起请求
    return apiClient.get("/jobs", { params: queryParams });
  },

  createJob: (jobData: Job) => {
    return apiClient.post("/jobs", jobData);
  },

  updateJob: (id: number, jobData: Job) => {
    return apiClient.put(`/jobs/${id}`, jobData);
  },

  deleteJob: (id: number) => {
    return apiClient.delete(`/jobs/${id}`);
  },

  deleteMultipleJobs: (ids: number[]) => {
    return apiClient.post("/jobs/batch-delete", { ids: ids });
  },
};
