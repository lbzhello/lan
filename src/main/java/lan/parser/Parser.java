package lan.parser;

import lan.ast.Expression;

/**
 * 文本解析器
 */
public interface Parser {
    /**
     *
     * @return
     */
    Expression parseExpression();
}
