import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame implements ConnectionListener, ActionListener {

    private static final String IP = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }

    private final JTextArea textArea = new JTextArea();
    private final JTextField fieldMsg = new JTextField();
    private final JTextField fieldName = new JTextField("Введите имя");
    private Connection connection;

    private Client() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрытие нажатием на крестик
        setSize(WIDTH, HEIGHT); // ширина и высота окна
        setLocationRelativeTo(null); //окно всегда посередине
        setAlwaysOnTop(true); // поверх всех окон


        textArea.setEditable(false); //запрет редактирования
        textArea.setLineWrap(true); //перевод на новую строку
        add(textArea, BorderLayout.CENTER);


        add(fieldMsg, BorderLayout.SOUTH);
        fieldMsg.addActionListener(this);


        add(fieldName, BorderLayout.NORTH);

        setVisible(true);
        try {
            connection = new Connection(this, new Socket(IP, PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void connectionReady(Connection connection) {
        printMsg("Connection ready!");
    }

    @Override
    public void receivedString(Connection connection, String string) {
        printMsg(string);
    }

    @Override
    public void disconnect(Connection connection) {
        printMsg("Connection close!");
    }

    @Override
    public void receivedException(Connection connection, Exception e) {
        printMsg("Connection exception: " + e);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = fieldMsg.getText();
        if (msg.equals("")) {
            return;
        }
        fieldMsg.setText(null);
        connection.sendString(fieldName.getText() + ": " + msg);

    }

    private synchronized void printMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            textArea.append(msg + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}
