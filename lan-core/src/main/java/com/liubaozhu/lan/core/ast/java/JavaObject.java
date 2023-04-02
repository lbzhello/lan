package com.liubaozhu.lan.core.ast.java;

import com.liubaozhu.lan.core.ast.Expression;

/**
 * 表示一个 java 对象
 */
public class JavaObject implements Expression {
    private Object object;

    public JavaObject(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return String.valueOf(object);
    }

}
