package csust.student.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.activity.CourseDetailActivity;
import csust.student.activity.R;
import csust.student.adapter.MyListAdapter;
import csust.student.info.CourseInfo;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.refresh.PullToRefreshLayout;
import csust.student.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.student.refresh.view.PullableListView;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;

/**
 * 查看的课程列表的fragment
 * 
 * @author U-anLA
 *
 */

public class CourseFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mTopImg;
	private ImageView mSendAshamed;
	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private CourseFragmentCallBack mCourseFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<CourseInfo> list = new ArrayList<CourseInfo>();
	private MyListAdapter mAdapter = null;
	private int mStart = 0;
	private int mEnd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottomFlag = true;
	private Context ctx;

	// 用于标记是否是调用了onpause。。
	private boolean isPause = false;

	private PullableListView listView;

	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_course, null);
		ctx = view.getContext();
		// 这是鸡肋，可能需要改！！！！！！
		if (list != null) {
			list.removeAll(list);
		}
		initView();
		return view;
	}

	private void initView() {
		load_progressBar = (LinearLayout) view
				.findViewById(R.id.load_progressBar);
		mLinearLayout = (LinearLayout) view.findViewById(R.id.HomeGroup);

		mTopImg = (ImageView) view.findViewById(R.id.Menu);
		mSendAshamed = (ImageView) view.findViewById(R.id.SendAshamed);
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);

		((PullToRefreshLayout) view.findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) view.findViewById(R.id.content_view);

		mTopImg.setOnClickListener(this);
		mSendAshamed.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);
		mAdapter = new MyListAdapter(ctx, list);

		listView.setAdapter(mAdapter);

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSTUCOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getStudent_username() + "&count="
					+ Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}

		listView.setOnItemClickListener(new MainListOnItemClickListener());
		listView.setOnItemLongClickListener(new MainListOnItemClickListener());
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
				load_progressBar.setVisibility(View.GONE);
				if (pullToRefreshLayout != null) {
					pullToRefreshLayout
							.refreshFinish(PullToRefreshLayout.SUCCEED);
				}
				String result = (String) msg.obj;
				if (result == null) {
					return;
				}
				if (isFirst == true) {
					// 清空
					if (list != null) {
						list.removeAll(list);
					}
				}
				List<CourseInfo> newList = myJson.getCourseInfoList(result);
				if (newList.size() != 0) {

					for (CourseInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(ctx, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有课程信息喔");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				mAdapter.notifyDataSetChanged();

			}
			mAdapter.notifyDataSetChanged();
		};
	};

	public void setCallBack(CourseFragmentCallBack mCourseFragmentCallBack) {
		this.mCourseFragmentCallBack = mCourseFragmentCallBack;
	}

	public interface CourseFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mCourseFragmentCallBack.callback(R.id.Menu);
			break;
		case R.id.SendAshamed:
			mCourseFragmentCallBack.callback(R.id.SendAshamed);
			break;
		default:
			break;
		}
	}

	private class MainListOnItemClickListener implements OnItemClickListener,
			OnItemLongClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ctx, CourseDetailActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("courseInfo", list.get(arg2));

			// 这句暂时不嫩共。

			intent.putExtra("value", bund);
			startActivity(intent);
		}

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			final int myPosition = position;
			new AlertDialog.Builder(ctx)
					.setTitle("删除提示框")
					.setMessage("确认删除本门课程(相关的课程记录和签到记录均会删除！！)")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									CourseInfo c = list.get(myPosition);
									// 用于删除某一门课程！courseName就是course_id
									String delteUrl = Model.STUDELETECOURSE
											+ "student_id="
											+ Model.MYUSERINFO.getStudent_id()
											+ "&course_id=" + c.getCourse_id();
									ThreadPoolUtils.execute(new HttpGetThread(
											hand1, delteUrl));

								}
							}).setNegativeButton("取消", null).show();
			// 注意这里是防止再次出发单词点击实际，如果是false，就会出发单词短点击事件
			return true;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mStart = 0;
		HomeNoValue.setVisibility(View.GONE);
		if (isPause == false) {
			return;
		}

		if (list.size() != 0) {
			list.removeAll(list);
		}
		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSTUCOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getStudent_username() + "&count="
					+ Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));

		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}

	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			CourseFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSTUCOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getStudent_username() + "&count="
					+ Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			CourseFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETSTUCOURSE + "startCount=" + mStart + "&username="
					+ Model.MYUSERINFO.getStudent_username() + "&count=" + 5;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

	}

	// 用于删除课程的url
	Handler hand1 = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(ctx, "找不到服务器地址", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 100) {
				Toast.makeText(ctx, "传输失败", 1).show();
				listBottomFlag = true;
			} else if (msg.what == 200) {
				// 正确的处理逻辑
				String result = (String) msg.obj;

				if (result.equals("[1]")) {
					// 说明删除成功！
					Toast.makeText(ctx, "删除成功", 1).show();
					// 这里还需要刷新
					list.removeAll(list);
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
				} else {
					Toast.makeText(ctx, "删除失败！！！", 1).show();
					// 说明删除失败！
				}

			}
		}
	};

}
