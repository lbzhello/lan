package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.util.FileUtils;
import com.liubaozhu.lan.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 文本解析器
 */
public class LanParser implements CharIterator {
    private static final Logger logger = LoggerFactory.getLogger(LanParser.class);

    // 空迭代器
    public static final LanParser EMPTY_ITERATOR = text("");

    private final StringCharacterIterator iterator;

    // 原文档
    private String text;

    // 所处行
    private int line = 1;
    // 相对于当前行的位置
    private int linePos = 1;

    // 单词分割符
    private Set<Character> delimiters = new HashSet<>();

    private LanParser(String text) {
        this.text = text;
        this.iterator = new StringCharacterIterator(text);
    }

    /**
     * 根据 string 创建一个 iterator
     * @param text
     * @return
     */
    public static LanParser text(String text) {
        return new LanParser(text);
    }

    /**
     * 根据 file 路径创建一个 iterator
     * @return
     */
    public static LanParser file(String path) {
        if (StringUtils.isEmpty(path)) {
            return EMPTY_ITERATOR;
        }
        File file = new File(path);
        return file(file);
    }

    /**
     * 根据文件创建一个 iterator
     * @param file
     * @return
     */
    public static LanParser file(File file) {
        try {
            String text = FileUtils.toString(file);
            return text(text);
        } catch (IOException e) {
            logger.error("failed to read file", e);
            return EMPTY_ITERATOR;
        }
    }

    public boolean addDelimiter(char c) {
        return delimiters.add(c);
    }

    public void addDelimiters(Collection<Character> delimiters) {
        getDelimiters().addAll(delimiters);
    }

    private void removeDelimiter(Collection<Character> delimiters) {
        getDelimiters().removeAll(delimiters);
    }

    public Set<Character> getDelimiters() {
        return delimiters;
    }

    @Override
    public boolean hasNext() {
        return current() != DONE;
    }

    @Override
    public char current() {
        return iterator.current();
    }

    @Override
    public char next() {
        if (current() == LINE_BREAK) {
            line++;
        }
        return iterator.next();
    }

    @Override
    public char previous() {
        char previous = iterator.previous();
        if (previous == LINE_BREAK) {
            line--;
        }
        return previous;
    }

    /**
     * 获取当前所处位置，1-based
     * @return
     */
    @Override
    public int position() {
        return iterator.getIndex() + 1;
    }

    /**
     * 获取当前所处行
     * @return
     */
    @Override
    public int lineNumber() {
        return line;
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
     * 判断字符是否分割符（空白，特殊字符）
     * @param c
     * @return
     */
    public boolean isDelimiter(char c) {
        return delimiters.contains(c) || Character.isWhitespace(c);
    }

    /**
     * 判断当前字符是否分隔符
     * @return
     */
    public boolean isDelimiter() {
        return isDelimiter(current());
    }

    /**
     * 获取下一个 Token，非分隔符的连续字符串为一个 Token
     * @return
     */
    public String nextToken() {
        StringBuilder sb = new StringBuilder();
        while (!isDelimiter(current()) && hasNext()) {
            sb.append(current());
            next();
        }
        return sb.toString();
    }

    /**
     * 获取字符序列，直到遇到 c 字符为止
     * 最终返回的字符串不包括 c 字符
     * @return
     */
    public String nextUntil(char c) {
        StringBuilder sb = new StringBuilder();
        while (hasNext()) {
            if (current() == c) {
                break;
            }
            sb.append(current());
            next();
        }
        return sb.toString();
    }

    /**
     * 跳过空白字符
     * @return 当前指针指向字符
     */
    public char skipBlank() {
        while (Character.isWhitespace(current())) {
            next();
        }

        return current();
    }

    /**
     * 当前字符是否匹配
     * @param cs
     * @return
     */
    public boolean currentMatch(char... cs) {
        char current = current();
        for (char c : cs) {
            if (current == c) {
                return true;
            }
        }

        return false;
    }

    /**
     * 非换行字符的空白字符
     * @return
     */
    public boolean isBlankNotLineBreak() {
        return Character.isWhitespace(current()) && current() != LINE_BREAK;
    }

    /**
     * 查看前一个字符，不会改变 pos
     */
    public char lookPrevious() {
        char previous = iterator.previous();
        iterator.next();
        return previous;
    }

    /**
     * 查看当前位置开始的 num 个字符序列，不会移动 pos
     * @param num
     */
    public String lookNext(int num) {
        int p = iterator.getIndex();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(iterator.current());
            iterator.next();
        }
        iterator.setIndex(p);
        return sb.toString();
    }

}
