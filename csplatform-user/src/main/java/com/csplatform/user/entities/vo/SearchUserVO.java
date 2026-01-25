package com.csplatform.user.entities.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author WangXing
 * @Date 2026/1/23 17:45
 * @PackageName:com.csplatform.chat.entities.vo
 * @ClassName: SearchUserVO
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserVO {

    private Long id;

    private String username;

    private String avatarUrl;
}
