package allst.utils.service;

import allst.utils.tools.StringUtil;

import java.util.Collection;

/**
 * 仅用于规范返回的异常.服务不允许返回其他类型的异常.
 * 注意:在未进行配置的情况下,必须RuntimeExcetion类型的异常才能进行回滚.
 *
 * @author June
 * @since 2021年09月
 */
public class ServiceException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 2075501432978112001L;

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(String message) {
        super(message);
    }

    /**
     * 通过String.format()格式化消息
     */
    public ServiceException(String message, Object... params) {
        super(String.format(message, params));
    }

    /**
     * 返回格式化的消息
     */
    public static ServiceException format(String message, Object... params) {
        return new ServiceException(String.format(message, params));
    }

    public static ServiceException format(String message) {
        return new ServiceException(message);
    }

    public static ServiceException format(String message, Exception e) {
        return new ServiceException(message, e);
    }

    /**
     * 直接抛出异常
     */
    public static void error(String message) {
        throw new ServiceException(message);
    }

    /**
     * 直接抛出异常.通过String.format格式化消息
     *
     * @param message
     */
    public static void error(String message, Object... params) {
        throw new ServiceException(message, params);
    }

    /**
     * 断言值必须为false
     *
     * @param val
     * @param message
     */
    public static void assertFalse(boolean val, String message, Object... params) {
        if (val == true) throw new ServiceException(message, params);
    }

    /**
     * 断言值必须为true
     *
     * @param val
     * @param message
     */
    public static void assertTrue(boolean val, String message, Object... params) {
        if (val != true) throw new ServiceException(message, params);
    }

    /**
     * 断言不为null.如果为null则抛出异常
     *
     * @param val
     * @param message
     */
    public static void assertNotNull(Object val, String message, Object... params) {
        if (val == null) throw new ServiceException(message, params);
    }

    /**
     * 断言为null.如果非null则抛出异常.
     *
     * @param val
     * @param message
     */
    public static void assertNull(Object val, String message, Object... params) {
        if (val != null) throw new ServiceException(message, params);
    }

    /**
     * 断言不为空
     *
     * @param val
     * @param message
     */
    public static void assertNotEmpty(Object val, String message, Object... params) {
        if (!StringUtil.checkEmpty(val)) throw new ServiceException(message, params);
    }

    /**
     * 断言不为空
     */
    public static void assertNotEmpty(String val, String message, Object... params) {
        if (!StringUtil.checkEmpty(val)) throw new ServiceException(message, params);
    }

    /**
     * 断言不为空
     */
    public static void assertNotEmpty(Collection<?> list, String message, Object... params) {
        if (list == null || list.size() == 0) throw new ServiceException(message, params);
    }

    /**
     * 断言为空.非空则抛出异常
     */
    public static void assertEmpty(Object val, String message, Object... params) {
        if (StringUtil.checkEmpty(val)) throw new ServiceException(message, params);
    }

    /**
     * 断言为空.非空则抛出异常
     */
    public static void assertEmpty(String val, String message, Object... params) {
        if (StringUtil.checkEmpty(val)) throw new ServiceException(message, params);
    }

    /**
     * 断言为空.非空则抛出异常
     */
    public static void assertEmpty(Collection<?> list, String message, Object... params) {
        if (list != null && list.size() > 0) throw new ServiceException(message, params);
    }

    /**
     * 断言相等	如果不相等则抛出异常
     */
    public static void assertEqual(String a, String b, String message, Object... params) {
        if (!StringUtil.checkEqual(a, b)) throw new ServiceException(message, params);
    }

    /**
     * 断言相等.	如果不相等则抛出异常
     */
    public static void assertEqual(Object a, Object b, String message, Object... params) {
        if (!StringUtil.checkEqual(a, b)) throw new ServiceException(message, params);
    }

    /**
     * 断言不相等.	如果相等则抛出异常
     */
    public static void assertNotEqual(String a, String b, String message, Object... params) {
        if (StringUtil.checkEqual(a, b)) throw new ServiceException(message, params);
    }

    /**
     * 断言不相等.	如果相等则抛出异常
     *
     * @param a
     * @param b
     * @param message
     */
    public static void assertNotEqual(Object a, Object b, String message, Object... params) {
        if (StringUtil.checkEqual(a, b)) throw new ServiceException(message, params);
    }

    /*********无参方法*******************************************************************************/

    /**
     * 断言值必须为false
     *
     * @param val
     * @param message
     */
    public static void assertFalse(boolean val, String message) {
        if (val == true) throw new ServiceException(message);
    }

    /**
     * 断言值必须为true
     *
     * @param val
     * @param message
     */
    public static void assertTrue(boolean val, String message) {
        if (val != true) throw new ServiceException(message);
    }

    /**
     * 断言不为null.如果为null则抛出异常
     *
     * @param val
     * @param message
     */
    public static void assertNotNull(Object val, String message) {
        if (val == null) throw new ServiceException(message);
    }

    /**
     * 断言为null.如果非null则抛出异常.
     *
     * @param val
     * @param message
     */
    public static void assertNull(Object val, String message) {
        if (val != null) throw new ServiceException(message);
    }

    /**
     * 断言不为空
     *
     * @param val
     * @param message
     */
    public static void assertNotEmpty(Object val, String message) {
        if (!StringUtil.checkEmpty(val)) throw new ServiceException(message);
    }

    /**
     * 断言不为空
     *
     * @param val
     * @param message
     */
    public static void assertNotEmpty(String val, String message) {
        if (!StringUtil.checkEmpty(val)) throw new ServiceException(message);
    }

    /**
     * 断言不为空
     */
    public static void assertNotEmpty(Collection<?> list, String message) {
        if (list == null || list.size() == 0) throw new ServiceException(message);
    }

    /**
     * 断言为空.非空则抛出异常
     *
     * @param val
     * @param message
     */
    public static void assertEmpty(Object val, String message) {
        if (StringUtil.checkEmpty(val)) throw new ServiceException(message);
    }

    /**
     * 断言为空.非空则抛出异常
     *
     * @param val
     * @param message
     */
    public static void assertEmpty(String val, String message) {
        if (StringUtil.checkEmpty(val)) throw new ServiceException(message);
    }

    /**
     * 断言为空.非空则抛出异常
     */
    public static void assertEmpty(Collection<?> list, String message) {
        if (list != null && list.size() > 0) throw new ServiceException(message);
    }

    /**
     * 断言相等	如果不相等则抛出异常
     *
     * @param a
     * @param b
     * @param message
     */
    public static void assertEqual(String a, String b, String message) {
        if (!StringUtil.checkEqual(a, b)) throw new ServiceException(message);
    }

    /**
     * 断言相等.	如果不相等则抛出异常
     *
     * @param a
     * @param b
     * @param message
     */
    public static void assertEqual(Object a, Object b, String message) {
        if (!StringUtil.checkEqual(a, b)) throw new ServiceException(message);
    }

    /**
     * 断言不相等.	如果相等则抛出异常
     *
     * @param a
     * @param b
     * @param message
     */
    public static void assertNotEqual(String a, String b, String message) {
        if (StringUtil.checkEqual(a, b)) throw new ServiceException(message);
    }

    /**
     * 断言不相等.	如果相等则抛出异常
     *
     * @param a
     * @param b
     * @param message
     */
    public static void assertNotEqual(Object a, Object b, String message) {
        if (StringUtil.checkEqual(a, b)) throw new ServiceException(message);
    }
    /*********结束无参方法*******************************************************************************/
}
