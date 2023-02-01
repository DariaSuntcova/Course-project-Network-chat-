import connection.Connection;
import connection.ConnectionListener;
import log.LogLvl;
import log.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements ConnectionListener {
    private final List<Connection> connections = new ArrayList<>();

    private final static Logger logger = new Logger("Server");


    public static void main(String[] args) {

        int port = 0;
        try (FileInputStream fin = new FileInputStream("D:\\Online chat\\settings.txt")) {
            int i;
            while ((i = fin.read()) != -1) {
                port = i;
            }
        } catch (IOException ex) {
            logger.logging(LogLvl.ERROR, ex.getMessage());
            System.out.println(ex.getMessage());
        }

        new Server(port);
    }

    private Server(int port) {
        System.out.println("Server running!");
        logger.logging(LogLvl.INFO, "Server running!");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                try {
                    new Connection(this, serverSocket.accept());

                } catch (IOException e) {
                    logger.logging(LogLvl.ERROR, "Connection exception: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.logging(LogLvl.ERROR, "Connection exception: " + e.getMessage());
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
        logger.logging(LogLvl.ERROR, "Connection exception: " + e.getMessage());
        System.out.println("Connection exception: " + e);
    }

    public void sendToAllClient(String msg) {
        logger.logging(LogLvl.INFO, msg);
        System.out.println(msg);
        for (Connection connection : connections) {
            connection.sendString(msg);
        }
    }
}
