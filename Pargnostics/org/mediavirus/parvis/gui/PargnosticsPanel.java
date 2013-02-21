package org.mediavirus.parvis.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.mediavirus.parvis.gui.analysis.AxisPair;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;
import org.mediavirus.parvis.model.DataSet;

import sun.rmi.transport.proxy.CGIHandler;
/**
 * This is for testing the Scagnostics metrics vis-a-vis our metrics
 * and see which of them closely correspond to ours.
 * @author adasgupt
 *
 */
@SuppressWarnings("serial")
class PargnosticsPanel extends JPanel implements MouseListener,MouseMotionListener {
	
	public static final float RECORD_COLOR_HIGH[] = {251/255f, 106/255f, 74/255f, .9f};

	public static final float RECORD_COLOR_MID[] = {0.874509804f, 0.760784314f, 0.490196078f, .1f};

	public static final float RECORD_COLOR_LOW[] = {49/255f, 130/255f, 189/255f, .9f};

	private static final int PADDING = 2;
	private ParallelDisplay parallelDisplay;
	public static PargnosticsPanel scatterInstance;
	int scatterInstanceHeight;
	int scatterInstanceWidth;
	int axisPairBoxWidth;
	int axisPairBoxHeight;
	int rankedAxis1 = -1 ;
	int rankedAxis2 = -1 ;
	int rankedMouseX ;
	int rankedMouseY ;
	int filterFlag = -1;
	int buttonIndex = 0;
	Metrics metric;
	AxisPairMetrics[][] rankedPairs;
	SortedSet<Integer> axisSet=new TreeSet<Integer>();
	List<Integer> axisList=new ArrayList<Integer>();
	int lastClicked =-1;
	JRadioButton[] viewButton;
	JButton[] filterButton;
	JButton[] sortButton = new JButton[2] ;
	private int maxParallelism = -1;
	private int maxCrossings = -1;
	private Views view= Views.SCATTERPLOT;
	private int numClick=0;
	private boolean ascendingSort=true;
	private boolean inversionFlag= false;

	RenderedImage renderedImg = null;
	BufferedImage bufferImg = null;
	private int numDimensions;
	private int stepx;
	private int padding;
	private int startX  =10;

	public enum Views{
		SCATTERPLOT, PARCOORDS, HISTOGRAM, RANKEDPAIRS
	}


	public PargnosticsPanel(ParallelDisplay parDisp,JPanel hp) {
		super();

		Dimension pargnosticsdDimension= new Dimension(800,800);
		setPreferredSize(pargnosticsdDimension);
		setBackground(Color.WHITE);
		scatterInstance=this;

		parallelDisplay=parDisp;
		createHelperPanel(hp);

		//register listeners

		addMouseListener(this);
		addMouseMotionListener(this);
		repaint();

	}

	//panel to control the output in the matrix.

	public void createHelperPanel(JPanel helperPanel) {

		//JPanel helperPanel=new JPanel();

		helperPanel.setLayout(new BorderLayout());
		Box mainBox = new Box(BoxLayout.Y_AXIS);
		helperPanel.add(mainBox,BorderLayout.CENTER);
		helperPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		Dimension helperPanelDimension =new Dimension(150,800);
		helperPanel.setPreferredSize(helperPanelDimension);

		JLabel viewLabel=new JLabel("Switch views");
		//mainBox.add(Box.createHorizontalStrut(10));
		mainBox.add(Box.createHorizontalGlue());
		mainBox.add(viewLabel);
		RadioButtonHandler radiohandler=new RadioButtonHandler();
		String[] radioButtonNames={"Scatterplot","ParCoords","Histograms","Ranked"};


		viewButton=new JRadioButton[ Views.values().length ];
		sortButton[0] = new JButton("Ascending");
		sortButton[1] = new JButton("Descending");
		for (int i=0;i<sortButton.length; i++)
		{
			sortButton[i].setSize(20, 80);
			sortButton[i].addItemListener(radiohandler);

		}
		ButtonGroup group= new ButtonGroup();

		for(int i=0;i<viewButton.length;i++){

			viewButton[i]=new JRadioButton(radioButtonNames[i]);
			viewButton[i].setSize(20, 80);

			viewButton[i].addItemListener(radiohandler);
			group.add(viewButton[i]);
			mainBox.add(viewButton[i]);
		}

		Box innerBox=new Box(BoxLayout.Y_AXIS);
		innerBox.add(viewButton[3]);
		int sortflag=0;
		for(int i=0;i<sortButton.length;i++)
		{
			sortButton[i].setAlignmentX(SwingConstants.CENTER);
			innerBox.add(sortButton[i]);
			Dimension sortDimension =new Dimension(20,100);
			sortButton[i].setPreferredSize(sortDimension);
			innerBox.add(Box.createVerticalStrut(10));
			sortButton[i].addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					for( int i=0;i<sortButton.length;i++ )
					{
						if( e.getSource()==sortButton[i] )
						{
							if(i == 0)
								ascendingSort= true;
							else
								ascendingSort= false;
							repaint();

						}
					}
				}
			});
		}
		JCheckBox inversionBox = new JCheckBox("Show inversions");
		inversionBox.setSelected( false );
		innerBox.add(inversionBox);
		inversionBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {

				JCheckBox jCheckBox = (JCheckBox)e.getSource();
				if( jCheckBox.isSelected())
					inversionFlag = true;
				else 
					inversionFlag = false;
				repaint();

			}
		});


		innerBox.setBorder(BorderFactory.createLineBorder(Color.black));
		mainBox.add(innerBox);
		mainBox.add(Box.createVerticalStrut(10));

		JButton deleteViewbutton= new JButton("Clear Axes");

		deleteViewbutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				for(int i=0;i<parallelDisplay.getModel().getNumDimensions();i++)
				{
					parallelDisplay.removeAxis(i);
					numClick++;
				}

			}
		});
		mainBox.add(deleteViewbutton);

		mainBox.add(Box.createVerticalStrut(10));

		JLabel filterLabel=new JLabel("Filter by metrics");

		filterButton = new JButton[Metrics.values().length-1];

		mainBox.add(filterLabel);

		for( int i=0; i < Metrics.values().length-1; i++ )
		{


			filterButton[i] = new JButton( Metrics.values()[i].toString());

			mainBox.add(filterButton[i]);

			mainBox.add(Box.createVerticalStrut(10));

			filterButton[i].addActionListener(new ActionListener() {


				public void actionPerformed(ActionEvent e) {


					for(int j=0;j<filterButton.length;j++){

						if(e.getSource() == filterButton[j])
						{

							filterFlag = j;
							setFilterCondition(Metrics.values()[j]);


						}
					}

				}
			});

		}

		viewButton[0].setEnabled(true);

	}

	public void setFilterCondition(Metrics f){

		metric = f;
		repaint();



	}


	public void setDrawFlag(Views v){

		view=v;

	}
	private class RadioButtonHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e){

			for(int i=0;i<viewButton.length-1;i++)
				if(e.getSource()==viewButton[i] && e.getStateChange()==ItemEvent.SELECTED){

					viewButton[i].setEnabled(true);

					setDrawFlag(Views.values()[i]);
					repaint();
				}
			if(e.getSource() == viewButton[3]){

				viewButton[3].setEnabled(true);

				setDrawFlag(Views.values()[3]);
				repaint();



			}

		}
	}


	public void paint(Graphics g) {

		//	PargnosticsPanel comp = (PargnosticsPanel)c;

		super.paint(g);
		Graphics2D ig = (Graphics2D)g;


		Graphics2D g2 = (Graphics2D)g; // we need a Graphics2D context


		int w = this.getWidth();

		int h = this.getHeight();
		
		bufferImg = new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);

		bufferImg = (BufferedImage)(this.createImage(w,h));

		ig = bufferImg.createGraphics();
		ig.setColor(this.getBackground());
		ig.fillRect(0, 0, this.getWidth(), this.getHeight());


		//	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		padding = PADDING;
		DataSet data = parallelDisplay.getModel();

		scatterInstanceHeight = getHeight() /data.getNumDimensions()-padding;
		scatterInstanceWidth  = getWidth()/ data.getNumDimensions()-padding;
		//scatterInstanceWidth = scatterInstanceHeight = Math.max(scatterInstanceHeight, scatterInstanceWidth);



		//		width = c.getWidth() - 2 * borderH;
		//		height = c.getHeight() - 2 * borderV;
		//
		numDimensions = parallelDisplay.getNumAxes();
		//		numRecords = comp.getNumRecords();
		if (numDimensions!=1)
			stepx = scatterInstanceWidth / (numDimensions - 1);





		if(data != null) {

			//Calculating the global maximum for scaling the histograms

			if( maxParallelism == -1 && view == Views.HISTOGRAM )
			{
				for(int row = 0; row < data.getNumDimensions(); row++) {

					for(int col = 0; col < data.getNumDimensions(); col++) {
						if (row != col) {
							int parallelismData[] = data.getAxisPair(col, row, parallelDisplay).getDistanceHistogram(scatterInstanceHeight/2, col > row);

							for(int f : parallelismData)
								if(f > maxParallelism )
									maxParallelism = f;

							int crossingAnglesData[] = data.getAxisPair(col, row, parallelDisplay).getAngleOfCrossingHistogram(scatterInstanceHeight, col > row);

							for(int k : crossingAnglesData)
								if(k > maxCrossings )
									maxCrossings = k;
						}
					}
				} 
			}

			if(view!= Views.RANKEDPAIRS)
			{

				for(int row=0;row<data.getNumDimensions();row++) {

					for(int col = 0; col <data.getNumDimensions(); col++) {

						//g.drawLine(startX+(scatterInstanceWidth*(col)),scatterInstanceHeight*row,startX+(scatterInstanceWidth*(col+1)),scatterInstanceHeight*row);
						//g.drawLine(startX+(scatterInstanceWidth*(col)),scatterInstanceHeight*row, startX+(scatterInstanceWidth*(col)), scatterInstanceHeight*(row+1));
						ig.setColor(Color.lightGray);

						ig.drawRect(startX+(scatterInstanceWidth*(col)+(padding*col)), scatterInstanceHeight*(row)+(padding*row), scatterInstanceWidth , scatterInstanceHeight );


						if (row == col) {
							//draw the labels

							ig.setColor(Color.BLACK);

							//String text1=  "  Axis1   "  + parallelDisplay.getModel().getAxisLabel(rankedAxis1)  +  "   Axis2  " + parallelDisplay.getModel().getAxisLabel(rankedAxis2)+ " " ;

							String text1= data.getAxisLabel(row);
							FontMetrics fm= ig.getFontMetrics();
							int strWidth= fm.stringWidth(text1);
							int ascent= fm.getMaxAscent();
							int descent= fm.getMaxDescent();
							int rectHeight = scatterInstanceHeight;
							int rectWidth  = scatterInstanceWidth;
							int str_y= rectHeight/2-descent/2+ascent/2;
							int str_x= rectWidth/2-strWidth/2;


							ig.drawString(text1, startX+(padding*col)+(scatterInstanceWidth*(col))+str_x, (padding*col)+(scatterInstanceHeight*(col)+str_y));
							//g.drawRect(startX+(scatterInstanceWidth*(col)),(scatterInstanceHeight*(col)), scatterInstanceWidth, scatterInstanceHeight);
							//g2d.drawString(label,startX+(scatterInstanceWidth*(col))+50,startX+(scatterInstanceHeight*(col))+50);
							//g.fillRect(arg0, arg1, arg2, arg3)
						} 
						else {

							if(view==Views.HISTOGRAM)
								ig.drawLine(startX+(scatterInstanceWidth*(col)+(padding*col))+scatterInstanceWidth/2, scatterInstanceHeight*(row)+( padding*row ), startX+(scatterInstanceWidth*(col) + (padding*col) )+scatterInstanceWidth/2, scatterInstanceHeight*(row+1)+ (padding*(row)));

							switch(view) {

							case SCATTERPLOT: 
								int locsX=startX+(scatterInstanceWidth*(col))+(padding*col);
								int locsY=scatterInstanceHeight*row + (padding*row);
								//								if(row>col)
								//								drawScatterplot(g2d, data, scatterInstanceHeight, scatterInstanceWidth, startX, locsX, locsY,row, col);
								//								if(col>row)
								drawScatterplot(ig, data, scatterInstanceHeight-1, scatterInstanceWidth-1, startX, locsX, locsY,col, row);
								break;

							case PARCOORDS: 
								int locX= startX+(scatterInstanceWidth*(col))+(padding*col);
								int locY= scatterInstanceHeight*row +(padding*row);
								if(col<row)
								{
									inversionFlag = false;
									drawParallelCoordinatesplot(ig, data, scatterInstanceHeight, scatterInstanceWidth, locX, locY, row, col, inversionFlag);
								}
								if(col>row)
								{
									inversionFlag = true;
									drawParallelCoordinatesplot(ig, data, scatterInstanceHeight, scatterInstanceWidth, locX, locY, col, row, inversionFlag);
								}
								break;

							case HISTOGRAM: drawHistograms(ig,data,scatterInstanceHeight,scatterInstanceWidth,startX,col, row);
							break;
							}
						}
					}
				}
			}
			else
				if(view==Views.RANKEDPAIRS)
				{

					List<List<AxisPairMetrics>> metricsByAxisPairsList=new ArrayList<List<AxisPairMetrics>>();

					for(int i=0;i<Metrics.values().length;i++)
					{
						// get the ranked axis pairs for all the metrics
						List<AxisPairMetrics> sortedList = getRankedAxispairs(data, Metrics.values()[i], ascendingSort, inversionFlag);



						metricsByAxisPairsList.add(sortedList);
					}
					// draw the axis pairs on screen column by column
					drawRankedAxisPairs( ig,data,startX,metricsByAxisPairsList, inversionFlag);
					if( rankedAxis1!=-1 )
					{	 

						String text1=  "  Axis1   "  + parallelDisplay.getModel().getAxisLabel(rankedAxis1)  +  "   Axis2  " + parallelDisplay.getModel().getAxisLabel(rankedAxis2)+ " " ;
						FontMetrics fm=g.getFontMetrics();
						int strWidth=fm.stringWidth(text1);
						int ascent=fm.getMaxAscent();
						int descent=fm.getMaxDescent();
						int rectHeight = 50;
						int rectWidth  = 200;
						int rectX =  rankedMouseX+1;
						int rectY =  rankedMouseY+1;
						g.setColor(Color.BLUE);
						g.fillRect(rectX, rectY, rectWidth, rectHeight);
						g.setColor(Color.WHITE);
						int str_y=rectHeight/2-descent/2+ascent/2;
						int str_x=rectWidth/2-strWidth/2;
						ig.drawString(text1, rectX+str_x, rectY+str_y);
					}

				}
		}

		
		g2.drawImage(bufferImg, null, 0, 0);
		
	    try {
		        // retrieve image
		        
		        File outputfile = new File("saved.png");
		        ImageIO.write(bufferImg, "png", outputfile);
		    } catch (IOException e) {
		        
		    }

	}

	//	
	//	public static BufferedImage componentToImage(PargnosticsPanel component) 
	//	{
	//	    BufferedImage bufferedImage = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
	//	    Image image = component.createImage(component.getWidth(), component.getHeight());
	//	    Graphics g = image.getGraphics();
	////	    g.setColor(component.getForeground());
	////	    g.setFont(component.getFont());
	//	    bufferedImage.getGraphics().drawImage(image, 0 , 0, null);
	//	    
	//	    try {
	//	        // retrieve image
	//	        
	//	        File outputfile = new File("saved.png");
	//	        ImageIO.write(bufferedImage, "png", outputfile);
	//	    } catch (IOException e) {
	//	        
	//	    }
	//	   
	//	    return bufferedImage;
	//	}

	// for rendering the scatterplot matrix
	private void drawScatterplot(Graphics g2d, DataSet data, int scatterInstanceHeight, int scatterInstanceWidth, int startX,int locX, int locY,int row, int col) {

		//float axisOffset1 = parallelDisplay.getAxisOffset(col);
		//float axisOffset2 = parallelDisplay.getAxisOffset(row);
		//float scale1 = parallelDisplay.getAxisScale(col);
		//float scale2 = parallelDisplay.getAxisScale(row);


		if (lastClicked == row || lastClicked == col) {

			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);

			System.err.println("Yes clicked");

		}



		if(filterFlag !=-1)
		{
			float val = getFilteredValue(col, row);

			//Color code backgorund for Crossings:Low medium and high
			Color backGroundColor = getColor(val);


			g2d.setColor(backGroundColor);
			g2d.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);

		}
		float scale1 = (data.getMaxValue(col) - data.getMinValue(col));
		float scale2 = (data.getMaxValue(row) - data.getMinValue(row));
		float axisOffset1 = data.getMinValue(col);
		float axisOffset2 = data.getMinValue(row);
		Color lineColor =new Color(0,0,0,100);
		g2d.setColor(lineColor);

		/*  Check Inversion for valid output*/
		for (float[] dataRow : data) {

			int v1 = (int)((dataRow[col] - axisOffset1) * (scatterInstanceHeight) / scale1);
			int v2 = (int)((dataRow[row] - axisOffset2) * (scatterInstanceHeight) / scale2);
			g2d.setColor(getRecordColor(v1, v2, scatterInstanceHeight));
			g2d.drawOval(locX+v1, locY+v2,2,2);
			//g2d.drawLine(locX+v1, locY+v2, locX+v1+2, locY+v2);

		}
		/* Inversion code

		for (float[] dataRow : data) {
			int v1 = (int)((dataRow[col] - axisOffset1) * (scatterInstanceHeight) / scale1);
			int v2 = (int)((data.getMaxValue(row)-dataRow[row]) * (scatterInstanceHeight) / (scale2));
			g2d.drawLine(startX+(scatterInstanceWidth*(row))+v1, scatterInstanceHeight*col+v2, startX+(scatterInstanceWidth*(row))+v1+1, scatterInstanceHeight*col+v2);
		}

		 **/

	}

	
	 protected Color getRecordColor(float point1, float point2, int numBins){

			


			float norm = (float)((point1/(float)numBins));
			float mult[] = {RECORD_COLOR_HIGH[0] * norm + RECORD_COLOR_LOW[0]*(1-norm), RECORD_COLOR_HIGH[1] * norm + RECORD_COLOR_LOW[1]*(1-norm), 
					RECORD_COLOR_HIGH[2] * norm + RECORD_COLOR_LOW[2]*(1-norm), 0.2f};

         Color color = new Color(mult[0], mult[1], mult[2]);

			return color;

		}

	// for rendering parallel coordinates matrix

	private void drawParallelCoordinatesplot(Graphics g2d,DataSet data, int scatterInstanceHeight, int scatterInstanceWidth, int locX, int locY, int axis1, int axis2, boolean inversionFlag){

		//float axisOffset1 = parallelDisplay.getAxisOffset(axis1);
		//float axisOffset2 = parallelDisplay.getAxisOffset(axis2);
		//float scale1 = parallelDisplay.getAxisScale(axis1);
		//float scale2 = parallelDisplay.getAxisScale(axis2);

		float scale1 = (data.getMaxValue(axis1) - data.getMinValue(axis1));
		float scale2 = (data.getMaxValue(axis2) - data.getMinValue(axis2));
		float axisOffset1 = data.getMinValue(axis1);
		float axisOffset2 = data.getMinValue(axis2);

		//Color code background for Crossings:Low medium and high
		if(filterFlag !=-1)
		{
			float val = getFilteredValue( axis1, axis2 );

			//Color code backgorund for Crossings:Low medium and high
			Color backGroundColor = getColor(val);


			g2d.setColor(backGroundColor);
			g2d.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);

		}

		//alpha
		Color lineColor=new Color(0,0,0,10);
		g2d.setColor(lineColor);


		if(inversionFlag == false)
		{
			for(float[]dataRow : data){

				int v1 = (int)((dataRow[axis1] - axisOffset1) * (scatterInstanceHeight) / scale1);
				int v2 = (int)((dataRow[axis2] - axisOffset2) * (scatterInstanceHeight) / scale2);

				g2d.drawLine(locX, locY+v1, locX+scatterInstanceWidth, locY+v2);	
			}

		} else {
			/* Inversion code for parcoords*/
			for(float[]dataRow : data){

				int v1 = (int)((dataRow[axis1] - axisOffset1) * (scatterInstanceHeight) / scale1);
				int v2 = (int)((data.getMaxValue(axis2)-dataRow[axis2]) * (scatterInstanceHeight) / scale2);

				g2d.drawLine(locX, locY+v1, locX+scatterInstanceWidth, locY+v2);	
			}

		}

	}

	// for rendering the histograms matrix

	private void drawHistograms(Graphics g2d,DataSet data, int scatterInstanceHeight, int scatterInstanceWidth, int startX, int row, int col){

		drawParallelismHist(g2d,data,scatterInstanceHeight,scatterInstanceWidth, startX, row, col);
		drawCrossingAnglesHist(g2d,data,scatterInstanceHeight,scatterInstanceWidth, startX, row, col);
	}


	// angles of crossing histograms

	private void drawCrossingAnglesHist(Graphics g2d, DataSet data,
			int scatterInstanceHeight, int scatterInstanceWidth, int startX,
			int row, int col) {

		//angles of crossing is based on the number of bins in the original view
		int crossingAngles[]= data.getAxisPair(col, row, parallelDisplay).getAngleOfCrossingHistogram(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV(), row > col);

		//g2d.setColor(new Color(150, 150, 150));

		g2d.setColor(Color.black);

		//System.err.println("Max Crossing  " + maxCrossing);
		int h = crossingAngles.length;
		for (int y = 0; y < crossingAngles.length; y++){
			//g.fillRect(0, (h - y) * 3 + borderV, (int)(data[y] / max * w), 3);
			int linelen = (int)(((float)crossingAngles[y] / maxCrossings) * (scatterInstanceWidth/2));
			g2d.fillRect(startX+(scatterInstanceWidth*(col)+(PADDING*col))+scatterInstanceWidth/2, scatterInstanceHeight*row+(PADDING*row)+(y) * 2 , linelen, 1);
			//	g2d.drawLine(startX+(scatterInstanceWidth*(col)+(PADDING*col))+scatterInstanceWidth/2, scatterInstanceHeight*row+(PADDING*row)+y, linelen+startX+(scatterInstanceWidth*(col))+(PADDING*col)+scatterInstanceWidth/2, scatterInstanceHeight*row+(PADDING*row)+y);

		}
	}

	//parallelism histograms

	private void drawParallelismHist(Graphics g2d,DataSet data, int scatterInstanceHeight, int scatterInstanceWidth, int startX, int row, int col){

		int parallelism[] = data.getAxisPair(Math.min(col, row), Math.max(col, row), parallelDisplay).getDistanceHistogram(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV(), row > col);

		int subSampleHeight= (int)(float)scatterInstanceHeight/parallelDisplay.getHeight()-2*parallelDisplay.getBorderV();
		if(filterFlag !=-1)
		{

			float val = getFilteredValue(col, row);
			Color backGroundColor = getColor(val);
			g2d.setColor(backGroundColor);
			g2d.fillRect( startX+(scatterInstanceWidth*(col)+(PADDING*col)),scatterInstanceHeight*row + (PADDING* row), scatterInstanceWidth, scatterInstanceHeight);

		}
		//g2d.setColor(new Color(100, 100, 100));
		g2d.setColor(Color.black);
		//	System.err.println("Max parallelism  " + maxParallelism);
		int h = parallelism.length;
		for (int i = 0; i < parallelism.length; i++) {
			int linelen = (int) (((float)parallelism[i] / maxParallelism) * (scatterInstanceWidth/2));
			g2d.drawLine(startX+(scatterInstanceWidth*(col)+(PADDING*col)),scatterInstanceHeight*row+((i)) +(PADDING*row), startX+(scatterInstanceWidth*(col))+ +(PADDING*col)+linelen,(scatterInstanceHeight*row+(PADDING*row)+((i))));
		}


	}

	// ranking view

	private List<AxisPairMetrics> getRankedAxispairs(DataSet data, Metrics metric, boolean ascending, boolean inversionFlag){

		List<AxisPairMetrics> metricsList=new ArrayList<AxisPairMetrics>();
		float val=0;
		float invertedVal=0;
		for(int row = 0; row < data.getNumDimensions(); row++) {
			for(int col = row+1; col < data.getNumDimensions(); col++) {


				if (metric == Metrics.PixelBasedEntropy)

					val = 0;
				else
					if(inversionFlag == false)
					{
						val = data.getAxisPair(row, col, parallelDisplay).getMetric(metric, parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
						AxisPairMetrics metricByAxisPairNonInverted = new AxisPairMetrics(row,col,val,inversionFlag);
						metricsList.add(metricByAxisPairNonInverted); 
					}
					else if( inversionFlag == true )
					{	
						val = data.getAxisPair(row, col, parallelDisplay).getMetric(metric, parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
						invertedVal = data.getAxisPair(row, col, parallelDisplay).getMetric(metric, parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getInvertedValue();

						AxisPairMetrics metricByAxisPairNonInverted = new AxisPairMetrics(row,col,val,false);
						AxisPairMetrics metricByAxisPairInverted = new AxisPairMetrics(row,col,invertedVal,true);
						metricsList.add(metricByAxisPairNonInverted);
						metricsList.add(metricByAxisPairInverted );
					}


			}
		}

		List<AxisPairMetrics> sortedList=getSortedList(metric,metricsList, ascending);
		return sortedList;

	}

	//draw ranked axis pairs

	private void drawRankedAxisPairs(Graphics g2d, DataSet data, int startX,List<List<AxisPairMetrics>> metricsByAxisPairsList, boolean inversionFlag ){

		//Number of metrics= screenCol
		int screenCol = Metrics.values().length-1;

		//Number of axispairs= screenRow
		int screenRow = 10;
		int padding=  PADDING;

		int labelHeight=40;
		//g2d.setColor(Color.black);

		rankedPairs = new AxisPairMetrics[screenCol][screenRow];
		axisPairBoxWidth  =  getWidth()/screenCol-padding;
		axisPairBoxHeight =  (getHeight()-labelHeight)/(screenRow)-padding;

		for( int i=0; i<screenCol; i++ ){

			//draw labels for the metrics

			//			if( ascendingSort == false) {
			//			screenRow = metricsByAxisPairsList.get(i).size();
			//			if (screenRow > 10)
			//			screenRow = 10;
			//			}

			g2d.setColor( Color.GRAY );
			g2d.fillRect( startX+i*(axisPairBoxWidth+padding),0,axisPairBoxWidth, labelHeight );
			g2d.setColor( Color.white );
			g2d.drawString( Metrics.values()[i].toString(), startX+i*(axisPairBoxWidth+padding)+20, labelHeight/2 );

			System.err.println(" Metrc  "  + Metrics.values()[i].toString());


			for( int j=0; j<screenRow; j++ ){

				int axis1= metricsByAxisPairsList.get(i).get(j).axis1;
				int axis2= metricsByAxisPairsList.get(i).get(j).axis2;
				float val = metricsByAxisPairsList.get(i).get(j).metricValue;
				int locX = startX+(axisPairBoxWidth*(i))+(padding*i);
				int locY = labelHeight+axisPairBoxHeight*j+(padding*j);

				System.err.println("    "  + data.getAxisLabel(axis1) +"   " + data.getAxisLabel(axis2) + "  " + val   );


				//g2d.drawRect( startX+((boxWidth+padding)*( i )), boxHeight*j, boxWidth, boxHeight );
				//  if(inversionFlag == false)
				drawParallelCoordinatesplot( g2d, data,axisPairBoxHeight, axisPairBoxWidth,locX,locY, axis1, axis2,inversionFlag );
				//  else
				//	drawParallelCoordinatesplot( g2d, data,axisPairBoxHeight, axisPairBoxWidth,locX,locY, axis1, axis2, inversionFlag);
				rankedPairs[i][j]= new AxisPairMetrics(axis1, axis2);
				//System.err.println("1st Axis  "  + axis1   + "   2nd axis  "  +axis2);
			}
		}

	}

	private List<AxisPairMetrics> getSortedList(Metrics metric,List<AxisPairMetrics> metricsList, boolean ascending){

		Collections.sort(metricsList, new SortMetrics(ascending));

		//		if(ascending ==false)
		//		{
		//		float cutOff = metric.getCutOff(parallelDisplay.getModel());
		//		int i = 0;
		//		while ( metricsList.get(i).metricValue > cutOff && i<10)
		//		i++;

		//		return metricsList.subList(0, i);
		//		}
		return metricsList;
	}

	// storage class for sorting

	private class AxisPairMetrics{

		int axis1;
		int axis2;
		boolean inversion=false;
		float metricValue;

		public void setVal(int col,int row,float val){

			axis1 = row;
			axis2 = col;
			metricValue = val;

		}

		public AxisPairMetrics(){


		}


		public AxisPairMetrics(int row, int col,float val, boolean inv){

			axis1=row;
			axis2=col;
			metricValue=val;
			inversion =inv;


		}
		public AxisPairMetrics(int a1, int a2){

			axis1 = a1;
			axis2 = a2;

		}
		public float getVal(){

			return metricValue;
		}

	}

	private class SortMetrics implements Comparator<AxisPairMetrics>{

		boolean ascending;

		public SortMetrics(boolean asc){

			ascending = asc;

		}


		public int compare(AxisPairMetrics a1, AxisPairMetrics a2) {

			float v1=a1.getVal();
			float v2=a2.getVal();
			if(ascending == true)
			{
				if(v1>v2)
					return 1;
				else if(v1<v2)
					return -1;
				else
					return 0;
			}
			else
			{
				if(v1<v2)
					return 1;
				else if(v1>v2)
					return -1;
				else
					return 0;

			}

		}
	}

	public float getFilteredValue(int col, int row){

		float metricVal=0;

//		switch(metric){
//
//		case NumCrossings: 
//			int numCrossings = (int)parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getNumCrossings(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
//			metricVal =(float)(numCrossings/Metrics.NumCrossings.getRange(parallelDisplay.getModel()));
//			break;
//		case AngleOfCrossings: 
//			int anglesOfCrossing = (int)parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getAngleOfCrossingMedian(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
//			metricVal =(float)(anglesOfCrossing/Metrics.AngleOfCrossings.getRange(parallelDisplay.getModel()));
//			break;
//
//		case Parallelism:
//			if (row > col)
//				metricVal = parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getParallelism(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getInvertedValue();
//			else
//				metricVal = parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getParallelism(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
//			break;
//
//		case MutualInformation:
//			int mutualInfo = (int)parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getMutualInfo(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
//			//metricVal =(float)(mutualInfo/Metrics.MutualInformation.getRange(parallelDisplay.getModel()));
//			metricVal =(float)(mutualInfo/5);
//			break;
//
//		case Convergence_Divergence:
//			int convdiv = (int)parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getConvergence_Divergence(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV(),4).getValue();
//			metricVal =(float)convdiv/Metrics.Convergence_Divergence.getRange(parallelDisplay.getModel());
//			break;
//		case Overplotting:	
//			int overplotting = (int)parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getMedianOverPlotting(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV());
//			metricVal =(float)overplotting/Metrics.Overplotting.getRange(parallelDisplay.getModel());
//			break;
//
//
//		}

		return metricVal;

	}

	public void mouseClicked(MouseEvent e) {


		int mousex = e.getX()-10;
		int mousey = e.getY()-10;

		if(view!=Views.RANKEDPAIRS)
		{

			int axis1 = mousex/scatterInstanceWidth;
			int axis2 = mousey/scatterInstanceHeight;
			drawClickedAxes(axis1, axis2);

		}

		if(view == Views.RANKEDPAIRS)
		{


			int axis1position = mousex/axisPairBoxWidth;
			int axis2position = (mousey-40)/axisPairBoxHeight;

			int axis1 = rankedPairs[ axis1position ][axis2position ].axis1;	
			int axis2 = rankedPairs[ axis1position ][axis2position ].axis2;

			System.err.println(" Axis 1  " + parallelDisplay.getModel().getAxisLabel(axis1));
			System.err.println(" Axis 2  " + parallelDisplay.getModel().getAxisLabel(axis2));
			drawClickedAxes(axis1, axis2);

		}



		//		rankedMouseX = e.getX()-10;
		//		rankedMouseY = e.getY()-10;

		//		if(view == Views.RANKEDPAIRS  && rankedMouseX < scatterInstance.getWidth() && rankedMouseY< scatterInstance.getHeight())

		//		{

		//		int axis1position = rankedMouseX / (axisPairBoxWidth+PADDING);
		//		int axis2position = ( rankedMouseY-40 )/(axisPairBoxHeight+PADDING);


		//		rankedAxis1 = rankedPairs[ axis1position ][axis2position ].axis1;	
		//		rankedAxis2 = rankedPairs[ axis1position ][axis2position ].axis2;


		//		repaint();



		//		}

	}



	public void mouseMoved(MouseEvent e) {

		int mousex = e.getX()-10;
		int mousey = e.getY()-10;


		int axis1 = mousex/scatterInstanceWidth;
		int axis2 = mousey/scatterInstanceHeight;

		double entropy = computeEntropy(axis1, axis2);


		System.err.println("Entropy   ********* " + entropy);

		//		if(view == Views.RANKEDPAIRS  && rankedMouseX < scatterInstance.getWidth() && rankedMouseY< scatterInstance.getHeight())

		//		{

		//		int axis1position = rankedMouseX / (axisPairBoxWidth+PADDING);
		//		int axis2position = ( rankedMouseY-40 )/(axisPairBoxHeight+PADDING);


		//		rankedAxis1 = rankedPairs[ axis1position ][axis2position ].axis1;	
		//		rankedAxis2 = rankedPairs[ axis1position ][axis2position ].axis2;


		//		//	repaint();



		//		}

	}
	public void drawClickedAxes(int axis1, int axis2){



		if (lastClicked!=-1 && lastClicked == axis1) {
			axisList.add(axis2);
		}

		if (lastClicked != axis1) {
			axisList.add(axis1);
			axisList.add(axis2);
		}

		lastClicked = axisList.get(axisList.size()-1);
		repaint();
		parallelDisplay.addAxesToDraw(axisList);
	}

	public Color getColor(float val){

		Color backGroundColor = new Color(0,0,0,100);
		if (val <= 0.2)
			backGroundColor = new Color(254, 224, 210);
		else if (val <= 0.5)
			backGroundColor = new Color(252, 146, 114 );
		else
			backGroundColor = new Color( 222, 45, 38);


		return backGroundColor;
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	int[] computeImageHistogram(int dim1, int dim2) {

		// System.out.println("Testing"+ bufferImg.getHeight());
		//		int imageWidth = bufferImg.getWidth();
		int imageHeight = bufferImg.getHeight();
		double sumOfColorVal = 0;
		//		int numPixels = imageHeight * imageWidth;
		int sizeOfArray = 256;
		int colorHistogramArray[] = new int[sizeOfArray];


		// calculate the sum of all the pixel values for each pixel
		/*
		 * for(int x=0;x<imageWidth;x++){ for(int y=0;y<imageHeight;y++){
		 * 
		 * int colorVal=(bufferImg.getRGB(x, y)&0xFF);
		 * colorValArray[colorVal]++;
		 * 
		 * sumOfColorVal=sumOfColorVal+colorValArray[colorVal];
		 * //System.out.println("Testing"+ sumOfColorVal);
		 * 
		 * }
		 * 
		 * }
		 */

		//int upperBoundaryOfRegion = (currentDimension + 1) * stepx;
		//int lowerBoundaryOfRegion = currentDimension * stepx;

		//		for(int row=0;row<numDimensions;row++) {
		//
		//			for(int col = 0; col <numDimensions; col++) {

		int row = dim2;
		int col = dim1;

		int xCoord = startX+(scatterInstanceWidth*(col)) +(padding*col);				
		int yCoord = startX + (scatterInstanceHeight*(row))+(padding*row);

		System.err.println("Xcoord " + xCoord + " Ycoord  " + yCoord);


		//BufferedImage bufferImg = componentToImage(this);

		for (int x = xCoord; x < (xCoord +scatterInstanceWidth); x++) {
	
			for (int y = yCoord; y < (yCoord + scatterInstanceHeight) ; y++) {

				//int colorVal = (bufferImg.getRGB(x, y) & (0xFF));
				
				int Rval = bufferImg.getRGB(x, y)<<16;
				int Gval = bufferImg.getRGB(x, y)<<8;
				int Bval = bufferImg.getRGB(x, y);
				
				int colorVal = (Rval | Gval | Bval)& (0xFF);
				
				colorHistogramArray[colorVal]++;
//				if(colorVal!=0)
//					System.err.println("Color val " +colorVal);

				sumOfColorVal = sumOfColorVal + colorHistogramArray[colorVal];					


			}



		}
		//			g2d.drawRect(startX+(scatterInstanceWidth*(col)+(padding*col)), scatterInstanceHeight*(row)+(padding*row), scatterInstanceWidth , scatterInstanceHeight );



		// Once the colorHistogram is computed for a region, compute the entropy
		//computeEntropy(numPixels);
		return colorHistogramArray;
	}

	public double computeEntropy(int dim1, int dim2) {

		int colorHistogramArray[] = computeImageHistogram(dim1, dim2);

		//double numPixels = (bufferImg.getWidth()/(numDimensions-1))*bufferImg.getHeight();

		double numPixels =bufferImg.getWidth()*bufferImg.getHeight();

		double sumOfEntropy = 0;
		double probabilityValue = 0;
		double logprobabilityValue = 0;
		for (int i = 0; i < colorHistogramArray.length; i++) {
			probabilityValue = colorHistogramArray[i] / numPixels;

			if (colorHistogramArray[i] > 0)
				logprobabilityValue = (Math.log(numPixels
						/ (colorHistogramArray[i])) / AxisPair.LOG_BASE_2);

			double entropy = (probabilityValue * logprobabilityValue);

			sumOfEntropy = sumOfEntropy + entropy;


		}
		return sumOfEntropy;

	}



}