package Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Endpoint extends Thread {
    private static Set<Endpoint> endpoints = new CopyOnWriteArraySet<>();
    private static HashMap<Integer, String> users = new HashMap<>();
    Socket socket;
    Session session;

    public Endpoint(Socket socket) {
        this.socket = socket;
        session = new Session(socket);
        users.put(session.getId(), "");
        endpoints.add(this);
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true){
            int len = -1;

            try {
                if((len = inputStream.available()) > 0) {
                    byte[] buffer = new byte[len];

                    inputStream.read(buffer, 0, len);

                    String msg = decode(buffer, len);

                    if (users.get(session.getId()).length() == 0) {
                        users.put(session.getId(), msg);
                        System.out.println("new user: " + users.get(session.getId()));
                    } else {
                        endpoints.forEach(endpoint -> {
                            endpoint.session.send(msg);
                        });
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String decode(byte[] buffer, int len) {
        byte[] key = Arrays.copyOfRange(buffer, 2, 6);
        byte[] encoded = Arrays.copyOfRange(buffer, 6, len);

        String result = "";

        for (int i = 0; i < encoded.length; i++) {
            result += (char) (encoded[i] ^ key[i & 0x3]);
        }

        return result;
    }
}
