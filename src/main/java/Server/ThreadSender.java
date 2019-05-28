package Server;

import java.io.IOException;
import java.io.OutputStream;

public class ThreadSender extends Thread {
    OutputStream outputStream;

    public ThreadSender(OutputStream outputStream){
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
        byte[] send = new byte[2 + 4];
        send[0] = (byte) 0x81; // last frame, text
        send[1] = 4; // not masked, length 3
        send[2] = 97;
        send[3] = 98;
        send[4] = 99;
        send[5] = 100;

        try {
            outputStream.write(send, 0, send.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
