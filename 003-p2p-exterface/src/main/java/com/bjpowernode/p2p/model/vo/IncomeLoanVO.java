package com.bjpowernode.p2p.model.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * ClassName:IncomeLoanVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/8 14:12
 * @author:动力节点
 */
public class IncomeLoanVO implements Serializable {

    private String productName;

    private Date incomeDate;

    private Double incomeMoney;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public Double getIncomeMoney() {
        return incomeMoney;
    }

    public void setIncomeMoney(Double incomeMoney) {
        this.incomeMoney = incomeMoney;
    }
}
