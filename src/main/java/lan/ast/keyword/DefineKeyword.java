package lan.ast.keyword;

import lan.ast.Expression;
import lan.ast.Keyword;
import lan.base.ValueEnum;

/**
 * define 关键字语法树
 */
public class DefineKeyword implements Keyword {

    @Override
    public Expression eval() {
        return ValueEnum.NIL;
    }
}
