/**
 * @FileName: TopicType.java
 * @Package: com.asura.framework.rabbitmq
 * @author sence
 * @created 3/9/2016 5:36 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbitmq;

/**
 * <p>Publish/Subscribe 四种交换模式</p>
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
public enum PublishSubscribeType {


    DIRECT("direct"),
    TOPIC("topic"),
    FANOUT("fanout"),
    HEADERS("headers");

    PublishSubscribeType(String name){
        this.name = name;
    }

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
