package allst.utils.utils.umeng.android;


import allst.utils.utils.umeng.AndroidNotification;

/**
 * 文件播发送
 */
public class AndroidFilecast extends AndroidNotification {
    public AndroidFilecast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "filecast");
    }

    public void setFileId(String fileId) throws Exception {
        setPredefinedKeyValue("file_id", fileId);
    }
}
