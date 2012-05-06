/*****************************************************************************

    Physics Demos - Ising Model Simulation

    Copyright 2004, 2012 Jeffrey L. Wasserman
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

*****************************************************************************/

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.font.*;
import java.lang.Math.*;
import java.util.Random;
import java.util.Vector;

public class ising extends JApplet {

    public isingFrame fr;
	
    public void init() {
	setName ("Ising-JApplet");
	fr = new isingFrame();
	//fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void start() {
	fr.isingInit();
    }

    public void stop() {
    }

    public void destroy() { 
    }
}

class isingFrame extends JFrame {

    isingPanel magDraw;
    JPanel panCont;
    Container buttonCont, drawCont, frameCont, heatCont, boundCont,
	flowButCont, stepCont, cellCont, plotCont;
    JButton quitButton;
    JButton clearButton;
    JToggleButton pauseButton;
    JToggleButton holdButton, heatButton, coolButton;
    JToggleButton periodicButton, antiPeriodicButton, 
	upBoundaryButton, downBoundaryButton;
    JToggleButton dataButton, b;
    JButton exportButton;
    JComboBox modeCombo, cellCombo, cb, xDataBox, yDataBox;
    JSlider speedSlider;
    JSpinner deltaTSpinner, averagesSpinner;
    SpinnerNumberModel deltaTSpinnerModel, averagesSpinnerModel;
    JTextArea messageBox;
    JScrollPane scroller;
    JLabel tempLab;
    Color defaultColor, hiColor;

    public isingFrame (){

	super("2-D Ising Model Simulation Applet");
	magDraw = new isingPanel();
	panCont = new JPanel();

	JPanel p, b; // dummy panel

	periodicButton = new JToggleButton ("Periodic");
	antiPeriodicButton = new JToggleButton ("Anti-Periodic");
	upBoundaryButton = new JToggleButton ("Up");
	downBoundaryButton = new JToggleButton ("Down");
	boundCont = new Container();
	boundCont.setLayout (new GridLayout(5,1));
	boundCont.add (new JLabel("Boundary Conditions"));
	boundCont.add (periodicButton);
	boundCont.add (antiPeriodicButton);
	boundCont.add (upBoundaryButton);
	boundCont.add (downBoundaryButton);

	pauseButton = new JToggleButton ("Pause");
	exportButton = new JButton ("Export");
	quitButton = new JButton ("Quit");
	flowButCont = new Container();
	flowButCont.setLayout (new GridLayout (1,3));
	flowButCont.add (pauseButton);
	flowButCont.add (quitButton);
	flowButCont.add (exportButton);

	String [] modeList = {"Visual","Intensive"};
	modeCombo = new JComboBox (modeList);
	modeCombo.setEditable(false);
	String [] cellSizeList = {"1","2","4","8","16"};
	int[] cellSizeIntList = {1,2,4,8,16};
	cellCombo = new JComboBox(cellSizeList);
	cellCombo.setEditable(false);
	cellCont = new Container();
	cellCont.setLayout (new GridLayout (4,1));
	cellCont.add (new JLabel("Simulation Mode"));
	cellCont.add (modeCombo);
	cellCont.add (new JLabel("Cell Size"));
	cellCont.add (cellCombo);

	xDataBox = new JComboBox(new String[]{"T","E","M"});
	xDataBox.setEditable(false);
	yDataBox = new JComboBox(new String[]{"E","C","M","Chi"});
	yDataBox.setEditable(false);
	dataButton = new JToggleButton("Show Data Table");
	plotCont = new Container();
	plotCont.setLayout (new GridLayout(5,1));
	plotCont.add (new JLabel("X Axis"));
	plotCont.add (xDataBox);
	plotCont.add (new JLabel("Y Axis"));
	plotCont.add (yDataBox);
	plotCont.add (dataButton);

	holdButton = new JToggleButton ("Hold");
	heatButton = new JToggleButton ("Heat");
	coolButton = new JToggleButton ("Cool");
	clearButton = new JButton("Clear Data");
	clearButton.setBackground (new Color(160,0,0));
	clearButton.setForeground (Color.white);
	heatCont = new Container();
	heatCont.setLayout (new GridLayout(5,1));
	heatCont.add (new JLabel("Heater Controls"));
	heatCont.add (holdButton);
	heatCont.add (heatButton);
	heatCont.add (coolButton);
	heatCont.add (clearButton);

	speedSlider = new JSlider (JSlider.HORIZONTAL, 0, 50, 40);
	speedSlider.setMajorTickSpacing(10);
	speedSlider.setMinorTickSpacing(5);
	speedSlider.setPaintTicks(true);
	speedSlider.setPaintLabels(true);

	deltaTSpinnerModel = new SpinnerNumberModel (0.1, 0.001, 1, 0.001);
	deltaTSpinner = new JSpinner(deltaTSpinnerModel);
	averagesSpinnerModel = new SpinnerNumberModel (10, 5, 1000000, 5);
	averagesSpinner = new JSpinner(averagesSpinnerModel);
	stepCont = new Container();
	stepCont.setLayout (new GridLayout(5,1));
	//stepCont.add (new JLabel("Stepper Controls"));
	stepCont.add (new JLabel("delta T"));
	stepCont.add (deltaTSpinner);
	stepCont.add (new JLabel("Averages per Temp"));
	stepCont.add (averagesSpinner);

	messageBox = new JTextArea();
	messageBox.setFont (new Font("Monospaced",Font.PLAIN, 12));
	messageBox.setColumns(50);
	//messageBox.setBorder(BorderFactory.createLineBorder(Color.black));
	scroller = new JScrollPane (messageBox);
	
	buttonCont = new Container();
	buttonCont.setLayout (new FlowLayout());

	Box programControlCont = Box.createVerticalBox();
	programControlCont.add (flowButCont);
	p = new JPanel();
	p.add (new JLabel("Speed (Attempted Frames Per Second)"));
	programControlCont.add (p);
	programControlCont.add (speedSlider);

	Box MECellBox = Box.createHorizontalBox();
	p = new JPanel();
	p.add(cellCont);
	MECellBox.add (p);
	p = new JPanel();
	p.add(stepCont);
	MECellBox.add(p);
	p = new JPanel();
	p.add(plotCont);
	MECellBox.add(p);
	p = new JPanel();
	p.add(heatCont);
	MECellBox.add(p);
	p = new JPanel();
	p.add(boundCont);	
	MECellBox.add (p);

	/*
	Box infoBox = Box.createVerticalBox();
	infoBox.add (messageBox);
	//infoBox.add (projectBox);
	*/

	buttonCont.add(programControlCont);
	buttonCont.add(MECellBox);

	frameCont = getContentPane();
	frameCont.setLayout(new BorderLayout());
	frameCont.add(buttonCont, BorderLayout.NORTH);
	frameCont.add(magDraw, BorderLayout.CENTER);
	frameCont.add(scroller, BorderLayout.EAST);

	pack();
	setVisible(true);

	deltaTSpinnerModel.addChangeListener (new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    Double obb = (Double)deltaTSpinnerModel.getValue();
		    magDraw.setDeltaT ((float)obb.doubleValue());
		}
	    });
	averagesSpinnerModel.addChangeListener (new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    Integer obi = (Integer) averagesSpinnerModel.getValue();
		    magDraw.setAverages (obi.intValue());
		}
	    });
	modeCombo.addActionListener (new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    cb = (JComboBox)e.getSource();
		    if (cb.getSelectedIndex()==1) {
			System.out.println("intensive");
			magDraw.numericalMode=true;
			periodicButton.doClick();
			periodicButton.setEnabled(false);
			antiPeriodicButton.setEnabled(false);
			upBoundaryButton.setEnabled(false);
			downBoundaryButton.setEnabled(false);
			speedSlider.setEnabled(false);
			speedSlider.setPaintTicks(false);
			speedSlider.setPaintLabels(false);
			//deltaTSpinner.setEnabled(true);
			averagesSpinner.setEnabled(true);
			magDraw.clearVectors();
			magDraw.setDeltaT(0.1f);
			deltaTSpinner.setValue(new Double(0.1));
		    }
		    else {
			System.out.println("visual");
			magDraw.numericalMode=false;
			periodicButton.setEnabled(true);
			antiPeriodicButton.setEnabled(true);
			upBoundaryButton.setEnabled(true);
			downBoundaryButton.setEnabled(true);
			speedSlider.setEnabled(true);
			speedSlider.setPaintTicks(true);
			speedSlider.setPaintLabels(true);
			periodicButton.doClick();
			//deltaTSpinner.setEnabled(false);
			averagesSpinner.setEnabled(false);
			magDraw.setDeltaT(0.005f);
			deltaTSpinner.setValue(new Double(0.005f));
		    }
		}
	    });		
	cellCombo.addActionListener( new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    cb = (JComboBox)e.getSource();
		    String ob = (String)cb.getSelectedItem();
		    magDraw.setCellSize(Integer.parseInt(ob));
		    magDraw.initialize();
		}
	    });
	xDataBox.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    cb = (JComboBox)e.getSource();
		    magDraw.xDataSource = cb.getSelectedIndex();
		}
	    });
	yDataBox.addActionListener(new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    cb = (JComboBox)e.getSource();
		    magDraw.yDataSource = cb.getSelectedIndex();
		}
	    });
	clearButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    magDraw.clearVectors();
		}
	    });
	dataButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    if (magDraw.showData) {
			magDraw.showData=false;
			b.setText("Show Data Table");
			b.setSelected(false);
		    }
		    else {
			magDraw.showData=true;
			b.setSelected(true);
			b.setText("Show Summary");
		    }
		}
	    });
	pauseButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    magDraw.paused = b.isSelected();
		}
	    });

	quitButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    magDraw.quit = true;
		}
	    });

	speedSlider.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    int s=speedSlider.getValue();
		    if (s==0) s=1;
		    magDraw.speed=s;
		}
	    });

	holdButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    if (magDraw.heaterHold) {
			magDraw.heaterHold=false;
			magDraw.pointSkip=true;
			b.setSelected(false);
		    }
		    else {
			magDraw.heaterHold=true;
			b.setSelected(true);
		    }
		}
	    });
	heatButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    coolButton.setSelected(false);
		    magDraw.heaterValue=1;
		}
	    });
	coolButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    heatButton.setSelected(false);
		    magDraw.heaterValue=2;
		}
	    });
	periodicButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    antiPeriodicButton.setSelected(false);
		    upBoundaryButton.setSelected(false);
		    downBoundaryButton.setSelected(false);
		    magDraw.boundValue=0;
		}
	    });	
	antiPeriodicButton.addActionListener (new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    periodicButton.setSelected(false);
		    upBoundaryButton.setSelected(false);
		    downBoundaryButton.setSelected(false);
		    magDraw.boundValue=1;
		}
		});
	upBoundaryButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    periodicButton.setSelected(false);
		    antiPeriodicButton.setSelected(false);
		    downBoundaryButton.setSelected(false);
		    magDraw.boundValue=2;
		}
	    });	
	downBoundaryButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    periodicButton.setSelected(false);
		    antiPeriodicButton.setSelected(false);
		    upBoundaryButton.setSelected(false);
		    magDraw.boundValue=3;
		}
	    });

    }

    public void isingInit () {
	new Thread (magDraw).start();
    }
}

class isingPanel extends JPanel 
    implements Runnable, MouseInputListener {

    public int panX, panY; // Size of the isingPanel total drawing area
    public int xsize, ysize; // Size of only the magnet system drawing area
    public int magX, magY; // Size of the magnet itself
    public int cellSize=1; // Size of a single spin in pixels
    public int heaterValue=2; // Start with the system being chilled
    public boolean heaterHold=false;
    public boolean pointSkip=false; // Store datapoint, go to next value
    public int boundValue=0; //Start with periodic boundary conditions
    //public int demon, mDemon;
    public int energyCount=0, mCount=0, volumeCount=0;
    public boolean paused=false,quit=false;
    public int speed;
    public byte[][] data; // The system itself in 2-D format
    public byte[] imageData; // Drawn magnet image, in 1-D format
    private float[] energyTable;
    public DataBuffer db;
    public WritableRaster wr;
    public ColorModel cm;
    public float t, tc, deltaT=0.005f; // Temperature, critical temperature
    public Random random;
    public BufferedImage image;
    public boolean numericalMode=false;
    private long iteration;
    private isingFrame parent;
    private float eCalc,mCalc;
    private float eAvg,e2Avg,mAvg,m2Avg;
    private int bw=0, bh=0;
    private int bulbWidth=14, neckWidth=5, bord=15; // Thermometer
    private int thermShift,tempZeroY,tempMaxY,tempHeight, tempLeftX;
    private int plotZeroX, plotMaxX, plotZeroY, plotMaxY;
    private int plotLabelSize;
    private int plotPointRad=2;
    private float [] plotXValues, plotYValues;
    private float maxTemp=5f, tempTick=1f;
    private int tickLen=3;
    private String maxTempString="5";
    private boolean hiTemp=false, moveTemp=false;
    public int numberOfAverages=10, numberOfIndependents;
    private int averageStep, independentStep;
    private Vector tempVector, magnetVector, energyVector;
    private Vector specHeatVector, susceptVector;
    private Vector xVector, yVector;
    public int xDataSource, yDataSource;
    public boolean showData=false;
    private String xDataString, yDataString;
    private int vectorSize=0;
    boolean invalidData=true;
    public boolean firstTime=true;

    Color backCol = new Color (0,0,0);
    Color spinCol = new Color (0xDD,0xDD,0);
    Color mercuryCol = new Color (0xCC, 0, 0);
    Color mercuryHiCol = new Color (0xFF, 0, 0);
    Color emptyCol = new Color (0xFF, 0xFF, 0xFF);
    Color labelCol = new Color (0xFF, 0xFF, 0xFF);
    Color criticalCol = new Color (0, 0xFF, 0);
    Color zeroCol = new Color (0x7F, 0x7F, 0x7F);
    Color plotPointCol = new Color (0xFF, 0, 0xFF);
    Color plotAxisCol = new Color (0xFF, 0xFF, 0xFF);
    Color fontCol = new Color (0xFF, 0xFF, 0xFF);

    public isingPanel () {
	addMouseListener(this);
	addMouseMotionListener(this);
	xsize=256;
        ysize=256;
	thermShift=bulbWidth+2*bord;
        setPreferredSize (new Dimension(2*(int)xsize+thermShift+bord,
					(int)ysize));
	t=3;
	tc=(float)(2/Math.log(1+Math.sqrt(2)));
	// calculate energy hash table to save computing cycles
	energyTable = new float [2];
	energyTable[0]=(float)Math.exp(-8f/t);
	energyTable[1]=(float)Math.exp(-4f/t);
	tempVector = new Vector();
	energyVector = new Vector();
	magnetVector = new Vector();
	specHeatVector = new Vector();	
	susceptVector = new Vector();
	plotXValues = new float[2];
	plotYValues = new float[2];
    }

    public void mouseClicked(MouseEvent e) {
	if (hiTemp) {
	    t=scrnToTemp(e.getY());
	    invalidData=true;
	    clearPoints();
	}
	repaint();
    }

    public void mousePressed(MouseEvent e) {
	if (hiTemp) moveTemp=true;
    }

    public void mouseReleased(MouseEvent e) {
	moveTemp=false;
	repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (moveTemp) {
	    t=scrnToTemp(e.getY());
	    invalidData=true;
	    clearPoints();
	}
    }

    public void mouseMoved(MouseEvent e) {
	int delta=3;
	hiTemp = false;
	int x=e.getX();
	int y=e.getY();
	if ((x>(tempLeftX-delta)) &&
	    (x<(tempLeftX+neckWidth+delta)) &&
	    (y>(tempMaxY-delta)) &&
	    (y<(tempZeroY+delta+bulbWidth))) hiTemp=true;
	repaint();
    }

    synchronized public void setDeltaT (float in) {
	deltaT = in;
	if (deltaT < 0) deltaT=0;
	if (deltaT > 1) deltaT=1;
    }

    synchronized public void setAverages (int in) {
	numberOfAverages = in;
	if (numberOfAverages<1) numberOfAverages=1;
    }

    synchronized public void clearVectors () {
	tempVector.clear();
	energyVector.clear();
	magnetVector.clear();
	specHeatVector.clear();
	susceptVector.clear();
	vectorSize=0;
	clearPoints();
	repaint();
    }

    synchronized public void clearPoints () {
	eAvg=0;
	e2Avg=0;
	mAvg=0;
	m2Avg=0;
	independentStep=0;
	averageStep=0;
    }
	     
    synchronized public void imageDataCreate () {

	int i,j,k,l;
	//System.out.println("data size "+data.length+" by "+data[0].length);
	//System.out.println("imageData size "+imageData.length);
	//System.out.println("sizes "+xsize+" , "+ysize);
	try {
	    for (i=0;i<magX;i++) 
		for (j=0;j<magY;j++)
		    for (k=0;k<cellSize;k++)
			for (l=0;l<cellSize;l++)
			    //following line OCCASIONALLY has error
			    //"ArrayIndexOutOfBoundsException"
			    {
				//System.out.println(i+" - "+j+" - "+k+" - "+l);
				//System.out.println((j*cellSize+l)*ysize+i*cellSize+k);
				imageData[(j*cellSize+l)*(int)ysize+i*cellSize+k]=
				    data[i][j];
			    }
	}
	catch (Exception e) {
	    initialize();
	    return;
	}
    }

    synchronized public void setCellSize(int inCellSize) {
	cellSize=inCellSize;
    }       

    public void firstTimeRoutine() {
	firstTime=false;
	parent = (isingFrame) getTopLevelAncestor();
	parent.averagesSpinner.setEnabled(false);
	/*
	parent.periodicButton.setEnabled(true);
	parent.antiPeriodicButton.setEnabled(true);
	parent.upBoundaryButton.setEnabled(true);
	parent.downBoundaryButton.setEnabled(true);
	parent.speedSlider.setEnabled(true);
	*/
	speed = parent.speedSlider.getValue();
	parent.coolButton.doClick();
	parent.periodicButton.doClick();
	deltaT=0.005f;
	parent.deltaTSpinner.setValue(new Double((double)deltaT));
	t=3f;
	numericalMode=false;
    }
	
    synchronized public void initialize() {
	if (firstTime)
	    firstTimeRoutine();
	int i,j;
	iteration=0;

	panX = getSize().width;
	panY = getSize().height;
	xsize=(panX-thermShift-bord)/2;
	ysize=panY;
	magX=(int)xsize/cellSize;
	magY=(int)ysize/cellSize;
	// recompensate for even # of cells in image
	xsize=magX*cellSize;
	ysize=magX*cellSize;
	//System.out.println("pan : "+panX+" , "+panY);
	//System.out.println("size : "+xsize+" , "+ysize);
	//System.out.println("mag : "+magX+" , "+magY);

	// Account for demons hopping 29 rows
	// xtoys ensures magY is multiple of sizeof(long) here, not sure why
	if ((magX%29)==0) magX-=1;
	if ((magY%29)==0) magY-=1;
	data = new byte[magX][magY];
	imageData = new byte[xsize*ysize];
	//System.out.println("new data : "+data.length+" , "+data[0].length);
	//System.out.println("new image : "+imageData.length);

	// Note key points for screen painting
	tempZeroY = panY-bord-bulbWidth;
	tempMaxY = bord;
	tempHeight = tempZeroY-tempMaxY;
	tempLeftX = bord+Math.round((bulbWidth-neckWidth)/2);
	plotLabelSize = 15;
	plotZeroX = thermShift+xsize+plotLabelSize+bord;
	plotMaxX = panX-bord;
	plotZeroY = panY-plotLabelSize;
	plotMaxY = bord;
	
	db = new DataBufferByte (imageData, xsize*ysize);
	wr = Raster.createPackedRaster
	    (db,xsize,ysize,8,null);
	byte ze=(byte)0;
	byte ff=(byte)255;
	byte hf=(byte)127;
	cm = new IndexColorModel
	    (1,3,
	     new byte[] {ff,ze,ze},
	     new byte[] {ff,ze,ff},
	     new byte[] {ze,ff,ff});        
	
	image = new BufferedImage(cm, wr, false, null);
	random = new Random();
	independentStep=0;
	averageStep=0;
	getNumberOfIndependents();
	
	// start with a random state here
	for (i=0; i<magX; i++)
	    for (j=0; j<magY; j++) {
		data[i][j]=(byte)random.nextInt(2);
	    }
    }
    
     public void run() {
	 System.out.println("Running...");
	 int i=0,j;
	 String ps;
	 if (random == null) {
	     initialize();
	 }
	 while (!quit) {
	     if (!paused)
		 {
		     // calculate next frame of magnet data
		     update();

		     // build the image from the magnet data
		     imageDataCreate ();

		     // Calculate the parameters when it's independent
		     independentDataRoutine();
		 }
	     int sleepTime=1;
	     if (!numericalMode) sleepTime=1000/speed;
	     try {Thread.sleep(sleepTime);}
	     catch (InterruptedException e) {}
	 }
	 System.exit(0);
     }

     synchronized private void independentDataRoutine () {

	 //	 System.out.println(independentStep+" -- "+averageStep);
	 if (!numericalMode) repaint();

	 independentStep++;

	 if (independentStep >= numberOfIndependents) {

	     if (numericalMode) repaint();
	     calculateEM();
	     eAvg+=eCalc;
	     e2Avg+=(eCalc*eCalc);
	     mAvg+=mCalc;
	     m2Avg+=(mCalc*mCalc);

	     if ((numericalMode && ((averageStep%5)==4)) ||
		 ((!numericalMode) && ((iteration%20)==0)))
		 paramWrite();

	     averageStep++;
	     if (((!numericalMode) || 
		  (averageStep >= numberOfAverages) ||
		  (pointSkip)) &&
		 (!heaterHold)) {
		
		 pointSkip=false;
		 if (invalidData) {
		     invalidData=false;
		     clearPoints();
		 }
		 
		 eAvg/=averageStep;
		 e2Avg/=averageStep;
		 mAvg/=averageStep;
		 m2Avg/=averageStep;
		 
		 // Build the vectors
		 float v=magX*magY;
		 tempVector.add(new Float(t));
		 energyVector.add(new Float(eAvg/v));
		 magnetVector.add(new Float(mAvg/v));
		 specHeatVector.add(new Float((e2Avg-eAvg*eAvg)/(v*t*t)));
		 susceptVector.add(new Float((m2Avg-mAvg*mAvg)/(v*t)));
		 vectorSize++;
		 
		 // ReZero the average sums
		 eAvg=0f;
		 e2Avg=0f;
		 mAvg=0f;
		 m2Avg=0f;
		 
		 averageStep=0;
		 
		 // Change the temperature
		 if (heaterValue == 1) t+=deltaT;
		 if (heaterValue == 2) t-=deltaT;
		 if (t<deltaT) t=deltaT;
		 if (t>maxTemp) t=maxTemp;
		 getNumberOfIndependents();
	     }
	     
	     independentStep=0;
	 }
     }


     private int putInRange (int p,
			     int size) {

	 //while (p>=size) p-=size;
	 p%=size;
	 while (p<0) p+=size;
	 return p;
     }

     synchronized public void update() {

	 int i,j,x,y,k;
	 byte []neighbor;
	 int energy, eNew;
	 double energyTable[];

	 neighbor=new byte[4];

	 iteration++;
	 
	 // calculate energy hash table to save computing cycles
	 energyTable = new double [3];
	 energyTable[0]=Math.exp(-8/t);
	 energyTable[1]=Math.exp(-4/t);
	 energyTable[2]=0.5;

	 i=random.nextInt(magX);
	 j=random.nextInt(magY);
	 for (x=0;x<magX;x++) {
	     i+=29;
	     i%=magX;
	     for (y=0;y<magY;y++) {
		 j+=29;
		 j%=magY;

		 neighbor[0]=data[putInRange(i,magX)][putInRange(j+1,magY)];
		 neighbor[1]=data[putInRange(i,magX)][putInRange(j-1,magY)];
		 neighbor[2]=data[putInRange(i+1,magX)][putInRange(j,magY)];
		 neighbor[3]=data[putInRange(i-1,magX)][putInRange(j,magY)];

		 switch (boundValue) {
		 case 0: // Don't need to do anything
		     break;
		 case 1: // Flip neighbor if at the edges
		     if (j==(magY-1)) neighbor[0]=(byte)(1-neighbor[0]);
		     if (j==0) neighbor[1]=(byte)(1-neighbor[1]);
		     if (i==(magX-1)) neighbor[2]=(byte)(1-neighbor[2]);
		     if (i==0) neighbor[3]=(byte)(1-neighbor[3]);
		     break;
		 case 2: // Set neighbor to 0 if at edges
		     if (j==(magY-1)) neighbor[0]=0;
		     if (j==0) neighbor[1]=0;
		     if (i==(magX-1)) neighbor[2]=0;
		     if (i==0) neighbor[3]=0;
		     break;
		 case 3: // Set neighbor to 1 if at edges
		     if (j==(magY-1)) neighbor[0]=1;
		     if (j==0) neighbor[1]=1;
		     if (i==(magX-1)) neighbor[2]=1;
		     if (i==0) neighbor[3]=1;
		     break;
		 default: // This should never be called
		     break;
		 }

		 energy=0;
		 for (k=0;k<4;k++) {
		     if (data[i][j]==neighbor[k]) energy-=1;
		     else energy+=1;
		 }

		 // flip the spin if the flipped energy is lower
		 if (energy > 0) {
		     data[i][j]=(byte)(1-data[i][j]);
		 }
		 // otherwise apply Boltzman factor probability
		 else {
		     if (random.nextFloat() < energyTable[(energy+4)/2])
			 data[i][j]=(byte)(1-data[i][j]);
		 }
	     }
	 }
     }

     synchronized public void getNumberOfIndependents() {

	 // this should return how many steps to get an
	 // independent sample.  
	 // Should be high near tc, smaller far away
	 // For now just return 5 steps independent of t
	 if (numericalMode) {
	     numberOfIndependents=5;
	 }
	 else {
	     numberOfIndependents=1;
	 }
	 //System.out.println("t is "+t+" -- #="+numberOfIndependents);	 
     }

    private String floatPrint(float f, 
			      int d,
			      int l) {

	double t = Math.pow(10,d);
	String s1 = (new Float(Math.round(f*t)/t)).toString();
	while (s1.length()<l)
	    s1=" "+s1;
	return s1;
    }

    synchronized public void paramWrite() {

	 float n,n2,v,v2,e,e2,m,m2;
	 //System.out.println("3.0 is "+floatPrint(3.0f,1,5));
	 String ps = new String();
	 if (showData) {
	     ps+="      T        E        C        M       Chi\n";
	     for (int i=0;i<vectorSize;i++) {
		 ps+=floatPrint(((Float)tempVector.get(i))
				.floatValue(),3,9);
		 ps+=floatPrint(((Float)energyVector.get(i))
				.floatValue(),3,9);
		 ps+=floatPrint(((Float)specHeatVector.get(i))
				.floatValue(),3,9);
		 ps+=floatPrint(((Float)magnetVector.get(i))
				.floatValue(),3,9);
		 ps+=floatPrint(((Float)susceptVector.get(i))
				.floatValue(),3,9);
		 ps+="\n";
	     }  
	 }
	 else {
	     calculateEM();
	     ps+="Grid Size "+magX+" by "+magY+ "\n";
	     ps+="Temp : "+t+" -- Tc : "+tc+"\n";
	     n=(float)(averageStep+1);
	     n2=n*n;
	     e=eAvg/n;
	     e2=e2Avg/n;
	     m=mAvg/n;
	     m2=m2Avg/n;
	     v=(float)magX*magY;
	     v2=v*v;
	     if (numericalMode) {
		 ps+="Averaging : "+n;
		 if (heaterHold)
		     ps+=" and Holding\n";
		 else
		     ps+=" of "+numberOfAverages+"\n";
		 ps+="E : "+(e/v)+"\n";
		 ps+="M : "+(m/v)+"\n";
		 ps+="C : "+(e2-e*e)/(t*t*v)+"\n";
		 ps+="Chi : "+(m2-m*m)/(t*v)+"\n";
	     }
	     else {
		 ps+="E : "+(e/v)+"\n";
		 ps+="M : "+(m/v)+"\n";
	     }
	     ps+="\n\n";
	     ps+="Demonstration part of :\n";
	     ps+="Java Virtual Physics Laboratory\n";
	     ps+="Johns Hopkins University\n";
	     ps+="CER TF 2004-2005\n";
	     ps+="www.pha.jhu.edu/~javalab\n";
	 }
	 parent.messageBox.setText(ps);
     }

     synchronized public void calculateEM () {

	 int i,j;
	 long etot=0,mtot=0;
	 for (i=0;i<magX;i++)
	     for (j=0;j<magY;j++) {
		 if (data[i][j]==data[(i+1)%magX][j])
		     etot--;
		 else
		     etot++;
		 if (data[i][j]==data[i][(j+1)%magY]) 
		     etot--;
		 else
		     etot++;
		 mtot+=(2*data[i][j]-1);
	    }
	 eCalc=(float)etot;
	 mCalc=(float)mtot;
    }

    private float scrnToTemp(int y) {
	float d;
	d=maxTemp*(tempZeroY-y)/tempHeight;
	if (d>maxTemp) d=maxTemp;
	if (d<0) d=0;
	return d;
    }

    private int tempToScrn(float t) {
	return Math.round(tempZeroY-tempHeight*(t/maxTemp));
    }

    private float[] vectorMaxMin (Vector v) {
	int i;
	float t,max=0,min=0;
	float[] d;
	for (i=0;i<v.size();i++) {
	    t=((Float)v.get(i)).floatValue();
	    if (i==0) {
		max=t;
		min=t;
	    }
	    else {
		if (t>max) max=t;
		if (t<min) min=t;
	    }
	}
	d=new float[2];
	d[0]=min;
	d[1]=max;
	return d;
    }		

    public int xyToScrn (float val, 
			 float rangeMin, 
			 float rangeMax, 
			 int scrnMin, 
			 int scrnMax) {

	return Math.round(scrnMin+((val-rangeMin)/
				   (rangeMax-rangeMin))*(scrnMax-scrnMin));
    }

    public int xToScrn (float val) {
	return Math.round(plotZeroX+
			  ((val-plotXValues[0])/
			   (plotXValues[1]-plotXValues[0]))
			  *(plotMaxX-plotZeroX));
    }

    public int yToScrn (float val) {
	return Math.round(plotZeroY+
			  ((val-plotYValues[0])/
			   (plotYValues[1]-plotYValues[0]))
			  *(plotMaxY-plotZeroY));
    }

    public boolean isBadDataPoint (float f) {
	boolean r=false;
	if (Float.isNaN(f)) r=true;
	if (Float.isInfinite(f)) r=true;
	return r;
    }
    
    public void paint(Graphics g) {
	paintWrapper(g);
    }

    public void paintWrapper (Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	int i;
	float x,y;

	// Ensure image is valid
	if (image == null) {
	    initialize();
	}

	// Draw the background window
	g2.setPaint (backCol);
	g2.fillRect (0,0,panX,panY);

	// Draw the thermometer
	g2.setPaint (emptyCol);
	g2.drawRect (tempLeftX,tempMaxY,neckWidth-1,tempHeight);
	g2.setPaint (hiTemp ? mercuryHiCol : mercuryCol);
	g2.fillOval (bord,tempZeroY,bulbWidth,bulbWidth);
	g2.fillRect (tempLeftX, tempToScrn(t),
		     neckWidth,tempZeroY-tempToScrn(t)+1);
	g2.setPaint (criticalCol);
	g2.drawLine (tempLeftX, tempToScrn(tc),
		     tempLeftX+neckWidth-1,tempToScrn(tc));
	g2.setPaint (labelCol);
	for (i=0;i<(maxTemp/tempTick);i++) {
	    g2.drawLine (tempLeftX, tempToScrn(i*tempTick),
			 tempLeftX+neckWidth-1, tempToScrn(i*tempTick));
	}

	// Draw the magnet image
	g2.drawImage (image, thermShift, 0, this);

	// Draw plot and thermometer labels
	Font font = new Font("SansSerif",Font.PLAIN,12);
	g2.setFont (font);
	FontRenderContext frc = g2.getFontRenderContext();
	String writer="X";
	float fWidth=(float) font.getStringBounds(writer,frc).getWidth();
	float fHeight=(float) font.getStringBounds(writer,frc).getHeight();
	
	g2.setPaint(labelCol);
	for (i=0;i<=maxTemp;i++) {
	    writer = (new Integer(i)).toString();
	    g2.drawString (writer, tempLeftX+neckWidth+fWidth/2,
			   tempToScrn((float)i)+fHeight/2);
	}
	g2.drawString ("Tc", tempLeftX+neckWidth+fWidth/2,
		       tempToScrn(tc)+fHeight/2);

	
	if (numericalMode) {
	    // Draw the plot axes
	    g2.setPaint (plotAxisCol);
	    g2.drawLine (plotZeroX, plotZeroY, plotMaxX, plotZeroY);
	    g2.drawLine (plotZeroX, plotZeroY, plotZeroX, plotMaxY);
	    
	    // Draw the plot points
	    g2.setPaint (plotPointCol);
	    
	    // Determine what the data source is
	    switch (xDataSource) {
	    case 0 : 
		xVector = (Vector) tempVector.clone();
		xDataString = new String("Temperature");
		break;
	    case 1:
		xVector = (Vector) energyVector.clone();
		xDataString = new String("Energy");
		break;
	    case 2:
		xVector = (Vector) magnetVector.clone();
		xDataString = new String("Magnetization");
		break;
	    default:
		xVector = (Vector) tempVector.clone();
		xDataString = new String("Temperature");
		break;
	    }
	    switch (yDataSource) {
	    case 0 :
		yVector = (Vector) energyVector.clone();
		yDataString = new String("Energy");
		break;
	    case 1:
		yVector = (Vector) specHeatVector.clone();
		yDataString = new String ("Specific Heat");
		break;
	    case 2:
		yVector = (Vector) magnetVector.clone();
		yDataString = new String ("Magnetization");
		break;
	    case 3:
		yVector = (Vector) susceptVector.clone();
		yDataString = new String ("Susceptability");
		break;
	    default:
		yVector = (Vector) specHeatVector.clone();
		yDataString = new String ("Specific Heat");
		break;
	    }
		 
	    // Get the plot limits
	    plotXValues = vectorMaxMin(xVector);
	    plotYValues = vectorMaxMin(yVector);
	    if ((isBadDataPoint(plotXValues[0])) ||
		(isBadDataPoint(plotXValues[1])) ||
		(isBadDataPoint(plotYValues[0])) ||
		(isBadDataPoint(plotYValues[1]))) {
		clearVectors();
		plotXValues[0]=0;
		plotXValues[1]=1;
		plotYValues[0]=0;
		plotYValues[1]=1;
	    }

	    // Set the range in certain instances

	    // of T on x axis
	    if (xDataSource==0) {

		// Set full temperature range
		plotXValues[0]=0;
		plotXValues[1]=maxTemp;

		// Draw Tc line
		g2.setPaint (criticalCol);
		if ((tc>plotXValues[0]) && (tc<plotXValues[1])) {
		    g2.setPaint (criticalCol);
		    g2.drawLine (xToScrn(tc),plotZeroY,
				 xToScrn(tc),plotMaxY);
		}
	    }

	    // M on x axis
	    if (xDataSource==2) {
		// Set range -1 to +1
		plotXValues[0]=-1f;
		plotXValues[1]=1f;
		// Draw zero line
		g2.setPaint (zeroCol);
		g2.drawLine (xToScrn(0),plotZeroY,
			     xToScrn(0),plotMaxY);
	    }

	    // M on y axis
	    if (yDataSource==2) {
		// set range -1 to +1
		plotYValues[0]=-1f;
		plotYValues[1]=+1f;
		// Draw zero line
		g2.setPaint (zeroCol);
		g2.drawLine (plotZeroX, yToScrn(0),
			     plotMaxX, yToScrn(0));
	    }

	    // E on x axis
	    if (xDataSource==1) {
		// set range -2 to +2
		plotXValues[0]=-2f;
		plotXValues[1]=+2f;
		// Draw zero line
		g2.setPaint (zeroCol);
		g2.drawLine (xToScrn(0),plotZeroY,
			     xToScrn(0),plotMaxY);
	    }

	    // E on y axis
	    if (yDataSource==0) {
		// set range -2 to +2
		plotYValues[0]=-2f;
		plotYValues[1]=+2f;
		// Draw zero line
		g2.setPaint (zeroCol);
		g2.drawLine (plotZeroX, yToScrn(0),
			     plotMaxX, yToScrn(0));
	    }

	    // C on y axis
	    if (yDataSource==1) {
		// set lower range to 0
		plotYValues[0]=0;
	    }

	    // Chi on y axis
	    if (yDataSource==3) {
		// set lower range to 0
		plotYValues[0]=0;
	    }	  

	    // Draw plot labels
	    g2.setPaint (labelCol);

	    writer = xDataString;
	    fWidth=(float) font.getStringBounds(writer,frc).getWidth();
	    fHeight=(float) font.getStringBounds(writer,frc).getHeight();
	    g2.drawString (writer, (plotZeroX+plotMaxX-fWidth)/2,
			 plotZeroY+1f*fHeight);

	    writer = yDataString;
	    fWidth=(float) font.getStringBounds(writer,frc).getWidth();
	    fHeight=(float) font.getStringBounds(writer,frc).getHeight();
	    g2.rotate (-Math.PI/2.0);
	    g2.drawString (writer, -(plotMaxY+plotZeroY+fWidth)/2,
			   +plotZeroX-0.5f*fHeight);			 
	    g2.rotate (+Math.PI/2.0);

	    // Draw the plot points
	    g2.setPaint (plotPointCol);
	    for (i=0; i<vectorSize; i++) {
		x=((Float)xVector.get(i)).floatValue();
		y=((Float)yVector.get(i)).floatValue();
		g2.fillOval (xToScrn(x)-plotPointRad,yToScrn(y)-plotPointRad,
			     2*plotPointRad, 2*plotPointRad);
	    }
	}
    }

    public void setBounds (int x, int y, int width, int height) {
	// This is screwed up, need to stop the initialize from
	// always occuring, not sure why setBounds is repeatedly
	// being called, this hack avoids the initialize
	// unless it's necessary.
	if ((panX!=width)||(panY!=height)) {
	    panX=width;
	    panY=height;
	    super.setBounds(x,y,width,height);
	    System.out.println("bounds");
	    initialize();
	}
    }

}
 
