package com.liubaozhu.lan.core.lexer;

import com.liubaozhu.lan.core.util.FileUtils;
import com.liubaozhu.lan.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.function.Predicate;

/**
 * 词法分析器
 */
public class LanLexer extends TextReader {
    private static final Logger logger = LoggerFactory.getLogger(LanLexer.class);

    // 最大预取数量，有时候需要查看下几个字符来判断语法行为
    // 这个值应该大于关键子长度
    public static final int MAX_PREFETCH_SIZE = 32;

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

    private LanLexer(String text) {
        super(text);
    }

    /**
     * 根据 string 创建一个 iterator
     * @param text
     * @return
     */
    public static LanLexer text(String text) {
        return new LanLexer(text);
    }

    /**
     * 根据 file 路径创建一个 iterator
     * @return
     */
    public static LanLexer file(String path) throws IOException {
        if (StringUtils.isEmpty(path)) {
            return text("");
        }
        File file = new File(path);
        return file(file);
    }

    /**
     * 根据文件创建一个 iterator
     * @param file
     * @return
     */
    public static LanLexer file(File file) throws IOException {
        try {
            String text = FileUtils.toString(file);
            return text(text);
        } catch (IOException e) {
            logger.error("failed to read file", e);
            throw e;
        }
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
     * 是否结束换行符
     * @return
     */
    public boolean isLineFeed() {
        return currentIs('\n');
    }

    /**
     * 跳过空格后是否行结束
     * @return
     */
    public boolean isStatementEndSkipBlank() {
        skipBlank();
        return isStatementEnd();
    }

    /**
     * 是否语句结束
     * @return
     */
    public boolean isStatementEnd() {
        return isEOF() || currentMatch('\n', ';', ',', ')', ']', '}');
    }

    /**
     * 是否非换行空白字符
     * @return
     */
    public boolean isBlank() {
        return Character.isWhitespace(current()) && currentNot('\n');
    }

    /**
     * 跳过非换行空白字符和 skipChars
     * @param skipChars
     */
    public void skipBlank(char... skipChars) {
        while (Character.isWhitespace(current()) && currentNot('\n') || currentMatch(skipChars)) {
            next();
        }
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
     * 部分情况需要预读下一个 token 来确定语法行为，这里用于恢复至预读前的数据
     */
    
    /**
     * 预读当前位置开始的 num 个字符序列，
     * 如果字符序列和 expect 相等则移动，否则回退到原位置
     * @return true 预读成功；
     *              预读失败，回退原位置
     */
    public boolean prefetchNextChars(int num, String expect) {
        // 记录当前信息
        Snapshot snapshot = snapshot();

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
        restore(snapshot);
        return false;
    }

    /**
     * 预取下一个合法 token，判断是否为运算符或关键字
     * @param checker
     * @return
     */
    public String prefetchNextChars(Predicate<String> checker) {
        Predicate<Character> collector= null;
        if (composableDelimiters.contains(current())) { // 分隔符组成的 token，一般用来判断运算符，例如 ==, ++, +=
            collector = c -> composableDelimiters.contains(c);
        } else { // 非分割符组成的 token，一般用来判断关键字，例如：if else in not
            collector = c -> !isDelimiter(c);
        }

        return prefetchNextChars(checker, collector);
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
    public String prefetchNextChars(Predicate<String> checker, Predicate<Character> collector) {
        // 记录当前信息
        Snapshot snapshot = snapshot();

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
        restore(snapshot);
        return null;
    }

}
