package csust.student.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import csust.student.model.Model;

public class SettingActivity extends Activity implements OnClickListener{
	
	private ImageView mClose;
	private RelativeLayout mRemoveCach,mAdvice,mNewEdition,mAbout;
	private CheckBox mNotice;
	private TextView mCach;
	
	private String cahePath = null;
    private ProgressDialog mProDialog;
	
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		init();
	}



	private void init() {
		cahePath = Model.LOCALSTORAGE;
		mClose = (ImageView) findViewById(R.id.setting_back);
		mRemoveCach = (RelativeLayout) findViewById(R.id.setting_removecahe);
		mAdvice = (RelativeLayout) findViewById(R.id.setting_yijianfankui);
		mNewEdition = (RelativeLayout) findViewById(R.id.setting_banbenjiance);
		mAbout = (RelativeLayout) findViewById(R.id.setting_guanyuqiubai);
		mNotice = (CheckBox) findViewById(R.id.setting_tongzhi_checkbox);
		mCach = (TextView) findViewById(R.id.setting_cahe);
		
		mClose.setOnClickListener(this);
		mRemoveCach.setOnClickListener(this);
		mAdvice.setOnClickListener(this);
		mNewEdition.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		mNotice.setOnClickListener(this);
		
		mCach.setText(getTotalSizeOfFilesInDir(new File(cahePath))/1000000+"M");
		
		
		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(false);
		mProDialog.setTitle("请稍后，正在清除缓存");


	}



	@SuppressLint("ShowToast")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_back:
			finish();
			break;
		case R.id.setting_removecahe:
			deleteCahe();
			break;
		case R.id.setting_yijianfankui:
			Toast.makeText(this, "请联系qq：350493391", 1).show();
			break;
		case R.id.setting_guanyuqiubai:
			Toast.makeText(this, "A...N...L...A", 1).show();
			break;
		case R.id.setting_banbenjiance:
			Toast.makeText(this, "版本更新待加入哈..", 1).show();
			break;
		case R.id.setting_tongzhi_checkbox:
			Toast.makeText(this, "实时通知功能待加入哈..", 1).show();
			break;
				
		default:
			break;
		}
	}
	/**
	 * 用于删除缓存
	 */
	private void deleteCahe(){
		new AlertDialog.Builder(this).setTitle("删除提示框").setMessage("确认清除缓存(主要是签到时的照片)")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@SuppressLint("ShowToast")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mProDialog.show();
				deleteFile(new File(cahePath));
				mProDialog.dismiss();
				mCach.setText(getTotalSizeOfFilesInDir(new File(cahePath))/1000000+"M");
			}
		}).setNegativeButton("取消", null).show();
	}
	
	/**
	 * 获得缓存目录文件大小
	 * @param file
	 * @return
	 */
	private long getTotalSizeOfFilesInDir(final File file){
		if(file.isFile()){
			return file.length();
		}
		
		final File[] children = file.listFiles();
		long total = 0;
		if(children != null){
			for(final File child : children){
				total += getTotalSizeOfFilesInDir(child);
			}
		}
		return total;
	}
	
	private void deleteFile(final File file){
		if(file.isFile()){
			file.delete();
			return ;
		}
		final File[] children = file.listFiles();
		if(children != null){
			for(final File child : children){
				deleteFile(child);
			}
		}
	}
	
	
}




