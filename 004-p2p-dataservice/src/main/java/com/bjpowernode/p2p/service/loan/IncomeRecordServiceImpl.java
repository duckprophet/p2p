package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.IncomeLoanVO;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/8 14:14
 * @author:动力节点
 */
@Component
@Service(interfaceClass = IncomeRecordService.class,version = "1.0.0",timeout = 15000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private IncomeRecordMapper incomeRecordMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public List<IncomeLoanVO> queryRecentlyIncomeRecordListByUid(Map<String, Object> paramMap) {
        return incomeRecordMapper.selectRecentlyIncomeRecordListByUid(paramMap);
    }

    @Transactional
    @Override
    public void generateIncomePlan() {

        //查询已满标的产品  -> 返回List<已满标产品>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);

        //循环遍历,获取到每一个已满标产品
        for (LoanInfo loanInfo : loanInfoList) {

            //获取当前已满标产品的所有投资记录 -> 返回List<投资记录>
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllBidInfoListByLoanId(loanInfo.getId());

            //循环遍历,获取互每一条投资记录
            for (BidInfo bidInfo : bidInfoList) {

                //将当前投资记录生成对应的收益计划
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);

                //收益时间(Date) = 产品满标时间(Date) + 产品周期(int[天|月])
                Date incomeDate = null;
                Double incomeMoney = null;

                //判断产品的类型
                if (Constants.PRODUCT_TYPE_X == loanInfo.getProductType()) {
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                } else {
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * 30;
                }

                incomeRecord.setIncomeDate(incomeDate);
                incomeMoney = Math.round(Math.pow(10,2)*incomeMoney)/Math.pow(10,2);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);

            }

            //更新当前产品的状态为2满标且生成收益计划
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanInfo.getId());
            updateLoanInfo.setProductStatus(2);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }
    }

    @Transactional
    @Override
    public void generateIncomeBack() {
        //查询收益状态为0且收益时间与当前时间一致的收益记录 -> 返回List
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurDate(0);

        Map<String,Object> paramMap = new HashMap<String, Object>();

        //循环遍历,获取到每一条收益记录
        for (IncomeRecord incomeRecord : incomeRecordList) {

            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            //将当前收益记录的本金与收益返还给对应用户的帐户
            financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            //更新当前收益记录的状态为1已返还
            IncomeRecord updateIncome = new IncomeRecord();
            updateIncome.setId(incomeRecord.getId());
            updateIncome.setIncomeStatus(1);
            incomeRecordMapper.updateByPrimaryKeySelective(updateIncome);
        }


    }
}
