package com.liubaozhu.lan.core.lexer;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

/**
 * 读取文本格式源代码，提供基本字符操作
 * @see CharacterIterator
 */
public class TextReader {
    public static char DONE = '\uFFFF';

    public static char LINE_FEED = '\n';

    // 所处行
    private int line = 1;
    // 相对于当前行的位置
    private int column = 1;

    private StringCharacterIterator iterator;

    public TextReader(String text) {
        this.iterator = new StringCharacterIterator(text);
    }

    public char current() {
        return iterator.current();
    }

    public char next() {
        if (current() == LINE_FEED) {
            line++;
            column = 1;
        } else {
            column++;
        }
        return iterator.next();
    }

    public boolean hasNext() {
        return current() != DONE;
    }

    /**
     * 判断当前字符是否为 c
     * @param c
     * @return
     */
    public boolean currentIs(char c) {
        return current() == c;
    }

    /**
     * 判断当前字符不是 c
     * @param c
     * @return
     */
    public boolean currentNot(char c) {
        return !currentIs(c);
    }

    /**
     * 获取当前所处行，1-based
     * @return
     */
    public int getLine() {
        return line;
    }

    /**
     * 获取当前所处列，1-based
     * @return
     */
    public int getColumn() {
        return column;
    }

    public int getIndex() {
        return iterator.getIndex();
    }

    public void setIndex(int p) {
        iterator.setIndex(p);
    }

    /**
     * 字符流结束
     * @return
     */
    public boolean isEOF() {
        return !hasNext();
    }

    /**
     * 备份当前解析位置
     * @return
     */
    public Snapshot snapshot() {
        return new Snapshot(getIndex(), line, column);
    }

    /**
     * 恢复解析位置至快照
     * @param snapshot
     */
    public void restore(Snapshot snapshot) {
        setIndex(snapshot.index);
        this.line = snapshot().line;
        this.column = snapshot.column;
    }

    /**
     * beifa
     */
    public static class Snapshot {
        private int index = 0;
        // 所处行
        private int line = 1;
        // 相对于当前行的位置
        private int column = 1;

        public Snapshot(int index, int line, int column) {
            this.index = index;
            this.line = line;
            this.column = column;
        }

    }
}
