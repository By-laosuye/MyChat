package com.laosuye.mychat.common.user.dao;

import com.laosuye.mychat.common.user.domain.entity.Black;
import com.laosuye.mychat.common.user.mapper.BlackMapper;
import com.laosuye.mychat.common.user.service.IBlackService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author <a href="https://gitee.com/laosuye">laosuye</a>
 * @since 2023-10-02
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> implements IBlackService {

}
