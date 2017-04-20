package csust.student.thread;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.client.ClientProtocolException;

import csust.student.model.Model;
import csust.student.net.MyPost;
import csust.student.utils.UploadPic;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 网络post请求的线程
 * @author U-anLA
 *
 */
public class HttpPostThread implements Runnable{

	private Handler hand;
	private String url;
	private String value;
	private String img = "";
	private MyPost myPost = new MyPost();
	private String newName,uploadFile;
	
	/**
	 * 构造方法，有img的
	 * @param hand
	 * @param endParamerse
	 * @param value
	 * @param img
	 */
	public HttpPostThread(Handler hand, String endParamerse, String value,
			String img) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = endParamerse;
		this.value = value;
		this.img = img;
	}
	
	public HttpPostThread(Handler hand, String endParamerse, String value) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = endParamerse;
		this.value = value;
	}
	
	public HttpPostThread(Handler hand, String endParamerse, String value,String newName,String uploadFile) {
		this.hand = hand;
		// 拼接访问服务器完整的地址
		url = endParamerse;
		this.value = value;
		this.newName = newName;
		this.uploadFile = uploadFile;
	}

	@Override
	public void run() {
		//获取我们回调主ui的message
		Message msg = hand.obtainMessage();
		String result = null;
		
		
		
		
		
		


		try {
			if(img.equalsIgnoreCase("")){
				result = myPost.doPost(url, value);
				//Log.i("myresult", result);
				myUploadPic(Model.UPLOADPIC, newName, uploadFile);
			}else{
				result = myPost.doPost(url, img,value);
			}
			msg.what = 200;
			msg.obj = result;
		} catch (ClientProtocolException e) {
			msg.what = 404;
		} catch(IOException e){
			msg.what = 100;
		} catch (Exception e) {
			msg.what = 404;
		}
		//给主ui发送信息传递数据
		hand.sendMessage(msg);
		
		
		
		
	}
	
	
	
	public static void myUploadPic(String actionUrl, String newName,
			String uploadFile) {

		/* 上传文件至Server的方法 */

		String end = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";
		try {
			URL url = new URL(Model.HTTPURL+actionUrl);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			/* 允许Input、Output，不使用Cache */
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			/* 设置传送的method=POST */
			con.setRequestMethod("POST");
			/* setRequestProperty */
			con.setRequestProperty("Connection", "Keep-Alive");
			con.setRequestProperty("Charset", "UTF-8");
			con.setRequestProperty("Content-Type",
					"multipart/form-data;boundary=" + boundary);
			/* 设置DataOutputStream */
			DataOutputStream ds = new DataOutputStream(con.getOutputStream());
			ds.writeBytes(twoHyphens + boundary + end);
			ds.writeBytes("Content-Disposition: form-data; "
					+ "name=\"file1\";filename=\"" + newName + "\"" + end);
			ds.writeBytes(end);

			/* 取得文件的FileInputStream */
			FileInputStream fStream = new FileInputStream(uploadFile);
			/* 设置每次写入1024bytes */
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];

			int length = -1;
			/* 从文件读取数据至缓冲区 */
			while ((length = fStream.read(buffer)) != -1) {
				/* 将资料写入DataOutputStream中 */
				ds.write(buffer, 0, length);
			}
			ds.writeBytes(end);
			ds.writeBytes(twoHyphens + boundary + twoHyphens + end);

			/* close streams */
			fStream.close();
			ds.flush();
			/* 取得Response内容 */
			InputStream is = con.getInputStream();
			int ch;
			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			/* 将Response显示于Dialog */
			//showDialog(b.toString().trim());
			/* 关闭DataOutputStream */
			ds.close();


		} catch (Exception e) {
			// showDialog("" + e);
			e.printStackTrace();
		}
	}
	
	
	
	
	
}
