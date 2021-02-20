package com.bjpowernode.p2p.model.loan;

/**
 * ClassName:BidInfoExtLoan
 * Package:com.bjpowernode.p2p.model.loan
 * Description:
 *
 * @date:2020/4/8 11:52
 * @author:动力节点
 */
public class BidInfoExtLoan extends BidInfo {

    private LoanInfo loanInfo;

    public LoanInfo getLoanInfo() {
        return loanInfo;
    }

    public void setLoanInfo(LoanInfo loanInfo) {
        this.loanInfo = loanInfo;
    }
}
