package wg.pojo;

/**
 * User: Frank Tang <br/>
 * Date: 14-12-8<br/>
 * Time: 上午10:53<br/>
 * Email: lovefree103@gmail.com<br/>
 */
public class Server {

    public String serverIp;

    public int port;

    @Override
    public String toString() {
        return "Server{" +
                "serverIp='" + serverIp + '\'' +
                ", port=" + port +
                '}';
    }

    public Server(String serverIp, int port) {
        this.serverIp = serverIp;
        this.port = port;
    }

}
