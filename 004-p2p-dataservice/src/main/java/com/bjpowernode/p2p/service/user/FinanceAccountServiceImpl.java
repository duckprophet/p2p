package com.bjpowernode.p2p.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName:FinanceAccountServiceImpl
 * Package:com.bjpowernode.p2p.service.user
 * Description:
 *
 * @date:2020/4/8 9:18
 * @author:动力节点
 */
@Component
@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 15000)
public class FinanceAccountServiceImpl implements FinanceAccountService {

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer uid) {
        return financeAccountMapper.selectFinanceAccountByUid(uid);
    }
}
