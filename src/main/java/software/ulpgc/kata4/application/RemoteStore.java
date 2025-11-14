package software.ulpgc.kata4.application;

import software.ulpgc.kata4.architecture.io.Store;
import software.ulpgc.kata4.architecture.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class RemoteStore implements Store {

    private final Function<String, Movie> deserialize;

    public RemoteStore(Function<String, Movie> deserialize) {this.deserialize = deserialize;}

    @Override
    public Stream<Movie> movies() {
        try {
            return loadFrom(new URL("https://datasets.imdbws.com/title.basics.tsv.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Stream<Movie> loadFrom(URL url) throws IOException {
        return loadFrom(url.openConnection());
    }

    private Stream<Movie> loadFrom(URLConnection urlConnection) throws IOException {
        return loadFrom(unzip(urlConnection.getInputStream()));
    }

    private Stream<Movie> loadFrom(InputStream unzip) throws IOException {
        return loadFrom(toReader(unzip));
    }

    private Stream<Movie> loadFrom(BufferedReader reader) throws IOException {
        return reader.lines().skip(1).map(deserialize);
    }

    private BufferedReader toReader(InputStream unzip) {
        return new BufferedReader(new InputStreamReader(unzip));
    }

    private InputStream unzip(InputStream is) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(is));
    }
}

