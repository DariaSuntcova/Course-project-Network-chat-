import connection.Connection;
import connection.ConnectionListener;
import log.LogLvl;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends JFrame implements ConnectionListener, ActionListener {

    private static final String IP = "127.0.0.1";
    private static int port;
    private static String nameClient;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static Logger logger;

    public static void main(String[] args) {

        System.out.println("Hello! Enter your nickname to continue");
        try (Scanner scanner = new Scanner(System.in)) {
            nameClient = scanner.nextLine();
        }

        logger = new Logger(nameClient);

        try (FileInputStream fin = new FileInputStream("D:\\Online chat\\settings.txt")) {
            int port;
            while ((port = fin.read()) != -1) {
                Client.port = port;
            }
        } catch (IOException e) {
            logger.logging(LogLvl.ERROR, e.getMessage());
            System.out.println(e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new Client(port));
    }

    private final JTextArea textArea = new JTextArea();
    private final JTextField fieldMsg = new JTextField();
    private final JTextField fieldName = new JTextField(nameClient);
    private Connection connection;

    private Client(int port) {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // закрытие нажатием на крестик
        setSize(WIDTH, HEIGHT); // ширина и высота окна
        setLocationRelativeTo(null); //окно всегда посередине
        setAlwaysOnTop(true); // поверх всех окон


        textArea.setEditable(false); //запрет редактирования
        textArea.setLineWrap(true); //перевод на новую строку
        add(textArea, BorderLayout.CENTER);  //поле чата по центру


        add(fieldMsg, BorderLayout.SOUTH); // поле ввода нового сообщения внизу
        fieldMsg.addActionListener(this);


        add(fieldName, BorderLayout.NORTH); // поле имени вверху

        setVisible(true); // видимость
        try {
            connection = new Connection(this, new Socket(IP, port));
        } catch (IOException e) {
            logger.logging(LogLvl.ERROR, e.getMessage());
            e.printStackTrace();


        }


    }

    @Override
    public void connectionReady(Connection connection) {
        printMsg("Connection ready!", LogLvl.INFO);
    }

    @Override
    public void receivedString(Connection connection, String string) {
        if (string.equals("exit")) {
            connection.disconnect();
        } else {
            printMsg(string, LogLvl.INFO);
        }
    }

    @Override
    public void disconnect(Connection connection) {
        printMsg("Connection close!", LogLvl.ERROR);
    }

    @Override
    public void receivedException(Connection connection, Exception e) {
        printMsg("Connection exception: " + e, LogLvl.ERROR);
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

    private synchronized void printMsg(String msg, LogLvl lvl) {
        SwingUtilities.invokeLater(() -> {
            logger.logging(lvl, msg);
            textArea.append(msg + "\n");
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}
