package Server;

public class ServerContainer {
    public static void main(String[] args) {
       Server server = new Server(8000);

       server.start();
       server.waitTerminate();
    }
}
