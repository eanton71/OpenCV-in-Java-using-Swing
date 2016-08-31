package com.main;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class OpenCVProcessor {
	public static final int processMode_NONE = 0;
	public static final int processMode_CANNY= 1;
	
	private int processMode = processMode_NONE;
	
	
	public OpenCVProcessor() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
		System.out.println("Open CV Processor Loaded");
	}
	
	public Mat Process(Mat obj) {
		Mat proccesedObj = obj;
		if (processMode == processMode_NONE) {
			System.out.println("ProcessMode: NONE");
		} else if (processMode == processMode_CANNY) {
			System.out.println("ProcessMode: CANNY");	
		}
		return proccesedObj;
	}
	
	public void setProcessMode(int mode) {
		processMode = mode;
	}
}
