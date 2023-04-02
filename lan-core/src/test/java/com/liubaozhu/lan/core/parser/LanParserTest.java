package com.liubaozhu.lan.core.parser;

import org.junit.jupiter.api.Test;

public class LanParserTest {
    @Test
    public void textTest() {
        LanParser parser = LanParser.text("hello () nihao ma");
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
