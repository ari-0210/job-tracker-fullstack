<template>
  <n-drawer
    :show="show"
    @update:show="emit('update:show', $event)"
    :width="500"
  >
    <n-drawer-content :title="jobDetail?.company + ' - 详情'" closable>
      <n-spin :show="loading">
        <n-space vertical size="large">
          <n-descriptions label-placement="left" bordered :column="1">
            <n-descriptions-item label="当前状态">
              <n-tag :type="getStatusType(jobDetail?.status)">
                {{ jobDetail?.status }}
              </n-tag>
            </n-descriptions-item>
            <n-descriptions-item label="截止日期">
              {{ jobDetail?.deadline }}
            </n-descriptions-item>
          </n-descriptions>

          <n-card title="备注" size="small">
            <button
              @click="handleGoToEdit"
              class="text-xs text-blue-600 hover:text-blue-800 flex items-center gap-1"
            >
              ✏️ 弹出编辑
            </button>

            <p
              class="text-sm text-gray-700 whitespace-pre-wrap leading-relaxed"
            >
              {{ jobDetail?.notes || "暂无备注信息" }}
            </p>
          </n-card>

          <n-card title="附件中心" size="small">
            <n-upload
              action="/api/files/uploads"
              :data="{ jobId: props.jobId }"
              :show-file-list="false"
              :headers="uploadHeaders"
              @finish="handleFinish"
              @error="handleUploadError"
            >
              <n-upload-dragger>
                <div style="margin-bottom: 12px">
                  <n-icon size="48" :depth="3"><CloudUploadOutline /></n-icon>
                </div>
                <n-text style="font-size: 16px"
                  >点击或拖拽文件到此处上传</n-text
                >
                <n-p depth="3" style="margin: 8px 0 0 0"
                  >支持 PDF、Word、图片，大小不超过 10MB</n-p
                >
              </n-upload-dragger>
            </n-upload>

            <n-list hoverable clickable class="mt-4">
              <n-list-item v-for="file in fileList" :key="file.id">
                <div class="flex justify-between items-center">
                  <n-space align="center">
                    <n-icon size="20"><DocumentTextOutline /></n-icon>
                    <span>{{ file.originalFileName }}</span>
                  </n-space>
                  <n-space>
                    <n-button size="small" @click="handlePreview(file)"
                      >预览</n-button
                    >
                    <n-button
                      quaternary
                      circle
                      type="primary"
                      @click="handleDownload(file)"
                    >
                      <template #icon><DownloadOutline /></template>
                    </n-button>
                    <n-popconfirm @positive-click="deleteFile(file.id)">
                      <template #trigger>
                        <n-button quaternary circle type="error">
                          <template #icon><TrashOutline /></template>
                        </n-button>
                      </template>
                      确定要永久删除这个附件吗？
                    </n-popconfirm>
                  </n-space>
                </div>
              </n-list-item>
              <n-empty v-if="fileList.length === 0" description="暂无附件" />
            </n-list>
          </n-card>
        </n-space>
      </n-spin>
    </n-drawer-content>
  </n-drawer>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { getStatusType } from "@/constants/job";
import { useJobStore } from "@/stores/job";

import {
  CloudUploadOutline,
  DocumentTextOutline,
  TrashOutline,
  DownloadOutline,
} from "@vicons/ionicons5";

import { FileApi } from "@/api/file";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();


const uploadHeaders = {
  Authorization: `Bearer ${authStore.token}`,
};

const handleUploadError = () => {
  window.$message?.error("上传失败，请检查网络或登录状态");
};


const props = defineProps<{
  jobId: number | null;
  show: boolean;
}>();

const emit = defineEmits(["update:show", "open-edit-form"]);

const handleGoToEdit = () => {
  if (!props.jobId) return;
  // 通知父组件：把详情页关掉和打开JobForm弹窗
  emit("update:show", false);
  emit("open-edit-form", props.jobId);
};

const jobStore = useJobStore();

const jobDetail = ref<any>(null);
const noteText = ref("");
const fileList = ref<any[]>([]);
const loading = ref(false);


watch(
  () => [props.jobId, props.show],
  async ([newId, isShow]) => {
    if (!newId || !isShow) return;
    if (newId && isShow) {
      loading.value = true;
      try {
        
        const originalJob = jobStore.jobs.find((j) => j.id === newId);
        jobDetail.value = originalJob;
        noteText.value = originalJob?.notes || "";

        
        await new Promise((resolve) => setTimeout(resolve, 50));
        
        
        const res = await FileApi.getFileByID(Number(newId));
        
        fileList.value = res.data.data || [];
      } catch (error: any) {
        console.error(
          "加载文件失败，错误详情:",
          error.config?.url,
          error.message,
          error.code,
        );
        fileList.value = []; 
      } finally {
        loading.value = false;
      }
    }
  },
  { immediate: true },
);

const handlePreview = (file: any) => {
  if (!file.savedFileName) return;
  const fileUrl = `/uploads/${file.savedFileName}`;
  
  window.open(fileUrl, "_blank");
};


const deleteFile = async (id: number) => {
  try {
    await FileApi.deleteFile(id);
    window.$message?.success("文件删除成功");
    
  } catch (error: any) {
    console.error("删除文件失败:", error);
    window.$message?.error("文件删除失败，请稍后重试");
  }
};

const handleDownload = (file: any) => {
  

  if (!file || !file.savedFileName) {
    console.error("属性缺失！", file);
    window.$message?.error("文件名不存在，无法下载");
    return;
  }

  
  const fileName = file.originalFileName || "download";

  const fileUrl = `/uploads/${file.savedFileName}`;

  
  const link = document.createElement("a");
  link.href = fileUrl;
  link.setAttribute("download", fileName); 
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};


const handleFinish = ({ event }: { event?: ProgressEvent }) => {
  
  const target = event?.target as XMLHttpRequest;
  if (target.status === 200 || target.status === 201) {
    const savedFile = JSON.parse(target.response);
    fileList.value.push(savedFile);
    window.$message?.success("文件上传成功");
  } else {
    window.$message?.error("上传失败：服务器返回错误");
  }
};
</script>
