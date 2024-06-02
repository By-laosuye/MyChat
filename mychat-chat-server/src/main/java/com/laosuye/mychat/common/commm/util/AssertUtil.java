package com.laosuye.mychat.common.commm.util;

import cn.hutool.core.util.ObjectUtil;
import com.laosuye.mychat.common.commm.exception.BusinessException;
import com.laosuye.mychat.common.commm.exception.CommonErrorEnum;
import com.laosuye.mychat.common.commm.exception.ErrorEnum;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.text.MessageFormat;
import java.util.*;

/**
 * 校验工具类
 */
public class AssertUtil {

    /**
     * 校验到失败就结束
     */
    private static final Validator failFastValidator = Validation.byProvider(HibernateValidator.class)
            .configure()
            .failFast(true)
            .buildValidatorFactory().getValidator();

    /**
     * 全部校验
     */
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 使用快速失败验证策略对对象进行验证。
     * 如果对象不满足验证注解的条件，则立即抛出异常。
     * 这种验证方式适用于需要在第一时间发现并处理错误的场景，避免了不必要的后续操作。
     *
     * @param obj 待验证的对象。
     * @param <T> 泛型参数，表示待验证对象的类型。
     */
    public static <T> void fastFailValidate(T obj) {
        // 使用快速失败验证器对对象进行验证，获取所有验证不通过的约束违规信息。
        Set<ConstraintViolation<T>> constraintViolations = failFastValidator.validate(obj);
        // 如果存在约束违规信息，即对象验证不通过，则抛出异常。
        if (!constraintViolations.isEmpty()) {
            // 抛出异常，异常信息为第一个验证失败的错误消息。
            throwException(CommonErrorEnum.PARAM_INVALID, constraintViolations.iterator().next().getMessage());
        }
    }


    /**
     * 对给定对象进行全属性验证，如果存在验证不通过的情况，则抛出异常。
     * 此方法使用Java Bean Validation规范（JSR 380）进行验证，需要在对象的属性上使用相应的注解定义验证规则。
     * 验证失败时，会拼接所有失败的验证消息，并抛出自定义的异常。
     *
     * @param obj 待验证的对象。
     * @param <T> 泛型参数，表示待验证对象的类型。
     */
    public static <T> void allCheckValidateThrow(T obj) {
        // 使用Bean Validation验证器对对象进行验证，返回所有不通过的验证违规信息。
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        // 如果存在验证违规信息，则进行异常处理。
        if (!constraintViolations.isEmpty()) {
            StringBuilder errorMsg = new StringBuilder();
            // 遍历所有验证违规信息，拼接成错误消息字符串。
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                // 拼接属性路径和错误消息，以逗号分隔。
                //拼接异常信息
                errorMsg.append(violation.getPropertyPath().toString()).append(":").append(violation.getMessage()).append(",");
            }
            // 去除最后一个逗号，为抛出异常做准备。
            //去掉最后一个逗号
            throwException(CommonErrorEnum.PARAM_INVALID, errorMsg.toString().substring(0, errorMsg.length() - 1));
        }
    }



    /**
     * 对给定的对象进行全方位的验证，收集并返回所有验证失败的错误信息。
     *
     * @param obj 待验证的对象。
     * @return 包含验证错误信息的映射。如果对象完全通过验证，则返回空映射。
     */
    public static <T> Map<String, String> allCheckValidate(T obj) {
        // 使用验证器对对象进行验证，收集所有违反约束的违规行为
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj);
        // 如果存在违规行为
        if (!constraintViolations.isEmpty()) {
            // 初始化一个映射来存储错误消息
            Map<String, String> errorMessages = new HashMap<>();
            // 遍历所有违规行为
            Iterator<ConstraintViolation<T>> iterator = constraintViolations.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation<T> violation = iterator.next();
                // 将违规属性路径作为键，违规消息作为值，存储到错误消息映射中
                errorMessages.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            // 返回包含所有错误消息的映射
            return errorMessages;
        }
        // 如果没有违规行为，返回空映射
        return new HashMap<>();
    }


    /**
     * 验证给定的布尔表达式是否为真。如果表达式为假，则抛出一个运行时异常。
     * 这个方法用于在程序的关键点检查条件，以确保程序的逻辑正确性。
     *
     * @param expression 要验证的布尔表达式。
     * @param msg 如果表达式为假，将用于抛出异常的消息字符串。
     * @throws IllegalArgumentException 如果表达式为假，则抛出此异常。
     */
    //如果不是true，则抛异常
    public static void isTrue(boolean expression, String msg) {
        if (!expression) {
            throwException(msg);
        }
    }


    /**
     * 验证给定的表达式是否为真。如果表达式为假，则根据提供的错误代码和参数抛出异常。
     * 此方法用于在程序的特定点验证条件，以确保程序的逻辑正确性或数据的有效性。
     *
     * @param expression 要验证的布尔表达式。
     * @param errorEnum 表示错误类型的枚举，用于确定抛出的异常类型。
     * @param args 可变参数，用于格式化错误消息或提供给抛出的异常的额外信息。
     */
    public static void isTrue(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (!expression) {
            throwException(errorEnum, args);
        }
    }


    /**
     * 检查给定的布尔表达式是否为假。如果表达式为真，则抛出一个运行时异常。
     * 这个方法的设计目的是在编程中作为一种断言，用于验证某些条件是否不成立。
     * 当我们期望一个条件不应该满足，但又无法通过常规的条件判断避免程序执行错误流程时，可以使用此方法。
     *
     * @param expression 要检查的布尔表达式。
     * @param msg 如果表达式为真时，抛出异常时使用的消息。
     * @throws RuntimeException 如果表达式为真，则抛出运行时异常，异常消息为传入的msg。
     */
    public static void isFalse(boolean expression, String msg) {
        if (expression) {
            throwException(msg);
        }
    }


    /**
     * 检查给定的布尔表达式是否为假。如果表达式为真，则根据提供的错误代码和可变参数抛出异常。
     * 这个方法的目的是在编程中提供一种明确的、可读性强的异常处理机制，使得当一个条件违反了预期的假设定时，能够清晰地表达出错的性质和上下文。
     *
     * @param expression 要检查的布尔表达式。如果此表达式的计算结果为真，则抛出异常。
     * @param errorEnum  错误枚举，用于标识要抛出的异常类型。它应该包含关于错误的详细信息，如错误代码和错误消息模板。
     * @param args       可变参数，用于错误消息的参数化。这些参数将被插入到错误消息模板中，以提供更具体的错误上下文信息。
     * @throws RuntimeException 如果表达式的计算结果为真，则根据错误枚举抛出相应的异常。异常的具体类型和消息由错误枚举定义。
     */
    public static void isFalse(boolean expression, ErrorEnum errorEnum, Object... args) {
        if (expression) {
            throwException(errorEnum, args);
        }
    }


    /**
     * 确保对象不为空，如果为空则抛出异常。
     * 此方法用于验证传入的对象是否为空，如果为空，则通过抛出异常的方式来提示调用者。
     * 这是一种用于参数验证的常用手段，旨在提前发现潜在的空指针异常问题，从而避免程序在运行时发生错误。
     *
     * @param obj 待检查的对象。
     * @param msg 当对象为空时，抛出异常所携带的信息。
     * @throws IllegalArgumentException 如果对象为空，则抛出此异常，异常信息为msg。
     */
    public static void isNotEmpty(Object obj, String msg) {
        if (isEmpty(obj)) {
            throwException(msg);
        }
    }


    /**
     * 检查对象是否为空，如果为空，则抛出异常。
     * 此方法用于在调用方未提供有效对象时，强制执行非空要求，以避免潜在的空指针异常或其他错误。
     *
     * @param obj 待检查的对象，可以是任何类型的对象。
     * @param errorEnum 代表错误类型的枚举，用于指定抛出的异常类型。
     * @param args 可变参数，用于在抛出异常时提供额外的错误信息。
     * @throws 自定义异常，根据errorEnum和args参数抛出相应的异常。
     */
    public static void isNotEmpty(Object obj, ErrorEnum errorEnum, Object... args) {
        if (isEmpty(obj)) {
            throwException(errorEnum, args);
        }
    }


    /**
     * 检查对象是否为空，并在为空时抛出异常。
     * 此方法旨在提供一种明确的异常抛出机制，以处理对象为空的情况。
     * 它是 isEmpty 方法的变体，允许通过传入自定义消息来抛出更具体的异常信息。
     *
     * @param obj 要检查的对象，可以是任何类型的对象。
     * @param msg 当对象为空时，抛出异常时要使用的自定义消息。
     * @throws IllegalArgumentException 如果对象为空，则抛出此异常，异常消息为传入的 msg。
     */
    public static void isEmpty(Object obj, String msg) {
        if (!isEmpty(obj)) {
            throwException(msg);
        }
    }


    /**
     * 检查两个对象是否相等。如果它们不相等，则抛出异常。
     * 此方法使用ObjectUtil的equal方法来比较两个对象，该方法能够处理null值的比较。
     *
     * @param o1 第一个对象，可以是任何类型的对象。
     * @param o2 第二个对象，可以是任何类型的对象。
     * @param msg 当两个对象不相等时，抛出异常时使用的消息字符串。
     * @throws AssertionError 如果两个对象不相等，则抛出AssertionError异常，包含msg作为消息。
     */
    public static void equal(Object o1, Object o2, String msg) {
        if (!ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }


    /**
     * 检查两个对象是否不相等。如果它们相等，则抛出异常。
     * 此方法用于验证两个对象的不等价性，以确保某个逻辑的正确性。
     * 当期望两个对象不相等时，可以调用此方法进行验证。
     *
     * @param o1 第一个对象，用于比较。
     * @param o2 第二个对象，用于比较。
     * @param msg 当两个对象相等时，抛出异常所使用的消息。
     * @throws IllegalArgumentException 如果两个对象相等，则抛出此异常。
     */
    public static void notEqual(Object o1, Object o2, String msg) {
        if (ObjectUtil.equal(o1, o2)) {
            throwException(msg);
        }
    }


    /**
     * 检查对象是否为空。
     * <p>
     * 该方法通过调用ObjectUtil的isEmpty方法来判断传入的对象是否为空。空对象定义为null或其toString()方法返回空字符串。
     * 这个方法提供了一个统一的方式来检查对象的空状态，避免了直接的null检查或字符串空检查的冗余代码。
     *
     * @param obj 要检查的对象，可以是任何类型的对象。
     * @return 如果对象为空，则返回true；否则返回false。
     */
    private static boolean isEmpty(Object obj) {
        return ObjectUtil.isEmpty(obj);
    }

    /**
     * 抛出一个异常。
     * 此方法通过调用另一个重载的throwException方法来抛出异常，它允许指定一个异常类型和一个错误消息。
     * 如果异常类型为null，则默认为抛出IllegalArgumentException。
     * 使用此方法的目的是为了提供一个统一的方式来抛出异常，从而简化代码并提高可读性。
     *
     * @param msg 异常的消息部分，可以是null。
     * @throws IllegalArgumentException 如果异常类型为null且传入的消息不为null，则抛出此异常。
     */
    private static void throwException(String msg) {
        throwException(null, msg);
    }


    /**
     * 根据错误代码和参数抛出业务异常。
     *
     * 此方法旨在通过错误代码和可变的参数来抛出具体的业务异常。如果传入的错误代码为null，
     * 则默认使用BUSINESS_ERROR错误代码。错误消息会根据错误代码和参数进行格式化，使得
     * 错误消息可以包含动态内容，提高错误信息的准确性和可用性。
     *
     * @param errorEnum 错误代码枚举，包含错误代码和错误消息模板。
     * @param arg 可变参数，用于错误消息的格式化。根据错误代码的不同，可能需要传入不同的参数。
     * @throws BusinessException 始终抛出业务异常，包含错误代码和格式化后的错误消息。
     */
    private static void throwException(ErrorEnum errorEnum, Object... arg) {
        // 检查错误代码枚举是否为null，如果是，则使用默认的错误代码
        if (Objects.isNull(errorEnum)) {
            errorEnum = CommonErrorEnum.BUSINESS_ERROR;
        }
        // 根据错误代码和参数格式化错误消息，然后抛出业务异常
        throw new BusinessException(errorEnum.getErrorCode(), MessageFormat.format(errorEnum.getErrorMsg(), arg));
    }



}