package lan.base;

import lan.ast.Expression;
import lan.ast.ExpressionFactory;
import lan.ast.Name;
import lan.ast.Operator;

import java.util.function.Supplier;

/**
 * 关键字
 */
public enum OperateName implements Name {
    DEFINE("def", ExpressionFactory::plus),
    FUNCTION("fn", ExpressionFactory::plus),
    LET("let", ExpressionFactory::plus),

    DO("do", ExpressionFactory::plus),
    END("end", ExpressionFactory::plus),
    RETURN("ret", ExpressionFactory::plus),

    IF("if", ExpressionFactory::plus),
    ELSE("else", ExpressionFactory::plus),
    ELSEIF("elif", ExpressionFactory::plus),
    FOR("for", ExpressionFactory::plus),

    NEW("new", ExpressionFactory::plus),
    IMPORT("import", ExpressionFactory::plus),
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
    private final String value;
    // 运算符表达式
    private final Supplier<Operator> operatorSupplier;

    OperateName(String value, Supplier<Operator> operatorSupplier) {
        this.value = value;
        this.operatorSupplier = operatorSupplier;
    }

    @Override
    public Expression eval() {
        return this;
    }

    public String getValue() {
        return value;
    }

    public Supplier<Operator> getOperatorSupplier() {
        return operatorSupplier;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
