package Commands;

import Movie.HashtableInfo;
import Movie.Movie;
import Xml.Xml;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Hashtable;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class SumOfOscarsCountTest {

    @Test
    void execute() throws Exception{
        HashtableInfo hashtableInfo = Xml.fromXml(new File("Lab6_dop.xml"));
        Hashtable<String, Movie> collection = Objects.requireNonNull(hashtableInfo).getCollection();
        SumOfOscarsCount sumOfOscarsCount = new SumOfOscarsCount();

        String expected = "Total Oscar's count: 6.";
        String actual = sumOfOscarsCount.execute(collection).getText();

        assertEquals(expected, actual);
    }
}