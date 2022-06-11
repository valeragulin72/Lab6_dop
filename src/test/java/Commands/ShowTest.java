package Commands;

import Movie.HashtableInfo;
import Movie.Movie;
import Xml.Xml;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Hashtable;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ShowTest {

    @Test
    void execute() throws Exception{
        HashtableInfo hashtableInfo = Xml.fromXml(new File("Lab6_dop.xml"));
        Hashtable<String, Movie> collection = Objects.requireNonNull(hashtableInfo).getCollection();
        Show show = new Show();

        String expected = "Total: 3" +
        "\nCollection's elements: \n" +
        "key3 = 7693, Oliver!, 123.0 76.0, 2022-04-24T19:48:32.678, 1, 1, musical, G, Vernon Harris, 1905-02-26, brown, brown, United Kingdom\n" +
        "key2 = 1, Parasites, 1.0 1.0, 2019-05-21T15:56:20.547, 4, 1, comedy, NC-17, Bong Joon Ho, 1969-09-14, brown, black, North Korea\n" +
        "key1 = 13, The Good, the Bad and the Ugly, 13.0 567.0, 1955-12-23T15:32:24.432, 1, 1, western, NC-17, Sergio Leone, 1929-01-03, brown, black, Italy\n" + "\n";

        String actual = show.execute(collection).getText();

        assertEquals(expected, actual);
    }
}