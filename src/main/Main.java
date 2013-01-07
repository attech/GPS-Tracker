package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import server.Server;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import eu.attech.gpstracker.R;
import gui.MainGui;
import gui.Register;

/**
 * 
 * @author Okermüller Patrick
 * @web www.attech.eu
 * @email info@attech.eu
 * 
 */

public class Main extends Activity {

	private Server server;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		server = new Server(this);
		
		setContentView(R.layout.startpage);
		checkIfFirstStart();
		super.onCreate(savedInstanceState);
	}

	/* Check if the App is started the first time or not */
	public void checkIfFirstStart() {
		File root = this.getFilesDir();
		if (root.canWrite()) {
			
			File version = new File(root, "version.dat");
			if (!version.exists()) {
				Log.d("AAA", "First Start");
				if (server.checkIfNetworkIsOn()) {
					Log.d("AAA", "TEST");
					createFiles();
				} else {
					Toast.makeText(this, "Please turn Internet on!",
							Toast.LENGTH_LONG).show();
					final Button reload = (Button) findViewById(R.id.reload);
					reload.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (server.checkIfNetworkIsOn()) {
								reload.setVisibility(View.GONE);
								reload.setEnabled(false);
								createFiles();
							}
						}
					});
					reload.setVisibility(View.VISIBLE);
					reload.setEnabled(true);
				}
			} else {
				// Is not the First start
				createLoginScreen();
			}
		}
	}
	
	/* Create the Login Screen for the App*/
	public void createLoginScreen() {
		
		
		
		checkIfUpdate();
		if(server.IsLoggedIn()){
			finish();
			startActivity(new Intent(this, MainGui.class));
		}
		
		EditText ed1 = (EditText)findViewById(R.id.editText1);
		EditText ed2 = (EditText)findViewById(R.id.editText2);
		TextView tv1 = (TextView)findViewById(R.id.textView2);
		TextView tv2 = (TextView)findViewById(R.id.textView3);
		TextView tv3 = (TextView)findViewById(R.id.textView1);
		Button loginB = (Button)findViewById(R.id.loginButton);
		Button registerB = (Button)findViewById(R.id.registerButton);
		
		ed1.setVisibility(View.VISIBLE);
		ed2.setVisibility(View.VISIBLE);
		tv1.setVisibility(View.VISIBLE);
		tv2.setVisibility(View.VISIBLE);
		tv3.setVisibility(View.VISIBLE);
		loginB.setVisibility(View.VISIBLE);
		loginB.setEnabled(true);
		registerB.setVisibility(View.VISIBLE);
		
		loginB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				server.loginToServer();
			}
		});
		
		registerB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				register();
			}
		});
	}
	
	/* In this Method you can Register a new User for the App */
	public void register() {
		finish();
		startActivity(new Intent(this, Register.class));
	}

	/*
	 * If it is the first start then create a folder on the sd-card, with some
	 * files
	 */
	public void createFiles() {
		try {
			File root = this.getFilesDir();
			if (root.canWrite()) {
				File version = new File(root, "version.dat");
				FileWriter versionwriter = new FileWriter(version);
				BufferedWriter out = new BufferedWriter(versionwriter);
				out.write(""
						+ getPackageManager().getPackageInfo(getPackageName(),
								0).versionCode);
				out.close();
				File login = new File(root, "login.dat");
				FileWriter loginwriter = new FileWriter(login);
				BufferedWriter write = new BufferedWriter(loginwriter);
				write.write("");
				write.close();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		createLoginScreen();
	}
	
	
	/* Check if an Update is Aviable on Server */
	public void checkIfUpdate() {
		if(server.checkIfNetworkIsOn()) {
			try {
				InputStream in = this.getAssets().open("data/version.dat");
			    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				String version = reader.readLine();
				server.getActualVersion(version);
				Log.d("AAA", "Version: " + version);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}
	}
	
	
}
