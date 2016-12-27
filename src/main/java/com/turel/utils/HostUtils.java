package com.turel.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by chaimturkel on 12/20/16.
 */
public class HostUtils {

    static public class Host {
        String host;
        Integer port;

        public Host(String host, Integer port) {
            this.host = host;
            this.port = port;
        }

        @Override public String toString() {
            return host + ":" + port;
        }

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }
    }

    public static List<Host> parseHosts(String hostList, int defaultPort) {
        final String[] hosts = hostList.split(",");
        return Arrays.stream(hosts).map(host -> {
            final String[] parts = host.split(":");
            if (parts.length == 1)
                return new Host(parts[0].trim(), defaultPort);
            else if (parts.length == 2)
                return new Host(parts[0].trim(), Integer.parseInt(parts[1]));
            else
                throw new RuntimeException("invalid formation " + host);
        })
                .collect(Collectors.toList());
    }
}
