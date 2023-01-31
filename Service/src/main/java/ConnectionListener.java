public interface ConnectionListener {

    void connectionReady(Connection connection);

    void receivedString(Connection connection, String string);

    void disconnect(Connection connection);

    void receivedException(Connection connection, Exception e);

}
