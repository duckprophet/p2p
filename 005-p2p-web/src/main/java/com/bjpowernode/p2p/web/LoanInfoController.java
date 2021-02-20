package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.LoanInfoService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/4/2 10:25
 * @author:动力节点
 */
@Controller
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class,version = "1.0.0",check = false)
    private LoanInfoService loanInfoService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false)
    private FinanceAccountService financeAccountService;

    @RequestMapping(value = "/loan/loan")
    public String loan(HttpServletRequest request, Model model,
                       @RequestParam (value = "ptype",required = false) Integer ptype,
                       @RequestParam (value = "currentPage",defaultValue = "1") Integer currentPage,
                       @RequestParam (value = "pageSize",defaultValue = "9") Integer pageSize) {

        //分页查询页面要展示的数据:每页显示的数据,总条数,总页数,当前页码

        //根据产品类型分页查询产品信息列表(产品类型,页码,每页显示数据) -> 返回数据:每页显示数据,总条数
        //将分页查询返回的数据封装成一个分页模型对象PaginationVO:每页显示数据,总条数
        //准备分页查询的数据
        Map<String,Object> paramMap = new HashMap<String, Object>();

        //判断是否有值
        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType",ptype);
        }
        paramMap.put("currentPage",(currentPage-1)*pageSize);
        paramMap.put("pageSize",pageSize);

        //调用业务方法
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoListByPage(paramMap);

        //计算总页数
        int totalPage = paginationVO.getTotal().intValue() / pageSize;
        int mod = paginationVO.getTotal().intValue() % pageSize;
        if (mod > 0) {
            totalPage = totalPage + 1;
        }

        //将以数据存放到model中
        model.addAttribute("loanInfoList",paginationVO.getDataList());
        model.addAttribute("totalPage",totalPage);
        model.addAttribute("totalRows",paginationVO.getTotal());
        model.addAttribute("currentPage",currentPage);
        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype",ptype);
        }

        //用户投资排行榜
        List<BidUser> bidUserList = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserList",bidUserList);

        return "loan";
    }

    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(HttpServletRequest request,Model model,
                           @RequestParam (value = "id",required = true) Integer id) {

        //根据产品标识获取产品详情
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo",loanInfo);

        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("loanId",id);
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",10);

        //根据产品标识获取最近前10笔的投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryRecentlyBidInfoListByLoanId(paramMap);
        model.addAttribute("bidInfoList",bidInfoList);

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //判断用户是否登录
        if (ObjectUtils.allNotNull(sessionUser)) {

            //根据用户标识获取帐户可用余额
            FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
            model.addAttribute("financeAccount",financeAccount);
        }


        return "loanInfo";
    }
}

