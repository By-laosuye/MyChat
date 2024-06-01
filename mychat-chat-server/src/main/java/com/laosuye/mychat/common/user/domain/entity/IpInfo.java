package com.laosuye.mychat.common.user.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * IP信息
 */
@Data
public class IpInfo implements Serializable {

    //注册时的ip
    private String createIp;

    //注册时的ip详情
    private IpDetail createIpDetail;

    //注册时的ip详情
    private IpDetail updateIpDetail;

    //最新登录时时的ip
    private String updateIp;


    /**
     * 刷新IP地址。
     * 该方法用于更新或设置对象的IP地址。如果传入的IP地址为空，则不进行任何操作。如果对象尚未设置过创建时的IP地址（createIp），则同时将其设置为传入的IP地址。无论之前创建IP是否已设置，都会将更新IP地址（updateIp）设置为传入的IP地址。
     *
     * @param ip 需要刷新的IP地址。如果为空或null，则不进行任何操作。
     */
    public void refreshIp(String ip) {
        // 检查传入的IP地址是否为空
        if (StringUtils.isBlank(ip)) {
            return;
        }
        // 如果创建IP地址未设置，则将其设置为传入的IP地址
        if (StringUtils.isBlank(createIp)) {
            createIp = ip;
        }
        // 更新IP地址为传入的IP地址
        updateIp = ip;
    }


    /**
     * 判断是否需要刷新IP。
     * 该方法会检查当前的IP详情（如果存在）是否与待更新的IP相同，如果相同，则不需要刷新，返回null；否则，返回待更新的IP。
     *
     * @return 如果不需要刷新IP，则返回null；否则，返回待更新的IP地址。
     */
    public String needRefreshIp() {
        // 检查updateIpDetail是否存在，如果存在，进一步检查其IP是否与updateIp相同
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp) // 获取IPDetail中的IP地址
                .filter(ip -> Objects.equals(ip, updateIp)) // 筛选出与updateIp相同的IP
                .isPresent(); // 检查是否存在满足条件的IP
        return notNeedRefresh ? null : updateIp; // 如果不需要刷新，则返回null；否则，返回updateIp
    }


    /**
     * 刷新IP详情信息。
     * <p>
     * 本方法用于更新创建IP和更新IP的详细信息。通过比较传入的IP详细信息对象中的IP地址，
     * 如果与当前的创建IP或更新IP地址相匹配，则更新相应的IP详细信息对象。
     * 这样可以确保我们总是持有最新、最准确的IP详细信息，以便于后续的IP地址管理和查询。
     *
     * @param ipDetail IP详情信息对象，包含待刷新的IP地址及其详细信息。
     */
    public void refreshIpDetail(IpDetail ipDetail) {
        // 检查传入的IP详情是否与创建IP相匹配，如果匹配，则更新创建IP的详情信息
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        // 检查传入的IP详情是否与更新IP相匹配，如果匹配，则更新更新IP的详情信息
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }

}
