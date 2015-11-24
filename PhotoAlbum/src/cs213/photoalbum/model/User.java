package cs213.photoalbum.model;

import java.util.*;
import java.io.*;



/**
 * This class represents the Model of the Photo Library implementation. An individual user
 * is given a unique ID and username to differentiate itself from other users. All methods developed
 * in the container classes are provided in the User for processing. Any command that is relayed by the 
 * controller is parsed by the model in order to manipulate and alter the user's information. Any changes
 * made by the model are sent back to the controller, which then relays back to the Interactive View.
 * 
 * @author Gregory Grzymski, Gaston Gonzalez
 * @since 02-19-2014
 *
 */
public class User implements Serializable
{
	private static final long serialVersionUID = 1L; // 1L for now, until completion of the project.
	
	/**
	 * Represents the user's identification number.
	 */
	public final String user_id;
	
	/**
	 * Represents the user's full name, an identifier used to locate the userId.
	 */
	public final String full_name;
	
	/**
	 * A container that represents the user's list of albums.
	 */
	public List<Album> albums;
	
	/**
	 * Creates a Model object from user information specified by the command line.
	 * 
	 * @param id User Identification Number
	 * @param name User's Full Name
	 */
	public User(String id, String name)
	{
		this.user_id=id;
		this.full_name=name;
		this.albums= new ArrayList<Album>();
	}
	
	/**
	 * Creates a new album and adds it to the user's list.
	 * 
	 * @param albumName Title of the new album.
	 * @return if the creation of the album was successful, true is returned.
	 */
	public boolean createAlbum(Album albumName)
	{
		this.albums.add(albumName);
		return true;
	}
	
	/**
	 * Deletes the specified album from the user's list.
	 *  
	 * @param item Album object.
	 * @return if the deletion of the album was successful, true is returned.
	 */
	public boolean deleteAlbum(Album item)
	{
		this.albums.remove(item);
		return true;
	}
	
	/**
	 * Looks through the user's list of albums for the existence of the specified photo.
	 * 
	 * @param filename Name of Photo
	 * @return The photo is returned if the search is successful. Otherwise, null is returned.
	 */
	public Photo findPhoto(String filename)
	{
		Photo identified;
		
		for(int i=0; i<this.albums.size();i++)
		{
			identified= this.albums.get(i).findPhoto(filename);
			
			if(identified!=null)
			{
				return identified;
			}
		}
		
		return null;
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
		
		for(int i=0; i<this.albums.size();i++)
		{
			if(this.albums.get(i).album_name.compareToIgnoreCase(albumName)==0)
			{
				find_album=this.albums.get(i);
				return find_album;
			}
		}
		
		return null;
	}
	
	/**
	 * Performs a search on the user's albums, and adds any that contain the specified photo into
	 * a list data structure.
	 * 
	 * @param item Photo to be searched for.
	 */
	public void getAlbumList(Photo item)
	{
		// Traverse the album list
		
		Album tester;
		int index=0;
		
		for(int i=0; i<this.albums.size();i++)
		{
			// If you found the photo, add it to the photo's album collection
			
			if(this.albums.get(i).hasPhoto(item.file_name, item.caption)==true)
			{
				// make sure it doesn't exist already
				
				tester=this.albums.get(i);
				
				index=item.album_collection.indexOf(tester);
					
				if(index!=-1) // album exists already
				{
					continue;	
				}
			
				item.album_collection.add(this.albums.get(i));
			}
		}
	}
	
	/**
	 * Renames the title of the specified album.
	 * 
	 * @param album Album object that will have its name changed.
	 * @param name  New title to replace the album's prior one.
	 */
	public void renameAlbum(Album album, String name)
	{
		album.album_name=name;
	}
	
	/**
	 * A string representation of the User's information, displaying the user id and full name.
	 */
	public String toString()
	{
		String input="";
		
		input = "user id: "+this.user_id+"  ;  "+"user name: "+this.full_name;
		
		return input;
	}
	
	/**
	 * Overridden object equals method which facilitates the proper comparison among user objects in data structures.
	 * 
	 * @return If both user objects have the same Id, then true is returned.
	 */
	public boolean equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		
		if(!(o instanceof User))
		{
			return false;
		}
		
		User other= (User)o;
		
		if(this.user_id.compareTo(other.user_id)==0)
		{
			return true;
		}
		
		return false;
	}
}

