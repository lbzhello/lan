package lan.ast;

import lan.ast.impl.StringExpression;
import org.junit.jupiter.api.Test;

public class ExpressionFactoryTest {
    @Test
    public void stringExprTest() {
        StringExpression hello_lan = ExpressionFactory.string("");
        System.out.println(hello_lan);

        // null 值
        StringExpression helloNull = ExpressionFactory.string(null);
        System.out.println(helloNull);
    }
}
