package csust.student.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.adapter.MySearchCourseInfoAdapter;
import csust.student.info.SearchCourseInfo;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.refresh.PullToRefreshLayout;
import csust.student.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.student.refresh.view.PullableListView;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;

public class AddCourseActivity extends Activity implements OnClickListener {

	// 定义相应activity控件。
	private ImageView mClose;
	private Button mSubmit;
	private RadioGroup mRadioGroup;
	private RadioButton radioTeacher, radioCourse;
	private EditText editTeacher, editCourse;
	

	// 获得隐藏控件
	private TextView HomeNoValue;
	private LinearLayout mLinearLayout;

	// 定义进度框
	private ProgressDialog mProgressDialog;

	// 定义接受的字符串变量
	private String teacherNum, courseNum;

	// 定义获取list的url
	private String urlList = null;
	
	//用于添加课程的url
	private String urlAddCourse = null;

	// 定义json的解析类
	private MyJson myJson = new MyJson();

	// 定义自己的searchcourseinfoadapter
	private MySearchCourseInfoAdapter myAdapter;


	// 定义自己的list
	private List<SearchCourseInfo> list;
	
	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	//用于检测起始地点
	private int mStart = 0;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_course);
		initView();
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		mProgressDialog = new ProgressDialog(AddCourseActivity.this);
		mClose = (ImageView) findViewById(R.id.add_course_close);
		mSubmit = (Button) findViewById(R.id.add_course_submit);
		mRadioGroup = (RadioGroup) findViewById(R.id.add_course_radiogroup1);
		radioTeacher = (RadioButton) findViewById(R.id.add_course_radio1);
		radioCourse = (RadioButton) findViewById(R.id.add_course_radio2);
		editTeacher = (EditText) findViewById(R.id.add_course_edittext1);
		editCourse = (EditText) findViewById(R.id.add_course_edittext2);
		HomeNoValue = (TextView) findViewById(R.id.add_course_HomeNoValue);
		mLinearLayout = (LinearLayout) findViewById(R.id.HomeGroup);

		((PullToRefreshLayout)findViewById(R.id.refresh_view))
		.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView)findViewById(R.id.content_view);

		
		
		// 设置radiogroup的选择事件
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.add_course_radio2) {
					editTeacher.setText("");
					editCourse.setText("");
					editTeacher.setEnabled(false);
					editCourse.setEnabled(true);
				} else {
					editTeacher.setText("");
					editCourse.setText("");
					editTeacher.setEnabled(true);
					editCourse.setEnabled(false);
				}
			}
		});

		// 注册监听
		mClose.setOnClickListener(this);
		mSubmit.setOnClickListener(this);

		// 初始化myadapter
		list = new ArrayList<SearchCourseInfo>();
		myAdapter = new MySearchCourseInfoAdapter(AddCourseActivity.this, list);
		// 设置listview

		listView.setAdapter(myAdapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 添加点击事件。用于添加课程。
				final int myPosition = position;
				new AlertDialog.Builder(AddCourseActivity.this).setTitle("添加提示框").setMessage("确定要添加该门课么？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@SuppressLint("ShowToast")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mProgressDialog.setTitle("添加中");
						mProgressDialog.show();
						//添加逻辑
						//拼装语句
						urlAddCourse = Model.ADDNEWCOURSE + "student_id=" + Model.MYUSERINFO.getStudent_id()+"&course_id="+list.get(myPosition).getCourse_id();
						//发送请求
						ThreadPoolUtils.execute(new HttpGetThread(hand2, urlAddCourse));
					}
				}).setNegativeButton("取消", null).show();
			}
		});

	}

	@Override
	public void onClick(View v) {
		// 控件点击事件。
		switch (v.getId()) {
		case R.id.add_course_close:
			// 关闭的处理
			finish();
			break;
		case R.id.add_course_submit:
			// 提交查询页面。
			teacherNum = editTeacher.getText().toString().trim();
			courseNum = editCourse.getText().toString().trim();

			doThreadMethod();

			break;

		default:
			break;
		}
	}

	private void doThreadMethod() {
		if (mRadioGroup.getCheckedRadioButtonId() == R.id.add_course_radio1
				&& !teacherNum.equals("")) {
			// 选择教师，并且教师号不为空
			//判断是否有非法字符
			if(teacherNum.matches("[a-zA-Z0-9]{0,12}")){
				isFirst = true;
				urlList = Model.SEARCHFORLIST + "teacherNum=" + teacherNum+"&student_id="+Model.MYUSERINFO.getStudent_id()+"&start="+mStart+"&count="+Model.INIT_COUNT;
				// 异步线程请求
				ThreadPoolUtils.execute(new HttpGetThread(hand1, urlList));
			}else{
				Toast.makeText(AddCourseActivity.this, "不能包含非法字符，只能数字", 1).show();
			}
			
		} else if (mRadioGroup.getCheckedRadioButtonId() == R.id.add_course_radio2
				&& !courseNum.equals("")) {
			
			if(courseNum.matches("[a-zA-Z0-9]{0,12}")){
				// 选择学生号，并且学生号不为空
				isFirst = true;
				urlList = Model.SEARCHFORLIST + "courseNum=" + courseNum+"&student_id="+Model.MYUSERINFO.getStudent_id()+"&start="+mStart+"&count="+Model.INIT_COUNT;
				// 异步线程请求
				ThreadPoolUtils.execute(new HttpGetThread(hand1, urlList));
			}else{
				Toast.makeText(AddCourseActivity.this, "不能包含非法字符，只能数字", 1).show();
			}

		}
	}

	// 用于处理获得课程结果的list
	Handler hand1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(AddCourseActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(AddCourseActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				if (pullToRefreshLayout != null) {
					pullToRefreshLayout
							.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
				String result = (String) msg.obj;
				if (isFirst == true) {
					// 清空
					if (list != null) {
						list.removeAll(list);
					}
				}
				List<SearchCourseInfo> newList = myJson.getSearchCourseInfoList(result);
				if (newList.size() != 0) {

					for (SearchCourseInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(AddCourseActivity.this, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有课程记录情况");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				myAdapter.notifyDataSetChanged();

			}
			myAdapter.notifyDataSetChanged();
		};
	};
	
	
	Handler hand2 = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(AddCourseActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(AddCourseActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				// 正常的处理逻辑。
				String result = (String) msg.obj;
				if(result.equals("[1]")){
					//添加成功界面
					mProgressDialog.setTitle("添加成功");
					mProgressDialog.dismiss();
					Toast.makeText(AddCourseActivity.this, "添加课程成功！", 1).show();
					teacherNum = editTeacher.getText().toString().trim();
					courseNum = editCourse.getText().toString().trim();

					doThreadMethod();
				}else{
					//添加失败的逻辑
					mProgressDialog.setTitle("添加失败");
					mProgressDialog.dismiss();
					Toast.makeText(AddCourseActivity.this, "添加课程失败！！！！", 1).show();
				}
				
			}
		};
	};
	
	
	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			AddCourseActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			urlList = Model.SEARCHFORLIST + "courseNum=" + courseNum+"&student_id="+Model.MYUSERINFO.getStudent_id()+"&start="+mStart+"&count="+Model.INIT_COUNT;

			ThreadPoolUtils.execute(new HttpGetThread(hand1, urlList));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			AddCourseActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			urlList = Model.SEARCHFORLIST + "courseNum=" + courseNum+"&student_id="+Model.MYUSERINFO.getStudent_id()+"&start="+mStart+"&count="+5;

			ThreadPoolUtils.execute(new HttpGetThread(hand1, urlList));
		}

	}

}
