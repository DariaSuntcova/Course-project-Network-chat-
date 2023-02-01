import log.LogLvl;
import log.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;

public class LoggerTest {
    String path = "D:\\Online chat\\Logger\\TestLog.txt";
    String name = "Test";


    @ParameterizedTest
    @CsvSource({
            "true", // создается новый файл
            "true"} // файл уже есть
    )
    public void shouldLogger(boolean expected) {
        Logger logger = new Logger(name);
        File logFile = new File(path);
        Assertions.assertEquals(expected, logFile.exists());
    }

    @Test
    public void shouldLogging() {
        Logger logger = new Logger(name);
        logger.logging(LogLvl.INFO, "Test logger");
        String expected = "Test logger";
        String lvl = "INFO";

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String actual = reader.readLine();
            Assertions.assertTrue(actual.contains(expected) && actual.contains(lvl));
        } catch (IOException e) {
            throw new RuntimeException(e);

        }
    }
}
