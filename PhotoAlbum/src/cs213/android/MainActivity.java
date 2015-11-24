package cs213.android;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import cs213.photoalbum.control.Controller;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Backend;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class MainActivity extends Activity
{
	public Controller c;
	public Backend b;
	public Context context;
	private boolean isStarted = false;
	private GridView gv;
	private List<Album> album_holder = new ArrayList<Album>();
	
	// This is the Home Screen where our albums will display themselves
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		initialize();
	}
	
	public void onResume() 
	{
		super.onResume();
		
		if(isStarted==true)
		{
			displayAlbums();
		}
		
		isStarted=true;
	}
	
	// user may switch out and come back, keep this write persistent
	
	public void onPause() 
	{
		super.onPause();
		b.writeApp(b.list_albums);
	}
	
	/* Initialize the home screen when user decides to click on the app */
	
	public void initialize() 
	{
		setContentView(R.layout.main);
		context=this;
		b = Backend.getInstance(context);		
		c= new Controller(context , b.list_albums, b);
		gv = (GridView) findViewById(R.id.gridview);
		
		// show the user all of the current albums
		
		displayAlbums();
	}
	
	private void displayAlbums()
	{
		/* Use a Textview to portray that no albums exist in the center of the screen
		 * 
		 * Use a Base Adapter to display any existing albums 
		 * 
		 */
		
		TextView tv = (TextView) findViewById(R.id.textview);
		
		if(c.getAlbums().isEmpty()==true) // Tell the user no albums exist in the center of the screen
		{
			tv.setVisibility(View.VISIBLE);
			tv.setGravity(Gravity.CENTER);
			tv.setText("No Albums Exist");
			
			AlbumListAdapter adapter = new AlbumListAdapter(context, b.list_albums,b);
			
			gv.setAdapter(adapter);
		}
		else
		{
			tv.setVisibility(View.GONE); // the text view is only responsible for no albums selected
			AlbumListAdapter adapter = new AlbumListAdapter(context, b.list_albums,b);
			
			gv.setAdapter(adapter);
			registerForContextMenu(gv);
			
			gv.setOnItemClickListener(new OnItemClickListener()
			{
				public void onItemClick(AdapterView<?> parent, View view, int position, long id)
				{
					album_holder.clear();
					album_holder.add(b.list_albums.get(position));
					view.showContextMenu();
				}
			});
		}
	}
	
	/*Create our own menu for the album home screen. For starters, create an album with the plus sign
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.layout.menu, menu);
		
		// Set up a Search View from xml
		
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
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.layout.context, menu);
	}
	
	public boolean onContextItemSelected(MenuItem item) 
	{
		int choice = item.getItemId(); // what user selected
		
		// Rename an Album
		
		if(choice==R.id.rename_album)
		{
			if(album_holder.size()==0)
			{
				Toast toast = Toast.makeText(context, "No Album Selected", Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
			
			// Set up a new Dialog Window for Rename the current Album
			
				final EditText filler = new EditText(this); // this is the text shown in the field prior to user input
				
				filler.setHint("Album Name");
				
				final AlertDialog rename_album = new AlertDialog.Builder(this)
				.setView(filler)
				.setTitle(R.string.rename_album)
						
				// set the submit button guidelines, need to override user operations on the click
				
				.setPositiveButton(R.string.submit, null)
				
				// set the cancel button guidelines 
				
				.setNegativeButton(R.string.cancel,null)
				.create();
				
				// clicking overrride operations
				
				rename_album.setOnShowListener(new DialogInterface.OnShowListener() 
				{
					@Override
					public void onShow(DialogInterface dialog) 
					{
						Button butt = rename_album.getButton(AlertDialog.BUTTON_POSITIVE);
						butt.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								String result = filler.getText().toString();
								
								// Can't allow empty string
								
								if(result.length()==0)
								{
									Toast.makeText(context, "You must enter an album name.", Toast.LENGTH_SHORT)
									.show();
								}
								
								// Try to Rename a new album via the Controller
								
								else
								{
									if(album_holder.get(0).album_name.compareToIgnoreCase(result)==0)
									{
										Toast toast = Toast.makeText(context, "You have to specify a different name from the old one", Toast.LENGTH_SHORT);
										toast.show();
										return;
									}
									
									if(b.findAlbum(result)!=null)
									{
										Toast toast = Toast.makeText(context, "Album name already exists", Toast.LENGTH_SHORT);
										toast.show();
										return;
									}
									
									c.renameAlbum(album_holder.get(0).album_name, result);
									Toast toast = Toast.makeText(context, "Renamed album: "+album_holder.get(0).album_name+" to "+result, Toast.LENGTH_SHORT);
									toast.show();
									rename_album.dismiss();
									refresh();
								}
							}
							
						});
						
					}
				});
				
				rename_album.show();
		}
		
		// Open an album
		
		else if(choice==R.id.open_album)
		{
			if(album_holder.size()==0)
			{
				Toast toast = Toast.makeText(context, "No Album Selected", Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
			
			// switch to another activity
			
			Intent i = new Intent(context, AlbumActivity.class);
			i.putExtra(Constants.ALBUM_NAME, album_holder.get(0).album_name);
			context.startActivity(i);
		}
		
		// Delete an Album
		
		else if(choice==R.id.delete_album)
		{
			if(album_holder.size()==0)
			{
				Toast toast = Toast.makeText(context, "No Album Selected", Toast.LENGTH_SHORT);
				toast.show();
				return false;
			}
			
			final AlertDialog delete = new AlertDialog.Builder(this)
			.setTitle("Delete Album")
			.setMessage("Delete Selected Album?")
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
							String album = album_holder.get(0).album_name;
							
							if (c.deleteAlbum(album_holder.get(0).album_name)==true) 
							{
								Toast toast = Toast.makeText(context, "Deleted album: " + album, Toast.LENGTH_SHORT);
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
		
		return true;
	}

	
	/* response by the user when clicking on one of the bar options */
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		int choice = item.getItemId(); // what user selected
		
		// Create an album
		
		if(choice==R.id.create_new_album)
		{
			// Set up a new Dialog Window for Creating a new Album
			
			final EditText filler = new EditText(this); // this is the text shown in the field prior to user input
			
			filler.setHint("Album Name");
			
			final AlertDialog create_album = new AlertDialog.Builder(this)
			.setView(filler)
			.setTitle(R.string.create_album)
					
			// set the submit button guidelines, need to override user operations on the click
			
			.setPositiveButton(R.string.submit, null)
			
			// set the cancel button guidelines 
			
			.setNegativeButton(R.string.cancel,null)
			.create();
			
			// clicking overrride operations
			
			create_album.setOnShowListener(new DialogInterface.OnShowListener() 
			{
				@Override
				public void onShow(DialogInterface dialog) 
				{
					Button b = create_album.getButton(AlertDialog.BUTTON_POSITIVE);
					b.setOnClickListener(new View.OnClickListener() 
					{
						@Override
						public void onClick(View v) 
						{
							String result = filler.getText().toString();
							
							// Can't allow empty string
							
							if(result.length()==0)
							{
								Toast.makeText(context, "You must enter an album name.", Toast.LENGTH_SHORT)
								.show();
							}
							
							// Try to Create a new album via the Controller
							
							else
							{
								if (c.createAlbum(result)) 
								{
									Toast toast = Toast.makeText(context, "Created album: " + result, Toast.LENGTH_SHORT);
									toast.show();
									create_album.dismiss();
									refresh();
								} 
								else 
								{
									Toast toast =Toast.makeText(context, "Album already exists." , Toast.LENGTH_SHORT);
									toast.show();
								}
							}
						}
						
					});
					
				}
			});
			
			create_album.show();
		}
			
		return true;
	}
	
	// Refresh the Screen after every change 
	
	public void refresh() 
	{
		album_holder.clear();
		displayAlbums();
		gv.invalidate();
	}
}
