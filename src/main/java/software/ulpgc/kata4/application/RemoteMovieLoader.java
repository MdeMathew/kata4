package software.ulpgc.kata4.application;

import software.ulpgc.kata4.io.MovieLoader;
import software.ulpgc.kata4.model.Movie;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;

public class RemoteMovieLoader implements MovieLoader {

    private final Function<String, Movie> deserialize;

    public RemoteMovieLoader(Function<String, Movie> deserialize) {this.deserialize = deserialize;}

    @Override
    public List<Movie> loadAll() {
        try {
            return loadFrom(new URL("https://datasets.imdbws.com/title.basics.tsv.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Movie> loadFrom(URL url) throws IOException {
        return loadFrom(url.openConnection());
    }

    private List<Movie> loadFrom(URLConnection urlConnection) throws IOException {
        try (InputStream is = urlConnection.getInputStream()) {
            return loadFrom(unzip(is));
        }
    }

    private List<Movie> loadFrom(InputStream unzip) throws IOException {
        return loadFrom(toReader(unzip));
    }

    private List<Movie> loadFrom(BufferedReader reader) throws IOException {
        List<Movie> movies = new ArrayList<>();
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            movies.add(deserialize.apply(line));
        }
        return movies;
    }

    private BufferedReader toReader(InputStream unzip) {
        return new BufferedReader(new InputStreamReader(unzip));
    }

    private InputStream unzip(InputStream is) throws IOException {
        return new GZIPInputStream(new BufferedInputStream(is));
    }
}

