package com.liubaozhu.lan.core.exception;

import java.io.Serializable;

public interface ErrorCode {
    String UNKNOWN_ERROR = "-1";
    String NOT_REALIZED = "NOT_REALIZED";
    String PARSE_PHRASE_FAILED = "PARSE_PHRASE_FAILED";
    String PARSE_LIST_FAILED = "PARSE_LIST_FAILED";
    String PARSE_COMMAND_FAILED = "PARSE_COMMAND_FAILED";
    String PARSE_TUPLE_FAILED = "PARSE_TUPLE_FAILED";
    String PARSE_LISP_FAILED = "PARSE_LISP_FAILED";
}
