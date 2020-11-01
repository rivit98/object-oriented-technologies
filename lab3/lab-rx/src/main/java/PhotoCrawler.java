import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import model.Photo;
import model.PhotoSize;
import util.PhotoDownloader;
import util.PhotoProcessor;
import util.PhotoSerializer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PhotoCrawler {

    private static final Logger log = Logger.getLogger(PhotoCrawler.class.getName());

    private final PhotoDownloader photoDownloader;

    private final PhotoSerializer photoSerializer;

    private final PhotoProcessor photoProcessor;

    public PhotoCrawler() throws IOException {
        this.photoDownloader = new PhotoDownloader();
        this.photoSerializer = new PhotoSerializer("./photos");
        this.photoProcessor = new PhotoProcessor();
    }

    public void resetLibrary() throws IOException {
        photoSerializer.deleteLibraryContents();
    }

    public void downloadPhotoExamples() {
        try {
            var downloadedExamples =
                    photoDownloader
                            .getPhotoExamples()
                            .compose(this::processPhotos);

            downloadedExamples
                    .subscribe(photoSerializer::savePhoto);

        } catch (IOException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForQuery(String query) throws IOException {
        try {
            var downloadedExamples =
                    photoDownloader
                            .searchForPhotos(query)
                            .compose(this::processPhotos);

            downloadedExamples
                    .subscribe(photoSerializer::savePhoto);

        } catch (IOException | InterruptedException e) {
            log.log(Level.SEVERE, "Downloading photo examples error", e);
        }
    }

    public void downloadPhotosForMultipleQueries(List<String> topics) {
        var downloadedExamples =
                photoDownloader
                        .searchForPhotos(topics)
                        .compose(this::processPhotos);

        downloadedExamples
                .subscribe(photoSerializer::savePhoto);
    }

    public Observable<Photo> processPhotos(Observable<Photo> observable){
        return observable.filter(photoProcessor::isPhotoValid).map(photoProcessor::convertToMiniature);
    }

    public void downloadGrouping(List<String> topics) {
        var downloadedExamples =
                photoDownloader
                        .searchForPhotos(topics)
                        .filter(photoProcessor::isPhotoValid)
                        .groupBy(PhotoSize::resolve)
//                        .flatMap(grouped -> grouped.buffer(5, TimeUnit.SECONDS));
                        .flatMap(grouped -> {
                            if(grouped.getKey() == PhotoSize.MEDIUM){
                                return grouped
                                        .buffer(5, TimeUnit.SECONDS)
                                        .flatMapIterable(l -> l);
                            }

                            return grouped
                                    .observeOn(Schedulers.computation())
                                    .map(photoProcessor::convertToMiniature);
                        });

        downloadedExamples.subscribe(photoSerializer::savePhoto);
    }
}
