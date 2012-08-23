package cmusv.mr.carbon;

import org.json.JSONObject;

import cmusv.mr.carbon.io.sendToServer.ClientHelper;
import cmusv.mr.carbon.utils.ShareTools;
import cmusv.mr.carbon.utils.SharepreferenceHelper;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	private final String TAG = LoginActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		Button loginBT = (Button) findViewById(R.id.btnLogin);
		loginBT.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Switching to Register screen
				if (!ShareTools.isInternetConnected(getApplicationContext())) {
					Toast.makeText(
							getApplicationContext(),
							"There is no connection to the internet.\nThe register process need internet access, please try again later.",
							Toast.LENGTH_SHORT).show();
					return;
				}
				SharedPreferences settings = getSharedPreferences("account",
						MODE_PRIVATE);
				final SharepreferenceHelper preferenceHelper = new SharepreferenceHelper(
						settings);
				final EditText username = (EditText) findViewById(R.id.username);
				EditText password = (EditText) findViewById(R.id.password);
				final String usernameStr = username.getText().toString();
				final String passwordStr = password.getText().toString();
				
				Toast.makeText(getApplicationContext(), "Login...",	Toast.LENGTH_SHORT).show();
				
				new Thread(new Runnable() {
					@Override
					public void run() {
						
						try {
							ClientHelper clientHelper = new ClientHelper(usernameStr,
									passwordStr);
							JSONObject jsonMessage = clientHelper.register(usernameStr,	passwordStr);
							Log.d(TAG, jsonMessage.toString());
							Log.d(TAG, "token = " + jsonMessage.getString("token"));
							Log.d(TAG, username.getText().toString());

							preferenceHelper.setUserAccount(username.getText()
									.toString());
							preferenceHelper.setUserToken(jsonMessage.getString("token"));
							finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							Toast.makeText(getApplicationContext(), "Login failed",	Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}
				}).start();


				// finish();
			}
		});

	}
}