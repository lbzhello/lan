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
        delimiter.put(',', LanToken.COMMA);
        delimiter.put(';', LanToken.SEMICOLON);
        delimiter.put('.', LanToken.POINT);
        delimiter.put(':', LanToken.COLON);
        delimiter.put('(', LanToken.ROUND_BRACKET_LEFT);
        delimiter.put(')', LanToken.ROUND_BRACKET_RIGHT);
        delimiter.put('{', LanToken.CURLY_BRACKET_LEFT);
        delimiter.put('}', LanToken.CURLY_BRACKET_RIGHT);
        delimiter.put('[', LanToken.SQUARE_BRACKET_LEFT);
        delimiter.put(']', LanToken.SQUARE_BRACKET_RIGHT);
        delimiter.put('\\', LanToken.SLASH_LEFT);
        delimiter.put('"', LanToken.QUOTE_MARK_DOUBLE);
        delimiter.put('\'', LanToken.QUOTE_MARK_SINGLE);
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
