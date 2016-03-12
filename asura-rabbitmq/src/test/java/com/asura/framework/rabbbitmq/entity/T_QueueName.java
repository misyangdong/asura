/**
 * @FileName: T_QueueName.java
 * @Package: com.asura.framework.rabbbitmq.entity
 * @author sence
 * @created 3/12/2016 11:20 AM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbbitmq.entity;

import com.asura.framework.rabbitmq.entity.NameKey;
import com.asura.framework.rabbitmq.entity.QueueName;
import org.junit.Assert;
import org.junit.Test;

/**
 * <p></p>
 *
 * <PRE>
 * <BR>	修改记录
 * <BR>-----------------------------------------------
 * <BR>	修改日期			修改人			修改内容
 * </PRE>
 *
 * @author sence
 * @since 1.0
 * @version 1.0
 */
public class T_QueueName {

    @Test
    public void getQueueName(){
        QueueName nameKey = new QueueName("s","m","f");
        Assert.assertEquals(nameKey.getQueueName(),"s_m_f");
    }

    @Test
    public void getQueueName1(){
        try {
            QueueName nameKey = new QueueName("","","");
        }catch (Exception e){
            Assert.assertEquals(e.getMessage(),"queueName:system is null");
        }
    }

    @Test
    public void getQueueName2(){
        try {
            QueueName nameKey = new QueueName("s","","");
        }catch (Exception e){
            Assert.assertEquals(e.getMessage(),"queueName:module is null");
        }
    }

    @Test
    public void getQueueName3(){
        try {
            QueueName nameKey = new QueueName("s","m","");
        }catch (Exception e){
            Assert.assertEquals(e.getMessage(),"queueName:function is null");
        }
    }

}
