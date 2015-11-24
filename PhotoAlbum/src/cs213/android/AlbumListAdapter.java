package cs213.android;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cs213.photoalbum.model.*;

import java.util.List;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class AlbumListAdapter extends BaseAdapter 
{
	/* We are setting up an Adapter to portray the albums inside the main 
	 * Home Screen. The albums will be portrayed in a grid view.
	 * 
	 * 1. If no albums are in the database, then "No Albums Exist" is portrayed in the center of the screen
	 * 2. If there is at least one album in the database, then the folder icon of the album is portrayed as the
	 * album cover.
	 */
	
	 private Context context;
	 private List<Album> albums; // albums that are currently in the database
	 public Backend back;
	 
	    public AlbumListAdapter(Context c, List<Album> a, Backend b)
	    {
	    	super();
	        this.context = c;
	        this.albums = a;
	        this.back =b;
	    }

	    public int getCount() 
	    {
	        return albums.size();
	    }

	    public Object getItem(int position) 
	    {
	        return albums.get(position);
	    }

	    public long getItemId(int position) 
	    {
	        return position;
	    }
	    
	    // Represents the object for one album icon in the home display 
	    
	    public class AlbumIcon
	    {
	    	ImageView folder; // image of an album folder
	    	TextView caption; //name of the album;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
	    	AlbumIcon temp;
	    	
	    	if(convertView==null)
	    	{
	    		// need an inflater of the view because it needs to be customized
	    		
	    		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    		
	    		// set it to the album xml file 
	    		
	    		convertView = inflater.inflate(R.layout.album,null);
	    		
	    		temp = new AlbumIcon();
	    		
	    		temp.folder = (ImageView) convertView.findViewById(R.id.image);
	    		temp.folder.setScaleType(ImageView.ScaleType.CENTER_INSIDE);  // Center the Album photo
	    		temp.caption = (TextView) convertView.findViewById(R.id.caption);
	    		temp.caption.setText(albums.get(position).album_name); // Set the name of the Album
	    		convertView.setTag(temp); // in case we have to reload
	    	}
	    	else
	    	{
	    		temp = (AlbumIcon) convertView.getTag();
	    	}
	    	
	    	return convertView;
	    }
}
