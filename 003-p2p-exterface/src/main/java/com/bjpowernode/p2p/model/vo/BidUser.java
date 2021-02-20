package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * ClassName:BidUser
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/10 11:52
 * @author:动力节点
 */
@Data
public class BidUser implements Serializable {

    /**
     * 用户手机号码
     */
    private String phone;

    /**
     * 分数:累计投资金额
     */
    private Double score;
}
