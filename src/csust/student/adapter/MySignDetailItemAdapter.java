package csust.student.adapter;

import java.util.List;

import csust.student.activity.R;
import csust.student.adapter.MySearchCourseInfoAdapter.Holder;
import csust.student.info.SearchCourseInfo;
import csust.student.info.StudentSignDetail;
import csust.student.model.Model;
import csust.student.utils.LoadImg;
import csust.student.utils.LoadImg.ImageDownloadCallBack;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 每位同学的每一门课程的所有不同的签到几率的adapter
 * @author U-anLA
 *
 */
public class MySignDetailItemAdapter extends BaseAdapter{

	private List<StudentSignDetail> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	//获得coursename和studentname
	private String courseName,studentName;
	

	public MySignDetailItemAdapter(Context ctx, List<StudentSignDetail> list,String courseName,String studentName) {
		this.list = list;
		this.ctx = ctx;
		// 加载图像
		loadImgHeadImg = new LoadImg(ctx);
		this.courseName = courseName;
		this.studentName = studentName;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final Holder hold;

		if (convertView == null) {
			hold = new Holder();
			convertView = View.inflate(ctx,
					R.layout.mylistview_item_student_sign_detail_list, null);
			hold.myPic = (ImageView) convertView
					.findViewById(R.id.mydetail_studentName_pic);
			hold.courseName = (TextView) convertView
					.findViewById(R.id.mydetail_courseName);
			hold.studentName = (TextView) convertView
					.findViewById(R.id.mydetail_studentName);
			hold.signDate = (TextView) convertView
					.findViewById(R.id.mydetail_signDate);
			hold.signState = (TextView) convertView
					.findViewById(R.id.mydetail_signState);
			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();
		}
		Object b = convertView.getTag();

		hold.courseName.setText("课程名:" + this.courseName);
		hold.studentName.setText("学生名:" + this.studentName);
		hold.signDate.setText("签到日期:" + list.get(position).getSign_date());
		hold.signState.setText("签到状态:" + list.get(position).getSign_state());

		// 设置监听
		hold.myPic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 用于和教师会话~，后期实现。
				if (Model.MYUSERINFO != null) {
					// 已登录
				} else {
					Toast.makeText(ctx, "哈哈哈哈", 1).show();
				}
			}
		});

		hold.myPic.setImageResource(R.drawable.default_users_avatar);
		if (list.get(position).getSign_date().equalsIgnoreCase("")) {
			hold.myPic.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.myPic.setImageResource(R.drawable.default_users_avatar);
//			hold.myPic.setTag(Model.USERHEADURL
//					+ list.get(position).getSign_date());
//			Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.myPic,
//					Model.USERHEADURL + list.get(position).getSign_date(),
//					new ImageDownloadCallBack() {
//						@Override
//						public void onImageDownload(ImageView imageView,
//								Bitmap bitmap) {
//							if (position >= list.size()) {
//								if (hold.myPic.getTag().equals(
//										Model.USERHEADURL
//												+ list.get(position - 1)
//														.getSign_date())) {
//									hold.myPic.setImageBitmap(bitmap);
//								}
//							} else {
//								if (hold.myPic.getTag().equals(
//										Model.USERHEADURL
//												+ list.get(position)
//														.getSign_date())) {
//									hold.myPic.setImageBitmap(bitmap);
//								}
//							}
//						}
//					});
//			if (bitTeacher != null) {
//				hold.myPic.setImageBitmap(bitTeacher);
//			}
		}

		return convertView;

	}

	static class Holder {
		ImageView myPic;
		TextView studentName;
		TextView courseName;
		TextView signDate;
		TextView signState;
	}
}
