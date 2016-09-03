//To create a stand-alone application, export->runnable jar->packaged required library..
//then call by java -Djava.library.path="C:<opencv_path>\opencv\2.4.11\build\java\x86" -jar gui1.jar .


package com.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;
import javax.swing.JButton;
import java.awt.Canvas;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Font;
import javax.swing.JPanel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class MainOpenCV {

	private JFrame frame;
	private JButton btnCamera;
	
	private	OpenCVProcessor objOpenCV;
	private CameraGrabber objGrabber;
	private MyCanvas objCanvas;
	private boolean camIsRunning;
	private Thread camThread = null;
	private JSlider sliderCannyMin;
	private JSlider sliderCannyMax;
	private JCheckBox chckbxCannyInvert;
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
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent arg0) {
				if (camIsRunning) {
					camIsRunning = false;
					camThread.stop();
					camThread = null;
				}
				objGrabber.Close();
			}
		});
		frame.setBounds(100, 100, 757, 361);
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
		btnCanny.setBounds(408, 11, 90, 75);
		frame.getContentPane().add(btnCanny);
		
		sliderCannyMin = new JSlider();
		sliderCannyMin.setValue(20);
		sliderCannyMin.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				objOpenCV.SetCannyLoLimit(sliderCannyMin.getValue());
			}
		});
		sliderCannyMin.setMaximum(255);
		sliderCannyMin.setBounds(599, 16, 142, 23);
		frame.getContentPane().add(sliderCannyMin);
		
		JLabel lblMinThreshold = new JLabel("Min Threshold");
		lblMinThreshold.setBounds(508, 20, 81, 14);
		frame.getContentPane().add(lblMinThreshold);
		
		sliderCannyMax = new JSlider();
		sliderCannyMax.setMinorTickSpacing(1);
		sliderCannyMax.setValue(255);
		sliderCannyMax.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				objOpenCV.SetCannyHiLimit(sliderCannyMax.getValue());
			}
		});
		sliderCannyMax.setMaximum(255);
		sliderCannyMax.setBounds(599, 38, 142, 23);
		frame.getContentPane().add(sliderCannyMax);
		
		JLabel lblMaxThreshold = new JLabel("Max Threshold");
		lblMaxThreshold.setBounds(508, 45, 90, 14);
		frame.getContentPane().add(lblMaxThreshold);
		
		JLabel lblInvert = new JLabel("Invert");
		lblInvert.setBounds(547, 68, 38, 14);
		frame.getContentPane().add(lblInvert);
		
		chckbxCannyInvert = new JCheckBox("");
		chckbxCannyInvert.setSelected(true);
		chckbxCannyInvert.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				objOpenCV.SetInverted(chckbxCannyInvert.isSelected());
			}
		});
		chckbxCannyInvert.setBounds(597, 63, 97, 23);
		frame.getContentPane().add(chckbxCannyInvert);
		
		JButton btnBlackandwhite = new JButton("B/W");
		btnBlackandwhite.addActionListener(new ButtonListeners(ButtonListeners.BnW));
		btnBlackandwhite.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnBlackandwhite.setBounds(408, 89, 90, 28);
		frame.getContentPane().add(btnBlackandwhite);
		
		JButton btnNoProcessing = new JButton("None");
		btnNoProcessing.addActionListener(new ButtonListeners(ButtonListeners.None));
		btnNoProcessing.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnNoProcessing.setBounds(408, 120, 90, 28);
		frame.getContentPane().add(btnNoProcessing);
		
		//JPanel panel = new JPanel();
		//panel.setBounds(410, 11, 200, 50);
		//frame.getContentPane().add(panel);
		
		
		camIsRunning = false;
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
	         //g2.drawString ("This is is a string", 0 + 10, getHeight() - 10);
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
			private static final int None		       = 0;
			private static final int CameraButtonEvent = 1;
			private static final int CannyButtonEvent = 2;
			private static final int BnW		      = 3;
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
									Thread.sleep(objGrabber.CameraGetFps());
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
				} else if (ButtonEvent == BnW) {
					objOpenCV.setProcessMode(OpenCVProcessor.processMode_BnW);
				} else if (ButtonEvent == None) {
					objOpenCV.setProcessMode(OpenCVProcessor.processMode_NONE);
				}
			}
		}
}
