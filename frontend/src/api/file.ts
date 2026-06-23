import apiClient from "./client";
import type { Result } from "@/types/job";
/**
 * 事项关联物理附件生命周期客户端代理对象.
 * @author Ari
 */
export const FileApi = {
  /**
   * 根据申请事项主键拉取其名下登记的所有附件列表.
   * @param {number} id - 关联的申请事项（Job）的主键 ID
   * @returns {Promise<Result<JobFile[]>>} 返回包含该事项下所有文件元数据（大小、扩展名等）的集合
   */
  getFileByID: (id: number) => {
    return apiClient.get<Result<JobFile[]>>(`/files/job/${id}`);
  },
  /**
   * 根据主键执行归属权强审计安全逻辑删除.
   * <p>注意：后端内置水平越权校验，非文件上传者行为将被服务端拦截拒绝。</p>
   * @param {number} fileId - 文件记录的唯一自增主键 ID
   * @returns {Promise<Result<null>>}
   */
  deleteFile: (fileId: number) => {
    return apiClient.delete<Result<null>>(`/files/${fileId}`);
  },
};
