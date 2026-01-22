package com.csplatform.course.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author WangXing
 * @Date 2026/1/3 17:19
 * @PackageName:com.csplatform.file.entities
 * @ClassName: FileInfo
 * @Version 1.0
 */
@Data
@TableName("file_info")
public class FileInfo {
    // 核心文件属性
    @TableId("file_id")
    private String fileId;          // 文件ID [3](@ref)
    private Long userId;         // 所属用户ID
    private String fileMd5;         // 文件的MD5值 [2](@ref)
    private String filePid;        // 父文件夹ID [3](@ref)
    private Long fileSize;         // 文件大小(字节) [4](@ref)
    private String fileName;        // 文件名 [3](@ref)
    private String filePath;        // 文件在服务器中的路径 [4](@ref)
    private LocalDateTime createTime;       // 文件创建时间
    private Integer fileCategory;   // 文件类型分类 [7](@ref)
    private Integer state;          // 传输状态
    private Integer delFlag;        // 删除状态(0-正常 1-已删除)

}