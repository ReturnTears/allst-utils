package allst.utils.tools;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;

/**
 * @author June
 * @since 2021年09月
 */
public class CollectionUtil {
    /**
     * 数组迭代器
     */
    public static class ArrayIterator<T> implements Iterable<T>, Iterator<T> {

        public Object array = null;

        private int length = 0;
        private int currentIndex = 0;

        public ArrayIterator(Object array) {
            this.array = array;
            this.length = CollectionUtil.getLength(array);
        }

        public ArrayIterator(T[] array) {
            this.array = array;
            this.length = (array == null ? 0 : array.length);
        }

        @Override
        public boolean hasNext() {
            return length > currentIndex;
        }

        @Override
        public T next() {
            T t = (T) Array.get(array, currentIndex);
            currentIndex++;
            return t;
        }

        @Override
        public void remove() {
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }

    }

    /**
     * 空数组迭代器
     */
    public static final Iterator EMPTY_ITERATOR = new ArrayIterator((Object) null);

    /**
     * 对象生成键的接口
     *
     * @param <K>
     * @param <V>
     *
     * @author
     */
    public static interface KeyMaker<K, V> {

        public K getKey(V v);
    }

    /**
     * 集合中的数据进行迭代,允许返回一个值.可以不用使用
     *
     * @param <V>
     *
     * @author pingchungg
     */

    public static interface CollectionIterator<V> {
        public <T> T iterator(V value);
    }

    /**
     * 将集合映射成Map的接口
     *
     * @param <E>
     *
     * @author pingchungg
     */
    public static interface CollectionMapper<K, V, E> {
        public K getKey(E e);

        public V getValue(E e);
    }

    /**
     * map过滤器
     *
     * @author pingchungg
     */
    public static abstract class MapFilter<K, V> {
        /**
         * 操作
         *
         * @param newMap
         * @param et
         */
        public abstract void filter(Map<K, V> newMap, Map.Entry<K, V> et);

        /**
         * 将实体放入map
         *
         * @param newMap
         * @param et
         */
        public void put(Map<K, V> newMap, Map.Entry<K, V> et) {
            newMap.put(et.getKey(), et.getValue());
        }
    }

    /**
     * 对象进行过滤
     *
     * @param <T>
     *
     * @author pingchungg
     */
    public static interface ObjectFilter<T> {
        public boolean accept(T t);
    }

    /**
     * 对象迭代器,用户处理有子列表的对象
     *
     * @param <T>
     *
     * @author pingchungg
     */
    public static interface ObjectIterator<T> {
        /**
         * 对象的子数据
         *
         * @param t
         *
         * @return
         */
        public Collection<T> getChild(T t);

        /**
         * 如果返回false,将停止迭代
         *
         * @param t
         *
         * @return
         */
        public boolean handle(T t);
    }

    /**
     * map重复验证器,验证某个对象是否已经验证过.如果已经验证过则返回false,未验证过着返回true
     *
     * @param <T>
     *
     * @author pingchungg
     */
    public static class RepeatChecker<T> {
        public Map<T, T> map = new HashMap<T, T>();

        /**
         * 如果不存在则返回true并且将对象放入内部map中,否则返回false
         *
         * @param t
         *
         * @return
         */
        public boolean check(T t) {
            if (map.containsKey(t)) return false;
            map.put(t, t);
            return true;
        }
    }

    /**
     * map构建工具
     *
     * @param <K>
     * @param <V>
     *
     * @author
     */
    public static class MapBuilder<K, V> {

        private Map<K, V> map = new HashMap<K, V>();

        public static <K, V> MapBuilder<K, V> builderMap(K k, V v) {
            MapBuilder<K, V> builder = new MapBuilder<K, V>();
            return builder.put(k, v);
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> map() {
            return map;
        }

    }

    /**
     * 转换Map中值得类型
     *
     * @param map
     *
     * @return
     */
    public static <K, A, B> Map<K, B> convert(Map<K, A> map) {
        Map<K, B> m = new HashMap<K, B>();
        for (Map.Entry<K, A> et : map.entrySet()) {
            m.put(et.getKey(), (B) et.getValue());
        }
        return m;
    }

    /**
     * 判断集合是否为空.	非空则返回true,否则false
     * 2018-10-25
     *
     * @param list
     *
     * @return
     */
    public static <T> boolean checkEmpty(Collection<T> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 判断集合是否为空.	非空则返回true,否则false
     * 2018-10-25
     *
     * @param list
     *
     * @return
     */
    public static <T> boolean checkEmpty(T[] list) {
        return list != null && list.length > 0;
    }

    /**
     * 判断原始值是否为empty,如果为空则设置新的值
     *
     * @param map
     * @param key
     * @param newValue
     *
     * @return
     */
    public static <K, V> void checkValueEmptyChange(Map<K, V> map, K key, V newValue) {
        if (!StringUtil.checkEmpty(map.get(key))) {
            map.put(key, newValue);
        }
    }

    /**
     * 判断原始值是否为empty,如果为空则设置新的值
     *
     * @param map
     * @param key
     * @param newValue
     *
     * @return
     */
    public static <K, V> void checkValueNullChange(Map<K, V> map, K key, V newValue) {
        if (!StringUtil.checkNull(map.get(key))) {
            map.put(key, newValue);
        }
    }

    /**
     *
     */
    public static <T> Set<T> asSet(T... objs) {
        Set<T> set = new HashSet<>();
        if (objs != null) {
            for (T t : objs)
                set.add(t);
        }
        return set;
    }

    /**
     *
     */
    public static <T> List<T> asList(T... objs) {
        List<T> list = new ArrayList<>();
        if (objs != null) {
            for (T t : objs)
                list.add(t);
        }
        return list;
    }

    /**
     * 将对象组合成map.注意:未进行验证.需要调用者自行保证数据正确
     *
     * @param map
     * @param data
     *
     * @return
     */
    private static <K, V> Map<K, V> populateMap(Map<K, V> map, Object... data) {
        if (data != null) {
            for (int i = 0; i < data.length; ) {
                map.put((K) data[i++], (V) data[i++]);
            }
        }
        return map;
    }

    /**
     * 将进行类型转换.注意:未进行验证.需要调用者自行保证数据正确
     *
     * @param name1
     * @param value1
     *
     * @return
     */
    public static <K, K1 extends K, V, V1 extends V> Map<K, V> asMap(K1 name1, V1 value1) {
        return populateMap(new HashMap<K, V>(), name1, value1);
    }

    /**
     * .注意:未进行验证.需要调用者自行保证数据正确
     */
    public static <K, K1 extends K, V, V1 extends V, V2 extends V> Map<K, V> asMap(K1 name1, V1 value1, K1 name2, V2 value2) {
        return populateMap(new HashMap<K, V>(), name1, value1, name2, value2);
    }

    /**
     * 注意:未进行验证.需要调用者自行保证数据正确
     *
     * @param data
     *
     * @return
     */
    public static <K, V> Map<K, V> asMap(Object... data) {
        Map<K, V> map = new HashMap<K, V>();
        return populateMap(map, data);
    }

    /**
     * 注意:未进行验证.需要调用者自行保证数据正确
     *
     * @param data
     *
     * @return
     */
    public static <K, V> Map<K, V> asSortMap(Object... data) {
        Map<K, V> map = new LinkedHashMap<K, V>();
        return populateMap(map, data);
    }

    /**
     * 将对象转换成集合.  如果对象是数组则将数组转换成集合,如果是单个对象则转换成List,如果是集合则返回原对象. null则返回空List
     *
     * @param obj
     *
     * @return
     */
    public static Collection asCollection(Object obj) {
        if (obj == null) return new ArrayList();
        if (obj instanceof Collection) return (Collection) obj;
        else if (isArray(obj)) {
            List a = new ArrayList();
            int n = Array.getLength(obj);
            for (int i = 0; i < n; i++) {
                a.add(Array.get(obj, i));
            }
            return a;
        } else {
            List a = new ArrayList();
            a.add(obj);
            return a;
        }
    }

    /**
     * 将对象转换成迭代器.   如果null则返回空迭代器. 集合则返回集合的迭代器. 否则将对象放入集合后返回迭代器
     *
     * @param obj
     *
     * @return
     */
    public static Iterator asIterator(Object obj) {
        if (obj == null) return EMPTY_ITERATOR;
        if (obj instanceof Collection) return ((Collection) obj).iterator();
        else if (isArray(obj)) {
            return new ArrayIterator(obj);
        } else {
            List a = new ArrayList();
            a.add(obj);
            return a.iterator();
        }
    }

    /**
     * 将会返回一个新的数组. 如果os和ns都空(null或0长度)则会返回null
     *
     * @param os
     * @param ns
     *
     * @return
     */
    public static Object arrayAppend(Object os, Object ns) {
        int n1 = (os == null) ? 0 : getLength(os);
        int n2 = (ns == null) ? 0 : getLength(ns);
        int an = n1 + n2;
        Object array = null;
        if (n1 != 0) array = Array.newInstance(os.getClass().getComponentType(), an);
        else if (n2 != 0) array = Array.newInstance(ns.getClass().getComponentType(), an);
        else {
            return null;
        }
        if (n1 != 0) System.arraycopy(os, 0, array, 0, n1);
        if (n2 != 0) System.arraycopy(ns, 0, array, n1, n2);
        return array;
    }

    /**
     * 将会返回一个新的数组. 如果os和ns都空(null或0长度)为空会返回null
     *
     * @param os
     * @param ns
     *
     * @return
     */
    public static <T> T[] arrayAppend(T[] os, T[] ns) {
        int n1 = (os == null) ? 0 : os.length;
        int n2 = (ns == null) ? 0 : ns.length;
        int an = n1 + n2;
        Object array = null;
        if (n1 != 0) array = Array.newInstance(os.getClass().getComponentType(), an);
        else if (n2 != 0) array = Array.newInstance(ns.getClass().getComponentType(), an);
        else {
            return null;
        }
        if (n1 != 0) System.arraycopy(os, 0, array, 0, n1);
        if (n2 != 0) System.arraycopy(ns, 0, array, n1, n2);
        return (T[]) array;
    }

    /**
     * 对数组进行追加数据.如果os和ns都空(null或0长度)为空会返回null
     *
     * @param <T>
     * @param os
     * @param ns
     *
     * @return
     */
    public static <T> T[] arrayAppendItem(T[] os, T... ns) {
        return arrayAppend(os, ns);
    }

    /**
     * 将数组转换成String,之间用指定的字符串分割
     *
     * @param obj  对象数组
     * @param join 分割的字符串
     *
     * @return
     */
    public static String arrayToString(Object obj, String join) {
        if (!isArray(obj)) return "";
        int n = Array.getLength(obj);
        if (n < 1) return "";
        StringBuilder sb = new StringBuilder();
        Object temp = null;
        for (int i = 0; i < n - 1; i++) {
            temp = Array.get(obj, i);
            if (temp != null) sb.append(temp.toString() + join);
        }
        temp = Array.get(obj, n - 1);
        if (temp != null) sb.append(temp.toString());
        return sb.toString();
    }

    /**
     * 强制转换对象类型.  直接通过  (S)V 的方式直接转换.   只能子类转成父类.例如(Map<String,String>,Map<String,Object>)
     */
    public static <K, V, S> Map<K, S> castMap(Map<K, V> srcMap, Map<K, S> descMap) {
        if (srcMap == null || srcMap.isEmpty()) return descMap;
        Set<Map.Entry<K, V>> set = srcMap.entrySet();
        for (Map.Entry<K, V> et : set) {
            descMap.put(et.getKey(), (S) et.getValue());
        }
        return descMap;
    }

    /**
     * 进行连接数组
     *
     * @param obj
     * @param join
     *
     * @return
     */
    public static String joinArray(Object obj, String join) {
        return arrayToString(obj, join);
    }

    /**
     * 构建一个List<Map<K,V>>列表.  即:仅k和v组成一个map然后放入list中
     *
     * @param k
     * @param v
     *
     * @return
     */
    public static <K, V> List<Map<K, V>> buildListMap(K k, V v) {
        List<Map<K, V>> list = new ArrayList<Map<K, V>>();
        Map<K, V> map = new HashMap<K, V>();
        map.put(k, v);
        return list;
    }

    /**
     * 列表增加数据
     *
     * @param list   可为空
     * @param values 可空
     *
     * @return
     */
    public static <T> void listAdd(List<T> list, T... values) {
        if (values != null) {
            for (T v : values) {
                list.add(v);
            }
        }
    }

    /**
     * 复制数组
     *
     * @param <T>
     * @param fs
     *
     * @return
     */

    public static <T> T[] cloneArray(T[] fs) {
        return Arrays.copyOf(fs, fs.length);
    }

    public static <T> void exchangeIndex(List<T> fs, int i1, int i2) {
        if (i1 == i2) return;
        T temp = null;
        temp = fs.get(i1);
        fs.set(i1, fs.get(i2));
        fs.set(i2, temp);
    }

    public static <T> void exchangeIndex(T[] fs, int i1, int i2) {
        if (i1 == i2) return;
        T temp = null;
        temp = fs[i1];
        fs[i1] = fs[i2];
        fs[i2] = temp;
    }

    /**
     * 扩展List<Map>>中的map元素,遍历操作List
     */
    public static <T> void extendMap(List<T> list, CollectionIterator<T> each) {
        for (T t : list) {
            each.iterator(t);
        }
    }

    /**
     * 扩展List<Map>中的map元素,获取key的值产生新值设置新的newKey值
     * 将原来Map中的字段通过计算,产生新字段
     * 例如:  原来List<Map<String,Object>>  中的map中包含一个name字段.
     * 现在需要生产一个pinyin(字段).  则extendMapObject(list,"name","pinyin",new CollectionIterator(){});  在CollectionIterator中返回pinyin字段的内容
     *
     * @param list
     * @param key
     * @param newKey
     * @param extend
     */
    public static <K, V> void extendMapObject(List<Map<K, V>> list, K key, K newKey, CollectionIterator<V> extend) {
        for (Map<K, V> map : list) {
            V v = map.get(key);
            extend.iterator(v);
            map.put(newKey, v);
        }
    }

    /**
     * 过滤对象,首个参数为返回新的集合
     *
     * @param cn     返回的新集合
     * @param c      旧集合
     * @param filter
     *
     * @return
     */
    public static <C extends Collection<T>, T> C filter(C cn, C c, ObjectFilter<T> filter) {
        if (c == null || c.size() == 0) return cn;
        for (T t : c) {
            if (filter.accept(t)) {
                cn.add(t);
            }
        }
        return cn;
    }

    /**
     * 会自动反射创建新集合对象.如果集合是内部类可能会有错误
     */
    public static <C extends Collection<T>, T> C filter(C c, ObjectFilter<T> filter) {
        return filter((C) ReflectUtil.getInstance(c.getClass()), c, filter);
    }

    /**
     * 过滤map,通过反射建立新map
     */
    public static <K, V> Map<K, V> filterMap(Map<K, V> map, MapFilter<K, V> filter) {
        if (filter == null) return map;
        try {
            Map<K, V> nmap = (Map<K, V>) ReflectUtil.getInstance(map.getClass());
            for (Map.Entry<K, V> et : map.entrySet()) {
                filter.filter(nmap, et);
            }
            return nmap;
        } catch (ReflectUtil.ClassReflectException e) {
            throw new RuntimeException("Can't create Map install of class " + map.getClass());
        }

    }

    /**
     * 会自动反射创建新集合对象.如果集合是内部类可能会有错误
     *
     * @param c
     *
     * @return
     */
    public static <C extends Collection<T>, T> C filterRepeat(C c) {
        return filterRepeat((C) ReflectUtil.getInstance(c.getClass()), c);
    }

    /**
     * 过滤重复对象的默认实现,首个参数为返回的无重复对象
     *
     * @param c
     *
     * @return
     */
    public static <C extends Collection<T>, T> C filterRepeat(C cn, C c) {
        return filter(cn, c, new ObjectFilter<T>() {
            private RepeatChecker<T> rc = new RepeatChecker<T>();

            @Override
            public boolean accept(T t) {
                if (rc.check(t)) return true;
                return false;
            }

        });
    }

    /**
     * 从map中排除exclude中具有的键值对.相当于做键的减法
     * 例如: {"a":1,"b":"2"} , {"b":"xx"}  执行结果  {"a":1}
     * 注意:返回的新new的map对象
     *
     * @param map
     * @param exclude
     *
     * @return
     */
    public static <K, V> Map<K, V> exclude(Map<K, V> map, Map<K, V> exclude) {
        Map<K, V> temp = cloneMap(map);
        for (K k : exclude.keySet()) {
            temp.remove(k);
        }
        return temp;
    }

    /**
     * 取出同时在map和include中的键.  相当于取并
     * 例如: {"a":1,"b":"2"} , {"b":"xx"}  执行结果  {"b":2}
     * 注意:返回的新new的map对象
     */
    public static <K, V> Map<K, V> include(Map<K, V> map, Map<K, V> include) {
        Map<K, V> temp = cloneMap(map);
        for (Object k : map.keySet()) {
            if (!include.containsKey(k)) temp.remove(k);
        }
        return temp;
    }

    /**
     * 从list中排除exclude中具有的元素. 相当于  list 减去 exclude
     * 注意:是返回的新new的list
     */
    public static <T> List<T> exclude(List<T> list, List<T> exclude) {
        List<T> temp = cloneList(list);
        for (T t : exclude) {
            temp.remove(t);
        }
        return temp;
    }

    /**
     * 取list和include的交集
     * 注意:是返回的新new的list
     * <p>
     * public static <T> List<T> include(List<T> list, List<T> include) {
     * List<T> temp = cloneList(list);
     * for (T t : temp) {
     * if (!include.contains(t)) list.remove(t);
     * }
     * return list;
     * }
     * <p>
     * /**
     * 通过,连接集合   通过s连接集合
     */
    public static String join(Collection col, String s) {
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (Object obj : col) {
            if (i != 0) sb.append(s);
            sb.append(obj);
            i++;
        }
        return sb.toString();
    }

    /**
     * 连接有效字符串.回去掉空白对象
     *
     * @param col
     * @param s
     *
     * @return
     */
    public static String joinValid(Collection col, String s) {
        boolean append = false;
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        for (Object obj : col) {
            tmp = StringUtil.toStringSimple(obj, null);
            if (StringUtil.checkTrimEmpty(tmp)) {
                if (append) sb.append(s);
                sb.append(tmp);
                append = true;
            }
        }
        return sb.toString();
    }

    public static String join(Collection col) {
        return join(col, ",");
    }

    /**
     * 连接集合或者数组
     */
    public static String join(Object col, String s) {
        if (col == null) return "";
        if (col instanceof Collection) {
            return join((Collection) col, s);
        } else if (CollectionUtil.isArray(s)) {
            return joinArray(col, s);
        }
        throw new RuntimeException("对象非数组或集合:" + col.getClass());
    }

    /**
     * 检测数组是否包涵对象
     */

    public static <T> boolean find(T[] array, T obj) {
        return findIndex(array, obj) > -1;
    }

    public static <T> boolean find(List<T> list, T obj) {
        return findIndex(list, obj) > -1;
    }

    /**
     * int[]数组的包含下标
     */
    public static int findIndex(int[] as, int c) {
        if (as == null || as.length == 0) return -1;
        for (int i = 0; i < as.length; i++) {
            if (as[i] == c) return i;
        }
        return -1;
    }

    /**
     * long[]数组的包含下标
     */
    public static int findIndex(long[] as, long c) {
        if (as == null || as.length == 0) return -1;
        for (int i = 0; i < as.length; i++) {
            if (as[i] == c) return i;
        }
        return -1;
    }

    /**
     * 检测数组包涵对象的下标,不包涵为-1,支持查找null
     */
    public static <T> int findIndex(List<T> list, T obj) {
        if (list == null || list.size() == 0) return -1;
        if (obj == null) {
            for (int i = 0, n = list.size(); i < n; i++) {
                if (list.get(i) == null) return i;
            }
        } else {
            for (int i = 0, n = list.size(); i < n; i++) {
                if (obj.equals(list.get(i))) return i;
            }
        }
        return -1;
    }

    /**
     * 检测数组包涵对象的下标,不包涵为-1,支持查找null
     */
    public static <T> int findIndex(T[] array, T obj) {
        if (array == null || array.length == 0) return -1;
        if (obj == null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i] == null) return i;
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                if (obj.equals(array[i])) return i;
            }
        }
        return -1;
    }

    /**
     * 数组中的字符串	是否有以s开始的  字符串,支持查找null
     */
    public static int findStartIndex(String[] as, String s) {
        if (as == null || as.length == 0) return -1;
        if (s == null) {
            for (int i = 0; i < as.length; i++) {
                if (as[i] == null) return i;
            }
        } else {
            String temp = null;
            for (int i = 0; i < as.length; i++) {
                temp = as[i];
                if (temp != null && temp.startsWith(s)) return i;
            }
        }
        return -1;
    }

    /**
     * 获取list首个元素
     *
     * @param list
     *
     * @return
     */
    public static <T> T getFirst(List<T> list) {
        return getIndex(list, 0);
    }

    /**
     * 获取arrat首个元素
     *
     * @param array
     *
     * @return
     */
    public static <T> T getFirst(T[] array) {
        return getIndex(array, 0);
    }

    public static <T> T getLast(List<T> list) {
        if (list == null) return null;
        return getIndex(list, list.size() - 1);
    }

    public static <T> T getLast(T[] array) {
        if (array == null) return null;
        return getIndex(array, array.length - 1);
    }

    /**
     * 获取list的下标
     *
     * @param list
     * @param index
     *
     * @return
     */
    public static <T> T getIndex(List<T> list, int index) {
        if (list != null && index < list.size() && index > -1) return list.get(index);
        return null;
    }

    /**
     * 获取list的下标文本
     *
     * @param list
     * @param index
     * @param def
     *
     * @return
     */
    public static <T> String getIndexString(List<T> list, int index, String def) {
        return StringUtil.toStringSimple(getIndex(list, index), def);
    }

    /**
     * 获取list的下标文本
     *
     * @param list
     * @param index
     * @param def
     *
     * @return
     */
    public static <T> String getIndexStringTrim(List<T> list, int index, String def) {
        return StringUtil.trimSimple(StringUtil.toStringSimple(getIndex(list, index), def));
    }

    /**
     * 获取指定下标列表的值
     *
     * @param list
     * @param indexs
     * @param check
     *
     * @return
     */
    public static <T> List<T> getIndexs(List<T> list, int[] indexs, boolean check) {
        if (list == null) return null;
        List<T> result = newList();
        if (list.size() == indexs.length) {
            result.addAll(list);
            return result;
        }
        if (check) {
            for (int i : indexs) {
                result.add(getIndex(list, i));
            }
        } else {
            for (int i : indexs) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 获取指定下标列表的值
     *
     * @param list
     * @param indexs
     * @param check
     *
     * @return
     */
    public static <T> List<T> getIndexs(List<T> list, Integer[] indexs, boolean check) {
        if (list == null) return null;
        List<T> result = newList();
        if (list.size() == indexs.length) {
            result.addAll(list);
            return result;
        }
        if (check) {
            for (int i : indexs) {
                result.add(getIndex(list, i));
            }
        } else {
            for (int i : indexs) {
                result.add(list.get(i));
            }
        }
        return result;
    }

    /**
     * 获取数组对象的指定下标对象
     *
     * @param obj
     * @param index
     *
     * @return
     */
    public static Object getIndex(Object obj, int index) {
        if (isArray(obj)) {
            int n = Array.getLength(obj);
            if (n > 0 && index > -1 && index < n) return Array.get(obj, index);
        }
        return null;
    }

    /**
     * 获取子列表.包含下限,不包含上限. 计算方式:大于长度则为长度,小于0则为0. end如果小于等于0则表示从后算.       start为计算后的小的数值,end为大得数值
     *
     * @param list
     * @param start
     * @param end   如果为0则表示最后.负数则表示倒数
     *
     * @return
     */
    public static <T> List<T> subList(List<T> list, int start, int end, boolean copy) {

        if (list == null) return new ArrayList<T>();
        int n = list.size();
        if (start < 0) start = 0;
        else if (start > n) start = n;
        if (end <= 0) {
            end = n + end; //如果负数则使用,从后获取
            if (end < 0) end = 0;
        } else if (end > n) end = n;
        int n1 = Math.min(start, end);
        int n2 = Math.max(start, end);
        if (copy) {
            List<T> sub = new ArrayList<T>();
            for (int i = start; i < end; i++) {
                sub.add(list.get(i));
            }
            return sub;
        }
        return list.subList(n1, n2);
    }

    /**
     * @param array
     * @param index
     *
     * @return
     */
    public static <T> T getIndex(T[] array, int index) {
        if (array != null && index < array.length) return array[index];
        return null;
    }

    /**
     * 获取iterator中下标的对象
     */
    public static <T> T getIteratorObject(Iterator<T> it, int index) {
        int n = 0;
        T obj = null;
        while (it.hasNext()) {
            obj = it.next();
            if (n == index) return obj;
            n++;
        }
        return null;
    }

    /**
     * 获取集合或数组的长度
     *
     * @param obj
     *
     * @return
     */
    public static int getLength(Object obj) {
        if (obj == null) return 0;
        if (obj instanceof Collection) {
            return ((Collection) obj).size();
        } else if (isArray(obj)) {
            return Array.getLength(obj);
        }
        return 0;
    }

    /**
     * 获取map中的项,会去掉空值
     *
     * @param list
     * @param key
     *
     * @return
     */
    public static <K, V> List<V> getMapItems(List<Map<K, V>> list, K key) {
        List<V> temp = new ArrayList<V>();
        V t = null;
        for (Map<K, V> map : list) {
            t = map.get(key);
            if (t != null) temp.add(t);
        }
        return temp;
    }

    /**
     * 获取list的size
     *
     * @param list
     *
     * @return
     */
    public static int getSize(Collection<?> list) {
        return list == null ? 0 : list.size();
    }

    /**
     * 集合长度是否大于等于给定长度
     *
     * @param list
     * @param s
     *
     * @return
     */
    public static boolean sizeThanGe(Collection<?> list, int s) {
        return getSize(list) >= s;
    }

    /**
     * 集合长度是否小于等于给定长度
     *
     * @param list
     * @param s
     *
     * @return
     */
    public static boolean sizeThanLe(Collection<?> list, int s) {
        return getSize(list) <= s;
    }

    /**
     * 用于以后,需要使用其他方式创建map时,方便替换
     *
     * @return
     */
    public static <K, V> Map<K, V> newMap() {
        return new HashMap<K, V>();
    }

    /**
     * 用于以后,需要使用其他方式创建list时,方便替换
     *
     * @return
     */
    public static <T> List<T> newList() {
        return new ArrayList<T>();
    }

    /**
     * 判断对象是否是数组
     *
     * @param obj
     *
     * @return
     */
    public static boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * 对集合进行迭代,用于有子列表的对象
     *
     * @param list
     * @param iterator
     */
    public static <T> void iteratorObject(Collection<T> list, ObjectIterator<T> iterator) {
        if (list == null) return;
        for (T t : list) {
            iteratorObject(t, iterator);
        }
    }

    /**
     * 对对象进行迭代,用于有子列表的对象
     *
     * @param t
     * @param iterator
     */
    public static <T> void iteratorObject(T t, ObjectIterator<T> iterator) {
        if (t == null) return;
        if (iterator.handle(t) == false) return;
        Collection<T> childs = iterator.getChild(t);
        if (childs != null && childs.size() > 0) {
            for (T c : childs) {
                iteratorObject(c, iterator);
            }
        }
    }

    /**
     * 复制列表
     *
     * @param fromList
     *
     * @return
     */
    public static <V> List<V> cloneList(List<V> fromList) {
        return cloneList(fromList, new ArrayList<V>());
    }

    public static <V> List<V> cloneList(List<V> fromList, List<V> toList) {
        fromList.addAll(toList);
        return fromList;
    }

    /**
     * 复制map
     *
     * @param fromMap
     *
     * @return
     */
    public static <K, V> Map<K, V> cloneMap(Map<K, V> fromMap) {
        return cloneMap(fromMap, new HashMap<K, V>());
    }

    /**
     * 复制map
     *
     * @param fromMap
     * @param toMap
     *
     * @return
     */
    public static <K, V> Map<K, V> cloneMap(Map<K, V> fromMap, Map<K, V> toMap) {
        toMap.putAll(fromMap);
        return toMap;
    }

    /**
     * 合并map,原始的oldMap
     * 注意:返回的oldMap
     *
     * @param oldMap
     * @param newMap
     *
     * @return
     */
    public static <K, V> Map<K, V> mergerMap(Map<K, V> oldMap, Map<K, V> newMap) {
        Iterator<Map.Entry<K, V>> it = newMap.entrySet().iterator();
        Map.Entry<K, V> entry = null;
        while (it.hasNext()) {
            entry = it.next();
            oldMap.put(entry.getKey(), entry.getValue());
        }
        return oldMap;
    }

    /**
     * 转换对象数组列表到Map,用于将数据库中查询出的List<Object[]>转换成List<Map<String,Object>>
     */
    public static <K, V> List<Map<K, V>> parseListArrayToMap(List<Object[]> list, K... keys) {
        List<Map<K, V>> result = new ArrayList<Map<K, V>>();
        if (list != null && list.size() > 0) {
            Map<K, V> map = null;
            for (Object[] obj : list) {
                map = new HashMap<K, V>();
                for (int i = 0; i < keys.length; i++) {
                    K key = keys[i];
                    map.put(key, (V) obj[i]);
                }
                result.add(map);
            }
        }
        return result;
    }

    /**
     * 转换Object数组转换字符串数组
     *
     * @param objs
     *
     * @return
     */
    public static String[] parseObjectToString(Object[] objs) {
        if (objs == null) return new String[0];
        int n = objs.length;
        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            if (objs[i] == null) temp[i] = "";
            else temp[i] = objs[i].toString();
        }
        return temp;
    }

    /**
     * 将int[]数组转换成String[]
     */
    public static String[] parseIntToString(int[] nums) {
        if (nums == null) return new String[0];
        int n = nums.length;
        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            temp[i] = String.valueOf(nums[i]);
        }
        return temp;
    }

    /**
     * 将int[]数组转换成String[]
     */
    public static String[] parseIntegerToString(Integer[] nums) {
        if (nums == null) return new String[0];
        int n = nums.length;
        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            temp[i] = String.valueOf(nums[i]);
        }
        return temp;
    }

    /**
     * 将long[]数组转换成String[]
     */
    public static String[] parseLongToString(long[] nums) {
        if (nums == null) return new String[0];
        int n = nums.length;
        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            temp[i] = String.valueOf(nums[i]);
        }
        return temp;
    }

    public static String[] parseLongerToString(Long[] nums) {
        if (nums == null) return new String[0];
        int n = nums.length;
        String[] temp = new String[n];
        for (int i = 0; i < n; i++) {
            temp[i] = String.valueOf(nums[i]);
        }
        return temp;
    }

    /**
     * 将String数组转换成int数组
     *
     * @param as 为空时,返回new int[0];
     *
     * @return
     */
    public static int[] parseStringToInt(String[] as) {
        if (as == null) return new int[0];
        int n = as.length;
        int[] temp = new int[n];
        for (int i = 0; i < n; i++) {
            temp[i] = Integer.parseInt(as[i]);
        }
        return temp;
    }

    /**
     * 将String数组转换成int数组
     *
     * @param as 为空时,返回new int[0];
     */
    public static Integer[] parseStringToInteger(String[] as) {
        if (as == null) return new Integer[0];
        int n = as.length;
        Integer[] temp = new Integer[n];
        for (int i = 0; i < n; i++) {
            temp[i] = new Integer(as[i]);
        }
        return temp;
    }

    /**
     * 将String数组转换成long数组
     *
     * @param as 为空时,返回new long[0];
     */
    public static long[] parseStringToLong(String[] as) {
        if (as == null) return new long[0];
        int n = as.length;
        long[] temp = new long[n];
        for (int i = 0; i < n; i++) {
            temp[i] = Long.parseLong(as[i]);
        }
        return temp;
    }

    /**
     * 将String数组转换成long数组
     *
     * @param as 为空时,返回new long[0];
     *
     * @return
     */
    public static Long[] parseStringToLonger(String[] as) {
        if (as == null) return new Long[0];
        int n = as.length;
        Long[] temp = new Long[n];
        for (int i = 0; i < n; i++) {
            temp[i] = new Long(as[i]);
        }
        return temp;
    }

    /**
     * 如果不存在且value不能为空,则向map中插入值,返回是否进行了插入值
     *
     * @param map
     * @param key
     * @param value
     *
     * @return
     */
    public static <K, V> boolean putNotExist(Map<K, V> map, K key, V value) {
        if (value != null && !map.containsKey(key)) {
            map.put(key, value);
            return true;
        }
        return false;
    }

    public static <T> void randomSort(List<T> list) {
        if (list == null || list.size() < 2) return;
        for (int i = 0; i < list.size(); i++) {
            exchangeIndex(list, i, NumberUtil.getRandom(0, list.size()));
        }
    }

    public static <T> void randomSort(List<T> list, int time) {
        for (int i = 0; i < time; i++)
            randomSort(list);
    }

    public static <T> void randomSort(T[] fs) {
        if (fs == null || fs.length < 2) return;
        for (int i = 0; i < fs.length; i++) {
            exchangeIndex(fs, i, NumberUtil.getRandom(0, fs.length));
        }
    }

    public static <T> void randomSort(T[] fs, int time) {
        for (int i = 0; i < time; i++)
            randomSort(fs);
    }

    /**
     * 将对象转换成数组,如果对象为null则返回空数组
     *
     * @param obj
     *
     * @return
     */
    public static Object[] toArray(Object obj) {
        if (obj == null) return new Object[0];
        if (obj instanceof Object[]) return (Object[]) obj;
        if (obj.getClass().isArray()) {
            int n = Array.getLength(obj);
            Object[] objs = new Object[n];
            for (int i = 0; i < n; i++) {
                objs[i] = Array.get(obj, i);
            }
            return objs;
        }
        throw new RuntimeException("Can cast object to Object[]:" + obj);
    }

    /**
     * 映射指定k键到值,如果为null则返回默认值.
     * 表示获取map指定的key值,如果值为null则返回默认值.
     *
     * @param map
     * @param k
     * @param def
     *
     * @return
     */
    public static <K, V> V getMapValue(Map<K, V> map, K k, V def) {
        //		if(map.containsKey(k))return
        if (map == null) return def;
        V v = map.get(k);
        if (v != null) return v;
        return def;
    }

    /**
     * 复制翻转数组
     *
     * @param array
     *
     * @return
     */

    public static <T> T[] reverseArray(T[] array) {
        T[] temp = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length);
        for (int i = 0; i < array.length; i++) {
            temp[i] = array[array.length - i - 1];
        }
        return temp;
    }

    /**
     * 遍历集合,将集合转换成map. 如果后面参数为true,则表示空值不能放入
     *
     * @param list
     * @param mapper
     *
     * @return
     */
    public static <K, V, E> Map<K, V> toMap(Collection<E> list, CollectionMapper<K, V, E> mapper, boolean nonull) {
        Map<K, V> map = new HashMap<K, V>();
        K k = null;
        V v = null;
        for (E e : list) {
            k = mapper.getKey(e);
            if (k != null) {
                v = mapper.getValue(e);
                if (v != null || !nonull) map.put(k, v);
            }

        }
        return map;
    }

    public static <T> List<T> filterTree(List<T> list) {

        return null;
    }

    /**
     * 对集合进行分组
     *
     * @param list
     *
     * @return
     */
    public static <K, V> Map<K, List<V>> groupCollection(Iterable<V> list, KeyMaker<K, V> m) {
        Map<K, List<V>> map = new HashMap<K, List<V>>();
        for (V v : list) {
            K k = m.getKey(v);
            List<V> ll = map.get(k);
            if (ll == null) {
                ll = new ArrayList<V>();
                map.put(k, ll);
            }
            ll.add(v);
        }
        return map;
    }

    /**
     * 清除map中的空值. 通过StringUtil.checkEmpty(obj) 检测空值.注意:是直接操作原来的map
     *
     * @param map
     *
     * @return
     */
    public static <K, V> Map<K, V> clearEmpty(Map<K, V> map) {
        Iterator<K> it = map.keySet().iterator();
        K k = null;
        while (it.hasNext()) {
            k = it.next();
            if (!StringUtil.checkEmpty(map.get(k))) {
                it.remove();
                map.remove(k);
            }
        }
        return map;
    }

    /**
     * 转换对象
     *
     * @param it
     *
     * @return
     */
    public static <T> Enumeration<T> toEnumeration(final Iterator<T> it) {
        if (it == null) return null;
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return it.hasNext();
            }

            @Override
            public T nextElement() {
                return it.next();
            }

        };
    }

    public static <T> List<T> clearRepeate(List<T> list) {
        Set<T> set = new LinkedHashSet<T>();
        set.addAll(list);
        List<T> tmp = new ArrayList<T>();
        tmp.addAll(set);
        return tmp;
    }

    /**
     * 对list进行减法.返回newList中独有的对象  newList-oldList,
     *
     * @param newList
     * @param oldList
     *
     * @return
     */
    public static <T> List<T> subtractList(List<T> newList, List<T> oldList) {
        List<T> result = new ArrayList<T>();
        if (newList != null && newList.size() > 0) {
            if (oldList == null || oldList.size() == 0) { //原来为空则全部添加
                result.addAll(newList);
            } else {
                for (T t : newList) {
                    if (!oldList.contains(t)) {
                        result.add(t); //原来不存在则增加
                    }
                }
            }
        }
        return result;
    }

    /**
     * 对list进行减法.返回newList中独有的对象  newList-oldList,
     *
     * @param newList
     * @param oldList
     *
     * @return
     */
    public static <T, R> List<T> subtractList(List<T> newList, List<T> oldList, Function<T, R> idFun) {
        List<T> result = new ArrayList<T>();

        if (newList != null && newList.size() > 0) {//新list存在则进行计算

            if (oldList == null || oldList.size() == 0) { //原来为空则全部添加
                result.addAll(newList);
            } else {
                for (T t : newList) {
                    boolean find = false;
                    for (T old : oldList) {
                        R rn = idFun.apply(t);
                        R ro = idFun.apply(old);
                        if (StringUtil.checkEqual(rn, ro)) {
                            find = true;
                            break;
                        }
                    }
                    if (find == false) {
                        result.add(t);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 对两个list进行取并
     *
     * @param newList
     * @param oldList
     *
     * @return
     */
    public static <T> List<T> unionList(List<T> newList, List<T> oldList) {
        List<T> result = new ArrayList<T>();
        if (newList != null && newList.size() > 0) {
            if (oldList != null && oldList.size() > 0) { //
                for (T t : newList) {
                    if (oldList.contains(t)) {
                        result.add(t); //原来存在则增加
                    }
                }
            }
        }
        return result;
    }

    /**
     * 对两个list进行取并
     *
     * @param newList
     * @param oldList
     *
     * @return
     */
    public static <T, R> List<T> unionList(List<T> newList, List<T> oldList, Function<T, R> idFun) {
        List<T> result = new ArrayList<T>();

        if (newList != null && newList.size() > 0) {//新list存在则进行计算

            if (oldList != null && oldList.size() > 0) { //
                for (T t : newList) {
                    boolean find = false;
                    for (T old : oldList) {
                        R rn = idFun.apply(t);
                        R ro = idFun.apply(old);
                        if (StringUtil.checkEqual(rn, ro)) {
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        result.add(t);
                    }
                }
            }
        }
        return result;
    }
}
