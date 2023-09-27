package allst.utils.pay;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class TestZf {

    public static void main(String[] args) {

        //String url = "https://api.wangsir9.cn/qrcode.php";//支付地址
        String url = "https://api.wangsir9.cn/submit.php";
        String hdUrl = "https://api.wangsir9.cn";
        String pid = "1012";//商户id
        String type = "alipay";//支付类型
        String outTradeNo = "zf16124038831849";//商户单号
        String notifyUrl = "http://duozi.aaem.xyz/callBack/payBank";//异步通知
        String returnUrl = "https://www.baidu.com/";//跳转地址
        String name = "月卡";//商品名
        String money = "9.9";//价格
        String signType = "MD5";//签名类型
        String key = "OGbsCmcH5g1xEbbxGkCCrhgP8E5B2EP1";//商户密钥

        //参数存入 map
       /* Map<String,String> sign = new HashMap<>();
        sign.put("pid",pid);
        sign.put("type",type);
        sign.put("out_trade_no",outTradeNo);
        sign.put("notify_url",notifyUrl);
        sign.put("return_url",returnUrl);
        sign.put("name",name);
        sign.put("money",money);*/
        Map<String,String> sign = new HashMap<>();
        sign.put("pid","1012");
        sign.put("type","alipay");
        sign.put("out_trade_no","zf16113029348198");
        sign.put("trade_no","2021012216100078614");
        //*treeMap.put("notify_url",notifyUrl);
        //treeMap.put("return_url",returnUrl);//
        sign.put("name","半月卡");
        sign.put("money","9.90");
        sign.put("trade_status","TRADE_SUCCESS");
        //根据key升序排序
        sign = sortByKey(sign);

        String signStr = "";

        //遍历map 转成字符串
        for(Map.Entry<String,String> m :sign.entrySet()){
            signStr += m.getKey() + "=" +m.getValue()+"&";
        }

        //去掉最后一个 &
        signStr = signStr.substring(0,signStr.length()-1);
        String test = DigestUtils.md5DigestAsHex(signStr.getBytes());
        //最后拼接上KEY
        signStr += key;


        //转为MD5
        signStr = DigestUtils.md5DigestAsHex(signStr.getBytes());
        String d = "money=9.9&name=月卡&notify_url=http://81.68.111.99:8080/callBack/bankTest&out_trade_no=zf16113029348198&pid=1012&return_url=https://www.baidu.com/&type=alipay&key=OGbsCmcH5g1xEbbxGkCCrhgP8E5B2EP1";
        String test2 = DigestUtils.md5DigestAsHex(d.getBytes());
        // 测试MD5
        /*TreeMap<String,String> treeMap = new TreeMap<>();
        treeMap.put("pid","1012");
        treeMap.put("type","alipay");
        treeMap.put("out_trade_no","zf16113029348198");
        treeMap.put("trade_no","2021012216100078614");
        *//*treeMap.put("notify_url",notifyUrl);
        treeMap.put("return_url",returnUrl);*//*
        treeMap.put("name","月卡");
        treeMap.put("money","9.9");
        treeMap.put("trade_status","TRADE_SUCCESS");
        String md5 = StringUtil.signRequest(treeMap,null,"utf-8");*/

        sign.put("sign_type",signType);
        sign.put("sign",signStr);

        /******************* 开始业务 ************************************/
        String returnHtml = "";
        System.out.println("<form id='paying' action='"+url+"/submit.php' method='post'>");
        returnHtml = "<form id='paying' action='"+url+"/submit.php' method='post'>";
        for(Map.Entry<String,String> m :sign.entrySet()){
            System.out.println("<input type='hidden' name='"+m.getKey()+"' value='"+m.getValue()+"'/>");
            returnHtml += "<input type='hidden' name='"+m.getKey()+"' value='"+m.getValue()+"'/>";
        }
        System.out.println("<input type='submit' value='正在跳转'>");
        returnHtml += "<input type='submit' value='正在跳转'>";
        System.out.println("</form>");
        returnHtml += "</form>";
        System.out.println("<script>document.forms['paying'].submit();</script>");
        returnHtml += "<script>document.forms['paying'].submit();</script>";
        System.out.println(returnHtml);

        /***************** 开始业务2 ************************************/
        String param = "";
        // 组合请求参数
        param =
        "pid=1012"+"&type=alipay&out_trade_no=zf16124038831849&notify_url="+notifyUrl +
                "&return_url="+returnUrl+"&name=半月卡&money=9.90&sign=759be6620c24f3d409c912127c2628d2&sign_type=MD5";
        System.out.println("***************************"+param);
        String newUrl = HttpRequest.sendPost(url,param);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+newUrl);
       String script = subString(newUrl,"<script>","</script>");
        System.out.println("######################"+script);

        String end =  subString(script,"='","';");
        System.out.println(hdUrl+end);
    }



    public static <K extends Comparable<? super K>, V > Map<K, V> sortByKey(Map<K, V> map) {
        Map<K, V> result = new LinkedHashMap<>();

        map.entrySet().stream()
                .sorted(Map.Entry.<K, V>comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }

    public static String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }


}