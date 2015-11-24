package cs213.photoalbum.model;

import java.util.*;
import java.io.Serializable;

/**
 * This class represents a container for an album object. The album
 * will be utilized with the User class in this program.
 * 
 * @author Gregory Grzymski
 * @since 02-16-2014
 */
public class Album implements Serializable
{
	private static final long serialVersionUID = 1L; // 1L for now, until completion of the project.
	
	/**
	 * Field for the name of the album.
	 */
	public String album_name;
	
	/**
	 *  A list data structure that represents the album's collection of photos. 
	 */
	public List<Photo> photos;
	
	/**
	 * Creates a new album object with the specified title.
	 * 
	 * @param name Name of new photo album.
	 */
	public Album(String name)
	{
		this.album_name=name;
		photos=new ArrayList<Photo>();
	}
	
	/**
	 * Adds a photo to the current album.
	 * 
	 * @param newPhoto Photo object queried by the user to add into the specific album.
	 * @return True is returned after successful addition.
	 */
	public boolean addPhoto(Photo newPhoto)
	{
		this.photos.add(newPhoto);
		
		return true;
	}
	
	/**
	 * Removes specified photo from the album, if applicable.
	 * 
	 * @param filename Name of photo.
	 * @return If item is successfully removed, true is returned.
	 */
	public boolean removePhoto(String filename)
	{
		for(int i=0; i<this.photos.size();i++)
		{
			if(this.photos.get(i).file_name.compareToIgnoreCase(filename)==0) // found the photo
			{
				this.photos.remove(i);
				return true;
			}
		}
		
		return false;
	}
	
	/** 
	 *  Provides the controller with the album's photo library for listing purposes.
	 *  
	 *  @return A list data structure that contains the album's photos. 
	 */
	public List<Photo> listPhotos()
	{
		return this.photos;
	}
	
	/**
	 * Identifies the total amount of photos within the album. Assists with the process
	 * of listing all of the user's albums.
	 * 
	 * @return Total number of photos.
	 */
	public int getNumPhotos()
	{
		return this.photos.size();
	}
	
	/**
	 * Attempts to find the specific photo within the album.
	 * 
	 * @param filename Name of photo.
	 * @return The photo is returned if successful. Otherwise, null is returned.
	 */
	public Photo findPhoto(String filename)
	{
		Photo find_photo;
		
		for(int i=0; i<this.photos.size();i++)
		{
			if(this.photos.get(i).file_name.compareToIgnoreCase(filename)==0)
			{
				find_photo= this.photos.get(i);
				return find_photo;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks for the existence of a photo in the album.
	 * 
	 * @param filename Name of photo.
	 * @param caption Description of photo.
	 * @return If the photo exists within the album, then true is returned.
	 */
	public boolean hasPhoto(String filename, String caption)
	{
		for(int i=0; i<photos.size();i++)
		{
			if(this.photos.get(i).file_name.compareToIgnoreCase(filename)==0)
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Overridden Object equals method that allows the comparison of album objects to be based
	 * on their names(titles).
	 * 
	 * @return If both Album objects are equal, true is returned.
	 * 
	 */
	public boolean equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		
		if(!(o instanceof Album))
		{
			return false;
		}
		
		Album other= (Album)o;
		
		if(this.album_name.equalsIgnoreCase(other.album_name))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * String representation of the User's album. The album's representation is by its name.
	 * 
	 */
	public String toString()
	{
		String enter="";
		enter= this.album_name;
		
		return enter;
	}
}

