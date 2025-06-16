import apiClient from './axiosInstance';

export const getJobs = (queryParams) => {
  console.log("DEBUG: 'getJobs' function in api/job.js is being called with params:", queryParams);
// 创建请求配置对象
  const requestConfig = { 
    url: '/jobs', // 目标路径
    params: queryParams // 查询参数
  };
  
  // 使用 Axios 的辅助方法打印出将要请求的完整 URL
  // getUri() 会将 baseURL 和请求的 url、params 组合起来
  try {
    console.log("DEBUG: Axios is about to request a URL computed as:", apiClient.getUri(requestConfig));
  } catch(e) {
    console.error("DEBUG: Error trying to get URI from Axios config", e);
  }

  // 使用 apiClient 实例发起请求
  return apiClient.get(requestConfig.url, { params: requestConfig.params });
};


export const createJob = (jobData) => {
  return apiClient.post('/jobs', jobData);
};

export const updateJob = (id, jobData) => {
  return apiClient.put(`/jobs/${id}`, jobData);
};

export const deleteJob = (id) => {
  return apiClient.delete(`/jobs/${id}`);
};

export const deleteMultipleJobs = (ids) => {
  return apiClient.post('/jobs/batch-delete', { ids: ids });
};