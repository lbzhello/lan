package com.liubaozhu.lan.core.base.impl;

import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.ExpressionFactory;
import com.liubaozhu.lan.core.ast.Keyword;
import com.liubaozhu.lan.core.ast.Operator;
import com.liubaozhu.lan.core.base.Definition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * lan 语言定义
 */
public class LanDefinition implements Definition {
    /** 运算符 start **/
    public static final String NOT = "!";
    public static final String AUTO_DEC = "--";
    public static final String AUTO_INC = "++";

    //--------- Binary -----------//
    public static final String COLON = ":";
    public static final String COLON2 = "::";
    public static final String ASSIGN = "=";
    public static final String OR = "||";
    public static final String AND = "&&";

    public static final String EQUAL = "==";
    public static final String NOT_EQUAL = "!=";
    public static final String GT = ">";
    public static final String GE = ">=";
    public static final String LT = "<";
    public static final String LE = "<=";

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MUL = "*";
    public static final String DIV = "/";

    //--------- Other -----------//
    public static final String POINT = ".";
    public static final String PRINT = "print";
    public static final String FILE = "file";
    /** 运算符 start **/

    /** 关键字 start **/
    public static final String DEFINE = "define";
    public static final String FUNCTION = "fn";
    public static final String LET = "let";
    public static final String LAMBDA = "lambda";

    // 泛型
    public static final String TYPE = "type";
    // 类
    public static final String CLASS = "class";
    // 继承类
    public static final String EXTEND = "extend";

    // 协议（接口）
    public static final String PROTOCOL = "protocol";

    // 实现接口
    public static final String IMPL = "impl";

    public static final String DO = "do";
    public static final String END = "end";
    public static final String RETURN = "ret";

    public static final String IF = "if";
    public static final String ELSE = "else";
    public static final String ELSEIF = "elif";
    public static final String FOR = "for";

    public static final String NEW = "new";
    public static final String IMPORT = "import";

    public static final String ARROW = "->";
    /** 关键字 end **/

    /**
     * 运算符提供者，用于创建运算符表达式
     */
    private Map<String, Supplier<Operator>> operatorSupplier = new HashMap<>();

    /**
     * 运算符优先级
     */
    private final Map<String, Integer> operatorPrecedence = new HashMap<>();

    /**
     * 关键字提供者，用于创建关键字表达式
     */
    private final Map<String, Supplier<Keyword>> keywordSupplier = new HashMap<>();

    /**
     * 默认优先级
     */
    private static final int DEFAULT_PRECEDENCE = 0;

    public LanDefinition() {
        init();
    }

    private void init() {
        // 关键字
        keywordSupplier.put(DEFINE, ExpressionFactory::define);

        // 运算符 todo 运算符通过方法实现，只是解析时行为不同
        operatorSupplier.put(PLUS, null);
        operatorSupplier.put(MUL, null);

        // 运算符优先级
        setPrecedence(ARROW, -1);

        setPrecedence(ASSIGN, 0);

        setPrecedence(OR, 11);
        setPrecedence(AND, 12);

        setPrecedence(EQUAL, 21);
        setPrecedence(NOT_EQUAL, 21);
        setPrecedence(GT, 21);
        setPrecedence(GE, 21);
        setPrecedence(LT, 21);
        setPrecedence(LE, 21);

        setPrecedence(PLUS, 31);
        setPrecedence(MINUS, 31);
        setPrecedence(MUL, 41);
        setPrecedence(DIV, 41);

        setPrecedence(POINT, 50);

        setPrecedence(COLON2, 51);

    }

    /**
     * 配置运算符优先级
     * @return
     */
    private void setPrecedence(String op, int precedence) {
        operatorPrecedence.put(op, DEFAULT_PRECEDENCE + precedence);
    }

    /**
     * 判断标识符是否是运算符
     * @param op
     * @return
     */
    @Override
    public boolean isOperator(String op) {
        return this.operatorSupplier.containsKey(op);
    }

    /**
     * 默认运算符提供者，运算符不存在时返回
     */
    private static final Supplier<Operator> defaultOperatorSupplier = () -> null;

    /**
     * 获取运算符表达式
     * @param op 运算符，如 + - * / 等
     * @return 对应的运算符表达式，可以执行相应的计算
     */
    @Override
    public Operator createOperator(String op) {
        return this.operatorSupplier.getOrDefault(op, defaultOperatorSupplier).get();
    }

    /**
     * 判断标识符是否是关键字
     * @param keyword
     * @return
     */
    @Override
    public boolean isKeyWord(String keyword) {
        return this.keywordSupplier.containsKey(keyword);
    }

    /**
     * 默认关键字提供者，关键字不存在时返回
     */
    private static final Supplier<Keyword> defaultKeywordSupplier = () -> null;

    /**
     * 获取关键字表达式
     * @param keyword
     * @return
     */
    @Override
    public Keyword createKeyword(String keyword) {
        return this.keywordSupplier.getOrDefault(keyword, defaultKeywordSupplier).get();
    }

    /**
     * 获取运算符优先级
     * @param operatorName
     * @return
     */
    @Override
    public Integer getPrecedence(String operatorName) {
        return operatorPrecedence.get(operatorName);
    }

    @Override
    public boolean isOperator(Expression op) {
        return isOperator(String.valueOf(op));
    }

    @Override
    public int comparePrecedence(Expression op1, Expression op2) {
        return 0;
    }
}
