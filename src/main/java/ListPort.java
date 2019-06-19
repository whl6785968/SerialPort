import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;

import java.util.Enumeration;
import java.util.HashSet;

public class ListPort {
    public static void listports(){
        HashSet<CommPortIdentifier> availableSerialPorts = getAvailableSerialPorts();
        for(CommPortIdentifier c:availableSerialPorts){
            System.out.println(c.getName() + ":" +getPortTypeName(c.getPortType()));
        }
    }

    public static void listCommPorts(){
        //获取系统中每个端口的CommPortIdentifier对象
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while(portEnum.hasMoreElements()){
            CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum.nextElement();

            System.out.println(portIdentifier.getName() + ":" + getPortTypeName(portIdentifier.getPortType()));
        }
    }
    //获取通信端口类型名称
    public static String getPortTypeName(int portType)
    {
        switch (portType){
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "parallel";
            case CommPortIdentifier.PORT_RAW:
                return "raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "serial";
            default:
                return "unkown type";

        }
    }

    //获取所有可用串口集合
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts(){
        HashSet<CommPortIdentifier> set = new HashSet<CommPortIdentifier>();
        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();

        while(portIdentifiers.hasMoreElements()){
            CommPortIdentifier cpi = (CommPortIdentifier) portIdentifiers.nextElement();

            switch (cpi.getPortType()){
                case CommPortIdentifier.PORT_SERIAL:
                    try {
                        CommPort port = cpi.open(Object.class.getSimpleName(), 50);
                        port.close();
                        set.add(cpi);

                    } catch (Exception e) {


                    }
            }
        }

        return set;
    }

    public static void main(String[] args) {
        listports();
        listCommPorts();
    }
}


