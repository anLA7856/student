package csust.student.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;











import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import csust.student.fragment.BeginSignFragment;
import csust.student.fragment.CourseFragment;
import csust.student.fragment.BeginSignFragment.BeginSignFragmentCallBack;
import csust.student.fragment.CourseFragment.CourseFragmentCallBack;
import csust.student.fragment.TeacherFragment;
import csust.student.fragment.TeacherFragment.TeacherFragmentCallBack;
import csust.student.info.UserInfo;
import csust.student.model.Model;
import csust.student.utils.MyJson;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends SlidingFragmentActivity implements
		OnClickListener {

//	private KFSettingsManager mSettingsMgr;
	// 左边的抽屉类
	private View mLeftView;
	// 第三方抽屉菜单管理工具类
	private SlidingMenu mSlidingMenu;
	// 开始签到的碎片
	private BeginSignFragment mBeginSignFragment;
	// 课程管理的碎片
	private CourseFragment mCourseFragment;
	//teacher的fragment
	private TeacherFragment mTeacherFragment;
	
	// 定义fragment管理器：
	private FragmentManager mFragmentManager;
	// 获取fragment栈
	private android.support.v4.app.FragmentTransaction mFragmentTransaction;
	private List<Fragment> myFragmentList = new ArrayList<Fragment>();
	// leftView里面的空间
	private LinearLayout mLoginThisApp;// 用户登录用户
	private TextView myUserName;
	private ImageView mSettingBtn; // 设置按钮
	// leftview中下面的按钮
	private RelativeLayout mLeftSign, mLeftCourse,mLeftTeacher;
	private int fragmentFlag = 0;
	private MyJson myjson = new MyJson();
	
	//判断是否退出
	private boolean isExist;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
//		mSettingsMgr = KFSettingsManager.getSettingsManager(this);
		// 设置为开发者调试模式，默认为true，如果关闭开发者模式，请设置为false
		// 没有了
		initView();
		login();
		SharedPreferences sp = MainActivity.this.getSharedPreferences(
				"UserInfo", MODE_PRIVATE);
		String result = sp.getString("UserInfoJson", "none");
		Log.e("SharedPreferencesOld", result);
		File file = new File(Model.LOCALSTORAGE);
		if(!file.exists()){
			file.mkdir();
		}
		if (!result.equals("none")) {
			List<UserInfo> newList = myjson.getUserInfoList(result);
			if (newList != null) {
				Model.MYUSERINFO = newList.get(0);
				myUserName.setText(Model.MYUSERINFO.getStudent_username());

			}
		}

	}

	private void login() {
		// 检查 用户名/密码 是否已经设置,如果已经设置，则登录
//		if (!"".equals(mSettingsMgr.getUsername())
//				&& !"".equals(mSettingsMgr.getPassword()))
//			Log.i("user", "user");
////		 KFIMInterfaces.login(this, mSettingsMgr.getUsername(),
////		 mSettingsMgr.getPassword());
		// 设置个人资料"NICKNAME"
	}

	private void initView() {
		// 获取相应的控件
		mLeftView = View.inflate(MainActivity.this, R.layout.leftview, null);
		mLoginThisApp = (LinearLayout) mLeftView
				.findViewById(R.id.LoginThisAPP);
		mSettingBtn = (ImageView) mLeftView.findViewById(R.id.SettingBtn);
		mLeftCourse = (RelativeLayout) mLeftView.findViewById(R.id.LeftCourse);
		mLeftSign = (RelativeLayout) mLeftView.findViewById(R.id.LeftSign);
		mLeftTeacher = (RelativeLayout) mLeftView.findViewById(R.id.LeftTeacher);
		myUserName = (TextView) mLeftView.findViewById(R.id.myUserName);

		mLoginThisApp.setOnClickListener(MainActivity.this);
		mSettingBtn.setOnClickListener(MainActivity.this);
		mLeftCourse.setOnClickListener(MainActivity.this);
		mLeftSign.setOnClickListener(MainActivity.this);
		mLeftTeacher.setOnClickListener(MainActivity.this);

		mLeftCourse
				.setBackgroundResource(R.drawable.side_menu_background_active);
		mCourseFragment = new CourseFragment();
		myFragmentList.add(mCourseFragment);
		mBeginSignFragment = new BeginSignFragment();
		myFragmentList.add(mBeginSignFragment);
		mTeacherFragment = new TeacherFragment();
		myFragmentList.add(mTeacherFragment);

		mSlidingMenu = this.getSlidingMenu();
		this.setBehindContentView(mLeftView);

		mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mFragmentManager = MainActivity.this.getSupportFragmentManager();
		mFragmentTransaction = mFragmentManager.beginTransaction();
		// 供跳转
		createFargment(2);
		createFargment(1);

		FragmentTransaction mFragmentTransaction = mFragmentManager
				.beginTransaction();
		mFragmentTransaction.replace(R.id.main, mCourseFragment);
		mFragmentTransaction.commit();

	}

	// 设置右边的fragment加载的控件
	private void createFargment(int flag) {
		// 如果正在加载的fragment是传过来的，那么久不操作，否则去加载
		MainActivity.this.toggle();
		if (fragmentFlag != flag) {
			switch (flag) {
			case 1: // 课程管理的fragment
				mCourseFragmentCallBack();
				
				break;
			case 2: // 签到的fragment
				mBeginSignFragmentCallBack();
				break;
			case 3: // 教师的fragment
				mTeacherFragmentCallBack();
				break;
			}

			if (fragmentFlag != 0) {
				mFragmentTransaction.remove(myFragmentList
						.get(fragmentFlag - 1));
			}
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.replace(R.id.main,
					myFragmentList.get(flag - 1));
			// 提交保存杠杆替换或者添加fragment
			mFragmentTransaction.commit();
			fragmentFlag = flag;
		}
	}

	/**
	 * 从mcoursefragment里面毁掉回来的事件监听设置方法
	 */
	private void mCourseFragmentCallBack() {
		mCourseFragment.setCallBack(new MyCourseFragmentCallBack());
	}

	/**
	 * 从mbeginsignfragment里面回调回来的事件监听设置方法
	 */
	private void mBeginSignFragmentCallBack() {
		mBeginSignFragment.setCallBack(new MyBeginSignFragmentCallBack());
	}

	/**
	 * 从teacherfragment里面来调用回调监听方法。
	 */
	private void mTeacherFragmentCallBack() {
		mTeacherFragment.setCallBack(new MyTeacherFragmentCallBack());
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.LoginThisAPP:
			if (Model.MYUSERINFO != null) {
				Intent intent = new Intent(MainActivity.this,
						UserInfoActivity.class);
				Bundle bund = new Bundle();
				bund.putSerializable("UserInfo", Model.MYUSERINFO);
				intent.putExtra("value", bund);
				startActivity(intent);
			} else {
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.SettingBtn:
			Intent intent = new Intent(MainActivity.this, SettingActivity.class);
			startActivity(intent);
			break;
		case R.id.LeftCourse:
			createleftviewbg();
			mLeftCourse
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFargment(1);
			break;
		case R.id.LeftSign:
			createleftviewbg();
			mLeftSign
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFargment(2);
			break;
		case R.id.LeftTeacher:
			createleftviewbg();
			mLeftTeacher
					.setBackgroundResource(R.drawable.side_menu_background_active);
			createFargment(3);
			break;
		default:
			break;
		}
	}

	// 设置leftview控件的默认背景色
	private void createleftviewbg() {
		mLeftCourse.setBackgroundResource(R.drawable.leftview_list_bg);
		mLeftSign.setBackgroundResource(R.drawable.leftview_list_bg);
		mLeftTeacher.setBackgroundResource(R.drawable.leftview_list_bg);
	}

	@Override
	protected void onStart() {

		if (Model.MYUSERINFO != null) {
			myUserName.setText(Model.MYUSERINFO.getStudent_username());
			// 这里没写
			// KFIMInterfaces.setVCardField(MainActivity.this, "NICKNAME",
			// Model.MYUSERINFO.getUname());

		} else {
			myUserName.setText("登录益签到");
		}
		super.onStart();
	}

	/**
	 * 实现coursefragment接口类
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyCourseFragmentCallBack implements CourseFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;

			case R.id.SendAshamed:
				if (Model.MYUSERINFO != null) {
					Intent intent = new Intent(MainActivity.this,
							AddCourseActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}
				break;
			default:
				break;
			}
		}

	}

	/**
	 * 实现接口子类。
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyBeginSignFragmentCallBack implements
			BeginSignFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;
				//这里就没有上一个的那个加入新的东西的模块
			default:
				break;
			}
		}

	}
	
	
	/**
	 * 实现接口子类。
	 * 
	 * @author U-anLA
	 *
	 */
	private class MyTeacherFragmentCallBack implements
			TeacherFragmentCallBack {

		@Override
		public void callback(int flag) {
			switch (flag) {
			case R.id.Menu:
				MainActivity.this.toggle();
				break;
				//这里就没有上一个的那个加入新的东西的模块
			default:
				break;
			}
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {  
            exit();  
            return false;  
        } else {  
            return super.onKeyDown(keyCode, event);  
        }  
	}

	private void exit() {
        if (!isExist) {  
            isExist = true;  
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();  
            mHandler.sendEmptyMessageDelayed(0, 2000);  
        } else {  
            Intent intent = new Intent(Intent.ACTION_MAIN);  
            intent.addCategory(Intent.CATEGORY_HOME);  
            startActivity(intent);  
            System.exit(0);
        }  	
		
	}
	
	Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			 isExist = false;  
		};
	};

}
