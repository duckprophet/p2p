package com.bjpowernode.p2p.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName:PaginationVO
 * Package:com.bjpowernode.p2p.model.vo
 * Description:
 *
 * @date:2020/4/2 10:50
 * @author:动力节点
 */
@Data
public class PaginationVO<T> implements Serializable {

    //此类没有自己编写set/get方法,交给lombok插件完成
    //完成这件事情需要以下三个步骤:
        //1.添加lombok插件
        //2.添加lombok依赖
        //3.在要使用的类上添加@Data注解

    //该种方式不是所有项目组都会要求使用
    //如果工作中不知道项目组是否使用lombok,那就不要使用
    //如果工作中项目组说可以使用,那么咱就使用就OK

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 每页显示的数据集合
     */
    private List<T> dataList;

}
