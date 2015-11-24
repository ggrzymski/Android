package cs213.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;
import cs213.photoalbum.model.Tag;

/**
 * 
 * @author Gregory Grzymski, Gaston Gonzalez
 *
 */
public class TagListAdapter extends BaseAdapter
{
	private Context context;
	private List<Tag> tags; // tag list to print out
	public List<Tag> checked; // tags selected by checkboxes
	LayoutInflater inflater;
	Controller c;
	Backend b;
	
	String photo;
	String album;
	Photo current;
	
	public TagListAdapter(Context ctx, String input)
	{
		this.context=ctx;
		this.photo = input;
		b= Backend.getInstance(ctx);
		c= new Controller(this.context, b.list_albums,b);
		
		current = b.findPhoto(photo);
		tags = current.tag_list;
		checked = new ArrayList<Tag>();
		
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() 
	{
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
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View v = inflater.inflate(R.layout.tag, parent,false);
		
		final CheckBox lockbox = (CheckBox) v.findViewById(R.id.select);
		TextView tv = (TextView) v.findViewById(R.id.taglabel);
		
		tv.setText(tags.get(position).toString());
		
		final Tag chosen_one = tags.get(position); // make it final for the clicks
		
		lockbox.setChecked(false);
		
		// in case the user tries to uncheck by clicking the name instead of the box
		
		lockbox.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (lockbox.isChecked()) 
				{
					checked.add(chosen_one);
				} 
				else 
				{
					checked.remove(chosen_one);
				}
			}
		});
		
		v.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if (lockbox.isChecked()) // it means user will uncheck
				{
					lockbox.setChecked(false);
					checked.remove(chosen_one);
				} else 
				{
					lockbox.setChecked(true);
					checked.add(chosen_one);
				}
			}
		});
			
		return v;
	}
	
}
