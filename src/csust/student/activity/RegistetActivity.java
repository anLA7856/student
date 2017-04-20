package csust.student.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;

import csust.student.model.Model;
import csust.student.net.ThreadPoolUtils;
import csust.student.thread.HttpPostThread;
import csust.student.utils.CharacterUtil;
import csust.student.utils.FaceUtil;

public class RegistetActivity extends Activity implements OnClickListener {

	private final int REQUEST_PICTURE_CHOOSE = 1;
	private final int REQUEST_CAMERA_IMAGE = 2;

	private ImageView mClose;
	private RelativeLayout mDerectLogin;
	private RelativeLayout mNext;
	private EditText mName, mStuNum, mUsername, mPassword;
	private ImageView mPic;
	private RadioGroup mRadioGroup;
	private RadioButton mRadioMan, mRadioWoman;

	private String username, password, url, value, name, sex = "男", age,
			stuNum;
	private File mPictureFile = null;

	// FaceRequest对象，集成了人脸识别的各种功能
	private FaceRequest mFaceRequest;

	// 进度对话框
	private ProgressDialog mProDialog;

	private Toast mToast;

	private Bitmap mImage = null;
	private byte[] mImageData = null;

	private String imgUrl = null;

	// 定义判断变量，用于检测是否拍照
	private boolean isPhoto = false;
	// 定义判断变量，用于检测科大讯飞是否检测通过
	private boolean isPass = false;

	// 用于对科大讯飞模型的操作，在我们这里主要是删除

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		// 在程序入口处传入appid，初始化SDK
		SpeechUtility
				.createUtility(this, "appid=" + getString(R.string.app_id));
		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		initView();
		
		
		//暂时有不知名bug，总是在删除模型时候报引擎失败！先不用。
//		// 初始化科大讯飞引擎
//		// 身份验证对象初始化
//		mIdVerifier = IdentityVerifier.createVerifier(this, new InitListener() {
//
//			@Override
//			public void onInit(int errorCode) {
//				if (ErrorCode.SUCCESS == errorCode) {
//					Toast.makeText(RegistetActivity.this, "引擎初始化成功", 1).show();
//					showTip("引擎初始化成功");
//				} else {
//					showTip("引擎初始化失败，错误码：" + errorCode);
//				}
//			}
//		});
	}

	private void initView() {


		mClose = (ImageView) findViewById(R.id.RegiRegisterClose);
		mName = (EditText) findViewById(R.id.RegiMyName);
		mNext = (RelativeLayout) findViewById(R.id.next);

		mStuNum = (EditText) findViewById(R.id.RegiMyStuNum);
		mUsername = (EditText) findViewById(R.id.RegiMyUsername);
		mPassword = (EditText) findViewById(R.id.RegiMyPassword);
		mPic = (ImageView) findViewById(R.id.RegiCamera);
		mRadioGroup = (RadioGroup) findViewById(R.id.myRadioGroup);
		mRadioMan = (RadioButton) findViewById(R.id.myRadioMan);
		mRadioWoman = (RadioButton) findViewById(R.id.myRadioWoman);

		mClose.setOnClickListener(this);
		mNext.setOnClickListener(this);
		mPic.setOnClickListener(this);
		mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// 实际处理逻辑
				if (checkedId == R.id.myRadioMan) {
					sex = "男";
				} else if (checkedId == R.id.myRadioWoman) {
					sex = "女";
				}
			}
		});

		mProDialog = new ProgressDialog(this);
		// 一旦提交数据，
		mProDialog.setCancelable(true);
		mProDialog.setTitle("验证照片中...");

		mProDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// cancel进度框时,取消正在进行的操作
				if (null != mFaceRequest) {
					mFaceRequest.cancel();
				}


			}
		});

		mFaceRequest = new FaceRequest(this);

	}

	@Override
	public void onClick(View v) {
		int mId = v.getId();
		switch (mId) {

		case R.id.RegiCamera:
			String str = Model.LOCALSTORAGE + "signPic"
					+ System.currentTimeMillis() / 1000 + ".jpg";
			mPictureFile = new File(str);
			// urlPath = mPictureFile.getAbsolutePath();
			// ContentValues values = new ContentValues();
			// values.put(Media.TITLE, urlPath);
			// photoUri = getContentResolver().insert(
			// MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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

		case R.id.RegiRegisterClose:
			finish();
			break;
		case R.id.next:
			username = mUsername.getText().toString();
			password = mPassword.getText().toString();
			name = mName.getText().toString();
			stuNum = mStuNum.getText().toString();
			// 登录
			username = mUsername.getText().toString();
			password = mPassword.getText().toString();
			name = mName.getText().toString();

			if (username.equals("")) {
				Toast.makeText(RegistetActivity.this, "用户名不能为空！", 1).show();
				return;
			}
			if (password.equals("")) {
				Toast.makeText(RegistetActivity.this, "用户名不能为空！", 1).show();
				return;
			}
			if (name.equals("")) {
				Toast.makeText(RegistetActivity.this, "姓名不能为空！", 1).show();
				return;
			}
			if (stuNum.equals("")) {
				Toast.makeText(RegistetActivity.this, "用户学号不能为空！", 1).show();
				return;
			}

			// 中文英语都可以判定在8位内
			if (name.length() > 8) {
				Toast.makeText(RegistetActivity.this, "姓名长度超过8位", 1).show();
				return;
			}

			if (!username.matches("[a-zA-Z0-9]{5,12}")) {
				// 判定5~12位
				Toast.makeText(RegistetActivity.this, "用户名必须是5~12位的字母数字", 1)
						.show();
				return;
			}
			if (!stuNum.matches("[a-zA-Z0-9]{5,12}")) {
				// 判定5~12位
				Toast.makeText(RegistetActivity.this, "用户学号必须是5~12位的字母数字", 1)
						.show();
				return;
			}
			if (!password.matches("[a-zA-Z0-9]{6,12}")) {
				// 判定5~12位
				Toast.makeText(RegistetActivity.this, "密码必须是6~12位的字母数字", 1)
						.show();
				return;
			}

			// 检测姓名是否有非法字符。
			if (!new CharacterUtil().checkString(name)) {
				Toast.makeText(RegistetActivity.this, "名字中包含非法字符", 1).show();
				return;
			}

			if (isPhoto == false) {
				Toast.makeText(RegistetActivity.this, "请上传自己的头像。", 1).show();
				return;
			} else {
				registerKDXF();
				// 先检测科大讯飞是否检测成功！

			}

			break;

		}
	}

	private void registerKDXF() {

		if (null != mImageData) {
			mProDialog.setMessage("人脸注册中...");
			mProDialog.show();
			// 设置用户标识，格式为6-18个字符（由字母、数字、下划线组成，不得以数字开头，不能包含空格）。
			// 当不设置时，云端将使用用户设备的设备ID来标识终端用户。
			mFaceRequest.setParameter(SpeechConstant.AUTH_ID, username);
			mFaceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
			mFaceRequest.sendRequest(mImageData, new RequestListener() {

				@Override
				public void onEvent(int arg0, Bundle arg1) {

				}

				@Override
				public void onCompleted(SpeechError error) {
					if (null != mProDialog) {
						mProDialog.setMessage("人脸注册成功，用户名注册中。。");
					}

					if (error != null) {
						mProDialog.dismiss();
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
				public void onBufferReceived(byte[] buffer) {
					// if (null != mProDialog) {
					// mProDialog.dismiss();
					// }

					try {
						String result = new String(buffer, "utf-8");
						// Log.d("FaceDemo", result);

						JSONObject object = new JSONObject(result);
						String type = object.optString("sst");
						if ("reg".equals(type)) {
							register(object);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (JSONException e) {
					}
				}
			});
		} else {
			// 检测未通过。
			isPass = false;
			Toast.makeText(RegistetActivity.this, "请选择图片后再操作", 1).show();
		}

	}

	/**
	 * post方式传送
	 */
	private void myRegister() {
		mProDialog.setMessage("注册账号中...");
		mProDialog.show();
		url = Model.REGISTET;
		value = "{\"username\":\"" + username + "\",\"password\":\"" + password
				+ "\",\"name\":\"" + name + "\",\"sex\":\"" + sex
				+ "\",\"age\":\"" + age + "\",\"stuNum\":\"" + stuNum + "\"}";
		ThreadPoolUtils.execute(new HttpPostThread(hand, url, value, username
				+ ".jpg", imgUrl));
	}

	Handler hand = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (msg.what == 404) {
				Toast.makeText(RegistetActivity.this, "请求失败，服务器故障", 1).show();
			} else if (msg.what == 100) {
				Toast.makeText(RegistetActivity.this, "服务器无响应", 1).show();
			} else if (msg.what == 200) {
				mProDialog.dismiss();
				String result = (String) msg.obj;
				// Log.e("anla", "result:" + result);
				if (result.equals("ok")) {
					Toast.makeText(RegistetActivity.this, "用户名注册成功,请登陆", 1)
							.show();
					Intent intent = new Intent();
					intent.putExtra("NameValue", username);
					intent.putExtra("PasswordValue", password);
					setResult(2, intent);
					finish();
				} else if (result.trim().equals("no")) {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(RegistetActivity.this, "用户名已存在,请重新注册", 1)
							.show();

					//有bug，先暂时不实现。
					// 此时能走到这一步，说明人脸注册成功，但是用户名却没有成功，所以需要把人脸模型删除。
//					executeModelCommand("delete");
					return;
				} else {
					mName.setText("");
					mPassword.setText("");
					Toast.makeText(RegistetActivity.this, "服务器原因注册失败", 1)
							.show();
					//先暂时不实现
//					executeModelCommand("delete");
					return;
				}

			}
		};
	};

	private void showTip(final String str) {
		new AlertDialog.Builder(this).setTitle("提示").setMessage(str)
				.setPositiveButton("确定", null).show();
		// mToast.setText(str);
		// mToast.show();
	}

	private void register(JSONObject obj) throws JSONException {
		// Log.i("register", obj.toString());
		int ret = obj.getInt("ret");
		if (ret != 0) {
			mProDialog.dismiss();
			showTip("人脸注册失败");
			return;
		}
		if ("success".equals(obj.get("rst"))) {
			mProDialog.setTitle("人脸注册成功");
			// showTip("人脸注册成功");
			RegistetActivity.this.myRegister();

		} else {
			mProDialog.dismiss();
			showTip("注册失败");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		String fileSrc = null;
		if (requestCode == REQUEST_CAMERA_IMAGE) {

			if (null == mPictureFile) {
				Toast.makeText(this, "拍照失败，请重试", 1).show();
				// showTip("拍照失败，请重试");
				return;
			}
			isPhoto = true;
			fileSrc = mPictureFile.getAbsolutePath();
			// fileSrc = urlPath;
			imgUrl = fileSrc;
			updateGallery(fileSrc);
			Uri u = Uri.fromFile(new File(fileSrc));
			// 跳转到图片裁剪页面
			FaceUtil.cropPicture(this, Uri.fromFile(new File(fileSrc)));
		} else if (requestCode == FaceUtil.REQUEST_CROP_IMAGE) {
			// 获取返回数据
			Bitmap bmp = data.getParcelableExtra("data");
			// 若返回数据不为null，保存至本地，防止裁剪时未能正常保存
			if (null != bmp) {
				FaceUtil.saveBitmapToFile(RegistetActivity.this, bmp);
			}
			// 获取图片保存路径
			fileSrc = FaceUtil.getImagePath(RegistetActivity.this);
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

			((ImageView) findViewById(R.id.RegiCamera)).setImageBitmap(mImage);
		}

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
	public void finish() {
		if (null != mProDialog) {
			mProDialog.dismiss();
		}
		super.finish();
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

//	/**
//	 * 用于删除模型的操作。
//	 * 
//	 * @param cmd
//	 */
//	private void executeModelCommand(String cmd) {
//		// 设置人脸模型操作参数
//		// 清空参数
//		mIdVerifier.setParameter(SpeechConstant.PARAMS, null);
//		// 设置特征场景，用来说明本次验证将涉及的业务
//		mIdVerifier.setParameter(SpeechConstant.MFV_SCENES, "ifr");
//		// 用户的唯一标识，在人脸业务获取注册、验证和删除模型时都要填写，不能为空
//		mIdVerifier.setParameter(SpeechConstant.AUTH_ID, username);
//
//		// 设置模型参数，若无可以传空字符传
//		StringBuffer params = new StringBuffer();
//		// 执行模型操作
//		mIdVerifier.execute("ifr", cmd, params.toString(),
//				new IdentityListener() {
//
//					@Override
//					public void onResult(IdentityResult result, boolean islast) {
//						JSONObject jsonResult = null;
//						int ret = ErrorCode.SUCCESS;
//						try {
//							jsonResult = new JSONObject(result
//									.getResultString());
//							ret = jsonResult.getInt("ret");
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
//						// 根据操作类型判断结果类型
//						if (ErrorCode.SUCCESS == ret) {
//							showTip("未注册成功，人脸删除成功");
//						} else {
//							showTip("未注册成功，人脸删除失败");
//						}
//					}
//
//					@Override
//					public void onEvent(int eventType, int arg1, int arg2,
//							Bundle obj) {
//
//					}
//
//					@SuppressLint("ShowToast")
//					@Override
//					public void onError(SpeechError error) {
//						// 弹出错误信息
//						Toast.makeText(RegistetActivity.this,error.getPlainDescription(true) , 1).show();
//					
//					}
//				});
//	}

}
