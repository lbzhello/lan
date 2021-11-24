package lan.ast.operator;

import lan.ast.Expression;
import lan.ast.Operator;
import lan.ast.Value;
import lan.ast.expression.ListExpression;
import lan.ast.expression.NumberExpression;
import lan.ast.expression.StringExpression;

/**
 * + 运算符
 * e.g. 3 + 2
 */
public class PlusOperator extends AbstractOperator implements Operator {
    /**
     * 加法运算符的行为取决于第一个元素
     * 第一个元素若是数字，表示数字相加
     * 第一个元素若是字符串，表示字符串相加
     * 第一个元素若是列表，表示向集合中添加元素，或者取两个列表的并集
     * @return
     */
    @Override
    public Expression eval() {
        if (size() < 2) {
            return Value.NIL;
        } else if (size() == 2) { // 只有一个操作数
            return get(1).eval();
        }

        Plus operand = null;
        Expression first = get(1).eval();

        if (first instanceof StringExpression) { // 字符串和非 List 表达式相加都是字符串

        } else if (first instanceof NumberExpression) {
            NumberExpression number = (NumberExpression) first;
            return number.plus(get(2));
        } else if (first instanceof ListExpression) { // 取并集，或者将元素加入列表

        }


        return Value.NIL;
    }
}
