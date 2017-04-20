package csust.student.activity;

import java.util.List;

import csust.student.info.UserInfo;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpPostThread;
import csust.student.utils.MyJson;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ActionMode;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {

	private ImageView mClose;
	private RelativeLayout mWeibo, mQQ;
	private Button mLogin;
	
	private EditText mName, mPassword;
	private TextView mRegister;
	private String NameValue = null;
	private String PasswordValue = null;
	private String url = null;
	private String value = null;
	private MyJson myJson = new MyJson();

	// 定义进度匡
	private ProgressDialog mProDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		initView();
	}

	private void initView() {
		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mClose = (ImageView) findViewById(R.id.loginClose);
		//mLogin = (RelativeLayout) findViewById(R.id.login);
		mLogin = (Button) findViewById(R.id.Ledit_login);
		
		mWeibo = (RelativeLayout) findViewById(R.id.button_weibo);
		mQQ = (RelativeLayout) findViewById(R.id.buton_qq);
		mName = (EditText) findViewById(R.id.Ledit_name);
		
		mPassword = (EditText) findViewById(R.id.Ledit_password);
		mRegister = (TextView) findViewById(R.id.register);
		mClose.setOnClickListener(this);
		mLogin.setOnClickListener(this);
		mWeibo.setOnClickListener(this);
		mQQ.setOnClickListener(this);
		mRegister.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		int mId = v.getId();
		switch (mId) {
		case R.id.loginClose:
			//finish();
			break;
		case R.id.Ledit_login:
			NameValue = mName.getText().toString();
			PasswordValue = mPassword.getText().toString();
			Log.e("qianpengyu", "NameValue" + NameValue + "  PasswordValue"
					+ PasswordValue);
			if (NameValue.equalsIgnoreCase(null)
					|| PasswordValue.equalsIgnoreCase(null)
					|| NameValue.equals("") || PasswordValue.equals("")) {
				Toast.makeText(LoginActivity.this, "用户名密码不为空", 1).show();
				return;
			} else if (!PasswordValue.matches("[a-zA-Z0-9]{6,12}")) {
				Toast.makeText(LoginActivity.this, "密码错误", 1).show();
				return;
			} else if (!NameValue.matches("[a-zA-Z0-9]{5,12}")) {
				Toast.makeText(LoginActivity.this, "用户名错误", 1)
						.show();
				return;
			} else {
				// 登录接口
				mProDialog.setTitle("登录中。。。");
				mProDialog.show();
				login();
			}
			break;
		case R.id.button_weibo:
			Toast.makeText(LoginActivity.this, "(暂时无法使用)正在与Sina公司沟通中...", 1)
					.show();
			break;
		case R.id.buton_qq:
			Toast.makeText(LoginActivity.this, "(暂时无法使用)正在与Tencent公司沟通中...", 1)
					.show();
			break;
		case R.id.register:
			Intent intent = new Intent(LoginActivity.this,
					RegistetActivity.class);
			// startActivity(intent);
			startActivityForResult(intent, 1);

		}
	}

	private void login() {
		url = Model.LOGIN;
		value = "{\"uname\":\"" + NameValue + "\",\"upassword\":\""
				+ PasswordValue + "\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 进度匡消失
			mProDialog.dismiss();

			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(LoginActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(LoginActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				// Log.e("loginInfo", result);
				if (result.equalsIgnoreCase("NOUSER")) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "用户名不存在", 1).show();
					return;
				} else if (result.equalsIgnoreCase("NOPASS")) {
					mPassword.setText("");
					Toast.makeText(LoginActivity.this, "密码错误", 1).show();
					return;
				} else if (result != null) {
					Toast.makeText(LoginActivity.this, "登录成功", 1).show();
					List<UserInfo> newList = myJson.getUserInfoList(result);
					if (newList != null) {
						Model.MYUSERINFO = newList.get(0);
					}
					Intent intent = new Intent(LoginActivity.this,
							UserInfoActivity.class);
					Bundle bund = new Bundle();
					bund.putSerializable("UserInfo", Model.MYUSERINFO);
					intent.putExtra("value", bund);
					startActivity(intent);
					SharedPreferences sp = LoginActivity.this
							.getSharedPreferences("UserInfo", MODE_PRIVATE);
					Log.e("SharedPreferencesOld",
							sp.getString("UserInfoJson", "none"));
					SharedPreferences.Editor mSettinsEd = sp.edit();
					mSettinsEd.putString("UserInfoJson", result);
					// 提交保存
					mSettinsEd.commit();
					// 设置个人资料"NICKNAME"
					// KFIMInterfaces.setVCardField(LoginActivity.this,
					// "NICKNAME", Model.MYUSERINFO.getUname());
					Log.e("SharedPreferencesNew",
							sp.getString("UserInfoJson", "none"));
					finish();
				}
			}
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("111", "111");
		if (requestCode == 1 && resultCode == 2 && data != null) {
			NameValue = data.getStringExtra("NameValue");
			mName.setText(NameValue);
		}

	};

	@Override
	protected void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		// intentFilter.addAction(KFMainService.ACTION_XMPP_CONNECTION_CHANGED);
		registerReceiver(mXmppreceiver, intentFilter);
	};

	@Override
	protected void onStop() {
		super.onStop();

		// KFSLog.d("onStop");
		unregisterReceiver(mXmppreceiver);
	}

	private BroadcastReceiver mXmppreceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// if (action.equals(KFMainService.ACTION_XMPP_CONNECTION_CHANGED))
			// {
			// updateStatus(intent.getIntExtra("new_state", 0));
			// }

		}
	};

	private void updateStatus(int status) {
		// switch (status) {
		// case KFXmppManager.CONNECTED:
		// KFSLog.d("登录成功");
		// break;
		// case KFXmppManager.DISCONNECTED:
		// KFSLog.d("未登录");
		// break;
		// case KFXmppManager.CONNECTING:
		// KFSLog.d("登录中");
		// break;
		// case KFXmppManager.DISCONNECTING:
		// KFSLog.d("登出中");
		// break;
		// case KFXmppManager.WAITING_TO_CONNECT:
		// case KFXmppManager.WAITING_FOR_NETWORK:
		// KFSLog.d("waiting to connect");
		// break;
		// default:
		// throw new IllegalStateException();
		// }
	}

}
