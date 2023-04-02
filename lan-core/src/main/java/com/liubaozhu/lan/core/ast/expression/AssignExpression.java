package com.liubaozhu.lan.core.ast.expression;


import cn.hutool.core.util.ArrayUtil;
import com.liubaozhu.lan.core.ast.BaseExpression;

public class AssignExpression extends BaseExpression {
    @Override
    public String toString() {
        return "(= " + ArrayUtil.join(toArray(), " ") + ")";
    }
}
