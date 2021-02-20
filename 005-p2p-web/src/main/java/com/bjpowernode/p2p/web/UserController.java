package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.common.constant.Constants;
import com.bjpowernode.p2p.common.util.HttpClientUtils;
import com.bjpowernode.p2p.common.util.Result;
import com.bjpowernode.p2p.model.loan.BidInfoExtLoan;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.IncomeLoanVO;
import com.bjpowernode.p2p.service.loan.BidInfoService;
import com.bjpowernode.p2p.service.loan.IncomeRecordService;
import com.bjpowernode.p2p.service.loan.RechargeRecordService;
import com.bjpowernode.p2p.service.loan.RedisService;
import com.bjpowernode.p2p.service.user.FinanceAccountService;
import com.bjpowernode.p2p.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description:
 *
 * @date:2020/4/3 9:14
 * @author:动力节点
 */
@Controller
public class UserController {


    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false)
    private RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class,version = "1.0.0",check = false)
    private FinanceAccountService financeAccountService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",check = false)
    private RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = IncomeRecordService.class,version = "1.0.0",check = false)
    private IncomeRecordService incomeRecordService;

    @Value("${account.open.amount}")
    private Double accountOpenAmount;


    @RequestMapping(value = "/loan/page/register")
    public String pageRegister(HttpServletRequest request, Model model) {
        return "register";
    }

    @RequestMapping(value = "/loan/page/realName")
    public String pageRealName() {
        return "realName";
    }


    /**
     * 接口名称:验证手机号码是否重复
     * 接口地址:http://localhost:8080/p2p/loan/checkPhone
     * 请求方式:HTTP GET POST
     * 请求示例:http://localhost:8080/p2p/loan/checkPhone?phone=13900000000
     * 请求参数:phone String 必填
     * 响应参数:code int 业务处理结果
     *         message string 处理描述
     *         success boolean  true|false
     * 返回示例值:{"code":1,"success":true}
     *
     * @param phone
     * @return
     */
    @RequestMapping(value = "/loan/checkPhone")
    public @ResponseBody Object checkPhone(@RequestParam (value = "phone",required = true) String phone) {
        Map<String,Object> retMap = new HashMap<String, Object>();

        //判断手机号码是否重复(手机号码)  -> 返回int|boolean|string
        //根据手机号码查询用户信息(手机号码) -> 返回:user
        User user = userService.queryUserByPhone(phone);

        //判断手机号码是否重复
        if (ObjectUtils.allNotNull(user)) {

            //该手机号码已被注册,请更换手机号码
            /*retMap.put("code",-1);
            retMap.put("message","该手机号码已被注册,请更换手机号码");
            retMap.put("success",false);
            return retMap;*/


            return Result.error("该手机号已被注册,请更换手机号码");
        }

        /*retMap.put("code",1);
        retMap.put("success",true);
        return retMap;*/

        return Result.success();
    }


    @PostMapping(value = "/loan/register")
    public @ResponseBody Result register(HttpServletRequest request,
                                         @RequestParam (value = "phone",required = true) String phone,
                                         @RequestParam (value = "loginPassword",required = true) String loginPassword,
                                         @RequestParam (value = "messageCode",required = true) String messageCode) {

        try {

            //从redis中获取短信验证码
            String redisMessageCode = redisService.get(phone);

            //判断用户输入的是否正确
            if (!StringUtils.equals(messageCode, redisMessageCode)) {
                return Result.error("请输入正确的短信验证码");
            }


            //用户注册[1.新增用户 2.开立帐户](手机号码,密码,开户金额)
            User user = userService.register(phone,loginPassword,accountOpenAmount);

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,user);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败,请稍后重试");
        }

        return Result.success();
    }


    @RequestMapping(value = "/loan/messageCode")
    public @ResponseBody Result messageCode(HttpServletRequest request,
                                            @RequestParam (value = "phone",required = true) String phone) {

        String messageCode = "";

        try {

            //请求参数|请求报文
            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("appkey","");
            paramMap.put("mobile",phone);
            messageCode = this.getRandomCode(4);
            String content = "【凯信通】您的验证码是：" + messageCode;
            paramMap.put("content",content);

            //发送短信,调用京东万象平台-106短信接口
            //响应参数|响应报文
//            String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);

            String jsonString = "{\n" +
                    "                \"code\": \"10000\",\n" +
                    "                    \"charge\": false,\n" +
                    "                    \"remain\": 0,\n" +
                    "                    \"msg\": \"查询成功\",\n" +
                    "                    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                    "            }";

            //模拟报文
            /*{
                "code": "10000",
                    "charge": false,
                    "remain": 0,
                    "msg": "查询成功",
                    "result": "<?xml version=\"1.0\" encoding=\"utf-8\" ?><returnsms>\n <returnstatus>Success</returnstatus>\n <message>ok</message>\n <remainpoint>-1111611</remainpoint>\n <taskID>101609164</taskID>\n <successCounts>1</successCounts></returnsms>"
            }*/

            //通过fastjson来解析JSON格式的字符串
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            //获取通信标识code
            String code = jsonObject.getString("code");

            //判断通信是否成功
            if (!StringUtils.equals("10000", code)) {
                return Result.error("通信异常");
            }

            //获取result对应的xml格式的字符串
            String resultXmlString = jsonObject.getString("result");

            //通过dom4j+xpath来解析xml格式的字符串
            //将xml格式的字符串转换为document对象
            Document document = DocumentHelper.parseText(resultXmlString);

            //获取returnstatus节点的路径表达式: //returnstatus  或者  /returnsms/returnstatus
            Node returnstatusNode = document.selectSingleNode("//returnstatus");

            //获取节点的文本内容
            String text = returnstatusNode.getText();

            //判断是否成功
            if (!StringUtils.equals("Success", text)) {
                return Result.error("短信平台异常");
            }

            //将生成的短信验证码存放到redis缓存中
            redisService.put(phone,messageCode);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("短信平台异常");
        }
        return Result.success(messageCode);
    }

    private String getRandomCode(int count) {

        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < count; i++) {
            int code = (int) Math.round(Math.random()*9);
            stringBuffer.append(code);
        }

        return stringBuffer.toString();
    }


    @RequestMapping(value = "/loan/myFinanceAccount")
    public @ResponseBody FinanceAccount myFinanceAccount(HttpServletRequest request) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());

        return financeAccount;
    }

    @PostMapping(value = "/loan/realName")
    public @ResponseBody Result realName(HttpServletRequest request,
                                         @RequestParam (value = "phone",required = true) String phone,
                                         @RequestParam (value = "realName",required = true) String realName,
                                         @RequestParam (value = "idCard",required = true) String idCard,
                                         @RequestParam (value = "messageCode",required = true) String messageCode) {
        try {

            //从redis中获取短信验证码
            String redisMessageCode = redisService.get(phone);

            //判断用户输入的是否正确
            if (!StringUtils.equals(messageCode, redisMessageCode)) {
                return Result.error("请输入正确的短信验证码");
            }


            Map<String,Object> paramMap = new HashMap<String, Object>();
            paramMap.put("appkey","8caf293a0ef6571ffef316d6d3c71b3e");
            paramMap.put("cardNo",idCard);
            paramMap.put("realName",realName);

            //实名认证,调用京东万象平台的实名认证
            String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
            System.out.println("实名认证响应参数-------> " + jsonString);
            /*String jsonString = "{\n" +
                    "                \"code\": \"10000\",\n" +
                    "                \"charge\": false,\n" +
                    "                \"remain\": 1305,\n" +
                    "                \"msg\": \"查询成功\",\n" +
                    "                \"result\": {\n" +
                    "                    \"error_code\": 0,\n" +
                    "                    \"reason\": \"成功\",\n" +
                    "                    \"result\": {\n" +
                    "                        \"realname\": \"乐天磊\",\n" +
                    "                        \"idcard\": \"350721197702134399\",\n" +
                    "                        \"isok\": true\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            }";*/

            //解析json格式的字符串
            //将json格式的字符串转换为JSON对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);


            /*
            * {
                "code": "10000",
                "charge": false,
                "remain": 1305,
                "msg": "查询成功",
                "result": {
                    "error_code": 0,
                    "reason": "成功",
                    "result": {
                        "realname": "乐天磊",
                        "idcard": "350721197702134399",
                        "isok": true
                    }
                }
            }
            *
            * */

            //获取通信标识code
            String code = jsonObject.getString("code");

            //判断是否通信成功
            if (!StringUtils.equals("10000", code)) {
                return Result.error("通信异常");
            }

            //获取是否匹配的标识isok
            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");

            //判断是否匹配
            if (!isok) {
                return Result.error("真实姓名与身份证不一致");
            }

            //从session中获取用户的信息
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            //将用户的真实姓名和身份证号码更新到用户信息
            User updateUser = new User();
            updateUser.setId(sessionUser.getId());
            updateUser.setName(realName);
            updateUser.setIdCard(idCard);

            int modifyUserCount = userService.modifyUserById(updateUser);
            if (modifyUserCount <= 0) {
                return Result.error("更新用户信息失败");
            }

            //更新session中用户的信息
            sessionUser.setName(realName);
            sessionUser.setIdCard(idCard);

            request.getSession().setAttribute(Constants.SESSION_USER,sessionUser);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("平台异常,请稍后重试");
        }
        return Result.success();
    }


    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request,Model model) {

        //从session中获取用户的信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        //根据用户标识获取帐户信息
        FinanceAccount financeAccount = financeAccountService.queryFinanceAccountByUid(sessionUser.getId());
        model.addAttribute("financeAccount",financeAccount);

        //将以下查询看作是一个分页,但实际上它们不是分页功能
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("uid",sessionUser.getId());
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",5);

        //根据用户标识获取最近投资记录,显示第1页,每页显示5条
        List<BidInfoExtLoan> bidInfoExtLoanList = bidInfoService.queryRecentlyBidInfoListByUid(paramMap);
        model.addAttribute("bidInfoExtLoanList",bidInfoExtLoanList);

        //根据用户标识获取最近充值记录,显示第1页,每页显示5条
        List<RechargeRecord> rechargeRecordList = rechargeRecordService.queryRecentlyRechargeRecordListByUid(paramMap);
        model.addAttribute("rechargeRecordList",rechargeRecordList);

        //根据用户标识获取最近收益记录,显示第1页,每页显示5条
        List<IncomeLoanVO> incomeLoanVOList = incomeRecordService.queryRecentlyIncomeRecordListByUid(paramMap);
        model.addAttribute("incomeLoanVOList",incomeLoanVOList);


        return "myCenter";
    }


    @RequestMapping(value = "/loan/page/login")
    public String pageLogin(Model model,
                            @RequestParam (value = "redirectURL",required = false) String redirectURL) {

        model.addAttribute("redirectURL",redirectURL);

        return "login";
    }

    @PostMapping(value = "/loan/login")
    public @ResponseBody Result login(HttpServletRequest request,
                                      @RequestParam (value = "phone",required = true) String phone,
                                      @RequestParam (value = "loginPassword",required = true) String loginPassword,
                                      @RequestParam (value = "messageCode",required = true) String messageCode) {
        try {

            //从redis缓存中获取的短信验证码
            String redisMessageCode = redisService.get(phone);

            //判断用户输入的短信验证码是否正确
            if (!StringUtils.equals(messageCode, redisMessageCode)) {
                return Result.error("请输入正确的短信验证码");
            }

            //用户登录[1.根据手机号和登录密码查询用户信息 2.更新最近登录时间](手机号,登录密码) -> 返回User
            User user = userService.login(phone,loginPassword);

            //将用户的信息存放到session中
            request.getSession().setAttribute(Constants.SESSION_USER,user);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("手机号或密码有误,请重新输入");
        }
        return Result.success();
    }

    @RequestMapping(value = "/loan/logout")
    public String logout(HttpServletRequest request) {
        //让session失效
        request.getSession().invalidate();
        //消除指定session中的值
//        request.getSession().removeAttribute(Constants.SESSION_USER);

        return "redirect:/index";
    }
}
