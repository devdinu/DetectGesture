/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package objdetect;

/**
 *
 * @author Google
 */
public class SystemTest {
    public static void main(String[] args) {
        SequenceDetector sd = new SequenceDetector();
        sd.detect();
        System.out.println(SequenceDetector.sequenceOfGestures);
    }
}