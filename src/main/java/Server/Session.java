package Server;

import java.io.IOException;
import java.net.Socket;

public class Session {
    private static int currId = 0;
    private int id;
    Socket socket;

    public Session(Socket socket){
        this.socket = socket;
        id = currId++;
    }

    public void send(String message){
        int len = message.length();
        byte[] buffer = new byte[2 + len];

        buffer[0] = (byte) 0x81; // last frame, text
        buffer[1] = 4; // not masked, length 3

        for (int i = 0; i < len; i++){
            buffer[i + 2] = (byte) message.charAt(i);
        }

        try {
            socket.getOutputStream().write(buffer, 0, buffer.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }
}
