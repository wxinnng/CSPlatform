package com.csplatform.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.csplatform.common.exception.BusinessException;
import com.csplatform.course.entity.CardSet;
import com.csplatform.course.entity.UserCardSet;
import com.csplatform.course.entity.vo.UserCardSetVO;
import com.csplatform.course.enums.UserCardSetStatus;
import com.csplatform.course.mapper.CardSetMapper;
import com.csplatform.course.mapper.UserCardSetMapper;
import com.csplatform.course.service.UserCardSetService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author WangXing
 * @Date 2026/1/12 8:40
 * @PackageName:com.csplatform.course.service.impl
 * @ClassName: UserCardSetServiceImpl
 * @Version 1.0
 */
@Service
public class UserCardSetServiceImpl extends ServiceImpl<UserCardSetMapper, UserCardSet> implements UserCardSetService {


    @Autowired
    private UserCardSetMapper userCardSetMapper;

    @Autowired
    private CardSetMapper cardSetMapper;


    @Override
    @Transactional
    public void deleteLearningCardById(Long id) {
        //1.先查询
        UserCardSet userCardSet = userCardSetMapper.selectById(id);
        if(userCardSet == null)
            throw new BusinessException("参数错误！");

        //2.修改状态
        int result1 = userCardSetMapper.removeCard(id);
        if(result1 < 1)
            throw new BusinessException("操作失败！");

        //3.修改cardSet表,学习数量减一
        int result2 = cardSetMapper.decreaseOne(userCardSet.getCardSetId());
        if(result2 < 1)
            throw new BusinessException("操作失败！");

    }

    @Override
    public List<UserCardSetVO> getAllLearningCards(Long userId) {
        return userCardSetMapper.selectLinkedCardSetsByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void studyCardSet(Long userId, Long cardSetId) {

        // 1.查询参数是否正确
        CardSet cardSet = cardSetMapper.selectById(cardSetId);
        if (cardSet == null) {
            throw new BusinessException("参数异常！");
        }

        // 2.看是否已插入
        LambdaQueryWrapper<UserCardSet> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCardSet::getUserId, userId)
                .eq(UserCardSet::getCardSetId, cardSetId)
                .ne(UserCardSet::getStatus,UserCardSetStatus.END.getCode())
        ;
        List<UserCardSet> userCardSets = userCardSetMapper.selectList(queryWrapper);
        if (!userCardSets.isEmpty()) {
            throw new BusinessException("已经加入课程学习！");
        }

        // 3.封装对象，插入到数据库
        UserCardSet userCardSet = new UserCardSet()
                .setCardSetId(cardSetId)
                .setUserId(userId)
                .setStatus(UserCardSetStatus.NOT_STARTED.getCode())
                .setJoinTime(LocalDateTime.now());

        int result = userCardSetMapper.insert(userCardSet);
        if (result < 1) {
            throw new BusinessException("操作失败！");
        }

        // 4.更新cardSet信息 - 修改这里
        cardSet.setLearningNum(cardSet.getLearningNum() + 1);
        int update = cardSetMapper.updateById(cardSet);  // 使用 updateById
        if (update < 1) {
            throw new BusinessException("操作失败！");
        }
    }
}
