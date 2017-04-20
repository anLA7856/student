package csust.student.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
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
import csust.student.adapter.MyTeacherListAdapter;
import csust.student.info.TeacherListInfo;
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

public class TeacherFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mTopImg;
	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private TeacherFragmentCallBack mTeacherFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<TeacherListInfo> list = new ArrayList<TeacherListInfo>();
	private MyTeacherListAdapter mAdapter = null;
	// 用于设定初始值
	private int mStart = 0;
	// 用于设定以此增加的条目
	private int mAdd = 5;
	private String url = null;
	private boolean flag = true;
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
		view = inflater.inflate(R.layout.frame_teacher, null);

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
		mTopMenuOne = (TextView) view.findViewById(R.id.TopMenuOne);
		HomeNoValue = (TextView) view.findViewById(R.id.HomeNoValue);

		((PullToRefreshLayout) view.findViewById(R.id.refresh_view))
				.setOnRefreshListener(new MyInnerListener());
		listView = (PullableListView) view.findViewById(R.id.content_view);

		mTopImg.setOnClickListener(this);
		HomeNoValue.setVisibility(View.GONE);
		mAdapter = new MyTeacherListAdapter(ctx, list);

		listView.setAdapter(mAdapter);

		if (Model.MYUSERINFO != null) {
			isFirst = true;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACHERLIST + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&count="
					+ Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);
		}
		
		//设置项目监听事件
		listView.setOnItemLongClickListener(new OnItemLongClickListener()
		{

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Toast.makeText(
						ctx,
						"LongClick on "
								+ parent.getAdapter().getItemId(position),
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				Toast.makeText(ctx,
						" Click on " + parent.getAdapter().getItemId(position),
						Toast.LENGTH_SHORT).show();
			}
		});

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
				if(result == null){
					return;
				}
				if (isFirst == true) {
					// 清空
					if (list != null) {
						list.removeAll(list);
					}
				}
				List<TeacherListInfo> newList = myJson.getTeacherList(result);
				if (newList.size() != 0) {

					for (TeacherListInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(ctx, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有教师信息记录情况");
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

	public void setCallBack(TeacherFragmentCallBack mTeacherFragmentCallBack) {
		this.mTeacherFragmentCallBack = mTeacherFragmentCallBack;
	}

	public interface TeacherFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mTeacherFragmentCallBack.callback(R.id.Menu);
			break;
		case R.id.SendAshamed:
			mTeacherFragmentCallBack.callback(R.id.SendAshamed);
			break;
		default:
			break;
		}
	}

	private class MainListOnItemClickListener implements OnItemClickListener {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent intent = new Intent(ctx, CourseDetailActivity.class);
			Bundle bund = new Bundle();
			bund.putSerializable("courseInfo", list.get(arg2 - 1));

			// 这句暂时不嫩共。

			intent.putExtra("value", bund);
			startActivity(intent);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次onresum时候，就要把homenovalue设为false
		mStart=0;
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
			url = Model.GETTEACHERLIST + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&count="
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
			TeacherFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACHERLIST + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&count="
					+ Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			TeacherFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETTEACHERLIST + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id() + "&count=" + 5;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

	}

}
