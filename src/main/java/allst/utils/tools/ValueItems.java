package allst.utils.tools;

import java.util.List;

/**
 * @author June
 * @since 2021年09月
 */
public interface ValueItems extends Iterable<ValueItem> {
    /**
     * 是在是否忽略未找到
     * @param ingore
     */
    public void setIngoreUnfound(boolean ingore);

    /**
     * 返回是否返回会忽略伪造大
     * @return
     */
    public boolean isIngoreUnfound();

    /**
     * 增加下拉项
     * @param item
     */
    public void addItem(ValueItem item);

    /**
     * 增加下拉项
     * @param item
     */
    public void insertItem(ValueItem item, int index);

    /**
     * 获取下拉项
     * @param value
     * @return
     */
    public ValueItem getItem(String value);

    /**
     * 获取所有值源项
     * @return
     */
    public List<ValueItem> getItems();

    /**
     * 将值转换成文本
     * @param value
     * @return
     */
    public String toText(String value);

    /**
     * 将文本转换成值. 不一定精确
     * @param text
     * @return
     */
    public String toValue(String text);

    /**
     * 通过文本查找项
     * @param text
     * @return
     */
    public List<ValueItem> findByText(String text);

    /**
     * 通过文本查找项
     * @param text
     * @return
     */
    public ValueItem findByTextOne(String text);

    /**
     * 获取数据长度
     * @return
     */
    public int getSize();
}
