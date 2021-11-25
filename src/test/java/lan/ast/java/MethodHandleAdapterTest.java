package lan.ast.java;

import lan.ast.Expression;
import lan.ast.expression.NumberExpression;
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
