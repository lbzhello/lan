package lan.parser;

import lan.ast.Expression;

/**
 * 文本解析器
 */
public interface Parser {

    default Expression parseExpression(String text, int pos) {
        return parseExpression(text, pos, false);
    }

    /**
     *
     * @param text 需要解析的文本
     * @param pos 当前解析位置
     * @param parseBegin 是否需要解析开头关键字
     * @return
     */
    Expression parseExpression(String text, int pos, boolean parseBegin);
}
