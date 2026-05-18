import apiClient from "./client";

export const FileApi = {
  getFileByID: (id: number) => {
    return apiClient.get(`/files/job/${id}`);
  },
  deleteFile: (fileId: number) => {
    return apiClient.delete(`/files/${fileId}`);
  },
};
