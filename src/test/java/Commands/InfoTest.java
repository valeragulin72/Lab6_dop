package Commands;

import Movie.HashtableInfo;
import Movie.Movie;
import Xml.Xml;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class InfoTest {

    @Test
    void execute() throws Exception{
        HashtableInfo hashtableInfo = Xml.fromXml(new File("Lab6_dop.xml"));
        Hashtable<String, Movie> collection = Objects.requireNonNull(hashtableInfo).getCollection();
        LocalDateTime initDate = LocalDateTime.now();
        Info info = new Info();

        String expected = "Information about the collection: \n" +
                "Type: " + Movie.class.getName() + "\n" +
                "Initialization date: " + initDate + "\n" +
                "Amount of elements: 3" + "\n";
        String actual = info.execute(collection, initDate).getText();

        assertEquals(expected, actual);
    }
}