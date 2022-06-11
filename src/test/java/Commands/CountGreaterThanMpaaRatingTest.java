package Commands;

import Movie.HashtableInfo;
import Movie.Movie;
import Xml.Xml;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Hashtable;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CountGreaterThanMpaaRatingTest {

    @Test
    void execute() throws Exception{
        HashtableInfo hashtableInfo = Xml.fromXml(new File("Lab6_dop.xml"));
        Hashtable<String, Movie> collection = Objects.requireNonNull(hashtableInfo).getCollection();
        String[] key = new String[1];
        key[0] = "G";
        CountGreaterThanMpaaRating countGreaterThanMpaaRating = new CountGreaterThanMpaaRating(key);

        String expected = "Count of movies with greater MPAA rating is: 2.";
        String actual = countGreaterThanMpaaRating.execute(collection).getText();

        assertEquals(expected, actual);
    }
}