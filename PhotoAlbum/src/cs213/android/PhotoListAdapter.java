package cs213.android;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cs213.photoalbum.model.Album;
import cs213.photoalbum.model.Photo;

/**
 * 
 * @author Gregory Grzymski, Gaston Gonzalez
 *
 */

public class PhotoListAdapter extends BaseAdapter 
{
	List<Photo> photoList;
	Context context;
	LayoutInflater inflater;
	Bitmap mPlaceHolderBitmap;
	
	public PhotoListAdapter(Context c, Album a)
	{
		super();
		this.context=c;
		this.photoList=a.photos;
		mPlaceHolderBitmap=BitmapFactory.decodeResource(context.getResources(),R.drawable.empty);
		
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public int getCount() 
	{
		return this.photoList.size();
	}

	
	public Object getItem(int arg0) 
	{
		return this.photoList.get(arg0);
	}

	
	public long getItemId(int arg0) 
	{
		return arg0;
	}
	
	 public class PhotoIcon
	 {
	    	ImageView icon; // image of an album folder
	    	TextView caption; //name of the album;
	 }

    public View getView(int arg0, View convertView, ViewGroup arg2) 
    {
    	PhotoIcon img;
    	
        if (convertView == null) 
        {
        	convertView = inflater.inflate(R.layout.photolist, null);
        	
        	img = new PhotoIcon();
        	
            img.icon = (ImageView) convertView.findViewById(R.id.photo);
            img.icon.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 
            convertView.setTag(img);
        } 
        else 
        {
        	img = (PhotoIcon) convertView.getTag();
        }
 
        String filepath = new File(context.getFilesDir() + File.separator+ photoList.get(arg0).file_name).getAbsolutePath();
        loadBitmap(filepath, img.icon);
        return convertView;
	}
    
    public void loadBitmap(String resId, ImageView imageView) 
    {
		 BitMapWorkerTask task = new BitMapWorkerTask(imageView);
		 final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
		 imageView.setImageDrawable(asyncDrawable);
		 task.execute(resId);	
	}
    
    private static BitMapWorkerTask getBitmapWorkerTask(ImageView imageView) 
    {
    	   if (imageView != null) 
    	   {
    	       final Drawable drawable = imageView.getDrawable();
    	       if (drawable instanceof AsyncDrawable) 
    	       {
    	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
    	           return asyncDrawable.getBitmapWorkerTask();
    	       }
    	    }
    	    return null;
    }
    public static boolean cancelPotentialWork(String data, ImageView imageView) 
    {
        final BitMapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData.compareTo(data)!=0) 
            {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }
   
    
    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitMapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                BitMapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                new WeakReference<BitMapWorkerTask>(bitmapWorkerTask);
        }

        public BitMapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }
    
    class BitMapWorkerTask extends AsyncTask<String, Void, Bitmap> 
    {

		private WeakReference<ImageView> imageViewReference = null;
	    private String data = null;

	    public BitMapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	    	imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) 
	    {
	    	data = params[0];
            final Bitmap bitmap = decodeSampledBitmapFromFile(data,100,100);
            return bitmap;
	    }
	    
	    public Bitmap decodeSampledBitmapFromFile(String resId, int reqWidth, int reqHeight) 
	    {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(resId, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	        
	        // Decode bitmap with inSampleSize set
	        
	        options.inJustDecodeBounds = false;
	        Bitmap image = BitmapFactory.decodeFile(resId, options);
	        
	        Bitmap for_real = Bitmap.createScaledBitmap(image, 100, 100, true);
	        
	        image.recycle();
	        
	        return for_real;
	    }
	    
	    public int calculateInSampleSize (BitmapFactory.Options options, int reqWidth, int reqHeight) 
	    {
		    // Raw height and width of image
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
	
		    if (height > reqHeight || width > reqWidth) 
		    {
	
		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;
	
		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) 
		        {
		            inSampleSize *= 2;
		        }
		    }

		    return inSampleSize;
	    }
	        

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) 
	    {
	        if (imageViewReference != null && bitmap != null) 
	        {
	            ImageView imageView = imageViewReference.get();
	            final BitMapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
	            
	            if (imageView != null && this==bitmapWorkerTask) 
	            {
	                imageView.setImageBitmap(bitmap);
	                bitmap = null;
	                imageView = null;
	            } else {
	            	bitmap.recycle();
	            }
	        }
	    }	
	}
}
