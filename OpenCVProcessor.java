package com.main;

import java.awt.image.BufferedImage;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class OpenCVProcessor {
	public static final int processMode_NONE = 0;
	public static final int processMode_CANNY= 1;
	public static final int processMode_BnW  = 2;
	
	private int processMode = processMode_NONE;
	
	/* Canny variables */
	private int CannyLoLimit = 20;
	private int CannyHiLimit = 255;
	private boolean CannyInverted = true;
	
	
	public OpenCVProcessor() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		System.out.println("Open CV Processor Loaded");
	}
	
	private Mat BlackAndWhite(Mat obj, int channels) {
		Mat Bnw3C = new Mat(obj.rows(), obj.cols(), CvType.CV_8UC3);
		Mat BnW1C = new Mat(obj.rows(), obj.cols(), CvType.CV_8UC1);
		
		Imgproc.cvtColor(obj, BnW1C, Imgproc.COLOR_BGR2GRAY);
		if (channels == 3) {
			Imgproc.cvtColor(BnW1C,Bnw3C,Imgproc.COLOR_GRAY2BGR);
			return Bnw3C;
		}
		return BnW1C;
	}
	
	private Mat Canny(Mat obj) {
		Mat BnW1C = new Mat(obj.rows(), obj.cols(), CvType.CV_8UC1);
		
		//Convert Image to B/W first.
		BnW1C = BlackAndWhite(obj, 1);
		
		//Then Apply Canny.
		Imgproc.Canny(BnW1C, BnW1C, CannyLoLimit, CannyHiLimit);
		
		//Just convert the 1C B/W to 3 channel B/W
		Mat Canny3C = new Mat(BnW1C.rows(), BnW1C.cols(), CvType.CV_8UC3);		
		Imgproc.cvtColor(BnW1C,Canny3C,Imgproc.COLOR_GRAY2BGR);
		
		//Then invert the image. Black becomes white, white becomes white. 
		if (CannyInverted) {
			Imgproc.threshold(Canny3C, Canny3C, 0, 255, Imgproc.THRESH_BINARY_INV);
		}
		return Canny3C;
	}
	
	public void SetCannyLoLimit(int limit) {
		CannyLoLimit = limit;	
	}
	public void SetCannyHiLimit(int limit) {
		CannyHiLimit = limit;	
	}
	public void SetInverted(boolean status) {
		CannyInverted = status;	
	}
	
	public Mat Process(Mat obj) {
		Mat proccesedObj = obj;
		
		if (processMode == processMode_NONE) {
			//System.out.println("ProcessMode: NONE");
		} else if (processMode == processMode_CANNY) {
			//System.out.println("ProcessMode: CANNY");
			proccesedObj = Canny(obj);
		} else if (processMode == processMode_BnW) {
			proccesedObj = BlackAndWhite(obj, 3);
		}
		return proccesedObj;
	}
	
	public void setProcessMode(int mode) {
		processMode = mode;
	}
}
