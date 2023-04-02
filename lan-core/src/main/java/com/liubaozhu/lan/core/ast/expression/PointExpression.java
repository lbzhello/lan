package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.BaseExpression;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.Value;
import com.liubaozhu.lan.core.ast.java.JavaObject;
import com.liubaozhu.lan.core.ast.java.MethodInfo;

/**
 * 类中方法，字段引用
 */
public class PointExpression extends BaseExpression {
    @Override
    public Expression eval() {
        // caller.member
        Expression caller = get(0).eval();
        Expression member = get(1).eval();
        // 类中方法，字段引用
        if (caller instanceof ClassExpression) {
            Expression lookup = ((ClassExpression) caller).lookup(String.valueOf(member));
            // 反射调用 java 方法
            if (lookup instanceof MethodInfo) {
                MethodInfo method = (MethodInfo) lookup;
                return method.bindTo(caller);
            }

            return lookup;
        }

        // 反射调用 java 方法
        if (caller instanceof JavaObject) {

        }

        return Value.NIL;
    }

    @Override
    public String toString() {
        return get(0) + "." + get(1);
    }
}
