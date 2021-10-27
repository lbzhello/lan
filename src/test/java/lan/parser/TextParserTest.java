package lan.parser;

import org.junit.jupiter.api.Test;

public class TextParserTest {
    @Test
    public void textTest() {
        TextParser parser = TextParser.text("hello () nihao ma");
        System.out.println();
    }

    /**
     * \uFFFF 不是空白字符
     */
    @Test
    public void charTest() {
        System.out.println(Character.isWhitespace('\uFFFF'));
    }
}
