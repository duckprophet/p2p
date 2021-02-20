package com.bjpowernode.p2p.service.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LoanInfoServiceImpl
 * Package:com.bjpowernode.p2p.service
 * Description:
 *
 * @date:2020/4/1 11:09
 * @author:动力节点
 */
@Slf4j
@Component
@Service(interfaceClass = LoanInfoService.class,version = "1.0.0",timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Override
    public Double queryHistoryAverageRate() {

        //设置redisTemplate对象key的序列化方式,目的就是:提高可读性
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //首先去redis缓存中查询,如果有:直接使用,如果没有:去数据库查询,并存放到redis缓存中
        /*Double historyAverageRate = (Double) redisTemplate.opsForValue().get("historyAverageRate");

        //判断是否有值
        if (!ObjectUtils.allNotNull(historyAverageRate)) {
            System.out.println("从数据库中查询.......");

            //去数据库查询
            historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

            //并存放到redis缓存中
            redisTemplate.opsForValue().set("historyAverageRate", historyAverageRate, 15, TimeUnit.MINUTES);

        } else {
            System.out.println("从Redis缓存中查询.......");
        }*/

        //以上代码出现了一个问题是:缓存穿透,该现在只有在多线程高并发的时候才可出现
        //解决"缓存穿透"现象:通过双重检测+同步代码块的方式来解析

        //首先去redis缓存中查询
        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

        //第一次对该值进行判断
        if (!ObjectUtils.allNotNull(historyAverageRate)) {

            //设置同步代码块
            synchronized (this) {

                //再次从redis中获取该值
                historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);

                //第二次对该值进行判断
                if (!ObjectUtils.allNotNull(historyAverageRate)) {

                    log.info("从数据库中获取数据.......");

                    //去数据库查询
                    historyAverageRate = loanInfoMapper.selectHistoryAverageRate();

                    //并存放到redis缓存中
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE, historyAverageRate, 15, TimeUnit.MINUTES);
                } else {
                    log.info("从Redis中获取数据.......");
                }
            }
        } else {
            log.info("从Redis中获取数据.......");
        }


        return historyAverageRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();

        //获取总记录数
        Long total = loanInfoMapper.selectTotal(paramMap);
        paginationVO.setTotal(total);

        //在设计方法的时候,我们要考虑到是DAO接口还是业务接口
        //业务层的方法粒度要比dao中方法的粒度要粗
        //小明有一个功能:会把大象放进冰箱 -> 相当于server层中的方法
            //完成该功能需要3个步骤: -> 3个步骤相当于dao中的方法
                //1.开冰箱门
                //2.放大象
                //3.关冰箱门

        //获取每页展示的数据
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);
        paginationVO.setDataList(loanInfoList);

        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }
}
