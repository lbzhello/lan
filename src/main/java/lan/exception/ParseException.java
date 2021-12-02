package lan.exception;

/**
 * 语法解析错误
 */
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
