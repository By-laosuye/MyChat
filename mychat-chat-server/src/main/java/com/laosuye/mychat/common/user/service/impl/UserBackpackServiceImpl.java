package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.commm.annotation.RedissonLock;
import com.laosuye.mychat.common.commm.domain.enums.YesOrNoEnum;
import com.laosuye.mychat.common.user.dao.UserBackpackDao;
import com.laosuye.mychat.common.user.domain.entity.UserBackpack;
import com.laosuye.mychat.common.user.domain.enums.IdempotentEnum;
import com.laosuye.mychat.common.user.service.IUserBackpackService;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserBackpackDao userBackpackDao;


    /**
     * 给用户发放一个物品
     *
     * @param uid            用户id
     * @param itemId         物品id
     * @param idempotentEnum 幂等类型
     * @param businessId     幂等唯一标识
     */
    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        //获取幂等标识
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        ((UserBackpackServiceImpl)AopContext.currentProxy()).doAcquireItem(uid, itemId, idempotent);
    }

    @RedissonLock(key = "#idempotent", waitTime = 5000)
    public void doAcquireItem(Long uid, Long itemId, String idempotent) {
        //判断是否存在
        UserBackpack userBackpack = userBackpackDao.getByIdempotent(idempotent);
        if (Objects.nonNull(userBackpack)) {
            return;
        }
        //发放物品
        UserBackpack insert = UserBackpack.builder()
                .uid(uid)
                .itemId(itemId)
                .status(YesOrNoEnum.YES.getStatus())
                .idempotent(idempotent)
                .build();
        userBackpackDao.save(insert);
    }

    /**
     * 获取幂等标识
     * @param itemId 物品id
     * @param idempotentEnum 幂等类型
     * @param businessId 幂等唯一标识
     * @return
     */
    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
