package allst.utils.note;

import allst.utils.bean.CommNoteRespBean;
import allst.utils.bean.CommNoteSendBean;
import allst.utils.bean.VariNoteRespBean;
import allst.utils.bean.VariNoteSendBean;
import allst.utils.common.Constant;
import allst.utils.utils.NoteUtil;
import com.alibaba.fastjson.JSON;

/**
 * 短信发送类
 */
public class NoteSendUtils {
    /**
     * ************************************短信**************************
     * 普通短信发送
     * @param phone 手机号
     *              多手机号用逗号隔开
     * @param msg 消息内容
     * @return
     */
    public static void sendCommSms(String phone, String msg) {
        String commUrl = Constant.COMM_URL;
        String account = Constant.ACCOUNT;
        String password = Constant.PASSWORD;
        String report= "true";
        CommNoteSendBean commSendBean = new CommNoteSendBean(account, password, msg, phone, report);
        String requestJson = JSON.toJSONString(commSendBean);
        String resp = NoteUtil.sendSmsByPost(commUrl, requestJson);
        CommNoteRespBean commRespBean = JSON.parseObject(resp, CommNoteRespBean.class);
        if ("0".equals(commRespBean.getCode())) {
            // 消息发送成功
            System.out.println("消息发送成功");
        } else {
            // 消息发送失败时
            System.out.println("消息发送失败");
        }
    }

    /**
     * 变量短信发送
     * @param params 参数
     *                  "手机号1,变量,变量,变量...;手机号2,变量,变量..."  多手机号用分号隔开
     * @param msg 消息内容
     *					{$var}获取变量, 所有变量的获取方式一样
     * @return
     */
    public static void sendVariSms(String params, String msg) {
        String variUrl = Constant.VARI_URL;
        String account = Constant.ACCOUNT;
        String password = Constant.PASSWORD;
        String report = "true";
        VariNoteSendBean variSendBean = new VariNoteSendBean(account, password, msg, params, report);
        String requestJson = JSON.toJSONString(variSendBean);
        String resp = NoteUtil.sendSmsByPost(variUrl, requestJson);
        VariNoteRespBean variRespBean = JSON.parseObject(resp, VariNoteRespBean.class);
        if ("0".equals(variRespBean.getCode())) {
            System.out.println("变量消息发送成功");
        } else {
            System.out.println("变量消息发送失败");
        }
    }
}
