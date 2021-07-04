package lan.parser.impl;

import lan.parser.CharIterator;
import lan.util.FileUtils;
import lan.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.StringCharacterIterator;

/**
 * string 文本迭代器
 */
public class StringIterator implements CharIterator {
    private static final Logger logger = LoggerFactory.getLogger(StringIterator.class);

    // 空迭代器
    public static final CharIterator EMPTY_ITERATOR = string("");

    private final StringCharacterIterator iterator;

    // 所处行
    private int line = 1;
    // 相对于当前行的位置
    private int linePos = 1;

    private StringIterator(String text) {
        this.iterator = new StringCharacterIterator(text);
    }

    /**
     * 根据 string 创建一个 iterator
     * @param text
     * @return
     */
    public static CharIterator string(String text) {
        return new StringIterator(text);
    }

    /**
     * 根据 file 路径创建一个 iterator
     * @return
     */
    public static CharIterator file(String path) {
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
    public static CharIterator file(File file) {
        try {
            String text = FileUtils.toString(file);
            return string(text);
        } catch (IOException e) {
            logger.error("failed to read file", e);
            return EMPTY_ITERATOR;
        }
    }

    @Override
    public boolean hasNext() {
        return current() == DONE;
    }

    @Override
    public char current() {
        return iterator.current();
    }

    @Override
    public char next() {
        return iterator.next();
    }

    @Override
    public char previous() {
        return iterator.previous();
    }

    /**
     * 获取当前所处位置，1-based
     * @return
     */
    @Override
    public int position() {
        return iterator.getIndex() + 1;
    }
}
