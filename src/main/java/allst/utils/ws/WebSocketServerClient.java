package allst.utils.ws;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(value = "/ws/{xx}", encoders = { ServerEncoder.class })
@Component
public class WebSocketServerClient {

	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
	private static int onlineCount = 0;

	//concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	private static CopyOnWriteArraySet<WebSocketServerClient> webSocketSet = new CopyOnWriteArraySet<WebSocketServerClient>();

	private static ConcurrentHashMap<String,Object> clients = new ConcurrentHashMap<String,Object>();

	//与某个客户端的连接会话，需要通过它来给客户端发送数据
	private Session session;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("token") String token) {
		this.session = session;
		if(clients.get(token) != null){
			Object obj = clients.get(token);
			webSocketSet.remove(obj);
		}
		webSocketSet.add(this);     //加入set中
		clients.put(token,this);
		addOnlineCount();           //在线数加1
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
		try {
			WsMessage msg = new WsMessage();
			Map<String,Object> map  = new HashMap<>();
			map.put("code",10000000);
			map.put("msg","连接成功");

			msg.setMessage(map);
			sendMessage(msg);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IO异常");
		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose(@PathParam("token") String token) {
		webSocketSet.remove(this);  //从set中删除
		//String userId = TokenUtil.getUserId(token);
		clients.remove(token);
		subOnlineCount();           //在线数减1
		System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
	}

	/**
	 * 收到客户端消息后调用的方法
	 *
	 * @param message 客户端发送过来的消息
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		WsMessage msg = new WsMessage();
		Map<String,Object> map  = new HashMap<>();
		map.put("data","收到回复！");
		msg.setMessage(map);
		try {
			sendMessage(msg);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 发生错误时调用
	 *
	 * @OnError
	 */
	public void onError(Session session, Throwable error) {
		System.out.println("发生错误");
		error.printStackTrace();
	}

	public void sendMessage(Object message) throws IOException, EncodeException {
		this.session.getBasicRemote().sendObject(message);
	}

	public static void sendMessage2All(Object message,String type) {
		Set<String> keys =clients.keySet();
		for (String  key: keys) {
			WebSocketServerClient item  = (WebSocketServerClient)clients.get(key);
			try {
				WsMessage msg = new WsMessage();
				Map<String,Object> map  = new HashMap<>();
				map.put("data",message);
				map.put("source",type);
				msg.setMessage(map);
				item.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketServerClient.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketServerClient.onlineCount--;
	}
}
