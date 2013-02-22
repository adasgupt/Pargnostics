package org.mediavirus.parvis.gui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;

import org.mediavirus.parvis.gui.ParameterizedDisplay.AxisPairMetrics;
import org.mediavirus.parvis.gui.PargnosticsPanel.Views;
import org.mediavirus.parvis.model.DataSet;

public class MatrixMetaView extends JPanel implements MouseListener, MouseMotionListener{

	/*
	 * drawing parameters
	 */
	private int scatterInstanceHeight;
	private int scatterInstanceWidth;
	private int startX;
	private int stepx;
	private int padding;

	/*
	 * For linking with data view and keeping track of what the user selects
	 */
	List<Integer> currentAxisList =new ArrayList<Integer>();
	/*
	 * List of all axis pair objects
	 */

	ArrayList<ParameterizedDisplay.AxisPairMetrics> metricsList;
	/*
	 * List of axis pairs that are suggested by the system
	 */
	ArrayList<AxisPairMetrics> suggestedAxisPairList = new ArrayList<AxisPairMetrics>();

	public enum MetaMetrics{

		JointEntropy, ImageEntropy, SumofJointImageEntropy, GrayEntropy, ColorEntropy,

	}

	int lastClicked =-1;

	/*
	 * connect with other classes
	 */
	ParallelDisplay parallelDisplay;
	ParameterizedDisplay parameterizedDisplay;
	DataSet data;
	private int numDimensions;

	public MatrixMetaView(){

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	public void initialize(DataSet data, ParallelDisplay parallelDisplay, ParameterizedDisplay parameterizedDisplay){

		this.parallelDisplay = parallelDisplay;
		this.data = data;
		this.parameterizedDisplay = parameterizedDisplay;


	}

	public void paint(Graphics g) {

		//	PargnosticsPanel comp = (PargnosticsPanel)c;

		super.paint(g);
		Graphics2D ig = (Graphics2D)g;


		Graphics2D g2 = (Graphics2D)g; // we need a Graphics2D context


		int w = this.getWidth();

		int h = this.getHeight();




		//scatterInstanceWidth = scatterInstanceHeight = Math.max(scatterInstanceHeight, scatterInstanceWidth);



		//		width = c.getWidth() - 2 * borderH;
		//		height = c.getHeight() - 2 * borderV;
		//

		//		numRecords = comp.getNumRecords();
		if (numDimensions!=1)
			stepx = scatterInstanceWidth / (numDimensions - 1);




		if(data != null) {

			scatterInstanceHeight = getHeight() /data.getNumDimensions()-padding;
			scatterInstanceWidth  = getWidth()/ data.getNumDimensions()-padding;
			numDimensions = parallelDisplay.getNumAxes();
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

					int locX=startX+(scatterInstanceWidth*(col))+(padding*col);
					int locY=scatterInstanceHeight*row + (padding*row);
					//								if(row>col)
					//								drawScatterplot(g2d, data, scatterInstanceHeight, scatterInstanceWidth, startX, locsX, locsY,row, col);
					//		


					if(row>col)
					{
						Graphics2D g3 = getMetricColor(ig, row, col, 0);
						g3.fillRect(startX+(scatterInstanceWidth*(col)+(padding*col)), scatterInstanceHeight*(row)+(padding*row), scatterInstanceWidth-2 , scatterInstanceHeight-2);



					}
					if(col>row)
					{
						Graphics2D g3 = getMetricColor(ig, col, row, 1);
						g3.fillRect(startX+(scatterInstanceWidth*(col)+(padding*col)), scatterInstanceHeight*(row)+(padding*row), scatterInstanceWidth-2 , scatterInstanceHeight-2);



					}

					if (lastClicked == row || lastClicked == col) {

						ig.setColor(new Color(160, 40, 30, 100));
						ig.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);

						System.err.println("Yes clicked");

					}
				}
			}
		}


	}


	/**
	 * get sorted list and set up colors for painting
	 *
	 */
	private Graphics2D getMetricColor(Graphics2D g2, int row, int col, int mode) {

		metricsList = parameterizedDisplay.getMetricsList();

		//		for(AxisPairMetrics am: parameterizedDisplay.getMetricsList())
		//			System.err.println("testing ******** " + am.getDistanceEntropy());
		int firstQuartile = metricsList.size()/4;
		int thirdQuartile= (3*metricsList.size())/4;

		if(mode==0)
		{
			Color[] colorPattern0 = {new Color(203, 201, 226), new Color(158, 154, 200), new Color(106, 81, 163)};

			Collections.sort(metricsList, new SortMetrics(MetaMetrics.JointEntropy));
			for(int index=0; index<metricsList.size(); index++){
				AxisPairMetrics am = metricsList.get(index);
				if(am.getDimension1()==row & am.getDimension2()==col)
				{
					if(index<=firstQuartile)
						g2.setColor(colorPattern0[0]);
					else if(index>firstQuartile && index <= thirdQuartile)
						g2.setColor(colorPattern0[1]);
					else if(index>thirdQuartile)
						g2.setColor(colorPattern0[2]);
				}
			}
		}
		else if(mode==1)
		{

			Color[] colorPattern1 = {new Color(186, 228, 179), new Color(116, 196, 118), new Color(35, 139, 69)};
			Collections.sort(metricsList, new SortMetrics(MetaMetrics.ImageEntropy));
			for(int index=0; index<metricsList.size(); index++){
				AxisPairMetrics am = metricsList.get(index);
				if(am.getDimension1()==row & am.getDimension2()==col)
				{
					if(index<=firstQuartile)
						g2.setColor(colorPattern1[0]);
					else if(index>firstQuartile && index <= thirdQuartile)
						g2.setColor(colorPattern1[1]);
					else if(index>thirdQuartile)
						g2.setColor(colorPattern1[2]);
				}
			}


		}

		return g2;

	}


	public static class SortMetrics implements Comparator<AxisPairMetrics>{

		private String metricName;
		/*
		 * left part of matrix denoted by 0 and the right part as 1
		 */
		private int sortMode;
		private MetaMetrics metric;


		public SortMetrics(MetaMetrics metric){

			this.metric = metric;

		}
		@Override
		public int compare(AxisPairMetrics m1, AxisPairMetrics m2) {

			float entropy1 = 0;
			float entropy2 = 0;

			if(metric==MetaMetrics.JointEntropy)

			{
				System.err.println("Metric joint entropy");
				entropy1 = m1.getJointEntropy();
				entropy2 = m2.getJointEntropy();	

			}
			else if(metric == MetaMetrics.ImageEntropy)
			{

				System.err.println("Metric distance entropy");
				entropy1 = m1.getDistanceEntropy();
				entropy2 = m2.getDistanceEntropy();

			}

			System.err.println("Compared " + entropy1 + " "+ entropy2);

			if(entropy1 < entropy2)
				return 1;
			else if(entropy1 > entropy2)
				return -1;
			else
				return 0;

		}
	}

	public void drawClickedAxes(int axis1, int axis2){


		if (lastClicked!=-1 && lastClicked == axis1) {
			currentAxisList .add(axis2);
		}

		if (lastClicked != axis1) {
			currentAxisList .add(axis1);
			currentAxisList .add(axis2);
		}

		lastClicked = currentAxisList.get(currentAxisList .size()-1);
		repaint();
		parallelDisplay.addAxesToDraw(currentAxisList );
	}


	public void suggestAxisPairs(MetaMetrics selectedMetric){

		Collections.sort(metricsList, new SortMetrics(selectedMetric));

		for(AxisPairMetrics am: metricsList)
		{

			int dim1 = am.getDimension1();
			int dim2 = am.getDimension2();

			// filtering conditions

			List<Integer> subList = currentAxisList.subList(1,currentAxisList.size()-2);

			if(!subList.contains(dim1)|| !subList.contains(dim2)){


				suggestedAxisPairList.add(am);



			}


   }


	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub.

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub.

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int mousex = e.getX()-10;
		int mousey = e.getY()-10;



		int axis1 = mousex/scatterInstanceWidth;
		int axis2 = mousey/scatterInstanceHeight;
		drawClickedAxes(axis1, axis2);


	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub.

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub.

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub.

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub.

	}

}
