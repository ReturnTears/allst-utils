package allst.utils.ws;

import java.util.Map;

public class WsMessage {

    private static final long serialVersionUID = 1L;
    private Map<String, Object> message;

    public Map<String, Object> getMessage() {
        return message;
    }

    public void setMessage(Map<String, Object> message) {
        this.message = message;
    }
}
