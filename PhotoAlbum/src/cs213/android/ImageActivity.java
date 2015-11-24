package cs213.android;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;
import cs213.photoalbum.model.Tag;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class ImageActivity extends Activity
{
	private static final int THEME_HOLO_LIGHT = 0x00000003;

	public Controller c;
	public Backend b;
	Album current;
	Context context;
	String album; // name of album currently holding photo
	String photo; //name of photo currently displayed
	private ImageView imageView;
	private ListView lv;
	private BitmapFactory.Options options;
	TagDisplayAdapter dataAdapter;
	Photo identified;
	boolean threading =true;
	int tag_size=0;
	public TagListAdapter tg;
	boolean isStarted=false;
	int index;
	List<Photo> photos; // photos to display

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.image);
		initialize();
	}
	
	public void onResume() 
	{
		super.onResume();
		initialize();
	}
	
	public void onPause() 
	{
		super.onPause();
		b = Backend.getInstance(getApplicationContext());
		b.writeApp(b.list_albums);
	}
	
	public void initialize()
	{
		context=this;
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Backend and Controller
		
		b = Backend.getInstance(this);
		c = new Controller(context, b.list_albums,b);
		
		// Full Image Properties
		
		imageView = (ImageView) findViewById(R.id.full_image_view);
		
		options = new BitmapFactory.Options();
		options.inSampleSize = 5;
		options.inPurgeable = true;
		options.inJustDecodeBounds = false;
		
		
		// Get Album and Photo Information
		
		this.album = getIntent().getExtras().getString(Constants.ALBUM_NAME);
		this.photo = getIntent().getExtras().getString(Constants.PHOTO_NAME);
		
		current = b.findAlbum(album);
		photos = current.photos;
		
		index=0;
		
		// Set the Image
		
		String path = this.getFilesDir() + File.separator + photo;
		
		for(int i=0; i<photos.size();i++)
		{
			if(photos.get(i).file_name.compareTo(photo)==0)
			{
				index=i;
			}
		}

		Bitmap bips = BitmapFactory.decodeFile(path, options);
        Bitmap for_real = Bitmap.createScaledBitmap(bips, 384, 216, true);
		imageView.setImageBitmap(for_real);
		
		// Display Tags
		
		identified = b.findPhoto(photo);
		tag_size = identified.tag_list.size();
		
		dataAdapter = new TagDisplayAdapter(context, identified.tag_list);
		lv = (ListView) findViewById(R.id.taggles);
		
		lv.setAdapter(dataAdapter);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.layout.photomenu, menu);
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int choice = item.getItemId(); // what user selected
		
		// add a tag, make it like create album
		
		if(choice==R.id.add_tag)
		{
			addTag();
		}
		
		// delete a tag
		
		else if(choice==R.id.delete_tag)
		{
			deleteTag();
		}
		
		// previous photo
		
		else if(choice==R.id.previous_photo)
		{
			// need to go to previous picture
			
			handleLeft();
			
			identified = photos.get(index);
				
			String path = context.getFilesDir() + File.separator + identified.file_name;
			Bitmap bips = BitmapFactory.decodeFile(path, options);
	        Bitmap for_real = Bitmap.createScaledBitmap(bips, 384, 216, true);
			imageView.setImageBitmap(for_real);
			imageView.invalidate();
			imageView.refreshDrawableState();
			
			dataAdapter = new TagDisplayAdapter(context, identified.tag_list);
			lv = (ListView) findViewById(R.id.taggles);
			
			lv.setAdapter(dataAdapter);
			lv.invalidate();
		}
		
		else if(choice==R.id.next_photo)
		{
			handleRight();
			
			identified = photos.get(index);
			
			String path = context.getFilesDir() + File.separator + identified.file_name;
			Bitmap bips = BitmapFactory.decodeFile(path, options);
	        Bitmap for_real = Bitmap.createScaledBitmap(bips, 384, 216, true);
			imageView.setImageBitmap(for_real);
			imageView.invalidate();
			imageView.refreshDrawableState();
			
			dataAdapter = new TagDisplayAdapter(context, identified.tag_list);
			lv = (ListView) findViewById(R.id.taggles);
			
			lv.setAdapter(dataAdapter);
			lv.invalidate();
		}
		
		else if(choice==android.R.id.home)
		{
			finish();
		}
		
		return true;
	}
	
	private void addTag()
	{
		LayoutInflater inflater = this.getLayoutInflater();
		View v = inflater.inflate(R.layout.addtag, null);
		
		// Set up the Spinner
		
		List<String> tagTypes = new ArrayList<String>();
		tagTypes.add("Person");
		tagTypes.add("Location");
		
		final Spinner tagtype = (Spinner) v.findViewById(R.id.tagtypes);
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tagTypes);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		tagtype.setAdapter(dataAdapter);
		final EditText et = (EditText) v.findViewById(R.id.valuetext);
		
		// Set up the dialog box
		
		AlertDialog.Builder tag_dialog = new AlertDialog.Builder(this);
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
					String photo_name = identified.file_name;
					String tag_type = tagtype.getSelectedItem().toString();
					String tag_value = result;
					
					if (c.addTag(photo_name, tag_type, tag_value)) 
					{
						Toast toast = Toast.makeText(context, "Created Tag "+tag_type+": "+tag_value+" for: "+photo_name, Toast.LENGTH_SHORT);
						toast.show();
						refresh();
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
				
			}
		});
		
		tag_dialog.create();
		tag_dialog.show();

	}
	
	private void deleteTag()
	{		
		if(identified.tag_list.size()==0)
		{
			Toast toasty =Toast.makeText(this, "No tags exist for this photo", Toast.LENGTH_SHORT);
			toasty.show();
			return;
		}
		
		LayoutInflater inflater = this.getLayoutInflater();
		
		View v = inflater.inflate(R.layout.taglist, null);
		lv = (ListView) v.findViewById(R.id.tagList);
		
		tg = new TagListAdapter(this.context, identified.file_name);
		
		lv.setAdapter(tg);
		
		AlertDialog.Builder tag_dialog = new AlertDialog.Builder(this, THEME_HOLO_LIGHT);
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
				
			}
		});
		
		tag_dialog.create();
		tag_dialog.show();
	}
	
	private void deleteTags()
	{
		// List of Tags to delete
		
		List<Tag> chosen_ones = tg.checked;
		
		for(int i=0; i< chosen_ones.size();i++)
		{
			c.deleteTag(identified.file_name, chosen_ones.get(i).tag_type, chosen_ones.get(i).tag_value);
		}
	
		Toast toasty =Toast.makeText(context, "Deleted Selected Tags", Toast.LENGTH_SHORT);
		toasty.show();
		refresh();
	}
	
	public void refresh() 
	{
		dataAdapter.notifyDataSetChanged();

	}
	private void handleLeft()
	{
		if(index==0)
		{
			Toast toasty =Toast.makeText(context, "No Photo to Display", Toast.LENGTH_SHORT);
			toasty.show();
			return;
		}
		
		index--;
	}
	
	private void handleRight()
	{
		if(index==photos.size()-1)
		{
			Toast toasty =Toast.makeText(context, "No Photo to Display", Toast.LENGTH_SHORT);
			toasty.show();
			return;
		}
		
		index++;
	}
}
