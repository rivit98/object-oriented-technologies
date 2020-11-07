import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.observables.ConnectableObservable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.Test;
import util.Color;

import java.io.FileNotFoundException;
import java.util.concurrent.TimeUnit;

import static util.ColorUtil.print;
import static util.ColorUtil.printThread;

public class RxTests {
    private static final String MOVIES1_DB = "movies1";
    private static final String MOVIES2_DB = "movies2";
    private static MovieReader movieReader = new MovieReader();

    /**
     * Example 1: Creating and subscribing observable from iterable.
     */
    @Test
    public void loadMoviesAsList() throws FileNotFoundException {
        movieReader.getMoviesFromList(MOVIES1_DB)
                .subscribe(movie -> print(movie, Color.GREEN));
    }

    /**
     * Example 2: Creating and subscribing observable from custom emitter.
     */
    @Test
    public void loadMoviesAsStream() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .subscribe(movie -> print(movie, Color.GREEN));
    }

    /**
     * Example 3: Handling errors.
     */
    @Test
    public void loadMoviesAsStreamAndHandleError() {
        movieReader.getMoviesAsStream("asdf")
                .subscribe(
                        movie -> print(movie, Color.GREEN),
                        error -> System.out.println("error")
                );
    }

    /**
     * Example 4: Signaling end of a stream.
     */
    @Test
    public void loadMoviesAsStreamAndFinishWithMessage() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .subscribe(
                        movie -> print(movie, Color.GREEN),
                        error -> System.out.println("error"),
                        () -> System.out.println("onComplete")
                );
    }

    /**
     * Example 5: Filtering stream data.
     */
    @Test
    public void displayLongMovies() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .filter(movie -> movie.getLength() > 60)
                .subscribe(
                        movie -> print(movie, Color.GREEN),
                        error -> System.out.println("error"),
                        () -> System.out.println("onComplete")
                );
    }

    /**
     * Example 6: Transforming stream data.
     */
    @Test
    public void displaySortedMoviesTitles() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .map(Movie::getTitle)
                .sorted()
                .take(10)
                .subscribe(
                        movie -> print(movie, Color.GREEN)
                );
    }

    /**
     * Example 7: Monads are like burritos.
     */
    @Test
    public void displayActorsForMovies() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .map(movie -> movieReader.readActors(movie))
                .flatMap(Observable::fromIterable)
                .distinct()
                .sorted()
                .subscribe(
                        movie -> print(movie, Color.GREEN)
                );
    }

    /**
     * Example 8: Combining observables.
     */
    @Test
    public void loadMoviesFromManySources() {
        Observable<Movie> movies1 = movieReader.getMoviesAsStream(MOVIES1_DB)
                .doOnNext(movie -> print(movie, Color.GREEN));

        Observable<Movie> movies2 = movieReader.getMoviesAsStream(MOVIES2_DB)
                .doOnNext(movie -> print(movie, Color.BLUE));

        Observable.merge(movies1, movies2)
                .subscribe(movie -> print("RECEIVED: " + movie, Color.RED));

    }

    /**
     * Example 9: Playing with threads (subscribeOn).
     */
    @Test
    public void loadMoviesInBackground() throws InterruptedException {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(movie -> printThread(movie.getIndex(), Color.BLUE))
                .subscribe(movie -> printThread(movie.getIndex(), Color.GREEN));
        printThread("po wszystkim", Color.RED);
        Thread.sleep(10000);
    }

    /**
     * Example 10: Playing with threads (observeOn).
     */
    @Test
    public void switchThreadsDuringMoviesProcessing() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .take(10)
                .subscribeOn(Schedulers.newThread())
                .doOnNext(movie -> printThread(movie.getIndex(), Color.RED))
                .observeOn(Schedulers.newThread())
                .doOnNext(movie -> printThread(movie.getIndex(), Color.BLUE))
                .blockingSubscribe(movie -> printThread(movie.getIndex(), Color.GREEN));
    }

    /**
     * Example 11: Combining parallel streams.
     */
    @Test
    public void loadMoviesFromManySourcesParallel() throws InterruptedException {
        // Static merge solution
//
//        Observable<Movie> movies1 = movieReader.getMoviesAsStream(MOVIES1_DB)
//                .subscribeOn(Schedulers.io())
//                .doOnNext(movie -> print(movie, Color.GREEN));
//
//        Observable<Movie> movies2 = movieReader.getMoviesAsStream(MOVIES2_DB)
//                .subscribeOn(Schedulers.io())
//                .doOnNext(movie -> print(movie, Color.BLUE));
//
//        Observable.merge(movies1, movies2)
//                .subscribe(movie -> print("RECEIVED: " + movie, Color.RED));
//

        // FlatMap solution:
        final MovieDescriptor movie1Descriptor = new MovieDescriptor(MOVIES1_DB, Color.GREEN);
        final MovieDescriptor movie2Descriptor = new MovieDescriptor(MOVIES2_DB, Color.BLUE);

        Observable.just(movie1Descriptor, movie2Descriptor)
                .concatMap(db -> movieReader.getMoviesAsStream(db.movieDbFilename)
                        .doOnNext(movie -> print(movie, db.debugColor))
                        .subscribeOn(Schedulers.io()))
                .subscribe(movie -> print("RECEIVED: " + movie, Color.RED));

        Thread.sleep(10000);
    }

    /**
     * Example 12: Zip operator.
     */
    @Test
    public void loadMoviesWithDelay() {
        MovieReader movieReader = new MovieReader();

        Observable<Movie> moviesStream = movieReader.getMoviesAsStream(MOVIES1_DB)
                .subscribeOn(Schedulers.newThread());
        Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);

        Observable.zip(moviesStream, interval, (movie, i) -> movie)
                .blockingSubscribe(movie -> print(movie, Color.GREEN));
    }

    /**
     * Example 13: Backpressure.
     */
    @Test
    public void trackMoviesLoadingWithBackpressure() {
        movieReader.getMoviesAsStream(MOVIES1_DB)
                .doOnNext(movie -> print(movie, Color.RED))
                .doOnNext(movie -> Thread.sleep(10))
                .subscribeOn(Schedulers.newThread())
                .toFlowable(BackpressureStrategy.DROP) // <--- comment this line and see how result changes
                .observeOn(Schedulers.io(), true, 1)
                .doOnNext(this::displayProgress)
                .blockingSubscribe();
    }

    /**
     * Example 14: Cold and hot observables.
     */
    @Test
    public void oneMovieStreamManyDifferentSubscribers() {
        // cold observable
        final Observable<Movie> movieObservable = movieReader.getMoviesAsStream(MOVIES1_DB);

        // hot observable
        final ConnectableObservable<Movie> movieConnectableObservable = movieObservable.publish();

        movieConnectableObservable
                .take(10)
                .subscribe(movie -> print(movie, Color.RED));

        movieConnectableObservable
                .filter(movie -> movie.getRating().equals("G"))
                .subscribe(movie -> print(movie, Color.BLUE));

        movieConnectableObservable.connect();
    }

    /**
     * Example 15: Caching observables (hot-cold hybrid).
     */
    @Test
    public void cacheMoviesInfo() {
        final Observable<Movie> movieObservable = movieReader.getMoviesAsStream(MOVIES1_DB).cache();

        // reading 1-st time (from file)
        movieObservable.subscribe(movie -> print(movie, Color.RED));

        // emit cached values once again (no reading from file)
        System.out.println(movieObservable.count().blockingGet());
    }

    private void displayProgress(Movie movie) throws InterruptedException {
        print((movie.getIndex() / 500.0 * 100) + "%", Color.GREEN);
        Thread.sleep(50);
    }

    private class MovieDescriptor {
        private final String movieDbFilename;

        private final Color debugColor;

        private MovieDescriptor(String movieDbFilename, Color debugColor) {
            this.movieDbFilename = movieDbFilename;
            this.debugColor = debugColor;
        }

        public Color getDebugColor() {
            return debugColor;
        }

        public String getMovieDbFilename() {
            return movieDbFilename;
        }
    }
}
