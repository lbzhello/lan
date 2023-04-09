package com.liubaozhu.lan.core.lexer;

import org.junit.jupiter.api.Test;

public class LanLexerTest {
    @Test
    public void textTest() {
        LanLexer parser = LanLexer.text("hello () nihao ma");
        System.out.println();
    }

    /**
     * \uFFFF 不是空白字符
     */
    @Test
    public void charTest() {
        System.out.println(Character.isWhitespace('\uFFFF'));
    }
}
