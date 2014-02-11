package com.example.vorraete;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InsertArticleActivity extends Activity {
	private static final String TAG = InsertArticleActivity.class.getSimpleName();
	private static String URL = "http://nexopi.no-ip.org/php/vorraete_insert_rpi.php";
	//private List<Shopping> shoppings;
	private Button btnInsert;
	private EditText etNewArticle;
	private ListView lvShoppings;
	private Article[] articles;
	//private ShoppingDataSource source;
	//private ArrayAdapter<Shopping> adapter;
	private CursorAdapter listAdapter;
	private TextView tvResult;
	private TextView tvDebug;
	private DecimalFormat df = new DecimalFormat("0.00");
	private boolean debug = false;

	@Override
	protected void onStart() {

		super.onStart();
		
	}

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_activity_layout);
		
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    etNewArticle = (EditText) findViewById(R.id.et_new_article);
	    btnInsert = (Button) findViewById(R.id.btn_insert);
		btnInsert.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (etNewArticle.getText().toString().matches("")) {
					Toast.makeText(InsertArticleActivity.this, "kein Artikel angegeben", Toast.LENGTH_SHORT).show();
				} else {
					articles = new Article[]{new Article(etNewArticle.getText().toString())};
					HttpConnectTask task = new HttpConnectTask();
					task.execute(articles);
				}
				
			}
		});
	    
	}

		
	class HttpConnectTask extends AsyncTask<Article, Void, String> {

		protected String doInBackground(Article...articles) {
			String result = null;
			Article article = new Article("Kuchen");

			try {
				InputStream is = null;
				StringBuilder sb = new StringBuilder();
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(InsertArticleActivity.URL);
				List<NameValuePair> nameValuePairs = buildNameValuePairs(articles[0]);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpClient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				try {
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(is));
					String line = null;

					while ((line = reader.readLine()) != null) {
						sb.append(line);
					}
				} catch (IOException ioe) {
					Log.d(TAG, ioe.toString());
					result = "Error: " + ioe.toString();
				}
				
				result = sb.toString();

			} catch (Exception e) {
				result = "Error: " + e.toString();
			}
			if (result.contains("MYSQL_ERROR"))
				result = "Error: " + result;
			return result;
		}

		protected void onPostExecute(String result) {
			Log.d(TAG, "http done");
			boolean error = true;
			if (result.startsWith("Error")) {
				error = true;
			} else {
				error = false;
			}
			Toast.makeText(InsertArticleActivity.this, "http abgesetzt",
					Toast.LENGTH_SHORT).show();
			etNewArticle.setText("");
			
		}
		
		public List<NameValuePair> buildNameValuePairs(Article article) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("name", article.name));
			
			return nameValuePairs;

		}
	}

	
	

}
