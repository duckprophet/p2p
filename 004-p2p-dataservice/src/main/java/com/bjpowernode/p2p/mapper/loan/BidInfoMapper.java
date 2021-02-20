package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.BidInfoExtLoan;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取平台累计投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品标识获取最近的投资记录(包含用户信息)
     * @param paramMap
     * @return
     */
    List<BidInfo> selectRecentlyBidInfoListByLoanId(Map<String, Object> paramMap);

    /**
     * 根据用户标识获取最近的投资记录(包含产品信息)
     * @param paramMap
     * @return
     */
    List<BidInfoExtLoan> selectRecentlyBidInfoListByUid(Map<String, Object> paramMap);

    /**
     * 根据产品标识获取所有投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectAllBidInfoListByLoanId(Integer loanId);
}