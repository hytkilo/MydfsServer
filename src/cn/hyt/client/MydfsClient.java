package cn.hyt.client;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Alex on 2016/6/27.
 */
public class MydfsClient{
    private  String ip;
    private  int port;

    public MydfsClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public  void upload(String filename, String stuffix){
        try {
            File file=new File(filename);
            long filesize=file.length();
            //设置每块的大小是10M
            int block=1024*1024*10;
            int blockCount=(int)Math.ceil(filesize/(float)(block));
            //获取文件的最后的大小
            int lastblocksize=(int)(filesize%block);
            //发生随机生成的文件名字
            String randomFileName=UUID.randomUUID().toString().toUpperCase();

            ExecutorService executorService = Executors.newFixedThreadPool(10);
            SocketManager socketManager=new SocketManager("localhost",9999);
            for(int threadId=0;threadId<blockCount;threadId++){
                if(threadId==(blockCount-1))block=lastblocksize;
                executorService.execute(new ThreadUpload(socketManager,filename,stuffix,block,blockCount,threadId,randomFileName));
            }
            System.out.println("ThreadClose");
            executorService.shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String filename="C:/data/Test.xlsx";
        String stuffix=".xlsx";
        MydfsClient client=new MydfsClient("localhost",9999);
        client.upload(filename,stuffix);
    }
}
