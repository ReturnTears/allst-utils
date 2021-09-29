package allst.utils.tools;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用于处理时间加减等计算
 *
 * @author June
 * @since 2021年09月
 */
public class DateUtil {
    //	private static DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //	private static DateFormat datetimeFormatNosep = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    //	private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //	private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    private static final String datetimeFormatText = "yyyy-MM-dd HH:mm:ss";
    private static final String datetimeFormatNosepText = "yyyyMMddHHmmssSSS";
    private static final String dateFormatText = "yyyy-MM-dd";
    private static final String timeFormatText = "HH:mm:ss";

    private static DateFormat createDataFormat(String text) {
        return new SimpleDateFormat(text);
    }

    /**
     * 对时间进行类型转换
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <D extends Date, R extends Date> R castTo(D value, Class<R> clazz) {
        if (value == null) return null;
        if (clazz.equals(Date.class)) {
            return (R) value;
        } else if (clazz.equals(java.sql.Date.class)) {
            return (R) new java.sql.Date(value.getTime());
        } else if (clazz.equals(java.sql.Timestamp.class)) {
            return (R) new java.sql.Timestamp(value.getTime());
        }
        throw new RuntimeException(String.format("Value type(%s) error for cast to type(%s)", value.getClass(), clazz.getClass()));
    }

    /**
     * 将毫秒数转换成月
     *
     * @param millsecond
     *
     * @return
     */

    public static float toMonth(long millsecond) {
        return (millsecond) / ((float) 3600 * 1000 * 24 * 30);
    }

    /**
     * 将秒数转换成      n天n时n分
     *
     * @param miao
     *
     * @return
     */
    public static String toDayHourMinute(int miao) {

        return null;
    }

    /**
     * 将Date加上日期 	年	月	日
     *
     * @param date
     * @param y
     * @param m
     * @param d
     *
     * @return
     */

    public static Date addDate(Date date, int y, int m, int d) {
        Calendar c = getCalendar(date);
        c.add(Calendar.YEAR, y);
        c.add(Calendar.MONTH, m);
        c.add(Calendar.DAY_OF_MONTH, d);
        return c.getTime();
    }

    /**
     * 将Date加上时间	时	分	秒
     *
     * @param date
     * @param h
     * @param m    *
     */
    public static Date addTime(Date date, int h, int m, int s) {
        Calendar c = getCalendar(date);
        c.add(Calendar.HOUR_OF_DAY, h);
        c.add(Calendar.MINUTE, m);
        c.add(Calendar.SECOND, s);
        return c.getTime();
    }

    /**
     * 将Date加上毫秒
     */
    public static Date addMilliSecond(Date date, int ms) {
        Calendar c = getCalendar(date);
        c.add(Calendar.MILLISECOND, ms);
        return c.getTime();
    }

    /**
     * 设置Date的 日期,	为-1则表示不设置
     *
     * @param date
     * @param y    从1开始
     * @param m    注意: 从0开始. 1表示1月
     * @param d    从1开始
     *
     * @return
     */
    public static Date setDate(Date date, int y, int m, int d) {
        Calendar c = getCalendar(date);
        if (y > -1) c.set(Calendar.YEAR, y);
        if (m > -1) c.set(Calendar.MONTH, m);
        if (d > -1) c.set(Calendar.DAY_OF_MONTH, d);
        return c.getTime();
    }

    /**
     * 设置Date的 时间	为-1则表示不设置
     *
     * @param date
     * @param h
     * @param m    *
     */
    public static Date setTime(Date date, int h, int m, int s) {
        Calendar c = getCalendar(date);
        if (h > -1) c.set(Calendar.HOUR_OF_DAY, h);
        if (m > -1) c.set(Calendar.MINUTE, m);
        if (s > -1) c.set(Calendar.SECOND, s);
        return c.getTime();
    }

    /**
     * 返回符合中国时间的Calendar
     */
    //	public static Calendar getCalendar(){
    //		Calendar c=Calendar.getInstance();
    //		c.setFirstDayOfWeek(Calendar.MONDAY);
    //		return c;
    //	}

    /**
     * 返回符合中国时间的Calendar
     */
    public static Calendar getCalendar(Date date) {
        //		if(date==null)date=new Date();
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        if (date != null) c.setTime(date);
        return c;
    }

    /**
     * 清除时间部分,设置成 00:00:00.000
     */
    public static Calendar setTimeFirst(Calendar c) {
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c;
    }

    public static Timestamp setTimeFirst(Timestamp d) {
        Calendar c = getCalendar(d);
        setTimeFirst(c);
        return new Timestamp(c.getTimeInMillis());
    }

    public static java.sql.Date setTimeFirst(java.sql.Date d) {
        Calendar c = getCalendar(d);
        setTimeFirst(c);
        return new java.sql.Date(c.getTimeInMillis());
    }

    /**
     * 清除时间部分,设置成 00:00:00.000
     */
    public static Date setTimeFirst(Date d) {
        Calendar c = getCalendar(d);
        setTimeFirst(c);
        return c.getTime();
    }

    /**
     * 将时间部分设置成一天的最后, 23:59:59.999
     */
    public static Calendar setTimeLast(Calendar c) {
        c.set(Calendar.MILLISECOND, 999);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.HOUR_OF_DAY, 23);
        return c;
    }

    public static Timestamp setTimeLast(Timestamp d) {
        Calendar c = getCalendar(d);
        setTimeLast(c);
        return new Timestamp(c.getTimeInMillis());
    }

    public static java.sql.Date setTimeLast(java.sql.Date d) {
        Calendar c = getCalendar(d);
        setTimeLast(c);
        return new java.sql.Date(c.getTimeInMillis());
    }

    /**
     * 将时间部分设置成一天的最后, 23:59:59.999
     */
    public static Date setTimeLast(Date d) {
        Calendar c = getCalendar(d);
        setTimeLast(c);
        return c.getTime();
    }

    /**
     * 获取昨天
     *
     * @param date
     *
     * @return
     */
    public static Date getPrevDay(Date date) {
        Calendar c = getCalendar(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
    }

    /**
     * 获取明天
     *
     * @param date
     *
     * @return
     */
    public static Date getNextDay(Date date) {
        Calendar c = getCalendar(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 本月开始		格式:2000-MM-1 00:00:00
     */
    public static Date getMonthFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 上月开始			格式:2000-MM-1 00:00:00
     */
    public static Date getPrevMonthFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    /**
     * 下月开始			格式:2000-MM-1 00:00:00
     */
    public static Date getNextMonthFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    /**
     * 本月结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getMonthLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return c.getTime();
    }

    /**
     * 下月结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getNextMonthLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.add(Calendar.MONTH, 1);
        return c.getTime();
    }

    /***
     * 上月结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getPrevMonthLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.add(Calendar.MONTH, -1);
        return c.getTime();
    }

    /**
     * 本周开始		格式:2000-MM-1 00:00:00
     */
    public static Date getWeekFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return c.getTime();
    }

    /**
     * 上月开始		格式:2000-MM-1 00:00:00
     */
    public static Date getPrevWeekFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        return c.getTime();
    }

    /**
     * 下周开始		格式:2000-MM-1 00:00:00
     */
    public static Date getNextWeekFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.WEEK_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 本周结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getWeekLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return c.getTime();
    }

    /**
     * 下周结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getNextWeekLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.add(Calendar.WEEK_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 上周结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getPrevWeekLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        c.add(Calendar.WEEK_OF_YEAR, -1);
        return c.getTime();
    }

    /**
     * 本年开始		格式:2000-MM-1 00:00:00
     */
    public static Date getYearFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_YEAR, 1);
        return c.getTime();
    }

    /**
     * 上年开始		格式:2000-MM-1 00:00:00
     */
    public static Date getPrevYearFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.add(Calendar.YEAR, -1);
        return c.getTime();
    }

    /**
     * 下年开始		格式:2000-MM-1 00:00:00
     */
    public static Date getNextYearFirst(Date date) {
        Calendar c = getCalendar(date);
        setTimeFirst(c);
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.add(Calendar.YEAR, 1);
        return c.getTime();
    }

    /**
     * 本年结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getYearLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
        return c.getTime();
    }

    /**
     * 下年结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getNextYearLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
        c.add(Calendar.YEAR, 1);
        return c.getTime();
    }

    /**
     * 上年结束		格式:2000-MM-31 23:59:59.999
     */
    public static Date getPrevYearLast(Date date) {
        Calendar c = getCalendar(date);
        setTimeLast(c);
        c.set(Calendar.DAY_OF_YEAR, c.getActualMaximum(Calendar.DAY_OF_YEAR));
        c.add(Calendar.YEAR, -1);
        return c.getTime();
    }

    /**
     * 验证字符串是否是时间格式. 如果为空则返回false
     * <p>
     * *
     */
    public static boolean checkDate(String s) {
        try {
            if (StringUtil.checkEmpty(s)) return parseDate(s) != null;
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * 将对象转换成Date,格式  2012-12-12 或者 2012-12-12 12:12:12.转换错误则返回null
     * <p>
     * *
     */
    public static Date parseDate(String s) {
        return parseDate(s, null);
    }

    /**
     * 转换日期并且将日期设置成当天  23:59:59 999
     * <p>
     * *
     */
    public static Date parseDateEnd(String s) {
        return parseDateEnd(s, null);
    }

    /**
     * 转换日期并且将日期设置成当天  23:59:59 999
     *
     * @param s
     * @param pattern
     *
     * @return
     */
    public static Date parseDateEnd(String s, String pattern) {
        Date d = parseDate(s, pattern);
        if (d == null) return null;
        return DateUtil.setTimeLast(d);
    }

    /**
     * 解析时间,pattern为空时 格式为 2011-01-11 13:12:12	转换错误时返回null
     *
     * @param s       字符串默认将 / 替换成 -
     * @param pattern 指定解析格式,为空时使用默认格式
     *
     * @return
     */
    public static Date parseDate(String s, String pattern) {
        if (StringUtil.checkEmpty(s)) {
            s = s.replace('/', '-');
            DateFormat df = null;
            if (pattern == null || pattern.length() < 1) {
                //如果不存在,格式则可能是纯数字
                if (s.indexOf('-') < 0 && s.lastIndexOf(':') < 0) {
                    return new Date(StringUtil.toLong(s, 0));
                } else {
                    if (StringUtil.getLength(s) > 11) {
                        df = createDataFormat(datetimeFormatText);
                    } else {
                        df = createDataFormat(dateFormatText);
                    }
                }

            } else {
                df = new SimpleDateFormat(pattern);
            }
            try {
                return df.parse(s.toString());
            } catch (ParseException e) {

            }
        }
        return null;
    }

    /**
     * 将字符串按照无符号日期时间转换,格式:20110101120000000	日期+时间+毫秒
     */
    public static Date parseDateNoSeparator(String s) {
        try {
            s = StringUtil.alignRight(s, 17, '0');
            return createDataFormat(datetimeFormatNosepText).parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将字符串时间部分转换成毫秒数	格式:		12:12:12或者	2001:12:12 12:12:12(必须是空格分割)
     * 注意:不支持	毫秒
     * <p>
     * *
     */
    public static long getTimeNum(String s) {
        if (s == null || s.length() < 1) return -1;
        int index = s.lastIndexOf(' ');
        if (index > -1) s = s.substring(index + 1, s.length());

        String[] ss = s.split(":");
        long[] ls = CollectionUtil.parseStringToLong(ss);
        if (ls.length < 3) return -1;
        return ls[0] * 3600000 + ls[1] * 60000 + ls[2] * 1000;
    }

    /**
     *
     */
    public static String toDate(Date d, String format) {
        if (d == null) return "";
        if (!StringUtil.checkEmpty(format)) {
            format = dateFormatText;
        }
        return createDataFormat(format).format(d);
    }

    /**
     * 获取日期时间, 格式 2011-01-10 12:12:12 .如果为null则返回空字符串
     */

    public static String toDateTime(Date d) {
        if (d == null) return "";
        return createDataFormat(datetimeFormatText).format(d);
    }

    /**
     * 获取指定格式的日期时间 .如果为null则返回空字符串
     *
     * @param d
     * @param format
     *
     * @return
     */
    public static String toDateTime(Date d, String format) {
        if (d == null) return "";
        DateFormat datetimeFormat = new SimpleDateFormat(format);
        return datetimeFormat.format(d);
    }

    /**
     * 获取无分隔符的日期时间+毫秒,格式 20110101120000000

     */
    public static String toDateTimeNoSeparator(Date d) {
        if (d == null) return "";
        return createDataFormat(datetimeFormatNosepText).format(d);
    }

    /**
     * 获取日期,格式 2011-01-01
     *
     * @return
     */
    public static String toDate(Date d) {
        if (d == null) return "";
        return createDataFormat(dateFormatText).format(d);
    }

    /**
     * 获取日期无分隔符,格式 20110101
     *
     * @return
     */
    public static String toDateNoSeparator(Date d) {
        if (d == null) return "";
        return createDataFormat(datetimeFormatNosepText).format(d).substring(0, 8);
    }

    /**
     * 获取时间,格式 13:02:12
     *
     * @return
     */
    public static String toTime(Date d) {
        if (d == null) return "";
        return createDataFormat(timeFormatText).format(d);
    }

    /**
     * 获取时间无分隔符,格式 130212
     *
     * @return
     */
    public static String toTimeNoSeparator(Date d) {
        if (d == null) return "";
        return createDataFormat(datetimeFormatNosepText).format(d).substring(8, 14);
    }

    /**
     * 获取Date的年,为null时表示当前
     */
    public static int getYear(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.YEAR);
    }

    /**
     * 获取Date的月,为null时表示当前.注意:从0开始, 0表示1月份
     */
    public static int getMonth(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.MONTH);
    }

    /**
     * 获取Date的日,为null时表示当前
     */
    public static int getDay(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取Date的小时,为null时表示当前
     */
    public static int getHour(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取Date的分,为null时表示当前
     */
    public static int getMinute(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.MINUTE);
    }

    /**
     * 获取Date的秒,为null时表示当前
     */
    public static int getSecond(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.SECOND);
    }

    /**
     * 获取Date的毫秒,为null时表示当前
     */
    public static int getMillisecond(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.MILLISECOND);
    }

    /**
     * 获取Date的当前年的第几周
     *
     * @param d
     *
     * @return
     */
    public static int getYearWeek(Date d) {
        Calendar c = getCalendar(d);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取时间的指定属性
     *
     * @param d
     * @param field
     *
     * @return
     */
    public static int getField(Date d, int field) {
        Calendar c = getCalendar(d);
        return c.get(field);
    }

    /**
     * 时间相减. 返回差距毫秒
     *
     * @param d1
     * @param d2
     *
     * @return
     */
    public static long subDateTime(Date d1, Date d2) {
        return (d1 == null ? 0 : d1.getTime()) - (d2 == null ? 0 : d2.getTime());
    }

    /**
     * 时间相减. 返回相差天数
     *
     * @param d1
     * @param d2
     *
     * @return
     */
    public static double subDateTimeDay(Date d1, Date d2) {
        long d = subDateTime(d1, d2);
        return ((double) d) / (3600 * 1000 * 24);
    }

    /**
     * 时间相减. 返回相差小时数
     *
     * @param d1
     * @param d2
     *
     * @return
     */
    public static double subDateTimeHour(Date d1, Date d2) {
        long d = subDateTime(d1, d2);
        return ((double) d) / (3600 * 1000);
    }

    /**
     * 获取日期的数字形式,当前时间清除掉 时分秒的数值
     *
     * @param d
     *
     * @return
     */

    public static long getDateNum(Date d) {
        return setTimeFirst(d).getTime();
    }

    /**
     * 将时间转换成秒数.如果为空则返回-1
     *
     * @param time 格式: 08:00:00		后面可以缺失,08:00也可以识别
     *
     * @return
     */
    public static int timeToSecond(String time) {
        if (!StringUtil.checkEmpty(time)) return -1;
        String[] ss = time.split(":");
        int h = StringUtil.toInt(CollectionUtil.getIndex(ss, 0), 0);
        int m = StringUtil.toInt(CollectionUtil.getIndex(ss, 1), 0);
        int s = StringUtil.toInt(CollectionUtil.getIndex(ss, 2), 0);
        return h * 60 * 60 + m * 60 + s;
    }

    /**
     * 比较时间是否在指定范围段中.支持跨天判断.包含上限和下限.
     * 支持跨天判断.当开始大于结束的时候表示跨天
     * 如果开始和结束都为空则始终返回true
     *
     * @param time  时间
     * @param start 开始时间. 包含等于,如果为空则不限制
     * @param end   结束时间. 包含等于,如果为空则不限制
     *
     * @return
     */
    public static boolean checkTimeIn(String time, String start, String end) {
        int s = timeToSecond(start);
        int e = timeToSecond(end);
        if (s == -1 && e == -1) return true;
        if (e == -1) { //如果end未设置则设置为最大
            e = Integer.MAX_VALUE;
        }
        int t = timeToSecond(time);
        if (t == -1) return false;
        if (s < e) {
            return t >= s && t <= e;
        } else { //跨天的情况
            return t <= e || t >= s;
        }
    }

    /**
     * 构建月时间序列
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static List<Date> getMonthSequence(Date start, Date end) {
        List<Date> list = new ArrayList<Date>();
        start = DateUtil.getMonthFirst(start);
        end = DateUtil.getMonthLast(end);
        long sl = start.getTime();
        long el = end.getTime();
        if (sl >= el) return list;

        Date d = null;
        while (sl < el) {
            d = new Date(sl);
            list.add(d);
            d = DateUtil.addDate(d, 0, 1, 0);
            sl = d.getTime();
        }

        return list;
    }

    /**
     * 构建月时间序列
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static List<String> getMonthSequences(Date start, Date end) {
        List<Date> list = getMonthSequence(start, end);
        List<String> result = new ArrayList<String>();
        for (Date d : list) {
            result.add(DateUtil.toDateTime(d, "yyyy-MM"));
        }
        return result;
    }

    /**
     * 构建天时间序列
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static List<Date> getDaySequence(Date start, Date end) {
        List<Date> list = new ArrayList<Date>();
        start = DateUtil.setTimeFirst(start);
        end = DateUtil.setTimeLast(end);
        long sl = start.getTime();
        long el = end.getTime();
        if (sl >= el) return list;

        Date d = null;
        while (sl < el) {
            d = new Date(sl);
            list.add(d);
            d = DateUtil.addDate(d, 0, 0, 1);
            sl = d.getTime();
        }

        return list;
    }

    /**
     * 构建天时间序列
     *
     * @param start
     * @param end
     *
     * @return
     */
    public static List<String> getDaySequences(Date start, Date end) {
        List<Date> list = getDaySequence(start, end);
        List<String> result = new ArrayList<String>();
        for (Date d : list) {
            result.add(DateUtil.toDateTime(d, "yyyy-MM-dd"));
        }
        return result;
    }

    /*public static void main(String[] args) {
        System.out.println("时间08:00 in (06:00-12:00) = " + checkTimeIn("08:00", "06:00", "12:00"));
        System.out.println("时间08:00 in (23:00-09:00) = " + checkTimeIn("08:00", "23:00", "09:00"));
        System.out.println("时间08:00 in (12:00-24:00) = " + checkTimeIn("08:00", "12:00", "24:00"));
        System.out.println("时间08:00 in (12:00-07:00) = " + checkTimeIn("08:00", "12:00", "07:00"));
    }*/

    /**
     * @param date
     * @param day
     *
     * @return
     */
    public static Date addDay(Date date, int day) {
        Calendar c = DateUtil.getCalendar(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }
}
