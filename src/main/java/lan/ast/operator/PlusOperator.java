package lan.ast.operator;

import lan.ast.Expression;
import lan.ast.Operator;
import lan.ast.Value;

public class PlusOperator implements Operator {

    @Override
    public Expression eval() {
        return Value.NIL;
    }
}
