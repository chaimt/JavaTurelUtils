package com.turel.utils;

import com.turel.utils.classExamples.Order;
import com.turel.utils.classExamples.OrderOther;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * Created by chaimturkel on 8/29/16.
 */
public class ReflectionUtilsTest {

    @Test
    public void fillObject() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        JSONObject data = new JSONObject();
        data.put("shippingPostalCode","486700");
        data.put("shippingCountry","Israel");
        data.put("shippingCity","Revava");
        data.put("shippingState","Shomron");
        data.put("shippingStreet","Eretz Hemda 126");
        

        Order order = ReflectionUtils.createObject(data, Order.class);
        Assert.assertNotNull(order);
        Assert.assertEquals("486700",order.getShippingPostalCode());
        Assert.assertEquals("Israel",order.getShippingCountry());
        Assert.assertEquals("Revava",order.getShippingCity());
        Assert.assertEquals("Shomron",order.getShippingState());
        Assert.assertEquals("Eretz Hemda 126",order.getShippingStreet());
    }

    @Test
    public void copyFields(){
        Order a = new Order();
        a.setStatus("a");
        a.setActual_Payment_Amount__c(42D);
        a.setDeferred_Payment_Date__c(new Date());
        Order b = new Order();
        ReflectionUtils.copyFields(a,b);
        Assert.assertEquals(a.getStatus(),b.getStatus());
        Assert.assertEquals(a.getActual_Payment_Amount__c(),b.getActual_Payment_Amount__c());
        Assert.assertEquals(a.getDeferred_Payment_Date__c(),b.getDeferred_Payment_Date__c());
    }

    @Test
    public void copyFieldsIntToShort(){
        Order a = new Order();
        a.setIntField((int)4);
        OrderOther b = new OrderOther();
        ReflectionUtils.copyFields(a,b);
        Assert.assertEquals((int)a.getIntField(),(int)b.getIntField());

        b.setIntField((short)6);
        ReflectionUtils.copyFields(b,a);
        Assert.assertEquals((int)a.getIntField(),(int)b.getIntField());
    }

}
