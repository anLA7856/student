package csust.student.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import csust.student.activity.R;
import csust.student.adapter.MySignListAdapter.SignHolder;
import csust.student.info.SignInfo;
import csust.student.info.TeacherListInfo;

public class MyTeacherListAdapter extends BaseAdapter {
	private List<TeacherListInfo> list;
	private Context ctx;

	public MyTeacherListAdapter(Context ctx, List<TeacherListInfo> list) {
		this.list = list;
		this.ctx = ctx;

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
		final SignHolder hold;

		if (convertView == null) {
			hold = new SignHolder();
			convertView = View.inflate(ctx,
					R.layout.mylistview_item_teacher_list, null);

			hold.teacherName = (TextView) convertView
					.findViewById(R.id.teacher_list_teacher_name);
			hold.teacherUsername = (TextView) convertView
					.findViewById(R.id.teacher_list_teacher_username);

			convertView.setTag(hold);
		} else {
			hold = (SignHolder) convertView.getTag();
		}

		hold.teacherName
				.setText("教师名字：" + list.get(position).getTeacher_name());
		hold.teacherUsername.setText("教师号："
				+ list.get(position).getTeacher_username());

		return convertView;

	}

	static class SignHolder {
		ImageView teacherPic;

		TextView teacherUsername;
		TextView teacherName;

	}
}
