package com.liubaozhu.lan.core.parser;

import com.liubaozhu.lan.core.ast.Expression;

public class Token implements Expression {
    // 文件结束 end of file
    public static Token EOF = new Token("EOF");
    // 文件开始 head of file
    public static Token HOF = new Token("HOF");
    // 行结束 end-of-line
    public static Token EOL = new Token("EOL");

    public static Token SPACE = new Token(" ");

    public static Token COMMA = new Token(",");
    public static Token SEMICOLON = new Token(";");
    public static Token POINT = new Token(".");
    public static Token COLON = new Token(":");

    // 括号前带有空格
    // e.g. (...), [1 2 3 4], def max {...}
    // 区别于 max(a, b), a[1], loop{...}
    public static Token SPACE_ROUND_BRACKET_LEFT = new Token(" (");
    public static Token SPACE_CURLY_BRACKET_LEFT = new Token(" {");
    public static Token SPACE_SQUARE_BRACKET_LEFT = new Token(" [");

    public static Token ROUND_BRACKET_LEFT = new Token("(");
    public static Token ROUND_BRACKET_RIGHT = new Token(")");

    public static Token CURLY_BRACKET_LEFT = new Token("{");
    public static Token CURLY_BRACKET_RIGHT = new Token("}");

    public static Token SQUARE_BRACKET_LEFT = new Token("[");
    public static Token SQUARE_BRACKET_RIGHT = new Token("]");

    public static Token ANGLE_BRACKET_LEFT = new Token("<");
    public static Token ANGLE_BRACKET_RIGHT = new Token(">");

    public static Token SLASH_LEFT = new Token("\\");
    public static Token SLASH_RIGHT = new Token("/");

    public static Token QUOTE_MARK_DOUBLE = new Token("\"");
    public static Token QUOTE_MARK_SINGLE = new Token("'");

    public static Token BACK_QUOTE = new Token("`");

    private String name;

    private Token(String name) {
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
