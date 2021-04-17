import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * A class to perform tasks requested by a client
 */
public class ChatClientTask implements Runnable {

    private ChatClientTask chatClientTask;
    private ChatClient client;
    private List<String> messages;
    private int wait;

    public ChatClient getClient() {
        return client;
    }

    public void setClient(ChatClient client) {
        this.client = client;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public int getWait() {
        return wait;
    }

    public void setWait(int wait) {
        this.wait = wait;
    }

    private ChatClientTask(ChatClient client, List<String> messages, int wait) {
        this.client = client;
        this.messages = messages;
        this.wait = wait;
    }

    /**
     * Returns an instance of ChatClientTask
     * @param c a client
     * @param msgs list of messages to be sent by the client
     * @param wait time (in milliseconds) between sending consecutive messages
     * @return
     */
    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) throws IOException{
        return new ChatClientTask(c, msgs, wait);
    }

    public void get() {
        run();
    }
    @Override
    public void run() {
        try {
//
//            InetSocketAddress address = new InetSocketAddress(client.getHost(), client.getPort());
//            SocketChannel clientSC = SocketChannel.open(address);
//
            client.login();

            // if wait is not zero then wait for wait seconds before sending the first message
            if(wait != 0)
                Thread.sleep(wait);

//
//            log(client.getId() + " logged in");

            for (String message: messages) {
//
//                byte[] mArray = new String(m).getBytes();
//                ByteBuffer buffer = ByteBuffer.wrap(mArray);
//                clientSC.write(buffer);
                client.send(message);
//                client.receiveBroadCast();
//                log(client.getId() + ": " + m);
//                buffer.clear();
//                System.out.println(Thread.currentThread().getName());
//
                // if wait is not zero then wait for wait seconds before sending next message
                if(wait != 0)
                    Thread.sleep(wait);
            }

            client.logout();

            if(wait != 0)
                Thread.sleep(wait);

//            clientSC.close();
//
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

//    private static void log(String str) {
//        System.out.println(str);
//    }

    public static void main(String[] args) throws IOException {

        ChatClient chatClient = new ChatClient("localhost", 9999, "thomasm");
        List<String> messages = new ArrayList<String>();
        messages.add("Thomas is here boys");
        messages.add("Hey Hey");
        ChatClientTask chatClientTask = ChatClientTask.create(chatClient, messages, 2);
        new Thread(chatClientTask).start();
    }
}
