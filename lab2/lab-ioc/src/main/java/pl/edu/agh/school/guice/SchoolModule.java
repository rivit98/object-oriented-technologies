package pl.edu.agh.school.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import pl.edu.agh.logger.ConsoleMessageSerializer;
import pl.edu.agh.logger.FileMessageSerializer;
import pl.edu.agh.logger.IMessageSerializer;
import pl.edu.agh.logger.Logger;
import pl.edu.agh.school.persistence.IPersistenceManager;
import pl.edu.agh.school.persistence.SerializablePersistenceManager;

public class SchoolModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPersistenceManager.class).to(SerializablePersistenceManager.class);
        bind(String.class).annotatedWith(Names.named("teachersStorage")).toInstance("teachers.dat");
        bind(String.class).annotatedWith(Names.named("classStorage")).toInstance("classes.dat");

        bind(Logger.class).in(Singleton.class);
        Multibinder<IMessageSerializer> multibinder = Multibinder.newSetBinder(binder(), IMessageSerializer.class);
        multibinder.addBinding().to(ConsoleMessageSerializer.class);
        multibinder.addBinding().to(FileMessageSerializer.class);

        bind(String.class).annotatedWith(Names.named("logFilename")).toInstance("persistence.log");
    }
}
