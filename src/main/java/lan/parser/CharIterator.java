package lan.parser;

import java.text.CharacterIterator;

/**
 * 字符迭代器，用来解析文本
 * @see CharacterIterator
 */
public interface CharIterator {
    char DONE = '\uFFFF';

    /**
     * 设置源文件来源
     * @param path
     */
    default void setSource(String path) {

    }

    // template method
    default void refresh() {

    }

    boolean hasNext();

    char current();

    char next();

    char previous();

    /**
     * 获取当前所处位置，1-based
     * @return
     */
    int position();
}
