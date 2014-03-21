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

    public static void capture()
    {

        OpenCVFrameGrabber frameGrabber = new OpenCVFrameGrabber(0);
        try {
            frameGrabber.start();
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        CanvasFrame canvasFrame = new CanvasFrame("Detect Sequence");
        canvasFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        while (canvasFrame.isShowing())
        {
            opencv_core.IplImage image = null;
            try {
                image = frameGrabber.grab();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }

            if(image!=null) canvasFrame.showImage(image);
            BufferedImage capturedImage = image.getBufferedImage();

            try {
                ImageIO.write(capturedImage, "png", new File("savedImage"));
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }



    public static void main(String[] args) {
        capture();
    }
}
