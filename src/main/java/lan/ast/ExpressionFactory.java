package lan.ast;

import lan.ast.impl.NumberExpression;
import lan.ast.impl.StringExpression;
import lan.ast.impl.SymbolExpression;

/**
 * 简单工厂
 */
public class ExpressionFactory {
    /**
     * String 表达式
     * @param value
     * @return
     */
    public static StringExpression string(String value) {
        return new StringExpression(value);
    }

    /**
     * 数字表达式
     * @param value
     * @return
     */
    public static NumberExpression number(String value) {
        return new NumberExpression(value);
    }

    /**
     * 符号表达式
     * 表示一个变量，类或方法的名字等
     * @param value
     * @return
     */
    public static SymbolExpression symbol(String value) {
        return new SymbolExpression(value);
    }
}
