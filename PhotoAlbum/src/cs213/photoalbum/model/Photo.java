package cs213.photoalbum.model;

import java.util.*;
import java.io.*;
import java.text.*;

/**
 * This class represents a container for a photo object. The photo
 * will be utilized with the album class in this program. 
 *  
 * @author Gregory Grzymski
 * @since 02-16-2014
 *
 */

public class Photo implements Serializable 
{
	private static final long serialVersionUID = 1L; // 1L for now, until completion of the project.
	
	/**
	 * Field represents the photo's filename.
	 */
	public String file_name;
	
	/**
	 * Field represents the photo's caption, a description of the photo's attributes.
	 */
	public String caption;
	
	/**
	 *  A list data structure that represents the individual's complete collection of tag fields.
	 */
	public List<Tag> tag_list; // dynamic structure to handle the tag objects
	
	/** 
	 * A Calendar object that represents the date of modification for the photo.
	 */
	public Calendar date_taken; // This needs to be recorded as modification date instead.
	
	/**
	 * A Date conversion tool that displays the photo's date taken as
	 * MM/DD/YYYY-HH:mm:ss
	 */
	private SimpleDateFormat print_time; // will keep "MM/dd/yyyy-HH:mm:ss" time of date
	
	/**
	 * Date object that represents the printable form of the photo's date taken field.
	 */
	private Date time_stamp;
	
	/**
	 * Represents a list of albums that the photo belongs to.
	 */
	public List<Album> album_collection;
	
	public String belong_album;
	
	/**
	 * Creates a new photo object.
	 * 
	 * @param filename Name of the photo file.
	 * @param caption Description of the photo.
	 */
	public Photo(String filename, String caption, String album)
	{
		this.file_name=filename;
		this.belong_album= album;
		this.caption=caption;
		this.tag_list= new ArrayList<Tag>();
		this.album_collection= new ArrayList<Album>();
		
		File convert_photo= new File(filename);
		
		long date_record= convert_photo.lastModified(); // gets the date modified time.
		
		time_stamp= new Date(date_record); 
		
		date_taken=Calendar.getInstance();
		date_taken.setTime(time_stamp); // For date comparison methods.
		date_taken.set(Calendar.MILLISECOND, 0); // For accuracy purposes.
		
		print_time= new SimpleDateFormat("MM/dd/yyyy-HH:mm:ss"); // Example. 02/19/2014-21:37:31
	}
	
	/**
	 * Creates a String representation of the photo's date taken(modified) field.
	 * 
	 * @return Date in the format of "MM/DD/YYYY-HH:MM:SS"
	 */
	public String getTime()
	{
		String result;
		result=print_time.format(time_stamp);
		return result;
	}
	
	/**
	 * Retrieves the file name of the photo object.
	 * 
	 * @return  Photo file name.
	 */
	public String getPhotoName()
	{
		return this.file_name;
	}
	
	/**
	 * Retrieves the caption of the photo object.
	 * 
	 * @return Photo caption.
	 */
	public String getCaption()
	{
		return this.caption;
	}
	
	/**
	 * Adds a new tag item to the photo object.
	 * 
	 * @param new_item New Tag object to be added.
	 * @return True is returned for a successful add.
	 */
	public boolean addTag(Tag new_item)
	{
		this.tag_list.add(new_item);	
		return true;
	}
	
	/**
	 * Checks for the existence of an identical tag object.
	 * 
	 * @param item Tag item to be compared with.
	 * @return If the item already exists, true is returned.
	 */
	public boolean checkTag(Tag item)
	{
		boolean check_equal=true;
		
		for(int i=0; i<this.tag_list.size();i++)
		{
			check_equal= this.tag_list.get(i).equals(item);
			
			if(check_equal==true) // item already exists in the list
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Removes the specified tag object from the photo's list, if applicable.
	 * 
	 * @param item Tag object that was requested by the user for removal.
	 * 
	 * @return If removal is successful, true is returned. 
	 */
	public boolean deleteTag(Tag item)
	{
		boolean check_equal=true;
		
		for(int i=0; i<this.tag_list.size();i++)
		{
			check_equal= this.tag_list.get(i).equals(item);
			
			if(check_equal==true) // found the item in the list
			{
				this.tag_list.remove(this.tag_list.get(i));
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Overridden Object equals method that allows the comparison of photo objects to be
	 * based on their file names.
	 * 
	 * @param o Object type that is typecasted as a Photo if it is an instance of the class.
	 * 
	 * @return If both Photo objects are equal, true is returned.
	 */
	public boolean equals(Object o)
	{
		if(o==null)
		{
			return false;
		}
		
		if(!(o instanceof Photo))
		{
			return false;
		}
		
		Photo other= (Photo)o;
		
		if(this.file_name.equalsIgnoreCase(other.file_name))
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Changes the caption of the photo with the given input.
	 * 
	 * @param caption Description of photo
	 */
	public void changeCaption(String caption) 
	{
		this.caption = caption;
	}
	
	/**
	 * Retrieves the photo's complete tag list.
	 * 
	 * @return A list data structure of the photo tag list.
	 */
	public List<Tag> listTags()
	{
		return this.tag_list;
	}
	
	public String toString()
	{
		String entire="";
		
		// File Name
		
		entire = "File Name: "+ this.file_name+"\n";
		
		// Album 
		
		entire+="Album: "+this.belong_album+"\n";
		
		// Tag List
		
		entire+="Tags: ";
		
		for (int i=0; i<this.tag_list.size(); i++)
		{
			entire+=tag_list.get(i).tag_type +" , "+tag_list.get(i).tag_value+"\n";
		}
		
		return entire;
	}
}

