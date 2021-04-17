import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ChatClient {
    private  String host;
    private  int port;
    private String id;
    private InetSocketAddress address;
    private SocketChannel clientSC;
    private static StringBuilder chat;
    private boolean loggedIn;

    /**
     * Construcs a ChatClient
     */
    public ChatClient () {

    }

    /**
     * Constructs a ChatClient with the given parameters
     * @param host the host of server the chat client is connecting to
     * @param port the port of the server the chat client is connecting to
     * @param id the id of the client
     */
    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Logs a client in
     */
    public void login() {
        loggedIn = true;
        chat = new StringBuilder();
        address = new InetSocketAddress(host, port);
        try {
            clientSC = SocketChannel.open(address);
            send(id + " logged in");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Logs out a client
     */
    public void logout() {
        loggedIn = false;
        try {
            send(id + " logged out");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message
     * @param req the message to send
     */
    public void send(String req) throws IOException {
        byte[] mArray = (req).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(mArray);
        clientSC.write(buffer);
//        log(req);
        buffer.clear();
    }

    public void receive() {
        try{
            ByteBuffer buffer = ByteBuffer.allocate(256);
            clientSC.read(buffer);
            String result = new String(buffer.array()).trim();
            chat.append(result + "\n");
        }catch(Exception e){
                System.out.println(e.getMessage());
        }
    }

    private void log(String str) {
        System.out.println(str);
    }

    /**
     * Returns the chat as viewed from the client's side
     * @return
     */
    public String getChatView() {
       return "=== " + id + " chat view\n" + chat.toString();
    }
}
