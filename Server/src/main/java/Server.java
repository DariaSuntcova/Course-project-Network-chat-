import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {
    private final List<Connection> connections = new ArrayList<>();


    public static void main(String[] args) {

        new Server();
    }

    private Server() {
        System.out.println("Server running!");

        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());

                } catch (IOException e) {
                    System.out.println("Connection exception: " + e);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void connectionReady(Connection connection) {
        connections.add(connection);
        sendToAllClient("New client connected: " + connection);
    }

    @Override
    public synchronized void receivedString(Connection connection, String string) {
        sendToAllClient(string);
    }

    @Override
    public synchronized void disconnect(Connection connection) {
        connections.remove(connection);
        sendToAllClient("Client disconnected: " + connection);
    }

    @Override
    public synchronized void receivedException(Connection connection, Exception e) {
        System.out.println("Connection exception: " + e);
    }

    public void sendToAllClient(String msg) {
        System.out.println(msg);
        for (Connection connection : connections) {
            connection.sendString(msg);
        }
    }
}
