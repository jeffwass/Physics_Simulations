/*****************************************************************************

    Physics Demos - One Dimensional Crystal Simulation

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
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.lang.Math.*;
import java.util.Random;

public class crystal1d extends JApplet {
    
    public crystalFrame fr;

    public void init() {
	setName ("crystal1d-JApplet");
	fr = new crystalFrame();
    }

    public void start() {
	fr.panDraw.reSizeInit();
	fr.panDraw.readyToDraw=true;
	fr.panDraw.init();
	fr.panDraw.repaint();
    }

    public void stop() {
    }

    public void destroy() {
    }
}

class crystalFrame extends JFrame {

    cryPanel panDraw;
    JPanel panCont;
    JPanel nPan;
    Container boxCont;
    JScrollBar nScroll;
    JLabel nTit, nLab;
    public JLabel kLab,kappaLab,aLab,trLab;
    public TableModel model;
    public JTable paramTable;
    JButton butRanH,butRanX,butInit;
    JButton butRedraw;
    Container cont;

    public crystalFrame (){

	super("Quantum Scattering of a 1-D Crystal");
	setName("crystalFrame-Jframe");
	panDraw = new cryPanel();
	panCont = new JPanel();
	
	nTit = new JLabel ("Number of Atoms");
	nLab = new JLabel ("1");
	nScroll = new JScrollBar (JScrollBar.HORIZONTAL, 1, 5, 1,100);
	kLab = new JLabel ("k = ");
	kappaLab = new JLabel ("kappa = ");
	aLab = new JLabel ("a = ");
	 trLab = new JLabel ("T = ");
	model = new cryParamTableModel();
	paramTable = new JTable (model);
	    //butInput = new JButton ("Input Values");
	butRanH = new JButton ("Random Height");
	butRanX = new JButton ("Random Spacing");
	butInit = new JButton ("Initialize");
	butRedraw = new JButton ("Toggle Redraw");

	boxCont = Box.createVerticalBox();
	boxCont.add(Box.createVerticalGlue());
	boxCont.add(nTit);
	boxCont.add(nLab);
	boxCont.add(nScroll);
	boxCont.add(Box.createVerticalGlue());
	//boxCont.add(butInput);
	//boxCont.add(butRandom);
	//boxCont.add(kLab);
	//boxCont.add(kappaLab);
	//boxCont.add(aLab);
	//boxCont.add(trLab);
	boxCont.add(Box.createVerticalGlue());
	boxCont.add(butRanX);
	boxCont.add(butRanH);
	boxCont.add(butInit);	
	boxCont.add(butRedraw);
	boxCont.add(Box.createVerticalGlue());
	boxCont.add(paramTable);
	//boxCont.add(Box.createVerticalGlue());
	boxCont.add(Box.createVerticalGlue());
	boxCont.add(new JLabel("Demonstration part of :"));
	boxCont.add(new JLabel("Virtual Quantum Mechanics"));
	boxCont.add(new JLabel("Johns Hopkins University"));
	boxCont.add(new JLabel("CER TF 2003-2004"));
	boxCont.add(new JLabel("www.pha.jhu.edu/~javalab"));
	
	cont = getContentPane();
	cont.setLayout(new BorderLayout());
	cont.add(panDraw, BorderLayout.WEST);
	cont.add(boxCont, BorderLayout.EAST);

	pack();
	setVisible(true);

	// Seed the random number generator (use defined int
	// just for pseudo-random functionality)

	// Handle ScrollBar Listener
	nScroll.addAdjustmentListener (new AdjustmentListener() {
		public void adjustmentValueChanged(AdjustmentEvent e) {
		    int n=nScroll.getValue();
		    nLab.setText(""+n);
		    if (nScroll.getValueIsAdjusting()) panDraw.tranValid=false;
		    else panDraw.tranValid=true;
		    panDraw.n=n;
		    panDraw.nSet();
		}
	    });

	/*
	// Handle Manual Input Button
	butInput.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    JOptionPane.showMessageDialog (null,"This option will eventually be used to manually input n, k, kappa, and a.");
		}
		}); */

	// Handle Randomize Buttons
	butRanX.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    panDraw.isSpacingRan=true;
		    panDraw.nSet();
		}
	    });
	butRanH.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    panDraw.isHeightRan=true;
		    panDraw.nSet();
		}
	    });		    
	

	// Handle Initialize Button
	butInit.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    nScroll.setValue(1);
		    nLab.setText("1");
		    panDraw.init();
		}
	    });      

	// Handle redraw
	butRedraw.addActionListener (new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    panDraw.dragRedraw=!panDraw.dragRedraw;
		}
	    });
    }

    public class myJTable extends JTable {

	public int tabRowK=0;
	public int tabRowKappa=1;
	public int tabRowA=2;
	public int tabRowTr=3;
	
	public TableCellRenderer getCellRenderer(int row, int column) {
	    Object value = getValueAt (row,column);
	    if (value != null) {
		return getDefaultRenderer(value.getClass());
	    }
	    return super.getCellRenderer(row,column);
	}
    };

    class cryParamTableModel extends AbstractTableModel {
	private String[] headings = new String[] {
	    "Field", "Value"};
	private Object[][] data = new Object[][] {
	    {"k", new Float (0.8f)},
	    {"kappa", new Float (1.0f)},
	    {"a", new Float (5.0f)},
	    {"Tr", new Float(0.5f)}
	};
	
	public int getRowCount() {return data.length;}
	public int getColumnCount() {return data[0].length;}
	public Object getValueAt(int row, int column) {
	    return data[row][column];}
	public String getColumnName(int column) {
	    return headings[column];
	}
	public Class getColumnClass(int column) {
	    return data[0][column].getClass();
	}
	public void setValueAt (Object value, int row, int column) {
	  data[row][column] = value;
	  fireTableDataChanged();
	}
    } 
    
}

class cryPanel extends JPanel
    implements MouseMotionListener, MouseListener {

    public float k=0.8f;
    public float a=10.0f;
    public float kappa=1.0f;
    public int n=1;
    public float tranMat[];
    public float delY[];
    public float delX[];
    public int tranSize;
    public boolean readyToDraw=false;
    public boolean isHeightRan=false;
    public boolean isSpacingRan=false;

    int prefX=600;
    int prefY=100;

    int bord=10;
    int fontSize=8;
    int fontSpace=20;
    public int wX;
    public int wY;
    public int drX;
    public int drY;
    int x,y;
    
    int boxR=3;
    int clickR=5;
    int curTran=7;
    float kMax=2.0f;
    float xMax=100.0f;
    float intcpX=-5.0f;
    int nDraw;
    public float ranX;
    public float ranY=kMax/10;
    int kScr;
    boolean hiK, hiA, hiKappa;
    boolean moveK=false;
    boolean moveKappa=false;
    boolean moveA=false;
    boolean tranValid=true;
    boolean dragRedraw=true;
    
    Color offCol = new Color (0xAA,0xAA,0xAA);
    Color backCol = new Color (0,0,0);
    Color kCol = new Color (0xFF,0xFF,0);
    Color kappaMoveCol = new Color (0,0xAA,0xAA);
    Color potCol = new Color (0xFF,0xFF,0xFF);
    Color tranCol = new Color (0,0,0xFF);
    Color tranCurCol = new Color (0,0xFF,0xFF);
    Color tranInvalidCol = new Color (0x77,0x77,0x77);
    Color unHiBoxCol = new Color (0xAA,0,0);
    Color hiBoxCol = new Color (0xFF,0,0);
    Color textCol = new Color (0,0,0);

    public cryPanel () {
	setPreferredSize (new Dimension(prefX,prefY));
	addMouseListener(this);
	addMouseMotionListener(this);
	delX = new float[1];
	delX[0] = 0.0f;
	delY = new float[1];
	delY[0] = 1.0f;
    }

    public void init() {
	isHeightRan=false;
	isSpacingRan=false;
	tranValid=true;
	n=1;
	kappa=1.0f;
	k=0.8f;
	a=10.0f;
	nSet();
    }

    public void reSizeInit() {
	wX=getSize().width;
	wY=getSize().height;
	drX=(wX-3*bord-2*fontSpace)/2;
	drY=(wY-2*bord-fontSpace);
	tranSize=drY;
	tranCalc();
    }	

    public void nSet() {
	
	int i;
	int j;
	float dum;

	// Build the array of atoms
	delX=new float[n];
	delY=new float[n];
	for (i=0;i<n;i++) {
	    delX[i]=i*a;
	    if (isSpacingRan) delX[i]=(float)Math.random()*(n-1f)*a;
	    delY[i]=kappa;
	    if (isHeightRan) delY[i]+=((float)Math.random()-0.5f)*kappa*2f;
	    if (xToScr(delX[i])<(bord+drX)) nDraw=i+1;
	}

	// If random spacing, sort the atoms by position
	if (isSpacingRan) {
	    for (i=0;i<(n-1);i++) for (j=i+1;j<n;j++) {
		if (delX[i] > delX[j]) {
		    dum=delX[i];
		    delX[i]=delX[j];
		    delX[j]=dum;
		}
	    }
	}

	if (tranValid || dragRedraw) 
	    tranCalc();
	repaint();
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (hiK) moveK=true;
	if (hiKappa) moveKappa=true;
	if (hiA) moveA=true;
    }

    public void mouseReleased(MouseEvent e) {
        moveK=false;
	moveKappa=false;
	moveA=false;
	if (!tranValid) {
	    tranValid=true;
	    tranCalc();
	}
	repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
	if (moveK) {
	    k=bound (scrToK(e.getY()),0f,kMax);
	}
	if (moveKappa) {
	    kappa=bound (scrToK(e.getY()),0f,kMax);
	    tranValid=false;
	    nSet();
	}
	if (moveA) {
	    a=bound (scrToX(e.getX()),0f,xMax);
	    tranValid=false;
	    nSet();
	}
	paramUpdate();
	repaint();
    }

    public void mouseMoved(MouseEvent e) {
        hiK=false;
	hiKappa=false;
	hiA=false;
	int x=e.getX();
	int y=e.getY();
        if (inRange(x,y,bord+fontSpace+(clickR/2),kToScr(k),clickR))
	    hiK=true;
	if (inRange(x,y,xToScr(delX[0]),kToScr(delY[0]),clickR))
	    hiKappa=true;
	if ((n>1) && (!isSpacingRan))
	    if (inRange(x,y,xToScr(delX[1]),kToScr(0.0f),clickR))
		hiA=true;
        repaint();
    }

    public void paint (Graphics g) {
	if (readyToDraw) paintWrapper (g);
    }

    public void paintWrapper (Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	int i;
	int k0 = kToScr(0);
	int kScr = kToScr(k);

	// Draw Two Windows
	g2.setPaint (offCol);
       	g2.fillRect (0,0,wX,wY);
	g2.setPaint(backCol);
	g2.fillRect (bord+fontSpace,bord,drX,drY);
	g2.fillRect (drX+2*bord+2*fontSpace, bord, drX, drY);

	// Draw the Graph Axes Text
	Font font = new Font("SansSerif",Font.PLAIN,12);
	g2.setFont (font);
	FontRenderContext frc = g2.getFontRenderContext();
	String ax = "Position";
	g2.drawString (ax, bord+fontSpace+(drX/2)-Math.round
		       (font.getStringBounds(ax,frc).getWidth()/2),
		       bord+drY+(fontSpace/2)+Math.round
		       (font.getStringBounds(ax,frc).getHeight()/2));
	ax = "Probability";
	g2.drawString (ax, 2*bord+2*fontSpace+(3*drX)/2-Math.round
		       (font.getStringBounds(ax,frc).getWidth()/2),
		       bord+drY+(fontSpace/2)+Math.round
		       (font.getStringBounds(ax,frc).getHeight()/2));
	g2.rotate (-Math.PI/2.0);
	ax = "Momentum";
	g2.drawString (ax, -bord-(drY/2)-Math.round
		       (font.getStringBounds(ax,frc).getWidth()/2),
		       bord+fontSpace/2+Math.round
		       (font.getStringBounds(ax,frc).getHeight()/2));
	g2.drawString (ax, -bord-(drY/2)-Math.round
		       (font.getStringBounds(ax,frc).getWidth()/2),
		       fontSpace+drX+2*bord+fontSpace/2+Math.round
		       (font.getStringBounds(ax,frc).getHeight()/2));
	g2.rotate (+Math.PI/2.0);

	// Draw the line of k-momentum
	g2.setPaint (kCol);
	g2.drawLine (bord+fontSpace,kScr,bord+fontSpace+drX-1,kScr);
	g2.setPaint (hiK ? hiBoxCol : unHiBoxCol);
	g2.fillRect (bord+fontSpace,kScr-boxR,2*boxR,2*boxR);

	// Draw the Delta-Function Potential Profile
	g2.setPaint (potCol);
	g2.drawLine (bord+fontSpace,k0,bord+fontSpace+drX-1,k0);
	for (i=0;i<n;i++) {
	    if (xToScr(delX[i])<(bord+fontSpace+drX-1))
		g2.drawLine (xToScr(delX[i]),k0,
			     xToScr(delX[i]),kToScr(delY[i]));
	}
	// Draw the kappa-adjust box and line
	g2.setPaint (unHiBoxCol);
	if (hiKappa) {
	    g2.setPaint (kappaMoveCol);
	    g2.drawLine (bord+fontSpace, kToScr(kappa),
			 bord+fontSpace+drX-1,kToScr(kappa));
	    g2.setPaint (hiBoxCol);
	}
	g2.fillRect (xToScr(delX[0])-boxR, kToScr(delY[0])-boxR,
			 2*boxR, 2*boxR);
	if ((n>1) && !(isSpacingRan)) {
	    g2.setPaint (hiA ? hiBoxCol : unHiBoxCol);
	    g2.fillRect (xToScr(delX[1])-boxR, k0-boxR, 2*boxR, 2*boxR);
	}

	// Draw the Transmission Probability graph
	g2.setPaint ((tranValid || dragRedraw) ? tranCol : tranInvalidCol);
	for (i=0; i<(tranSize-2); i++) {
	    g2.drawLine (2*(bord+fontSpace)+drX+Math.round((drX-1)*tranMat[i]),
			 kToScr(i*kMax/(tranSize)),
			 2*(bord+fontSpace)+drX+
			 Math.round((drX-1)*tranMat[i+1]),
			 kToScr((i+1)*kMax/(tranSize)));
	}
	g2.setPaint ((tranValid || dragRedraw) ? tranCurCol : tranInvalidCol);
	int dumIndex = (int)Math.round(Math.floor(k/kMax*(float)(tranSize-1)));
	g2.fillOval (2*(bord+fontSpace)+
		     drX+Math.round
		     ((drX-1)*tranMat[(int)Math.round(Math.floor
						      (k/kMax*(float)
						       (tranSize-1)))]
		      -(curTran/2)),kToScr(k)-(curTran/2), curTran, curTran);
    }

    public void paramUpdate () {

	crystalFrame dummy = (crystalFrame) getTopLevelAncestor();
	dummy.paramTable.setValueAt (new Float(k),0,1);
	dummy.paramTable.setValueAt (new Float(kappa),1,1);
	dummy.paramTable.setValueAt (new Float(a),2,1);
	dummy.paramTable.setValueAt (new Float(tranMat[
						   (int)Math.round(
						       Math.floor(
							   k/kMax*(float)
							   (tranSize-1)
							   ))]),3,1);
//	dummy.paramTable.setValueAt (new(4.0),0,1);
//	dummy.kLab.setText ("k = "+k);
	//dummy.kappaLab.setText ("kappa = "+kappa);
	//dummy.aLab.setText ("a = "+a);
	//dummy.trLab.setText ("T = "+tranMat[(int)Math.round(Math.floor
	//					    (k/kMax*(float)(tranSize-1)))]);
    }

    public void tranCalc () {

        int ks, ns;
	float kt, kk, dela, kcos, ksin;
	
	tranMat = new float[tranSize];
        for (ks=0; ks<tranSize;ks++) {
	    kt=ks*kMax/(tranSize-1);
	    kk=delY[0]/(2f*kt);
	    float [][] M = {{1f,0f,0f,1f},{-1f*kk,-1f*kk,kk,kk}};
	    for (ns=0; ns<(n-1); ns++) {
		kk=delY[ns+1]/(2f*kt);
		dela=delX[ns+1]-delX[ns];
		kcos=(float)Math.cos(kt*dela);
		ksin=(float)Math.sin(kt*dela);
		M=mat2Mul (new float [][] {{kcos,0f,0f,kcos},
					   {ksin,0f,0f,-1f*ksin}}, M);
		M=mat2Mul (new float [][] {{1f,0f,0f,1f},
					   {-1f*kk,-1f*kk,kk,kk}}, M);
	    }
	    float dum = M[0][3]*M[0][3]+M[1][3]*M[1][3];
	    tranMat[ks]=1f/(M[0][3]*M[0][3]+M[1][3]*M[1][3]);
        }
	paramUpdate();
    }

    static final float[][] mat2Mul (float[][] a, float[][] b) {
	return (new float [][]
	    {{a[0][0]*b[0][0]-a[1][0]*b[1][0]+a[0][1]*b[0][2]-a[1][1]*b[1][2],
	      a[0][0]*b[0][1]-a[1][0]*b[1][1]+a[0][1]*b[0][3]-a[1][1]*b[1][3],
	      a[0][2]*b[0][0]-a[1][2]*b[1][0]+a[0][3]*b[0][2]-a[1][3]*b[1][2],
	      a[0][2]*b[0][1]-a[1][2]*b[1][1]+a[0][3]*b[0][3]-a[1][3]*b[1][3]},
	     {a[0][0]*b[1][0]+a[1][0]*b[0][0]+a[0][1]*b[1][2]+a[1][1]*b[0][2],
	      a[0][0]*b[1][1]+a[1][0]*b[0][1]+a[0][1]*b[1][3]+a[1][1]*b[0][3],
	      a[0][2]*b[1][0]+a[1][2]*b[0][0]+a[0][3]*b[1][2]+a[1][3]*b[0][2],
	      a[0][2]*b[1][1]+a[1][2]*b[0][1]+a[0][3]*b[1][3]+a[1][3]*b[0][3]}});
    }

    private boolean inRange (float xx,
                             float yy,
                             float xgoal,
                             float ygoal,
                             float r) {
        boolean val=false;
        if ((Math.pow(xx-xgoal,2) + Math.pow(yy-ygoal,2)) 
            <= Math.pow(r,2)) val=true;
        return (val);
    }

    private float bound (float v, float minn, float maxx) {
	v=Math.min(v,maxx);
	v=Math.max(v,minn);
	return (v);
    }

    public int kToScr(float k) {
	return (bord+drY-1-Math.round((drY-1)*k/kMax));
    }

    public float scrToK(int y) {
	return (((drY-1)+bord-y)/(1.0f*(drY-1))*kMax);
    }

    public int xToScr(float x) {
       	return (bord+fontSpace+Math.round((x-intcpX)/xMax*(drX-1)));
    }

    public float scrToX(int scr) {
	return ((scr-bord-fontSpace)/(1.0f*drX-1f)*xMax+intcpX);
    }

}
