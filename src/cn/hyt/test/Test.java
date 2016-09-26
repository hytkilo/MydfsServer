package cn.hyt.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import static java.lang.System.out;

public class Test {
        public static final int BUFSIZE = 1024 * 8;  
        public static void mergeFiles(String outFile, String[] files) {  
            FileChannel outChannel = null;
            out.println("Merge " + Arrays.toString(files) + " into " + outFile);
            try {  
                outChannel = new FileOutputStream(outFile).getChannel();
                for(String f : files){  
                    FileChannel fc = new FileInputStream(f).getChannel();
                    ByteBuffer bb = ByteBuffer.allocate(BUFSIZE);
                    while(fc.read(bb) != -1){  
                        bb.flip();  
                        outChannel.write(bb);  
                        bb.clear();  
                    }  
                    fc.close();  
                }  
                out.println("Merged!! ");  
            } catch (IOException ioe) {
                ioe.printStackTrace();  
            } finally {  
                try {if (outChannel != null) {outChannel.close();}} catch (IOException ignore) {}  
            }  
        }  
        public static void main(String[] args) {  
            mergeFiles("H:/output.xlsx", new String[]{"C:\\data\\mydfs\\Test.xlsx-0-part", "C:\\data\\mydfs\\Test.xlsx-1-part"});
        }  
    }