package com.bjpowernode.p2p.service.loan;

import com.bjpowernode.p2p.model.vo.IncomeLoanVO;

import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordService
 * Package:com.bjpowernode.p2p.service.loan
 * Description:
 *
 * @date:2020/4/8 14:14
 * @author:动力节点
 */
public interface IncomeRecordService {

    /**
     * 根据用户标识获取最近收益记录(包含产品信息)
     * @param paramMap
     * @return
     */
    List<IncomeLoanVO> queryRecentlyIncomeRecordListByUid(Map<String, Object> paramMap);

    /**
     * 生成收益计划
     */
    void generateIncomePlan();

    /**
     * 收益的返还
     */
    void generateIncomeBack();
}
