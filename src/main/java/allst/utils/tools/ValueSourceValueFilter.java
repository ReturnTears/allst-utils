package allst.utils.tools;

import com.alibaba.fastjson.serializer.ValueFilter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author June
 * @since 2021年09月
 */
public class ValueSourceValueFilter extends AbstractSerializeFilter implements ValueFilter {
    protected Map<String, ValueItems> valueItemsMap;

//	protected Map<String,String> ingore

    public ValueSourceValueFilter() {
        valueItemsMap = new HashMap<String, ValueItems>();
    }

    /**
     * 注册值源ValueItems
     */
    public void register(String name, ValueItems vis) {
        register(null, name, vis);
    }

    public void register(Class<?> clazz, String name, ValueItems vis) {
        valueItemsMap.put(createKey(clazz, name), vis);
    }

    /**
     * 通过值源ValueSource和参数获取ValueItems
     */
    public void register(String name, ValueSource vs, Object... params) {
        ValueItems vis = vs.getItems(params);
        register(null, name, vis);
    }

    /**
     * 注册
     * @param clazz
     * @param name
     * @param vs
     * @param params
     */
    public void register(Class<?> clazz, String name, ValueSource vs, Object... params) {
        ValueItems vis = vs.getItems(params);
        register(clazz, name, vis);
    }

    @Override
    public Object process(Object object, String name, Object value) {
        ValueItems vis = findValueItems(object.getClass(), name);
        if (vis != null) {
            return vis.toText(StringUtil.toStringSimple(value, ""));
        }
        return value;
    }

    /**
     * 查找ValueItems
     */
    protected ValueItems findValueItems(Class<?> clazz, String name) {
        String key = createKey(clazz, name);
        ValueItems vis = valueItemsMap.get(key);
        if (vis == null) {
            List<Class<?>> list = ReflectUtil.getSupperAll(clazz);
            for (Class<?> cls : list) {
                key = createKey(cls, name);
                vis = valueItemsMap.get(key);
                if (vis != null) {
                    break;
                }
            }
        }
        //通过简单key
        if (vis == null) {
            key = createSimpleKey(clazz, name);
            vis = valueItemsMap.get(key);
        }
        return vis;
    }
}
