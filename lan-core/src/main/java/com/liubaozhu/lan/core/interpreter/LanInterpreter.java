package com.liubaozhu.lan.core.interpreter;

import cn.hutool.core.util.NumberUtil;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.expression.*;
import com.liubaozhu.lan.core.base.Definition;
import com.liubaozhu.lan.core.exception.ParseException;
import com.liubaozhu.lan.core.parser.LanParser;
import com.liubaozhu.lan.core.parser.Token;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 语言解析器
 * 关键字通过额外的解析器解析，支持扩展
 * word = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
 * phrase = word || word word
 * operator = phrase || phrase op phrase
 * command = phrase || phrase operator operator || operator operator operator
 * statement = operator || command
 */
public class LanInterpreter implements Interpreter {

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

    /**
     * 基础文本解析器
     */
    private LanParser parser;

    /**
     * 基础语言定义，运算符，关键字等
     */
    private Definition definition;

    /**
     * 解析栈，有时候需要解析下一个单词 {@link #phrase}才能确定是否返回;
     * 比如 {@link #operator}, 需要判断下一个单词是否是运算符
     * 可以将已经解析的下一个单词推入此栈中，供其他表达式获取
     */
    private Deque<Expression> termStack = new ArrayDeque<>();

    /**
     * 关键字解析器
     */
    private Map<String, Interpreter> keywordInterpreter = new HashMap<>();

    public LanInterpreter(LanParser parser, Definition definition, @Nullable Map<String, Interpreter> keywordInterpreter) {
        this.parser = parser;
        this.definition = definition;
        this.keywordInterpreter = keywordInterpreter;
    }

    /**
     * 查找关键字对应的解析器，关键字对应的语法结构通过专门的解析器解析
     * @param keyword
     * @return
     */
    private Interpreter getKeywordInterpreter(String keyword) {
        return this.keywordInterpreter.get(keyword);
    }

    /**
     * 查看栈里是否存在表达式
     * @return
     */
    private boolean peek() {
        return termStack.peek() != null;
    }

    /**
     * 表达式出栈
     * @return
     */
    private Expression pop() {
        return termStack.poll();
    }

    /**
     * 表达式入栈
     * @return
     */
    private void push(Expression expr) {
        termStack.push(expr);
    }

    /**
     * 字，原子表达式, 即最小结构的完整表达式
     * 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
     * @return
     */
    public Expression word() {
        skipBlank('\n', ';');
        char current = parser.current();
        if (current == ROUND_BRACKET_LEFT) { // (
            return roundBracketExpression();
        } else if (current == SQUARE_BRACKET_LEFT) { // [
            return squareBracketExpr();
        } else if (current == CURLY_BRACKET_LEFT) { // {

        } else if (current == QUOTE_MARK_DOUBLE) { // "
            return stringExpression();
        } else if (current == QUOTE_MARK_SINGLE) { // '

        } else if (current == BACK_QUOTE) { // `

        } else if (parser.isDelimiter(current)) { // 间隔符
            throw new ParseException("word 表达式语法错误，非法间隔符", parser);
        } else if (parser.hasNext()) { // 数字，字面量等
            String token = parser.nextToken();
            // 关键字
            if (this.definition.isKeyWord(token)) {
                // 交给对应的关键字解析器解析
                Interpreter keywordInterpreter = getKeywordInterpreter(token);
                // 调用
                return null;
            }

            if (NumberUtil.isNumber(token)) { //todo 无依赖
                return new NumberExpression(token);
            }
            return new SymbolExpression(token);
        }

        return Token.EOF;
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
     * 不包括运算符表达式 {@link #operator} 和命令表达
     * 式 {@link #command(CommandExpression)}
     * e.g. foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression phrase(Expression left) {
        if (isBlank()) { // left ... 中间有空白字符
            skipBlank();
            return left;
        }
        char current = parser.current();
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
            ListExpression list = squareBracketExpr();
            EvalExpression eval = new EvalExpression(left);
            eval.addAll(list.toArray());
            return phrase(eval);
        } else if (current == '{') {

        } else if (current == '.') {

        } else if (current == ':') {

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
     *      运算符 {@link #operator(Expression, Expression)}，
     *      命令 {@link #command(CommandExpression)}
     * 等组成的一条完整句子
     *
     * word = statement || operator || command || phrase, phrase, phrase || statement = statement
     * e.g.
     * head a b... || head + b...
     * @param supportCommand 是否支持命令表达式，一般只有行开始支持命令式调用
     * @return
     */
    public Expression statement(boolean supportCommand) {
        Expression phrase = phrase();
        if (isStatementEndSkipBlank()) {
            return phrase;
        }

        // phrase op ... 运算符表达式
        String op = parser.prefetchNextToken(it -> definition.isOperator(it));
        if (op != null) {
            return operator(phrase, new SymbolExpression(op));
        }

        // phrase = ... 赋值表达式
        if (parser.currentIs('=')) {
            return assignExpression(phrase);
        }

        if (!supportCommand) {
            throw new ParseException("当前位置不支持命令式调用，是否缺少语句结束符 ';' ?", parser);
        }

        // phrase ... 命令表达式
        Expression command = command(new CommandExpression(phrase));

        skipBlank('\n', ';');
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
        parser.next(); // eat =

        skipBlank('\n'); // 跳过空白和换行
        if (isStatementEnd()) { // left = ;... 解析错误，等号后缺少表达式
            throw new ParseException("解析错误，等号后缺少表达式", parser);
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
    private Expression operator(Expression left, Expression op) {
        skipBlank('\n'); // 运算符支持换行
        if (isStatementEnd()) {
            throw new ParseException("运算符后面缺少参数", parser);
        }

        // Operator binary = definition.createOperator(String.valueOf(op));

        // 运算符等同函数调用
        // left.op 调用类 left 中的函数 op
        MethodExpression method = new MethodExpression();
        method.add(left);
        method.add(op);

        // op 函数调用
        EvalExpression eval = new EvalExpression();
        eval.add(method); // (left.op ...

        Expression right = phrase();
        if (isStatementEndSkipBlank()) { // left op right
            eval.add(right);
            return eval;
        }

        // left op right op2... 运算符表达式
        String op2Str = parser.prefetchNextToken(it -> definition.isOperator(it));
        if (op2Str == null) { // left op right ... 非运算符表达式，且缺少语句结束符号
            throw new ParseException("运算符解析错误，是否缺少 ';'", parser);
        }

        Expression op2 = new SymbolExpression(op2Str);

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
        if (isStatementEndSkipBlank()) {
            return cmd;
        }

        try {
            cmd.add(phrase());
        } catch (Exception e) {
            throw new ParseException("命令表达式语法错误，参数必须是合法 phrase 表达式", parser, e);
        }
        return command(cmd);

    }

    /**
     * 命令参数，递归，结果倒叙
     * @return
     */
    private EvalExpression commandParam() {
        skipBlank();
        if (parser.currentIs(',')) { // param1, param2 支持逗号分割
            skipBlank(',', '\n'); // ',' 后面可以接换行
        }

        if (isStatementEnd()) {
            EvalExpression eval = new EvalExpression();
            if (peek()) {
                eval.add(pop());
            }
            return eval;
        }

        // 解析下一个表达式参数
        Expression expression = operator();
        EvalExpression param = commandParam();
        param.add(expression);
        return param;
    }

    /**
     * 解析逗号分割的列表
     * e.g. operator, operator, operator = operator,...
     * @return
     */
    private ListExpression commaListExpr(Expression left) {
        ListExpression comma = commaListExpr0(left);
        comma.reverse();
        return comma;
    }

    /**
     * 解析逗号分割的列表 - 元素倒叙
     * e.g.
     * expr,
     * expr1, expr2,,expr3,,
     * expr1, expr2 = expr3 + expr4,, expr5
     * @return
     */
    private ListExpression commaListExpr0(Expression left) {
        if (parser.currentNot(',')) { // 解析完成
            ListExpression list = new ListExpression();
            list.add(left);
            return list;
        }

        skipBlank(',', '\n');

        if (isStatementEnd()) { // left,
            ListExpression list = new ListExpression();
            list.add(left);
            return list;
        }

        if (parser.currentMatch('=', ':')) { // left, = || left, :
            throw new ParseException("语法错误", parser);
        }

        // left, phrase...
        Expression expr = operator();
        if (peek()) {
            throw new ParseException("表达式解析错误，缺少 ','", parser);
        }
        skipBlank();
        ListExpression list = commaListExpr0(expr);
        list.add(left);
        return list;
    }

    /**
     * 运算符
     * expr
     * expr1 + expr2
     * expr = expr1 + expr2 = expr3 + expr4
     * @return
     */
    private Expression operator() {
        Expression left = phrase();
        if (isStatementEndSkipBlank()) {
            return left;
        }

        if (parser.currentIs('=')) { // left =...
            return assignExpression(left);
        }

        Expression phrase = phrase();
        if (definition.isOperator(phrase)) { // left phrase...
            return operator(left, phrase);
        }

        // 支持预取，放入栈中
        push(phrase);
        return left;
    }

    /**
     * 是否非换行空白字符
     * @return
     */
    private boolean isBlank() {
        return Character.isWhitespace(parser.current()) && parser.currentNot('\n');
    }

    /**
     * 跳过非换行空白字符和 skipChars
     * @param skipChars
     */
    private void skipBlank(char... skipChars) {
        while (Character.isWhitespace(parser.current()) && parser.currentNot('\n') || parser.currentMatch(skipChars)) {
            parser.next();
        }
    }

    /**
     * 是否结束换行符
     * @return
     */
    private boolean isLineFeed() {
        return parser.current() == '\n';
    }

    /**
     * 是否语句结束
     * @return
     */
    private boolean isStatementEnd() {
        return parser.isEOF() || parser.currentMatch('\n', ';', ',', ')', ']', '}');
    }

    /**
     * 跳过空格后是否行结束
     * @return
     */
    private boolean isStatementEndSkipBlank() {
        skipBlank();
        return isStatementEnd();
    }

//    /**
//     * 跳过空格符后，判断当前字符是否换行符
//     * @return
//     */
//    private boolean isLineBreakSkipBlank() {
//        skipBlank();
//        return isLF();
//    }

    /**
     * 解析 [...] 表达式
     * [foo, bar, 123]
     * [1 2 3 4]
     * [foo, 1 + 2 233]
     * @return
     */
    private ListExpression squareBracketExpr() {
        parser.next();

        ListExpression list = squareBracketExpr0();

        if (parser.currentIs(']')) {
            parser.next();
        } else {
            throw new ParseException("列表解析错误，缺少 ]", parser);
        }

        list.reverse();
        return list;
    }

    /**
     * 解析 [...] 表达式
     * [foo, bar, 123]
     * [1 2 3 4]
     * [foo, 1 + 2 233]
     * @return
     */
    private ListExpression squareBracketExpr0() {
        skipBlank(',', '\n');


        if (parser.currentIs(']') || !parser.hasNext()) { // []
            ListExpression list = new ListExpression();
            Expression pop = pop();
            if (Objects.nonNull(pop)) {
                list.add(pop);
            }
            return list;
        }

        Expression expr = operator();

        ListExpression list = squareBracketExpr0();

        list.add(expr);

        return list;
    }

    /**
     * 解析括号表达式
     * (operator, operator, operator) 列表表达式
     * (cmd operator operator) 命令调用
     * e.g. (expr) || (cmd word word) || (operator, operator, operator)
     * @return
     */
    private Expression roundBracketExpression() {
        parser.next(); // eat '('
        skipBlank('\n');

        // ( )
        if (parser.currentIs(')')) {
            parser.next();
            return new TupleExpression();
        }

        // (,) 空元组表达式
        if (parser.currentIs(',')) {
            skipBlank(',', '\n');
            if (!parser.currentIs(')')) {
                parser.next(); // eat ')'
                return new TupleExpression();
            } else {
                throw new ParseException("元组表达式解析错误，空元组表达式格式：(,); 非空元组表达式不能以 ',' 号开始", parser);
            }

        }

        // (expr...
        Expression statement = statement();

        // (expr,... 元组表达式
        if (parser.currentIs(',')) {
            TupleExpression tuple = new TupleExpression();
            tuple.add(statement);
            roundBracketTuple(tuple);
            return tuple;
        }

        // (statement)... 语句等表达式，e.g. (a + b) || (a = c + d) || (max a b)
        if (parser.currentIs(')')) {
            parser.next(); // eat ')'
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
        skipBlank('\n');
        if (parser.currentIs(')')) {
            parser.next(); // eat ')'
            return;
        }

        // (
        if (!parser.hasNext()) {
            throw new ParseException("lisp 表达式解析错误，是否缺少 ')' ?", parser);
        }

        try {
            // (lisp phrase...
            Expression phrase = phrase();
            lisp.add(phrase);
        } catch (Exception e) {
            throw new ParseException("lisp 表达式解析错误，参数只支持词语表达式，是否缺少 ')' ?", parser, e);
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
        skipBlank('\n');
        // (tuple)
        if (parser.currentIs(')')) {
            parser.next(); // eat ')'
            return;
        }

        if (parser.currentIs(',')) {
            parser.next(); // eat ','
            skipBlank('\n');
            // (tuple,)... 允许最后多个空格
            if (parser.currentIs(')')) {
                return;
            }
        } else {
            throw new ParseException("tuple 表达式解析错误，缺少 ','", parser);
        }

        // (a, b
        if (!parser.hasNext()) {
            throw new ParseException("元组表达式解析错误，缺少 ')'。", parser);
        }

        // (tuple, param,... 解析参数，元组中参数不支持命令式调用
        try {
            Expression param = statement(false);
            tuple.add(param);
        } catch (Exception e) {
            throw new ParseException("元组表达式解析错误，是否缺少 ',' ?", parser);
        }

        roundBracketTuple(tuple);
    }

    /**
     * 解析 String
     * @return
     */
    private StringExpression stringExpression() {
        parser.next(); // eat "
        StringBuilder sb = new StringBuilder();
        while (!parser.currentIs('"') && parser.hasNext()) {
            sb.append(parser.current());
            parser.next();
        }

        if (parser.currentIs('"')) {
            parser.next(); // eat "
        }
        return new StringExpression(sb.toString());
    }
}
