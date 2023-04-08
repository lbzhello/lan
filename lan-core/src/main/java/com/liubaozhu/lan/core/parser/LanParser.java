package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.util.FileUtils;
import com.liubaozhu.lan.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.StringCharacterIterator;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 文本解析器
 */
public class LanParser implements CharIterator {
    private static final Logger logger = LoggerFactory.getLogger(LanParser.class);

    // 最大预取数量，有时候需要查看下几个字符来判断语法行为
    // 这个值应该大于关键子长度
    public static final int MAX_PREFETCH_SIZE = 32;

    // 空迭代器
    public static final LanParser EMPTY_ITERATOR = text("");

    private final StringCharacterIterator iterator;

    // 原文档
    private String text;

    // 所处行
    private int line = 1;
    // 相对于当前行的位置
    private int column = 1;

    // 单词分割符，用来分割 token
    private Set<Character> delimiters = Set.of('=', '(', ')', '{', '}', '[', ']', '<', '>',
            ';', ',', '.', ':', '\\', '\'', '"', '`',
            '+', '-', '*', '/', '%', '&', '|', '!', '^', '~',
            '@', '#'
    );

    // 可组合分隔符，有些分割符能够组合成合法 token，通常是运算符或关键字，例如：+, -, ==, ++, +=, &&
    private Set<Character> composableDelimiters = Set.of('=', '<', '>', ':',
            '+', '-', '*', '/', '%', '&', '|', '!', '^', '~'
    );

    private LanParser(String text) {
        init();
        this.text = text;
        this.iterator = new StringCharacterIterator(text);
    }

    private void init() {

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

    public Set<Character> getDelimiters() {
        return delimiters;
    }

    public void setDelimiters(Set<Character> delimiters) {
        this.delimiters = delimiters;
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
        if (current() == LINE_FEED) {
            line++;
            column = 1;
        } else {
            column++;
        }
        return iterator.next();
    }

    @Override
    public char previous() {
        char previous = iterator.previous();
        if (previous == LINE_FEED) {
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

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    /**
     * 字符流结束
     * @return
     */
    public boolean isEOF() {
        return !hasNext();
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
        return Character.isWhitespace(current()) && current() != LINE_FEED;
    }

    /**
     * 查看前一个字符，不会改变 pos
     */
    public char lookPrevious() {
        char previous = previous();
        next();
        return previous;
    }

    /**
     * 部分情况需要预读下一个 token 来确定语法行为，这里用于恢复至预读前的数据
     */
    
    /**
     * 预读当前位置开始的 num 个字符序列，
     * 如果字符序列和 expect 相等则移动，否则回退到原位置
     * @return true 预读成功；
     *              预读失败，回退原位置
     */
    public boolean prefetchNext(int num, String expect) {
        // 记录当前信息
        int p = iterator.getIndex();
        int ln = line;
        int col = column;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append(current());
            next();
        }
        String str = sb.toString();
        if (str.equals(expect)) {
            return true;
        }

        // 不相等，回退到原来位置
        iterator.setIndex(p);
        line = ln;
        column = col;
        return false;
    }

    /**
     * 预取下一个合法 token，判断是否为运算符或关键字
     * @param checker
     * @return
     */
    public String prefetchNextToken(Predicate<String> checker) {
        Predicate<Character> collector= null;
        if (composableDelimiters.contains(current())) { // 分隔符组成的 token，一般用来判断运算符，例如 ==, ++, +=
            collector = c -> composableDelimiters.contains(c);
        } else { // 非分割符组成的 token，一般用来判断关键字，例如：if else in not
            collector = c -> !isDelimiter(c);
        }

        return prefetchNextToken(checker, collector);
    }

    /**
     * 预取下一个合法 token，判断是否为运算符或关键字
     * 一般由分隔符 {@link #delimiters} 分割，或者由其组成
     * 如果没通过 {@param checker} 验证，则回退到原位置
     * @param checker 检查下一个 token 是否匹配，一般是一个关键字或运算符;
     * @param collector 收集合法字符组成 token
     * @return 如果通过 {@param checker} 验证，则返回下一步获取的 token；
     *         如果没通过验证，则返回 null，并回退至原位置。
     */
    public String prefetchNextToken(Predicate<String> checker, Predicate<Character> collector) {
        // 记录当前信息
        int p = iterator.getIndex();
        int ln = line;
        int col = column;

        int len = 0;

        StringBuilder sb = new StringBuilder();
        while (collector.test(current()) && hasNext() && len < MAX_PREFETCH_SIZE) {
            len++;
            sb.append(current());
            next();
        }
        String token = sb.toString();
        if (checker.test(token)) {
            return token;
        }

        // 回退到原来位置
        iterator.setIndex(p);
        line = ln;
        column = col;
        return null;
    }

}
