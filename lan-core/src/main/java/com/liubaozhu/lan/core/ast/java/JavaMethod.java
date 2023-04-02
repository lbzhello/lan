package com.liubaozhu.lan.core.ast.java;

/**
 * 表示 java 方法引用
 */

import com.liubaozhu.lan.core.ast.BaseExpression;
import com.liubaozhu.lan.core.ast.expression.NumberExpression;
import com.liubaozhu.lan.core.ast.expression.StringExpression;
import com.liubaozhu.lan.core.ast.Expression;
import com.liubaozhu.lan.core.ast.Value;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * java 方法引用，不知道参数及返回值类型，调用时通过参数推导
 */
public class JavaMethod extends BaseExpression implements MethodInfo {
    private String methodName = "";
    private Class<?> methodClass;
    private Object methodObject;

    public JavaMethod(Object methodObject, String methodName) {
        this.methodName = methodName;
        this.methodObject = methodObject;
        this.methodClass = methodObject.getClass();
    }

    /**
     * 先提供方法名称，后续需调用 {@link #bindTo} 方法配置接收者
     * @see #bindTo(Object)
     * @param methodName
     */
    public JavaMethod(String methodName) {
        this.methodName = methodName;
    }

    /**
     * 方法接收者，即方法所属对象
     * @param methodObject
     */
    @Override
    public JavaMethod bindTo(Object methodObject) {
        this.methodObject = methodObject;
        this.methodClass = methodObject.getClass();
        return this;
    }



    @Override
    public Expression invoke(Object... args) {
        Class<?>[] paramTypes = new Class[size()];
        Object[] paramValues = new Object[size()];
        for (int i = 0; i < size(); i++) {
            Object param = args[i];
            if (param instanceof StringExpression) {
                paramTypes[i] = String.class;
                paramValues[i] = ((StringExpression) param).getValue();
            } else if (param instanceof NumberExpression) {
                //number as int
                paramTypes[i] = int.class;
                paramValues[i] = ((NumberExpression) param).getValue().intValue();
            } else if (param instanceof JavaObject) {
                paramTypes[i] = ((JavaObject) param).getObject().getClass();
                paramValues[i] = ((JavaObject) param).getObject();
            } else {  //type Expression
//                paramTypes[i] = param.getClass();
                paramTypes[i] = param.getClass();
                paramValues[i] = param;
            }
        }

        try {
            Method method = methodClass.getMethod(methodName, paramTypes);
            MethodHandle methodHandle = MethodHandles.lookup().unreflect(method)
                    .asSpreader(Object[].class, size()).bindTo(methodObject);
            Object rstObj = methodHandle.invoke(paramValues);
            if (rstObj instanceof Expression) {
                return (Expression) rstObj;
            } else { // 当作 Java 对象返回
                return new JavaObject(rstObj);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Value.NIL;
    }

    @Override
    public String toString() {
        return Objects.isNull(methodObject) ? Objects.toString(methodClass) : methodObject.toString();
    }
}