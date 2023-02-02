package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class Logger {
    private final String path;


    public Logger(String name) {
        this.path = name + "Log.txt";
        File logFile = new File(path);
        if (!logFile.exists()) {
            try {
                if (logFile.createNewFile()) {
                    System.out.println("New log file created!");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void logging(LogLvl lvl, String msg) {
        try(FileWriter writer = new FileWriter(path ,true))
        {
            String text = "[" + lvl + "] " + LocalDateTime.now() + " === " + msg;
            writer.write(text);
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
