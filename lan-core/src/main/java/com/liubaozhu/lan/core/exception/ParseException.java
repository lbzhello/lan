package com.liubaozhu.lan.core.exception;

import com.liubaozhu.lan.core.lexer.LanLexer;

/**
 * 语法解析错误
 */
public class ParseException extends LanEexception {
    private String message;

    public ParseException(String message) {
        this(message, null);
    }

    public ParseException(String message, LanLexer parser) {
        this(message, parser, null);
    }

    public ParseException(String message, LanLexer parser, Exception exception) {
        this("-1", message, parser, exception);
    }

    public ParseException(String code, String message, LanLexer parser, Exception exception) {
        super(code, message, exception);
        this.message = "[" +
                "line=" + parser.getLine() + ", " +
                "column=" + parser.getColumn() + ", " +
                "char='" + parser.current() + "'" +
                "] " +
                message;
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        String message = this.message;
        return (message != null) ? (s + ": " + message) : s;
    }
}
