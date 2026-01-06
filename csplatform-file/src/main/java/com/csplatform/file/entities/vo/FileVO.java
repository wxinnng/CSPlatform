package com.csplatform.file.entities.vo;


import com.baomidou.mybatisplus.annotation.TableField;
import com.csplatform.file.entities.FileInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author:WangXing
 * @DATE:2024/6/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 文件实体
     */
    private MultipartFile file;

    /**
     * 片数索引
     */
    private Integer chunkNumber;

    /**
     * 总片数
     */
    private Integer chunkTotal;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件md5
     */
    private String fileMd5;

    /**
     * 父文件夹ID
     */
    private String filePid;

    /**
     * 子文件
     */
    @TableField(exist = false)
    private List<FileInfo> children;

    /**
     * 总大小
     */
    @TableField(exist = false)
    private Long totalSize;

}
