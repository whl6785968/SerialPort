import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

public class SerialPortTest implements SerialPortEventListener {
    private CommPortIdentifier identifier;
    private Enumeration<CommPortIdentifier> portList;
    private SerialPort serialPort;

    private InputStream inputStream;
    private OutputStream outputStream;

    private String test = "";

    private static SerialPortTest singleton = new SerialPortTest();
    public void init(){
        portList = CommPortIdentifier.getPortIdentifiers();

        while(portList.hasMoreElements()){
            identifier = portList.nextElement();
            if(identifier.getPortType() == CommPortIdentifier.PORT_SERIAL){
                //比较串口的名称是否为COM1
                if("COM1".equals(identifier.getName())){
                    try {
                        if(serialPort==null){
                            serialPort = (SerialPort)identifier.open(Object.class.getSimpleName(),50);
                            System.out.println("className"+Object.class.getSimpleName());
                            System.out.println("获取串口COM1");
                            serialPort.addEventListener(this);
                            //设置数据有效时间
                            serialPort.notifyOnDataAvailable(true);
                            //设置串口通讯参数
                            //波特率、数据位、停止位、校验方式
                            serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);


                        }
                      /*  test = "";
                        outputStream = serialPort.getOutputStream();*/


                    } catch (PortInUseException e) {
                        e.printStackTrace();
                    } catch (TooManyListenersException e) {
                        e.printStackTrace();
                    } catch (UnsupportedCommOperationException e) {
                        e.printStackTrace();
                    }/* catch (IOException e) {
                        e.printStackTrace();
                    }*/
                    break;
                }
            }

        }
    }
    public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()){
            case SerialPortEvent.BI: //通讯中断
            case SerialPortEvent.OE: //溢位错误
            case SerialPortEvent.FE: //帧错误
            case SerialPortEvent.PE: //奇偶校验错误
            case SerialPortEvent.CD: //载波检测
            case SerialPortEvent.CTS: //清除发送
            case SerialPortEvent.DSR: //数据设备准备好
            case SerialPortEvent.RI: //响铃侦测
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: //输出缓冲区已清空
                break;
            case SerialPortEvent.DATA_AVAILABLE: //数据到达
                try {
                    readFromComm();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    //读取串口返回信息
    public void readFromComm() throws IOException {
        inputStream = serialPort.getInputStream();
        byte[] bytes = new byte[inputStream.available()];

        int len = 0;
        while ((len = inputStream.read(bytes))>0){
            System.out.println("实时反馈："+new String(bytes,0,len));
            test += new String(bytes,0,len).trim();
            System.out.println("test = " + test);
//            break;
        }
    }

    //关闭串口
    public void closeSerialPOrt(){
        if(serialPort!=null){
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();

            if(inputStream!=null){
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(outputStream!=null){
                try {
                    outputStream.close();
                    outputStream=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            serialPort.close();
            serialPort = null;
        }
    }

    public  void sendComm(String something) throws IOException {
        outputStream = serialPort.getOutputStream();
        outputStream.write(something.getBytes());
        outputStream.flush();

    }

    public static void main(String[] args) throws IOException {
        SerialPortTest serialPortTest = new SerialPortTest();
        serialPortTest.init();
//        serialPortTest.sendComm("sometg");
        System.out.println("输出数据"+serialPortTest.test);
//        serialPortTest.closeSerialPOrt();
    }
}
