package cmusv.mr.carbon.utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharepreferenceHelper {
	private final String ACCOUNT = "account";
	private final String TOKEN = "token";

	private SharedPreferences mSettings;

	public SharepreferenceHelper(SharedPreferences p){
		mSettings = p;
	}
	
	/*
	 *  Setter 
	 */
	public void setUserAccount(String account){
		Editor editor = mSettings.edit();
		editor.putString(ACCOUNT, account);
		editor.commit();
	}

	public void setUserToken(String token){
		Editor editor = mSettings.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}
	
	/*
	 *  Getter
	 */
	public String getUserAccount(){
        return mSettings.getString(ACCOUNT, null);
	}
	
	public String getUserToken(){
		return mSettings.getString(TOKEN, null);
	}

	public void clearAll() {
		Editor editor = mSettings.edit();
		editor.clear();
		editor.commit();
	}


}
