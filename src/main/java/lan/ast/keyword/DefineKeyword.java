package lan.ast.keyword;

import lan.ast.Expression;
import lan.ast.Keyword;
import lan.ast.Value;

/**
 * define 关键字语法树
 */
public class DefineKeyword implements Keyword {

    @Override
    public Expression eval() {
        return Value.NIL;
    }
}
