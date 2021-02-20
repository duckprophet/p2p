package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016102300747602";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCrdvMV+up9FuTji3wLn/M4xRVd8K2GerEyDt+TIKrY9tsFgIKmMyzu2oIVvfpasBCAOi2zpAfvVCUd+v4s6AyOvPaw7fZzuY2Y48I1jTovDhkrmZwsr4iQLXNpGCEg3pQGlkihD+iaw1KzXmkBLt/7DoyMugSqVH20+7LKMj7TtPMKO5+N10sQZhC4CQ9UZ4olRdqamoBWhovXfFWO8ksV4+IDjNWQ7Ml3Y0j1ulcqCmo8p0ay9RwCMwYOD+DzZZALfVBKzAjQia/TO+n/UwcubNCS2TS/A7jaRXnA4wNyqJiiaZeZ3/u7nRnGwLesFNlO7/j0gRCnH+kZZDXhEtAHAgMBAAECggEAUygVLIvMPW5lA5YaSuj/5IBwpGadQaMvXL+p3ojxUtgl0L1Kn6AiIpUGkdTZHjJVTlro3z6mOoPsnGWl79F6hZ9PZO+DnwxWh9et0Jr1pOyJeIO/IkzfRk1Es6toJbDMyYUJJFO605P2/9bBxjkdl3yu3XE54gGoFaYcR8OmhLVop4xldrCMzBjLNVs7hKH2B1HkZFrvurHQnvn7bq912DdHqzKjJv1CCCffAN/GL5CvViAQyhoo9qzs3Ww+dQgYoDFAz7OFr7SWinrx37CtxFd37J0CWeGXJ8A/GNwuztXOWNU9ul2+wjiGCiAQa5hC0SkfANtMy/OzpzmLsA59gQKBgQDrt4/I8OsN7cb2IK0VWgXPyLgGovYKY80EdJ5fhqAMmLyBy0swsmublIB1/g5gSNX35ZM9yO5er6dPRwky8erjla2bLbuLqVUteJRvm/TpCsYsG2u344Z+gimcJcALVPccdT3pSb9o6DMBhJhU+7t2as4rARe/p2AaiqBqST0uzwKBgQC6OATY1+WS7NZ1hhpwaRntD7+KuAzw+Thf4ZbvMnA7+oC/f8K4cbwxg/VCXpWMcEJwGDHQACsOX/cZNxHLszPQaZ42nryuGQLYD2HacH3nlj0QTDdvZkASID+i5ZjSyKqc/BtTdoCjEE+soXjonp/DFL+rNFffmXyrOUhAA53ZSQKBgQCiW9RxDuiItpAFtioh+9jm7xU6lQKt02JCwGTZ3WveYVHugO2whCS0jAwTucODNIgU8Q+DTiRKGek/JOnEKnek/NMQgOaeYytlX5m2apC9+AraFKUUi2Z3OwaYJDRsWcC/3E2ho4dxJgcD86XNW3sbLKQHYQBPpWT/wx6NNxcqZwKBgEnivjGXf/RK2lCpE8Z0PSAtNm+A1UmraK3KYUuo9JUnsHvK4mSyY9w1olNiGe4uAH5fayw/VNYQR4eOrRXolW9k6Kx9URm2vMUjXU0Bg0Mm/jWAOCKQLG3Es9sbRAamLeO5t9Hm2+VXLHWvX7zUa0lI6z+vtgn26Vs6kCsuBCxZAoGBAM6DpWBVTYhjkG104OlSNPQ4P8rSTn7pbQWbI2Kkv0uEVW5vbE+58+CzsnEOaGOKXF8pkqakSrjKJWnz69rW6keIicG1U9tWbbqhMKEU5gxhF4H/bAudnNeHe4i5OiEhQg/Mxv5kcIcnqc+V+xiy6QVHwwENUTm55XnNlKGnoxnb";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr97s46m0fS4lyz+OtCuU8aU8va5Jnv/PoOnHBcmWHp2n6ecW2pAtXfvSkfg36u6Bfp7Tne9fD0x8qCpEoXsAdoM3KtM6sr6uTNzKQEu1ZosTkqM3XpArbt+8w+gYFh5FYd0Srzg+sIVcbSiXwfFAv73yngiRA2qibs86hA/MZ7pA0gwfWfyoE+Cs53auA/i2Lj+x8AcRx710Dyrks36nd/Bf9XYitLTvnehPo1M90o0ogw3hfprdWxL8HbCBbD+sovYJ47vTD27bzrx+xZP6wtrq1CJnMKC7etqscah5L5nsXTpq1Pmhai/Ws5ksoUUaM/PrP3XJcjsglrbLZOvRgQIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

