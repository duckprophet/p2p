package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/8 14:07
 * @author:动力节点
 */
@Component
@Service(interfaceClass = RechargeRecordService.class,version = "1.0.0",timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Autowired
    private RechargeRecordMapper rechargeRecordMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public List<RechargeRecord> queryRecentlyRechargeRecordListByUid(Map<String, Object> paramMap) {
        return rechargeRecordMapper.selectRecentlyRechargeRecordListByUid(paramMap);
    }

    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    @Override
    public int modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        return rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Transactional
    @Override
    public void recharge(Map<String, Object> paramMap) throws Exception {

        //更新帐户可用余额
        int updateFinanceAccountCount = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
        if (updateFinanceAccountCount <= 0) {
            throw new Exception();
        }

        //更新充值记录的状态为1
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
        rechargeRecord.setRechargeStatus("1");
        int updateRechargeCount = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
        if (updateRechargeCount <= 0) {
            throw new Exception();
        }


    }

    @Override
    public void dealRechargeRecord() throws Exception {
        //查询充值记录状态为0 -> 返回List<充值记录>
        List<RechargeRecord> rechargeRecordList = rechargeRecordMapper.selectRechargeRecordByRechargeStatus("0");

        Map<String,Object> paramMap = new HashMap<String, Object>();

        //循环遍历,获取到每一条充值记录
        for (RechargeRecord rechargeRecord : rechargeRecordList) {
            paramMap.put("out_trade_no",rechargeRecord.getRechargeNo());

            //调用查询接口,返回订单详情
            String jsonString = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);

            //将json格式的字符串转换为JSON对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            //获取alipay_trade_query_response对应的JSON对象
            JSONObject tradeQueryResponse = jsonObject.getJSONObject("alipay_trade_query_response");

            //获取通信标识code
            String code = tradeQueryResponse.getString("code");

            if (StringUtils.equals("10000", code)) {

                //获取trade_status
                String tradeStatus = tradeQueryResponse.getString("trade_status");

                /*交易状态：
                WAIT_BUYER_PAY（交易创建，等待买家付款）
                TRADE_CLOSED（未付款交易超时关闭，或支付完成后全额退款）
                TRADE_SUCCESS（交易支付成功）
                TRADE_FINISHED（交易结束，不可退款）*/

                if (StringUtils.equals("TRADE_CLOSED", tradeStatus)) {
                    //更新充值记录状态为2
                    RechargeRecord updateRecharge = new RechargeRecord();
                    updateRecharge.setRechargeNo(rechargeRecord.getRechargeNo());
                    updateRecharge.setRechargeStatus("2");
                    rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRecharge);
                }

                if (StringUtils.equals("TRADE_SUCCESS", tradeStatus)) {

                    //再次查询订单详情
                    RechargeRecord rechargeRecordDetail = rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeRecord.getRechargeNo());

                    if (StringUtils.equals(rechargeRecordDetail.getRechargeStatus(), "0")) {

                        //给用户充值
                        paramMap.put("uid",rechargeRecord.getUid());
                        paramMap.put("rechargeMoney",rechargeRecord.getRechargeMoney());
                        financeAccountMapper.updateFinanceAccountByRecharge(paramMap);

                        RechargeRecord updateRechargeRecord = new RechargeRecord();
                        updateRechargeRecord.setRechargeNo(rechargeRecord.getRechargeNo());
                        updateRechargeRecord.setRechargeStatus("1");
                        rechargeRecordMapper.updateRechargeRecordByRechargeNo(updateRechargeRecord);
                    }

                }


            }

        }

    }

    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String rechargeNo) {
        return rechargeRecordMapper.selectRechargeRecordByRechargeNo(rechargeNo);
    }
}
