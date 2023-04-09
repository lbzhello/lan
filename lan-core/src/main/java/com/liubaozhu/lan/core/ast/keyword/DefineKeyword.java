package com.liubaozhu.lan.core.ast.keyword;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.Keyword;

/**
 * define 关键字语法树
 */
public class DefineKeyword implements Keyword {

    @Override
    public Expression eval() {
        return Expression.NIL;
    }
}
