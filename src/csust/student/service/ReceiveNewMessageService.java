package csust.student.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import csust.student.info.ChatMessage;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;
import csust.student.utils.NetUtil;

public class ReceiveNewMessageService extends Service{

	
	private volatile boolean isRun = false;
	private String url = Model.STUGETNEWCHATMESSAGE+"studentId="+Model.MYUSERINFO.getStudent_id();
	private MyJson myJson = new MyJson();
	
	
	private Handler hand = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 100) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 200) {
				String result = msg.obj.toString();
				//请求后的处理界面。，主要是将结果插入到数据库，如果有结果是当前用户正在聊天的，则要进行实时的界面更新操作。
				List<ChatMessage> list = myJson.getChatMessageList(result);
				ww
			}
		};
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		while(true){
			//一旦启动，就一直轮询下去。，这里就不适用sleep来进行睡眠操作，因为可能一次和服务器通讯，就需要点时间。
			if(!isRun){
				//说明一次轮询已经完成，此时可以进行下一次轮询操作。
				if(NetUtil.isNetConnected(getApplicationContext()) == false){
					//网络不通畅，下一次；
					continue;
				}
				ThreadPoolUtils.execute(new HttpGetThread(hand, url));
			}else{
				//过了这一次，不轮询
				continue;
			}
		}
	}
}
