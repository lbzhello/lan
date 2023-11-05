package com.liubaozhu.lan.core.ast.expression;

import com.liubaozhu.lan.core.ast.java.MethodHandleAdapter;
import com.liubaozhu.lan.core.ast.java.MethodInfo;
import com.liubaozhu.lan.core.base.impl.LanDefinition;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 字符串
 */
public class StringExpression extends ClassExpression implements Value {
    // 系统提供的方法 + 等
    private static Map<String, MethodInfo> methods = new HashMap<>();
    static {
        methods.put(LanDefinition.PLUS, new MethodHandleAdapter(StringExpression.class, "plus", StringExpression.class, Object.class));
    }

    private String value;

    public StringExpression(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public Expression lookup(String key) {
        Expression lookup = super.lookup(key);
        if (Objects.isNull(lookup)) {
            return methods.get(key);
        }
        return lookup;
    }

    @Override
    public String toString() {
        return Objects.isNull(value) ? "\"\"" : "\"" + value + "\"";
    }

    public StringExpression plus(Object that) {
        return new StringExpression(this.value + that);
    }
}
