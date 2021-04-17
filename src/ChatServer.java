import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

public class ChatServer implements Runnable {

    private String host;
    private int port;
    private String log;
    private boolean running;
    private static final StringBuilder sbLog = new StringBuilder();
    private Selector selector;
    private SocketChannel clientSC;

    /**
     * Constructs a ChatServer
     */
    public ChatServer() {

    }

    /**
     * Constructs a ChatServer with the given parameters
     *
     * @param host the name/ address of the chat server
     * @param port the port on which the chart server is listening
     */
    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
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

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    /**
     * Starts the server
     * @throws IOException
     */
    public void startServer() {
        running = true;
        new Thread(this).start();
    }

    @SuppressWarnings("unused")
    public static void main(String[] args) throws IOException {
        ChatServer chatServer = new ChatServer("localhost", 9999);
        chatServer.startServer();
//        new Thread(chatServer).start();
    }

    private void log(String str) {
        System.out.println(str);
        sbLog.append(str);
    }

    @Override
    public void run() {

        try {
            // Selector: multiplexor of SelectableChannel objects
            selector = Selector.open(); // selector is open here

            // ServerSocketChannel: selectable channel for stream-oriented listening sockets
            ServerSocketChannel socket = ServerSocketChannel.open();
            InetSocketAddress address  = new InetSocketAddress(host, port);

            // Binds the channel's socket to a local address and configures the socket to listen for connections
            socket.bind(address);

            // Adjusts this channel's blocking mode.
            socket.configureBlocking(false);

            int ops = socket.validOps();
            SelectionKey selectKy = socket.register(selector, ops, null);

            // Keep server running as until stopped by user
//            System.out.println("Server is running: " + running);
            while (running) {

                // Selects a set of keys whose corresponding channels are ready for I/O operations
                selector.select();

                // token representing the registration of a SelectableChannel with a Selector
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();

                while (iterator.hasNext()) {
                    SelectionKey myKey = iterator.next();

                    // Tests whether this key's channel is ready to accept a new socket connection
                    if (myKey.isAcceptable()) {
                        SocketChannel Client = socket.accept();

                        // Adjusts this channel's blocking mode to false
                        Client.configureBlocking(false);

                        // Operation-set bit for read operations
                        Client.register(selector, SelectionKey.OP_READ);

                        // Tests whether this key's channel is ready for reading
                    } else if (myKey.isReadable()) {
                        SocketChannel client = (SocketChannel) myKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        client.read(buffer);
                        String result = new String(buffer.array()).trim();
                        if(!result.equals("")) {
                            sbLog.append(Calendar.HOUR + ":" + Calendar.MINUTE + ":" + Calendar.SECOND +
                                    " . " + Calendar.MILLISECOND + " " + result + "\n");
                            broadcast(result + "\n", client);
                        }

                    }
                    iterator.remove();
                }
            }
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void broadcast(String msg , SocketChannel sc) throws IOException {
        byte[] mArray = (msg).getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(mArray);
        sc.write(buffer);
        buffer.clear();
    }

    public String getServerLog() {
        return sbLog.toString();
    }

    /**
     * Stops the server
     */
    public void stopServer() {
        log("Server Stopped");
        running = false;
    }
        
}
