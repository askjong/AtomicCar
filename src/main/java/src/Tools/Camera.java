package src.Tools;

import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.CvSize;
import org.bytedeco.opencv.opencv_core.IplImage;
import src.Servers.UDPServerCam;

import java.awt.image.BufferedImage;

import static org.bytedeco.opencv.global.opencv_core.*;


/**
 * @author Andreas S.S
 * @version X.X
 * @since 29.10.2019, 14:48
 */
public class Camera extends Thread {
    final private static int WEBCAM_DEVICE_INDEX = 0;
    private OpenCVFrameGrabber grabber;
    private Java2DFrameConverter bufferedImageConverter;
    private OpenCVFrameConverter.ToIplImage converter;
    private IplImage srcIm;
    private Thread thread;
    private Frame frame;
    private boolean initialized;
    private BufferedImage buffIm;
    private UDPServerCam server;

    /**
     * Create a class to capture images from connected Camera.
     * @param server, server to send the image to.
     */
    public Camera(UDPServerCam server){
        this.server = server;
        this.grabber = new OpenCVFrameGrabber(WEBCAM_DEVICE_INDEX);
        this.converter = new OpenCVFrameConverter.ToIplImage();
        this.frame = new Frame();
        this.thread = new Thread(this);
        this.initialized = false;
        this.bufferedImageConverter = new Java2DFrameConverter();
        this.srcIm = cvCreateImage(new CvSize(640,480),8,3);
    }

    /**
     * Starts capturing images.
     */
    public void startCamera() {
        this.thread.start();
    }

    /**
     * Capture images and sends them to the server.
     */
    @Override
    public void run() {
        try {
            grabber.start();
            initialized = true;
            this.frame = grabber.grab();
            this.srcIm = this.converter.convert(frame);
            BufferedImage data;

            while (true) {
                grabber.grab();
                data = this.bufferedImageConverter.convert(frame);
                if(data != null) {
                    //setSrcIm(data);
                    server.outToClient(data);
                    cvReleaseImage(srcIm);
                }else {
                    System.out.println(this + ":: image is null");
                }


            }
        } catch (FrameGrabber.Exception e) {
            System.out.println(e.getMessage());
    }
    }


    /**
     * Gets the source image pointer for distribution.
     *
     * @return IplImage containing pointer to video stream data
     */
    public synchronized BufferedImage getSrcIm() {
        if (this.initialized) {
            return this.buffIm;
        } else {
            return null;
        }
    }

    /**
     * sets the source image pointer.
     *
     * @param bufIm containing pointer to video stream data.
     */
    public synchronized void setSrcIm(BufferedImage bufIm) {
        this.buffIm = bufIm;

    }
}