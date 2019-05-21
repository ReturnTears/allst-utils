package allst.utils.utils.umeng.ios;

import allst.utils.utils.umeng.IOSNotification;

/**
 * 广播推送
 */
public class IOSBroadcast extends IOSNotification {
	public IOSBroadcast(String appkey,String appMasterSecret) throws Exception {
			setAppMasterSecret(appMasterSecret);
			setPredefinedKeyValue("appkey", appkey);
			this.setPredefinedKeyValue("type", "broadcast");	
		
	}
}
