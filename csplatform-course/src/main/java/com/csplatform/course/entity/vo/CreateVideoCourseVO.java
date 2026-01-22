package com.csplatform.course.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/22 12:32
 * @PackageName:com.csplatform.course.entity.vo
 * @ClassName: CreateVideoCourseVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateVideoCourseVO {

    private Long userId;

    private String title;

    private String coverUrl;

    private String description;

    private String category;


    private List<episode> episodes;

    @Data
    public static class episode{

        private String id;

        private String name;

        private Integer order;

        private Integer duration;
    }

}
