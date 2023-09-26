package com.laosuye.mychat.common.user.dao;

import com.laosuye.mychat.common.user.domain.entity.ItemConfig;
import com.laosuye.mychat.common.user.mapper.ItemConfigMapper;
import com.laosuye.mychat.common.user.service.IItemConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 功能物品配置表 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-09-26
 */
@Service
public class ItemConfigDao extends ServiceImpl<ItemConfigMapper, ItemConfig> {

}
