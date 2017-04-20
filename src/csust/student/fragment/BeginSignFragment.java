package csust.student.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.activity.R;
import csust.student.activity.SignActivity;
import csust.student.adapter.MySignListAdapter;
import csust.student.info.SignInfo;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.refresh.PullToRefreshLayout;
import csust.student.refresh.PullToRefreshLayout.MyOnRefreshListener;
import csust.student.refresh.view.PullableListView;
import csust.student.thread.HttpGetThread;
import csust.student.utils.MyJson;

/**
 * 开始签到的fragment
 * 
 * @author U-anLA
 *
 */

public class BeginSignFragment extends Fragment implements OnClickListener {

	private View view;
	private ImageView mTopImg;

	private TextView mTopMenuOne;
	private LinearLayout mLinearLayout, load_progressBar;
	private TextView HomeNoValue;
	private BeginSignFragmentCallBack mBeginSignFragmentCallBack;
	private MyJson myJson = new MyJson();
	private List<SignInfo> list = new ArrayList<SignInfo>();
	private MySignListAdapter mSignAdapter = null;
	private int mStart = 0;
	private int mAdd = 5;
	private String url = null;
	private boolean flag = true;
	private boolean loadflag = false;
	private boolean listBottomFlag = true;
	private Context ctx;
	
	//用于保存点击的signinfo
	private SignInfo mySignInfo;
	
	//用于标记是否是调用了onpause。。
	private boolean isPause = false;
	
	// 用来判断是首次加载还是，到底部了加载
	private boolean isFirst = true;

	// 用于获取共享的PullToRefreshLayout pullToRefreshLayout
	private static PullToRefreshLayout pullToRefreshLayout;

	//用来显示的listview
	private PullableListView listView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.frame_sign, null);
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
		mSignAdapter = new MySignListAdapter(ctx, list);
		listView.setAdapter(mSignAdapter);
		
		if (Model.MYUSERINFO != null) {
			isFirst = true;
			//第一次，获得的基准数目是15
			url = Model.GETNOTSIGNINFO + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id()+"&count="+Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);		}
		
		listView.setOnItemClickListener(new MainListOnItemClickListener());


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
				List<SignInfo> newList = myJson.getNotSignInfoList(result);
				if (newList.size() != 0) {

					for (SignInfo t : newList) {
						list.add(t);
					}
					mLinearLayout.setVisibility(View.VISIBLE);

				} else {
					Toast.makeText(ctx, "已经没有了。。", 1).show();
					if (list.size() == 0) {
						mLinearLayout.setVisibility(View.GONE);
						HomeNoValue.setText("暂时没有签到信息哦");
						HomeNoValue.setVisibility(View.VISIBLE);
					} else {
						mLinearLayout.setVisibility(View.VISIBLE);

					}
				}

				mSignAdapter.notifyDataSetChanged();

			}
			mSignAdapter.notifyDataSetChanged();
		};
	};

	public void setCallBack(BeginSignFragmentCallBack mBeginSignFragmentCallBack) {
		this.mBeginSignFragmentCallBack = mBeginSignFragmentCallBack;
	}

	public interface BeginSignFragmentCallBack {
		public void callback(int flag);
	}

	@Override
	public void onClick(View v) {
		int mID = v.getId();
		switch (mID) {
		case R.id.Menu:
			mBeginSignFragmentCallBack.callback(R.id.Menu);
			break;
		default:
			break;
		}
	}

	private class MainListOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			mySignInfo = list.get(arg2);
			// 在这里判断是否已经签过到了！！！！，签过了就不嗯呢该在进去签到了
			String url = Model.VERTIFYIFCANSIGN + "allow_sign_id="
					+ list.get(arg2).getAlow_sign_id() + "&student_id="
					+ Model.MYUSERINFO.getStudent_id();
			ThreadPoolUtils.execute(new HttpGetThread(hand1, url));
			
		}



		/**
		 * 用于处理判断是否可以签到的hander
		 */
		Handler hand1 = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 404) {
					Toast.makeText(ctx, "找不到服务器地址", 1).show();
					listBottomFlag = true;
				} else if (msg.what == 100) {
					Toast.makeText(ctx, "传输失败", 1).show();
					listBottomFlag = true;
				} else if (msg.what == 200) {
					String result = (String) msg.obj;
					if(result.equals("[0]")){
						//证明还没签过到
						Intent intent = new Intent(ctx, SignActivity.class);

						Bundle bund = new Bundle();
						bund.putSerializable("courseInfo", mySignInfo);
						// intent.putExtra("value", bund);
						intent.putExtras(bund);
						startActivity(intent);
					}else{
						//证明签过到
						Toast.makeText(ctx, "抱歉，您已经签过到了。。。", 1).show();
						
					}
					
				}
			}
		};

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		mStart=0;
		HomeNoValue.setVisibility(View.GONE);
		if(isPause == false){
			return;
		}
		
		if(list.size() != 0){
			list.removeAll(list);
		}
		if (Model.MYUSERINFO != null) {
			isFirst = true;
			//第一次，获得的基准数目是15
			url = Model.GETNOTSIGNINFO + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id()+"&count="+Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		} else {
			// 为空的时候，直接显示请先登录
			load_progressBar.setVisibility(View.GONE);
			mLinearLayout.setVisibility(View.GONE);
			HomeNoValue.setText("请先登录");
			HomeNoValue.setVisibility(View.VISIBLE);		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		isPause = true;
	}
	
	private class MyInnerListener implements MyOnRefreshListener {

		@Override
		public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
			BeginSignFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 初始化
			isFirst = true;
			mStart = 0;
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETNOTSIGNINFO + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id()+"&count="+Model.INIT_COUNT;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

		@Override
		public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
			BeginSignFragment.pullToRefreshLayout = pullToRefreshLayout;
			// 向下拉的时候，这个就要变成false了
			isFirst = false;
			mStart = list.size();
			// 第一次，获得的个数为15，也就是init_count
			url = Model.GETNOTSIGNINFO + "start=" + mStart + "&student_id="
					+ Model.MYUSERINFO.getStudent_id()+"&count="+5;
			ThreadPoolUtils.execute(new HttpGetThread(hand, url));
		}

	}


}
