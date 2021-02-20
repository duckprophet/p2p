package com.bjpowernode.p2p.model.loan;

import com.bjpowernode.p2p.model.user.User;

import java.io.Serializable;

/**
 * ClassName:BidInfoExtUser
 * Package:com.bjpowernode.p2p.model.loan
 * Description:
 *
 * @date:2020/4/2 14:36
 * @author:动力节点
 */
public class BidInfoExtUser extends BidInfo implements Serializable {

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
