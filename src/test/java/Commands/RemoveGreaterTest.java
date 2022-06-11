package Commands;

import Movie.*;
import Xml.Xml;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Hashtable;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RemoveGreaterTest {

    @Test
    void execute() throws Exception{
        HashtableInfo hashtableInfo = Xml.fromXml(new File("Lab6_dop.xml"));
        Hashtable<String, Movie> collection = Objects.requireNonNull(hashtableInfo).getCollection();
        String[] key = new String[1];
        key[0] = "key1";
        RemoveGreater removeGreater = new RemoveGreater(key);

        String expected = "All necessary elements removed successfully.";
        String actual = removeGreater.execute(collection).getText();

        assertEquals(expected, actual);
    }
}