package cn.hyt.tracker;

/**
 * Created by hyt on 2016/6/28.
 */
public class ServerStatus {
    private String servername;
    private String host;
    private int port;
    private Long lasttime;
    private Long thistime;
    private  boolean isdead;

    public boolean isdead() {
        return isdead;
    }

    public void setIsdead(boolean isdead) {
        this.isdead = isdead;
    }

    public ServerStatus(String servername, String host, int port, Long lasttime, Long thistime) {
        this.servername = servername;
        this.host = host;
        this.port = port;
        this.lasttime = lasttime;
        this.thistime = thistime;
        this.isdead=false;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Long getLasttime() {
        return lasttime;
    }

    public void setLasttime(Long lasttime) {
        this.lasttime = lasttime;
    }

    public Long getThistime() {
        return thistime;
    }

    public void setThistime(Long thistime) {
        this.thistime = thistime;
    }
}
