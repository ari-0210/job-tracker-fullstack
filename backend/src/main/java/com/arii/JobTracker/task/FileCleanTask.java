package com.arii.JobTracker.task;

import com.arii.JobTracker.Repository.JobFileRepository;
import com.arii.JobTracker.pojo.JobFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 服务器磁盘孤儿附件自动流转与物理收割定时任务类.
 * <p>通过定时双向比对本地存储目录与数据库元数据，强制熔断并清理数据库无归属的孤儿脏文件.</p>
 */
@Slf4j
@Component
public class FileCleanTask {

    @Autowired
    private JobFileRepository fileRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 每天凌晨 2 点准时触发文件异步物理核销.
     * <p>Cron 表达式解释：[秒] [分] [时] [日] [月] [周]</p>
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOrphanFiles() {
        log.info("============== 开始执行凌晨磁盘孤儿文件自动清理任务 ==============");
        try {
            File directory = new File(uploadPath);
            if (!directory.exists() || !directory.isDirectory()) {
                log.warn("配置的上传目录不存在，跳过清理。");
                return;
            }

            //从数据库捞出当前所有合法记录里的保存文件名(长UUID名)，存入内存 Set 字典
            List<JobFile> allRecordedFiles = fileRepository.findAll();
            Set<String> validFileNames = allRecordedFiles.stream()
                    .map(JobFile::getSavedFileName)
                    .collect(Collectors.toSet());

            // 扫描服务器硬盘上该文件夹下的所有实际物理文件
            File[] filesOnDisk = directory.listFiles();
            if (filesOnDisk == null || filesOnDisk.length == 0) {
                log.info("磁盘中无可清理的物理文件。");
                return;
            }

            int deleteCount = 0;
            // 双向比对和清理孤儿文件
            for (File physicalFile : filesOnDisk) {
                String fileName = physicalFile.getName();


                if (!validFileNames.contains(fileName)) {
                    Path pathToDelete = Paths.get(physicalFile.getAbsolutePath());
                    Files.deleteIfExists(pathToDelete);
                    deleteCount++;
                    log.warn("成功物理收割孤儿脏文件: {}", fileName);
                }
            }
            log.info("============== 磁盘清理任务结束，本次共释放孤儿文件 {} 个 ==============", deleteCount);
        } catch (Exception e) {
            log.error("执行定时清理文件任务时发生致命崩溃: ", e);
        }
    }
}