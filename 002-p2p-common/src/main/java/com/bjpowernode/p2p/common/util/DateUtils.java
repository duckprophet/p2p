package com.bjpowernode.p2p.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.p2p.common.util
 * Description:
 *
 * @date:2020/4/11 10:28
 * @author:动力节点
 */
public class DateUtils {
    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
