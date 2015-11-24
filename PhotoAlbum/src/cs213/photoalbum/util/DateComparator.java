package cs213.photoalbum.util;

import java.util.*;

import cs213.photoalbum.model.Photo;

/**
 * This class represents a Comparator to properly sort photos by date within an album. 
 * The order of the album's photo date fields must be maintained chronologically. 
 * 
 * @author Gregory Grzymski
 *
 */
public class DateComparator implements Comparator<Photo>
{
	/**
	 * Compares two photo objects based on their date fields.
	 * 
	 * @param one First photo object.
	 * @param two Second photo object.
	 * @return If the first photo's date is before the second one, -1 is returned.
	 * 
	 *  If the first photo's date is equal to the second photo's date, 0 is returned.
	 *  
	 *  If the first photo's date is after the second photo's date, 1 is returned.
	 * 
	 */
	public int compare(Photo one, Photo two)
	{
		Calendar a=one.date_taken;
		Calendar b= two.date_taken;
		
		if(a.before(b))
		{
			return -1;
		}
		if(a.after(b))
		{
			return 1;
		}
		
		return 0;
	}

}

