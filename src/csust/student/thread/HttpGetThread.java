package csust.student.thread;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import csust.student.model.Model;
import csust.student.net.MyGet;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class HttpGetThread implements Runnable {
	private Handler hand;
	private String url;
	private MyGet myGet = new MyGet();
	
	public HttpGetThread(Handler hand,String endParamerse){
		this.hand = hand;
		//拼接访问服务器完整的地址
		url = Model.HTTPURL + endParamerse;
	}
	
	
	
	@Override
	public void run() {
		//获取我们回调主ui的message
		Message msg = hand.obtainMessage();
		Log.e("httpGetThread", url);
		try {
			String result = myGet.doGet(url);
			msg.what = 200;
			msg.obj = result;
		} catch (ClientProtocolException e) {
			msg.what = 404;
		} catch(IOException e){
			msg.what = 100;
		}
		//把消息传回给主线程
		hand.sendMessage(msg);
	}
}
