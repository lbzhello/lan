package lan.parser;

import java.text.CharacterIterator;

/**
 * 字符迭代器，用来解析文本
 * @see CharacterIterator
 */
public interface CharIterator {
    char DONE = '\uFFFF';

    char LINE_BREAK = '\n';

    char current();

    char next();

    char previous();

    boolean hasNext();

    /**
     * 获取当前所处位置，1-based
     * @return
     */
    int position();

    /**
     * 获取当前所处行
     * @return
     */
    int lineNumber();
}
