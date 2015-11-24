package cs213.photoalbum.model;

import java.io.Serializable;

	/**
	 * This class represents the Tag field in an individual photo.
	 * @author Gregory Grzymski
	 * @since 02-16-2014
	 */
public class Tag implements Serializable, Comparable<Tag>
{
	private static final long serialVersionUID = 1L; // 1L for now, until completion of the project.
	
	/**
	 * Represents the tag type of the photo's tag object.
	 */
	public String tag_type;
	
	/**
	 * Represents the tag value of the photo's tag object.
	 */
	public String tag_value;
	
	/**
	 * Creates a new tag object to be used with the photo container.
	 * 
	 * @param tagtype  Type of tag, which can represent location, person's name, etc.
	 * @param tagvalue Specific description for tag type.
	 */
	public Tag(String tagtype, String tagvalue)
	{
		this.tag_type= tagtype;
		this.tag_value=tagvalue;
	}
	
	/**
	 * Overriding Object Equals method in order to test for equivalent tag types and values.
	 * 
	 * @param o Object overridden as a Tag in order to properly compare tag types and values.
	 * 
	 * @return If the tag objects are equal, true is returned.
	 */
	public boolean equals(Object o)
	{
		if(o==null || !(o instanceof Tag))
		{
			return false;
		}
		
		Tag other= (Tag) o;
		
		if(this.tag_type.compareTo(other.tag_type)==0 && this.tag_value.compareToIgnoreCase(other.tag_value)==0)
		{
			return true; // Need to let user know this isn't allowed.
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Overrides Object comparable method in order to compare and sort tag values.
	 * 
	 * @param o Tag object argument to be compared with. 
	 * @return if the object is greater, 1 is returned.
	 * 		   if the object is equal, 0 is returned.
	 * 		   if the object is less than, -1 is returned.
	 */
	public int compareTo(Tag o)
	{
		return this.tag_value.compareToIgnoreCase(o.tag_value);
	}
	
	public String toString()
	{
		String output = tag_type + ": " + tag_value;
;
		return output;
	}
}

