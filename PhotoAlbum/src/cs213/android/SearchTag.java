package cs213.android;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class SearchTag extends Activity
{
	Context context;
	Backend b;
	Controller c;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchtags);
		
		this.context=this;
		
		b = Backend.getInstance(context);
		c = new Controller(context, b.list_albums, b);
		
		String result = getIntent().getExtras().getString(Constants.SEARCH);
		
		getActionBar().setTitle("Search Results for " + result);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		doSearch(result);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{

		int choice = item.getItemId();
		
		if (choice == android.R.id.home) 
		{
			finish();
		}
		
		return false;
	}
	
	private void doSearch(String query) 
	{
		// use the contains method to see if the tag value has it
		
		final List<Photo> results = new ArrayList<Photo>();
		
		String input =  query.toLowerCase(Locale.getDefault()).trim();
		
		List<Album> albums = b.list_albums;
		
		for(int i=0; i<albums.size(); i++)
		{
			List<Photo> photos = albums.get(i).photos;
			
			for(int j=0; j<photos.size(); j++)
			{
				for(int k=0; k<photos.get(j).tag_list.size(); k++)
				{
					String tag_value = photos.get(j).tag_list.get(k).tag_value.toLowerCase(Locale.getDefault()).trim();
					
					boolean check = tag_value.contains(input);
					
					if(check==true)
					{
						if(results.contains(photos.get(j))==false)
						{
							results.add(photos.get(j));
						}
					}
				}
			}
		}
		
		// Convert to String Array for Adapter
		
		if(results.size()==0)
		{
			Toast.makeText(getApplicationContext(), "No Results Found", Toast.LENGTH_SHORT).show();
		}
		else
		{
			ListView lv = (ListView) findViewById(R.id.results);
			ArrayAdapter<Photo> adaptation = new ArrayAdapter<Photo>(context, R.layout.tagitem, results);
			lv.setAdapter(adaptation);
			
			lv.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View v, int pos, long id) 
				{
					Photo p = results.get(pos);
					Intent i = new Intent(context, ImageActivity.class);
					i.putExtra(Constants.PHOTO_NAME, p.file_name);
					i.putExtra(Constants.ALBUM_NAME, p.belong_album );
					startActivity(i);
				}
			});
			
			Toast.makeText(getApplicationContext(), "Found "+results.size()+" Results.", Toast.LENGTH_SHORT).show();
		}
	}
}
