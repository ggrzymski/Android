package cs213.android;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Tag;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class DeleteTagMenu extends DialogFragment
{
	private static final int THEME_HOLO_DARK = 0x00000002;
	String album;
	String photo;
	
	Context context;
	
	public Controller c;
	public Backend b;
	
	private ListView lv;
	private TagListAdapter tg;
		
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		context = getActivity().getApplicationContext();
		b = Backend.getInstance(getActivity().getApplicationContext());
		c= new Controller(this.context, b.list_albums,b);
		
		// Photo and File Name
		
		photo = getArguments().getString(Constants.PHOTO_NAME);
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		
		View v = inflater.inflate(R.layout.taglist, null);
		lv = (ListView) v.findViewById(R.id.tagList);
		
		tg = new TagListAdapter(this.context, this.photo);
		
		lv.setAdapter(tg);
		
		AlertDialog.Builder tag_dialog = new AlertDialog.Builder(getActivity(), THEME_HOLO_DARK);
		tag_dialog.setView(v)
		.setTitle(R.string.delete_tag)
		
		.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				deleteTags();
			}
			
		})
		
		// set the cancel button guidelines 
		
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				DeleteTagMenu.this.getDialog().cancel();
			}
		});
		
		return tag_dialog.create();
	}
	
	private void deleteTags()
	{
		// List of Tags to delete
		
		List<Tag> chosen_ones = tg.checked;
		
		for(int i=0; i< chosen_ones.size();i++)
		{
			c.deleteTag(photo, chosen_ones.get(i).tag_type, chosen_ones.get(i).tag_value);
		}
		
		Toast toasty =Toast.makeText(context, "Deleted Selected Tags", Toast.LENGTH_SHORT);
		toasty.show();
	}
}
