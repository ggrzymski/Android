package cs213.android;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class AlbumActivity extends Activity 
{
	private String album; // name of the currently opened album
	private boolean isStarted= false;
	private Album hold;
	List<Album> albums;
	public Controller c;
	public Backend b;
	private Context context;
	private GridView gv;
	private List<Photo> photo_holder = new ArrayList<Photo>();
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		initialize();
		
	}
	
	public void onPause() 
	{
		super.onPause();
		b = Backend.getInstance(getApplicationContext());

		b.writeApp(b.list_albums);
	}
	
	public void onResume() 
	{
		super.onResume();
		
		if(isStarted==true)
		{
			resume();
		}
		
		isStarted=true;
	}
	
	public void initialize()
	{
		setContentView(R.layout.main); // make it another relative layout with gridview
		context = this;
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Get the Backend and Controller for album operations
		
		b = Backend.getInstance(getApplicationContext());
		c = new Controller(context, b.list_albums,b);
		
		// Get the Album
		
		album = getIntent().getExtras().getString(Constants.ALBUM_NAME);
		hold = b.findAlbum(album);
		
		// Set up the view
		
		gv = (GridView)findViewById(R.id.gridview);
		
		displayPhotos(hold);
	}
	
	public void resume()
	{
		//Get the Album
		
		album = getIntent().getExtras().getString(Constants.ALBUM_NAME);
		Album identified = b.findAlbum(album);
		
		// Set up the view
		
		gv = (GridView)findViewById(R.id.gridview);
		
		displayPhotos(identified);
	}
	
	private void displayPhotos(Album album)
	{
		TextView tv = (TextView) findViewById(R.id.textview);
		
		if(album.photos.size()==0) // Tell the user no photos exist in the center of the screen
		{
			tv.setVisibility(View.VISIBLE);
			PhotoListAdapter pt = new PhotoListAdapter(context, album);
			gv.setAdapter(pt);
			tv.setGravity(Gravity.CENTER);
			tv.setText("No Photos Exist");
		}
		else if(album.photos.size()!=0) // need to set up a similar adapter to album to portray photos
		{
			int placeholderWidth = (int) (1.1 * this.getResources().getDrawable(R.drawable.empty).getIntrinsicWidth());
	        gv.setColumnWidth(placeholderWidth);
			PhotoListAdapter pt = new PhotoListAdapter(context, album);
			gv.setAdapter(pt);
			tv.setText("");
			
			registerForContextMenu(gv);
			gv.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					photo_holder.clear();
					photo_holder.add(hold.photos.get(position));
					view.showContextMenu();
				}
			});
		}
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.layout.photocontext, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) 
	{
		int choice = item.getItemId(); // what user selected
		
		// Switch to another activity for display photo
		
		if(choice==R.id.display_photo)
		{
			if(photo_holder.size()==0)
			{
				Toast toast = Toast.makeText(context, "No Photo Selected", Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
			
			// switch to another activity
			
			Intent i = new Intent(context, ImageActivity.class);
			i.putExtra(Constants.PHOTO_NAME, photo_holder.get(0).file_name);
			i.putExtra(Constants.ALBUM_NAME, album);
			context.startActivity(i);
		}
		
		// Delete Photo, make it another dialog box like delete album
		
		else if(choice==R.id.delete_photo)
		{
			if(photo_holder.size()==0)
			{
				Toast toast = Toast.makeText(context, "No Photo Selected", Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
			
			final AlertDialog delete = new AlertDialog.Builder(this)
			.setTitle("Delete Photo")
			.setMessage("Delete Selected Photo?")
			.setCancelable(false)
			
			// set the submit button guidelines, need to override user operations on the click
			
			.setPositiveButton(R.string.submit, null)
			
			// set the cancel button guidelines 
			
			.setNegativeButton(R.string.cancel,null)
			.create();
			
			delete.setOnShowListener(new DialogInterface.OnShowListener() 
			{
				@Override
				public void onShow(DialogInterface dialog) 
				{
					Button b = delete.getButton(AlertDialog.BUTTON_POSITIVE);
					
					b.setOnClickListener(new View.OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							String photo = photo_holder.get(0).file_name;
							
							if (c.removePhoto(photo, album)==true) 
							{
								Toast toast = Toast.makeText(context, "Deleted photo: " + photo, Toast.LENGTH_SHORT);
								toast.show();
								delete.dismiss();
								refresh();
							}
						}
						
					});
				}
			});
			
			delete.show();
		}
		
		// Move Photo
		
		else if(choice==R.id.move_photo)
		{
			// Make sure it is more than one album
			
			if(b.list_albums.size()<=1)
			{
				Toast toasty =Toast.makeText(context, "User has only one album", Toast.LENGTH_SHORT);
				toasty.show();
				return false;
			}
			
			albums = new ArrayList<Album>();
			
			for(int i =0; i<b.list_albums.size();i++)
			{
				albums.add(b.list_albums.get(i));
			}
			
			albums.remove(hold); // Just list the other albums
			
			// Convert to String Array
			
			String [] album_names = new String[albums.size()];
			
			for(int i =0 ; i<album_names.length;i++)
			{
				album_names[i] = albums.get(i).album_name;
			}
			
			AlertDialog.Builder move_photo = new AlertDialog.Builder(this);
			move_photo.setTitle("Select an Album");
			
			ListView lv = new ListView(this);
			
			ArrayAdapter<String> albumAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, album_names);
			lv.setAdapter(albumAdapter);
			
			move_photo.setView(lv);
			
			final Dialog dg = move_photo.create();
			dg.show();
			
			lv.setOnItemClickListener(new OnItemClickListener() 
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) 
				{
					String photo = photo_holder.get(0).file_name;
					
					String oldalbum = album;
					String newalbum= albums.get(position).album_name;
					
					int result = c.movePhoto(photo, oldalbum, newalbum);
					
					if(result==2)
					{
						Toast toasty = Toast.makeText(context, "Photo already exists in new album", Toast.LENGTH_SHORT);
						toasty.show();
					}
					
					if(result==3)
					{
						Toast toasty = Toast.makeText(context, "Successfully moved Photo: "+ photo+ " to "+newalbum, Toast.LENGTH_SHORT);
						toasty.show();
						refresh();
						dg.dismiss();
					}
				}
			});
		}
		
		return true;
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.layout.open, menu);
		
		MenuItem mi = menu.findItem(R.id.search);
		
		final SearchView sv = (SearchView) mi.getActionView();
		
		sv.setQuery("", false);  // blank it
		
		sv.setOnQueryTextListener(new OnQueryTextListener() 
		{
			@Override
			public boolean onQueryTextSubmit(String query) 
			{
				// TODO Auto-generated method stub
				
				Intent i = new Intent(context, SearchTag.class);
				i.putExtra(Constants.SEARCH, sv.getQuery().toString());
				
				sv.setQuery("",false);
				
				startActivity(i);

				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) 
			{
				// TODO Auto-generated method stub
				return false;
			}
		});
		
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int choice = item.getItemId(); // what user selected
		
		// add a photo, redirect towards gallery screen
		
		if(choice==R.id.add_photo)
		{
			choosePicture();
		}
		
		else if(choice==android.R.id.home)
		{
			finish();
		}
		
		return true;
	}
	
	// Takes us to the gallery screen when user selects add a photo
	
	public void choosePicture()
	{
		// gallery
		
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		
		// start the image collecting activity
		
		startActivityForResult(i, 1);
	}
	
	// decipher the decisions made by multiple activities
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode==RESULT_OK)
		{
			// add a photo activity
			
			if(requestCode==1 && data!=null)
			{
				Uri image = data.getData();
				
				String file_path = getFilePath(image);
				String caption = file_path.substring(file_path.lastIndexOf("/")+1);
				boolean check=true;
				
				// call the controller and have the image saved in the project as well
				try
				{
					check = c.addPhoto(file_path, caption, album);
					
				}
				catch(FileNotFoundException f)
				{
					
				}
				catch(IOException e)
				{
					
				}
				
				if(check==false)
				{
					Toast toast = Toast.makeText(context, "Couldn't Add Photo", Toast.LENGTH_LONG);
					toast.show();
				}
				else
				{
					Toast toast = Toast.makeText(context, "Added Photo: "+caption, Toast.LENGTH_LONG);
					toast.show();
					refresh();				
				}
				
			}
		}
	}
	
	private String getFilePath(Uri image)
	{
		Cursor cursor = null;
		   
	    String[] directory = {MediaStore.Images.Media.DATA };
	    cursor = context.getContentResolver().query(image,  directory, null, null, null);
	    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	    cursor.moveToFirst();
		  
	
		String return_image = cursor.getString(column_index);
		
		cursor.close();
		  
		return return_image;
	}
	
	// Refresh the Screen after every change 
	
	public void refresh() 
	{
		photo_holder.clear();
		displayPhotos(b.findAlbum(album));
		gv.invalidate();
	}
}
