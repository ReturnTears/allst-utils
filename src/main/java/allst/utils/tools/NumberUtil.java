package allst.utils.tools;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Random;

/**
 * 处理数字的工具
 * 功能包括:进制转换,进制加减,小数位处理,左右对齐,生成随机数
 * 可以处理0-9_a-z_A-Z
 * 大写字符时,作为36进制以上处理
 * 36进制以下进制: 处理必须是小写,否则计算结果错误
 * 36进制以上进制: z+1=A
 * <p>
 * 64进制时 0-9 a-z_A-Z 和 + =
 * <p>
 * Long.Max_value转换:
 * 62进制:aZl8N0y58M7
 * 36进制:1y2p0ij32e8e7
 * 16进制:7fffffffffffffff
 *
 * @author June
 * @since 2021年09月
 */
public class NumberUtil {
    //	private static Logger log = LoggerFactory.getLogger(NumberUtil.class);

    public static char[] jz64 = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '+', '='};

    private static Random rd = new Random();

    /**
     * 最多暴露2位小数
     */
    private static DecimalFormat decimal2 = new DecimalFormat("#.##");
    /**
     * 总是保留2位小数
     */
    private static DecimalFormat alwaysDecimal2 = new DecimalFormat("#.00");

    /**
     * 保留2位小数
     *
     * @param always 是否总是保留2位小数
     *
     * @return
     */
    public static String formatDecimal2(Object val, boolean always) {
        if (always) return alwaysDecimal2.format(val);
        return decimal2.format(val);
    }

    /**
     * 小于等于
     *
     * @param num
     * @param n
     *
     * @return
     */
    public static boolean le(Number num, long n) {
        if (num == null) {
            return 0 <= n;
        }
        return num.longValue() <= n;
    }

    /**
     * 大于等于
     *
     * @param num
     * @param n
     *
     * @return
     */
    public static boolean ge(Number num, long n) {
        if (num == null) {
            return 0 >= n;
        }
        return num.longValue() >= n;
    }

    /**
     * 验证数值范围,为-1时表示不限,包含上限和下限
     * 上限下限同时不限时返回true
     *
     * @param num
     * @param sn
     * @param en
     *
     * @return
     */
    public static boolean checkRange(int num, int sn, int en) {
        if (sn < 0 && en < 0) {
            return true;
        }
        if (sn > -1) {
            if (en > -1) return num >= sn && num <= en;
            else return num >= sn;
        } else return num <= en;
    }

    public static boolean checkRange(BigDecimal d, int sn, int en) {
        int num = d.intValue();
        if (sn < 0 && en < 0) {
            return true;
        }
        if (sn > -1) {
            if (en > -1) return num >= sn && num <= en;
            else return num >= sn;
        } else return num <= en;
    }

    /**
     * 验证数值范围,为0时表示不限,包含上限和下限
     * 上限下限同时不限时返回true
     *
     * @param num
     * @param sn
     * @param en
     *
     * @return
     */
    public static boolean checkRange2(int num, int sn, int en) {
        if (sn <= 0 && en <= 0) {
            return true;
        }
        if (sn > 0) {
            if (en > 0) return num >= sn && num <= en;
            else return num >= sn;
        } else return num <= en;
    }

    /**
     * 将10进制转换成16
     */

    public static String parse10To16(long num) {
        return parseLongToString(num, 16, 0);
    }

    /**
     * 将10进制转换成36
     */
    public static String parse10To36(long num) {
        return parseLongToString(num, 36, 0);
    }

    /**
     * 将10进制转换成62
     */
    public static String parse10To62(long num) {
        return parseLongToString(num, 62, 0);
    }

    /**
     * 字符串小写,将16进制转换成10	字符串小于:7fffffffffffffff
     */
    public static long parse16To10(String num) {
        return parseStringToLong(num, 16);
    }

    /**
     * 字符串小写,将36进制转换成10	字符串小于:1y2p0ij32e8e7
     */
    public static long parse36To10(String num) {
        return parseStringToLong(num, 36);
    }

    /**
     * 字符串小写,将62进制转换成10	字符串小于:aZl8N0y58M7
     */
    public static long parse62To10(String num) {
        return parseStringToLong(num, 62);
    }

    /**
     * 字符串小写,将16进制加上指定数
     */
    public static String add16(String num, int addnum) {
        return add(num, 16, addnum);
    }

    /**
     * 将36进制加上指定数
     */
    public static String add36(String num, int addnum) {
        return add(num, 36, addnum);
    }

    /**
     * 将62进制加上指定数
     */
    public static String add62(String num, int addnum) {
        return add(num, 62, addnum);
    }

    /**
     * <pre>
     * 警告:被加数不能为负,如果最后结果为负数时将导致错误.只支持从0-9 a-z A-Z 顺序的进制
     * 将字符串作为指定进制加上数,
     * 未进行格式检查,num格式错误时将导致计算结果错误
     * </pre>
     *
     * @param num    小写,不能是负数		格式:		000ff
     * @param jz     进制
     * @param addnum 可以是负数
     *
     * @return
     */
    public static String add(String num, int jz, int addnum) {
        char[] cs = num.toCharArray();
        int n = cs.length;
        int c = 0;
        int jn = addnum;
        for (int i = n - 1; i >= 0; i--) {
            if (jn == 0) break; //无进位数则,退出
            c = cs[i];
            if (c >= 97) c -= 87;
            else if (c >= 65) c -= 55;
            else if (c >= 48) c -= 48;
            c += jn;
            jn = (int) Math.floor(c / (double) jz);
            c = c % jz + jz;
            cs[i] = jz64[c % jz];
        }
        if (jn != 0) {
            //如果超出位最大值,进行补位,为负时异常
            if (jn < 0)
                throw new NumberFormatException("Number (" + num + ") + (" + addnum + ") by Decimal(" + jz + ") is overflow!");
            return jz64[jn] + new String(cs);
        }
        return new String(cs);
    }

    /**
     * <pre>
     * 警告:被加数不能为负,如果最后结果为负数时将导致错误
     * 将字符串作为指定进制加上数,
     * 未进行格式检查,num格式错误时将导致计算结果错误
     * </pre>
     *
     * @param jzArray 进制的字符串数组  首个数组的值为0的值
     * @param num     小写,不能是负数		格式:		000ff
     * @param jz      进制
     * @param addnum  可以是负数
     *
     * @return
     */
    public static String add(char[] jzArray, String num, int jz, int addnum) {
        char[] cs = num.toCharArray();
        int n = cs.length;
        int c = 0;
        int jn = addnum;
        for (int i = n - 1; i >= 0; i--) {
            if (jn == 0) break; //无进位数则,退出
            c = jn + getCharIndex(jzArray, cs[i]);
//			c += jn;
            jn = (int) Math.floor(c / (double) jz);
            c = c % jz + jz;
            cs[i] = jzArray[c % jz];
        }
        if (jn != 0) {
            //如果超出位最大值,进行补位,为负时异常
            if (jn < 0)
                throw new NumberFormatException("Number (" + num + ") + (" + addnum + ") by Decimal(" + jz + ") is overflow!");
            return jzArray[jn] + new String(cs);
        }
        return new String(cs);
    }

    /**
     * 获取字符串的下标
     *
     * @param cc
     * @param c
     *
     * @return
     */
    public static int getCharIndex(char[] cc, char c) {
        for (int i = 0; i < cc.length; i++) {
            if (cc[i] == c) return i;
        }
        return -1;
    }

    /**
     * 删除数字左边的0,只是简单的删除左边的0
     *
     * @param num
     *
     * @return
     */
    public static String cutLeftZero(String num) {
        char[] cs = num.toCharArray();
        int index = -1;
        for (int i = 0; i < cs.length; i++) {
            if (cs[i] != '0') {
                index = i;
                break;
            }
        }
        if (index > -1) return new String(cs, index, cs.length - index);
        return num;
    }

    /**
     * 改变进制,进制只支持36进制以下(暂时未用)
     *
     * @param nums
     * @param oldjz 字符串以前的进制
     * @param newjz 转换成的新的进制
     *
     * @return
     */
    public static String changeCarry(String nums, int oldjz, int newjz) {
        throw new RuntimeException("method not implements");
//		char[] cs=nums.toCharArray();
//		char c=0;
//		int n=cs.length;
//		int jn=0;		//进位数		用于新数
//		int yn=0;		//余数		用于原数
//		StringBuilder sb=new StringBuilder();
//		for(int i=n-1;i>=0;i--){
//			c=cs[i];
//			if(c>=97)c-=87;
//			else if(c>=48)c-=48;
//			yn+=c*Math.pow(oldjz, n-i-1);
//			if(yn<newjz)continue;
//			else{
//				BigInteger bi=null;
//				System.out.println(jz36[yn%newjz+jn]);
//				sb.append(jz36[yn%newjz+jn]);
//				jn=yn/newjz;
//
//			}
//		}
//		System.out.println("------"+jn);
//		while(yn>0){
//			System.out.println(jz36[yn%newjz+jn]);
//			sb.append(jz36[yn%newjz+jn]);
//			yn=yn/newjz;
//		}
//		sb=sb.reverse();
//		return sb.toString();
    }

    /**
     * 进行数组加减法
     *
     * @param num
     * @param add
     * @param jz
     * @param overflow
     *
     * @return
     */
    public static long[] arrayAdd(long[] num, long[] add, long jz, boolean overflow) {
        int len = num.length + add.length;
        long[] re = new long[len]; //
        long[] add2 = new long[add.length];

        for (int i = 0; i < add.length; i++)
            add2[i] = add[add.length - i - 1];
        add = add2;
        for (int i = 0; i < num.length; i++)
            re[i] = num[num.length - 1 - i];

        long jn = 0; //进位

        for (int i = 0; i < add.length; i++) {

            long temp = re[i] + add[i] + jn;
            if (temp < 0) {
                //减法
                temp = jz - temp;
                jn = -(temp / jz);
                re[i] = jz - (temp % jz);
            } else {
                //加法
                jn = temp / jz;
                re[i] = temp % jz;
            }

        }
        /**
         * 处理进位
         */
        if (jn != 0) {
            for (int i = add.length; i < re.length; i++) {
                long temp = re[i] + jn;
                if (temp < 0) {
                    //减法
                    temp = jz - temp;
                    jn = -(temp / jz);
                    re[i] = jz - (temp % jz);
                } else {
                    //加法
                    jn = temp / jz;
                    re[i] = temp % jz;
                }
                if (jn == 0) break;
            }
        }

        /**
         *
         */
        int index = -1;
        for (int i = re.length - 1; i >= 0; i--) {
            if (re[i] != 0) {
                index = i;
                break;
            }
        }

        if (index > -1) {
            long[] temp = new long[index + 1];
//			System.arraycopy(re, 0, temp, 0, index+1);

            for (int i = 0; i < index + 1; i++) {
                temp[i] = re[index - i];
            }

            re = temp;

        } else {
            re = new long[num.length];
        }
        if (!overflow && re.length > num.length) throw new RuntimeException("Overflow");
        return re;
    }

    /**
     * <pre>
     * 将字符串转换成 10进制	支持 10	16	36	(或者按照 0-9_a-z 的顺序排列的进制) 进制小于36
     * jz 表示进制 num表示的进制
     * 以 - 开始则,计算成负,否则为正
     * 注意:未进行字符正确性验证,如果  使用超过进制的字符,计算结果会错误
     * </pre>
     */

    public static long parseStringToLong(String num, int jz) {
        if (num == null || num.length() < 1) return 0;
        boolean fu = false;
        char sc = num.charAt(0);
        if (sc == '-') {
            fu = true;
            num = num.substring(1);
        }
        char[] cs = num.toCharArray();
        int temp = 0;
        long all = 0;
        for (int i = 0; i < cs.length; i++) {
            temp = cs[i];
            if (temp >= 97) temp -= 87;
            else if (temp >= 65) temp -= 55;
            else if (temp >= 48) temp -= 48;
            all += Math.pow(jz, cs.length - i - 1) * temp;
        }
        if (fu) return 0 - all;
        return all;
    }

    /**
     * <pre>
     * 将num转换成  jz的数
     * len	补齐长度,小于1时表示不补齐
     * 将指定数转换成指定进制的字符串
     * </pre>
     */
    public static String parseLongToString(long num, int jz, int len) {
        //if(num==0)return "0";
        boolean fu = false;
        if (num < 0) fu = true;
        num = Math.abs(num);
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(jz64[(int) (num % jz)]);
            num = num / jz;
        } while (num > 0);
        String temp = sb.reverse().toString();
        if (len > 0 && temp.length() < len) {
            byte[] tb = new byte[len];
            for (int i = 0; i < len; i++)
                tb[i] = '0';
            temp = new String(tb) + temp;
            temp = temp.substring(temp.length() - len, temp.length());

        }
        if (fu) return "-" + temp;
        return temp;
    }

    /**
     * 将小数长度控制为指定长度
     *
     * @param num
     * @param len
     *
     * @return
     */
    public static String decimalLen(String num, int len) {
        return cutDecimal(fillDecimal(num, len), len);
    }

    /**
     * 字符串小写,切小数位,小数位过长时切除,不足时不管
     */
    public static String cutDecimal(String num, int len) {
        if (num != null) {
            int index = num.indexOf('.');
            if (index > -1) {
                int l = num.length() - 1;
                if (len < 1) return num.substring(0, index);
                else if (len < (l - index)) {
                    return num.substring(0, index + len + 1);
                }
            }
            return num;
        }
        return "";
    }

    /**
     * 字符串小写,填充小数位,小数位过长不管,不足时补齐
     */
    public static String fillDecimal(String num, int len) {
        if (num != null) {
            if (len < 0) return num;
            int index = num.indexOf('.');
            byte[] b = new byte[len];
            for (int i = 0; i < b.length; i++)
                b[i] = '0';
            String temp = new String(b);
            if (index > -1) {
                int l = num.length() - 1;
                if (len > (l - index)) {
                    num = num + temp;
                    return num.substring(0, index + len + 1);
                }
            } else {
                return num + '.' + temp;
            }
            return num;
        }
        return "";
    }

    /**
     * 将float转换成byte[]
     *
     * @param f
     *
     * @return
     */
    public static byte[] floatToBytes(float f) {
        return intToBytes(Float.floatToIntBits(f));
    }

    /**
     * 将double转换成byte[]
     *
     * @param d
     *
     * @return
     */
    public static byte[] doubleToBytes(double d) {
        return longToBytes(Double.doubleToLongBits(d));
    }

    /**
     * 将int转换成byte[]
     *
     * @param i
     *
     * @return
     */
    public static byte[] intToBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i >>> 24);
        b[1] = (byte) (i >>> 16);
        b[2] = (byte) (i >>> 8);
        b[3] = (byte) i;
        return b;
    }

    /**
     * 将long转换成byte[]
     *
     * @param l
     *
     * @return
     */
    public static byte[] longToBytes(long l) {
        byte[] b = new byte[8];
        b[0] = (byte) (l >>> 56);
        b[1] = (byte) (l >>> 48);
        b[2] = (byte) (l >>> 40);
        b[3] = (byte) (l >>> 32);
        b[4] = (byte) (l >>> 24);
        b[5] = (byte) (l >>> 16);
        b[6] = (byte) (l >>> 8);
        b[7] = (byte) (l);
        return b;
    }

    /**
     * 转换byte到二进制
     *
     * @param b
     *
     * @return
     */
    public static String parseByteToBinary(byte b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i >= 0; --i) {
            if (((1 << i) & b) == 0) sb.append('0');
            else sb.append('1');
        }
        return sb.toString();
    }

    /**
     * 转换byte到二进制
     *
     * @param bb
     *
     * @return
     */
    public static String parseByteToBinary(byte[] bb) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bb) {
            sb.append(parseByteToBinary(b));
        }
        return sb.toString();
    }

    /**
     * 将byte[]转换成16进制,不足位补0,
     *
     * @param bb 格式:每两位表示一个十六进制
     *
     * @return
     */
    public static String parseByteToHex(byte[] bb) {
//		StringBuilder sb = new StringBuilder();
//		int n = 0;
//		for (int i = 0; i < bb.length; i++) {
//			n = bb[i] & 0xff;
//			if (n < 16)
//				sb.append("0");
//			sb.append(Integer.toHexString(n));
//		}
//		return sb.toString();
        return HexUtil.encodeToString(bb);
    }

    /**
     * 将16进制转换成byte[],使用两位拆解
     *
     * @param ss 格式:每两位表示一个十六进制
     *
     * @return
     */
    public static byte[] parseHexToByte(String ss) {
//		int len = ss.length();
//		byte[] bb = new byte[len / 2];
//		int index = 0;
//		String temp = null;
//		for (int i = 0; i < len; i += 2) {
//			temp = ss.substring(i, i + 2);
//			bb[index++] = (byte) NumberUtil.parse16To10(temp);
//		}
//		return bb;
        return HexUtil.decode(ss);
    }

    /**
     * 获取随机数,包含下限,不包涵上限
     *
     * @param sn
     * @param en
     *
     * @return
     */
    public static int getRandom(int sn, int en) {

//		return (int) (rd.nextFloat()*(en-sn)+sn);

        return rd.nextInt(en - sn) + sn;
    }

    public static long getRandom(long sn, long en) {
        return (long) (rd.nextDouble() * (en - sn) + sn);
//		return 0;
    }

    /**
     * 获取随机的概率标志, sn/en的概率
     *
     * @param sn
     * @param en
     *
     * @return
     */
    public static boolean getRandomRate(long sn, long en) {
        long n = getRandom(0, en);
        return n < sn;
    }

    /**
     * 获取唯一id,	长度为19位	格式:时间+随机数		1380531412765690327
     */
    public static long getUniqueId() {
        long cur = System.currentTimeMillis();
        long rand = rd.nextInt(0xfffff);
        return cur << 20 | rand;
    }

    /**
     * 获取唯一id 	长度19位		格式:时间数字样式+随机数(2位)	2011092111555134735
     */
    public static long getUniqueTimeId() {
        String s = DateUtil.toDateTimeNoSeparator(new Date());
        long cur = Long.parseLong(s);
        return cur + getRandom(10, 100);
    }

    /**
     * 求数字串各位的和,只支持到long类型的精度
     */
    public static int getNumberCharSum(String s) {
        if (StringUtil.checkEmpty(s)) {
            return getNumberCharSum(new BigInteger(s).longValue());
        }

        return 0;
    }

    /**
     * 求数字串各位的和
     */
    public static int getNumberCharSum(long num) {
        int c = 0;
        do {
            c += num % 10;
            num = num / 10;
        } while (num > 0);
        return c;
    }

    /**
     * 获取值,如果value小于等于0则返回,defValue
     *
     * @param value
     * @param defValue
     *
     * @return
     */
    public static int getValue(int value, int defValue) {

        if (value < 1) {
//			log.trace("使用默认值:" + defValue);
            return defValue;
        }

        return value;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static BigDecimal check(BigDecimal b) {
        if (b == null) return BigDecimal.ZERO;
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static BigInteger check(BigInteger b) {
        if (b == null) return BigInteger.ZERO;
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static Integer check(Integer b) {
        if (b == null) return Integer.valueOf(0);
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static Long check(Long b) {
        if (b == null) return Long.valueOf(0);
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static Float check(Float b) {
        if (b == null) return Float.valueOf(0);
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static Double check(Double b) {
        if (b == null) return Double.valueOf(0);
        return b;
    }

    /**
     * 检查非Null.如果空则返回0
     *
     * @param b
     *
     * @return
     */
    public static Byte check(Byte b) {
        if (b == null) return Byte.valueOf((byte) 0);
        return b;
    }

    /**
     * 获取大的
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static int getBigger(int a, int b) {
        return a > b ? a : b;
    }

    /**
     * 获取小的
     *
     * @param a
     * @param b
     *
     * @return
     */
    public static int getSmaller(int a, int b) {
        return a < b ? a : b;
    }

    /**
     * format表示格式   例如: 0.00   #.### 等等
     *
     * @param val
     * @param format
     *
     * @return
     */
    public static String format(BigDecimal val, String format) {
        DecimalFormat f = new DecimalFormat(format);
        return f.format(val.doubleValue());
    }

    public static void main(String[] args) {
//		System.out.println(alignNumberRight("xx00", 5,'0'));
//		String ss="ZZa";
//		System.out.println(getUniqueId());
//		System.out.println(getUniqueTimeId());
//		System.out.println(Long.MAX_VALUE);
//		System.out.println(parseLongToString(Long.MAX_VALUE, 2, 0));
//		System.out.println(parseLongToString(System.currentTimeMillis(), 2, 0));
//		System.out.println(parseLongToString(System.currentTimeMillis()<<22, 2, 0));
//
//		System.out.println(DateTool.getDateTime(new Date(Long.MAX_VALUE>>20)));

//		System.out.println(parse10To62(System.currentTimeMillis()));
        System.out.println(NumberUtil.parseStringToLong("0800", 10));
        String s = "9999";
        s = add(s, 26, -22345);
        System.out.println(s);

//		byte b=-1;
//		//00000 000
//
//		byte bt=(byte) 0x7f;
//		System.out.println(bt);
//		System.out.println(bt>>2);
    }
}
