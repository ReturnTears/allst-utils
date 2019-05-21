package allst.utils.ws;

import com.alibaba.fastjson.JSON;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class ServerEncoder implements Encoder.Text<WsMessage> {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(EndpointConfig arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public String encode(WsMessage message) {
        try {
            String json = JSON.toJSONString(message.getMessage(), false);
            System.out.println(json);
            return json;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
