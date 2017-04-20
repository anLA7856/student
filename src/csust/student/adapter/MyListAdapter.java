package csust.student.adapter;

import java.util.List;

import csust.student.activity.R;
import csust.student.info.CourseInfo;
import csust.student.model.Model;
import csust.student.utils.LoadImg;
import csust.student.utils.LoadImg.ImageDownloadCallBack;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * mylistview的适配器
 * @author U-anLA
 *
 */
public class MyListAdapter extends BaseAdapter{
	
	private List<CourseInfo> list;
	private Context ctx;
	private LoadImg loadImgHeadImg;
	private boolean upFlag = false;
	private boolean downFlag = false;
	
	

	public MyListAdapter(Context ctx, List<CourseInfo> list) {
		this.list = list;
		this.ctx = ctx;
		//加载图像
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
		
		
		if(convertView == null){
			hold = new Holder();
			convertView = View.inflate(ctx, R.layout.mylistview_item, null);
			hold.teacherPic = (ImageView) convertView.findViewById(R.id.itemTeacherPic);
			hold.teacherNum = (TextView) convertView.findViewById(R.id.itemTeacherNum);
			hold.courseName = (TextView) convertView.findViewById(R.id.itemCourseName);
			hold.teacherName = (TextView) convertView.findViewById(R.id.itemTeacherName);
			convertView.setTag(hold);
		}else{
			hold = (Holder) convertView.getTag();
		}
		Object b = convertView.getTag();

		hold.teacherNum.setText("教师号:"+list.get(position).getTeacherNum());
		
		hold.teacherName.setText("教师名:"+list.get(position).getTeacherName());
		hold.courseName.setText("课程名:"+list.get(position).getCourseName());
		//设置监听
		hold.teacherPic.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//用于和教师会话~，后期实现。
				if(Model.MYUSERINFO != null){
					//已登录
				}else{
					Toast.makeText(ctx, "请先登录才能发送消息哦", 1).show();
				}
			}
		});
		
		hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
		if(list.get(position).getTeacherNum().equalsIgnoreCase("")){
			hold.teacherPic.setImageResource(R.drawable.default_users_avatar);
		}else{
			hold.teacherPic.setTag(Model.USERHEADURL + list.get(position).getTeacherNum());
			Bitmap bitTeacher = loadImgHeadImg.loadImage(hold.teacherPic, Model.USERHEADURL+list.get(position).getTeacherNum(), new ImageDownloadCallBack() {
				@Override
				public void onImageDownload(ImageView imageView, Bitmap bitmap) {
					if(position >= list.size()){
						if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position-1).getTeacherNum())){
							hold.teacherPic.setImageBitmap(bitmap);
						}
					}else{
						if(hold.teacherPic.getTag().equals(Model.USERHEADURL+list.get(position).getTeacherNum())){
							hold.teacherPic.setImageBitmap(bitmap);
						}
					}
				}
			});
			if(bitTeacher != null){
				hold.teacherPic.setImageBitmap(bitTeacher);
			}
		}
		
		return convertView;
		
	}
	
	static class Holder{
		ImageView teacherPic;
		TextView teacherNum;
		TextView courseName;
		TextView teacherName;
	}
	
}
