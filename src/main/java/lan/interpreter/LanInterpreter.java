package lan.interpreter;

import cn.hutool.core.util.NumberUtil;
import lan.ast.BaseExpression;
import lan.ast.Expression;
import lan.ast.expression.*;
import lan.base.Definition;
import lan.exception.ParseException;
import lan.parser.TextParser;
import lan.parser.Token;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 语言解析器
 * 关键字通过额外的解析器解析，支持扩展
 * word = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
 * term = word || word word
 * operator = term || term op term
 * command = term || term operator operator || operator operator operator
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

    public Set<Character> delimiters = Set.of(
            COMMA,
            SEMICOLON,
            POINT,
            COLON,
            ROUND_BRACKET_LEFT,
            ROUND_BRACKET_RIGHT,
            CURLY_BRACKET_LEFT,
            CURLY_BRACKET_RIGHT,
            SQUARE_BRACKET_LEFT,
            SQUARE_BRACKET_RIGHT,
            ANGLE_BRACKET_LEFT,
            ANGLE_BRACKET_RIGHT,
            SLASH_LEFT,
            SLASH_RIGHT,
            QUOTE_MARK_DOUBLE,
            QUOTE_MARK_SINGLE,
            BACK_QUOTE,
            ASSIGN
    );

    /**
     * 基础文本解析器
     */
    private TextParser parser;

    /**
     * 基础语言定义，运算符，关键字等
     */
    private Definition definition;

    /**
     * 解析栈，有时候需要解析下一个单词 {@link #term}才能确定是否返回;
     * 比如 {@link #operator}, 需要判断下一个单词是否是运算符
     * 可以将已经解析的下一个单词推入此栈中，供其他表达式获取
     */
    private Deque<Expression> termStack = new ArrayDeque<>();

    /**
     * 关键字解析器
     */
    private Map<String, Interpreter> keywordInterpreter = new HashMap<>();

    public LanInterpreter(TextParser parser, Definition definition, @Nullable Map<String, Interpreter> keywordInterpreter) {
        this.parser = parser;
        this.definition = definition;
        this.keywordInterpreter = keywordInterpreter;
        this.parser.addDelimiters(delimiters);
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
     * expr = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
     * @return
     */
    public Expression word() {
        parser.skipBlank(); // 去掉空白，换行字符
        char current = parser.current();
        if (current == ROUND_BRACKET_LEFT) { // (
            return roundBracketExpr();
        } else if (current == SQUARE_BRACKET_LEFT) { // [

        } else if (current == CURLY_BRACKET_LEFT) { // [

        } else if (current == QUOTE_MARK_DOUBLE) { // "
            return stringExpression();
        } else if (current == QUOTE_MARK_SINGLE) { // '

        } else if (current == BACK_QUOTE) { // `

        } else if (isLineBreak()) {
            return Token.EOL;
        } else if (parser.isDelimiter(current)) { // 间隔符
            parser.next(); // 先去掉
            return word();
        } else if (parser.hasNext()) { // 数字，字面量等
            String token = parser.nextWord();
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
     * 获取一个单词
     * term = word || word word
     * foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression term() {
        Expression pop = pop();
        if (Objects.isNull(pop)) {
            return term(word());
        }

        return pop;
    }

    /**
     * 解析词语，根据 left 继续向下解析一次
     * 不包括运算符表达式 {@link #operator} 和命令表达
     * 式 {@link #command(Expression)}
     * e.g. foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression term(Expression left) {
        if (isBlank()) { // left ... 中间有空白字符
            skipBlank();
            // expr (...) // 命令调用
            if (parser.currentIs('(') || parser.currentIs('[') || parser.currentIs('{')) {
                return left;
            }
            return term(left);
        }
        char current = parser.current();
        if (current == '(') { // 函数调用 expr(...)
            Expression expression = roundBracketExpr();
            // expr(...)(...)
            return term(expression);
        } else if (current == '[') { // expr1, expr2...

        } else if (current == '{') {

        } else if (current == '.') {

        } else if (current == ':') {

        }

        return left;
    }

    /**
     * 从头解析一个句子，即由表达式 {@link #word()}，运算符 {@link #operator(Expression, Expression)}，
     * 命令 {@link #command(Expression)} 等组成的语句
     * 语句结合顺序：expr -> operator -> command
     * operator || command || term, term, term || statement = statement
     * @return
     */
    public Expression statement() {
        Expression term = term();
        if (term == Token.EOF) { // 结束解析
            return Token.EOF;
        }
        Expression statement = statement(term);
        skipBlank('\n', ';');
        return statement;
    }

    /**
     * 解析句子。根据句子开头，解析句子接下来的语句结构
     * 解析完成后吃掉行结束符
     * operator || command || term, term, term || statement = statement
     * e.g.
     * head a b... || head + b...
     * @param head
     * @return
     */
    private Expression statement(Expression head) {
        head = operatorOrReturn(head, true);

        if (isStatementEndSkipBlank()) {
            Expression pop = pop();
            // 运算符可能预取了下一个单词
            if (pop != null) { // head pop
                return new EvalExpression(head, pop);
            }
            return head;
        }

        // head (... 命令表达式
        Expression commandExpr = command(head);

        // 句子解析结束
        if (isLineBreakSkipBlank()) {
            parser.next(); // eat '\n'
        }

        return commandExpr;
    }

    /**
     * 赋值表达式
     * expr = operator
     * expr = p1 + p2 = p3 + p4 + p5
     * @param left
     * @return
     */
    private Expression assignExpression(Expression left) {
        parser.next();
        if (parser.currentIs('=')) { // comma ==... 运算符
            parser.next();
            return operator(left, new SymbolExpression("=="));
        }
        skipBlank('\n'); // 跳过空白和换行
        if (isStatementEnd()) { // comma =
            return left;
        }

        Expression expr = operatorOrReturn(term(), true); // left = expr
        skipBlank();
        if (parser.currentIs('=')) { // left = expr =...
            expr = assignExpression(expr); // left = (expr =...
        }
        AssignExpression assign = new AssignExpression();
        assign.add(left);
        assign.add(expr);
        return assign;
    }

    /**
     * 命令方式的函数调用
     * 支持逗号分割参数
     * command = term || term operator operator || operator operator operator || term operator, operator, operator
     * e.g.
     * cmd
     * cmd p1 p2
     * cmd p1 + p2 p3 = 3 + 2 p4
     * @return
     */
    private Expression command(Expression cmd) {
        if (isStatementEndSkipBlank()) {
            return cmd;
        }
        Expression term = term();
        return command(cmd, term);
    }

    /**
     * command = term || term operator operator || operator operator operator
     * e.g.
     * cmd
     * cmd p1 p2
     * cmd p1 + p2 p3 = 3 + 2 p4
     * @param cmd
     * @param term 已经解析的下一个词语
     * @return
     */
    private Expression command(Expression cmd, Expression term) {
        BaseExpression baseExpression = new EvalExpression();
        baseExpression.add(cmd);

        // cmd term 语句结束直接返回
        if (isStatementEndSkipBlank()) {
            baseExpression.add(term);
            return baseExpression;
        }

        // cmd term... 解析参数
        List<Expression> params = new ArrayList<>();
        baseExpression.addAll(spaceListExpr(params, term));
        return baseExpression;
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
            throw new IllegalStateException("语法错误");
        }

        // left, term...
        Expression expr = operatorOrReturn(term(), false);
        skipBlank();
        ListExpression list = commaListExpr0(expr);
        list.add(left);
        return list;
    }

    /**
     * 解析运算符，赋值表达式，或者直接返回
     * expr
     * expr1 + expr2
     * expr = expr1 + expr2 = expr3 + expr4
     * @param left
     * @param prefetch 是否支持预取，即 operator 后面是否可以跟表达式
     * @return
     */
    private Expression operatorOrReturn(Expression left, boolean prefetch) {
        if (isStatementEndSkipBlank()) {
            return left;
        }

        if (parser.currentIs(',')) { // left,
            return left;
        }

        if (parser.currentIs('=')) { // left =...
            Expression assignExpression = assignExpression(left);
            return assignExpression;
        }

        Expression term = term();
        if (definition.isOperator(term)) { // left term...
            Expression operator = operator(left, term);
            if (!prefetch && Objects.nonNull(termStack.peek())) {
                throw new IllegalStateException("语法错误，缺少表达式结束符号");
            }

            return operator;
        }

        if (prefetch) {
            // 支持预取，放入栈中
            push(term);
            return left;
        }

        throw new IllegalStateException("语法错误，缺少表达式结束符号");
    }

    /**
     * 解析空格分割的列表
     * term term term || term operator operator || operator operator operator
     * @param list 表达式容器
     * @return
     */
    private List<Expression> spaceListExpr(List<Expression> list, Expression term) {
        if (isStatementEndSkipBlank()) {
            list.add(term); // [list term]
            return list;
        }

        Expression operatorOrReturn = operatorOrReturn(term, true);
        list.add(operatorOrReturn);

        if (isStatementEndSkipBlank()) {
            // 运算符可能预取了下一个单词，这里加上
            Expression pop = pop();
            if (Objects.nonNull(pop)) {
                list.add(pop);
            }
            return list;
        }

        return spaceListExpr(list, term());

    }

    /**
     * 是否空白字符
     * @return
     */
    private boolean isBlank() {
        return Character.isWhitespace(parser.current()) && parser.currentNot('\n');
    }

    /**
     * 跳过空格和 skipChars
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
    private boolean isLineBreak() {
        return parser.current() == '\n' || parser.current() == ';';
    }

    /**
     * 是换行符或者文档结束
     * @return
     */
    private boolean isLineBreakOrEnd() {
        return isLineBreak() || !parser.hasNext();
    }

    /**
     * 是否语句结束
     * @return
     */
    private boolean isStatementEnd() {
        return isLineBreakOrEnd() || parser.currentMatch(')', ']', '}');
    }

    /**
     * 跳过空格后是否行结束
     * @return
     */
    private boolean isStatementEndSkipBlank() {
        skipBlank();
        return isStatementEnd();
    }

    /**
     * 跳过空格符后，判断当前字符是否换行符
     * @return
     */
    private boolean isLineBreakSkipBlank() {
        skipBlank();
        return isLineBreak();
    }

    /**
     * 解析括号表达式
     * (operator, operator, operator) 列表表达式
     * (cmd operator operator) 命令调用
     * e.g. (expr) || (cmd a + b c = 2) || (operator, operator, operator)
     * @return
     */
    private Expression roundBracketExpr() {

        parser.next(); // eat '('
        skipBlank('\n');

        // ( )
        if (parser.currentIs(')')) {
            parser.next();
            return new ListExpression();
        }

        // (,...
        if (parser.currentIs(',')) {
            skipBlank(',', '\n');
            if (parser.currentIs(')')) { // ( , )
                parser.next();
                return new ListExpression();
            }
            // todo
        }

        // (expr...
        Expression expr = operatorOrReturn(term(), true);

        skipBlank('\n');

        // (expr...)
        if (parser.currentIs(')')) {
            Expression pop = pop();
            if (Objects.nonNull(pop)) { // (expr pop)
                parser.next(); // eat ')'
                return command(expr, pop);
            }

            parser.next(); // eat ')'

            // (expr)
            return expr;
        }

        // (expr...,...
        if (parser.currentIs(',')) {
            Expression pop = pop();
            if (Objects.nonNull(pop)) { // expr pop,...
                throw new ParseException("括号表达式解析错误，多余的符号：','");
            }

            // (expr,...
            ListExpression list = commaListExpr(expr);
            skipBlank('\n');
            if (parser.currentIs(')')) {
                parser.next();
            } else {
                throw new ParseException("括号表达式解析错误，缺少 ')'");
            }
            return list;
        }


        // (expr param...
        EvalExpression eval = new EvalExpression(expr);
        while (!parser.currentIs(')') && parser.hasNext()) {
            Expression param = operatorOrReturn(term(), true);
            eval.add(param);
            skipBlank('\n');
            if (parser.currentMatch(',', ';', ']', '}')) {
                throw new ParseException("括号表达式解析错误，多余的符号 " + parser.current());
            }
        }

        Expression pop = pop();
        if (pop != null) {
            eval.add(pop);
        }

        if (parser.currentIs(')')) {

            parser.next();
        }

        return eval;
    }

    /**
     * 解析运算符表达式
     * @param left
     * @param op
     * @return
     */
    private Expression operator(Expression left, Expression op) {
        if (isStatementEndSkipBlank() || parser.currentIs(',')) {
            return left;
        }

        // Operator binary = definition.createOperator(String.valueOf(op));

        // 运算符等同函数调用
        // left.op 调用类 left 中的函数 op
        PointExpression point = new PointExpression();
        point.add(left);
        point.add(op);

        // op 函数调用
        EvalExpression eval = new EvalExpression();
        eval.add(point); // left.op ...

        Expression right = term();
        if (isStatementEndSkipBlank() || parser.currentIs(',')) { // left op right
            eval.add(right);
            return eval;
        }

        if (parser.currentIs('=')) { // left op right =...
            parser.next();
            if (!parser.currentIs('=')) { // (left op right) = ... 赋值表达式
                parser.previous(); // 回退 '='
                eval.add(right);
                return eval;
            }

            push(new SymbolExpression("==")); // left op right == ...
        }

        Expression op2 = term(); // left op right op2...

        if (!definition.isOperator(op2)) {
            // left op right term... 运算符在列表中
            eval.add(right);

            push(op2);

            // 返回运算符表达式 left op right
            return eval;
        }

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
