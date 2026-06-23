import { defineStore } from "pinia";
import { ref } from "vue";
import type { Job, JobQueryParams } from "@/types/job";
import { jobApi } from "@/api/job";

/** 全局网络异常与 HTTP 异步状态追溯捕获器 */
const fetchError = ref<any>(null);

/**
 * 申请事项生命周期流动管理与 UI 弹窗状态核心编排轴仓库 (Pinia Store).
 * <p>集中式收纳和流转所有事项视图数据，内聚处理前后端分页架构契约对齐、编辑回显拷贝克隆、多维异步删改逻辑.</p>
 * * @author Ari
 */
export const useJobStore = defineStore("job", () => {
  // ==========================================
  // 1. 列表渲染与多维检索核心状态定义
  // ==========================================

  /** 当前页申请事项核心数据集 */
  const jobs = ref<Job[]>([]);
  /** 契合后端 Spring Page 模型的全大盘符合过滤条件的数据总物理条数 */
  const total = ref(0);
  /** 控制表格或整页全局全屏加载蒙层的状态锁 */
  const loading = ref(false);
  /** 深度联动前端表单的复合型查询与分页控制参数结构体 */
  const queryParams = ref<JobQueryParams>({
    keyword: "",
    page: 1, //learn; 前端视觉常识：从第 1 页开启计数
    size: 10,
  });

  // ==========================================
  // 2. 核心全栈业务 Actions 逻辑
  // ==========================================

  /**
   * 调度后端 API 执行分页异步拉取清洗.
   * <p>契约对齐：由于 Vue 组件库的分页普遍采用 1-based 基数，而后端 Spring Boot JPA 采用 0-based 基数，</p>
   * <p>本方法在发射请求前，会硬性将当前 page 执行负向对齐换算（page - 1），完美融合两端规范。</p>
   */
  const fetchJobs = async () => {
    loading.value = true;
    fetchError.value = null;
    try {
      const params = {
        ...queryParams.value,
        // 防御式编程：防止 page 在并发中退化为 undefined 触发空指针
        page: (queryParams.value.page || 1) - 1,
        size: queryParams.value.size || 10,
      };
      const res = await jobApi.getJobs(params);
      // 深度适配 Spring Page<T> 的规范 JSON 元数据解构
      // learn; 防御加固：如果后端没有吐出 .content，兜底给它一个干净的 []，绝不让 jobs 退化为 undefined
      jobs.value = res.data.data?.content || [];
      total.value = res.data.data?.totalElements || 0;
    } catch (err) {
      fetchError.value = err; //
      console.error("Fetch jobs failed:", err);
    } finally {
      loading.value = false;
    }
  };
  // ==========================================
  // 3. 模态弹窗（Modal Form）高内聚控制流状态
  // ==========================================

  /** 驱动新增/修改 Element 对话框显隐的核心开关指针 */
  const isFormShow = ref(false);
  /** 区分当前弹窗内部业务语义的模式锁 */
  const formMode = ref<"add" | "edit">("add");
  /** 专门用于表单回显与暂存编辑状态的单体中间数据模型载荷 */
  const currentJob = ref<Job | null>(null);
  /**
   * 触发弹窗，并执行增量/存量状态自清洁切换.
   * <p>安全隔离：处于 'edit' 模式时，采用 ES6 解构特性 `{ ...jobData }` 对原始行数据执行【浅拷贝克隆】。</p>
   * <p>能彻底隔离视图双向绑定污染，防止用户在弹窗输入时，导致外层列表背后的数据跟着实时被篡改的重大 UI 翻车事故。</p>
   *
   * @param {"add" | "edit"} mode - 指望唤醒的表单模式语义
   * @param {Job | null} [jobData=null] - 当执行修改回显时，由外部行级传入的原始实体模型数据
   */
  const openForm = (mode: "add" | "edit", jobData: Job | null = null) => {
    formMode.value = mode;
    // learn;如果是编辑模式，克隆一份数据防止直接修改列表
    currentJob.value = jobData ? { ...jobData } : null;
    isFormShow.value = true;
  };
  /**
   * 彻底归位表单控制状态，安全降下弹窗并擦除暂存区残留.
   */
  const closeForm = () => {
    isFormShow.value = false;
    currentJob.value = null;
  };
  /**
   * 重置分页并触发全量首屏搜索.
   */
  const handleSearch = async () => {
    queryParams.value.page = 1;
    await fetchJobs();
  };

  /**
   * 智能分流保存动作（聚合新增与修改双通道方案）.
   * <p>根据当前 Store 内置维护的 formMode 状态智能裁决。保存成功后自动执行视图收缩关闭，并无缝触发大盘列表时效性刷新。</p>
   * @param {Job} formData - 经由 Vue Form 组件收集校验完毕的纯净就绪业务事项载荷
   */
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

  /**
   * 联动单体物理事项彻底从服务端数据库收割斩断.
   * @param {number} id - 指望拔除的记录唯一主键 ID
   */
  const removeJob = async (id: number) => {
    await jobApi.deleteJob(id);
    await fetchJobs();
  };

  /**
   * 企业级高并发大面积批量清算物理收割动作.
   * @param {number[]} ids - 在多选表格中被勾选锁定的高危目标 ID 数组集合
   */
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
    fetchError,
    fetchJobs,
    bulkDelete,
    handleSearch,
    openForm,
    closeForm,
    saveJob,
    removeJob,
  };
});
