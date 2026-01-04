package com.csplatform.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.csplatform.file.entities.FileInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author WangXing
 * @Date 2026/1/3 17:24
 * @PackageName:com.csplatform.file.mapper
 * @ClassName: FileMapper
 * @Version 1.0
 */
@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {
}
