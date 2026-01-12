package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.entity.Card;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.enums.UserCardSetStatus;
import com.csplatform.course.mapper.CardMapper;
import com.csplatform.course.mapper.CardSetMapper;
import com.csplatform.course.mapper.UserCardSetMapper;
import com.csplatform.course.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
