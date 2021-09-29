package allst.utils.tools;

/**
 * 跟操作系统相关的工具类
 *
 * @author June
 * @since 2021年09月
 */
public class OSUtil {
    public enum OSType {
        windows, linux, android, unknow
    }

    public enum OSBit {
        x86, x64, unknow
    }

    /**
     * 获取系统类型
     */
    public static OSType getOSType() {
        String s = System.getProperty("os.name");
        if (s == null)
            return OSType.unknow;
        if (s.toLowerCase().indexOf("windows") > -1)
            return OSType.windows;
        else if (s.toLowerCase().indexOf("linux") > -1)
            return OSType.linux;
        return OSType.unknow;
    }

    /**
     * 获取系统位数
     *
     * @return
     */
    public static OSBit getOSBit() {
        String s = System.getProperty("os.arch");
        if (s == null)
            return OSBit.unknow;
        if (s.indexOf("x86") > -1)
            return OSBit.x86;
        else if (s.indexOf("x64") > -1)
            return OSBit.x64;
        return OSBit.unknow;
    }

    /**
     * 获取java位数  64或者32  未知为-1
     *
     * @return
     */
    public static int getJavaBit() {
        String s = System.getProperty("sun.arch.data.model");
        return StringUtil.toInt(s);
    }

    public static boolean isLinux() {
        return getOSType().equals(OSType.linux);
    }

    public static boolean isWindows() {
        return getOSType().equals(OSType.windows);
    }

    /**
     * 将指定的名称映射成library名称
     * windows:
     * a => a.dll
     * linux:
     * a => alib.so
     *
     * @param name
     *
     * @return
     */
    public static String mapLibraryName(String name) {
        return System.mapLibraryName(name);
    }

    /**
     * 将指定的名称映射成library名称
     * windows:
     * a => a.exe
     * linux和其他:
     * a => a
     *
     * @param name
     *
     * @return
     */
    public static String mapExecuteName(String name) {
        if (isWindows()) {
            return name + ".exe";
        } else {
            return name;
        }
    }
}
