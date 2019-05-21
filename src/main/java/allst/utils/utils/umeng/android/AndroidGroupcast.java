package allst.utils.utils.umeng.android;

import allst.utils.utils.umeng.AndroidNotification;
import com.alibaba.fastjson.JSONObject;

/**
 * 组播发送
 */
public class AndroidGroupcast extends AndroidNotification {

    public AndroidGroupcast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "groupcast");
    }

    public void setFilter(JSONObject filter) throws Exception {
        setPredefinedKeyValue("filter", filter);
    }
}
