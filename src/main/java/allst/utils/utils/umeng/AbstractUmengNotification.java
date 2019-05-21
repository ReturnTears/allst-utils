package allst.utils.utils.umeng;

import org.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Arrays;
import java.util.HashSet;

/**
 *
 */
public abstract class AbstractUmengNotification {

    private static final Log log = LogFactory.getLog(AbstractUmengNotification.class);

    protected final JSONObject rootJson = new JSONObject();

    protected String appMasterSecret;

    protected static final HashSet<String> ROOT_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "appkey", "timestamp", "type", "device_tokens", "alias", "alias_type", "file_id",
            "filter", "production_mode", "feedback", "description", "thirdparty_id"}));

    protected static final HashSet<String> POLICY_KEYS = new HashSet<>(Arrays.asList(new String[]{
            "start_time", "expire_time", "max_send_num"}));

    public abstract boolean setPredefinedKeyValue(String key, Object value) throws Exception;

    public void setAppMasterSecret(String secret) {
        appMasterSecret = secret;
    }

    public String getPostBody(){
        return rootJson.toString();
    }

    protected final String getAppMasterSecret(){
        return appMasterSecret;
    }

    protected void setProductionMode(Boolean prod) throws Exception {
        setPredefinedKeyValue("production_mode", prod.toString());
    }

    // 正式模式
    public void setProductionMode() throws Exception {
        setProductionMode(true);
    }

    // 测试模式
    public void setTestMode() throws Exception {
        setProductionMode(false);
    }

    // 发送消息描述，建议填写。
    public void setDescription(String description) throws Exception {
        setPredefinedKeyValue("description", description);
    }

    // 定时发送时间，若不填写表示立即发送。格式: "YYYY-MM-DD hh:mm:ss"。
    public void setStartTime(String startTime) throws Exception {
        setPredefinedKeyValue("start_time", startTime);
    }
    // 消息过期时间,格式: "yyyy-MM-dd hh:mm:ss"。
    public void setExpireTime(String expireTime) throws Exception {
        setPredefinedKeyValue("expire_time", expireTime);
    }
    // 发送限速，每秒发送的最大条数。
    public void setMaxSendNum(Integer num) throws Exception {
        setPredefinedKeyValue("max_send_num", num);
    }
}
