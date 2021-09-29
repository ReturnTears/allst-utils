package allst.utils.tools;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 反射工具
 * 有Declared表示只是当前类,能获取到当前类中不可见的方法或属性
 * 无Declared可获取到自身和父中可见的属性或方法
 *
 * @author June
 * @since 2021年09月
 */
public class ReflectUtil {
    /**
     * 空对象数组,用于进行反射时调用空参方法
     */
    public static final Object[] EMPTY_OBJECTS = new Object[0];
    /**
     * 空类数组,用于进行反射时调用空参方法
     */
    public static final Class<?>[] EMPTY_CLASSES = new Class<?>[0];

    /**
     * 记录类的父类列表
     */
    protected static Map<Class<?>, List<Class<?>>> superClassMap = new HashMap<Class<?>, List<Class<?>>>();
    /**
     * 记录类的接口列表
     */
    protected static Map<Class<?>, List<Class<?>>> superInterfaceMap = new HashMap<Class<?>, List<Class<?>>>();
    /**
     * 记录类的接口+父类列表
     */
    protected static Map<Class<?>, List<Class<?>>> superAllMap = new HashMap<Class<?>, List<Class<?>>>();

    /**
     * 基础数据类型
     */
    protected static Set<Class<?>> simpleClass = new HashSet<Class<?>>();

    static {
        simpleClass.add(Integer.class);
        simpleClass.add(Integer.TYPE);
        simpleClass.add(Long.class);
        simpleClass.add(Long.TYPE);
        simpleClass.add(Float.class);
        simpleClass.add(Float.TYPE);
        simpleClass.add(Double.class);
        simpleClass.add(Double.TYPE);
        simpleClass.add(Boolean.class);
        simpleClass.add(Boolean.TYPE);
        simpleClass.add(Short.class);
        simpleClass.add(Short.TYPE);
        simpleClass.add(Byte.class);
        simpleClass.add(Byte.TYPE);
        simpleClass.add(String.class);

        simpleClass.add(Date.class);
        simpleClass.add(java.sql.Date.class);
        simpleClass.add(Timestamp.class);

        simpleClass.add(BigInteger.class);
        simpleClass.add(BigDecimal.class);
    }

    /**
     * 是否是基础数据类型+String
     * @param type
     * @return
     */
    public static boolean isSimpleType(Class<?> type) {
        return simpleClass.contains(type);
    }

    /**
     * 简单复制(仅复制基础数据类型+String),不会进行类型装换,如果类型错误则不能够进行设置
     */
    public static <T> T cloneSimple(Class<T> clazz, Map<String, Object> attrs) {
        if (attrs == null) return null;
        T clone = (T) getInstance(clazz);
        PropertyDescriptor[] ds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor d : ds) {
            Class<?> type = d.getPropertyType();
            if (simpleClass.contains(type)) {
                String name = d.getName();
                Method setMethod = d.getWriteMethod();
                if (setMethod != null) {
                    try {
                        //复制属性
                        setMethod.invoke(clone, attrs.get(name));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return clone;
    }

    /**
     * 复制对象.仅复制基础属性
     * @param t
     * @return
     */
    public static <T> T cloneSimple(T t) {
        if (t == null) return null;
        Class<?> clazz = t.getClass();
        T clone = (T) getInstance(clazz);

        PropertyDescriptor[] ds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor d : ds) {
            Class<?> type = d.getPropertyType();
            if (simpleClass.contains(type)) {
                Method getMethod = d.getReadMethod();
                Method setMethod = d.getWriteMethod();
                if (getMethod != null && setMethod != null) {
                    try {
                        //复制属性
                        setMethod.invoke(clone, getMethod.invoke(t));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return clone;
    }

    /**
     * 获取对象对象的简单属性(基础类型+string)的map
     * @param value
     * @return
     */
    public static Map<String, Object> getSimpleFieldMap(Object value) {
        if (value == null) return null;
        return getSimpleFieldMap(value, value.getClass());
    }

    /**
     * 获取对象对象的简单属性(基础类型+string)的map
     * @param value
     * @return
     */
    public static Map<String, Object> getSimpleFieldMap(Object value, Class<?> clazz) {
        if (value == null) return null;
        Map<String, Object> values = new HashMap<>();
        PropertyDescriptor[] ds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor d : ds) {
            Class<?> type = d.getPropertyType();
            if (simpleClass.contains(type)) {
                Method getMethod = d.getReadMethod();
                if (getMethod != null) {
                    try {
                        values.put(d.getName(), getMethod.invoke(value));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        return values;
    }

    /**
     * 获取仅当前类中定义的方法.
     * @param cls
     * @return
     */

    public static Method[] getOwnMethods(Class<?> cls) {
        List<Method> list = new ArrayList<Method>();
        Method[] methods = cls.getMethods();
        for (Method m : methods) {
            if (m.getDeclaringClass().equals(cls)) {
                list.add(m);
            }
        }
        return (Method[]) list.toArray();
    }

    /**
     * 获取对象的类名称
     */
    public static String getClassName(Object obj) throws ClassReflectException {
        return obj.getClass().getName();
    }

    /**
     * 获取指定名称的Class对象.   通过线程的类加载器或者当前类的加载器
     */
    public static Class<?> getClass(String s) throws ClassReflectException {
        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        Class<?> clazz = null;
        try {
            clazz = threadLoader.loadClass(s);
            //			return Class.forName(s);
        } catch (ClassNotFoundException e) {
            //			throw new ClassReflectException(e);
        }
        if (clazz == null) {
            ClassLoader currentLoader = ReflectUtil.class.getClassLoader();
            if (currentLoader != threadLoader) {
                try {
                    clazz = currentLoader.loadClass(s);
                } catch (ClassNotFoundException e) {
                    //					e.printStackTrace();
                }
            }
        }
        if (clazz == null) throw new ClassReflectException("加载类失败:" + s);
        return clazz;
    }

    /**
     * 获取类的实例
     */
    public static Object getInstance(Class<?> cls, Object... params) throws ClassReflectException {
        Constructor<?> ct = getConstructor(cls, params);
        if (ct != null) {
            try {
                return ct.newInstance(params);
            } catch (Exception e) {
                throw new ClassReflectException(e);
            }
        } else
            throw new ClassReflectException("Can't find Constructor in(" + cls.getName() + ") by " + CollectionUtil.getLength(params) + " Paramters");
    }

    /**
     * 获取类的实例
     */
    public static Object getInstance(String name, Object... params) throws ClassReflectException {
        return getInstance(getClass(name), params);
    }

    /**
     * 获取构造函数
     */
    public static Constructor<?> getConstructor(Class<?> cls, Object... params) throws ClassReflectException {
        try {
            return cls.getConstructor(getClassArray(params));
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    /**
     * 获取对象数组的Class数组
     */
    public static Class<?>[] getClassArray(Object[] objs) {
        if (objs != null) {
            Class<?>[] cls = new Class<?>[objs.length];
            for (int i = 0; i < objs.length; i++) {
                cls[i] = objs[i].getClass();
            }
            return cls;
        }
        return null;
    }

    /**
     * 获取首个名称为name的方法
     * @param cls
     * @param name
     * @return
     * @throws ClassReflectException
     */
    public static Method getMethodFirst(Class<?> cls, String name) throws ClassReflectException {
        Method[] ms = cls.getMethods();
        for (Method m : ms) {
            if (name.equals(m.getName())) {
                return m;
            }
        }
        return null;
    }

    /**
     * 获取Class的Method对象
     */
    public static Method getMethod(Class<?> cls, String name, Object... params) throws ClassReflectException {
        return getMethod(cls, name, getClassArray(params));
    }

    /**
     * 获取方法
     * @param cls
     * @param name
     * @param pclass
     * @return
     * @throws ClassReflectException
     */
    public static Method getMethod(Class<?> cls, String name, Class[] pclass) throws ClassReflectException {
        try {
            return cls.getDeclaredMethod(name, pclass);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    /**
     * 获取Class的Field对象
     */
    public static Field getField(Class<?> cls, String name) throws ClassReflectException {
        try {
            return cls.getDeclaredField(name);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    /**
     * 获取field数组
     * @param obj class对象或者对象实例
     * @return
     */
    public static Field[] getFields(Object obj) {
        Class<?> cls = null;
        if (obj instanceof Class) cls = (Class<?>) obj;
        else cls = obj.getClass();
        return cls.getDeclaredFields();
    }

    /**
     * 获取method数组
     * @param obj class对象或者对象实例
     * @return
     */
    public static Method[] getMethods(Object obj) {
        Class<?> cls = null;
        if (obj instanceof Class) cls = (Class<?>) obj;
        else cls = obj.getClass();
        return cls.getDeclaredMethods();
    }

    /**
     * 获取get方法名
     * @param field
     * @return
     */
    public static String getGetMethodName(String field) {
        return "get" + StringUtil.toFirstUpper(field);
    }

    /**
     * 获取set方法
     * @param field
     * @return
     */
    public static String getSetMethodName(String field) {
        return "set" + StringUtil.toFirstUpper(field);
    }

    /**
     * 获取annotation
     * @param cls
     * @return
     */

    public static <T extends Annotation> T getAnnotation(Class<?> cls, Class<T> type) {
        return cls.getAnnotation(type);
    }

    /**
     * 获取class的所有annotaions
     * @param cls
     * @return
     */
    public static Annotation[] getAnnotations(Class<?> cls) {
        return cls.getAnnotations();
    }

    /**
     * 获取字段annotations
     * @param cls
     * @param filedName
     * @return
     */
    public static Annotation[] getFieldAnnotations(Class<?> cls, String filedName) {
        Field f = getField(cls, filedName);
        return f.getAnnotations();
    }

    /**
     * 获取field的annotation
     * @param cls
     * @param filedName
     * @param type
     * @return
     */
    public static <T extends Annotation> T getFieldAnnotation(Class<?> cls, String filedName, Class<T> type) {
        Field f = getField(cls, filedName);
        return f.getAnnotation(type);
    }

    /**
     * 获取method的annotation,无参数函数
     * @param cls
     * @param name
     * @param type
     * @return
     * @throws ClassReflectException
     */
    public static <T extends Annotation> T getMethodAnnotation(Class<?> cls, String name, Class<T> type) throws ClassReflectException {
        try {
            Method method = getMethod(cls, name);
            return method.getAnnotation(type);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    public static void setFieldValueAs(Class<?> cls, Object obj, String fieldName, Object value) throws ClassReflectException {
        Field f = getField(cls, fieldName);
        f.setAccessible(true);
        try {
            f.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new ClassReflectException(e);
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) throws ClassReflectException {
        if (obj instanceof Class) setFieldValueAs((Class) obj, obj, fieldName, value);
        else setFieldValueAs(obj.getClass(), obj, fieldName, value);
    }

    /**
     * 将对象作为指定类的实例获取属性值
     * @param cls
     * @param obj
     * @param fieldName
     * @throws ClassReflectException
     */
    public static Object getFieldValueAs(Class<?> cls, Object obj, String fieldName) throws ClassReflectException {
        Field f = getField(cls, fieldName);
        try {
            f.setAccessible(true);
            return f.get(obj);
        } catch (IllegalAccessException e) {
            throw new ClassReflectException(e);
        }
    }

    /**
     * 获取值.支持静态方法
     * @param obj
     * @param fieldName
     * @return
     * @throws ClassReflectException
     */
    public static Object getFieldValue(Object obj, String fieldName) throws ClassReflectException {
        if (obj instanceof Class) return getFieldValueAs((Class) obj, obj, fieldName);
        return getFieldValueAs(obj.getClass(), obj, fieldName);
    }

    /**
     * 将对象作为cls的实例调用指定方法
     * @param cls
     * @param obj
     * @param methodName
     * @param params
     * @return
     * @throws ClassReflectException
     */
    public static Object callMethodAs(Class<?> cls, Object obj, String methodName, Class[] pclass, Object[] params) throws ClassReflectException {
        Method m = getMethod(cls, methodName, pclass);
        try {
            m.setAccessible(true);
            return m.invoke(obj, params);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    public static Object callMethodAs(Class<?> cls, Object obj, String methodName, Class pclass, Object params) throws ClassReflectException {
        return callMethodAs(cls, obj, methodName, new Class[]{pclass}, new Object[]{params});
    }

    public static Object callMethodAs(Class<?> cls, Object obj, String methodName) throws ClassReflectException {
        Method m = getMethod(cls, methodName);
        try {
            m.setAccessible(true);
            return m.invoke(obj);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    public static Object callMethod(Object obj, String methodName, Class pclass, Object params) throws ClassReflectException {
        return callMethod(obj, methodName, new Class[]{pclass}, new Object[]{params});
    }

    public static Object callMethod(Object obj, String methodName, Class[] pclass, Object[] params) throws ClassReflectException {
        if (obj instanceof Class) return callMethodAs((Class) obj, obj, methodName, pclass, params);
        return callMethodAs(obj.getClass(), obj, methodName, pclass, params);
    }

    public static Object callMethod(Object obj, String methodName) throws ClassReflectException {
        if (obj instanceof Class) return callMethodAs((Class) obj, obj, methodName);
        return callMethodAs(obj.getClass(), obj, methodName);
    }

    public static Object callStaticMethod(Class<?> cls, String name, Class pclass, Object params) {
        return callStaticMethod(cls, name, new Class[]{pclass}, new Object[]{params});
    }

    public static Object callStaticMethod(Class<?> cls, String name, Class[] pclass, Object[] params) {
        Method method = getMethod(cls, name, pclass);
        try {
            method.setAccessible(true);
            return method.invoke(cls, params);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    public static Object callStaticMethod(Class<?> cls, String name) throws ClassReflectException {
        Method method = getMethod(cls, name);
        try {
            method.setAccessible(true);
            return method.invoke(cls);
        } catch (Exception e) {
            throw new ClassReflectException(e);
        }
    }

    /**
     * 复制对象,使用序列化方式
     * @param obj
     * @return
     * @throws IOException
     */
    public static Object cloneBySerializ(Object obj) throws IOException {
        if (obj == null) return null;
        if (obj instanceof Serializable) {
            ByteArrayOutputStream aos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(aos);
            oos.writeObject(obj);

            ByteArrayInputStream bis = new ByteArrayInputStream(new byte[aos.size()]);
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                return ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Can't clone!ClassNotFound!");
            }
        }
        throw new IOException("Object not implement Serializable");
    }

    /**
     * 从包package中获取所有的Class
     * @param pack      包路径
     * @param recursive 是否迭代
     * @param doStatic  是否执行类的静态块
     * @return
     */
    public static Set<Class<?>> getPackageClasses(String pack, boolean recursive, boolean doStatic) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            dirs = loader.getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, doStatic, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            if (doStatic) classes.add(Class.forName(packageName + '.' + className));
                                            else classes.add(loader.loadClass(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, boolean doStatic, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, doStatic, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    // 经过回复同学的提醒，这里用forName有一些不好，会触发static方法，没有使用classLoader的load干净
                    if (doStatic) classes.add(Class.forName(packageName + '.' + className));
                    else classes.add(loader.loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断a是否是b的父类
     * @param a
     * @param b
     */
    public static boolean isSupper(Class<?> a, Class<?> b) {
        Class<?> cls = b;
        do {
            cls = cls.getSuperclass();
            if (a.equals(cls)) return true;
            //&&!Object.class.equals(cls)
        } while (cls != null);

        return false;
    }

    /**
     * 判断a是否是b的子类
     * @param a
     * @param b
     */
    public static boolean isChild(Class<?> a, Class<?> b) {
        Class<?> cls = a;
        do {
            cls = cls.getSuperclass();
            if (b.equals(cls)) return true;
            //&&!Object.class.equals(cls)
        } while (cls != null);

        return false;
    }

    /**
     * 遍历获取接口
     * @param list
     * @param clazz
     */
    protected static void getRecursionInterfaces(List<Class<?>> list, Class<?> clazz) {
        list.add(clazz);
        Class<?>[] its = clazz.getInterfaces();
        if (its != null && its.length > 0) {
            for (Class<?> t : its) {
                getRecursionInterfaces(list, t);
            }
        }
    }

    /**
     * 获取所有父接口列表.注意不包含自身
     * @param clazz
     * @return
     */
    protected static void getSupperInterface(List<Class<?>> list, Class<?> clazz) {
        //获取当前的接口
        Class<?>[] its = clazz.getInterfaces();
        if (its != null && its.length > 0) {
            for (Class<?> t : its) {
                getRecursionInterfaces(list, t);
            }
        }

        //获取父接口
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {

            its = superclass.getInterfaces();
            if (its != null && its.length > 0) {
                for (Class<?> t : its) {
                    getRecursionInterfaces(list, t);
                }
            }
            superclass = superclass.getSuperclass();
        }
    }

    /**
     * @param clazz
     * @return
     */
    public static List<Class<?>> getSupperInterface(Class<?> clazz) {
        List<Class<?>> list = superInterfaceMap.get(clazz);
        if (list == null) {
            list = new ArrayList<Class<?>>();
            getSupperInterface(list, clazz);
            superInterfaceMap.put(clazz, list);
        }
        return list;
    }

    /**
     * 获取所有父类列表.注意不包含自身
     * @param clazz
     * @return
     */
    public static List<Class<?>> getSupperClass(Class<?> clazz) {

        List<Class<?>> list = superClassMap.get(clazz);
        if (list == null) {
            list = new ArrayList<Class<?>>();
            Class<?> superclass = clazz.getSuperclass();
            while (superclass != null) {
                list.add(superclass);
                superclass = superclass.getSuperclass();
            }
            superClassMap.put(clazz, list);
        }
        return list;
    }

    /**
     * 获取所有的父类的父接口.注意不包含自身
     * @param clazz
     * @return
     */
    public static List<Class<?>> getSupperAll(Class<?> clazz) {
        List<Class<?>> list = superAllMap.get(clazz);
        if (list == null) {
            list = new ArrayList<Class<?>>();
            list.addAll(getSupperClass(clazz));
            list.addAll(getSupperInterface(clazz));
            superAllMap.put(clazz, list);
        }
        return list;
    }

    /**
     * 获取类的泛型具体类型.
     * 例如    Class A exends B<String>  此时在A中获取到的应该是String
     * 注意: 不能B中获取.
     * @param clazz
     * @param index
     * @return
     */
    public static Class getGernericClassBySuperclass(Class clazz, int index) {
        Type type = clazz.getGenericSuperclass();//
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (actualTypeArguments != null && actualTypeArguments.length > index) {
                return (Class) actualTypeArguments[index];
            }
        }
        return null;
    }

    /**
     * 获取类泛型的具体类型.通过接口的方式
     * 例如    Class A implement B<String>  此时在A中获取到的应该是String
     * 	 * 注意: 不能B中获取.
     * @param clazz
     * @param interfaceClass
     * @param index
     * @return
     */
    public static Class getGernericClassByInterface(Class clazz, Class interfaceClass, int index) {
        Type[] types = clazz.getGenericInterfaces();//
        if (types != null && types.length > 0) {
            for (Type type : types) {
                if (type instanceof ParameterizedType) {
                    Class rawType = (Class) ((ParameterizedType) type).getRawType();
                    if (interfaceClass.isAssignableFrom(rawType)) {
                        Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                        if (actualTypeArguments != null && actualTypeArguments.length > index) {
                            return (Class) actualTypeArguments[index];
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void outFieldInfo(Object obj) {
        Field[] fs = getFields(obj);
        out("Fields:\n");
        for (Field f : fs) {
            out("    ");
            out("%-12s", Modifier.toString(f.getModifiers()));
            out("%-30s", f.getType().getSimpleName());
            out("%-20s\n", f.getName());
        }
    }

    public static void outFieldValue(Object obj) {
        Field[] fs = getFields(obj);
        out("Fields:\n");
        try {
            for (Field f : fs) {
                out("    ");
                out("%-30s", f.getType().getSimpleName());
                out("%-20s\n", f.getName());
                out("%-20s\n", f.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void outMethodInfo(Object obj) {
        Method[] ms = getMethods(obj);
        out("Methods:\n");
        for (Method m : ms) {
            out("    ");
            out("%-12s", Modifier.toString(m.getModifiers()));
            out("%-30s", m.getReturnType().getSimpleName());
            out("%-20s\n", m.getName());
        }
    }

    public static void out(String format, Object... param) {
        //System.out.print(MessageFormat.format(format, param));
        System.out.printf(format, param);
    }

    public static class MethodCaller {
        private FastClass fastClazz;
        //		private Method method;
        private FastMethod fastMethod;

        private Class<?> clazz;
        private String methodName;

        /**
         * 结构   类名:方法  的方式
         * @param s
         */
        public MethodCaller(String s) {
            int n = s.indexOf(":");
            Class<?> clazz = ReflectUtil.getClass(s.substring(0, n));
            if (clazz == null) throw new ClassReflectException("加载类失败:" + s.substring(0, n));
            init(clazz, s.substring(n + 1, s.length()));
        }

        public MethodCaller(Class<?> clazz, String methodName) {
            init(clazz, methodName);
        }

        private void init(Class<?> clazz, String methodName) {

            this.clazz = clazz;
            this.methodName = methodName;

            this.fastClazz = FastClass.create(clazz);
            Method method = getMethodFirst(clazz, methodName);
            if (method == null)
                throw new ClassReflectException(String.format("Class(%s) not has method(%s)", clazz.getName(), methodName));
            this.fastMethod = fastClazz.getMethod(method);
        }

        public Object call(Object obj, Object[] args) {
            try {
                return fastMethod.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw new ClassReflectException(e);
            }
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public void setClazz(Class<?> clazz) {
            this.clazz = clazz;
        }

        public String getMethodName() {
            return methodName;
        }

        public void setMethodName(String methodName) {
            this.methodName = methodName;
        }

    }

    public static class ClassReflectException extends RuntimeException {
        private static final long serialVersionUID = 4935819738071847519L;

        public ClassReflectException() {

        }

        public ClassReflectException(String s) {
            super(s);
        }

        public ClassReflectException(Exception e) {
            super(e);
        }
    }
}
