package allst.utils.bean;

/**
 * 变量短信发送响应实体类
 */
public class VariNoteRespBean {
    /**
     * 响应时间
     */
    private String time;
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 状态码说明（成功返回空）
     */
    private String errorMsg;
    /**
     * 失败的个数
     */
    private Integer failNum;
    /**
     * 成功的个数
     */
    private Integer successNum;
    /**
     * 状态码（详细参考提交响应状态码）
     */
    private String code;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getFailNum() {
        return failNum;
    }

    public void setFailNum(Integer failNum) {
        this.failNum = failNum;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "SmsVariRespBean{" +
                "time='" + time + '\'' +
                ", msgId='" + msgId + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", failNum='" + failNum + '\'' +
                ", successNum='" + successNum + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
