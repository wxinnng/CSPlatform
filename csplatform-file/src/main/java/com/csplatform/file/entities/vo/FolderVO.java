package com.csplatform.file.entities.vo;

import com.csplatform.file.entities.FileInfo;
import lombok.Data;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/5 8:48
 * @PackageName:com.csplatform.file.entities.vo
 * @ClassName: FolderVO
 * @Version 1.0
 */
@Data
public class FolderVO {

    /**
     * 目录信息
     */
    private FileInfo folderInfo;

    /**
     * 孩子信息
     */
    private List<FileInfo> children;

}
