package com.liubaozhu.lan.core.exception;

import com.liubaozhu.lan.core.lexer.LanLexer;

/**
 * 语法解析错误
 */
public class ParseException extends RuntimeException {
    private String message;
    private LanLexer parser;

    public ParseException(String message) {
        this(message, null);
    }

    public ParseException(String message, LanLexer parser) {
        this(message, parser, null);
    }

    public ParseException(String message, LanLexer parser, Exception exception) {
        super(message, exception);
        this.message = "[" +
                "line=" + parser.getLine() + ", " +
                "column=" + parser.getColumn() + ", " +
                "char='" + parser.current() + "'" +
                "] " +
                message;
        this.parser = parser;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = this.message;
        return (message != null) ? (s + ": " + message) : s;
    }
}
