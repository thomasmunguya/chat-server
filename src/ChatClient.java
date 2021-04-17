import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClient {
    private  String host;
    private  int port;
    private String id;
    private InetSocketAddress address;
    private SocketChannel clientSC;
    private StringBuilder chat;

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
        chat = new StringBuilder();
        address = new InetSocketAddress(host, port);
        try {
            clientSC = SocketChannel.open(address);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log(id + ": logged in");
    }

    /**
     * Logs out a client
     */
    public void logout() {
        log(id + ": logged out");
        try {
            clientSC.close();
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
//        log(id + ": " + req);
        buffer.clear();
    }

    public void receiveBroadCast() {

        try{
            Socket sock = new Socket(host, port);
            BufferedReader receive = new BufferedReader(new InputStreamReader(sock.getInputStream()));//get inputstream
            String msgReceived = null;
            while((msgReceived = receive.readLine())!= null)
            {
                System.out.println("From Server: " + msgReceived);
                chat.append(msgReceived);
//                System.out.println("Please enter something to send to server..");
            }
        }catch(Exception e){System.out.println(e.getMessage());}
    }

    private void log(String str) {
        chat.append(str + "\n");
        System.out.println(str);
    }

    /**
     * Returns the chat as viewed from the client's side
     * @return
     */
    public String getChatView() {
        return "=== id chat";
//       return "=== id chat view\n" + chat.toString();
    }
}
