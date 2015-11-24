package cs213.android;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Backend;
import cs213.photoalbum.model.Photo;

/**
 * 
 * @author Gregory Grzymski , Gaston Gonzalez
 *
 */
public class SlideShow extends Activity 
{
	String album;
	String photo;
	ImageView previous;
	ImageView next;
	Backend b;
	List<Photo> photos; // photos to display
	Context context;
	Album portray;
	private ImageView imageView;
	private BitmapFactory.Options options;
	
	int index = 0;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.slideshow);
		initialize();
	}
	
	private void initialize()
	{
		context=this;
		b = Backend.getInstance(context);
		
		album = getIntent().getExtras().getString(Constants.ALBUM_NAME);
		photo = getIntent().getExtras().getString(Constants.PHOTO_NAME);
		
		portray = b.findAlbum(album);
		
		photos = portray.photos;
		
		imageView = (ImageView) findViewById(R.id.full_image_view);
		
		options = new BitmapFactory.Options();
		options.inSampleSize = 5;
		options.inPurgeable = true; // 384 x 216
		options.inJustDecodeBounds = false;
		
		String path = this.getFilesDir() + File.separator + photo;
		
		for(int i=0; i<photos.size();i++)
		{
			if(photos.get(i).file_name.compareTo(photo)==0)
			{
				index=i;
			}
		}
		
		Bitmap bips = BitmapFactory.decodeFile(path, options);
		imageView.setImageBitmap(bips);
		
		previous = (ImageView) findViewById(R.id.previous);
		next = (ImageView) findViewById(R.id.next);
		
		previous.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				handleLeft();
				
				Photo p = photos.get(index);
					
				String path = context.getFilesDir() + File.separator + p.file_name;
				Bitmap b = BitmapFactory.decodeFile(path, options);
				imageView.setImageBitmap(b);
				imageView.invalidate();
				imageView.refreshDrawableState();
			}
		});
		
		next.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				handleRight();
				
				Photo p = photos.get(index);
					
				String path = context.getFilesDir() + File.separator + p.file_name;
				Bitmap b = BitmapFactory.decodeFile(path, options);
				imageView.setImageBitmap(b);
				imageView.invalidate();
				imageView.refreshDrawableState();
			}
		});
	}
	
	private void handleLeft()
	{
		if(index==0)
		{
			previous.setEnabled(false);
			return;
		}
		
		next.setEnabled(true);
		index--;
	}
	
	private void handleRight()
	{
		if(index==photos.size()-1)
		{
			next.setEnabled(false);
			return;
		}
		
		previous.setEnabled(true);
		index++;
	}
}
