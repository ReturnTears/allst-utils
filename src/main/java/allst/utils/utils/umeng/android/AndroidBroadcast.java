package allst.utils.utils.umeng.android;

import allst.utils.utils.umeng.AndroidNotification;

/**
 * 广播发送
 */
public class AndroidBroadcast extends AndroidNotification {

    public AndroidBroadcast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "broadcast");
    }
}
