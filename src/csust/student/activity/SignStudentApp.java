package csust.student.activity;

import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.RemoteViews;

import com.iflytek.cloud.SpeechUtility;

import csust.student.database.MessageDB;
import csust.student.database.RecentDB;
import csust.student.database.UserDB;
import csust.student.utils.SharePreferenceUtil;

public class SignStudentApp extends Application {
	
	@Override
	public void onCreate() {
		// 应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
		// 如在Application中调用初始化，需要在Mainifest中注册该Applicaiton
		// 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
		// 参数间使用半角“,”分隔。
		// 设置你申请的应用appid,请勿在'='与appid之间添加空格及空转义符
		
		// 注意： appid 必须和下载的SDK保持一致，否则会出现10407错误
		
	
		// 以下语句用于设置日志开关（默认开启），设置成false时关闭语音云SDK日志打印
		// Setting.setShowLog(false);
		super.onCreate();
		SpeechUtility.createUtility(SignStudentApp.this, "appid=" + getString(R.string.app_id));
		 mApplication = this;
	    
	        initData();
	}
	
	
	
	
    public final static String SECRIT_KEY = "PTtP7I1EfBnRMu0LimV8fRSIso0dZtgA";
    public static final String SP_FILE_NAME = "push_msg_sp";
    // ======头像===
    public static final int[] heads = { R.drawable.h0 };
    public static final int NUM_PAGE = 6;// 总共有多少页
    public static int NUM = 20;// 每页20个表情,还有最后一个删除button
    private static SignStudentApp mApplication;
   // private BaiduPush mBaiduPushServer;
    private Map<String, Integer> mFaceMap = new LinkedHashMap<String, Integer>();
    private SharePreferenceUtil mSpUtil;
    private UserDB mUserDB;
    private MessageDB mMsgDB;
    private RecentDB mRecentDB;
    // private List<User> mUserList;
    private MediaPlayer mMediaPlayer;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
   // private Gson mGson;

    public synchronized static SignStudentApp getInstance() {
        return mApplication;
    }


    private void initData() {
//        mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
//                SECRIT_KEY, API_KEY);
        // 不转换没有 @Expose 注解的字段
//        mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//                .create();
//        mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
        mUserDB = new UserDB(this);
        mMsgDB = new MessageDB(this);
        mRecentDB = new RecentDB(this);
        // mUserList = mUserDB.getUser();
//        mMediaPlayer = MediaPlayer.create(this, R.raw.office);
        mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
    }

    public synchronized UserDB getUserDB() {
        if (mUserDB == null)
            mUserDB = new UserDB(this);
        return mUserDB;
    }

//    public synchronized BaiduPush getBaiduPush() {
//        if (mBaiduPushServer == null)
//            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
//                    SECRIT_KEY, API_KEY);
//        return mBaiduPushServer;
//
//    }

//    public synchronized Gson getGson() {
//        if (mGson == null)
//            // 不转换没有 @Expose 注解的字段
//            mGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//                    .create();
//        return mGson;
//    }

    public synchronized MessageDB getMessageDB() {
        if (mMsgDB == null)
            mMsgDB = new MessageDB(this);
        return mMsgDB;
    }

    public synchronized RecentDB getRecentDB() {
        if (mRecentDB == null)
            mRecentDB = new RecentDB(this);
        return mRecentDB;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

//    public synchronized MediaPlayer getMediaPlayer() {
//        if (mMediaPlayer == null)
//            mMediaPlayer = MediaPlayer.create(this, R.raw.office);
//        return mMediaPlayer;
//    }
//
//    public synchronized SharePreferenceUtil getSpUtil() {
//        if (mSpUtil == null)
//            mSpUtil = new SharePreferenceUtil(this, SP_FILE_NAME);
//        return mSpUtil;
//    }

    public Map<String, Integer> getFaceMap() {
        if (!mFaceMap.isEmpty())
            return mFaceMap;
        return null;
    }

    /**
     * 创建挂机图标
     */
    @SuppressWarnings("deprecation")
    public void showNotification() {
        if (!mSpUtil.getMsgNotify())// 如果用户设置不显示挂机图标，直接返回
            return;

        int icon = R.drawable.notify_general;
        CharSequence tickerText = getResources().getString(
                R.string.app_is_run_background);
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;

        RemoteViews contentView = new RemoteViews(getPackageName(),
                R.layout.notify_status_bar_latest_event_view);
        contentView.setImageViewResource(R.id.icon,
                heads[mSpUtil.getHeadIcon()]);
        contentView.setTextViewText(R.id.title, mSpUtil.getNick());
        contentView.setTextViewText(R.id.text, tickerText);
        contentView.setLong(R.id.time, "setTime", when);
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;

        mNotificationManager.notify(0x000,
                mNotification);
    }



}
