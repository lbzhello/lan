package lan;

import lan.parser.CharIterator;
import lan.parser.TextParser;
import org.junit.jupiter.api.Test;

public class LanStartTest {
    @Test
    public void keywordTest() {

    }

    @Test
    public void stringIteratorTest() {
        CharIterator iterator = TextParser.text("def sql do hello end");
        System.out.println(iterator.current());
    }
}
