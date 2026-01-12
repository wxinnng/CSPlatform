package com.csplatform.course.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WangXing
 * @Date 2026/1/12 14:29
 * @PackageName:com.csplatform.course.entity.vo
 * @ClassName: UserCardSetVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCardSetVO {

    /**
     * cardSetID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面
     */
    private String coverUrl;

    // 手动添加 getter 和 setter（如果 Lombok 不工作）
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }
}