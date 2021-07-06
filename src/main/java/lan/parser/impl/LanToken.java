package lan.parser.impl;

import lan.ast.Expression;
import lan.parser.Token;

/**
 * lan 语言特殊 Token
 */
public class LanToken implements Token, Expression {
    // 文件结束 end of file
    public static LanToken EOF = new LanToken("EOF");
    // 文件开始 head of file
    public static LanToken HOF = new LanToken("HOF");
    // 行结束 end-of-line
    public static LanToken EOL = new LanToken("EOL");

    public static LanToken SPACE = new LanToken(" ");

    public static LanToken COMMA = new LanToken(",");
    public static LanToken SEMICOLON = new LanToken(";");
    public static LanToken POINT = new LanToken(".");
    public static LanToken COLON = new LanToken(":");

    // 括号前带有空格
    // e.g. (...), [1 2 3 4], def max {...}
    // 区别于 max(a, b), a[1], loop{...}
    public static LanToken SPACE_ROUND_BRACKET_LEFT = new LanToken(" (");
    public static LanToken SPACE_CURLY_BRACKET_LEFT = new LanToken(" {");
    public static LanToken SPACE_SQUARE_BRACKET_LEFT = new LanToken(" [");

    public static LanToken ROUND_BRACKET_LEFT = new LanToken("(");
    public static LanToken ROUND_BRACKET_RIGHT = new LanToken(")");

    public static LanToken CURLY_BRACKET_LEFT = new LanToken("{");
    public static LanToken CURLY_BRACKET_RIGHT = new LanToken("}");

    public static LanToken SQUARE_BRACKET_LEFT = new LanToken("[");
    public static LanToken SQUARE_BRACKET_RIGHT = new LanToken("]");

    public static LanToken ANGLE_BRACKET_LEFT = new LanToken("<");
    public static LanToken ANGLE_BRACKET_RIGHT = new LanToken(">");

    public static LanToken SLASH_LEFT = new LanToken("\\");
    public static LanToken SLASH_RIGHT = new LanToken("/");

    public static LanToken QUOTE_MARK_DOUBLE = new LanToken("\"");
    public static LanToken QUOTE_MARK_SINGLE = new LanToken("'");

    public static LanToken BACK_QUOTE = new LanToken("`");

    private String name;

    private LanToken(String name) {
        this.name = name;
    }

    @Override
    public Expression eval() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
