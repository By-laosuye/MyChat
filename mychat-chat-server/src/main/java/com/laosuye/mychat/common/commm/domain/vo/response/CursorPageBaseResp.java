package com.laosuye.mychat.common.commm.domain.vo.response;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("游标翻页返回")
@AllArgsConstructor
@NoArgsConstructor
public class CursorPageBaseResp<T> {

    @ApiModelProperty("游标（下次翻页带上这参数）")
    private String cursor;

    @ApiModelProperty("是否最后一页")
    private Boolean isLast = Boolean.FALSE;

    @ApiModelProperty("数据列表")
    private List<T> list;

    /**
     * 初始化CursorPageBaseResp对象。
     * 该方法用于基于传入的CursorPageBaseResp对象和数据列表创建并返回一个新的CursorPageBaseResp对象。
     * 新对象的属性值将基于旧对象的相应属性进行设置，确保分页逻辑的连续性。
     *
     * @param cursorPage 旧的CursorPageBaseResp对象，用于获取当前页是否为最后一页的信息、游标值等。
     * @param list 数据列表，用于设置新CursorPageBaseResp对象的数据部分。
     * @param <T> 泛型参数，表示数据类型的通配符。
     * @return 新的CursorPageBaseResp对象，包含了从旧对象继承的分页信息和新的数据列表。
     */
    public static <T> CursorPageBaseResp<T> init(CursorPageBaseResp cursorPage, List<T> list) {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<T>();
        cursorPageBaseResp.setIsLast(cursorPage.getIsLast());
        cursorPageBaseResp.setList(list);
        cursorPageBaseResp.setCursor(cursorPage.getCursor());
        return cursorPageBaseResp;
    }


    /**
     * 检查列表是否为空。
     *
     * 该方法通过调用CollectionUtil的isEmpty方法来判断列表list是否为空。isEmpty方法会检查列表是否为null或空，
     * 这样可以避免调用方需要手动检查null和空列表的情况，提高了代码的健壮性和易用性。
     *
     * @return Boolean 返回true如果列表为空（即null或无元素），否则返回false。
     */
    @JsonIgnore
    public Boolean isEmpty() {
        return CollectionUtil.isEmpty(list);
    }


    /**
     * 创建一个空的CursorPageBaseResp对象，用于表示没有更多数据的情况。
     * 这个方法适用于当数据查询结果为空，或者需要告知调用者已经到达数据的末尾时。
     *
     * @param <T> 泛型参数，表示返回的对象类型。
     * @return 返回一个空的CursorPageBaseResp对象，其中isLast字段被设置为true，表示这是最后一个页面；
     *         list字段被设置为一个新的空ArrayList，表示当前页面没有数据。
     */
    public static <T> CursorPageBaseResp<T> empty() {
        CursorPageBaseResp<T> cursorPageBaseResp = new CursorPageBaseResp<T>();
        cursorPageBaseResp.setIsLast(true);
        cursorPageBaseResp.setList(new ArrayList<T>());
        return cursorPageBaseResp;
    }


}