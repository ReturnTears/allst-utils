package allst.utils.tools;

import java.io.Serializable;

/**
 * @author June
 * @since 2021年09月
 */
public interface ValueItem extends Serializable {
    /**
     * 获取文本
     *
     * @return
     */
    public String getText();

    /**
     * 获取值
     *
     * @return
     */
    public String getValue();

    /**
     * 获取数据
     *
     * @return
     */
    public Object getData();
}
