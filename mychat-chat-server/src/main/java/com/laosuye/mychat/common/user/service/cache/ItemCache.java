package com.laosuye.mychat.common.user.service.cache;

import com.laosuye.mychat.common.user.dao.ItemConfigDao;
import com.laosuye.mychat.common.user.domain.entity.ItemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemCache {

    @Autowired
    private ItemConfigDao itemConfigDao;

    /**
     * 根据徽章类型获取本地缓存的徽章列表，如果本地缓存没有则去数据库查询，然后放入本地缓存中
     * @param itemType 徽章类型
     * @return 徽章列表
     */
    @Cacheable(cacheNames = "item", key = "'itemsByType:'+#itemType")
    public List<ItemConfig> getByType(Integer itemType) {
        return itemConfigDao.getByType(itemType);
    }

    /**
     * 清空缓存
     * @param itemType 徽章列表
     */
    @CacheEvict(cacheNames = "item", key = "'itemsByType:'+#itemType")
    public void evictByType(Integer itemType) {
    }
}
