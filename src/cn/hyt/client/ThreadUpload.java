package cn.hyt.client;

import cn.hyt.util.MD5Util;

import java.io.*;
import java.net.Socket;

/**
 * Created by hyt on 2016/6/27.
 */
public class ThreadUpload implements Runnable {
    private  SocketManager socketManager;
    private  String filename;
    private  String stuffix;
    private int block;
    private  int blockCount;
    private int threadId;
    private  String randomFilename;

    public ThreadUpload(SocketManager socketManager, String filename, String stuffix, int block, int blockCount, int threadId, String randomFilename) {
        this.socketManager=socketManager;
        this.filename = filename;
        this.stuffix = stuffix;
        this.block = block;
        this.blockCount = blockCount;
        this.threadId = threadId;
        this.randomFilename = randomFilename;
    }
    @Override
    public void run() {
        try{
            // 1 socket  2 socket  3 socket 4 socket 5 2-socket
            Socket socket=socketManager.getConnection();
            OutputStream out=socket.getOutputStream();
            final DataOutputStream socketOut=new DataOutputStream(out);
            InputStream in =socket.getInputStream();
            final DataInputStream socketIn=new DataInputStream(in);
            socketOut.writeBoolean(true);
            socketOut.writeInt(block);
            socketOut.writeUTF(randomFilename);
            socketOut.writeUTF(stuffix);
            socketOut.writeInt(threadId);
            RandomAccessFile randomFile=new RandomAccessFile(filename,"r");
            long begin=(long)threadId*(long)block;
            randomFile.seek(begin);
            //把数据输入到服务器
            byte[] buf=new byte[block];
            int read=randomFile.read(buf);
            socketOut.write(buf,0,read);
            socketOut.flush();
            //阻塞先把文件的二进制流输入到socket流中
            String message=socketIn.readUTF();
            //把字节数组写入到内存中
            ByteArrayOutputStream cacheOut=new ByteArrayOutputStream();
            cacheOut.write(buf,0,read);
            cacheOut.flush();
            String md5= MD5Util.md5sum(cacheOut.toByteArray());
            socketOut.writeUTF(md5);
            message = socketIn.readUTF();
            System.out.println(message);
            socketManager.releaseConnection(socket);
            randomFile.close();
            System.gc();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
