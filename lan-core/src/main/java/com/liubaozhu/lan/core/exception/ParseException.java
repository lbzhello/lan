package com.liubaozhu.lan.core.exception;

import com.liubaozhu.lan.core.parser.LanParser;

import java.text.CharacterIterator;

/**
 * 语法解析错误
 */
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, LanParser parser) {
        this(message);

    }
}
