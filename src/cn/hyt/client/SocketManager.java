package cn.hyt.client;

import java.net.Socket;
    import java.util.Hashtable;
    public class SocketManager {
        private int initsize;
        private  String host;
        private int port;
        private Hashtable socketPool = null;// 连接池
        private boolean[] socketStatusArray = null;// 连接的状态（true-被占用；false-空闲）
        public SocketManager(String host, int port){
            this.host=host;
            this.port=port;
            this.initsize=4;
            this.socketPool = new Hashtable();
            this.socketStatusArray = new boolean[initsize];
            System.out.println("准备建立连接池.");
            try {
                for (int i = 0; i < initsize; i++) {
                    this.socketPool.put(new Integer(i), new Socket(host, port));
                    this.socketStatusArray[i] = false;
                }
            } catch (Exception e) {
                System.out.println("与邮局的连接池建立失败！");
                throw new RuntimeException(e);
            }
        }
       //  \\\\\\\
        public synchronized  Socket getConnection() {
                for (int i = 0; i < initsize; i++) {
                    if (!this.socketStatusArray[i]) {
                        this.socketStatusArray[i] = true;
                        Socket socket = (Socket) this.socketPool.get(new Integer(i));
                        return socket;
                    }
                }
                //System.out.println("ready get socket,wait a minute....");

            return  null;
        }

        public  void releaseConnection(Socket socket) {
            for (int i = 0; i < initsize; i++) {
                if (((Socket) this.socketPool.get(new Integer(i))) == socket) {
                    this.socketStatusArray[i] = false;
                    break;
                }
            }
        }
        public  Socket rebuildConnection(Socket socket) {

            Socket newSocket = null;
            for (int i = 0; i < initsize; i++) {
                try {
                    if (((Socket) this.socketPool.get(new Integer(i))) == socket) {
                        System.out.println("重建连接池中的第" + i + "个连接.");
                        newSocket = new Socket(host, port);
                        this.socketPool.put(new Integer(i), newSocket);
                        this.socketStatusArray[i] = true;
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("重建连接失败！");
                    throw new RuntimeException(e);
                }
            }
            return newSocket;
        }

        public synchronized  void releaseAllConnection() {
            // 关闭所有连接
            Socket socket = null;
            for (int i = 0; i < initsize; i++) {
                socket = (Socket) this.socketPool.get(new Integer(i));
                try {
                    socket.close();
                } catch (Exception e) {
                }
            }
        }
    }
