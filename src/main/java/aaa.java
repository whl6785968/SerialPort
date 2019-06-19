import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import javax.print.attribute.standard.PrinterMessageFromOperator;

import com.sun.glass.ui.TouchInputSupport;

public class aaa implements SerialPortEventListener{

    protected static CommPortIdentifier portid = null;  //通讯端口标识符
    protected static SerialPort comPort = null;         //串行端口
    protected int BAUD = 9600;  //波特率
    protected int DATABITS = SerialPort.DATABITS_8;;  //数据位
    protected int STOPBITS = SerialPort.STOPBITS_1;  //停止位
    protected int PARITY = SerialPort.PARITY_NONE;  //奇偶检验
    private static OutputStream oStream;    //输出流
    private static InputStream iStream;     //输入流
    StringBuilder buf = new StringBuilder(128);

    public static void main(String[] args) {
        aaa my = new aaa();
        my.setSerialPortNumber();
    }

    /**
     * 读取所有串口名字
     */
    private void listPortChoices() {
        CommPortIdentifier portId;
        Enumeration en = CommPortIdentifier.getPortIdentifiers();
        // iterate through the ports.
        while (en.hasMoreElements()) {
            portId = (CommPortIdentifier) en.nextElement();
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                System.out.println(portId.getName());
            }
        }
    }

    /**
     * 设置串口号
     * @param
     * @return
     */
    private void setSerialPortNumber() {

        String osName = null;
        String osname = System.getProperty("os.name", "").toLowerCase();
        if (osname.startsWith("windows")) {
            // windows
            osName = "COM1";
        } else if (osname.startsWith("linux")) {
            // linux
            osName = "/dev/ttyS1";
        }
        System.out.println(osName);
        try {
            portid = CommPortIdentifier.getPortIdentifier(osName);
            // portid = CommPortIdentifier.getPortIdentifier(Port);
            if(portid.isCurrentlyOwned()){
                System.out.println("端口在使用");
            }else{
                comPort = (SerialPort) portid.open(this.getClass().getName(), 1000);
            }
        } catch (PortInUseException e) {
            System.out.println("端口被占用");
            e.printStackTrace();

        } catch (NoSuchPortException e) {
            System.out.println("端口不存在");
            e.printStackTrace();
        }

        try {
            iStream = comPort.getInputStream(); //从COM1获取数据
            oStream = comPort.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            comPort.addEventListener(this);       //给当前串口增加一个监听器
            comPort.notifyOnDataAvailable(true);  //当有数据是通知
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }

        try {
            //设置串口参数依次为(波特率,数据位,停止位,奇偶检验)
            comPort.setSerialPortParams(this.BAUD, this.DATABITS, this.STOPBITS, this.PARITY);
        } catch (UnsupportedCommOperationException e) {
            System.out.println("端口操作命令不支持");
            e.printStackTrace();
        }

        try {

            //# testData
            String testData = "1";
            oStream.write(testData.getBytes());


            // iStream.close();
            // comPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:// 当有可用数据时读取数据,并且给串口返回数据
                try {
                   /* while(iStream.available() > 0) {
                        byte[] bytes = new byte[iStream.available()];
                        int len = 0;
                        while ((len = iStream.read())!=-1){
                            System.out.println("接收数据："+(new String(bytes,0,len)));

                        }
                        System.out.println("接收数据:"+iStream.read());
                    }*/
                    byte[] bytes = new byte[iStream.available()];
                    int len = 0;
                    while ((len = iStream.read(bytes))>0){
                        System.out.println("len = " + len);
                        System.out.println("接收数据："+new String(new String(bytes,0,len).getBytes("GB2312"),"utf-8"));

                    }
                } catch (IOException e) {

                }
                break;
        }
    }
    public static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            // 奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            // 偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }
}