package objdetect;


import com.googlecode.javacv.CanvasFrame;
import com.googlecode.*;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.cvkernels;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_imgproc;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by dinesh on 3/20/14.
 */
public class PhotoCapturer {

    public void capture(opencv_core.IplImage iplImage,String fileName)
    {

        BufferedImage capturedImage= new BufferedImage(Configurations.WIDTH,Configurations.HEIGHT,BufferedImage.TYPE_INT_RGB);
        iplImage.copyTo(capturedImage);
            try {
                ImageIO.write(capturedImage, "png", new File(fileName));
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

}
