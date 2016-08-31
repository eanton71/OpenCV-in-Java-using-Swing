package com.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JFrame;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;

import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.Canvas;
import java.io.File;

import javax.imageio.ImageIO;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferByte;


public class MainOpenCV {

	private JFrame frame;
	private JButton btnCamera;
	
	private	OpenCVProcessor objOpenCV;
	private CameraGrabber objGrabber;
	private MyCanvas objCanvas;
	private boolean camIsRunning;
	private Thread camThread = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainOpenCV window = new MainOpenCV();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainOpenCV() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		objOpenCV =  new OpenCVProcessor();
		objGrabber = new CameraGrabber(0, 60);
		
		frame = new JFrame();
		frame.setBounds(100, 100, 546, 365);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		btnCamera = new JButton("Start Camera");
		btnCamera.addActionListener(new ButtonListeners(ButtonListeners.CameraButtonEvent));
		btnCamera.setBounds(141, 295, 127, 23);
		frame.getContentPane().add(btnCamera);
		
		objCanvas = new MyCanvas();
		frame.getContentPane().add(objCanvas);
		
		JButton btnCanny = new JButton("Canny");
		btnCanny.addActionListener(new ButtonListeners(ButtonListeners.CannyButtonEvent));
		btnCanny.setBounds(408, 11, 89, 23);
		frame.getContentPane().add(btnCanny);
		
		
		camIsRunning = false;
	}

	
	private class CameraGrabber {
		private	VideoCapture Camera;
		private int fps;

		
		public CameraGrabber() { }
		public CameraGrabber(int whichCamera, int fps) {
			Camera = new VideoCapture(0);
			Camera.open(whichCamera);
			this.fps = 1000/fps;
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				
			}
		}
		public boolean isOpen() {
			return (Camera.isOpened());
		}
		public Mat Capture() {
			Mat frame = new Mat();
			Camera.read(frame);
			return frame;
		}
		public void Close() {
			Camera.release();
		}
		
	}
	
	private class MyCanvas extends Canvas {
		 private BufferedImage img = null;
		 private Graphics g = null;

	      public MyCanvas () {
	         setBackground (Color.GRAY);
	         setBounds(10, 10, 387, 279);
	      }

	      public void paint (Graphics g) {
	         Graphics2D g2;
	         g2 = (Graphics2D) g;

	         if (img != null) {
	        	 g2.drawImage( img, 0, 0, getWidth(), getHeight(), null); 
	         }
	         //g2.drawString ("It is a custom canvas area", 70, 70);
	      }
	      public void repaint() {
	    	  	super.repaint();	  
	      }
	      
	      public void setImage(BufferedImage img) {
	    	  this.img = img;
	      }
	      
	      public Graphics getG() {
	    	  return this.g ;
	      }
	   }
	
		private class ButtonListeners implements ActionListener {
			private static final int CameraButtonEvent = 1;
			private static final int CannyButtonEvent = 2;
			private int ButtonEvent;
						
			public ButtonListeners() { }
			public ButtonListeners(int event) { ButtonEvent = event; }
			
			private void cameraButtonProc() {
				if (camThread == null) {
					camThread = new Thread() {
						public void run() {
							Mat matImg;
							while (true) {
								matImg = objGrabber.Capture();
								matImg = objOpenCV.Process(matImg);
							
								BufferedImage bufImg = new BufferedImage(matImg.cols(), matImg.rows(), BufferedImage.TYPE_3BYTE_BGR);
								matImg.get(0, 0, ((DataBufferByte)bufImg.getRaster().getDataBuffer()).getData());
								objCanvas.setImage(bufImg);
								objCanvas.repaint();
								
								try {
									Thread.sleep(objGrabber.fps);
								} catch (Exception e) {
									
								}
							}
						};
					};
				}
				if (camIsRunning == false) {
					camIsRunning = true;
					camThread.start();
					btnCamera.setText("Stop Camera");
				} else {
					camThread.stop();
					btnCamera.setText("Start Camera");
					camIsRunning = false;
					camThread = null;
				}					
			}
			
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (ButtonEvent == CameraButtonEvent) {
					cameraButtonProc();
				} else if ( ButtonEvent == CannyButtonEvent) {
					objOpenCV.setProcessMode(OpenCVProcessor.processMode_CANNY);
				}
			}
		}
}
