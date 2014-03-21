/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objdetect;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_objdetect.*;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.CanvasFrame;
import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import com.googlecode.javacv.cpp.opencv_highgui;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;
import java.awt.Point;
import java.util.ArrayList;

public class SequenceDetector {

    public static SystemState currentSystemState = SystemState.INITIAL;
    public static SystemState previousSystemState = null;

    public enum SystemState {

        INITIAL,
        SEQUENCE_DETECTED,
        FIST_DETECTED,
        PALM_DETECTED,}
    public static ArrayList<Character> sequenceOfGestures = new ArrayList<Character>();

    public void detect() {

        // Load object detection
        Loader.load(opencv_objdetect.class);


        // Construct classifiers from xml.
//        CvHaarClassifierCascade faceClassifier = loadHaarClassifier("haarcascade_frontalface_alt.xml");
//        CvHaarClassifierCascade handClassifier = loadHaarClassifier("hand1.xml");
        CvHaarClassifierCascade fistClassifier = loadHaarClassifier("fist.xml");
        CvHaarClassifierCascade palmClassifier = loadHaarClassifier("palm.xml");

        // Grab the default video device. This work for both built-win 
        // and usb webcams.
        CvCapture capture = opencv_highgui.cvCreateCameraCapture(0);

        // Set the capture resolution 480x320 gives decent quality 
        // and the lower resolution will make our real-time video 
        // processing a little faster.    
        opencv_highgui.cvSetCaptureProperty(capture,
                opencv_highgui.CV_CAP_PROP_FRAME_HEIGHT, 520);
        opencv_highgui.cvSetCaptureProperty(capture,
                opencv_highgui.CV_CAP_PROP_FRAME_WIDTH, 520);

        // Contruct a JavaCV Image that matches the properties of the 
        // captured imaged.    
        IplImage grabbedImage = opencv_highgui.cvQueryFrame(capture);
        IplImage mirrorImage = grabbedImage.clone();
        IplImage grayImage = IplImage.create(mirrorImage.width(),
                mirrorImage.height(), IPL_DEPTH_8U, 1);

        // OpenCV's C++ roots means we need to allocate memory
        // to use as working storage for object detection.
//        CvMemStorage faceStorage = CvMemStorage.create();
//        CvMemStorage handStorage = CvMemStorage.create();
        CvMemStorage fistStorage = CvMemStorage.create();
        CvMemStorage palmStorage = CvMemStorage.create();

        //Create a frame to echo to.
        CanvasFrame frame = new CanvasFrame("Object Detection Demo", 1);

        // Keep looping while our frame is visible and we're getting 
        // images from the webcam
        while (frame.isVisible()
                && (grabbedImage = opencv_highgui.cvQueryFrame(capture))
                != null
                && currentSystemState != SystemState.SEQUENCE_DETECTED) {

            // Clear out storage 
//            cvClearMemStorage(faceStorage);
            cvClearMemStorage(fistStorage);
//            cvClearMemStorage(handStorage);            
            cvClearMemStorage(palmStorage);

            // Flip the image because a mirror image looks more natural ?
            cvFlip(grabbedImage, mirrorImage, 1);
            // Create a black and white image - best for face detection
            // according to OpenCV sample.
            cvCvtColor(mirrorImage, grayImage, CV_BGR2GRAY);

            // Find faces in grayImage and mark with green 
            // rectangles on mirrorImage.
//            findAndMarkObjects(faceClassifier, faceStorage,CvScalar.GREEN, grayImage, mirrorImage);
            // Find hands in mirrorImage and mark with green
            // rectangles on mirrorImage
//              findAndMarkObjects(handClassifier, handStorage, CvScalar.BLUE, grayImage,mirrorImage,SystemState.INITIAL);
            //similarly for fist
            findAndMarkObjects(fistClassifier, fistStorage, CvScalar.RED, grayImage, mirrorImage, SystemState.FIST_DETECTED);
            findAndMarkObjects(palmClassifier, palmStorage, CvScalar.YELLOW, grayImage, mirrorImage, SystemState.PALM_DETECTED);

            
//             findFingerTips(bigContour, IMG_SCALE);
              
            // display mirrorImage on frame
            frame.showImage(mirrorImage);
        }



        // display captured image on frame
        frame.dispose();
        opencv_highgui.cvReleaseCapture(capture);
    }

    /**
     * Find objects matching the supplied Haar classifier.
     *
     * @param classifier The Haar classifier for the object we're looking for.
     * @param storage In-memory storage to use for computations
     * @param colour Colour of the marker used to make objects found.
     * @param inImage Input image that we're searching.
     * @param outImage Output image that we're going to mark and display.
     */
    private static void findAndMarkObjects(
            CvHaarClassifierCascade classifier,
            CvMemStorage storage,
            CvScalar colour,
            IplImage inImage,
            IplImage outImage,
            SystemState objectState) {


//        if (currentSystemState == SystemState.SEQUENCE_DETECTED) {
//            return;
//        }

        CvSeq detectedObject = cvHaarDetectObjects(inImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
        int totalObjects = detectedObject.total();
        if (totalObjects > 0) {

            for (int i = 0; i < totalObjects; i++) {
                CvRect r = new CvRect(cvGetSeqElem(detectedObject, i));
                int x = r.x(), y = r.y(), w = r.width(), h = r.height();
                cvRectangle(outImage, cvPoint(x, y), cvPoint(x + w, y + h), colour, 1, CV_AA, 0);
            }

            if (objectState == currentSystemState) {
                return;
            } else if ((objectState == SystemState.PALM_DETECTED) && (currentSystemState == SystemState.INITIAL)) {
                System.out.println("paaalm!!!");
                previousSystemState = currentSystemState;
                currentSystemState = SystemState.PALM_DETECTED;
                sequenceOfGestures.add('P');
//                }
            } else if ((objectState == SystemState.FIST_DETECTED) && (previousSystemState == SystemState.INITIAL)) {
                sequenceOfGestures.add('F');
                System.out.println("fist...");
                previousSystemState = currentSystemState;
                currentSystemState = SystemState.FIST_DETECTED;
            } else if (previousSystemState == SystemState.PALM_DETECTED && currentSystemState == SystemState.FIST_DETECTED && objectState == SystemState.PALM_DETECTED) {
                sequenceOfGestures.add('P');
                System.out.println("sequence detected!!!");
                currentSystemState = SystemState.SEQUENCE_DETECTED;
            } else {
                System.out.println("###pre: " + previousSystemState + " cur: " + currentSystemState + " st:" + objectState);
                System.out.println("Reset!!!");
                previousSystemState = currentSystemState = SystemState.INITIAL;
            }

            if (previousSystemState != objectState) {
                System.out.println("there is a change in gesture");
                System.out.println("pre: " + previousSystemState + " cur: " + currentSystemState);
            }

        }
    }

    /**
     * Load a Haar classifier from its xml representation.
     *
     * @param classifierName Filename for the haar classifier xml.
     * @return a Haar classifier object.
     */
    private static CvHaarClassifierCascade loadHaarClassifier(
            String classifierName) {

        CvHaarClassifierCascade classifier = new CvHaarClassifierCascade(cvLoad(classifierName));
        if (classifier.isNull()) {
            System.err.println("Error loading classifier file \"" + classifier + "\".");
            System.exit(1);
        }

        return classifier;
    }
    private static final int MAX_POINTS = 20;
    private CvMemStorage contourStorage, approxStorage, hullStorage, defectsStorage;
    private Point[] tipPts, foldPts;
    private float[] depths;

    private void findFingerTips(CvSeq bigContour, int scale) {
        CvSeq approxContour = cvApproxPoly(bigContour,
                Loader.sizeof(CvContour.class),
                approxStorage, CV_POLY_APPROX_DP, 3, 1);
// reduce number of points in the contour

        CvSeq hullSeq = cvConvexHull2(approxContour,
                hullStorage, CV_COUNTER_CLOCKWISE, 0);
// find the convex hull around the contour

        CvSeq defects = cvConvexityDefects(approxContour,
                hullSeq, defectsStorage);
// find the defect differences between the contour and hull

        int defectsTotal = defects.total();
        if (defectsTotal > MAX_POINTS) {
            System.out.println("Processing " + MAX_POINTS + " defect pts");
            defectsTotal = MAX_POINTS;
        }

        // copy defect information from defects sequence into arrays
        for (int i = 0; i < defectsTotal; i++) {
            Pointer pntr = cvGetSeqElem(defects, i);
            CvConvexityDefect cdf = new CvConvexityDefect(pntr);

            CvPoint startPt = cdf.start();
            tipPts[i] = new Point((int) Math.round(startPt.x() * scale),
                    (int) Math.round(startPt.y() * scale));
            // array contains coords of the fingertips

            CvPoint endPt = cdf.end();
            CvPoint depthPt = cdf.depth_point();
            foldPts[i] = new Point((int) Math.round(depthPt.x() * scale),
                    (int) Math.round(depthPt.y() * scale));
            //array contains coords of the skin fold between fingers

            depths[i] = cdf.depth() * scale;
            // array contains distances from tips to folds
        }

//        reduceTips(defectsTotal, tipPts, foldPts, depths);
    }
}
