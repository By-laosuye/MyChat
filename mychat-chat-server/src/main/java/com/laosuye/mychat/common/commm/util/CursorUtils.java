package com.laosuye.mychat.common.commm.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.laosuye.mychat.common.commm.domain.vo.request.CursorPageBaseReq;
import com.laosuye.mychat.common.commm.domain.vo.response.CursorPageBaseResp;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Description: 游标分页工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-03-28
 */
public class CursorUtils {

    /**
     * 根据Redis键和游标获取分页数据。
     * 该方法通过Redis的有序集合分数排序功能，实现数据的分页获取。支持从特定游标位置开始获取数据，或者从头开始获取。
     * 数据按照分数降序排列。
     *
     * @param cursorPageBaseReq 分页请求对象，包含游标和每页大小信息。
     * @param redisKey          Redis中的键名。
     * @param typeConvert       类型转换函数，用于将Redis中的字符串数据转换为泛型T类型。
     * @param <T>               泛型参数，表示转换后的数据类型。
     * @return 分页响应对象，包含游标、是否为最后一页以及数据列表。
     */
    public static <T> CursorPageBaseResp<Pair<T, Double>> getCursorPageByRedis(CursorPageBaseReq cursorPageBaseReq, String redisKey, Function<String, T> typeConvert) {
        // 根据游标判断是首次加载还是加载后续页码
        Set<ZSetOperations.TypedTuple<String>> typedTuples;
        if (StrUtil.isBlank(cursorPageBaseReq.getCursor())) {
            // 首次加载，无游标，从集合末尾开始向前取指定数量的数据
            typedTuples = RedisUtils.zReverseRangeWithScores(redisKey, cursorPageBaseReq.getPageSize());
        } else {
            // 加载后续页码，使用游标从集合中指定分数范围开始取数据
            typedTuples = RedisUtils.zReverseRangeByScoreWithScores(redisKey, Double.parseDouble(cursorPageBaseReq.getCursor()), cursorPageBaseReq.getPageSize());
        }

        // 将数据转换为泛型T类型和分数的Pair列表，并按照分数降序排序
        List<Pair<T, Double>> result = typedTuples
                .stream()
                .map(t -> Pair.of(typeConvert.apply(t.getValue()), t.getScore()))
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .collect(Collectors.toList());

        // 计算新的游标值，为下一页数据加载做准备
        String cursor = Optional.ofNullable(CollectionUtil.getLast(result))
                .map(Pair::getValue)
                .map(String::valueOf)
                .orElse(null);

        // 判断当前页是否为最后一页
        Boolean isLast = result.size() != cursorPageBaseReq.getPageSize();

        // 构建并返回分页响应对象
        return new CursorPageBaseResp<>(cursor, isLast, result);
    }


    /**
     * 根据MySQL查询结果生成游标分页响应。
     * <p>
     * 该方法通过提供的Lambda表达式和游标信息，查询数据库并返回游标分页响应对象。
     * 游标分页是一种高效的数据分页方式，尤其适用于大数据量的查询。
     *
     * @param mapper 数据访问接口，用于执行数据库查询操作。
     * @param request 分页请求对象，包含游标信息和分页参数。
     * @param initWrapper 查询条件初始化Lambda表达式，用于设置额外的查询条件。
     * @param cursorColumn 游标字段的Lambda表达式，用于指定按照哪个字段进行游标排序和比较。
     * @param <T> 数据实体的泛型类型。
     * @return 游标分页响应对象，包含查询结果、游标和是否为最后一页的信息。
     */
    public static <T> CursorPageBaseResp<T> getCursorPageByMysql(IService<T> mapper, CursorPageBaseReq request, Consumer<LambdaQueryWrapper<T>> initWrapper, SFunction<T, ?> cursorColumn) {
        // 获取游标字段的类型
        Class<?> cursorType = LambdaUtils.getReturnType(cursorColumn);
        // 初始化查询条件Wrapper
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        // 应用额外的查询条件初始化
        initWrapper.accept(wrapper);

        // 如果存在游标，则设置查询条件，查询上一页数据
        if (StrUtil.isNotBlank(request.getCursor())) {
            wrapper.lt(cursorColumn, parseCursor(request.getCursor(), cursorType));
        }
        // 按照游标字段降序排序
        wrapper.orderByDesc(cursorColumn);

        // 执行分页查询
        Page<T> page = mapper.page(request.plusPage(), wrapper);
        // 从查询结果中提取最后一个元素的游标值
        String cursor = Optional.ofNullable(CollectionUtil.getLast(page.getRecords()))
                .map(cursorColumn)
                .map(CursorUtils::toCursor)
                .orElse(null);
        // 判断是否为最后一页，根据查询结果的数量是否等于请求的页面大小
        Boolean isLast = page.getRecords().size() != request.getPageSize();
        // 构建并返回游标分页响应对象
        return new CursorPageBaseResp<>(cursor, isLast, page.getRecords());
    }

    /**
     * 将给定的对象转换为字符串形式的光标。
     * 此方法主要用于处理日期对象和其他类型的对象，将它们转换为字符串以用作光标值。
     * 对于日期对象，转换为自1970年1月1日00:00:00 GMT以来的毫秒数；
     * 对于其他类型的对象，直接使用其toString()方法的返回值。
     *
     * @param o 待转换的对象，可以是Date类型的实例或其他类型的实例。
     * @return 对象转换后的字符串形式。
     */
    private static String toCursor(Object o) {
        // 判断对象是否为Date类型
        if (o instanceof Date) {
            // 对于Date类型，转换为毫秒数字符串
            return String.valueOf(((Date) o).getTime());
        } else {
            // 对于非Date类型，直接转换为字符串
            return o.toString();
        }
    }


    /**
     * 根据游标字符串和游标类类型解析游标。
     * 游标是用来标记数据的特定位置，以便在分页或增量数据处理中继续从上次停止的地方开始。
     * 本函数支持将游标字符串解析为日期类型或字符串类型。
     *
     * @param cursor      游标字符串，可能是日期的长整型表示或其他字符串。
     * @param cursorClass 游标对应的类类型，用于判断游标字符串的解析方式。
     * @return 解析后的游标对象，如果是日期类型，则返回Date对象；否则返回原字符串。
     */
    private static Object parseCursor(String cursor, Class<?> cursorClass) {
        // 判断游标类类型是否为日期类型
        if (Date.class.isAssignableFrom(cursorClass)) {
            // 如果是日期类型，将游标字符串解析为长整型，然后创建一个对应的Date对象返回
            return new Date(Long.parseLong(cursor));
        } else {
            // 如果不是日期类型，直接返回游标字符串
            return cursor;
        }
    }

}
