package connection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Connection {
    private final Socket socket;
    private final Thread thread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final ConnectionListener listener;

    public Connection(ConnectionListener listener, Socket socket) throws IOException {
        this.listener = listener;
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    listener.connectionReady(Connection.this);
                    while (!thread.isInterrupted()) {
                        listener.receivedString(Connection.this, in.readLine());

                    }
                } catch (IOException e) {
                    listener.receivedException(Connection.this, e);
                } finally {
                    listener.disconnect(Connection.this);
                }
            }
        });
        thread.start();
    }

    public synchronized void sendString(String string) {
        try {
            out.write(string + "\r\n");
            out.flush();
        } catch (IOException e) {
            listener.receivedException(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect() {
        thread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            listener.receivedException(Connection.this, e);
        }
    }

    @Override
    public String toString() {
        return "connection.Connection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
