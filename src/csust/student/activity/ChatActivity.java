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
import android.widget.Toast;
import csust.student.adapter.MessageAdapter;
import csust.student.application.SignStudentApp;
import csust.student.database.MessageDB;
import csust.student.database.RecentDB;
import csust.student.info.ChatMessage;
import csust.student.info.RecentItem;
import csust.student.model.Model;
import csust.student.myview.MsgListView;
import csust.student.myview.MsgListView.IXListViewListener;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpGetThread;
import csust.student.utils.HomeWatcher;
import csust.student.utils.HomeWatcher.OnHomePressedListener;
import csust.student.utils.L;

/**
 * 聊天的主界面
 * 
 * @author anLA7856
 *
 */
public class ChatActivity extends Activity implements OnClickListener,
		OnTouchListener, IXListViewListener, OnHomePressedListener {

	public static final int NEW_MESSAGE = 0x001;// 收到消息
	public static int MSGPAGERNUM;
	private static final int CAMERA_WITH_DATA = 10;

	public static String defaulgUserName = "anla7856";
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
	private WindowManager.LayoutParams mParams;

	private HomeWatcher mHomeWatcher;// home键

	// 用于接收上个界面传来的teacherid
	private String teacherId;

	public static MessageAdapter getAdapter() {
		return adapter;
	}

	public String getTeacherId() {
		return teacherId;
	}

	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_main);
		mApplication = SignStudentApp.getInstance();
		mParams = getWindow().getAttributes();
		Intent intent = this.getIntent();
		// 必须要这个才能，获得名字value
		Bundle bundle = intent.getBundleExtra("value");

		teacherId = bundle.getSerializable("teacherId").toString();
		mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		// mSpUtil = PushApplication.getInstance().getSpUtil();

		MSGPAGERNUM = 0;
		// 进入的时候保存句柄。
		Model.MYCHATACTIVITY = this;

		initView();

		initUserInfo();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出的时候，将句柄清空
		Model.MYCHATACTIVITY = null;
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
		mMsgDB = mApplication.getMessageDB();// 发送数据库
		mRecentDB = mApplication.getRecentDB();// 接收消息数据库
		// mGson = mApplication.getGson();

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
		// PushMessageReceiver.ehList.add(this);// 监听推送的消息

	}

	@Override
	protected void onPause() {
		mInputMethodManager.hideSoftInputFromWindow(mEtMsg.getWindowToken(), 0);

		isFaceShow = false;
		super.onPause();
		mHomeWatcher.setOnHomePressedListener(null);
		mHomeWatcher.stopWatch();
		// PushMessageReceiver.ehList.remove(this);// 移除监听
	}

	public static MessageAdapter getMessageAdapter() {
		return adapter;
	}

	/**
	 * 加载消息历史，从数据库中读出
	 */
	private List<ChatMessage> initMsgData() {
		List<ChatMessage> list = mMsgDB.getMsg(Model.MYUSERINFO.getStudent_id()
				+ "", teacherId, MSGPAGERNUM);
		List<ChatMessage> msgList = new ArrayList<ChatMessage>();// 消息对象数组
		if (list.size() > 0) {
			for (ChatMessage entity : list) {
				if (entity.getName().equals("")) {
					entity.setName(defaulgUserName);
				}
				if (entity.getHeadPic().equals("")) {
					entity.setHeadPic(defaultCount + "");
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
			ChatMessage item = new ChatMessage(0,
					Model.MYUSERINFO.getStudent_id(),
					Integer.parseInt(teacherId), msg,
					System.currentTimeMillis() + "", 1, 2 + "",
					Model.MYUSERINFO.getStudent_name(), "");

			adapter.upDateMsg(item);
			mMsgListView.setSelection(adapter.getCount() - 1);
			mMsgDB.saveMsg(Model.MYUSERINFO.getStudent_id() + "", item);// 消息保存数据库
			mEtMsg.setText("");

			// 发送信息。通过线程池。

			String url = Model.STUCHATMESSAGEADD + "studentId="
					+ Model.MYUSERINFO.getStudent_id() + "&teacherId="
					+ teacherId + "&message=" + msg;

			ThreadPoolUtils.execute(new HttpGetThread(hand2, url));

			RecentItem recentItem = new RecentItem(1, teacherId, defaultCount,
					defaulgUserName, msg, 0, System.currentTimeMillis(), 0);
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
		List<ChatMessage> msgList = initMsgData();
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

	Handler hand2 = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ChatActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(ChatActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				// 正常的处理逻辑。
				String result = (String) msg.obj;
				if (result.equals("1")) {
					// 发送成功界面
					Toast.makeText(ChatActivity.this, "发送成功", 1).show();
				} else {
					// 发送失败的逻辑

					Toast.makeText(ChatActivity.this, "发送失败", 1).show();
				}

			}
		};
	};

}
