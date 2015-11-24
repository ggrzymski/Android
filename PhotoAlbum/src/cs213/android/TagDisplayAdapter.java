package cs213.android;


import java.util.List;

import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;
import cs213.photoalbum.model.Tag;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class TagDisplayAdapter extends BaseAdapter 
{
	private Context context;
	private List<Tag> tags; // tag list to print out
	LayoutInflater inflater;
	Controller c;
	Backend b;
	
	String photo;
	String album;
	Photo current;
	
	public TagDisplayAdapter(Context ctx, List<Tag> input)
	{
		this.context=ctx;
		
		b= Backend.getInstance(ctx);
		c= new Controller(this.context, b.list_albums,b);
		
		tags = input;
		
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return tags.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return tags.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	class ViewHolder 
	{
		  TextView title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		ViewHolder holder = null;
		 
		convertView= inflater.inflate(R.layout.tagdisplay, parent,false);
			
		holder = new ViewHolder();
			
		holder.title = (TextView) convertView.findViewById(R.id.tagdisplay);
			
		holder.title.setText(tags.get(position).toString());
			
		convertView.setTag(holder);
	
		return convertView;
	}
}
