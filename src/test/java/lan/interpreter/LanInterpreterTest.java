package lan.interpreter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanInterpreterTest {
    private static final Logger logger = LoggerFactory.getLogger(LanInterpreterTest.class);

    @Test
    public void squareBracketTest() {
        logger.debug("列表测试");
        ReplEngine.printExpr("[foo, bar hello + world 3 + 2 5]");
    }

    @Test
    public void roundBracketTest() {
        logger.debug("括号表达式测试");
        ReplEngine.printExpr("(foo, bar = 3 + 2\n, 5)");

        ReplEngine.printExpr("(cmd p1 = 5 + \n6 9 + 8 10)");

        ReplEngine.printExpr("(a, b\n, ,\n,c, d)");
        ReplEngine.printExpr("(,)");
        ReplEngine.printExpr("(a,)");
        ReplEngine.printExpr("(,a)");
    }

    @Test
    public void assignTest() {
        logger.debug("赋值表达式测试");
        ReplEngine.printExpr("a = 3 + 2 = 5 = 6 + 7");
    }

    @Test
    public void commandTest() {
        logger.debug("命令表达式测试");
        ReplEngine.printExpr("cmd a 2 + 3 c = 4 + 5 hello");
        ReplEngine.printExpr("cmd a");
        ReplEngine.printExpr("cmd a b, \n 3 + 2, d");
    }

    @Test
    public void operatorTest() {
        logger.debug("运算符测试");
        ReplEngine.printResult("\"hello\" + \"world\" + \n 2 + 5");

        ReplEngine.printResult("123 + 456 + 789");
    }

}
