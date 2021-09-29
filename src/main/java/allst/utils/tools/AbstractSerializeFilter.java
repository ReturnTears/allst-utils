package allst.utils.tools;

/**
 * @author June
 * @since 2021年09月
 */
public class AbstractSerializeFilter {
    /**
     * 根据类名和属性名称创建key
     */
    protected String createKey(Class<?> clazz, String name) {
        StringBuilder sb = new StringBuilder(name);
        if (clazz != null) {
            sb.append('-');
            sb.append(clazz.getName());
        }
        return sb.toString();
    }

    /**
     * 创建简单的Key
     */
    protected String createSimpleKey(Class<?> clazz, String name) {
        return name;
    }
}
