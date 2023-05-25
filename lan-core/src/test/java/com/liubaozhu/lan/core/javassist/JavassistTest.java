package com.liubaozhu.lan.core.javassist;

import javassist.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class JavassistTest {
    @Test
    public void foo() throws NotFoundException, IOException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get("java.util.function.Function");
        CtMethod method = CtMethod.make("public Integer hello() {return 3;}", cc);
        //cc.setSuperclass(pool.get("test.Point"));
        //method.setBody("System.out.println(\"new body\");");
        method.insertBefore("System.out.println(\"new body\");");
        method.insertParameter(cc);
        method.insertAfter("System.out.println(233);");
        cc.addMethod(method);
        cc.writeFile();
    }
}
