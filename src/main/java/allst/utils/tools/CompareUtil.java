package allst.utils.tools;

/**
 * 比较工具
 *
 * @author June
 * @since 2021年09月
 */
public class CompareUtil {
    /**
     * 返回大的数
     */
    public static int getBigger(int a, int b) {
        return Math.max(a, b);
    }

    /**
     * 返回大的数,如果参数为0个则返回-1
     */
    public static int getBiggers(int... nums) {
        if (nums != null && nums.length > 0) {
            int max = nums[0];
            for (int i = 1; i < nums.length; i++) {
                if (nums[i] > max) max = nums[i];
            }
            return max;
        }
        return -1;
    }

    /**
     * 返回小的数
     */
    public static int getSmaller(int a, int b) {
        return Math.min(a, b);
    }

    /**
     * 返回小的数,如果参数为0个则返回-1
     */
    public static int getSmallers(int... nums) {
        if (nums != null && nums.length > 0) {
            int min = nums[0];
            for (int i = 1; i < nums.length; i++) {
                if (nums[i] < min) min = nums[i];
            }
            return min;
        }
        return -1;
    }

    /**
     * 返回长的字符串,为null则返回 "" 空串
     */
    public static String getLonger(String a, String b) {
        if (a != null && b != null) return a.length() > b.length() ? a : b;
        else if (a == null && b == null) return "";
        else return b == null ? a : b;
    }

    /**
     * 返回长的字符串,为null则返回""空串
     */
    public static String getLongers(String... ss) {
        String re = null;
        int len = 0;
        int lenmax = 0;
        for (String s : ss) {
            len = (s == null ? 0 : s.length());
            if (len > lenmax) {
                lenmax = len;
                re = s;
            }
        }
        return re == null ? "" : re;
    }

    /**
     * 返回短字符串,为null则返回 "" 空串
     */
    public static String getShorter(String a, String b) {
        if (a != null && b != null) return a.length() < b.length() ? a : b;
        else return "";
    }

    /**
     * 返回短字符串,为null则返回 "" 空串
     */
    public static String getShorters(String... ss) {
        String re = null;
        int len = 0;
        int lenmax = 0;
        for (String s : ss) {
            len = (s == null ? 0 : s.length());
            if (len < lenmax) {
                lenmax = len;
                re = s;
            }
        }
        return re == null ? "" : re;
    }


    /**
     * 比较两个对象是否相等,同时为null时,相等
     */
    public static boolean equal(Object obj1, Object obj2) {
        if (obj1 == null && obj2 == null) return true;
        else if (obj1 != null && obj2 != null) return obj1.equals(obj2);
        return false;
    }

    /**
     * 比较两个对象是否相等,同时为空时,不相等
     */
    public static boolean equalNotNull(Object obj1, Object obj2) {
        if (obj1 != null && obj2 != null) return obj1.equals(obj2);
        return false;
    }
}
