package cn.hyt.server;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by Alex on 2016/6/28.
 */
public class MydfsServer {
    private  String host;
    private  int port;
    public   void  sendBeat(){

        try {
            String servername = UUID.randomUUID().toString().toUpperCase();
            while (true){
                Socket socket =new Socket("localhost",8888);
                OutputStream out = socket.getOutputStream();
                DataOutputStream socketOut=new DataOutputStream(out);
                Calendar calendar = Calendar.getInstance();
                socketOut.writeUTF(servername);
                socketOut.writeLong(calendar.getTimeInMillis());
                socketOut.writeUTF(host);
                socketOut.writeInt(port);
                Thread.sleep(30000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public  void upload(byte[] buf){

    }
}
