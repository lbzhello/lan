package lan.ast.expression;


import cn.hutool.core.util.ArrayUtil;
import lan.ast.BaseExpression;

public class AssignExpression extends BaseExpression {
    @Override
    public String toString() {
        return "(= " + ArrayUtil.join(toArray(), " ") + ")";
    }
}
