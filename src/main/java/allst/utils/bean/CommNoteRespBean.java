package allst.utils.bean;

/**
 * 普通短信发送响应实体类
 */
public class CommNoteRespBean {
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
     * 状态码（详细参考提交响应状态码）
     */
    private String code;
    /**
     * 消息发送失败条数(多手机号发送时用)
     */
    private Integer failNum;
    /**
     * 消息发送成功条数(多手机号发送时用)
     */
    private Integer successNum;

    public CommNoteRespBean() {
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    @Override
    public String toString() {
        return "CommNoteRespBean{" +
                "time='" + time + '\'' +
                ", msgId='" + msgId + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", code='" + code + '\'' +
                ", failNum=" + failNum +
                ", successNum=" + successNum +
                '}';
    }
}
