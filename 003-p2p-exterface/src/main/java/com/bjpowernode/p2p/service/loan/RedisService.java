package com.bjpowernode.p2p.service.loan;

/**
 * ClassName:RedisService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/7 14:37
 * @author:动力节点
 */
public interface RedisService {

    /**
     * 将值存放到redis缓存中
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * 获取指定的值
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 获取redis中的唯一数字
     * @return
     */
    Long getOnlyNumber();
}
