package model;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;


public class Photo {

    private StringProperty name = new SimpleStringProperty(this, "name");

    private final ObjectProperty<Image> photoData;

    public Photo(String extension, byte[] photoData) {
        this.photoData = new SimpleObjectProperty<>(new Image(new ByteArrayInputStream((photoData))));
        this.name.set(UUID.randomUUID().toString() + "." + extension);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty(){
        return name;
    }

    public Image getPhotoData() {
        return photoData.get();
    }

    public void setPhotoData(Image image){
        photoData.set(image);
    }

    public ObjectProperty<Image> photoDataProperty(){
        return photoData;
    }
}
