package csust.student.utils;

import java.util.List;

import android.content.Context;
import android.widget.Toast;

/**
 * 用于检测非法字符输入的
 * 
 * @author U-ANLA
 *
 */
public class CharacterUtil {

	public boolean checkString(String str) {
		if (str == null) {
			return false;
		}
		char[] myInput = str.toCharArray();
		
		char[] type={',','&','<','>','\'','/','%','!','~','`','\\'};
//		String type = ",~`!@#$%^&*'\"-";
		


		for(int i = 0;i < type.length;i++){
			for(int j = 0;j < myInput.length;j++){
				if(myInput[j] == type[i]){
					//包含非法字符
					return false;
				}
			}
		}

		return true;
	}
	
	
	/**
	 * 用于检测密码是否符合要求
	 * @param ctx
	 * @param password 都是trim完过来的。
	 * @return
	 */
	public boolean checkPasswordInput(Context ctx,String password){
		if(checkString(password) == false){
			Toast.makeText(ctx, "包括非法字符", 1).show();
			return false;
		}
		
		if(password.equals("")){
			Toast.makeText(ctx, "密码不能为空", 1).show();
			return false;
		}
		
		if(password.length()<6 || password.length() > 12){
			Toast.makeText(ctx, "密码必须为6~12位", 1).show();
			return false;
		}
		return true;
		
		
	}
}
