package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.course.entity.Card;
import com.csplatform.course.mapper.CardMapper;
import com.csplatform.course.service.CardService;
import org.springframework.stereotype.Service;

/**
 * @Author WangXing
 * @Date 2026/1/10 19:08
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: CardService
 * @Version 1.0
 */
@Service
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {
}
