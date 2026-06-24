/**
 * 事项关联物理附件（文件）核心契约定义
 */
export interface JobFile {
  /** 文件在数据库中的唯一自增主键 ID */
  id: number;

  /** 用户上传时的原始文件名（用于前端界面友好展示，如 "简历.pdf"） */
  originalFileName: string;

  /** 存储在服务器硬盘上的物理文件名（防重名混淆后的名字） */
  savedFileName: string;

  /** 文件后缀或 MIME 类型（如：application/pdf, image/png，可用于判断渲染什么图标） */
  contentType: string;

  /** 文件体积大小（单位：Bytes 字节） */
  fileSize: number;

  /** 文件上传并落库的时间戳（格式：yyyy-MM-dd HH:mm:ss） */
  uploadTime?: string;

  
  
}
