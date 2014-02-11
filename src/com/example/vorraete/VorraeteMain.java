package com.example.vorraete;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VorraeteMain extends Activity {
	private static final String TAG = VorraeteMain.class.getSimpleName();
	private TextView tvAll;
	private ListView lvArticles;
	private ArticleArrayAdapter arrayadapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vorraete_main);
		tvAll = (TextView) findViewById(R.id.tv_all);
		lvArticles = (ListView) findViewById(R.id.lv_articles);

		ArrayList<Article> tmparts = new ArrayList<Article>();
		tmparts.add(new Article("halle", 2));
		tmparts.add(new Article("Banane", 4));
		tmparts.add(new Article("Tisch", 10));
		arrayadapter = new ArticleArrayAdapter(this, tmparts);
		lvArticles.setAdapter(arrayadapter);
		lvArticles.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(final AdapterView<?> parent, View view,
					final int position, long id) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							// This doesn't work, can't access position from
							// here
							Article article = (Article) parent.getItemAtPosition(position);
							article.kaufen = !article.kaufen;
							Toast.makeText(VorraeteMain.this, article.toString(), Toast.LENGTH_SHORT).show();
							ArticleUpdateTask task = new ArticleUpdateTask();
							task.execute(article);
							refresh();
							
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(
						VorraeteMain.this);
				builder.setMessage("Are you sure you want to do this?")
						.setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener)
						.show();

			}

		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		GetJsonTask task = new GetJsonTask();
		task.execute(new String[] { "http://nexopi.no-ip.org/php/vorraete_fetch_rpi.php" });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.itm_insert:
			startActivity(new Intent(this, InsertArticleActivity.class));
			break;

		}

		return true;
	}

	class GetJsonTask extends AsyncTask<String, Integer, List<Article>> {
		@Override
		protected List<Article> doInBackground(String... params) {
			List<Article> result = new ArrayList<Article>();
			try {
				URL url = new URL(params[0]);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.connect();
				InputStream is = conn.getInputStream();
				byte[] bytes = new byte[1024];
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				while (is.read(bytes) != -1) {
					baos.write(bytes);
				}
				String JSONResp = new String(baos.toByteArray());
				JSONArray arr = new JSONArray(JSONResp);
				for (int i = 0; i < arr.length(); i++) {
					result.add(convertArticle(arr.getJSONObject(i)));
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				
				Log.d(TAG, e.toString());
			}
			return result;

		}

		@Override
		protected void onPostExecute(List<Article> articles) {
			super.onPostExecute(articles);
			StringBuilder sb = new StringBuilder();
			for (Article art : articles) {
				sb.append(art.toString());
			}
			// tvAll.setText(sb.toString());
			// arrayadapter.setArticleList(articles);
			// arrayadapter.notifyDataSetChanged();
			arrayadapter = new ArticleArrayAdapter(VorraeteMain.this, articles);
			lvArticles.setAdapter(arrayadapter);

			

		}

		private Article convertArticle(JSONObject obj) {
			String name;
			int id;
			boolean kaufen;
			Date date;
			Article article = null;
			try {
				name = obj.getString("name");
				id = obj.getInt("id");
				kaufen = obj.getInt("kaufen") == 1 ? true : false;
				try {
					date = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(obj.getString("time"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				article = new Article(name, id, kaufen);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return article;
		}
	}
	
	class ArticleUpdateTask extends AsyncTask<Article, Void, String> {

		protected String doInBackground(Article...articles) {
			String result = null;
			Article article = new Article("Kuchen");

			try {
				InputStream is = null;
				StringBuilder sb = new StringBuilder();
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://nexopi.no-ip.org/php/vorraete_update_rpi.php");
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
			
			
			
		}
		
		public List<NameValuePair> buildNameValuePairs(Article article) {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("id", "" +article.id));
			nameValuePairs.add(new BasicNameValuePair("kaufen", (article.kaufen)?"1":"0"));
			
			return nameValuePairs;

		}
	}
	
	private void refresh() {
		GetJsonTask task = new GetJsonTask();
		task.execute(new String[] { "http://nexopi.no-ip.org/php/vorraete_fetch_rpi.php" });
	}


}
