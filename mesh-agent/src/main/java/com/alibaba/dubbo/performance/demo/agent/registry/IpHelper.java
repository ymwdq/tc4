package com.alibaba.dubbo.performance.demo.agent.registry;

import java.net.InetAddress;

public class IpHelper {

    public static String getHostIp() throws Exception {

        String ip = InetAddress.getLocalHost().getHostAddress();
        return ip;
//        return "10.108.113.158";
//        return "10.108.112.131";
    }
}
