package com.laosuye.mychat.common.user.service.impl;

import com.laosuye.mychat.common.commm.domain.enums.YesOrNoEnum;
import com.laosuye.mychat.common.user.dao.UserBackpackDao;
import com.laosuye.mychat.common.user.domain.entity.UserBackpack;
import com.laosuye.mychat.common.user.domain.enums.IdempotentEnum;
import com.laosuye.mychat.common.user.service.IUserBackpackService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserBackpackServiceImpl implements IUserBackpackService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private UserBackpackDao userBackpackDao;


    @Override
    public void acquireItem(Long uid, Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        String idempotent = getIdempotent(itemId, idempotentEnum, businessId);
        RLock lock = redissonClient.getLock("acquireItem" + idempotent);
        try {
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
        } finally {
            lock.unlock();
        }

    }

    private String getIdempotent(Long itemId, IdempotentEnum idempotentEnum, String businessId) {
        return String.format("%d_%d_%s", itemId, idempotentEnum.getType(), businessId);
    }
}
