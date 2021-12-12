package de.eudaemon.swag;

import java.io.IOException;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class TestClient {
    public static void main(String[] args) throws IOException, MalformedObjectNameException {
        String port = args[0];
        JMXServiceURL url =
                new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
        JMXConnector connector = JMXConnectorFactory.connect(url);
        MBeanServerConnection connection = connector.getMBeanServerConnection();
        ObjectName objectName = new ObjectName("de.eudaemon.swag:type=ComponentInfo");
        ComponentInfoMBean componentInfo =
                MBeanServerInvocationHandler.newProxyInstance(
                        connection, objectName, ComponentInfoMBean.class, true);
        componentInfo.addNotificationListener(
                (notification, handback) -> {
                    try {
                        int id = (int) notification.getUserData();
                        System.out.println(
                                "componentInfo.getPlacementInfo(id) = "
                                        + componentInfo.getPlacementInfo(id));
                        System.out.println(
                                "componentInfo.getSize(is) = " + componentInfo.getSizeInfos(id));
                    } catch (Throwable t) {
                        System.out.println(t.getMessage());
                        t.printStackTrace();
                    }
                },
                null,
                null);
        while (true)
            ;
    }
}
