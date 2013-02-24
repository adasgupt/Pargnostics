package org.mediavirus.parvis.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JPanel;

import org.mediavirus.parvis.gui.ParallelDisplay.Axis;
import org.mediavirus.parvis.gui.ParameterizedDisplay.AxisPairMetrics;
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

	private static final int numSuggestedAxisPairs =5;


	Color[] suggestedPairColors = {new Color(255, 255, 178), new Color(254, 204, 92), new Color(253, 141, 60), new Color( 240, 59, 32), new Color(189, 0, 38)};

	/*
	 * For linking with data view and keeping track of what the user selects
	 */
	List<Integer> currentAxisList = new ArrayList<Integer>();;
	/*
	 * List of all axis pair objects
	 */

	ArrayList<ParameterizedDisplay.AxisPairMetrics> metricsList;
	/*
	 * List of axis pairs that are suggested by the system
	 */
	ArrayList<AxisPairMetrics> suggestedAxisPairList = null;

	public enum MetaMetrics{

		JointEntropy, ImageEntropy, SumofJointImageEntropy, GrayEntropy, ColorEntropy, DistanceEntropy

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

					int locX= startX+(scatterInstanceWidth*(col))+(padding*col);
					int locY= scatterInstanceHeight*row + (padding*row);
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

					if( suggestedAxisPairList!= null)
					{

						for(int numSuggested =0; numSuggested < numSuggestedAxisPairs ; numSuggested++){

							int dim1 = suggestedAxisPairList.get(numSuggested).getDimension1();
							int dim2 = suggestedAxisPairList.get(numSuggested).getDimension2();

							//	System.err.println("Suggested  +++++++ "+ dim1 +"  " +dim2);

							ig.setColor(suggestedPairColors[numSuggested]);
							ig.setStroke(new BasicStroke(4));
							ig.drawRect(startX+(scatterInstanceWidth*(dim2)+(padding*dim2)), scatterInstanceHeight*(dim1)+(padding*dim1), scatterInstanceWidth-1 , scatterInstanceHeight-1);
							ig.drawRect(startX+(scatterInstanceWidth*(dim1)+(padding*dim1)), scatterInstanceHeight*(dim2)+(padding*dim2), scatterInstanceWidth-1 , scatterInstanceHeight-1);
						}



					}

					ig.setStroke(new BasicStroke(1));
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

				/*
				 * since low joint entropy is good, we color low joint entropy with a stronger hue, to correspond with 
				 * 'goodness'
				 */
				if(am.getDimension1()==row & am.getDimension2()==col)
				{
					if(index<=firstQuartile)
						g2.setColor(colorPattern0[2]);

					else if(index>firstQuartile && index <= thirdQuartile)
						g2.setColor(colorPattern0[1]);
					else if(index>thirdQuartile)
						g2.setColor(colorPattern0[0]);
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
				//System.err.println("Metric joint entropy");
				entropy1 = m1.getJointEntropy();
				entropy2 = m2.getJointEntropy();	

			}
			else if(metric == MetaMetrics.ImageEntropy)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = m1.getWeightedColorEntropy();
				entropy2 = m2.getWeightedColorEntropy();

			}

			else if(metric == MetaMetrics.DistanceEntropy)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = m1.getDistanceEntropy();
				entropy2 = m2.getDistanceEntropy();

			}

			//for sorting in response to user selection
			else if(metric == MetaMetrics.SumofJointImageEntropy)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = m1.getWeightedColorEntropy()+ m1.getJointEntropy();
				entropy2 = m2.getWeightedColorEntropy()+ m2.getJointEntropy();;

			}

			//	System.err.println("Compared " + entropy1 + " "+ entropy2);

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
			currentAxisList.add(axis2);
		}

		if (lastClicked != axis1) {
			currentAxisList .add(axis1);
			currentAxisList .add(axis2);
		}

		lastClicked = currentAxisList.get(currentAxisList .size()-1);
		repaint();
		addAxesToDraw(currentAxisList);
		removeAxisPairMetricObjects(currentAxisList);
	}


	/**
	 * TODO Removes the drawn axis from the axispairmetrics list for consideration for further sorting.
	 *
	 * @param currentAxisList
	 */
	private void removeAxisPairMetricObjects(List<Integer> currentAxisList) {

		for(AxisPairMetrics am: metricsList){

			int axis1 = am.getDimension1();
			int axis2 = am.getDimension2();

			for(int index=0; index<currentAxisList.size()-1; index++)
			{

				if((currentAxisList.get(index)== axis1 && currentAxisList.get(index+1)== axis2)||(currentAxisList.get(index)== axis2 && currentAxisList.get(index+1)== axis1))
				{

					metricsList.remove(am);
					System.err.println(" Removed  ----------------  " +axis1 + " axis1"+ "  " + "axis2" +" " +axis2);

				}

			}
		}


	}

	public void suggestAxisPairs(MetaMetrics selectedMetric){


		Collections.sort(metricsList, new SortMetrics(selectedMetric));


		//instantiate suggested list
		suggestedAxisPairList = new ArrayList<AxisPairMetrics>();

		// sublist for selecting axis pairs, we want to include the first and last axes but not the intermediate ones
		List<Integer> subList = new ArrayList<Integer>();

		//local copy of all axis pair objects list to prevent concurrent modification
		//ArrayList<AxisPairMetrics> copyOfMetricsList = new ArrayList<AxisPairMetrics>();

		//		for(AxisPairMetrics am:metricsList)
		//		{
		//
		//			copyOfMetricsList.add(am);
		//
		//		}

		for(AxisPairMetrics am: metricsList)
		{

			int dim1 = am.getDimension1();
			int dim2 = am.getDimension2();

			// filtering conditions
			if(!currentAxisList.isEmpty())
			{
				int lastElementPosition = currentAxisList.size()-1;
				subList = currentAxisList.subList(1, lastElementPosition);
				if((!subList.contains(dim1)|| !subList.contains(dim2))&& 
						(dim1==currentAxisList.get(0)||dim1==currentAxisList.get(currentAxisList.size()-1)||dim2==currentAxisList.get(0)||dim2==currentAxisList.get(currentAxisList.size()-1)))
				{

					suggestedAxisPairList.add(am);
					System.err.println("Suggested 1  " +dim1 + " Suggested 2  "+dim2);
					//	copyOfMetricsList.remove(am);

				}
			}
			else
				suggestedAxisPairList.add(am);
			//	copyOfMetricsList.remove(am);
		}

		//	metricsList = copyOfMetricsList;

		repaint();
	}

	public void updateCurrentAxes(Axis currentAxes[]){
		
		List<Integer> updatedAxesList = new ArrayList<Integer>();
		
		for(int pos=0; pos<currentAxes.length; pos++)
		{
			int dim =currentAxes[pos].dimension;
			updatedAxesList.add(dim);
			
		}
		
		currentAxisList = updatedAxesList;


	}


	public void addAxesToDraw(List<Integer> axisList) {

		Axis newAxes[] = new Axis[axisList.size()];
		Integer axisSetArray[]=new Integer[axisList.size()];

		for(int i=0;i<axisList.size();i++)
		{

			axisSetArray[i] = axisList.get(i);
		}

		for(int i=0; i<axisSetArray.length ;i++)
		{
			int axis1=axisSetArray[i];
			Axis newAxis1 = parallelDisplay.new Axis(axis1, data.getMinValue(axis1) - data.getMaxValue(axis1), data.getMaxValue(axis1), data.getAxisLabel(axis1));
			newAxes[i]= newAxis1;

		}

		parallelDisplay.updateAxes(newAxes);
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
