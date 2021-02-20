p2p-web
1.它是SpringBoot框架web项目,并使用Thymeleaf前端模版引擎
2.它是dubbo分布式框架中服务的消费者
3.该工程集成了Thymeleaf,Dubbo,zookeeper
4.该工程是用户唯一能直接访问到的工程(即:该工程就是处理浏览器客户端发送的请求)
5.该工程必须由p2p-parent父工程进行管理

以下就是动力金融网响应参数的格式定义:
成功响应参数格式:
{"code":1,"success":true}

失败响应参数格式:
{"code":-1,"message":"13700000000被占用了","success":false}

code 业务处理的结果:1成功,-1失败
message 业务处理失败的消息(只有失败的时候才会有message)
success 结果:成功true,失败false