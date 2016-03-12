/**
 * @FileName: T_BindingKey.java
 * @Package: com.asura.framework.rabbbitmq.entity
 * @author sence
 * @created 3/12/2016 11:20 AM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbbitmq.entity;

import com.asura.framework.rabbitmq.entity.BindingKey;
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
public class T_BindingKey {

    @Test
    public void getBindingKey(){
        BindingKey nameKey = new BindingKey("s","m","f");
        Assert.assertEquals(nameKey.getBindingKey(), "s.m.f");
    }

    @Test
    public void getBindingKey1(){
        BindingKey nameKey = new BindingKey("","","");
        Assert.assertEquals(nameKey.getBindingKey(), "*.*.*");
    }

    @Test
    public void getBindingKey2(){
        BindingKey nameKey = new BindingKey("s","","");
        Assert.assertEquals(nameKey.getBindingKey(), "s.*.*");
    }

    @Test
    public void getBindingKey3(){
        BindingKey nameKey = new BindingKey("s","m","");
        Assert.assertEquals(nameKey.getBindingKey(), "s.m.*");
    }

    @Test
    public void getBindingKey4(){
        BindingKey nameKey = new BindingKey("s","","f");
        Assert.assertEquals(nameKey.getBindingKey(), "s.*.f");
    }

}
