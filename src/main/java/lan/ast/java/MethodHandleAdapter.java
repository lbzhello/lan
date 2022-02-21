package lan.ast.java;

import lan.ast.Expression;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * java 方法引用，知道参数及返回值类型
 */
public class MethodHandleAdapter implements MethodInfo, Expression {
    private MethodHandle methodHandle;

    public MethodHandleAdapter(MethodHandle methodHandle) {
        this.methodHandle = methodHandle;
    }

    public MethodHandleAdapter(Class<?> callerType, String methodName, Class<?> rtType, Class<?>... ptypes) {
        try {
            methodHandle = MethodHandles.lookup()
                    .findVirtual(callerType, methodName, MethodType.methodType(rtType, ptypes));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MethodHandleAdapter bindTo(Object caller) {
        MethodHandle mh = this.methodHandle.bindTo(caller);
        return new MethodHandleAdapter(mh);
    }

    @Override
    public Expression invoke(Object... params) {
        try {
            Object invoke = this.methodHandle.asSpreader(Object[].class, params.length).invoke(params);
            if (invoke instanceof Expression) {
                return (Expression) invoke;
            } else {
                return new JavaObject(invoke);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Expression.NIL;
    }
}

