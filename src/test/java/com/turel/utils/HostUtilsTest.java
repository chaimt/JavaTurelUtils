package com.turel.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by chaimturkel on 12/20/16.
 */
public class HostUtilsTest {

    @Test
    public void parseHosts() {
        List<HostUtils.Host> localhost = HostUtils.parseHosts("localhost", 8080);
        Assert.assertEquals(1, localhost.size());
        Assert.assertEquals("localhost", localhost.get(0).host);
        Assert.assertEquals(8080, (int) localhost.get(0).port);

        localhost = HostUtils.parseHosts("localhost:8080", 8080);
        Assert.assertEquals(1, localhost.size());
        Assert.assertEquals("localhost", localhost.get(0).host);
        Assert.assertEquals(8080, (int) localhost.get(0).port);

        localhost = HostUtils.parseHosts("localhost:8081", 8080);
        Assert.assertEquals(1, localhost.size());
        Assert.assertEquals("localhost", localhost.get(0).host);
        Assert.assertEquals(8081, (int) localhost.get(0).port);

        localhost = HostUtils.parseHosts("localhost:8081, a:1234, b, c:4567", 8080);
        Assert.assertEquals(4, localhost.size());
        Assert.assertEquals("localhost", localhost.get(0).host);
        Assert.assertEquals(8081, (int) localhost.get(0).port);
        Assert.assertEquals("a", localhost.get(1).host);
        Assert.assertEquals(1234, (int) localhost.get(1).port);
        Assert.assertEquals("b", localhost.get(2).host);
        Assert.assertEquals(8080, (int) localhost.get(2).port);
        Assert.assertEquals("c", localhost.get(3).host);
        Assert.assertEquals(4567, (int) localhost.get(3).port);

    }

    @Test(expected = RuntimeException.class)
    public void parseHostsError() {
        HostUtils.parseHosts("localhost:a:x", 8080);
    }
}