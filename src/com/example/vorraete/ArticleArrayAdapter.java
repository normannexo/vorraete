package com.example.vorraete;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
	private Context context;
	private List<Article> values;
	public ArticleArrayAdapter(Context context,List<Article> values) {
		super(context, R.layout.rowarticles_layout, values);
		this.context = context;
		this. values = values;
		
	}
	
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rowarticles_layout, null);
	    TextView textViewId = (TextView) rowView.findViewById(R.id.row_tv_id);
	    TextView textViewName = (TextView) rowView.findViewById(R.id.row_tv_name);
	    TextView textViewKaufen = (TextView) rowView.findViewById(R.id.row_tv_kaufen);
	    textViewName.setText(values.get(position).name);
	    textViewId.setText("" + values.get(position).id);
	    textViewKaufen.setText("" + values.get(position).kaufen);
	    if (values.get(position).kaufen) {
	    	textViewName.setTextColor(Color.RED);
	    } else {
	    	textViewName.setTextColor(Color.DKGRAY);
	    }

	    return rowView;
	  }
	
	public void setArticleList(List<Article> articles) {
		this.values = articles;
	}

}
