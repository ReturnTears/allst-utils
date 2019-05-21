package allst.utils.utils.umeng.android;


import allst.utils.utils.umeng.AndroidNotification;

/**
 * 列播推送
 */
public class AndroidListcast extends AndroidNotification {

    public AndroidListcast(String appkey, String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "listcast");
    }

    /**
     * 当type=unicast时, 必填, 表示指定的单个设备,
     * 当type=listcast时, 必填, 要求不超过500个, 以英文逗号分隔
     * @param tokens
     * @throws Exception
     */
    public void setDeviceToken(String tokens) throws Exception {
        setPredefinedKeyValue("device_tokens", tokens);
    }
}
