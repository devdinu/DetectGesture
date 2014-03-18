/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objdetect;
import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;

public class EchoDemo {
  public static void main(String[] args) throws Exception {
    // Grab the default video defive. This work for both built-win 
    // and usb webcams.
    CvCapture capture = opencv_highgui.cvCreateCameraCapture(0);

    // Set the capture resolution 480x320 gives decent quality 
    // and the lower resolution will make our real-time video 
    // processing a little faster.
    opencv_highgui.cvSetCaptureProperty(capture,
        opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 320);
    opencv_highgui.cvSetCaptureProperty(capture,
        opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 480);

    // Construct a JavaCV Image that matches the properties of the 
    // captured imaged.
    IplImage grabbedImage = opencv_highgui.cvQueryFrame(capture);

    // Create a frame to echo to.
    CanvasFrame frame = new CanvasFrame("Echo Demo", 1);

    // Keep looping while our frame is visable and we're getting 
    // images from the web cam
    while (frame.isVisible()
        && (grabbedImage = opencv_highgui.cvQueryFrame(capture)) 
          != null) {
      // display cpatured image on frame
      frame.showImage(grabbedImage);
    }

    // clean up after ourselves.
    frame.dispose();
    opencv_highgui.cvReleaseCapture(capture);
  }
}