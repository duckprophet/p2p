package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.BidInfoExtLoan;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.BidUser;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/4/1 14:44
 * @author:动力节点
 */
@Component
@Service(interfaceClass = BidInfoService.class,version = "1.0.0",timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {

    @Autowired
    private BidInfoMapper bidInfoMapper;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;


    @Override
    public Double queryAllBidMoney() {

        //从redis中获取该值
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

        //判断是否有值
        if (!ObjectUtils.allNotNull(allBidMoney)) {

            //设置同步代码块
            synchronized (this) {

                //再次从redis中获取该值
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);

                //再次判断是否有值
                if (!ObjectUtils.allNotNull(allBidMoney)) {

                    //去数据库查询
                    allBidMoney = bidInfoMapper.selectAllBidMoney();

                    //并存放到redis缓存中
                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney,15, TimeUnit.MINUTES);
                }

            }

        }

        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoListByLoanId(Map<String, Object> paramMap) {
        return bidInfoMapper.selectRecentlyBidInfoListByLoanId(paramMap);
    }

    @Override
    public List<BidInfoExtLoan> queryRecentlyBidInfoListByUid(Map<String, Object> paramMap) {
        return bidInfoMapper.selectRecentlyBidInfoListByUid(paramMap);
    }

    @Transactional
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {

        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");


        //更新产品剩余可投金额
        //当出现多线程高并发的时候,该步骤可以会导致超卖的现象
        //超卖:实际销售的数量超过了库存数量(该现象只会在线上的多线程高并发的时候出现)
        //通过数据库乐观锁机制来解决超卖现象

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        paramMap.put("version",loanInfo.getVersion());

        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoney(paramMap);
        if (updateLeftProductMoneyCount <= 0) {
            throw new Exception();
        }

        //更新帐户可用余额
        int updateFinanceAccountCount = financeAccountMapper.updateFinanceAccountByBid(paramMap);
        if (updateFinanceAccountCount <= 0) {
            throw new Exception();
        }

        //新增投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setUid(uid);
        bidInfo.setLoanId(loanId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(1);
        int insertBidInfoCount = bidInfoMapper.insertSelective(bidInfo);
        if (insertBidInfoCount <= 0) {
            throw new Exception();
        }

        //再次查询产品详情
        LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);

        //判断产品是否满标
        if (0 == loanInfoDetail.getLeftProductMoney()) {

            //产品已满标->更新产品的状态及满标时间
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setId(loanId);
            updateLoanInfo.setProductFullTime(new Date());
            updateLoanInfo.setProductStatus(1);//0未满标,1已满标,2满标且生成收益计划
            int i = loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
            if (i <= 0) {
                throw new Exception();
            }
        }

        //用户投资成功后,将投资的信息存放到redis缓存中
        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);

    }

    @Override
    public List<BidUser> queryBidUserTop() {
        List<BidUser> bidUserList = new ArrayList<BidUser>();

        //从redis缓存中获取数据
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        //获取迭代器
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();

        //循环遍历set集合
        while (iterator.hasNext()) {

            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();

            BidUser bidUser = new BidUser();
            bidUser.setPhone(phone);
            bidUser.setScore(score);

            bidUserList.add(bidUser);
        }

        return bidUserList;
    }
}
