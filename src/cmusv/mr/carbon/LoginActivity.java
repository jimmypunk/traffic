package cmusv.mr.carbon;

import cmusv.mr.carbon.utils.SharepreferenceHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button loginBT = (Button) findViewById(R.id.btnLogin);
        loginBT.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// Switching to Register screen
				SharedPreferences settings = getSharedPreferences("account", MODE_PRIVATE);
				SharepreferenceHelper preferenceHelper = new SharepreferenceHelper(settings);
				EditText username = (EditText)findViewById(R.id.username);
				EditText password = (EditText)findViewById(R.id.password);
				Log.d("LoginActivity",username.getText().toString());

				preferenceHelper.setUserAccount(username.getText().toString());
				finish();				
			}
		});

    }
}