package cs213.photoalbum.model;

import java.util.*;
import java.io.*;

import android.content.Context;
import android.util.Log;

/**
 * The backend refers to functionality that will allow for the storage and retrieval of database information from the
 * data directory.
 * 
 * Backend functions include: 
 * 
 *  1. A method to retrieve data for a user identified by a specific user ID.
 *  2. A method to write the current user's data into storage from memory.
 *  3. A method to delete a user that is identified by a specific userID.
 *  4. A method that retrieves a list of existing users in the database, identified by user ID.
 *  
 *  @author Gregory Grzymski
 */
public class Backend 
{
	
	
	/**
	 * Directory that contains the user files.
	 */
	public static final String storeDir = "test";
	
	/**
	 * Name of the file that contains all the user information.
	 */
	private static final String FileExtension = "albums.dat";
	
	private static Backend instance = null;

	public List<Album> list_albums;
	
	public Context context;
	
	public Backend(Context c)
	{
		this.context=c;
		
		// get the Android system path to a file
		
		File album_file = new File(context.getFilesDir() + File.separator + FileExtension);
		
		if(album_file.exists()==false)
		{
			list_albums = new ArrayList<Album>();
			Log.e("File", "Created New File.");
			writeApp(list_albums);
		}
		else
		{
			Log.e("File", "Reading file.");
			list_albums = readApp();
		}
	}
	
	public List<Album> getList()
	{
		return this.list_albums;
	}
	
	/**
	 * Initializes the backend's list with provided input.
	 * 
	 * @param list Data structure that contains user information to be stored.
	 */
	public void setList(List<Album> list)
	{
		this.list_albums=list;
	}
	
	/**
	 * Writes all current session data into the users.dat file within the data directory
	 *  
	 * @param users List of users to be written into the file.
	 * @throws IOException If the file isn't properly read. 
	 * @throws ClassNotFoundException If serialization problems are encountered.
	 */
	 public void writeApp(List<Album> albums)
	 {
		 try
		 {
			 ObjectOutputStream oos= new ObjectOutputStream(context.openFileOutput(FileExtension,Context.MODE_PRIVATE));
			 oos.writeObject(albums);
			 oos.close();
			 Log.i("File write", "success");
		 }
		 catch(IOException e)
		 {
			 
		 }
	 }
	 
	/**
	 * Reads the backend's list of users to load with a new user session.
	 * 
	 * @return A list data structure containing user database information.
	 * @throws IOException If the file isn't properly read.
	 * @throws ClassNotFoundException If serialization problems are encountered.
	 */
    @SuppressWarnings("unchecked")
	public List<Album> readApp()
    {
    	List<Album> album_list = new ArrayList<Album>();
    	
    	try
    	{
    		ObjectInputStream ois= new ObjectInputStream(context.openFileInput(FileExtension));
    		album_list= (List<Album>)ois.readObject();
    		ois.close();
    		Log.i("File read", "success");
    	}
    	catch(IOException e)
    	{
    		
    	}
    	catch(ClassNotFoundException c)
    	{
    		
    	}
    	
    	return album_list;
    }
    
    /**
	 * Searches for the specific album within the user's list, if applicable.
	 * 
	 * @param albumName Name of album.
	 * @return The album is returned if search is successful. Otherwise, null is returned.
	 */
	public Album findAlbum(String albumName)
	{
		Album find_album;
		
		for(int i=0; i<this.list_albums.size();i++)
		{
			if(this.list_albums.get(i).album_name.compareToIgnoreCase(albumName)==0)
			{
				find_album=this.list_albums.get(i);
				return find_album;
			}
		}
		
		return null;
	}
	
	public Photo findPhoto(String filename)
	{
		Photo identified;
		
		for(int i=0; i<this.list_albums.size();i++)
		{
			identified= this.list_albums.get(i).findPhoto(filename);
			
			if(identified!=null)
			{
				return identified;
			}
		}
		
		return null;
	}
	
	public void getAlbumList(Photo item)
	{
		// Traverse the album list
		
		Album tester;
		int index=0;
		
		for(int i=0; i<this.list_albums.size();i++)
		{
			// If you found the photo, add it to the photo's album collection
			
			if(this.list_albums.get(i).hasPhoto(item.file_name, item.caption)==true)
			{
				// make sure it doesn't exist already
				
				tester=this.list_albums.get(i);
				
				index=item.album_collection.indexOf(tester);
					
				if(index!=-1) // album exists already
				{
					continue;	
				}
			
				item.album_collection.add(this.list_albums.get(i));
			}
		}
	}
	
	public static Backend getInstance(Context context) 
	{
		if (instance == null) {
			instance = new Backend(context);
		}
		return instance;
	}
}

