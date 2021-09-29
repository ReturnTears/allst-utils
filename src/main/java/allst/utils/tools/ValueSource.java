package allst.utils.tools;

/**
 * @author June
 * @since 2021年09月
 */
public interface ValueSource {
    /**
     * 获取下拉项
     *
     * @param params
     *
     * @return
     */
    public ValueItems getItems(Object... params);

    /**
     * @return
     */
    public ValueItems getItems();

    /**
     * 添加默认项
     *
     * @param vi
     *
     * @return
     */
    public ValueSource addDefault(ValueItem vi);

    /**
     * 重新加载值源
     *
     * @return
     */
    public ValueSource reload();
}
