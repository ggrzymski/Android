package cs213.android;

import java.util.ArrayList;
import java.util.List;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Backend;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class TagDialog extends DialogFragment 
{
	public Controller c;
	public Backend b;
	Context context;
	
	private String photo;
	private Spinner tagtype;
	private EditText et;
	
	public Dialog onCreateDialog(Bundle savedInstanceState) 
	{
		context = getActivity().getApplicationContext();
		b = Backend.getInstance(getActivity().getApplicationContext());

		c = new Controller(context, b.list_albums,b);
		
		// Photo and File Name
		
		photo = getArguments().getString(Constants.PHOTO_NAME);
		
		// Inflate the custom layout
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.addtag, null);
		
		// Set up the Spinner
		
		List<String> tagTypes = new ArrayList<String>();
		tagTypes.add("Person");
		tagTypes.add("Location");
		
		tagtype = (Spinner) v.findViewById(R.id.tagtypes);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, tagTypes);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tagtype.setAdapter(dataAdapter);
		et = (EditText) v.findViewById(R.id.valuetext);
		
		// Set up the dialog box
		
		AlertDialog.Builder tag_dialog = new AlertDialog.Builder(getActivity());
		tag_dialog.setView(v)
		.setTitle(R.string.add_tag)
				
		// set the submit button guidelines, need to override user operations on the click
		
		.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() 
		{

			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				String result = et.getText().toString();
				
				// Can't allow empty string
				
				if(result.length()==0)
				{
					Toast.makeText(context, "You must enter a tag value.", Toast.LENGTH_SHORT)
					.show();
				}
				
				else
				{
					String photo_name = photo;
					String tag_type = tagtype.getSelectedItem().toString();
					String tag_value = result;
					
					if (c.addTag(photo_name, tag_type, tag_value)) 
					{
						Toast toast = Toast.makeText(context, "Created Tag "+tag_type+": "+tag_value+" for: "+photo_name, Toast.LENGTH_SHORT);
						toast.show();
					} 
					else 
					{
						Toast toast =Toast.makeText(context, "Tag already exists." , Toast.LENGTH_SHORT);
						toast.show();
					}
				}
			}
			
		})
		
		// set the cancel button guidelines 
		
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int id) 
			{
				TagDialog.this.getDialog().cancel();
			}
		});
		
		return tag_dialog.create();
	}
}
