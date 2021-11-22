package lan.ast.operator;

import lan.ast.Expression;

/**
 * 实现此接口表示可以使用 + 运算符进行计算
 * @see PlusOperator
 */
public interface Plus {
    Expression plus(Expression operand);
}
