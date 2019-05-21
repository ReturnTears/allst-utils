package allst.utils.utils.umeng.android;

import allst.utils.utils.umeng.AndroidNotification;

/**
 * 单播发送
 */
public class AndroidUnicast extends AndroidNotification {

    public AndroidUnicast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "unicast");
    }

    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }
}
