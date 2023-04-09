package com.liubaozhu.lan.core.lexer;

import java.text.CharacterIterator;

/**
 * 读取文本格式源代码
 * @see CharacterIterator
 */
public interface TextReader {
    char DONE = '\uFFFF';

    char LINE_FEED = '\n';

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
    int getLine();
}
