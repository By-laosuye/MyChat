package com.laosuye.mychat.common.commm.util;

import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.SneakyThrows;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-08-02
 */
public class LambdaUtils {
    /**
     * 字段映射
     */
    private static final Map<String, Map<String, ColumnCache>> COLUMN_CACHE_MAP = new ConcurrentHashMap<>();

    /**
     * SerializedLambda 反序列化缓存
     */
    private static final Map<String, WeakReference<com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda>> FUNC_CACHE = new ConcurrentHashMap<>();
    /**
     * 匹配Java字节码或类文件中方法返回类型的描述符。
     */
    private static final Pattern RETURN_TYPE_PATTERN = Pattern.compile("\\(.*\\)L(.*);");

    /**
     * 从Java方法签名中提取参数类型列表
     */
    private static final Pattern PARAMETER_TYPE_PATTERN = Pattern.compile("\\((.*)\\).*");

    private static final WeakConcurrentMap<String, SerializedLambda> cache = new WeakConcurrentMap<>();

    /**
     * 通过Lambda表达式获取返回类型。
     *
     * 此方法专门设计用于处理Lambda表达式，通过分析Lambda表达式的字符串表示形式，
     * 来提取并返回Lambda表达式的返回类型。这种方法的使用场景通常是在运行时，
     * 需要动态处理Lambda表达式的情况下。
     *
     * @param serializable Lambda表达式序列化对象。这个参数被序列化以绕过Lambda表达式的语法限制，
     *                    允许我们从中提取出必要的类型信息。
     * @return Lambda表达式的返回类型。这个类型是作为一个Class对象返回的，可以用于进一步的类型检查或实例化。
     * @throws RuntimeException 如果无法解析Lambda表达式的返回类型，或者类无法加载，则抛出运行时异常。
     */
    public static Class<?> getReturnType(Serializable serializable) {
        // 解析Lambda表达式，获取其方法类型字符串
        String expr = _resolve(serializable).getInstantiatedMethodType();
        // 使用预定义的正则表达式匹配方法类型的字符串，以提取返回类型
        Matcher matcher = RETURN_TYPE_PATTERN.matcher(expr);
        // 检查是否找到匹配项且只有一个匹配组，如果不满足则抛出异常
        if (!matcher.find() || matcher.groupCount() != 1) {
            throw new RuntimeException("获取Lambda信息失败");
        }
        // 从匹配结果中提取类名，并将命名空间中的斜杠替换为点，以符合Java类名的常规表示法
        String className = matcher.group(1).replace("/", ".");
        try {
            // 通过类名加载类，并返回该类的Class对象
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // 如果类无法加载，则抛出运行时异常，并封装原始的ClassNotFoundException
            throw new RuntimeException("无法加载类", e);
        }
    }


    /**
     * 通过SFunction获取返回类型。
     * 使用反射和Lambda表达式来解析函数式接口的具体实现，以获取其返回类型。
     * 此方法主要用于处理函数式接口的返回类型查询，尤其在MyBatis-Plus等框架中，用于动态生成SQL时。
     *
     * @param func 函数式接口，代表一个特定的操作。
     * @param <T> 函数式接口的泛型参数类型。
     * @return 返回该函数式接口实现方法的返回类型。
     * @throws NoSuchFieldException 如果无法找到字段时抛出。
     * @throws IllegalAccessException 如果无法访问字段时抛出。
     */
    @SneakyThrows
    public static <T> Class<?> getReturnType(SFunction<T, ?> func) {
        // 使用LambdaUtils解析func为SerializedLambda对象
        com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda lambda = com.baomidou.mybatisplus.core.toolkit.LambdaUtils.resolve(func);
        // 从SerializedLambda中获取实例化类型的Class对象
        Class<?> aClass = lambda.getInstantiatedType();
        // 将方法名转换为属性名
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        // 获取类中对应的字段
        Field field = aClass.getDeclaredField(fieldName);
        // 设置字段可访问，以绕过访问权限限制
        field.setAccessible(true);
        // 返回字段的类型
        return field.getType();
    }


    /**
     * 根据传入的Serializable对象，获取其表示的Lambda表达式的参数类型列表。
     * 此方法主要用于处理Lambda表达式，在Java中，Lambda表达式会被编译成匿名内部类，
     * 而这个方法的目的是通过反编译的方式，从匿名内部类中提取出Lambda表达式的参数类型。
     *
     * @param serializable 表示一个Lambda表达式的Serializable对象。
     * @return 返回一个包含Lambda表达式所有参数类型的Class对象列表。
     * @throws RuntimeException 如果无法解析Lambda表达式的参数类型，则抛出运行时异常。
     */
    public static List<Class<?>> getParameterTypes(Serializable serializable) {
        // 解析serializable对象，获取其表示的Lambda表达式的参数类型信息
        String expr = _resolve(serializable).getInstantiatedMethodType();
        // 使用预定义的正则表达式匹配参数类型信息
        Matcher matcher = PARAMETER_TYPE_PATTERN.matcher(expr);
        // 检查是否找到匹配项且匹配项的数量是否正确
        if (!matcher.find() || matcher.groupCount() != 1) {
            throw new RuntimeException("获取Lambda信息失败");
        }
        // 从匹配结果中提取参数类型信息
        expr = matcher.group(1);

        // 将参数类型信息分割并处理，去除Java反射中的特殊字符，然后转换为Class对象
        return Arrays.stream(expr.split(";"))
                .filter(StrUtil::isNotBlank) // 过滤掉空字符串
                .map(s -> s.replace("L", "").replace("/", ".")) // 替换特殊字符为Java类路径的常规表示
                .map(s -> {
                    try {
                        return Class.forName(s);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("无法加载类", e);
                    }
                })
                .collect(Collectors.toList());
    }


    /**
     * 解析lambda表达式,加了缓存。
     * 该缓存可能会在任意不定的时间被清除。
     *
     * <p>
     * 通过反射调用实现序列化接口函数对象的writeReplace方法，从而拿到{@link SerializedLambda}<br>
     * 该对象中包含了lambda表达式的所有信息。
     * </p>
     *
     * @param func 需要解析的 lambda 对象
     * @return 返回解析后的结果
     */
    private static SerializedLambda _resolve(Serializable func) {
        return cache.computeIfAbsent(func.getClass().getName(), (key)
                -> ReflectUtil.invoke(func, "writeReplace"));
    }

}
