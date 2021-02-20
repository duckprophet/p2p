package com.bjpowernode.p2p.service.user;

import com.bjpowernode.p2p.model.user.User;

/**
 * ClassName:UserService
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/4/1 14:35
 * @author:动力节点
 */
public interface UserService {

    /**
     * 获取平台注册总人数
     * @return
     */
    Long queryAllUserCount();

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 用户注册
     * @param phone
     * @param loginPassword
     * @param accountOpenAmount
     * @return
     */
    User register(String phone, String loginPassword, Double accountOpenAmount) throws Exception;

    /**
     * 根据用户标识更新用户信息
     * @param user
     * @return
     */
    int modifyUserById(User user);

    /**
     * 用户登录
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword) throws Exception;
}
