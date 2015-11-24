package cs213.photoalbum.control;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import android.content.Context;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.util.DateComparator;
import cs213.photoalbum.util.TagComparator;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;
import cs213.photoalbum.model.Tag;
import cs213.photoalbum.model.User;

/** This class represents the Control of the Photo Library implementation. It handles the algorithmic
 *  logic, decision making, and data manipulation processes of the program. The control parses the incoming
 *  method calls from the interactive mode, and performs those operations on the specified User object.
 *  When the computations are finished, the results are returned back to the View for printing. 
 * 
 * @author Gregory Grzymski, Gaston Gonzalez
 *
 */
public class Controller
{
	
	/**
	 * Represents a model for the user that was queried by the command view. 
	 */
	public User user;
	
	/**
	 * Represents a flag for whether the user remains logged in or not. If false,
	 * then the user chooses to logout. This field is relayed back to the command view
	 * to halt user prompts.
	 */
	public boolean loggedIn;
	
	
	public Context context;
	
	public List<Album> albums;
	
	public Backend backend;
	
	/**
	 * Initializes a new controller that is supported by the provided backend object. At default,
	 * the user is logged in. The controller will perform its operations on the user's model. 
	 * 
	 * @param b Backend driver for the user's session.
	 */
	public Controller(Context c, List<Album> a, Backend b)
	{
		this.albums=a;
		this.context=c;
		this.backend =b;
		this.loggedIn=true;
	}
	
	/**
	 * Controller queries the model to create a new album and add it to the user's list, if applicable.
	 * 
	 * @param albumName Title of the new album.
	 * @return if the creation of the album was successful, true is returned.
	 */
	public boolean createAlbum(String albumName)
	{
		// Check if the album exists already
		
		Album identified= new Album(albumName);
				
		boolean check=this.backend.list_albums.contains(identified);
		
		if(check==true) // album already exists
		{
			return false;
		}
		
		else // add the album to the list
		{
			backend.list_albums.add(identified);
		}
		
		return true;
	}
	
	/**
	 * Deletes the specified album from the user's list, if applicable.
	 *  
	 * @param albumName Name of album.
	 * @return if the deletion of the album was successful, true is returned.
	 */
	public boolean deleteAlbum(String albumName)
	{
		// First, see if the album exists
		
		Album find_album= new Album(albumName);
		
		boolean check=this.backend.list_albums.contains(find_album);
		
		if(check==false) // album wasn't found
		{
			return false;
		}
		else // album was found
		{
			this.backend.list_albums.remove(find_album);
		}
		
		return true;
	}
	
	/**
	 *  Renames the title of the old album with the user input. The original album is deleted with its photo list
	 *  stored, and the newer album is added with the same photo list. 
	 *  
	 * @param old Older album to remove from the list.
	 * @param newer The album that contains the new name that needs to replace the older one.
	 */
	public void renameAlbum(String old, String newguy)
	{
		Album older = this.backend.findAlbum(old);
		
		Album newer = new Album(newguy);
		
		List<Photo> old_photos = older.photos;
		newer.photos= old_photos;
		
		this.backend.list_albums.remove(older);
		this.backend.list_albums.add(newer);
	}
	
	/**
	 * Controller returns the user's current list of albums with their number of photos and 
	 * the range of dates that the photos were taken.
	 * 
	 * @return List data structure that contains all of the user's albums.
	 */
	public List<Album> listAlbums()
	{
		// check if albums exist
		
		if(user.albums.size()==0)
		{
			return null;
		}
		
		return this.user.albums;
	}
	

	/** 
	 * Controller retrieves the photo library for the specific album with name and date displayed.
	 * 
	 * @param albumName Name of album.
	 * @return List data structure containing the album's photos.
	 */
	public List<Photo> listPhotos(String albumName)
	{
		 // Check if album exists.
		
		Album find_me= new Album(albumName);
		
		boolean check=this.user.albums.contains(find_me);
		
		if(check==false)
		{
			return null;
		}
		
		int index= this.user.albums.indexOf(find_me);
		
		return this.user.albums.get(index).photos;
	}
	
	/**
	 * Adds a photo to the current album, if applicable.
	 * 
	 * @param filename Name of photo.
	 * @param caption Caption description for photo.
	 * @param albumName Name of album.
	 * @return String codes are returned according to the result of the method:
	 * 
	 * "file": The Photo file wasn't found.
	 * "album": The album couldn't be found.
	 * "exist": The photo already exists.
	 *  caption: A successful addition returns the caption of the photo.
	 */
	public boolean addPhoto(String filename, String caption, String albumName) throws FileNotFoundException, IOException
	{
		// Find out if the photo file exists already in the project data directory

		File existing = new File(context.getFilesDir(), caption);
				
		if(existing.exists()==false) // need to the write the file into our directory for the whole program
		{
			FileInputStream input = new FileInputStream(filename); 
			FileOutputStream output = new FileOutputStream(existing);
			
			// i need bytes because it is an image file
			
			byte [] storage = new byte[2056];
			
			int unit;
			
			while ((unit = input.read(storage)) > 0) 
			{
				output.write(storage, 0, unit);
			}
			
			input.close();
			output.close();
		}
		
		// find the album 
		
		Album identified= backend.findAlbum(albumName);
		
		// Once the album is found, check if the the photo exists already.
		
		boolean check;
		
		check= identified.hasPhoto(caption,caption);
		
		if(check==true) // photo already exists
		{
			return false;
		}
		
		// Add the photo the album
		
		Photo newPhoto= new Photo(caption, caption,albumName);
		
		// Add Existing Tag List if Possible
		
		for(int i=0; i<backend.list_albums.size();i++)
		{
			for(int j=0; j<backend.list_albums.get(i).photos.size();j++)
			{
				if(backend.list_albums.get(i).photos.get(j).file_name.compareTo(newPhoto.file_name)==0)
				{
					if(backend.list_albums.get(i).photos.get(j).tag_list.size()!=0)
					{
						for(int k =0; k<backend.list_albums.get(i).photos.get(j).tag_list.size();k++)
						{
							newPhoto.tag_list.add(backend.list_albums.get(i).photos.get(j).tag_list.get(k));
						}
						
						break;
					}
				}
			}
		}
		identified.addPhoto(newPhoto);
	
		return true;
		
	}
	
	/**
	 * Moves a photo from one album to another, if applicable.
	 * 
	 * @param filename File name of the Photo to be moved.
	 * @param oldAlbum Name of the current album that contains the photo.
	 * @param newAlbum Name of the album to have the photo moved to.
	 * @return An integer value is returned according to the result of the method:
	 * 
	 * 1: The photo wasn't found.
	 * 2: The photo already exists in the new album.
	 * 3: The photo was successfully moved into the new album.
	 */
	public int movePhoto(String filename, String oldAlbum, String newAlbum)
	{
		// First check if the photo exists in the oldAlbum
		
		Album old= backend.findAlbum(oldAlbum);
		Album newalbum = backend.findAlbum(newAlbum);
		
		Photo find_me=old.findPhoto(filename);
		
		if(find_me==null)
		{
			return 1;
		}
		
		// need to make sure the photo doesn't already exist in new album //
		
		if(newalbum.photos.contains(find_me))
		{
			return 2;
		}
		
		// just call the remove and add photo methods
		
		old.removePhoto(filename);
		newalbum.addPhoto(find_me);
				
		return 3;
	}
	
	/**
	 * Removes specified photo from album, if applicable.
	 * 
	 * @param filename Name of photo.
	 * @param albumName Name of album.
	 * @return if item is successfully removed, true is returned.
	 */
	public boolean removePhoto(String filename, String albumName)
	{
		// Find the album and see if it exists.
		
		Album find_album= backend.findAlbum(albumName);
		
		if(find_album==null) // Couldn't find album
		{
			return false;
		}
		
		// You have the album, now find the photo.
		
		Photo find_photo=find_album.findPhoto(filename);
		
		if(find_photo==null) // couldn't find the photo
		{
			return false;
		}
		
		// Delete the photo
		
		find_album.removePhoto(filename);
		
		return true;
	}
	
	/**
	 * Adds a new tag to the photo object, only if it doesn't exist already.
	 * 
	 * @param filename Name of the photo file
	 * @param tagType Type of tag object.
	 * @param tagValue Value of tag object.
	 * @return if the tag was successfully added, true is returned.
	 */
	public boolean addTag(String filename, String tagType, String tagValue)
	{
		// Find the photo first by looking through the albums
		
		Photo item;
		
		item=backend.findPhoto(filename);
		
		// Once found, attempt to add the tag to it.
		
		Tag new_item= new Tag(tagType,tagValue);
		
		boolean equal_check= item.checkTag(new_item); // check for existing tag type and value
		
		if(equal_check==true) /* Existing Tag was found */
		{
			return false;
		}
		
		// Make sure it doesn't have location already.
		
		if(tagType.compareToIgnoreCase("Location")==0)
		{
			for(int i=0; i<item.tag_list.size(); i++)
			{
				if(item.tag_list.get(i).tag_type.compareToIgnoreCase("Location")==0)
				{
					return false;
				}
			}
		}
		
		// Get Album list and make sure tag is added to every photo instance in all the albums
		
		this.backend.getAlbumList(item);
		
		for(int i=0; i<item.album_collection.size();i++)
		{
			Photo find = item.album_collection.get(i).findPhoto(filename);
			find.addTag(new_item);
			
			/* sort values and types */
			
			Collections.sort(find.tag_list);
			Collections.sort(find.tag_list,new TagComparator());
		}
		
		return true;
	}
	
	/**
	 * Removes the specified tag object from the photo's list, if applicable.
	 * 
	 * @param filename Name of the photo file.
	 * @param tagType Tag type field.
	 * @param tagValue Tag value field.
	 * @return If removal is successful, true is returned. 
	 */
	public boolean deleteTag(String filename, String tagType, String tagValue)
	{
		// Make sure photo exists
		
		Photo find_photo= backend.findPhoto(filename);
		
		if(find_photo==null) // photo wasn't found
		{
			return false;
		}
		
		// If photo exists, search its tag list.
		
		boolean search_check=true;
		
		Tag search= new Tag(tagType,tagValue);
		
		search_check=find_photo.checkTag(search);
		
		if(search_check==false) // couldn't find tags
		{
			return false;
		}
		
		// it must exist
		
		this.backend.getAlbumList(find_photo);
		
		for(int i=0; i<find_photo.album_collection.size();i++)
		{
			Photo find = find_photo.album_collection.get(i).findPhoto(filename);
			find.deleteTag(search);
			
			/* sort values and types */
			
			Collections.sort(find.tag_list);
			Collections.sort(find.tag_list,new TagComparator());
		}
		
		return true;
	}
	
	/**
	 * Controller retrieves the photo's details: filename, album, date, caption, and its tags.
	 * Photo may exist in multiple albums, or may not exist at all.
	 * 
	 * @param fileName File name of photo.
	 * @return if the photo exists, its object is returned.
	 */
	public Photo listPhotoInfo(String fileName)
	{
		// First, find the photo from one of the user's albums.
		
		Photo find_me= user.findPhoto(fileName);
		
		if(find_me==null) // couldn't find the photo
		{
			return null;
		}
		
		// I need to obtain all the albums the photo exists in.
		
		user.getAlbumList(find_me);
		
		return find_me;
	}
	
	/**
	 * Retrieves all photos taken within the given range of dates, in chronological order.
	 * 
	 * @param startDate String field that provides a starting range for retrieving the photos. 
	 * @param endDate String field that provides an ending range for retrieving the photos.
	 * @return A list data structure that contains all the photos within the valid date range. 
	 */
	public List<Photo> getPhotosByDate(String startDate, String endDate)
	{
		DateFormat df= new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss",Locale.ENGLISH);
		Date start;
		Date end;
		Calendar tester;
		Calendar starter;
		Calendar ender;
		
		int index=0;
		
		List<Photo> valid = new ArrayList<Photo>();
		
		try
		{
			df.setLenient(false);
			start= df.parse(startDate);
			end=df.parse(endDate);
		}
		catch(Exception e)
		{
			return null;
		}
		
		starter=Calendar.getInstance();
		ender= Calendar.getInstance();
		tester=Calendar.getInstance();
		
		
		starter.setTime(start);
		starter.set(Calendar.MILLISECOND,0);
		ender.setTime(end);
		ender.set(Calendar.MILLISECOND,0);
		
		// I need to find the photos within the given range, and get their album lists.
		
		for(int i=0; i<user.albums.size();i++)
		{
			// Look through each album's photo list
			
			for(int j=0; j<user.albums.get(i).photos.size();j++)
			{
				// Check if the photo is within the date range.
				
				tester=user.albums.get(i).photos.get(j).date_taken;
				tester.set(Calendar.MILLISECOND,0);
				
				
				if(tester.after(starter) && tester.before(ender)) // valid match, add it to the list
				{
					index=valid.indexOf(user.albums.get(i).photos.get(j));
					
					if(index==-1)
					{
						valid.add(user.albums.get(i).photos.get(j));
					
						// maintain the photo's album collection
					
						user.getAlbumList(user.albums.get(i).photos.get(j));
					}
				}
			}
		}
		
		Collections.sort(valid, new DateComparator());
		
		return valid;
	}
	
	/**
	 * Retrieves all the photos that contain the specified tags. Caption, album name(s), and
	 * date are printed in the view.
	 * 
	 * @param tags A list of tag objects that contains all the possible values given by
	 * the user.
	 * 
	 * @return A list data structure of photos that were found with the specified tag inputs.
	 */
	public List<Photo> getPhotosByTag(List<Tag> tags)
	{
		List<Photo> photos = new ArrayList<Photo>();
		int index=0;
		boolean check=false;
		int flag=0;
		
		for(int i=0;i<this.user.albums.size();i++)
		{
			for(int j=0; j<this.user.albums.get(i).photos.size();j++)
			{	
				for(int k=0; k<tags.size();k++)
				{   
					check=false;
					flag=0;
					
					if(tags.get(k).tag_type.length()==0)
					{
						for(int l=0; l<this.user.albums.get(i).photos.get(j).tag_list.size();l++)
						{
							if(this.user.albums.get(i).photos.get(j).tag_list.get(l).tag_value.compareToIgnoreCase(tags.get(k).tag_value)==0)
							{
								check=true;
								flag=1;
							}
						}
					}
					else
					{
						if(this.user.albums.get(i).photos.get(j).tag_list.contains(tags.get(k)))
						{
							check=true;
							flag=1;
						}
					}
					
					if(flag==0)
					{
					    check=false;
						break;
					}
				}
				
				if(check==true)
				{
					index=photos.indexOf(user.albums.get(i).photos.get(j));
					
					if(index==-1)
					{
						photos.add(user.albums.get(i).photos.get(j));
					
						// maintain the photo's album collection
					
						user.getAlbumList(user.albums.get(i).photos.get(j));
					}
				}
			}
		}
		
		Collections.sort(photos, new DateComparator());
		
		return photos;
	}
	
	public List<Album> getAlbums()
	{
		return this.albums;
	}
}

