//  Copyright 2012 Zonghai Li. All rights reserved.
//
//  Redistribution and use in binary and source forms, with or without modification,
//  are permitted for any project, commercial or otherwise, provided that the
//  following conditions are met:
//  
//  Redistributions in binary form must display the copyright notice in the About
//  view, website, and/or documentation.
//  
//  Redistributions of source code must retain the copyright notice, this list of
//  conditions, and the following disclaimer.
//
//  THIS SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
//  INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
//  PARTICULAR PURPOSE AND NONINFRINGEMENT OF THIRD PARTY RIGHTS. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
//  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
//  CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THIS SOFTWARE.


package httpimage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;


/**
 * Bimtap encoding/decoding helper methods based on BitmapFactory
 * 
 * @author zonghai
 */
public class BitmapUtil {

    private static final int UNCONSTRAINED = -1;

    public static Bitmap decodeByteArray( byte[] bytes, int maxNumOfPixels) {
        
        if (bytes == null) return null;
        
        try {
            BitmapFactory.Options option = new BitmapFactory.Options();
            // Decode only image size
            option.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option);

            option.inJustDecodeBounds = false;
            option.inSampleSize = computeSampleSize(option, UNCONSTRAINED,
                    maxNumOfPixels);

            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option);//scaleCenterCrop(BitmapFactory.decodeByteArray(bytes, 0, bytes.length, option),133,220);

        } catch (OutOfMemoryError oom) {

        	oom.printStackTrace();
            return null;
        }
    }
    
	public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
		try {
			int sourceWidth = source.getWidth();
			int sourceHeight = source.getHeight();

			// Compute the scaling factors to fit the new height and width,
			// respectively.
			// To cover the final image, the final scaling will be the bigger
			// of these two.
			float xScale = (float) newWidth / sourceWidth;
			float yScale = (float) newHeight / sourceHeight;
			float scale = Math.max(xScale, yScale);

			// Now get the size of the source bitmap when scaled
			float scaledWidth = scale * sourceWidth;
			float scaledHeight = scale * sourceHeight;

			// Let's find out the upper left coordinates if the scaled bitmap
			// should be centered in the new size give by the parameters
			float left = (newWidth - scaledWidth) / 2;
			float top = (newHeight - scaledHeight) / 2;

			// The target rectangle for the new, scaled version of the source
			// bitmap will now
			// be
			RectF targetRect = new RectF(left, top, left + scaledWidth, top
					+ scaledHeight);

			// Finally, we create a new bitmap of the specified size and draw
			// our new,
			// scaled bitmap onto it.
			Bitmap dest = Bitmap.createBitmap(newWidth, newHeight,
					source.getConfig());
			Canvas canvas = new Canvas(dest);
			canvas.drawBitmap(source, null, targetRect, null);

			return dest;
		} catch (OutOfMemoryError e) {
			return source;
		} catch (Exception e) {
			return source;
		}

	}
    public static Bitmap decodeStream(InputStream is, int maxNumOfPixels) {

        if (is == null) return null;
        
        try {
            return decodeByteArray( readStream(is), maxNumOfPixels);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    
    public static Bitmap decodeFile(String filePath, int maxNumOfPixels) {
        
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(filePath));
            return decodeByteArray(readStream(fis), maxNumOfPixels);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
        }
        
    }
    
    /*
     * Compute the sample size as a function of minSideLength and
     * maxNumOfPixels. minSideLength is used to specify that minimal width or
     * height of a bitmap. maxNumOfPixels is used to specify the maximal size in
     * pixels that is tolerable in terms of memory usage.
     * 
     * The function returns a sample size based on the constraints. Both size
     * and minSideLength can be passed in as IImage.UNCONSTRAINED, which
     * indicates no care of the corresponding constraint. The functions prefers
     * returning a sample size that generates a smaller bitmap, unless
     * minSideLength = IImage.UNCONSTRAINED.
     * 
     * Also, the function rounds up the sample size to a power of 2 or multiple
     * of 8 because BitmapFactory only honors sample size this way. For example,
     * BitmapFactory downsamples an image by 2 even though the request is 3. So
     * we round up the sample size to avoid OOM.
     */
    private static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    

    private static int computeInitialSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    
    private static byte[] readStream(InputStream is) throws IOException {
        int len;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            int size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[1024];
            while ((len = is.read(buf, 0, 1024)) != -1)
                bos.write(buf, 0, len);
            buf = bos.toByteArray();
        }
        
        return buf;
    }
}
