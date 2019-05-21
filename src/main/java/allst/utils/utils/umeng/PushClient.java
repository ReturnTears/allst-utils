package allst.utils.utils.umeng;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * push client
 *
 */
public class PushClient {

    private static final Log log = LogFactory.getLog(PushClient.class);

    protected final String USER_AGENT = "Mozilla/5.0";

    HttpClient client = new DefaultHttpClient();

    protected static final String host = "http://msg.umeng.com";

    protected static final String uploadPath = "/upload";

    protected static final String postPath = "/api/send";

    public boolean send(AbstractUmengNotification msg) throws Exception {
        String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
        msg.setPredefinedKeyValue("timestamp", timestamp);
        String url = host + postPath;
        String postBody = msg.getPostBody();
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + msg.getAppMasterSecret()).getBytes("utf8"));
        url = url + "?sign=" + sign;
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        // Send the post request and get the response
        HttpResponse response = client.execute(post);
        int status = response.getStatusLine().getStatusCode();
        // System.out.println("Response Code : " + status);
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        // System.out.println(result.toString());
        if (status == 200) {
             // System.out.println("Notification send successfully.");
            return true;
        } else {
             // System.out.println("Failed to send the notification!");
            log.error("Failed to send the notification!");
            return false;
        }
    }

    /**
     * 友盟自定义标签的操作
     * @param appkey            应用标识
     * @param appMasterSecret   密钥
     * @param deviceToken       设备标识
     * @param operate           对标签的操作
     * @param tag               标签
     * @throws Exception
     */
    public void operateTag(String appkey, String appMasterSecret, String deviceToken, String operate, String tag) throws Exception {
        // Construct the json string
        JSONObject addTag = new JSONObject();
        String timestamp = Integer.toString((int) (System.currentTimeMillis() / 1000));
        addTag.put("appkey", appkey);
        addTag.put("timestamp", timestamp);
        addTag.put("device_tokens", deviceToken);
        String url = null;
        // clear 和 list 接口不需要tag参数
        if (tag != null) {
            addTag.put("tag", tag);
        }
        url = host + "/api/tag/" + operate;
        String postBody = addTag.toString();
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + appMasterSecret).getBytes("utf8"));
        url = url + "?sign=" + sign;
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);
        // Send the post request and get the response
        HttpResponse response = client.execute(post);
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        // System.out.println(result.toString());
    }

    /**
     * 使用device_token从umeng下载文件
     * @param appkey    appkey
     * @param appMasterSecret   appMasterSecret
     * @param device_token  内容
     * @return
     * @throws Exception
     */
    public String uploadContents(String appkey,String appMasterSecret,String device_token) throws Exception {
        JSONObject uploadJson = new JSONObject();
        uploadJson.put("appkey", appkey);
        String timestamp = Integer.toString((int)(System.currentTimeMillis() / 1000));
        uploadJson.put("timestamp", timestamp);
        uploadJson.put("content", device_token);

        String url = host + uploadPath;
        String postBody = uploadJson.toString();
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + appMasterSecret).getBytes("utf8"));
        url = url + "?sign=" + sign;
        HttpPost post = new HttpPost(url);
        post.setHeader("User-Agent", USER_AGENT);
        StringEntity se = new StringEntity(postBody, "UTF-8");
        post.setEntity(se);

        HttpResponse response = client.execute(post);
        // System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
         System.out.println(result.toString());
        JSONObject respJson = new JSONObject(result.toString());
        String ret = respJson.getString("ret");
        if (!ret.equals("SUCCESS")) {
            throw new Exception("Failed to upload file");
        }
        JSONObject data = respJson.getJSONObject("data");
        String fileId = data.getString("file_id");

        return fileId;
    }
}
