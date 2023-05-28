package com.liubaozhu.lan.core.parser;

import cn.hutool.core.util.NumberUtil;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.ExpressionFactory;
import com.liubaozhu.lan.core.ast.expression.*;
import com.liubaozhu.lan.core.base.Definition;
import com.liubaozhu.lan.core.exception.ParseException;
import com.liubaozhu.lan.core.lexer.LanLexer;

import java.util.*;

/**
 * 语法解析器
 * 关键字通过额外的解析器解析 {@link SyntaxParser}，以支持扩展
 * word = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
 * phrase = word || word word
 * operator = phrase || phrase op phrase
 * command = phrase || phrase operator operator || operator operator operator
 * statement = operator || command
 */
public class LanParser {

    /**
     * 分隔符定义
     */
    public static char COMMA = ',';
    public static char SEMICOLON = ';';
    public static char POINT = '.';
    public static char COLON = ':';

    public static char ROUND_BRACKET_LEFT = '(';
    public static char ROUND_BRACKET_RIGHT = ')';

    public static char CURLY_BRACKET_LEFT = '{';
    public static char CURLY_BRACKET_RIGHT = '}';

    public static char SQUARE_BRACKET_LEFT = '[';
    public static char SQUARE_BRACKET_RIGHT = ']';

    public static char ANGLE_BRACKET_LEFT = '<';
    public static char ANGLE_BRACKET_RIGHT = '>';

    public static char SLASH_LEFT = '\\';
    public static char SLASH_RIGHT = '/';

    public static char QUOTE_MARK_DOUBLE = '"';
    public static char QUOTE_MARK_SINGLE = '\'';

    public static char BACK_QUOTE = '`';

    public static final char ASSIGN = '=';

    public static Expression EOF = new Expression.Constant("EOF");

    /**
     * 基础文本解析器
     */
    private LanLexer lexer;

    /**
     * 基础语言定义，运算符，关键字等
     */
    private Definition definition;

    /**
     * 关键字解析器
     */
    private Map<String, SyntaxParser> syntaxParser = new HashMap<>();

    /**
     * 查找关键字对应的解析器，关键字对应的语法结构通过专门的解析器解析
     * @param keyword
     * @return
     */
    private SyntaxParser getSyntaxParser(String keyword) {
        return this.syntaxParser.get(keyword);
    }

    /**
     * 只支持通过 builder 创建
     */
    private LanParser() {
    }

    public static LanParserBuilder builder() {
        return new LanParserBuilder();
    }

    /**
     * 字，原子表达式, 即最小结构的完整表达式
     * 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
     * @return
     */
    public Expression word() {
        lexer.skipBlank('\n', ';');
        char current = lexer.current();
        if (current == ROUND_BRACKET_LEFT) { // (
            return roundBracketExpression();
        } else if (current == SQUARE_BRACKET_LEFT) { // [
            return squareBracketExpression();
        } else if (current == CURLY_BRACKET_LEFT) { // {

        } else if (current == QUOTE_MARK_DOUBLE) { // "
            return stringExpression();
        } else if (current == QUOTE_MARK_SINGLE) { // '

        } else if (current == BACK_QUOTE) { // `

        } else if (lexer.isDelimiter(current)) { // ++i 可能是一元运算符，暂不支持
            String op = lexer.prefetchNextChars(it -> definition.isOperator(it));
            if (op == null) { // 间隔符
                throw new ParseException("word 表达式语法错误，非法间隔符", lexer);
            }
            return new SymbolExpression(op); // ++
        } else if (lexer.hasNext()) { // 数字，字面量等
            String token = lexer.nextToken();

            if (NumberUtil.isNumber(token)) { //todo 无依赖
                return new NumberExpression(token);
            }
            return new SymbolExpression(token);
        }

        return EOF;
    }

    /**
     * 词语，由字 {@link #word()} 组成的词语
     * phrase = word || word word
     * foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression phrase() {
        return phrase(word());
    }

    /**
     * 解析词语，根据 left 继续向下解析一次
     * 不包括运算符表达式 {@link #operator(Expression, SymbolExpression)} 和命令表达
     * 式 {@link #command(CommandExpression)}
     * e.g. foo(...) || foo.bar || foo::bar || foo: bar （左强结合） || i++ || i--
     * @return
     */
    private Expression phrase(Expression left) {
        if (lexer.isBlank()) { // left ... 中间有空白字符
            lexer.skipBlank();
            return left;
        }
        char current = lexer.current();
        if (current == '(') { // 函数调用 expr(...)
            Expression expression = roundBracketExpression();
            EvalExpression eval = new EvalExpression(left);
            // 参数
            if (expression instanceof ListExpression) {
                eval.addAll(((ListExpression) expression).toArray());
            } else {
                eval.add(expression);
            }
            // expr(...)(...)
            return phrase(eval);
        } else if (current == '[') { // left[expr1, expr2...
            ListExpression list = squareBracketExpression();
            EvalExpression eval = new EvalExpression(left);
            eval.addAll(list.toArray());
            return phrase(eval);
        } else if (current == '{') {

        } else if (current == '.') {

        } else if (current == ':') {
            if (lexer.prefetchNextChars(2, "::")) { // left:: 类型声明
                if (left instanceof SymbolExpression symbol) { // foo::... 变量声明
                    VariableExpression variable = new VariableExpression(symbol.literal());
                    Expression word = word();
                    variable.setType(word);
                    return variable;
                }
            }
        }

        return left;
    }

    /**
     * 语句，及一个完整的表达式，默认支持命令行
     * @return
     */
    public Expression statement() {
        return statement(true);
    }

    /**
     * 语句，即由
     *      单词 {@link #word()}，
     *      赋值表达式 {@link #assignExpression(Expression)}，
     *      运算符 {@link #operator(Expression, SymbolExpression)}，
     *      命令 {@link #command(CommandExpression)}
     * 等组成的一条完整句子。
     *
     * 解析完成后会去除空白符和换行符（'\n' ';'）。
     *
     * word = statement || operator || command || phrase, phrase, phrase || statement = statement
     * e.g.
     * head a b... || head + b...
     * @param supportCommand 是否支持命令表达式，一般只有行开始支持命令式调用
     * @return
     */
    public Expression statement(boolean supportCommand) {
        Expression phrase = phrase();
        if (lexer.isStatementEndSkipBlank()) {
            return phrase;
        }

        // 关键字 if for fn class 等语法结构解析
        if (this.definition.isKeyWord(phrase.literal())) {
            // 交给对应的关键字解析器解析
            SyntaxParser syntaxParser1 = getSyntaxParser(phrase.literal());
            // 调用
            return null;
        }

        // phrase op ... 运算符表达式
        String op = lexer.prefetchNextChars(it -> definition.isOperator(it));
        if (op != null) {
            return operator(phrase, ExpressionFactory.symbol(op));
        }

        // phrase = ... 赋值表达式
        if (lexer.currentIs('=')) {
            return assignExpression(phrase);
        }

        if (!supportCommand) {
            throw new ParseException("当前位置不支持命令式调用，是否缺少语句结束符 ';' ?", lexer);
        }

        // phrase ... 命令表达式
        Expression command = command(new CommandExpression(phrase));

        lexer.skipBlank('\n', ';');
        return command;
    }

    /**
     * 赋值表达式
     * expr = operator
     * expr = p1 + p2 = p3 + p4 + p5
     * @param left
     * @return
     */
    private Expression assignExpression(Expression left) {
        lexer.next(); // eat =

        lexer.skipBlank('\n'); // 跳过空白和换行
        if (lexer.isStatementEnd()) { // left = ;... 解析错误，等号后缺少表达式
            throw new ParseException("解析错误，等号后缺少表达式", lexer);
        }

        Expression right = statement(); // left = statement...

        AssignExpression assign = new AssignExpression();
        assign.add(left);
        assign.add(right);
        return assign;
    }

    /**
     * 运算符表达式
     * operator = phrase op phrase
     * @param left
     * @param op
     * @return
     */
    private Expression operator(Expression left, SymbolExpression op) {
        lexer.skipBlank('\n'); // 运算符支持换行

        // 运算符等同函数调用
        // left.op 调用类 left 中的函数 op
        MethodExpression method = new MethodExpression();
        method.add(left);
        method.add(op);

        // expr op ;...
        if (lexer.isStatementEnd()) {
            // i++ i--
            if (Objects.equals(op.literal(), "==") || Objects.equals(op.literal(), "--")) {
                return method;
            }
            throw new ParseException("运算符后面缺少参数", lexer);
        }

        // op 函数调用
        EvalExpression eval = new EvalExpression();
        eval.add(method); // (left.op ...

        Expression right = phrase();
        if (lexer.isStatementEndSkipBlank()) { // left op right
            eval.add(right);
            return eval;
        }

        // left op right op2... 运算符表达式
        String op2Str = lexer.prefetchNextChars(it -> definition.isOperator(it));
        if (op2Str == null) { // left op right ... 非运算符表达式，且缺少语句结束符号
            throw new ParseException("运算符解析错误，是否缺少 ';'", lexer);
        }

        SymbolExpression op2 = new SymbolExpression(op2Str);

        int precedence = definition.comparePrecedence(op, op2);
        if (precedence < 0) { // left op (right op2...
            eval.add(operator(right, op2));
            return eval;
        } else { // (left op right) op2...
            eval.add(right);
            return operator(eval, op2);
        }
    }

    /**
     * 命令方式的函数调用
     * 支持逗号分割参数
     * phrase || phrase phrase
     * e.g.
     * cmd
     * cmd p1 p2
     * cmd (p1 + p2) p4
     * cmd (p1 + p2) p3 (3 + 2)
     * @return
     */
    private CommandExpression command(CommandExpression cmd) {
        if (lexer.isStatementEndSkipBlank()) {
            return cmd;
        }

        try {
            cmd.add(phrase());
        } catch (Exception e) {
            throw new ParseException("命令表达式语法错误，参数必须是合法 phrase 表达式", lexer, e);
        }
        return command(cmd);

    }

    /**
     * lambda 表达式
     * {p1::Int p2:Int -> Int | return p1 + p2}
     * 1. 返回值 -> 可以省略；
     * 2. 如果 | 位于行尾，可以省略；
     * 3. 如果有参数，-> 和 | 只能省略一个；
     * 5. 如果无参数，-> 和 | 都可以省略；
     * @return
     */
    private LambdaExpression curlyBracketExpression() {
        LambdaExpression lambda = new LambdaExpression();

        lexer.next(); // eat '{'
        lexer.skipBlank('\n');

        if (lexer.currentIs('|')) { // { | ...
            lexer.next();

            // { | }
            lexer.skipBlank('\n');
            if (lexer.currentIs('}')) {
                lexer.next(); // eat '}'
                return lambda;
            }
            parseLambdaBody(lambda);
            return lambda;
        }

        if (lexer.currentIs('}')) { // {} 空lambda
            lexer.next();
            return lambda;
        }

        // 解析 lambda 参数
        parseLambdaParams(lambda);
        // 解析 lambda 方法体
        parseLambdaBody(lambda);
        return lambda;
    }

    /**
     * 判断 statement 是否是 lambda 参数
     * @param statement
     * @return
     */
    private boolean checkLambdaParams(Expression statement, List<Expression> lambdaParamCache) {
        // 如果是命令式参数，则 CommandExpression 列表中的值是变量声明类型
        if (statement instanceof CommandExpression command) {
            for (Expression elem : command.toArray()) {
                if (!(elem instanceof VariableExpression)
                        && !(elem instanceof SymbolExpression)) {
                    return false;
                }
            }
            lambdaParamCache.addAll(List.of(command.toArray()));
            return true;
        }

        // 变量声明类参数
        if (statement instanceof VariableExpression || statement instanceof SymbolExpression) {
            lambdaParamCache.add(statement);
            return true;
        }

        return false;
    }

    // 解析 ',' 分割的 lambda 参数
    // {p1, p2, p3...
    private void parseLambdaParamsWithComma(LambdaExpression lambda, List<Expression> lambdaParamCache) {
        // todo
    }

    /**
     * 完成 lambda 参数解析，如 '|'  '->' '}'
     * @param lambda
     * @param lambdaParamCache
     * @param lambdaBodyCache
     * @return true 完成 lambda 参数后续解析
     *         false 未完成解析，可能不含参数形式
     */
    private boolean parseLambdaParamsComplete(LambdaExpression lambda,
                                              List<Expression> lambdaParamCache,
                                              List<Expression> lambdaBodyCache) {
        // {p1, p2 -> ...
        if (lexer.prefetchNextChars(2, "->")) {
            lambda.addParams(lambdaParamCache);
            parseLambdaReturnType(lambda);
            return true;
        }

        // {p1, p2 | ...
        if (lexer.currentIs('|')) {
            lexer.next();
            lambda.addParams(lambdaParamCache);
            parseLambdaBody(lambda);
            return true;
        }

        // {p1...} 请求体全是变量声明式语句
        if (lexer.currentIs('}')) {
            lexer.next();
            lambda.addCode(lambdaBodyCache);
            return true;
        }

        return false;
    }

    /**
     * 解析 lambda 参数
     * 括号 {(p1, p2, p3) -> p4 | ...}
     * 逗号分割 {p1, p2, p3 -> p4 | ...}
     * 空格分割 {p1 p2 p3 -> p4 | ...}
     * @param lambda
     * @return true 有参数，false 无参数
     */
    private void parseLambdaParams(LambdaExpression lambda) {
        boolean containsComma = true;

        List<Expression> lambdaBodyCache = new ArrayList<>();
        List<Expression> lambdaParamCache = new ArrayList<>();

        Expression first = statement();
        lambdaBodyCache.add(first);

        // 括号形式参数
        // {(p1, p2, p3) -> ...
        if (first instanceof TupleExpression tuple) {
            lambdaParamCache.addAll(List.of(tuple.toArray()));
            if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                return;
            }
        }

        // 逗号分割的参数形式
        if (first instanceof VariableExpression || first instanceof SymbolExpression) {
            lambdaParamCache.add(first);
            // {p1 -> ... 单参数形式
            boolean isComplete = parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache);
            if (isComplete) {
                return;
            }

            // {p1, ... 逗号分割的参数
            if (lexer.currentIs(',')) {
                Expression phrase = first;
                while (phrase instanceof VariableExpression || phrase instanceof SymbolExpression) {
                    if (lexer.currentIs(',')) {
                        lexer.next();
                        lexer.skipBlank('\n');
                        // {p1, p2, -> ... 最后一个参数可以是 ','
                        if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) {
                            return;
                        }
                    } else if (parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache)) { // p1, p2 -> 解析完成
                        return;
                    } else {
                        throw new ParseException("lambda 参数缺少 ','", lexer);

                    }

                    phrase = phrase();
                    lambdaBodyCache.add(phrase);
                    lambdaParamCache.add(phrase);
                }

                // {p1, p2 p3...
                throw new ParseException("lambda 参数缺少 ','", lexer);
            }
        }

        // 空格分割的参数形式
        // {p1 p2
        //     p3::Str p4::Int -> ...
        Expression statement = first;
        while (checkLambdaParams(statement, lambdaParamCache)) {
            boolean isComplete = parseLambdaParamsComplete(lambda, lambdaParamCache, lambdaBodyCache);
            if (isComplete) {
                return;
            }
            // 继续解析下一个语句
            statement = statement();
            lambdaBodyCache.add(statement);
        }

        // 无参数形式，lambda body
        lambda.addCode(lambdaBodyCache);
        parseLambdaBody(lambda);
    }

    private void parseLambdaFirstParam(LambdaExpression lambda, Expression first) {
        lexer.skipBlank();

        // 表示第一个参数换过行
        boolean isLF = false;
        // 换行符
        if (lexer.isLineFeed()) {
            isLF = true;
            lexer.skipBlank('\n');
        }

        // { if ... 关键字
        if (definition.isKeyWord(first.literal())) {
            // 交给对应的关键字解析器解析
            SyntaxParser syntaxParser1 = getSyntaxParser(first.literal());
            Expression statement = null;
            lambda.addCode(statement);
            parseLambdaBody(lambda);
            return;
        }

        // first op ... 运算符表达式
        String op = lexer.prefetchNextChars(it -> definition.isOperator(it));
        if (op != null) {
            Expression operator = operator(first, ExpressionFactory.symbol(op));
            lambda.addCode(operator);
            parseLambdaBody(lambda);
            return;
        }

        // lambda
        // {first -> ...
        // {first | ...
        if (lexer.currentIs('|') || lexer.prefetchNextChars(2, "->")) {
            parseLambdaBody(lambda);
            return;
        }

        // { first = ... 赋值表达式
        if (lexer.currentIs('=')) {
            Expression assignExpression = assignExpression(first);
            lambda.addCode(assignExpression);
            parseLambdaBody(lambda);
            return;
        }

        // { first; ...
        if (lexer.currentIs(';')) {
            lexer.next(); // eat ';'
            lambda.addCode(first);
            parseLambdaBody(lambda);
            return;
        }

        // {first, ... 逗号分割参数
        if (lexer.currentIs(',')) { // {first, ...
            lexer.next(); // eat ','
            lexer.skipBlank('\n');
        }

        // 非 ',' 其他间隔符，表示没有参数
        if (lexer.isDelimiter()) {

        }

        // 空格分割参数
        lambda.addParams(first);

    }

    private void parseLambdaReturnType(LambdaExpression lambda) {
        Expression retType = phrase(); // 可能解析错误
        lambda.setReturnType(retType);
    }

    // 解析 lambda 方法体；
    private void parseLambdaBody(LambdaExpression lambda) {

    }


    /**
     * 解析 [...] 表达式
     *
     * 空格分割列表： [foo (1 + 2) 233]
     * 逗号分割列表： [foo, 1 + 2, 233]
     * @return
     */
    private ListExpression squareBracketExpression() {
        lexer.next(); // eat '['
        lexer.skipBlank('\n');

        ListExpression list = new ListExpression();

        // [] 空列表
        if (lexer.currentIs(']')) {
            lexer.next();
            return list;
        }

        // 解析第一个元素
        Expression first = phrase();

        // [first + ... 判断第一个元素是否为运算符
        String op = lexer.prefetchNextChars(it -> definition.isOperator(it));
        if (op != null) {
            first = operator(first, new SymbolExpression(op));
        }

        list.add(first);

        // [first, ... 逗号分割列表，运算符一定是逗号分割
        if (lexer.currentIs(',') || op != null) {
            parseSquareBracketByComma(list);
        } else { // [first ... 空格分割列表
            parseSquareBracketBySpace(list);
        }

        return list;
    }

    /**
     * 解析 [...] 表达式
     *
     * 空格分割列表： [foo (1 + 2) 233]
     * @return
     */
    private void parseSquareBracketBySpace(ListExpression list) {
        lexer.skipBlank('\n'); // 支持换行
        if (lexer.currentIs(']')) {
            lexer.next();
            return;
        }

        if (lexer.isStatementEnd()) {
            throw new ParseException("列表表达式 [] 解析错误，异常结束符号: '%s'".formatted(lexer.current()), lexer);
        }

        try {
            Expression phrase = phrase();
            list.add(phrase);
        } catch (Exception e) {
            throw new ParseException("列表表达式 [] 解析错误", lexer, e);
        }

        parseSquareBracketBySpace(list);
    }

    /**
     * 解析 [...] 表达式
     *
     * 逗号分割列表： [foo, 1 + 2, 233]
     * @return
     */
    private void parseSquareBracketByComma(ListExpression list) {
        lexer.skipBlank('\n');

        if (lexer.currentIs(']')) {
            return;
        }

        if (lexer.currentIs(',')) {
            lexer.next();
            lexer.skipBlank('\n');
            // list,] 允许最后多个 ','
            if (lexer.currentIs(']')) {
                lexer.next(); // eat ']'
                return;
            }
        } else {
            throw new ParseException("列表表达式 [] 解析错误，缺少 ','", lexer);
        }

        if (lexer.isStatementEnd()) {
            throw new ParseException("列表表达式 [] 解析错误，缺少 ']'", lexer);
        }

        try {
            Expression statement = statement(false);
            list.add(statement);
        } catch (Exception e) {
            throw new ParseException("列表表达式 [] 解析错误", lexer, e);
        }

        parseSquareBracketByComma(list);
    }

    /**
     * 解析括号表达式
     * (operator, operator, operator) 列表表达式
     * (cmd operator operator) 命令调用
     * e.g. (expr) || (cmd word word) || (operator, operator, operator)
     * @return
     */
    private Expression roundBracketExpression() {
        lexer.next(); // eat '('
        lexer.skipBlank('\n');

        // ( )
        if (lexer.currentIs(')')) {
            lexer.next();
            return new TupleExpression();
        }

        // (,) 空元组表达式
        if (lexer.currentIs(',')) {
            lexer.skipBlank(',', '\n');
            if (!lexer.currentIs(')')) {
                lexer.next(); // eat ')'
                return new TupleExpression();
            } else {
                throw new ParseException("元组表达式解析错误，空元组表达式格式：(,); 非空元组表达式不能以 ',' 号开始", lexer);
            }

        }

        // (expr...
        Expression statement = statement();

        // (expr,... 元组表达式
        if (lexer.currentIs(',')) {
            TupleExpression tuple = new TupleExpression();
            tuple.add(statement);
            roundBracketTuple(tuple);
            return tuple;
        }

        // (statement)... 语句等表达式，e.g. (a + b) || (a = c + d) || (max a b)
        if (lexer.currentIs(')')) {
            lexer.next(); // eat ')'
            return statement;
        }

        // (max a b c).. lisp 式函数调用语法
        LispExpression lisp = new LispExpression();

        // (cmd expr \n expr... lisp 表达式中的命令支持换行
        if (statement instanceof CommandExpression command) {
            lisp.addAll(command.toArray());
        }

        roundBracketLisp(lisp);
        return lisp;
    }

    /**
     * 括号表达式，lisp 语法
     * (max 2 5 + 6)
     * @return
     */
    private void roundBracketLisp(LispExpression lisp) {
        lexer.skipBlank('\n');
        if (lexer.currentIs(')')) {
            lexer.next(); // eat ')'
            return;
        }

        // (
        if (!lexer.hasNext()) {
            throw new ParseException("lisp 表达式解析错误，是否缺少 ')' ?", lexer);
        }

        try {
            // (lisp phrase...
            Expression phrase = phrase();
            lisp.add(phrase);
        } catch (Exception e) {
            throw new ParseException("lisp 表达式解析错误，参数只支持词语表达式，是否缺少 ')' ?", lexer, e);
        }

        // 递归解析
        roundBracketLisp(lisp);
    }

    /**
     * 解析元组表达式
     * (a, b, c) || (a + b, c, d)
     * @return
     */
    private void roundBracketTuple(TupleExpression tuple) {
        lexer.skipBlank('\n');
        // (tuple)
        if (lexer.currentIs(')')) {
            lexer.next(); // eat ')'
            return;
        }

        if (lexer.currentIs(',')) {
            lexer.next(); // eat ','
            lexer.skipBlank('\n');
            // (tuple,)... 允许最后多个空格
            if (lexer.currentIs(')')) {
                lexer.next(); // eat ')'
                return;
            }
        } else {
            throw new ParseException("tuple 表达式解析错误，缺少 ','", lexer);
        }

        // (a, b
        if (!lexer.hasNext()) {
            throw new ParseException("元组表达式解析错误，缺少 ')'。", lexer);
        }

        // (tuple, param,... 解析参数，元组中参数不支持命令式调用
        try {
            Expression param = statement(false);
            tuple.add(param);
        } catch (Exception e) {
            throw new ParseException("元组表达式解析错误，是否缺少 ',' ?", lexer);
        }

        roundBracketTuple(tuple);
    }

    /**
     * 解析 String
     * @return
     */
    private StringExpression stringExpression() {
        lexer.next(); // eat "
        StringBuilder sb = new StringBuilder();
        while (!lexer.currentIs('"') && lexer.hasNext()) {
            sb.append(lexer.current());
            lexer.next();
        }

        if (lexer.currentIs('"')) {
            lexer.next(); // eat "
        }
        return new StringExpression(sb.toString());
    }

    public void setLexer(LanLexer lexer) {
        this.lexer = lexer;
    }

    public void setDefinition(Definition definition) {
        this.definition = definition;
    }

    public void setSyntaxParser(Map<String, SyntaxParser> syntaxParser) {
        this.syntaxParser = syntaxParser;
    }

    /**
     * builder 模式创建 {@link LanParser}
     */
    public static class LanParserBuilder {
        /**
         * 基础文本解析器
         */
        private LanLexer lexer;

        /**
         * 基础语言定义，运算符，关键字等
         */
        private Definition definition;

        public LanParser build() {
            LanParser lanParser = new LanParser();
            lanParser.setLexer(this.lexer);
            lanParser.setDefinition(this.definition);
            lanParser.setSyntaxParser(this.syntaxParser);
            return lanParser;
        }

        /**
         * 关键字解析器
         */
        private Map<String, SyntaxParser> syntaxParser = new HashMap<>();

        public LanParserBuilder lexer(LanLexer lexer) {
            this.lexer = lexer;
            return this;
        }

        public LanParserBuilder definition(Definition definition) {
            this.definition = definition;
            return this;
        }

        public LanParserBuilder syntaxParser(Map<String, SyntaxParser> syntaxParser) {
            this.syntaxParser = syntaxParser;
            return this;
        }

    }
}
