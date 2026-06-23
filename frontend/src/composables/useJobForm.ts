import { ref } from "vue";
import type { Job } from "@/types/job";
/**
 * 申请事项表单驱动与生命周期复位高级 Composable 业务切片 Hook.
 * <p>解耦并代理响应式表单对象的初始化绑定、默认安全状态注入以及一键无缝复位逻辑。</p>
 * @author Ari
 * @param {Partial<Job>} [initialData={}] - 允许外部注入的初始化表单复用存量数据 (如修改回显场景)
 * @returns {Object} 包含响应式表单模型实例(formModel)与重置函数(resetForm)的组合载荷
 */
export const useJobForm = (initialData = {}) => {
  const formModel = ref<Job>({
    company: "",
    title: "",
    reminderDate: null,
    status: "DRAFT", // 默认锁定初生生命周期：草稿
    tags: "",
    notes: "",
    deadline: new Date().toISOString().split("T")[0] + " 23:59:59",
    // 建立高内聚响应式模型，默认配置当前自然日极限边缘时段（23:59:59）作为默认红线
    ...initialData,
  });
  /**
   * 将表单状态一键复位至纯净初始状态.
   */
  const resetForm = () => {
    formModel.value = {
      company: "",
      title: "",
      reminderDate: null,
      status: "DRAFT",
      tags: "",
      notes: "",
      deadline: new Date().toISOString().split("T")[0] + " 23:59:59",
    };
  };

  return { formModel, resetForm };
};
