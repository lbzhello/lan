package com.liubaozhu.lan.core.ast.java;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.expression.NumberExpression;
import org.junit.jupiter.api.Test;

public class MethodHandleAdapterTest {
    @Test
    public void handleTest() {
        MethodHandleAdapter methodHandleAdapter = new MethodHandleAdapter(NumberExpression.class, "plus",
                NumberExpression.class, NumberExpression.class);
        Expression invoke = methodHandleAdapter.bindTo(new NumberExpression("22")).invoke(new NumberExpression("33"));
        System.out.println();
    }
}
