package com.laosuye.mychat.common.commm.util;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * SpEl表达式工具类
 */
public class SpElUtils {

    /**
     * SpEl表达式解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 参数名称发现器
     */
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 获取方法的key
     * @param method 方法
     * @return 方法的key
     */
    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }

    /**
     * 解析SpEl表达式
     * @param method 方法
     * @param args 方法参数
     * @param spEl SpEl表达式
     * @return 解析后的字符串
     */
    public static String parseSpEl(Method method, Object[] args, String spEl) {
        String[] params = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method)).orElse(null);
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        Expression expression = PARSER.parseExpression(spEl);
        return expression.getValue(context, String.class);
    }
}
