package csust.student.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.adapter.MySignDetailItemAdapter;
import csust.student.chartView.MyArc;
import csust.student.fragment.TeacherFragment;
import csust.student.info.CourseInfo;
import csust.student.info.StudentSignDetail;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.refresh.PullToRefreshLayout;
import csust.student.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.student.refresh.view.PullableListView;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;

/**
 * 课程的详细信息，自己这门课的详细信息。
 * 
 * @author U-anLA
 *
 */
public class CourseDetailActivity extends Activity implements OnClickListener {

	// 定义相应控件。
	private LinearLayout load_progressBar, mArc, mLinearLayout;
	private TextView HomeNoValue, mDescription;
	private RelativeLayout mSignDetailTitle, mDetailTitle;

	// 用于获得课程综合签到率的url
	private String urlRate = null;

	// 获得每次签到的学生列表的url
	private String urlSignListUrl = null;

	// 用于接收上个界面传过来的courseinfo
	private CourseInfo myCourseInfo;

	// 定义adpter
	private MySignDetailItemAdapter myAdapter;

	// 定义自己的list
	private List<StudentSignDetail> list;

	// 定义自己可刷新的listview

	// 定义json解析类
	private MyJson myJson = new MyJson();

	// 关闭按钮
	private ImageView mClose;
	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;
	
	//定义起始地点
	private int mStart = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_course_sign_detail_info);
		// 初始化并且获得上一个界面传来的mycourseinfo
		Intent intent = this.getIntent();
		// 必须要这个才能，获得名字value
		Bundle bundle = intent.getBundleExtra("value");

		myCourseInfo = (CourseInfo) bundle.getSerializable("courseInfo");

		initView();
	}

	/**
	 * 初始化界面。
	 */
	private void initView() {

		list = new ArrayList<StudentSignDetail>();
		myAdapter = new MySignDetailItemAdapter(CourseDetailActivity.this,
				list, myCourseInfo.getCourseName(),
				Model.MYUSERINFO.getStudent_name());

		// 用于初始化界面。
		load_progressBar = (LinearLayout) findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) findViewById(R.id.HomeGroup);
		mArc = (LinearLayout) findViewById(R.id.arc);
		HomeNoValue = (TextView) findViewById(R.id.HomeNoValue);
		mSignDetailTitle = (RelativeLayout) findViewById(R.id.course_sign_detail_title);
		mDescription = (TextView) findViewById(R.id.course_detail_description);
		mClose = (ImageView) findViewById(R.id.close);
		mDetailTitle = (RelativeLayout) findViewById(R.id.course_sign_detail_title);

		((PullToRefreshLayout) findViewById(R.id.refresh_view))
		.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView)findViewById(R.id.content_view);

		
		mClose.setOnClickListener(this);


		// 拼接获取总签到率的url
		urlRate = Model.GETCOURSETOTALSIGNRATE + "student_id="
				+ Model.MYUSERINFO.getStudent_id() + "&course_id="
				+ myCourseInfo.getCourse_id();
		ThreadPoolUtils.execute(new HttpGetThread(hand1, urlRate));

		// 拼接获取每次签到列表状态的列表的url
		urlSignListUrl = Model.GETSIGNINFOLISTOFCOURSE + "student_id="
				+ Model.MYUSERINFO.getStudent_id() + "&course_id="
				+ myCourseInfo.getCourse_id()+"&start="+mStart+"&count="+Model.INIT_COUNT;
		ThreadPoolUtils.execute(new HttpGetThread(hand2, urlSignListUrl));
		listView.setAdapter(myAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 用于处理总签到率的handler
	 */
	Handler hand1 = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(CourseDetailActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(CourseDetailActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				// 正确的处理逻辑。处理总签到率
				String result = (String) msg.obj;

				String[] str = result.split(",");
				int myResult = Integer.parseInt(str[0]);
				int totalCount = Integer.parseInt(str[1]);
				int signCount = Integer.parseInt(str[2]);

				mArc.addView(new MyArc(CourseDetailActivity.this, myResult));
				mArc.setVisibility(View.VISIBLE);
				mDescription.setText("本门课总共签到" + totalCount + "次，您有效签到"
						+ signCount + "次");
				mDescription.setVisibility(View.VISIBLE);

				load_progressBar.setVisibility(View.GONE);
				mDetailTitle.setVisibility(View.VISIBLE);
			}
		}
	};

	/**
	 * 用于处理获得每位学生签到列表信息的list的hand
	 */
	Handler hand2 = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(CourseDetailActivity.this, "找不到服务器地址", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(CourseDetailActivity.this, "传输失败", 1).show();
			} else if (msg.what == 200) {
				HomeNoValue.setVisibility(View.GONE);
				load_progressBar.setVisibility(View.GONE);
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
				List<StudentSignDetail> newList = myJson.getStudentSignDetailInfoList(result);
				if (newList.size() != 0) {

					for (StudentSignDetail t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(CourseDetailActivity.this, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有签到情况");
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

	
	
	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			CourseDetailActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			urlSignListUrl = Model.GETSIGNINFOLISTOFCOURSE + "student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&course_id="
					+ myCourseInfo.getCourse_id()+"&start="+mStart+"&count="+Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand2, urlSignListUrl));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			CourseDetailActivity.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			urlSignListUrl = Model.GETSIGNINFOLISTOFCOURSE + "student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&course_id="
					+ myCourseInfo.getCourse_id()+"&start="+mStart+"&count="+5;
			ThreadPoolUtils.execute(new HttpGetThread(hand2, urlSignListUrl));
		}

	}

}
