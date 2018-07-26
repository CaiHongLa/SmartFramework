package cn.cloudwalk.smartframework.common.util;

import cn.cloudwalk.smartframework.common.exception.desc.impl.SystemExceptionDesc;
import cn.cloudwalk.smartframework.common.exception.exception.FrameworkInternalSystemException;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LIYANHUI
 */
public class NetUtil {

    public static Map<String, String> getAvailableIp(String pattern) {
        Map<String, String> ipList = new HashMap<>();

        for (Map.Entry<String, String> entry : getAvailableIp().entrySet()) {
            if (entry.getKey().matches(pattern)) {
                ipList.put(entry.getKey(), entry.getValue());
            }
        }

        return ipList;
    }

    public static Map<String, String> getAvailableIp() {
        Map<String, String> ipList = new HashMap<>();

        try {
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();

            while (true) {
                NetworkInterface networkInterface;
                do {
                    do {
                        if (!interfaces.hasMoreElements()) {
                            return ipList;
                        }

                        networkInterface = (NetworkInterface) interfaces.nextElement();
                    } while (networkInterface.isLoopback());
                } while (!networkInterface.isUp());

                Enumeration addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) addresses.nextElement();
                    ipList.put(inetAddress.getHostAddress(), networkInterface.getDisplayName());
                }
            }
        } catch (SocketException e) {
            throw new FrameworkInternalSystemException(new SystemExceptionDesc(e));
        }
    }
}
