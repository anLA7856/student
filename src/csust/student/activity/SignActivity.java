package csust.student.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

import csust.student.info.SignInfo;
import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpGetThread;
import csust.student.utils.FaceUtil;
import csust.student.utils.WifiAdmin;

public class SignActivity extends Activity implements OnClickListener {

	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private Bitmap mImage = null;
	private byte[] mImageData = null;
	// authid为6-18字符长度，用于唯一标识用户
	private String mAuthid = null;
	private Toast mToast;
	// 进度对话框
	private ProgressDialog mProDialog;
	// 拍照得到照片文件
	private File mPictureFile;
	// faceRequest对象，集成了人脸识别的各种功能
	private FaceRequest mFaceRequest;
	private String urlPath = new String();
	private Uri photoUri;
	private TextView mClassName;
	private ImageView mClose;

	// 用来保存当前教师端的wifimac地址
	private String wifiMac = null;
	// 用于接收前一个页面的signinfo
	private SignInfo signInfo = null;
	
	//用于存储扫描到的wifilist列表，防止缓存
	List<String> macList = null;
	
	
	//用来上传签到成功结果的url
	private String url = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sign);

		// 在程序入口处传入appid，初始化sdk
		SpeechUtility
				.createUtility(this, "appid=" + getString(R.string.app_id));
		findViewById(R.id.sign_camera).setOnClickListener(SignActivity.this);
		((TextView) findViewById(R.id.sign_authid)).setText("姓名："
				+ Model.MYUSERINFO.getStudent_name());
		findViewById(R.id.sign_do).setOnClickListener(SignActivity.this);

		mProDialog = new ProgressDialog(this);
		mProDialog.setCancelable(true);
		mProDialog.setTitle("请稍后，正在签到");

		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel进度框时，取消正在进行的操作
				if (null != mFaceRequest) {
					mFaceRequest.cancel();
				}
			}
		});

		mClassName = (TextView) findViewById(R.id.sign_className);
		mClose = (ImageView) findViewById(R.id.sign_close);
		mClose.setOnClickListener(this);
		mFaceRequest = new FaceRequest(this);
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();

		signInfo = (SignInfo) bundle.getSerializable("courseInfo");
		// SignInfo signInfo = (SignInfo) map.get("courseInfo");
		if (signInfo != null) {
			mClassName.setText("课程名称："+signInfo.getSign_courseName().toString());
			wifiMac = signInfo.getTeacher_wifimac().toString();
		}
		
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}

	private void verify(JSONObject obj) {
		Log.i("verify", obj.toString());
		try {
			int ret = obj.getInt("ret");
			if (ret != 0) {
				showTip("签到失败！");
				return;
			}
			if ("success".equals(obj.get("rst"))) {
				if (obj.getBoolean("verf")) {
					Date now = new Date();
					String date = (now.getYear()+1900)+"-"+now.getMonth()+"-"+now.getDay();
					String time = now.getHours()+":"+now.getMinutes()+":00";
					// 签到成功了，这里就需要给数据库插入了。
					url = Model.UPLOADSIGNINFO + "student_id=" + Model.MYUSERINFO.getStudent_id()+"&course_id="+signInfo.getSign_courseNum()+"&date="+date+"&time="+time+"&allow_id="+signInfo.getAlow_sign_id();
					ThreadPoolUtils.execute(new HttpGetThread(hand, url));
					//showTip("签到成功");
					finish();
				} else {
					showTip("签到失败");
				}
			}
		} catch (Exception e) {
			Log.e("verify", "verify exception");
			e.printStackTrace();
		}
	}
	
	//用于处理签到成功的handler回调
	Handler hand = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 404) {
				Toast.makeText(SignActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(SignActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				//成功的的回调处理逻辑
				String result = msg.obj.toString();
				if(result.equals("[1]")){
					showTip("签到成功！！！");
				}else{
					showTip("签到失败！！！");
				}
			}
		}
	};
	
	
	
	

	private void showTip(String string) {
		mToast.setText(string);
		mToast.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 选择拍照
		case R.id.sign_camera:
			mPictureFile = new File(Model.LOCALSTORAGE + "signPic"
					+ System.currentTimeMillis() / 1000 + ".jpg");
			urlPath = mPictureFile.getAbsolutePath();
			ContentValues values = new ContentValues();
			values.put(Media.TITLE, urlPath);
			photoUri = getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

			// Log.i("mPicturefile", Environment.getExternalStorageDirectory()
			// .toString());
			// Log.i("mpicturefiel111", "picture" + System.currentTimeMillis()
			// / 1000 + ".jpg");
			// 启动拍照,并保存到临时文件
			Intent mIntent = new Intent();
			mIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
			mIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mPictureFile));
			// mIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			mIntent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			startActivityForResult(mIntent, REQUEST_CAMERA_IMAGE);
			break;
		// 签到
		case R.id.sign_do:
			mAuthid = Model.MYUSERINFO.getStudent_username();
			if (TextUtils.isEmpty(mAuthid)) {
				showTip("anthid不能为空");
				return;
			}
			if (null != mImageData) {
				// 这里判断下是否在教师端的wifimac地址范围内。
//				if (wifiIsOk()) {
				if (wifiIsOk()) {
					mProDialog.setMessage("签到中...");
					mProDialog.show();
					// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
					// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
					mFaceRequest.setParameter(SpeechConstant.AUTH_ID, mAuthid);
					mFaceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
					mFaceRequest.sendRequest(mImageData, mRequestListener);
				}else{
					showTip("请确定在范围内！！！！");
				}


			} else {
				showTip("请拍照后再验证！");
			}
			break;

		case R.id.sign_close:
			finish();
			break;

		default:
			break;
		}
	}

	/**
	 * 判断wifimac是否在教师的热点内部。
	 * 
	 * @return
	 */
	private boolean wifiIsOk() {
		//清楚缓存
		if(macList != null){
			macList.removeAll(macList);
		}
		WifiAdmin wifiAdmin = new WifiAdmin(this);
		//开始扫描一遍
		wifiAdmin.startScan();

		
		// 首先打开wifi
		wifiAdmin.openWifi();
		// 获得wifi的list列表
		List<ScanResult> list = wifiAdmin.getWifiList();
		
		
//		Toast.makeText(SignActivity.this, list+"", 1).show();
//		
//		Toast.makeText(SignActivity.this, wifiMac, 1).show();
		if (list == null) {
			return false;
		}
		macList = new ArrayList<String>(list.size());
		for (int i = 0; i < list.size(); i++) {
			String temp = list.get(i).BSSID.substring(3,list.get(i).BSSID.length());
			macList.add(temp);
			
		}
		
		if (wifiMac == null) {
			return false;
		}
		
//		new AlertDialog.Builder(this).setTitle("提示").setMessage(macList.toString())
//		.setPositiveButton("确定", null).show();
//		new AlertDialog.Builder(this).setTitle("提示2").setMessage(wifiMac)
//		.setPositiveButton("确定", null).show();
//		
//		
		
		//可能获得的是小写的
		String myUpperWifiMac = wifiMac.toLowerCase();
		String subStringMyUpperWifiMac = myUpperWifiMac.substring(3, myUpperWifiMac.length());
		String subStringWifiMac = wifiMac.substring(3,wifiMac.length());
		if (macList.contains(subStringMyUpperWifiMac)||macList.contains(subStringWifiMac)) {
			
			return true;
		} else {
			return false;
		}

	}

	private RequestListener mRequestListener = new RequestListener() {

		@Override
		public void onEvent(int arg0, Bundle arg1) {

		}

		@Override
		public void onCompleted(SpeechError error) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}
			if (error != null) {
				switch (error.getErrorCode()) {
				case ErrorCode.MSP_ERROR_ALREADY_EXIST:
					showTip("authid已经被注册，请更换后再试");
					break;

				default:
					showTip(error.getPlainDescription(true));
					break;
				}
			}
		}

		@Override
		public void onBufferReceived(byte[] arg0) {
			if (null != mProDialog) {
				mProDialog.dismiss();
			}

			try {
				String result = new String(arg0, "utf-8");
				Log.d("FaceDemo", result);
				JSONObject object = new JSONObject(result);
				String type = object.optString("sst");
				if ("verify".equals(type)) {
					verify(object);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		String fileSrc = null;

		if (resultCode != RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_CAMERA_IMAGE) {
			if (null == mPictureFile) {
				showTip("拍照失败，请重试！");
				return;
			}
			fileSrc = mPictureFile.getAbsolutePath();
			updateGallery(fileSrc);
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获得返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止剪裁时未能正常保存
			if (null != bmp) {
				FaceUtil.saveBitmapToFile(SignActivity.this, bmp);

			}
			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(SignActivity.this);
			// 获取图片的宽和高
			Options options = new Options();
			options.inJustDecodeBounds = true;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			// 压缩图片
			options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
					(double) options.outWidth / 1024f,
					(double) options.outHeight / 1024f)));
			options.inJustDecodeBounds = false;
			mImage = BitmapFactory.decodeFile(fileSrc, options);

			// 若mImageBitmap为空则图片信息不能正常获取
			if (null == mImage) {
				showTip("图片信息无法正常获取！");
				return;
			}

			// 部分手机会对图片做旋转，这里检测旋转角度
			int degree = FaceUtil.readPictureDegree(fileSrc);
			if (degree != 0) {
				// 把图片旋转为正的方向
				mImage = FaceUtil.rotateImage(degree, mImage);
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			// 可根据流量及网络状况对图片进行压缩
			mImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);
			mImageData = baos.toByteArray();

			((ImageView) findViewById(R.id.sign_img)).setImageBitmap(mImage);

		}

	}

	@Override
	public void finish() {
		if (null != mProDialog) {
			mProDialog.dismiss();
		}
		super.finish();
	}

	private void updateGallery(String filename) {
		MediaScannerConnection.scanFile(this, new String[] { filename }, null,
				new MediaScannerConnection.OnScanCompletedListener() {

					@Override
					public void onScanCompleted(String path, Uri uri) {

					}
				});
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("UserInfoActivity", "onConfigurationChanged");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("UserInfoActivity", "横屏");
			Configuration o = newConfig;
			o.orientation = Configuration.ORIENTATION_PORTRAIT;
			newConfig.setTo(o);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("UserInfoActivity", "竖屏");
		}
		super.onConfigurationChanged(newConfig);
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// Log.i("UserInfoActivity", "onConfigurationChanged");
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	// Log.i("UserInfoActivity", "横屏");
	// Configuration o = newConfig;
	// o.orientation = Configuration.ORIENTATION_PORTRAIT;
	// newConfig.setTo(o);
	// } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
	// Log.i("UserInfoActivity", "竖屏");
	// }
	// super.onConfigurationChanged(newConfig);
	// }

}
