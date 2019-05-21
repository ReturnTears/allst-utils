package allst.utils.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 日期时间工具类
 */
public class DateTimeUtil {
    /**
     * 返回当前日期时间
     * @return
     */
    public static String currDateTime(String startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        // 下一分
        calendar.add(Calendar.MINUTE, 1);
        String dateTime = sdf.format(calendar.getTime());
        if (startTime == "" || startTime == null) {
            return dateTime;
        }
        return startTime;
    }
}
