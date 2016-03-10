/**
 * @FileName: T_RabbitMqSendUtil.java
 * @Package: com.asura.framework.rabbbitmq
 * @author sence
 * @created 3/9/2016 9:20 PM
 * <p/>
 * Copyright 2015 ziroom
 */
package com.asura.framework.rabbbitmq;

import com.asura.framework.rabbitmq.PublishSubscribeType;
import com.asura.framework.rabbitmq.connection.RabbitConnectionFactory;
import com.asura.framework.rabbitmq.send.RabbitMqSendClient;
import org.junit.Before;
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
public class T_RabbitMqSendUtil {

    RabbitConnectionFactory connectionFactory;

    @Before
    public void testBefore(){
        connectionFactory = new RabbitConnectionFactory();
        connectionFactory.init();
    }

    @Test
    public void testSendQueue() throws Exception {
        RabbitMqSendClient client = new RabbitMqSendClient();
        client.setRabbitConnectionFactory(connectionFactory);
        int i =0;
        String s = "";
        while (++i<100000) {
            if(s.length()<2048) {
                s += i;
            }
            client.sendQueue("LSQ_QUEUE_02", "HELLO WORLD +" +s);
        }
    }

    @Test
    public void testSendQueue2()throws Exception {
        RabbitMqSendClient client = new RabbitMqSendClient();
        client.setRabbitConnectionFactory(connectionFactory);
        int i =0;
        String s = "";
        while (++i<100000) {
            if(s.length()<2048) {
                s += i;
            }
            client.sendQueue("LSQ_QUEUE_03", "HELLO WORLD +" +s);
        }
    }

    @Test
    public void testSendTopic() throws Exception {
        RabbitMqSendClient client = new RabbitMqSendClient();
        client.setRabbitConnectionFactory(connectionFactory);
        int i =0;
        String s = "";
        while (++i<100000) {
            if(s.length()<2048) {
                s += i;
            }
            String routingKey = "";
            if(i%3==1){
                routingKey = "bbb";
            }
            if(i%3==2){
                routingKey = "ddd";
            }
            if(i%3==0){
                routingKey = "xxx";
            }
            client.sendTopic("LSQ_EXCHANGE_D_01",routingKey , PublishSubscribeType.DIRECT,"hello world "+i);
            System.out.println(i);
        }
    }

}
