package com.arii.JobTracker.Service;

import com.arii.JobTracker.pojo.Job;
import com.arii.JobTracker.pojo.JobFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 上传文件核心业务处理接口
 *
 * @author Ari
 */
public interface FileService {
    /**
     * 将物理文件异步或同步持久化至服务器磁盘，并在数据库登记元数据.
     * <p>内部采用 UUID 机制重命名本地文件，以防止重名覆盖现象.</p>
     *
     * @param file 原始文件流
     * @param job  关联的事项实体对象
     * @return 持久化成功后的文件实体元数据
     * @throws IOException 当磁盘空间不足或无写入权限时抛出受检异常
     */
    JobFile storeFile(MultipartFile file, Job job) throws IOException;

    /**
     * 查询属于指定事项的所有附件，内置越权安全审查.
     *
     * @param jobId 事项ID
     * @return 经权限过滤后的文件列表
     * @throws RuntimeException 当事项不存在或当前登录人非该事项所有者时越权阻断
     */
    List<JobFile> getFilesByJobId(Integer jobId);

    /**
     * 执行强一致性的物理及逻辑删除.
     * <p>采用声明式事务，一旦物理磁盘文件删除失败，数据库记录将强制回滚，防止产生孤儿数据.</p>
     *
     * @param id 文件主键ID
     */
    void deleteFileById(Integer id);

}