package controller;


import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import model.Gallery;
import model.Photo;
import util.PhotoDownloader;

public class GalleryController {

    @FXML
    public TextField searchTextField;

    @FXML
    public TextField imageNameField;

    @FXML
    public ImageView imageView;

    @FXML
    public ListView<Photo> imagesListView;

    private Gallery galleryModel;

    @FXML
    public void initialize() {
        imagesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Photo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ImageView photoIcon = new ImageView(item.getPhotoData());
                    photoIcon.setPreserveRatio(true);
                    photoIcon.setFitHeight(50);
                    setGraphic(photoIcon);
                }
            }
        });

        imagesListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener(((observable, oldValue, newValue) -> {
                    if(oldValue != null){
                        imageNameField.textProperty().unbindBidirectional(oldValue.nameProperty());
                    }
                    bindSelectedPhoto(newValue);
                }));
    }

    public void setModel(Gallery gallery) {
        this.galleryModel = gallery;
        imagesListView.setItems(gallery.getPhotos());
        imagesListView.getSelectionModel().select(0);
    }

    private void bindSelectedPhoto(Photo selectedPhoto) {
        if(selectedPhoto != null){
            imageView.imageProperty().bind(selectedPhoto.photoDataProperty());
            imageNameField.textProperty().bindBidirectional(selectedPhoto.nameProperty());
        }
    }

    public void searchButtonClicked(ActionEvent event) {
        var text = searchTextField.getText();
        if(text.isEmpty()){
            return;
        }
        var downloader = new PhotoDownloader();
        galleryModel.clear();

        downloader.searchForPhotos(text)
                .subscribeOn(Schedulers.io())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(galleryModel::addPhoto);
    }
}

