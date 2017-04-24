package csust.student.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.sina.weibo.sdk.openapi.models.User;

import csust.student.adapter.MessageAdapter;
import csust.student.database.MessageDB;
import csust.student.database.RecentDB;
import csust.student.database.UserDB;
import csust.student.info.CourseInfo;
import csust.student.info.MessageItem;
import csust.student.info.RecentItem;
import csust.student.myview.MsgListView;
import csust.student.myview.MsgListView.IXListViewListener;
import csust.student.utils.HomeWatcher;
import csust.student.utils.HomeWatcher.OnHomePressedListener;
import csust.student.utils.L;
import csust.student.utils.SharePreferenceUtil;
import csust.student.utils.T;

/**
 * 
 * @desc: 聊天界面主Activity /**
 * @desc:相册图片列表
 * @date: 2015年7月3日 下午4:40:54 QQ2050542273
 * @email:15162925211@163.com
 */

public class ChatActivity extends Activity implements OnClickListener, OnTouchListener, IXListViewListener,
		OnHomePressedListener {

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	public static int MSGPAGERNUM;
	private static final int POLL_INTERVAL = 300;
	private static final int CAMERA_WITH_DATA = 10;

	public static String DEFAULT_ID = "1100877319654414526";
	public static String defaulgUserName = "在飞";
	public static String defaulgIcon = "4";
	public static int defaultCount = 0;

	private boolean isFaceShow = false;
	private InputMethodManager mInputMethodManager;
	private EditText mEtMsg;

	private SignStudentApp mApplication;

	private Button mBtnSend;// 发送消息按钮
	private static MessageAdapter adapter;// 发送消息展示的adapter
	private MsgListView mMsgListView;// 展示消息的
	private MessageDB mMsgDB;// 保存消息的数据库
	private RecentDB mRecentDB;
	//private Gson mGson;
	private WindowManager.LayoutParams mParams;

	private HomeWatcher mHomeWatcher;// home键

	// 接受数据
	private UserDB mUserDB;
//	private SendMsgAsyncTask mSendTask;
	
	//用于接收上个界面传来的teacherid
	private String teacherId;

	/**
	 * 接收到数据，用来更新listView
	 */
	private Handler handler = new Handler() {
		// 接收到消息
		public void handleMessage(android.os.Message msg) {
			if (msg.what == NEW_MESSAGE) {
				// String message = (String) msg.obj;
				csust.student.info.Message msgItem = (csust.student.info.Message) msg.obj;
				String userId = msgItem.getUser_id();
				if (!userId.equals(teacherId))// 如果不是当前正在聊天对象的消息，不处理
					return;

				int headId = msgItem.getHead_id();

				MessageItem item = null;
				RecentItem recentItem = null;
				if (msgItem.getMessagetype() == MessageItem.MESSAGE_TYPE_TEXT) {
					item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
							msgItem.getNick(), System.currentTimeMillis(),
							msgItem.getMessage(), headId, true, 0,
							msgItem.getVoiceTime());
					recentItem = new RecentItem(MessageItem.MESSAGE_TYPE_TEXT,
							userId, headId, msgItem.getNick(),
							msgItem.getMessage(), 0,
							System.currentTimeMillis(), msgItem.getVoiceTime());

				}

				adapter.upDateMsg(item);// 更新界面
				mMsgDB.saveMsg(msgItem.getUser_id(), item);// 保存数据库
				mRecentDB.saveRecent(recentItem);

				scrollToBottomListItem();

			}
		}

	};

	/**
	 * @Description 滑动到列表底部
	 */
	private void scrollToBottomListItem() {

		// todo eric, why use the last one index + 2 can real scroll to the
		// bottom?
		if (mMsgListView != null) {
			mMsgListView.setSelection(adapter.getCount() + 1);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		mApplication = SignStudentApp.getInstance();
		mParams = getWindow().getAttributes();

		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		//mSpUtil = PushApplication.getInstance().getSpUtil();

		MSGPAGERNUM = 0;

		initView();

//		mApplication.getNotificationManager().cancel(
//				PushMessageReceiver.NOTIFY_ID);
//		PushMessageReceiver.mNewNum = 0;
//
//		mUserDB = mApplication.getUserDB();
//
//		// 启动百度推送服务
//		PushManager.startWork(getApplicationContext(),
//				PushConstants.LOGIN_TYPE_API_KEY, PushApplication.API_KEY);// 无baidu帐号登录,以apiKey随机获取一个id

		// 设置表情翻页效果
		// mSpUtil.setFaceEffect(8);

		
		Intent intent = this.getIntent();
		// 必须要这个才能，获得名字value
		Bundle bundle = intent.getBundleExtra("value");

		teacherId = bundle.getSerializable("teacherId").toString();
		initUserInfo();

	}

	/**
	 * 初始化用户信息
	 */
	private void initUserInfo() {

	}

	private void initView() {

		// 相册

		mEtMsg = (EditText) findViewById(R.id.msg_et);

		mBtnSend = (Button) findViewById(R.id.send_btn);
		mBtnSend.setClickable(true);
		mBtnSend.setEnabled(false);
		mBtnSend.setOnClickListener(this);

		// 消息
//		mApplication = PushApplication.getInstance();
		mMsgDB = mApplication.getMessageDB();// 发送数据库
		mRecentDB = mApplication.getRecentDB();// 接收消息数据库
//		mGson = mApplication.getGson();

		adapter = new MessageAdapter(this, initMsgData());
		mMsgListView = (MsgListView) findViewById(R.id.msg_listView);
		// 触摸ListView隐藏表情和输入法
		mMsgListView.setOnTouchListener(this);
		mMsgListView.setPullLoadEnable(false);
		mMsgListView.setXListViewListener(this);
		mMsgListView.setAdapter(adapter);
		mMsgListView.setSelection(adapter.getCount() - 1);

		// mTitleRightBtn.setOnClickListener(this);
		mEtMsgOnKeyListener();

	}

	/**
	 * 输入框key监听事件
	 */
	private void mEtMsgOnKeyListener() {
		mEtMsg.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mParams.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
							|| isFaceShow) {

						isFaceShow = false;
						// imm.showSoftInput(msgEt, 0);
						return true;
					}
				}
				return false;
			}
		});
		mEtMsg.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					mBtnSend.setEnabled(true);
				} else {
					mBtnSend.setEnabled(false);
				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
		//PushMessageReceiver.ehList.add(this);// 监听推送的消息

	}

	@Override
	protected void onPause() {
		mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);

		isFaceShow = false;
		super.onPause();
		mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
		//PushMessageReceiver.ehList.remove(this);// 移除监听
	}

	public static MessageAdapter getMessageAdapter() {
		return adapter;
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<MessageItem> initMsgData() {
		List<MessageItem> list = mMsgDB
				.getMsg(teacherId, MSGPAGERNUM);
		List<MessageItem> msgList = new ArrayList<MessageItem>();// 消息对象数组
		if (list.size() > 0) {
			for (MessageItem entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(defaulgUserName);
				}
				if (entity.getHeadImg() < 0) {
					entity.setHeadImg(defaultCount);
				}
				msgList.add(entity);
			}
		}
		return msgList;

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.send_btn: {
			// 发送消息
			String msg = mEtMsg.getText().toString();
			MessageItem item = new MessageItem(MessageItem.MESSAGE_TYPE_TEXT,
					"zhangsan1", System.currentTimeMillis(), msg,
					0, false, 0, 0);
			adapter.upDateMsg(item);
			mMsgListView.setSelection(adapter.getCount() - 1);
			mMsgDB.saveMsg(teacherId, item);// 消息保存数据库
			mEtMsg.setText("");
			// ===发送消息到服务器
			csust.student.info.Message msgItem = new csust.student.info.Message(
					MessageItem.MESSAGE_TYPE_TEXT, System.currentTimeMillis(),
					msg, "", 0);
			if ("".equals(teacherId)) {
				T.show(ChatActivity.this,
						"百度push id为空，不能发送消息,请到百度开发者官网生成新的push key，替换", 1);
				return;
			}
//			new SendMsgAsyncTask(mGson.toJson(msgItem), mSpUtil.getUserId())
//					.send();// push发送消息到服务器
			// ===保存近期的消息

			RecentItem recentItem = new RecentItem(
					MessageItem.MESSAGE_TYPE_TEXT, teacherId,
					defaultCount, defaulgUserName, msg, 0,
					System.currentTimeMillis(), 0);
			mRecentDB.saveRecent(recentItem);
			break;
		}

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("fff", "结果:" + resultCode);
		if (RESULT_OK != resultCode) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:
			break;

		default:
			break;
		}

	}

	// 防止乱pageview乱滚动
	private OnTouchListener forbidenScroll() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					return true;
				}
				return false;
			}
		};
	}

//	@Override
//	public void onMessage(Message message) {
//		// 接收到消息更新界面
//		android.os.Message handlerMsg = handler.obtainMessage(NEW_MESSAGE);
//		handlerMsg.obj = message;
//		handler.sendMessage(handlerMsg);
//
//	}

//	@Override
//	public void onBind(String method, int errorCode, String content) {
//		if (errorCode == 0) {// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
//			User u = new User(mSpUtil.getUserId(), mSpUtil.getChannelId(),
//					mSpUtil.getNick(), mSpUtil.getHeadIcon(), 0);
//			mUserDB.addUser(u);// 把自己添加到数据库
//			// com.way.bean.Message msgItem = new com.way.bean.Message(
//			// System.currentTimeMillis(), " ", mSpUtil.getTag());
//			// new SendMsgAsyncTask(mGson.toJson(msgItem), "").send();;
//		}
//
//	}

//	@Override
//	public void onNotify(String title, String content) {
//
//	}
//
//	@Override
//	public void onNetChange(boolean isNetConnected) {
//		if (!isNetConnected)
//			T.showShort(this, "网络连接已断开");
//
//	}

//	@Override
//	public void onNewFriend(User u) {
//
//	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.msg_listView:
			mInputMethodManager.hideSoftInputFromWindow(
					mEtMsg.getWindowToken(), 0);
			isFaceShow = false;
			break;
		case R.id.msg_et:
			mInputMethodManager.showSoftInput(mEtMsg, 0);
			isFaceShow = false;
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void onRefresh() {
		MSGPAGERNUM++;
		List<MessageItem> msgList = initMsgData();
		int position = adapter.getCount();
		adapter.setmMsgList(msgList);
		mMsgListView.stopRefresh();
		mMsgListView.setSelection(adapter.getCount() - position - 1);
		L.i("MsgPagerNum = " + MSGPAGERNUM + ", adapter.getCount() = "
				+ adapter.getCount());
	}

	@Override
	public void onLoadMore() {

	}

	@Override
	public void onHomePressed() {
		mApplication.showNotification();
	}

	@Override
	public void onHomeLongPressed() {

	}

}
