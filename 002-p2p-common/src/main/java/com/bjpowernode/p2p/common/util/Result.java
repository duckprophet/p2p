package com.bjpowernode.p2p.common.util;

import java.util.HashMap;

/**
 * ClassName:Result
 * Package:com.bjpowernode.p2p.common.util
 * Description:封装响应参数对象的处理类
 *
 * @date:2020/4/3 10:53
 * @author:动力节点
 */
public class Result extends HashMap {

    /**
     * 成功响应
     * @return
     */
    public static Result success() {
        Result result = new Result();
        result.put("code",1);
        result.put("success",true);
        return result;
    }

    /**
     * 失败响应
     * @param message
     * @return
     */
    public static Result error(String message) {
        Result result = new Result();
        result.put("code",-1);
        result.put("message",message);
        result.put("success",false);

        return result;
    }

    public static Result success(String data) {
        Result result = new Result();
        result.put("code",1);
        result.put("data",data);
        result.put("success",true);
        return result;
    }
}
