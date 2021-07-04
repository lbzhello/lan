package lan;

import lan.parser.CharIterator;
import lan.parser.impl.StringIterator;
import org.junit.jupiter.api.Test;

public class LanStartTest {
    @Test
    public void stringIteratorTest() {
        CharIterator iterator = StringIterator.string("def sql do hello end");
        System.out.println(iterator.current());
    }
}
