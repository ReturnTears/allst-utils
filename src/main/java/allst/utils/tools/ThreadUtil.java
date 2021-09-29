package allst.utils.tools;

/**
 * @author June
 * @since 2021年09月
 */
public class ThreadUtil {
    /**
     * 休眠指定时间,不返回异常
     */
    public static void safeSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void safeJoin(Thread thread) {
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
