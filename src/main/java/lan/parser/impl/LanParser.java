package lan.parser.impl;

import lan.ast.Expression;
import lan.parser.Parser;
import lan.parser.TextParser;
import lan.parser.Token;

import java.util.Set;

/**
 * 语言解析器
 * 关键字通过额外的解析器解析，支持扩展
 */
public class LanParser implements Parser {

    /**
     * 分隔符定义
     */
    public static char COMMA = ',';
    public static char SEMICOLON = ';';
    public static char POINT = '.';
    public static char COLON = ':';

    public static char ROUND_BRACKET_LEFT = '(';
    public static char ROUND_BRACKET_RIGHT = ')';

    public static char CURLY_BRACKET_LEFT = '{';
    public static char CURLY_BRACKET_RIGHT = '}';

    public static char SQUARE_BRACKET_LEFT = '[';
    public static char SQUARE_BRACKET_RIGHT = ']';

    public static char ANGLE_BRACKET_LEFT = '<';
    public static char ANGLE_BRACKET_RIGHT = '>';

    public static char SLASH_LEFT = '\\';
    public static char SLASH_RIGHT = '/';

    public static char QUOTE_MARK_DOUBLE = '"';
    public static char QUOTE_MARK_SINGLE = '\'';

    public static char BACK_QUOTE = '`';

    public Set<Character> delimiters = Set.of(
            COMMA,
            SEMICOLON,
            POINT,
            COLON,
            ROUND_BRACKET_LEFT,
            ROUND_BRACKET_RIGHT,
            CURLY_BRACKET_LEFT,
            CURLY_BRACKET_RIGHT,
            SQUARE_BRACKET_LEFT,
            SQUARE_BRACKET_RIGHT,
            ANGLE_BRACKET_LEFT,
            ANGLE_BRACKET_RIGHT,
            SLASH_LEFT,
            SLASH_RIGHT,
            QUOTE_MARK_DOUBLE,
            QUOTE_MARK_SINGLE,
            BACK_QUOTE
    );

    private TextParser parser;

    public LanParser(TextParser parser) {
        this.parser = parser;
        this.parser.addDelimiters(delimiters);
    }

    @Override
    public Expression parseExpression() {
        char current = parser.current();
        if (current == ROUND_BRACKET_LEFT) {

        } else if (current == SQUARE_BRACKET_LEFT) {

        } else if (current == CURLY_BRACKET_LEFT) {

        } else if (parser.isDelimiter(current)) {
            // 跳过间隔符
            parser.next();
            return this.parseExpression();
        } else if (parser.hasNext()) {
            parser.nextWord();
        } else {
            return Token.EOF;
        }

        return null;
    }
}
