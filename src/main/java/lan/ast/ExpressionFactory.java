package lan.ast;

import lan.ast.impl.NumberExpression;
import lan.ast.impl.StringExpression;
import lan.ast.impl.SymbolExpression;
import lan.ast.keyword.DefineKeyword;
import lan.ast.operator.PlusOperator;

/**
 * 简单工厂
 */
public class ExpressionFactory {
    /**
     * 创建一个值
     * @param value
     * @return
     */
    public static Value value(Object value) {
        return new Value.Constant(value);
    }

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

    /**
     * + 运算符
     * @return
     */
    public static PlusOperator plus() {
        return new PlusOperator();
    }

    /**
     * def 关键字
     * @return
     */
    public static DefineKeyword define() {
        return new DefineKeyword();
    }
}
