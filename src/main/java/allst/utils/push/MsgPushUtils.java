package allst.utils.push;

import allst.utils.common.Constant;
import allst.utils.utils.umeng.AndroidNotification;
import allst.utils.utils.umeng.PushClient;
import allst.utils.utils.umeng.android.*;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static allst.utils.utils.DateTimeUtil.currDateTime;

/**
 * 消息推送类
 */
public class MsgPushUtils {

    PushClient client = new PushClient();

    /**
     * App Key
     */
    private static String appKey = Constant.UMENG_APPKEY;
    /**
     * App Master Secret
     */
    private static String appMasterSecret = Constant.UMENG_APP_MASTER_SECRET;
    /**
     * Tag Key
     */
    private static String tagKey = Constant.UMENG_TAG_KEY;
    /**
     * And Key
     */
    private static String andKey = Constant.UMENG_AND_KEY;
    /**
     * Where Key
     */
    private static String whereKey = Constant.UMENG_WHERE_KEY;
    /**
     * ticker 状态栏
     */
    private static String _ticker = Constant._TICKER;
    /**
     * title  标题
     */
    private static String _title = Constant._TITLE;

    /**
     * ***************************************消息推送***************************
     * 广播推送消息
     * ticker 		状态栏
     * title		标题
     * content		消息内容
     * @return
     */
    public void sendAndroidBroadcast(Map<String, Object> params) {
        String startTime = currDateTime("");
        try {
            AndroidBroadcast broadcast = new AndroidBroadcast(appKey, appMasterSecret);
            broadcast.setTicker(_ticker);
            broadcast.setTitle(_title);
            broadcast.setText("");
            broadcast.setStartTime(startTime);
            broadcast.goAppAfterOpen();
            broadcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            // 测试模式
            // broadcast.setTestMode();
            // 正式模式
            broadcast.setProductionMode();
            // 可以设置外部字段, 多个在后面继续添加即可
            broadcast.setExtraField("xxx", "");
            // 发送成功返回true
            boolean result = client.send(broadcast);
            System.out.println("send result : "+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义播推送消息
     * @param ticker		通知栏
     * @param title			标题
     * @param content		消息内容
     * @return				返回结果:推送成功true，推送失败false
     * @throws Exception
     */
    public void sendAndroidCustomizedcast(String ticker, String title, String content, String startTime) throws Exception {
        AndroidCustomizedcast customizedcast = new AndroidCustomizedcast(appKey, appMasterSecret);
        String starTime = currDateTime(startTime);
        customizedcast.setAlias("alias", "alias_type");
        customizedcast.setTicker(ticker);
        customizedcast.setTitle(title);
        customizedcast.setText(content);
        customizedcast.setStartTime(starTime);
        customizedcast.goAppAfterOpen();
        customizedcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // 测试模式
        // customizedcast.setTestMode();
        // 正式模式
        customizedcast.setProductionMode();
        // 可以设置外部字段, 多个在后面继续添加即可
        customizedcast.setExtraField("fieldName", "");
        boolean result = client.send(customizedcast);
        System.out.println("result : " + result);
    }

    /**
     * 文件播推送消息
     * ticker		通知栏
     * title		标题
     * content		消息内容
     * startTime 	开始时间	yyyy-MM-dd HH:mm:ss
     * @return
     */
    public void sendAndroidFilecast(Map<String, Object> params) {
        String startTime = currDateTime("");
        try {
            AndroidFilecast filecast = new AndroidFilecast(appKey, appMasterSecret);
            // 参数3存储的是device tokens, 多个device tokens使用'\n'分割
            String fileId = client.uploadContents(appKey, appMasterSecret, "");
            filecast.setFileId(fileId);
            filecast.setTicker(_ticker);
            filecast.setTitle(_title);
            filecast.setText("");
            filecast.setStartTime(startTime);
            filecast.goAppAfterOpen();
            filecast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            // 测试模式
            // filecast.setTestMode();
            // 正式模式
            filecast.setProductionMode();
            // 可以设置外部字段, 多个在后面继续添加即可, 字段名不一致即可
            filecast.setExtraField("fieldName", "");
            boolean result = client.send(filecast);
            System.out.println("result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 列播推送消息
     * @param params
     * 					参数
     * @return
     */
    public void sendAndroidListcast(Map<String, Object> params) {
        String startTime = currDateTime("");
        //long timestamp = System.currentTimeMillis();
        String smsMsg = (String) params.get("smsMsg");
        String deviceToken = (String) params.get("deviceToken");
        try {
            AndroidListcast listcast = new AndroidListcast(appKey, appMasterSecret);
            // 必填, 要求不超过500个, 以英文逗号分隔
            listcast.setDeviceToken(deviceToken);
            listcast.setTicker(_ticker);
            listcast.setTitle(_title);
            listcast.setText(smsMsg);
            //listcast.setTimeStamp(timestamp);
            listcast.setStartTime(startTime);
            listcast.goAppAfterOpen();
            listcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
            // 正式模式
            listcast.setProductionMode();
            // 测试模式
            // listcast.setTestMode();

            // 可以设置外部字段, 多个在后面继续添加即可, 字段名不一致即可
            listcast.setExtraField("fieldName", "xxx");
            boolean result = client.send(listcast);
            System.out.println("result : " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 组播推送消息
     * tags				过滤标签集	[{"tag":"test"}, {"tag":"Test"}]
     * ticker			通知栏
     * title			标题
     * content			消息内容
     * startTime		开始时间 yyyy-MM-dd HH:mm:ss
     * @return			返回结果:推送成功true，推送失败false
     */
    public void sendAndroidGroupcast(Map<String, Object> params) throws Exception {

        AndroidGroupcast groupcast = new AndroidGroupcast(appKey, appMasterSecret);
        String startTime = currDateTime("");
        List<String> tags = new ArrayList<>();

        // filter json => {"where": {"and": [ ] }
        JSONObject filterJson = createGroupFilter(tags);
        groupcast.setFilter(filterJson);
        groupcast.setTicker(_ticker);
        groupcast.setTitle(_title);
        groupcast.setText(params.get("smsMsg").toString());
        groupcast.setStartTime(startTime);
        groupcast.goAppAfterOpen();
        groupcast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // 测试模式
        // groupcast.setTestMode();
        // 正式模式
        groupcast.setProductionMode();
        // 外部字段
        groupcast.setExtraField("fieldName", "xxx");
        boolean result = client.send(groupcast);
        System.out.println("result : " + result);
    }

    /**
     * 构建组播filter json
     * @param tags
     * @return
     * @throws JSONException
     */
    private static JSONObject createGroupFilter(List<String> tags) throws JSONException {
        JSONObject filterJson = new JSONObject();
        JSONObject whereJson = new JSONObject();
        org.json.JSONArray tagArray = new org.json.JSONArray();
        for (String tag : tags) {
            JSONObject tagJson = new JSONObject();
            tagJson.put(tagKey, tag);
            tagArray.put(tagJson);
        }
        whereJson.put(andKey, tagArray);
        filterJson.put(whereKey, whereJson);
        return filterJson;
    }

    /**
     * 单播推送消息
     *  deviceToken		友盟后台对设备的唯一标识
     *  ticker			通知栏
     *  title			标题
     *  content			消息内容
     *  startTime		开始时间	yyyy-MM-dd HH:mm:ss
     * @return			返回结果:推送成功true，推送失败false
     */
    public void sendAndroidUnicast(Map<String, Object> params) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String deviceToken = (String) params.get("deviceToken");
        String smsMsg = (String) params.get("smsMsg");
        String starTime = currDateTime("");

        AndroidUnicast unicast = new AndroidUnicast(appKey, appMasterSecret);
        unicast.setDeviceToken(deviceToken);
        unicast.setTicker(_ticker);
        unicast.setTitle(_title);
        unicast.setText(smsMsg);
        unicast.setStartTime(starTime);
        unicast.goAppAfterOpen();
        unicast.setDisplayType(AndroidNotification.DisplayType.NOTIFICATION);
        // 测试模式
        // unicast.setTestMode();
        // 正式模式
        unicast.setProductionMode();
        // 设置外部字段
        unicast.setExtraField("fieldName", "xxx");
        boolean result = client.send(unicast);
        System.out.println("result : " + result);
    }
}
