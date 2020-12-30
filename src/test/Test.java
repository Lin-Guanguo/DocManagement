package test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.LinkedList;

public class Test {
    private final Selector selector = Selector.open();
    private final LinkedList<AcceptEvent> acceptEvents = new LinkedList<>();
    private final LinkedList<ReadEvent> readEvents = new LinkedList<>();
    private final LinkedList<WriteEvent> writeEvents = new LinkedList<>();

    static class Event{

    }

    static class AcceptEvent extends Event {
        public ServerSocketChannel channel;

        public AcceptEvent(ServerSocketChannel channel) {
            this.channel = channel;
        }
    }

    static class ReadEvent extends Event {
        public SocketChannel channel;

        public ReadEvent(SocketChannel channel) {
            this.channel = channel;
        }
    }

    static class WriteEvent extends Event {
        public ByteBuffer buf;
        public SocketChannel channel;

        public WriteEvent(ByteBuffer buf, SocketChannel channel) {
            this.buf = buf;
            this.channel = channel;
        }
    }

    public Test() throws IOException {
        var serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress("127.0.0.1", 8005));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        for(;;){
            int nReady = selector.select();
            var selectKeys = selector.selectedKeys();
            for(var key : selectKeys){
                if(key.isAcceptable()){
                    acceptEvents.add(new AcceptEvent((ServerSocketChannel) key.channel()));
                }
                if(key.isReadable()){
                    readEvents.add(new ReadEvent((SocketChannel) key.channel()));
                }
            }
            selectKeys.clear();

            acceptEventHandler();
            readEventHandler();
            writeEventHandler();
        }
    }

    void acceptEventHandler() throws IOException {
        var iterator = acceptEvents.iterator();
        while(iterator.hasNext()){
            var event = iterator.next();
            var client = event.channel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            iterator.remove();
        }
    }

    void readEventHandler() throws IOException {
        var iterator = readEvents.iterator();
        while(iterator.hasNext()){
            var event = iterator.next();
            var channel = event.channel;

            var byteBuffer = ByteBuffer.allocate(1<<10);
            channel.read(byteBuffer);
            byteBuffer.flip();
            writeEvents.add(new WriteEvent(byteBuffer, channel));

            iterator.remove();
        }
    }

    void writeEventHandler() throws IOException {
        var iterator = writeEvents.iterator();
        while(iterator.hasNext()){
            var event = iterator.next();
            var channel = event.channel;
            var buf = event.buf;

            channel.write(buf);

            iterator.remove();
        }
    }


    public static void main(String[] args) throws IOException {
        new Test();
    }
}






