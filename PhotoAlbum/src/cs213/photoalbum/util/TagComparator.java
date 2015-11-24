package cs213.photoalbum.util;

import java.util.Comparator;

import cs213.photoalbum.model.Tag;

/**
 * This class represents a Comparator to properly sort tag types within a photo object.
 * The order of location, person, and all other fields must be maintained respectively. 
 * 
 * @author Gregory Grzymski
 *
 */
public class TagComparator implements Comparator<Tag> 
{
	/**
	 * Sorts the Tag items through their tag types. Sort order is: location, person, and other fields.
	 * 
	 * @param o1 First Tag object.
	 * @param o2 Second Tag object.
	 * 
	 * @return If the first tag type comes before the second tag type, -1 is returned. If both
	 * items are equal to each other, 0 is returned. If the first tag type comes after the second tag type,
	 * 1 is returned.
	 */
	public int compare(Tag o1, Tag o2)
	{
		boolean o1_others_check=false;
		boolean o2_others_check=false;
		
		// identify others
		
		if(o1.tag_type.compareTo("location")!=0 && o1.tag_type.compareTo("person")!=0)
		{
			o1_others_check=true;
		}
		
		if(o2.tag_type.compareTo("location")!=0 && o2.tag_type.compareTo("person")!=0)
		{
			o2_others_check=true;
		}
		
		// what if both are others? Who cares just throw them in
		
		if(o1_others_check==true && o2_others_check==true)
		{
			return 0;
		}
		
		// always put lecture and person before others
		
		if(o1_others_check==false && o2_others_check==true)
		{
			return -1;
		}
		
		// always put other ahead of lecture and person
		
		if(o1_others_check==true && o2_others_check==false)
		{
			return 1;
		}
		
		// if location is compared to a person on the list, put it before.
		
		if(o1.tag_type.compareTo("location")==0 && o2.tag_type.compareTo("person")==0)
		{
			return -1;
		}
		
		// if person is compared to a location on the list, put it after.
		
		if(o1.tag_type.compareTo("person")==0 && o2.tag_type.compareTo("location")==0)
		{
			return 1;
		}
		
		return 0;
	}
}

