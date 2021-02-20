package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description:展示首页控制层
 *
 * @date:2020/4/1 10:47
 * @author:动力节点
 */
@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @RequestMapping(value = "/index")
    public String index(Model model) {
        //方法名称       增       删       改            查
        //数据持久层     insert   delete   update        select
        //业务层方法     add      remove   modify/edit   query/find

        //创建一个固定的线程池
        /*ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 5000; i++) {
            //开启一个线程
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
                    model.addAttribute("historyAverageRate",historyAverageRate);
                }
            });
        }

       executorService.shutdownNow();*/

        //获取平台历史平均年化收益率
        Double historyAverageRate = loanInfoService.queryHistoryAverageRate();
        //常量的类名通过由constant单词构成
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);

        //获取平台注册总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT,allUserCount);

        //获取平台累计投资金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY,allBidMoney);

        //将以下查询看作是一个分页(实际上不是分页功能),根据产品类型获取产品信息列表
        //我们使用MySQL中的limit函数: limit 起始下标,截取长度   limit (页码-1)*截取长度,截取长度
        //根据产品类型获取产品信息列表(产品类型,页码,每页显示条数) -> 返回List<产品>
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("currentPage",0);

        //获取新手宝产品:产品类型0,显示第1页,每页显示1条
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);

        //获取优选产品:产品类型1,显示第1页,每页显示4条
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);

        //获取散标产品:产品类型2,显示第1页,每页显示8条
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);


        return "index";
    }
}
