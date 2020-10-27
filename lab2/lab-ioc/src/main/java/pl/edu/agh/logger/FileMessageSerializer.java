package pl.edu.agh.logger;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class FileMessageSerializer implements IMessageSerializer {
    @Inject @Named("logFilename")
    private String filename;


    @Inject
    public FileMessageSerializer(String filename) {
        this.filename = filename;
    }

    @Override
    public void serializeMessage(String message) {
        try (var output = new BufferedWriter(new FileWriter(filename, true))) {
            output.write(message + "\n");
            output.flush();
        } catch (IOException e) {
            System.err.println("FileMessageSerializer error: " + e.getMessage());
        }
    }
}
