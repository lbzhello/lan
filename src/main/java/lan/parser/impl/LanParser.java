package lan.parser.impl;

import lan.ast.Expression;
import lan.parser.Parser;
import lan.parser.Tokenizer;

public class LanParser implements Parser {
    private Tokenizer tokenizer = new LanTokenizer();

    @Override
    public Expression parseExpression(String text, int pos, boolean parseBegin) {
        return null;
    }
}
