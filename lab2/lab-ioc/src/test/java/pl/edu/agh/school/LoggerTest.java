package pl.edu.agh.school;

import com.google.inject.Guice;
import org.junit.jupiter.api.Test;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.guice.SchoolModule;

import static org.junit.jupiter.api.Assertions.assertSame;

public class LoggerTest {

    @Test
    public void loggerSingleton(){
        var injector = Guice.createInjector(new SchoolModule());
        var logger1 = injector.getInstance(Logger.class);
        var logger2 = injector.getInstance(Logger.class);



        assertSame(logger1, logger2);
    }
}
