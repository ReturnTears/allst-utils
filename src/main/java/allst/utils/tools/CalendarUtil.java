package allst.utils.tools;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * 操作日期的工具类
 * Calendar
 *
 * @author June
 * @since 2021年09月
 */
public class CalendarUtil {
    private final Calendar c;

    public CalendarUtil() {
        c = Calendar.getInstance();
    }

    public CalendarUtil(long time) {
        c = Calendar.getInstance();
        c.setTimeInMillis(time);
    }

    public CalendarUtil(Date date) {
        c = Calendar.getInstance();
        c.setTime(date);
    }

    public CalendarUtil(Calendar cc) {
        c = cc;
    }

    /**
     * 设置Calendar字段的值
     */
    public CalendarUtil set(int field, int value) {
        c.set(field, value);
        return this;
    }

    /**
     * 将时间置为23:59:59:999
     */
    public CalendarUtil lastTime() {
        c.set(Calendar.MILLISECOND, 999);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.HOUR_OF_DAY, 23);
        return this;
    }

    /**
     * 将时间置为00:00:00:000
     */
    public CalendarUtil firstTime() {
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return this;
    }

    /**
     * 设置 时间	为-1则表示不设置
     */
    public CalendarUtil setTime(int h, int m, int s) {
        if (h > -1) c.set(Calendar.HOUR_OF_DAY, h);
        if (m > -1) c.set(Calendar.MINUTE, m);
        if (s > -1) c.set(Calendar.SECOND, s);
        return this;
    }

    /**
     * 设置时间
     *
     * @param sh 空白或-1都表示不设置
     */
    public CalendarUtil setTime(String sh, String sm, String ss) {
        int h = StringUtil.toInt(sh, -1);
        int m = StringUtil.toInt(sm, -1);
        int s = StringUtil.toInt(ss, -1);
        setTime(h, m, s);
        return this;
    }

    /**
     * 设置日期,	为-1则表示不设置
     */
    public CalendarUtil setDate(int y, int m, int d) {
        if (y > -1) c.set(Calendar.YEAR, y);
        if (m > -1) c.set(Calendar.MONTH, m);
        if (d > -1) c.set(Calendar.DAY_OF_MONTH, d);
        return this;
    }

    /**
     * @param sy 空白或-1表示不设置
     */
    public CalendarUtil setDate(String sy, String sm, String sd) {
        int y = StringUtil.toInt(sy, -1);
        int m = StringUtil.toInt(sm, -1);
        int d = StringUtil.toInt(sd, -1);
        setDate(y, m, d);
        return this;
    }

    /**
     * 将Date加上日期 	年	月	日
     */
    public CalendarUtil addDate(String sy, String sm, String sd) {
        int y = StringUtil.toInt(sy, 0);
        int m = StringUtil.toInt(sm, 0);
        int d = StringUtil.toInt(sd, 0);
        addDate(y, m, d);
        return this;
    }

    /**
     * 将Date加上日期 	年	月	日
     */
    public CalendarUtil addDate(int y, int m, int d) {
        c.add(Calendar.YEAR, y);
        c.add(Calendar.MONTH, m);
        c.add(Calendar.DAY_OF_MONTH, d);
        return this;
    }

    /**
     * 将Date加上时间	时	分	秒
     */
    public CalendarUtil addTime(int h, int m, int s) {
        c.add(Calendar.HOUR_OF_DAY, h);
        c.add(Calendar.MINUTE, m);
        c.add(Calendar.SECOND, s);
        return this;
    }

    /**
     * 将Date加上时间	时	分	秒
     */
    public CalendarUtil addTime(String sh, String sm, String ss) {
        int h = StringUtil.toInt(sh, 0);
        int m = StringUtil.toInt(sm, 0);
        int s = StringUtil.toInt(ss, 0);
        addTime(h, m, s);
        return this;
    }

    /**
     * 将Date加上毫秒
     */
    public CalendarUtil addMillis(Date date, int ms) {
        c.add(Calendar.MILLISECOND, ms);
        return this;
    }

    /**
     * 清除毫秒
     */
    public CalendarUtil clearMillis() {
        c.set(Calendar.MILLISECOND, 0);
        return this;
    }

    /**
     * 获取时间的毫秒表示
     */
    public long getTimeInMillis() {
        return c.getTimeInMillis();
    }

    public Date toDate() {
        return c.getTime();
    }

    public Date toSqlDate() {
        return new java.sql.Date(c.getTimeInMillis());
    }

    public Timestamp toTimestamp() {
        return new Timestamp(c.getTimeInMillis());
    }

    public Calendar toCalendar() {
        return c;
    }
}
