package lan.ast.operator;

import lan.ast.Expression;
import lan.ast.Operator;
import lan.base.ValueEnum;

public class PlusOperator implements Operator {

    @Override
    public Expression eval() {
        return ValueEnum.NIL;
    }
}
