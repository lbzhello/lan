package lan.ast.operator;

import lan.ast.Expression;
import lan.ast.Operator;
import lan.ast.Value;
import lan.ast.impl.NumberExpression;
import lan.ast.impl.StringExpression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * + 运算符
 * e.g. 3 + 2
 */
public class PlusOperator extends AbstractOperator implements Operator {
    @Override
    public Expression eval() {
        if (size() < 1) {
            return Value.NIL;
        }

        boolean isString = false;
        boolean isNumber = false;
        boolean isArray = false;

        List<Expression> exprList = new ArrayList<>();
        Set<Expression> exprSet = new HashSet<>();

        for (int i = 1; i < size(); i++) {
            Expression expr = get(i).eval();

            exprList.add(expr);
            exprSet.add(expr);

            if (expr instanceof StringExpression) {
                isString = true;
            } else if (expr instanceof NumberExpression) {
                isNumber = true;
            }
        }

        // 字符串和任何表达式相加都是字符串
        if (isString) {

        }

        return Value.NIL;
    }
}
