package lan.interpreter;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LanInterpreterTest {
    private static final Logger logger = LoggerFactory.getLogger(LanInterpreterTest.class);

    @Test
    public void roundBracketTest() {
        logger.debug("括号表达式测试");
        ReplEngine.printExpr("(cmd p1 = 5 + 6 7 9 + 8)");
    }

    @Test
    public void assignTest() {
        logger.debug("赋值表达式测试");
        ReplEngine.printExpr("a = 3 + 2 = 5 = 6 + 7");
    }

    @Test
    public void commandTest() {
        logger.debug("命令表达式测试");
        ReplEngine.printExpr("cmd a b 2 + 3 c = 4 + 5");
    }

    @Test
    public void operatorTest() {
        logger.debug("运算符测试");
        ReplEngine.printResult("\"hello\" + \"world\" + 2 + 5");

        ReplEngine.printResult("123 + 456 + 789");
    }

}
