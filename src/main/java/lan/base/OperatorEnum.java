package lan.base;

import lan.ast.Expression;
import lan.ast.ExpressionFactory;
import lan.ast.Name;
import lan.ast.Operator;

import java.util.function.Supplier;

/**
 * 关键字
 */
public enum OperatorEnum implements Name {
    NOT("!", ExpressionFactory::plus),
    AUTO_DEC("--", ExpressionFactory::plus),
    AUTO_INC("++", ExpressionFactory::plus),

    //--------- Binary -----------//
    COLON(":", ExpressionFactory::plus),
    COLON2("::", ExpressionFactory::plus),
    ARROW("->", ExpressionFactory::plus),
    ASSIGN("=", ExpressionFactory::plus),
    OR("||", ExpressionFactory::plus),
    AND("&&", ExpressionFactory::plus),

    EQUAL("==", ExpressionFactory::plus),
    NOT_EQUAL("!=", ExpressionFactory::plus),
    GT(">", ExpressionFactory::plus),
    GE(">=", ExpressionFactory::plus),
    LT("<", ExpressionFactory::plus),
    LE("<=", ExpressionFactory::plus),

    PLUS("+", ExpressionFactory::plus),
    MINUS("-", ExpressionFactory::plus),
    MUL("*", ExpressionFactory::plus),
    DIV("/", ExpressionFactory::plus),

    //--------- Other -----------//
    POINT(".", ExpressionFactory::plus),
    PRINT("print", ExpressionFactory::plus),
    FILE("file", ExpressionFactory::plus),
    ;

    // 运算符名字
    private final String name;
    // 运算符表达式
    private final Supplier<Operator> astSupplier;

    OperatorEnum(String name, Supplier<Operator> astSupplier) {
        this.name = name;
        this.astSupplier = astSupplier;
    }

    @Override
    public Expression eval() {
        return this;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取运算符怒语法树
     * @return
     */
    public Operator getAst() {
        return astSupplier.get();
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
