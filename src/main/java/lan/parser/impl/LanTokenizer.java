package lan.parser.impl;

import lan.parser.Token;
import lan.parser.Tokenizer;

import java.util.HashMap;
import java.util.Map;

/**
 * lan 语言词法解析器
 */
public class LanTokenizer implements Tokenizer {
    private static final Map<Character, Token> delimiter = new HashMap<>();
    static {
        delimiter.put(',', Token.COMMA);
        delimiter.put(';', Token.SEMICOLON);
        delimiter.put('.', Token.POINT);
        delimiter.put(':', Token.COLON);
        delimiter.put('(', Token.ROUND_BRACKET_LEFT);
        delimiter.put(')', Token.ROUND_BRACKET_RIGHT);
        delimiter.put('{', Token.CURLY_BRACKET_LEFT);
        delimiter.put('}', Token.CURLY_BRACKET_RIGHT);
        delimiter.put('[', Token.SQUARE_BRACKET_LEFT);
        delimiter.put(']', Token.SQUARE_BRACKET_RIGHT);
        delimiter.put('\\', Token.SLASH_LEFT);
        delimiter.put('"', Token.QUOTE_MARK_DOUBLE);
        delimiter.put('\'', Token.QUOTE_MARK_SINGLE);
    }

    private static final boolean isDelimiter(Character c) {
        return delimiter.containsKey(c);
    }

    private static final Token getDelimiter(Character character) {
        return delimiter.get(character);
    }
    
    @Override
    public Token current() {
        return null;
    }

    @Override
    public Token next() {
        return null;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public int[] position() {
        return new int[0];
    }
}
