package lan.parser.impl;

import lan.ast.Expression;
import lan.base.impl.LanDefinition;
import lan.parser.Parser;
import lan.parser.TextParser;
import lan.parser.Token;

public class LanParser implements Parser {
    private TextParser parser;

    public LanParser(TextParser parser) {
        this.parser = parser;
    }

    @Override
    public Expression parseExpression() {
        char current = parser.current();
        if (current == LanDefinition.ROUND_BRACKET_LEFT) {

        } else if (current == LanDefinition.SQUARE_BRACKET_LEFT) {

        } else if (current == LanDefinition.CURLY_BRACKET_LEFT) {

        } else if (parser.isDelimiter(current)) {
            // 跳过间隔符
            parser.next();
            return this.parseExpression();
        } else {
            parser.nextWord();
        }
        return null;
    }
}
