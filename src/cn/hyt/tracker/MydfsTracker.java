package cn.hyt.tracker;

import cn.hyt.util.MD5Util;
import cn.hyt.util.PropertiesUtil;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hyt on 2016/6/27.
 */
public class MydfsTracker {
    private String host;
    private int port;

    public MydfsTracker(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startup() {

        try {
            String port = PropertiesUtil.getValue("mydfs.port");
            String worker = PropertiesUtil.getValue("mydfs.worker");
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(port));
            //  SocketAddress address = new InetSocketAddress("192.168.43.239", 1158);
            //serverSocket.bind(address);
            //该方法是阻塞式的方法，等待客户端的连接
            // 100万个连接过来
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(worker));
            boolean isRunning = true;
            while (isRunning) {
                System.gc();
                final Socket socket = serverSocket.accept();
                //每次接受到一个连接都创建一个线程去处理，体验感好一些
                //把所有的线程都放在线程池中,保证当前只能且仅有5个线程在运行
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //socket.setKeepAlive(true);
                            InputStream in = socket.getInputStream();
                            DataInputStream socketIn = new DataInputStream(in);
                            OutputStream out = socket.getOutputStream();
                            DataOutputStream socketOut = new DataOutputStream(out);
                            boolean isRunning = false;
                            do {
                                isRunning = socketIn.readBoolean();
                                //读取客户端每块文件的大小
                                int block = socketIn.readInt();
                                //客户端生成文件的名字
                                String ramdonFileName = socketIn.readUTF();
                                //客户端文件的后缀名
                                String stuffix = socketIn.readUTF();
                                //获取客户端的第几块文件
                                int threadId = socketIn.readInt();

                                byte[] buf = new byte[block];
                                String basepath = PropertiesUtil.getValue("mydfs.basepath");
                                File file = new File(basepath + "/" + ramdonFileName + "." + stuffix + "-" + threadId + "-part");
                                OutputStream fileOut = new FileOutputStream(file);
                                ByteArrayOutputStream cachedOut = new ByteArrayOutputStream();
                                //择优选择一台服务器

                                while (block > file.length()) {
                                    int read = socketIn.read(buf);
                                    fileOut.write(buf, 0, read);
                                    fileOut.flush();
                                    cachedOut.write(buf, 0, read);
                                    cachedOut.flush();
                                }
                                socketOut.writeUTF("uploadOk");
                                String clientMD5 = socketIn.readUTF();
                                System.out.println("client md5" + clientMD5);
                                String serverMD5 = MD5Util.md5sum(cachedOut.toByteArray());
                                socketOut.writeUTF(file.getName() + "-upload-success");
                                //比较客户端和服务端的md5 确认文件是否传输完成
                                if (clientMD5.equals(serverMD5)) {
                                    socketOut.writeUTF(file.getName() + " upload success");
                                } else {
                                    socketOut.writeUTF(file.getName() + " upload error");
                                }
                                cachedOut.close();
                                fileOut.close();
                            } while (isRunning);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

            }
            //把线程池中的线程全部执行完毕，才关闭线程池
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, ServerStatus> serverMap = new HashMap<String, ServerStatus>();

    public void monitor() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                DataInputStream socketIn = new DataInputStream(in);
                String servername = socketIn.readUTF();
                long thistime = socketIn.readLong();
                String host = socketIn.readUTF();
                int port = socketIn.readInt();
                ServerStatus serverstatus = null;
                if (serverMap.get(servername) == null) {
                    serverstatus = new ServerStatus(servername, host, port, thistime, thistime);
                } else {
                    serverstatus = serverMap.get(servername);
                    Long lasttime = serverstatus.getLasttime();
                    if (thistime - lasttime == 30000) {
                        System.out.println("i am aliving");
                    } else {
                        serverstatus.setIsdead(true);
                    }
                }
                serverMap.put(servername, serverstatus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MydfsTracker tracker = new MydfsTracker("localhost", 8888);
        tracker.startup();
        tracker.monitor();
    }
}
