package csust.student.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.adapter.MyListAdapter;
import csust.student.info.CourseInfo;
import csust.student.info.UserInfo;
import csust.student.model.Model;
import csust.student.myview.MyDetailsListView;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpGetThread;
import csust.student.utils.CharacterUtil;
import csust.student.utils.LoadImg;
import csust.student.utils.LoadImg.ImageDownloadCallBack;
import csust.student.utils.MyJson;



public class UserInfoActivity extends Activity implements OnClickListener {
	private UserInfo info = null;
	private ImageView mUserCamera;
	private LinearLayout mBrief, mQiushi;
	private LinearLayout mUserBrief, mUserQiushi;
	private Boolean myflag = true;
	private TextView SendMessage, UserMyName, UserAge, UserSex, UserStuNum,
			UserUserName, UserBrand;

	private LoadImg loadImgHeadImg;
	private MyJson myJson = new MyJson();
	private List<CourseInfo> list = new ArrayList<CourseInfo>();
	private MyListAdapter mAdapter = null;
	private Button listButtom = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottemFlag = true;
	private MyDetailsListView Detail_List;
	private LinearLayout Detail__progressBar;
	private RelativeLayout Detail_CommentsNum;
	private ProgressDialog mProDialog;

	//定义alertdialog
	private AlertDialog mAlertPassword;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_ACTION_BAR);

		setContentView(R.layout.activity_userinfo);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			setOverflowShowingAlways();// 进行调用设置actionbar的显示图标
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowHomeEnabled(false);// 把actionbar上面的默认显示的菜单图标隐藏掉
			actionBar.setTitle(Model.MYUSERINFO.getStudent_username());
		}
		Intent intent = getIntent();
		Bundle bund = intent.getBundleExtra("value");
		info = (UserInfo) bund.getSerializable("UserInfo");
		initView();
		createUserInfo();
		mAdapter = new MyListAdapter(UserInfoActivity.this, list);
		listButtom = new Button(UserInfoActivity.this);
		listButtom.setText("点击加载更多");
		listButtom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (flag && listBottemFlag) {
					url = Model.GETSTUCOURSE + "username="
							+ info.getStudent_username() + "&startCount="
							+ mStart;
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					listBottemFlag = false;
				} else if (!listBottemFlag)
					Toast.makeText(UserInfoActivity.this, "正在加载中...", 1).show();
			}
		});
		Detail_List.addFooterView(listButtom, null, false);
		listButtom.setVisibility(View.GONE);
		Detail_List.setAdapter(mAdapter);
		// 用于获得课程参数。
		String endParames = Model.GETSTUCOURSE + "username="
				+ info.getStudent_username() + "&startCount=" + mStart;
		ThreadPoolUtils.execute(new HttpGetThread(hand, endParames));
		// 设置个人资料"NICKNAME"
		// KFIMInterfaces.setVCardField(UserInfoActivity.this, "NICKNAME",
		// info.getUname());

	}

	private void initView() {
		mProDialog = new ProgressDialog(this);
		mBrief = (LinearLayout) findViewById(R.id.Brief);
		mBrief = (LinearLayout) findViewById(R.id.Brief);
		mQiushi = (LinearLayout) findViewById(R.id.Qiushi);
		mUserBrief = (LinearLayout) findViewById(R.id.UserBrief);
		mUserQiushi = (LinearLayout) findViewById(R.id.UserQiushi);
		mUserCamera = (ImageView) findViewById(R.id.UserCamera);
		SendMessage = (TextView) findViewById(R.id.SendMessage);
		UserAge = (TextView) findViewById(R.id.UserAge);
		UserMyName = (TextView) findViewById(R.id.UserMyName);
		UserSex = (TextView) findViewById(R.id.UserSex);
		UserStuNum = (TextView) findViewById(R.id.UserStuNum);
		UserUserName = (TextView) findViewById(R.id.UserUserName);

		Detail_List = (MyDetailsListView) findViewById(R.id.Detail_List);
		Detail__progressBar = (LinearLayout) findViewById(R.id.Detail__progressBar);
		Detail_CommentsNum = (RelativeLayout) findViewById(R.id.usernoashamed);

		Detail_List.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(UserInfoActivity.this,
						CourseDetailActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("AshamedInfo", list.get(arg2));
				intent.putExtra("value", bund);
				startActivity(intent);
			}
		});

		mBrief.setOnClickListener(this);
		mQiushi.setOnClickListener(this);
		SendMessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// KFIMInterfaces.startChatWithUser(UserInfoActivity.this,//
				// 上下文Context
				// Model.APPKEY + info.getUname(),// 对方用户名
				// info.getUname());// 自定义会话窗口标题

			}
		});
	}

	@Override
	public void onClick(View v) {
		int mId = v.getId();
		switch (mId) {
		case R.id.Brief:
			myflag = true;
			initCont(myflag);
			break;
		case R.id.Qiushi:
			// myflag = false;
			// initCont(myflag);
			Toast.makeText(UserInfoActivity.this, "课程功能待加入。。。", 1).show();
			break;

		case R.id.UserCamera:
			// Intent intent = new Intent(UserInfoActivity.this,.class);
			// startActivity(intent);
			Toast.makeText(UserInfoActivity.this, "更改皂片功能待加入。。。", 1).show();
			break;
		// case R.id.img_UserMore:
		// logout();
		// break;
		}
	}

	private void logout() {
		if (Model.MYUSERINFO != null) {
			if (info.getStudent_username().equals(
					Model.MYUSERINFO.getStudent_username())) {
				new AlertDialog.Builder(this)
						.setMessage("确认退出登录?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										Model.MYUSERINFO = null;
										SharedPreferences sp = getSharedPreferences(
												"UserInfo", MODE_PRIVATE);
										Editor editor = sp.edit();
										editor.clear();
										editor.commit();
										// 退出登录
										// KFIMInterfaces
										// .Logout(UserInfoActivity.this);
										Intent intent = new Intent(
												UserInfoActivity.this,
												LoginActivity.class);
										startActivity(intent);
										finish();
									}
								}).setNegativeButton("取消", null).create()
						.show();
			}
		}
	}

	Handler hand = new Handler() {

		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(UserInfoActivity.this, "请求失败，服务器故障", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(UserInfoActivity.this, "服务器无响应", 1).show();
				listBottemFlag = true;
			} else if (msg.what == 200) {
				String result = (String) msg.obj;
				if (result != null) {
					List<CourseInfo> newList = myJson.getCourseInfoList(result);
					if (newList != null) {
						if (newList.size() == 5) {
							Detail_List.setVisibility(View.VISIBLE);
							listButtom.setVisibility(View.VISIBLE);
							mStart += 5;
							mEnd += 5;
						} else if (newList.size() == 0) {
							if (list.size() == 0)
								Detail_CommentsNum.setVisibility(View.VISIBLE);
							listButtom.setVisibility(View.GONE);
							Detail_List.setVisibility(View.GONE);
						} else {
							Detail_List.setVisibility(View.VISIBLE);
							listButtom.setVisibility(View.GONE);
						}
						for (CourseInfo info : newList) {
							list.add(info);
						}
						listBottemFlag = true;
					} else {
						Detail_List.setVisibility(View.GONE);
						Detail_CommentsNum.setVisibility(View.VISIBLE);
					}
				}
				Detail__progressBar.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
			mAdapter.notifyDataSetChanged();
		};

	};

	private void initCont(Boolean myflag2) {
		if (myflag) {
			mBrief.setBackgroundResource(R.drawable.cab_background_top_light);
			mQiushi.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mUserBrief.setVisibility(View.VISIBLE);
			mUserQiushi.setVisibility(View.GONE);
		} else {
			mBrief.setBackgroundResource(R.drawable.ab_stacked_solid_light);
			mQiushi.setBackgroundResource(R.drawable.cab_background_top_light);
			mUserBrief.setVisibility(View.GONE);
			mUserQiushi.setVisibility(View.VISIBLE);
		}
	}

	private void createUserInfo() {
		if (Model.MYUSERINFO != null) {
			if (info.getStudent_username().equals(
					Model.MYUSERINFO.getStudent_username()))
				SendMessage.setVisibility(View.GONE);
		} else {
			Intent intent = new Intent(UserInfoActivity.this,
					LoginActivity.class);
			startActivity(intent);
		}
		// if (!info.getUage().equals("null")) {
		// UserAge.setText(info.getUage());
		// if (info.getUsex().equals("0")) {
		// UserAge.setBackgroundResource(R.drawable.nearby_gender_female);
		// } else if (info.getUsex().equals("1")) {
		// UserAge.setBackgroundResource(R.drawable.nearby_gender_male);
		// }
		// }
		if (!info.getStudent_username().equals("null")) {
			UserUserName.setText("" + info.getStudent_username());
		}
		if (!info.getStudent_name().equals("null")) {
			UserMyName.setText("" + info.getStudent_name());
		}
		// if (!info.getStudent_num().equals("null")) {
		// UserStuNum.setText("" + info.getStudent_num());
		// userinfo.setText(info.getUexplain());
		// }
		if (!info.getStudent_sex().equals("null")) {
			UserSex.setText("" + info.getStudent_sex());
		}
		// UserTime.setText("注册时间　" + info.getUtime());
		// 用于加载相对应的图片
		if (!info.getStudent_username().equals("null")) {
			loadImgHeadImg = new LoadImg(UserInfoActivity.this);
			Bitmap bit = loadImgHeadImg.loadImage(mUserCamera,
					Model.USERHEADURL + info.getStudent_username() + ".jpg",
					new ImageDownloadCallBack() {
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							mUserCamera.setImageBitmap(bitmap);
						}
					});
			if (bit != null) {
				mUserCamera.setImageBitmap(bit);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * 打开菜单的时候 设置icon 使用反射
	 */
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {// 这里也就是在actionbar打开菜单的时候
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {// MenuBuilder是menu的实现类
				try {
					Method m = menu.getClass().getDeclaredMethod(// 获取其中的一个方法setOptionalIconsVisible
							"setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}

	/**
	 * 
	 * 把actionBar上默认的那个菜单图标改成我们自己的图标“+”号的图标 在style里面我们先定义了显示的样式 这里再写个方法 才可以改变图标
	 * 
	 * 不写样式只写这里 或者 只写样式 不写这里 都是不能改变图标 使用反射
	 */

	private void setOverflowShowingAlways() {
		try {
			// true if a permanent menu key is present, false otherwise.
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");// 找到menukey
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_group_modify_base_info:
			Toast.makeText(this, "待加入", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_modify_password:
			// Toast.makeText(this, "action_modify_password",
			// Toast.LENGTH_SHORT)
			// .show();
			modifyPassword();
			break;
		case R.id.action_wait_in:
			Toast.makeText(this, "待加入", Toast.LENGTH_SHORT).show();
			break;
		case R.id.action_logout:
			logout();
			break;

		}
		return true;
	}

	private void modifyPassword() {
		LayoutInflater layoutInflater = LayoutInflater
				.from(UserInfoActivity.this);
		View modifyPasswordView = layoutInflater.inflate(
				R.layout.modify_password, null);

		final EditText oldPassword = (EditText) modifyPasswordView
				.findViewById(R.id.modify_password_oldpassword);
		final EditText newPassword1 = (EditText) modifyPasswordView
				.findViewById(R.id.modify_password_newpassword1);
		final EditText newPassword2 = (EditText) modifyPasswordView
				.findViewById(R.id.modify_password_newpassword2);

		mAlertPassword = new AlertDialog.Builder(UserInfoActivity.this)
				.setTitle("修改密码")
				.setView(modifyPasswordView)
				.setPositiveButton("确定",
						new android.content.DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 当点击确定的时候的操作。

								String strOld = oldPassword.getText()
										.toString().trim();
								String strNew1 = newPassword1.getText()
										.toString().trim();
								String strNew2 = newPassword2.getText()
										.toString().trim();
								// 判断输入是否合法
								// 定义检测包
								CharacterUtil cu = new CharacterUtil();

								// 检测
								if (cu.checkPasswordInput(
										UserInfoActivity.this, strOld) == true
										&& cu.checkPasswordInput(
												UserInfoActivity.this, strNew1) == true
										&& cu.checkPasswordInput(
												UserInfoActivity.this, strNew2) == true) {
									// 当全部检测通过的时候才可以进行下面的操作。
									if (!strOld.equals(Model.MYUSERINFO
											.getStudent_password())) {
										Toast.makeText(UserInfoActivity.this,
												"原密码输入有误", 1).show();
										return;
									}
									if (!strNew1.equals(strNew2)) {
										Toast.makeText(UserInfoActivity.this,
												"两次新密码输入不一致", 1).show();
										return;
									}
									// 正确的处理逻辑
									mProDialog.setTitle("修改中");
									mProDialog.show();
									String modifyPsswordUrl = Model.STUMODIFYPASSWORD
											+ "student_id="
											+ Model.MYUSERINFO.getStudent_id()
											+ "&new=" + strNew2;
									ThreadPoolUtils.execute(new HttpGetThread(
											hand1, modifyPsswordUrl));
								}

							}
						}).setNegativeButton("取消", null).create();

		mAlertPassword.show();

	}
	
	Handler hand1 = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if (msg.what == 404) {
				Toast.makeText(UserInfoActivity.this, "找不到服务器地址", 1).show();
				mProDialog.dismiss();
			} else if (msg.what == 100) {
				Toast.makeText(UserInfoActivity.this, "传输失败", 1).show();
				mProDialog.dismiss();
			} else if (msg.what == 200) {
				mProDialog.dismiss();
				String result = (String)msg.obj;
				if(result.equals("[1]")){
					//修改成功！
					
					Toast.makeText(UserInfoActivity.this, "密码修改成功，请用新密码登录！", 1).show();
					myLogout();
				}else{
					//修改失败！
					Toast.makeText(UserInfoActivity.this, "修改密码失败！", 1).show();
					
				}
			}
		}

		private void myLogout() {
			Model.MYUSERINFO = null;
			SharedPreferences sp = getSharedPreferences(
					"UserInfo", MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.clear();
			editor.commit();

			Intent intent = new Intent(
					UserInfoActivity.this,
					LoginActivity.class);
			startActivity(intent);
			finish();
		};
	};
}
