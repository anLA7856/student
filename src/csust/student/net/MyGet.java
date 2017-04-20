package csust.student.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

public class MyGet {
	public String doGet(String url) throws ClientProtocolException, IOException{
		String result = null; // 我们网络交互返回值
		HttpGet myGet = new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient();
		//此处问题暂未解决，为啥要替代defaulthttpclient
		//HttpClient httpClient = new httpclientb
		//httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5*1000);
		//RequestConfig.custom().setConnectTimeout(5*1000);
		httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5*1000);
		//RequestConfig.custom().setSocketTimeout(30*1000);
		httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 30*1000);
		HttpResponse httpResponse = httpClient.execute(myGet);
		if(httpResponse.getStatusLine().getStatusCode() == 200){
			result = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
		}
		return result;
	}
}
