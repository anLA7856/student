package csust.student.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.activity.R;
import csust.student.info.SearchCourseInfo;
import csust.student.model.Model;
import csust.student.utils.LoadImg;
import csust.student.utils.LoadImg.ImageDownloadCallBack;

/**
 * 添加课程的courseinfo的adapter
 * 
 * @author U-anLA
 *
 */
public class MySearchCourseInfoAdapter extends BaseAdapter {

	private List<SearchCourseInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;

	public MySearchCourseInfoAdapter(Context ctx, List<SearchCourseInfo> list) {
		this.list = list;
		this.ctx = ctx;
		// 加载图像
		loadImgHeadImg = new LoadImg(ctx);
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
					R.layout.mylistview_item_search_courseinfo, null);
			hold.coursePic = (ImageView) convertView
					.findViewById(R.id.search_course_pic);
			hold.courseNum = (TextView) convertView
					.findViewById(R.id.search_course_courseId);
			hold.courseName = (TextView) convertView
					.findViewById(R.id.search_course_courseName);
			hold.teacherNum = (TextView) convertView
					.findViewById(R.id.search_course_teacherId);
			hold.teacherName = (TextView) convertView
					.findViewById(R.id.search_course_teacherName);
			convertView.setTag(hold);
		} else {
			hold = (Holder) convertView.getTag();
		}
		Object b = convertView.getTag();

		hold.courseNum.setText("课程号:" + list.get(position).getCourse_id());
		hold.courseName.setText("课程名:" + list.get(position).getCourse_name());
		hold.teacherNum.setText("教师号:" + list.get(position).getTeacher_id());
		hold.teacherName.setText("教师名:" + list.get(position).getTeacher_name());

		// 设置监听
		hold.coursePic.setOnClickListener(new View.OnClickListener() {

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

		hold.coursePic.setImageResource(R.drawable.default_users_avatar);
		if (list.get(position).getCourse_name().equalsIgnoreCase("")) {
			hold.coursePic.setImageResource(R.drawable.default_users_avatar);
		} else {
			hold.coursePic.setTag(Model.USERHEADURL
					+ list.get(position).getCourse_name());
			Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.coursePic,
					Model.USERHEADURL + list.get(position).getCourse_name(),
					new ImageDownloadCallBack() {
						@Override
						public void onImageDownload(ImageView imageView,
								Bitmap bitmap) {
							if (position >= list.size()) {
								if (hold.coursePic.getTag().equals(
										Model.USERHEADURL
												+ list.get(position - 1)
														.getCourse_name())) {
									hold.coursePic.setImageBitmap(bitmap);
								}
							} else {
								if (hold.coursePic.getTag().equals(
										Model.USERHEADURL
												+ list.get(position)
														.getCourse_name())) {
									hold.coursePic.setImageBitmap(bitmap);
								}
							}
						}
					});
			if (bitTeacher != null) {
				hold.coursePic.setImageBitmap(bitTeacher);
			}
		}

		return convertView;

	}

	static class Holder {
		ImageView coursePic;
		TextView courseNum;
		TextView courseName;
		TextView teacherNum;
		TextView teacherName;
	}

}
