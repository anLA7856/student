package csust.student.service;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import csust.student.application.SignStudentApp;
import csust.student.database.MessageDB;
import csust.student.info.ChatMessage;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;
import csust.student.utils.NetUtil;

/**
 * 用于在后台服务中，获取新的聊天消息
 * 
 * @author anLA7856
 *
 */
public class ReceiveNewMessageService extends Service {

	private volatile boolean isRun = false;
	private String url = null;
	private MyJson myJson = new MyJson();

	private SignStudentApp mApplication;

	private MessageDB mMsgDB;// 保存消息的数据库

	private Handler hand = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 100) {
				Log.d("tt", "Receiveservice 连接失败");
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// 请求后的处理界面。，主要是将结果插入到数据库，如果有结果是当前用户正在聊天的，则要进行实时的界面更新操作。
				List<ChatMessage> list = myJson.getChatMessageList(result);
				for (int i = 0; i < list.size(); i++) {
					ChatMessage cm = list.get(i);
					if (Model.MYCHATACTIVITY == null) {

					} else {
						if (cm.getSenderId() == Integer
								.parseInt(Model.MYCHATACTIVITY.getTeacherId()
										.toString())
								&& cm.getReceiveId() == Model.MYUSERINFO
										.getStudent_id()) {
							// 说明是当前对话窗口的，从大管家那里获得引用，并更新界面,不确定是否可行
							Model.MYCHATACTIVITY.getAdapter().upDateMsg(cm);
						}
					}

					// 存到数据库
					mMsgDB.saveMsg(Model.MYUSERINFO.getStudent_id() + "", cm);
				}

			}
			// 改变标志位
			isRun = false;
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = SignStudentApp.getInstance();
		mMsgDB = mApplication.getMessageDB();// 发送数据库
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// Log.d("service", "I am a service");
					// 一旦启动，就一直轮询下去。，这里就不适用sleep来进行睡眠操作，因为可能一次和服务器通讯，就需要点时间。
					if (!isRun) {
						// 说明一次轮询已经完成，此时可以进行下一次轮询操作。
						if (NetUtil.isNetConnected(getApplicationContext()) == false) {
							// 网络不通畅，下一次；
							continue;
						}
						if (Model.MYUSERINFO == null) {
							continue;
						}
						url = Model.STUGETNEWCHATMESSAGE + "studentId="
								+ Model.MYUSERINFO.getStudent_id();
						Log.i("service", url);
						ThreadPoolUtils.execute(new HttpGetThread(hand, url));
						isRun = true;
					} else {
						// 过了这一次，不轮询
						continue;
					}
				}

			}
		}).start();

	}
}
