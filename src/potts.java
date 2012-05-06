/*****************************************************************************

    Physics Demos - Potts Model Simulation

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

public class potts extends JApplet {

    public pottsFrame fr;
	
    public void init() {
	setName ("Potts-JApplet");
	fr = new pottsFrame();
	//fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void start() {
	fr.pottsInit();
    }

    public void stop() {
    }

    public void destroy() {
    }
}

class pottsFrame extends JFrame {

    pottsPanel magDraw;
    JPanel panCont;
    Container buttonCont, drawCont, frameCont, heatCont, magCont, srCont,
	flowButCont;
    //Container programControlCont, MECellCont,infoCont;
    JButton quitButton;
    JToggleButton pauseButton;       
    JToggleButton conserveEButton, heatButton, coolButton, freeEButton;
    JToggleButton conserveMButton, increaseMButton, 
	decreaseMButton, freeMButton;
    JButton saveButton, restoreButton;
    SpinnerNumberModel qModel;
    JSpinner qSpinner;
    JComboBox cellCombo;
    JSlider speedSlider;
    JTextArea messageBox, projectBox;
    JLabel betaLab, hLab;
    Color defaultColor, hiColor;

    public pottsFrame (){

	super("Potts Model Simulation Applet");
	magDraw = new pottsPanel();
	panCont = new JPanel();

	JPanel p, b; // dummy panel

	conserveEButton = new JToggleButton ("Conserve E");
	heatButton = new JToggleButton ("Heat");
	coolButton = new JToggleButton ("Cool");
	freeEButton = new JToggleButton ("Free E");
	heatCont = new Container();
	heatCont.setLayout (new GridLayout(5,1));
	heatCont.add (new JLabel("Heater Controls"));
	heatCont.add (conserveEButton);
	heatCont.add (heatButton);
	heatCont.add (coolButton);
	heatCont.add (freeEButton);

	conserveMButton = new JToggleButton ("Conserve M");
	increaseMButton = new JToggleButton ("Increase M");
	decreaseMButton = new JToggleButton ("Decrease M");
	freeMButton = new JToggleButton ("Free M");
	magCont = new Container();
	magCont.setLayout (new GridLayout(5,1));
	magCont.add (new JLabel("Magnetic Field Controls"));
	magCont.add (conserveMButton);
	magCont.add (increaseMButton);
	magCont.add (decreaseMButton);
	magCont.add (freeMButton);

	pauseButton = new JToggleButton ("Pause");
	quitButton = new JButton ("Quit");
	saveButton = new JButton ("Save");
	restoreButton = new JButton ("Restore");
	flowButCont = new Container();
	flowButCont.setLayout (new GridLayout (2,2));
	flowButCont.add (pauseButton);
	flowButCont.add (saveButton);
	flowButCont.add (quitButton);
	flowButCont.add (restoreButton);

	qModel = new SpinnerNumberModel (8,2,16,1);
	qSpinner = new JSpinner(qModel);

	String [] cellSizeList = {"1","2","4","8"};
	int[] cellSizeIntList = {1,2,4,8};
	JComboBox cellCombo = new JComboBox(cellSizeList);
	cellCombo.setEditable(false);
	Box cellCont = Box.createVerticalBox();
	cellCont.add (new JLabel("Cell Size"));
	cellCont.add (cellCombo);
	cellCont.add (new JLabel("Number of States"));
	cellCont.add (qSpinner);

	speedSlider = new JSlider (JSlider.HORIZONTAL, 0, 50, 40);
	speedSlider.setMajorTickSpacing(10);
	speedSlider.setMinorTickSpacing(5);
	speedSlider.setPaintTicks(true);
	speedSlider.setPaintLabels(true);

	messageBox = new JTextArea("Potts Model Simulation",5,40);
	messageBox.setEditable(false);
	messageBox.setBorder(BorderFactory.createLineBorder(Color.black));

	projectBox = new JTextArea();
	projectBox.setText("Demonstration part of :\n");
	projectBox.append("Virtual Quantum Mechanics\n");
	projectBox.append("Johns Hopkins University\n");
	projectBox.append("CER TF 2004-2005\n");
	projectBox.append("www.pha.jhu.edu/~javalab\n");
	projectBox.setEditable(false);
	projectBox.setBorder(BorderFactory.createLineBorder(Color.black));
	
	buttonCont = new Container();
	buttonCont.setLayout (new FlowLayout());

	Box programControlCont = Box.createVerticalBox();
	//programControlCont.setLayout (new GridLayout(3,1));
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
	p.add(magCont);
	MECellBox.add(p);
	p = new JPanel();
	p.add(heatCont);	
	MECellBox.add (p);

	Box infoBox = Box.createVerticalBox();
	infoBox.add (messageBox);
	infoBox.add (projectBox);

	buttonCont.add(programControlCont);
	buttonCont.add(MECellBox);

	frameCont = getContentPane();
	frameCont.setLayout(new BorderLayout());
	frameCont.add(buttonCont, BorderLayout.NORTH);
	frameCont.add(magDraw, BorderLayout.CENTER);
	frameCont.add(infoBox, BorderLayout.EAST);

	pack();
	setVisible(true);

	defaultColor = conserveEButton.getBackground();
	hiColor = Color.blue;

	qModel.addChangeListener( new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    Integer ob = (Integer)qModel.getValue();
		    magDraw.setQ(ob.byteValue());
		    magDraw.initialize();
		}
	    });

	cellCombo.addActionListener( new ActionListener() {
		public void actionPerformed (ActionEvent e) {
		    JComboBox cb = (JComboBox)e.getSource();
		    String ob = (String)cb.getSelectedItem();
		    magDraw.setCellSize(Integer.parseInt(ob));
		    magDraw.initialize();
		    //System.out.println("cell");
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

	conserveEButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    heatButton.setSelected(false);
		    coolButton.setSelected(false);
		    freeEButton.setSelected(false);
		    magDraw.heaterValue=0;
		}
	    });
	heatButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    conserveEButton.setSelected(false);
		    coolButton.setSelected(false);
		    freeEButton.setSelected(false);
		    magDraw.heaterValue=1;
		}
	    });
	coolButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    conserveEButton.setSelected(false);
		    heatButton.setSelected(false);
		    freeEButton.setSelected(false);
		    magDraw.heaterValue=2;
		}
	    });
	freeEButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    conserveEButton.setSelected(false);
		    coolButton.setSelected(false);
		    heatButton.setSelected(false);
		    magDraw.heaterValue=3;
		}
	    });	
	conserveMButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    increaseMButton.setSelected(false);
		    decreaseMButton.setSelected(false);
		    freeMButton.setSelected(false);
		    magDraw.magValue=0;
		}
	    });	
	increaseMButton.addActionListener (new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    conserveMButton.setSelected(false);
		    decreaseMButton.setSelected(false);
		    freeMButton.setSelected(false);
		    magDraw.magValue=1;
		}
		});
	decreaseMButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    increaseMButton.setSelected(false);
		    conserveMButton.setSelected(false);
		    freeMButton.setSelected(false);
		    magDraw.magValue=2;
		}
	    });	
	freeMButton.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JToggleButton b = (JToggleButton)e.getSource();
		    b.setSelected(true);
		    increaseMButton.setSelected(false);
		    decreaseMButton.setSelected(false);
		    conserveMButton.setSelected(false);
		    magDraw.magValue=3;
		}
	    });

    }

    public void pottsInit () {
	System.out.println("starting thread");
	new Thread (magDraw).start();
	System.out.println("thread was started");
    }
}

class pottsPanel extends JPanel implements Runnable {

    public int xsize, ysize, magX, magY, panX,panY;
    //public int nrows, ncols, volume;
    public byte q=8;
    public int cellSize=1;
    //public int block, blockshift;
    public int heaterValue=2, magValue=3;
    public int demon, mDemon;
    public int energyCount=0, mCount=0, volumeCount=0;
    public boolean paused=false,quit=false;
    public int speed;
    public byte[][] data;
    public byte[] imageData;
    public DataBuffer db;
    public WritableRaster wr;
    public ColorModel cm;
    public Random random;
    public BufferedImage image;
    private pottsFrame parent;
    private int iteration;
    private float eCalc=0f,mCalc=0f;
    private int bw=0, bh=0;

    Color backCol = new Color (0,0,0);
    Color spinCol = new Color (0xDD,0xDD,0);

    public pottsPanel () {
	xsize=190;
        ysize=190;
        setPreferredSize (new Dimension(xsize,ysize));
    }

    synchronized public void imageDataCreate () {

	//System.out.println("idc - "+magX+","+magY+","+xsize+","+ysize);

	/*byte[][] data,
					      byte[] d,
					      int magX, 
					      int magY, 
					      int xsize, 
					      int ysize) {*/
	int i,j,k,l;
	for (i=0;i<magX;i++) 
	    for (j=0;j<magY;j++)
		for (k=0;k<cellSize;k++)
		    for (l=0;l<cellSize;l++)
			//following line OCCASIONALLY has error
			//"ArrayIndexOutOfBoundsException"
			//imageData[(j*cellSize+l)*ysize+i*cellSize+k]=
			imageData[(j*cellSize+l)*ysize+i*cellSize+k]=
			    data[i][j];
    }

    synchronized public void setQ(byte inQ) {
	q=inQ;
    }

    synchronized public void setCellSize(int inCellSize) {
	cellSize=inCellSize;
    }       
	
    synchronized public void initialize() {
	iteration=0;
	volumeCount=0;
	energyCount=0;
	mCount=0;
	//System.out.println("Reinit");
	parent = (pottsFrame) getTopLevelAncestor();
	parent.freeMButton.doClick();
	parent.coolButton.doClick();
	speed = parent.speedSlider.getValue();
	int i,j;
	if (panX<xsize) xsize=panX;
	if (panY<ysize) ysize=panY;
	magX=xsize/cellSize;
	magY=ysize/cellSize;
	// Account for demons hopping 29 rows
	// xtoys ensures magY is multiple of sizeof(long) here, not sure why
	if ((magX%29)==0) magX-=1;
	if ((magY%29)==0) magY-=1;
	data = new byte[magX][magY];
	imageData = new byte[xsize*ysize];
	db = new DataBufferByte (imageData, xsize*ysize);
	wr = Raster.createPackedRaster
	    (db,xsize,ysize,8,null);
	byte ze=(byte)0;
	byte ff=(byte)255;
	byte hf=(byte)127;
	cm = new IndexColorModel
	    (1,16,
	     new byte[] {ze,ff,ze,ze,ff,ff,ze,ff,hf,ze,hf,hf,ff,ze,hf,hf},
	     new byte[] {ze,ze,ze,ff,ff,ff,ff,ze,hf,hf,ze,hf,hf,ff,ze,hf},
	     new byte[] {ze,ze,ff,ze,ff,ze,ff,ff,ze,hf,hf,hf,ze,hf,ff,ff});
	image = new BufferedImage(cm, wr, false, null);
	random = new Random();
	
	// start with a random state here
	for (i=0; i<magX; i++)
	    for (j=0; j<magY; j++) {
		data[i][j]=(byte)random.nextInt(q);
	    }
    }
	
    public void run() {
	System.out.println("Running...");
	int i,j;
	String ps;
	float em=3.1f, m=0.987f;
	if (random == null) {
	    System.out.println("Random");
	    initialize();
	}
	while (!quit) {
	    if (!paused)
		{
		    // calculate next frame of magnet data
		    update();
		    // build the image from the magnet data
		    imageDataCreate ();
		    repaint();
		    // write the system parameters occasionally
		    if ((iteration%20)==0) {
			paramWrite();
			iteration=0;
		    }
		    iteration++;
		}
	    try {Thread.sleep(1000/speed);}
	    catch (InterruptedException e) {}
	}
    }

    synchronized public void paramWrite() {

	float beta, h;
	String ps = new String();
	calculateEM();
	ps+="Grid Size "+magX+" by "+magY;
	ps+="  -- "+q+" Potts States\n";
	ps+="Beta : ";
	if ((2*volumeCount>energyCount)&&(energyCount!=0))
	    ps+=0.5*Math.log((2f*volumeCount/(1f*energyCount)-1f));
	else ps +="Divergent";
	ps+="\nBeta Critical : "+Math.log(1f+Math.sqrt(1f*q));
	ps+="\nh : ";
	if ((volumeCount>mCount)&&(mCount!=0))
	    ps += (-Math.log((volumeCount/(1f*mCount)-1f)));
	else ps += "Divergent";
	ps+="\n";
	volumeCount=0;
	energyCount=0;
	mCount=0;
	ps+="E : "+eCalc+"\n";
	ps+="M : "+mCalc+"\n";
	parent.messageBox.setText(ps);
    }

    synchronized public void update() {

	// demonMask was 15 in xpotts, 255 seems to make it
	// run much quicker and smoother in this java sim.
	int demonMask=1023;
	int newDemon, oldEnergy, newEnergy;
	int rowLoop, colLoop, newMDemon;
	float beta, hfield, e, m;
	int i, row=1, col=1;
	byte i1, i2, i3, i4, oldSpin, trial, direction;

	// start on a random row/col
	row=1+random.nextInt(magY-2);
	col=1+random.nextInt(magX-2);

	for (rowLoop=2; rowLoop<magY; rowLoop++) {
	    int goofy=0;
	    row+=29;
	    while (row>=(magY-1)) row-=(magY-2);
	    direction=(byte)(1+(byte)random.nextInt(q-1));
	    if (heaterValue>0)
		// 1 in 2 chances
		if (((byte)random.nextInt(2))==0) {
		    //System.out.println("energy demon");
		    if (heaterValue==2) demon=0;
		    if ((heaterValue==1)&&(demon<demonMask)) demon++;
		    if (heaterValue==3) demon=(demonMask>>1);
		}

	    if (magValue>0)
		//1 in 32 chances (although xtoys code says 1 in 16 chance)
		if (((byte)random.nextInt(32))==0) {
		    //System.out.println("mag demon");
		    if (magValue==2) mDemon=0;
		    if (magValue==1) mDemon=demonMask;
		    if (magValue==3) mDemon=(demonMask>>1);
		}
	    //System.out.println(row+" ---- "+col);
	    for (colLoop=2; colLoop<magX; colLoop++) {
		//System.out.println(row+" -- "+col);
		col+=29;
		while (col>=(magX-1)) col-=(magX-2);
		oldSpin=data[col][row];
		i1=data[col+1][row];
		i2=data[col-1][row];
		i3=data[col][row+1];
		i4=data[col][row-1];
		// ** change following 2 lines to %q
		trial=(byte)(oldSpin+direction);
		if (trial>=q) trial-=q;
		oldEnergy=0;
		if (oldSpin==i1) oldEnergy++;
		if (oldSpin==i2) oldEnergy++;
		if (oldSpin==i3) oldEnergy++;
		if (oldSpin==i4) oldEnergy++;
		newEnergy=0;
		if (trial==i1) newEnergy++;
		if (trial==i2) newEnergy++;
		if (trial==i3) newEnergy++;
		if (trial==i4) newEnergy++;
		newDemon=demon-oldEnergy+newEnergy;
		newMDemon=mDemon;
		if (trial==0) mDemon--;
		if (oldSpin==0) mDemon++;
		if (((newDemon|newMDemon)&(~demonMask))==0) {
		    goofy++;
		    /*
		      System.out.println("neighbors are "+i1+","+i2+","+i3+","+i4);
		      System.out.println("Flip "+col+","+row+" from "+oldSpin+" to "+trial);
		      System.out.println("oldE "+oldEnergy+" -- newE "+newEnergy);
		      System.out.println("old demon "+demon+" -- new demon "+newDemon);
		    */
		    demon=newDemon;
		    mDemon=newMDemon;
		    data[col][row]=trial;
		    direction=(byte)(q-direction);
		    // fix vertical boundary
		    if (col==1)
			data[magX-1][row]=data[1][row];
		    else if (col==(magX-2))
			data[0][row]=data[magX-2][row];
		}
		volumeCount++;
		energyCount+=(2&demon);
		mCount+=(1&mDemon);
	    }
	    //System.out.println("row "+row+" : "+goofy+" flips : demon "+demon);
	    // fix horizontal boundary
	    if (row==1)
		for (i=0;i<magX;i++)
		    data[i][magY-1]=data[i][1];
	    else if (row==(magX-2))
		for (i=0;i<magX;i++)
		    data[i][0]=data[i][magY-2];
	}
	
    }
    
    synchronized public int calculateEM () {
	
	int i,j;
	int etot=0,mtot=0;
	for (i=1;i<(magX-1);i++)
	    for (j=1;j<(magY-1);j++) {
		if (data[i][j]==data[i+1][j]) etot++;
		if (data[i][j]==data[i][j+1]) etot++;
		if (data[i][j]==0) mtot++;
	    }
	eCalc=(1f-etot/(2f*(magX-2)*(magY-2)))/(1f-1f/q);
	mCalc=(mtot/(1f*(magX-2)*(magY-2))-1f/q)/(1-1f/q);
	return etot;
    }
    
    public void paint(Graphics g) {
	paintWrapper(g);
    }

    public void paintWrapper (Graphics g) {
	Graphics2D g2 = (Graphics2D) g;

	// Ensure image is valid
	if (image == null) {
	    initialize();
	    System.out.println("Image");
	}

	// Draw the background window
	g2.setPaint (backCol);
	g2.fillRect (0,0,panX,panY);

	// Draw the magnet image
	g.drawImage (image, 0, 0, this);
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
 
