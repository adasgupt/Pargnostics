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
import java.util.Arrays;
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
	private int boxHeight;
	private int boxWidth;

	/*
	 * filledHeight can be different from box height, as metrics guide the coloring, and a box might not be fully colored
	 */
	private int filledHeight;
	private int startX=0;
	private int stepx;
	private int padding;

	private static final int numSuggestedAxisPairs =5;


	Color[] suggestedPairColors = {new Color(255, 255, 178), new Color(254, 204, 92), new Color(253, 141, 60), new Color( 240, 59, 32), new Color(189, 0, 38)};

	/*
	 * For linking with data view and keeping track of what is drawn in the main view
	 */
	List<Integer> currentDrawnList = new ArrayList<Integer>();

	/*
	 * For linking with data view and keeping track of what the user selects
	 */
	List<Integer> selectedAxesList = null;
	/*
	 * List of all axis pair objects
	 */

	ArrayList<ParameterizedDisplay.AxisPairMetrics> metricsList;
	/*
	 * List of axis pairs that are suggested by the system
	 */
	ArrayList<AxisPairMetrics> suggestedAxisPairList = null;

	public enum MetaMetrics{

		JointEntropy, ImageEntropy, SumofJointImageEntropy, GrayEntropy, ColorEntropy, DistanceEntropy, KLDivergence, InformationLoss, Color

	}

	int lastClicked =-1;

	int lastRow =-1;
	int lastColumn =-1;

	boolean encodingFilter = false;

	boolean decodingFilter = false ;

	/*
	 * connect with other classes
	 */
	ParallelDisplay parallelDisplay;
	ParameterizedDisplay parameterizedDisplay;
	DataSet data;
	private int numDimensions;
	private int numSelected;

	public MatrixMetaView(){

		addMouseListener(this);
		addMouseMotionListener(this);

	}

	public void initialize(DataSet data, ParallelDisplay parallelDisplay, ParameterizedDisplay parameterizedDisplay){

		this.parallelDisplay = parallelDisplay;
		this.data = data;
		this.parameterizedDisplay = parameterizedDisplay;
		currentDrawnList = getListFromAxes(parallelDisplay.axes);
		metricsList =parameterizedDisplay.getMetricsList();
		
		


	}

	public void paint(Graphics g) {

		//	PargnosticsPanel comp = (PargnosticsPanel)c;

		super.paint(g);
		Graphics2D ig = (Graphics2D)g;
		
		ArrayList<ParameterizedDisplay.AxisPairMetrics> paintList = parameterizedDisplay.getMetricsList();
	

		Graphics2D g2 = (Graphics2D)g; // we need a Graphics2D context


		int w = this.getWidth();

		int h = this.getHeight();




		//scatterInstanceWidth = scatterInstanceHeight = Math.max(scatterInstanceHeight, scatterInstanceWidth);



		//		width = c.getWidth() - 2 * borderH;
		//		height = c.getHeight() - 2 * borderV;
		//

		//		numRecords = comp.getNumRecords();
		if (numDimensions!=1)
			stepx = boxWidth / (numDimensions - 1);




		if(data != null) {

			boxHeight = getHeight() /data.getNumDimensions()-padding;
			boxWidth  = getWidth()/ data.getNumDimensions()-padding;
			numDimensions = parallelDisplay.getNumAxes();
			
			for(int row=0;row<data.getNumDimensions();row++) {

				for(int col = 0; col <data.getNumDimensions(); col++) {

					//g.drawLine(startX+(scatterInstanceWidth*(col)),scatterInstanceHeight*row,startX+(scatterInstanceWidth*(col+1)),scatterInstanceHeight*row);
					//g.drawLine(startX+(scatterInstanceWidth*(col)),scatterInstanceHeight*row, startX+(scatterInstanceWidth*(col)), scatterInstanceHeight*(row+1));



					if (row == col) {
						//draw the labels

						ig.setColor(Color.BLACK);

						//String text1=  "  Axis1   "  + parallelDisplay.getModel().getAxisLabel(rankedAxis1)  +  "   Axis2  " + parallelDisplay.getModel().getAxisLabel(rankedAxis2)+ " " ;

						String text1= data.getAxisLabel(row);
						FontMetrics fm= ig.getFontMetrics();
						int strWidth= fm.stringWidth(text1);
						int ascent= fm.getMaxAscent();
						int descent= fm.getMaxDescent();
						int rectHeight = boxHeight;
						int rectWidth  = boxWidth;
						int str_y= rectHeight/2-descent/2+ascent/2;
						int str_x= rectWidth/2-strWidth/2;


						ig.drawString(text1, startX+(padding*col)+(boxWidth*(col))+str_x, (padding*col)+(boxHeight*(col)+str_y));
						//g.drawRect(startX+(scatterInstanceWidth*(col)),(scatterInstanceHeight*(col)), scatterInstanceWidth, scatterInstanceHeight);
						//g2d.drawString(label,startX+(scatterInstanceWidth*(col))+50,startX+(scatterInstanceHeight*(col))+50);
						//g.fillRect(arg0, arg1, arg2, arg3)
					}

					int locX= startX+(boxWidth*col)+(padding*col);
					int locY= boxHeight*row + (padding*row);
					//								if(row>col)
					//								drawScatterplot(g2d, data, scatterInstanceHeight, scatterInstanceWidth, startX, locsX, locsY,row, col);
					//		



					// Encoding side
					if(row>col)
					{

						filledHeight = boxHeight-2;
						//0 for left of diagonal
						Graphics2D g3 = getMetricColor(paintList, ig, row, col, 0);
						g3.fillRect(startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth-2 , filledHeight);
						if(encodingFilter)
						{
							filledHeight = (int)getReducedEncodingHeight(row, col);
							g3.setColor(Color.white);
							g3.fillRect(startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth-2 , filledHeight);

						}
                     
						System.err.println("Col  " + col);
					}
					// Decoding side
					if(col>row)
					{

						filledHeight = boxHeight-2;
						// 1 for right of diagonal
						Graphics2D g3 = getMetricColor(paintList, ig, col, row, 1);
						
						System.err.println("Row  " + row);
						g3.fillRect(startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth-2 , filledHeight);
						if(decodingFilter)
						{
							//System.err.println("Reduced decoding rectangle    ********* ");
							filledHeight = (int)getReducedDecodingHeight(col, row);
							g3.setColor(Color.white);
							g3.fillRect(startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth-2 , filledHeight);

						}


					}

					//suggested axis pairs
					if( suggestedAxisPairList!= null)
					{

						for(int numSuggested =0; numSuggested < numSuggestedAxisPairs ; numSuggested++){

							int dim1 = suggestedAxisPairList.get(numSuggested).getDimension1();
							int dim2 = suggestedAxisPairList.get(numSuggested).getDimension2();

							//	System.err.println("Suggested  +++++++ "+ dim1 +"  " +dim2);

							ig.setColor(suggestedPairColors[numSuggested]);
							ig.setStroke(new BasicStroke(4));
							ig.drawRect(startX+(boxWidth*(dim2)+(padding*dim2)), boxHeight*(dim1)+(padding*dim1), boxWidth-1 , boxHeight-1);
							ig.drawRect(startX+(boxWidth*(dim1)+(padding*dim1)), boxHeight*(dim2)+(padding*dim2), boxWidth-1 , boxHeight-1);
						}



					}

					// resetting the stroke after setting a wider stroke for suggesting axis pairs
					ig.setStroke(new BasicStroke(1));



					//					if (lastClicked == row || lastClicked == col) {
					//
					//						ig.setColor(new Color(160, 40, 30, 100));
					//						ig.fillRect( startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth, boxHeight);
					//
					//							System.err.println("padding  " + padding);
					//							System.err.println("col *****  " + col);
					//
					//					}

					//draw borders at the end
					ig.setColor(Color.lightGray);

					ig.drawRect(startX+(boxWidth*(col)+(padding*col)), boxHeight*(row)+(padding*row), boxWidth , boxHeight );


				}
			}
		}


	}




	/**
	 * get sorted list and set up colors for painting
	 *
	 */
	private Graphics2D getMetricColor(ArrayList<ParameterizedDisplay.AxisPairMetrics> localMetricsListForSorting, Graphics2D g2, int row, int col, int mode) {

	//	ArrayList<ParameterizedDisplay.AxisPairMetrics> localMetricsListForSorting = new ArrayList(metricsList);
		//metricsList = parameterizedDisplay.getMetricsList();
//		if(metricsList!=null)
//			localMetricsListForSorting = (ArrayList<ParameterizedDisplay.AxisPairMetrics>)metricsList.clone();

		//		for(AxisPairMetrics am: parameterizedDisplay.getMetricsList())
		//			System.err.println("testing ******** " + am.getDistanceEntropy());
		if(localMetricsListForSorting!=null)
		{
			int firstQuartile = localMetricsListForSorting.size()/4;
			int thirdQuartile= (3*localMetricsListForSorting.size())/4;

			if(mode==0)
			{
				Color[] colorPattern0 = {new Color(203, 201, 226), new Color(158, 154, 200), new Color(106, 81, 163)};

				Collections.sort(localMetricsListForSorting, new SortMetrics(MetaMetrics.JointEntropy));
				
				for(int index=0; index<localMetricsListForSorting.size(); index++){
					AxisPairMetrics am = localMetricsListForSorting.get(index);

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
				Collections.sort(localMetricsListForSorting, new SortMetrics(MetaMetrics.ImageEntropy));
				
				for(int index=0; index<localMetricsListForSorting.size(); index++){
					AxisPairMetrics am = localMetricsListForSorting.get(index);
					if(am.getDimension1()==row & am.getDimension2()==col)
					{
						if(index<=firstQuartile)
							g2.setColor(colorPattern1[2]);
						else if(index>firstQuartile && index <= thirdQuartile)
							g2.setColor(colorPattern1[1]);
						else if(index>thirdQuartile)
							g2.setColor(colorPattern1[0]);
					}
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

			if(metric==MetaMetrics.ColorEntropy)

			{
				//System.err.println("Metric joint entropy");
				entropy1 = m1.getColorEntropy();
				entropy2 = m2.getColorEntropy();	

			}

			if(metric==MetaMetrics.JointEntropy)

			{
				//System.err.println("Metric joint entropy");
				entropy1 = 1-(m1.getJointEntropy()/10);
				entropy2 = 1-(m2.getJointEntropy()/10);	

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

			else if(metric == MetaMetrics.KLDivergence)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = 1-(m1.getKLDivergence()/1000);
				entropy2 = 1-(m2.getKLDivergence()/1000);

			}
			else if(metric == MetaMetrics.InformationLoss)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = (1-m1.getKLDivergence()/100)+(1-m1.getJointEntropy()/10);
				entropy2 = (1-m2.getKLDivergence()/100)+(1-m2.getJointEntropy()/10);

			}
			else if(metric == MetaMetrics.Color)
			{

				//System.err.println("Metric distance entropy");
				entropy1 = m1.getDistanceEntropy();
				entropy2 = m2.getDistanceEntropy();

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

		selectedAxesList = new ArrayList<Integer>();
		lastRow = axis2;
		lastColumn =axis1;

		if (lastClicked!=-1 && lastClicked == axis1) {
			selectedAxesList.add(axis2);
		}

		if (lastClicked != axis1) {
			selectedAxesList.add(axis1);
			selectedAxesList.add(axis2);
		}

		lastClicked = selectedAxesList.get(selectedAxesList .size()-1);


		System.err.println("Last clicked   ******** "+ lastClicked);
		repaint();
		addAxesToDraw(selectedAxesList);
		removeAxisPairMetricObjects(selectedAxesList);

	}


	/**
	 * Removes the drawn axis from the axispairmetrics list for consideration for further sorting.
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

			System.err.println("Val *********************************************  " +am.getJointEntropy());
			
			int dim1 = am.getDimension1();
			int dim2 = am.getDimension2();

			// filtering conditions
			if(selectedAxesList!=null)
			{
				int lastElementPosition = currentDrawnList.size()-1;
				subList = currentDrawnList.subList(1, lastElementPosition);
				if((!subList.contains(dim1)|| !subList.contains(dim2))&& 
						(dim1==currentDrawnList.get(0)||dim1==currentDrawnList.get(currentDrawnList.size()-1)||dim2==currentDrawnList.get(0)||dim2==currentDrawnList.get(currentDrawnList.size()-1)))
				{

					suggestedAxisPairList.add(am);
					//	System.err.println("Suggested 1  " +dim1 + " Suggested 2  "+dim2);
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

	//from mainview to matrix view:updates axes list according to what is drawn in the main view

	public void updateCurrentAxes(Axis currentAxes[]){

		List<Integer> updatedAxesList = getListFromAxes(currentAxes);

		currentDrawnList = updatedAxesList;


	}

	//wrapper function to convert from Axis to integer dimensions 
	public List<Integer> getListFromAxes(Axis currentAxes[]){

		List<Integer> updatedAxesList = new ArrayList<Integer>();

		for(int pos=0; pos<currentAxes.length; pos++)
		{
			int dim =currentAxes[pos].dimension;
			updatedAxesList.add(dim);

		}

		return updatedAxesList;

	}

	/*
	 * from matrix view to main view: the linking function that draws axes in the main view
	 */
	public void addAxesToDraw(List<Integer> userSelectedAxisList) {

		numSelected++;
		Axis[] currentAxes = getAxesFromList(currentDrawnList);
		Integer[] currentAxesArray = new Integer[currentDrawnList.size()+1];
		currentAxesArray= currentDrawnList.toArray(currentAxesArray);



		int firstAxis = userSelectedAxisList.get(0);
		int lastAxis =  userSelectedAxisList.get(userSelectedAxisList.size()-1);


		System.err.println("First axis  " +firstAxis);
		System.err.println("Last axis  " + lastAxis);

		if(numSelected==1)
			currentDrawnList =userSelectedAxisList;

		//		if(userSelectedAxisList.size()>2)
		//		{

		System.err.println(" current drawn list first " +currentDrawnList.get(0));
		System.err.println(" current drawn list last " +currentDrawnList.get(currentDrawnList.size()-1));



		if(currentAxesArray[0] == firstAxis){

			System.err.println(" First time *************************** 1 ");
			//currentDrawnList.add(lastAxis);
			int x = lastAxis;

			for(int i= currentAxesArray.length-1; i>0; i--)
			{

				currentAxesArray[i] = currentAxesArray[i-1];




			}
			currentAxesArray[0]= x;
			currentDrawnList =Arrays.asList(currentAxesArray);


		}

		else if(currentAxesArray[0] == lastAxis){

			System.err.println(" Second time *************************** 2 ");
			//currentDrawnList.add(firstAxis);
			int x = firstAxis;

			for(int i= currentAxesArray.length-1; i>0; i--)
			{

				currentAxesArray[i] = currentAxesArray[i-1];




			}
			currentAxesArray[0]= x;
			currentDrawnList =Arrays.asList(currentAxesArray);


		}


		else if(currentAxesArray[currentAxesArray.length-2] == lastAxis){
			currentAxesArray[currentAxesArray.length-1] = firstAxis;
			currentDrawnList =Arrays.asList(currentAxesArray);
			System.err.println(" Second time *************************** 3 ");


		}

		else if(currentAxesArray[currentAxesArray.length-2]== firstAxis){
			System.err.println(" Second time *************************** 4 ");
			currentAxesArray[currentAxesArray.length-1] = lastAxis;
			currentDrawnList =Arrays.asList(currentAxesArray);

		}


		Axis[] newAxes = getAxesFromList(currentDrawnList);
		updateCurrentAxes(newAxes);
		parallelDisplay.updateAxes(newAxes);


		//	}
		//		else{
		//
		//			Axis[] newAxes = getAxesFromList(userSelectedAxisList);
		//			updateCurrentAxes(newAxes);
		//			parallelDisplay.updateAxes(newAxes);
		//
		//
		//
		//		}


	}


	public Axis[] getAxesFromList(List<Integer> axisList){

		Axis newAxes[] = new Axis[axisList.size()];
		Integer axisSetArray[]=new Integer[axisList.size()];

		for(int i=0;i<axisList.size();i++)
		{

			axisSetArray[i] = axisList.get(i);
		}

		for(int i=0; i<axisSetArray.length ;i++)
		{
			int axis1 = axisSetArray[i];
			Axis newAxis1 = parallelDisplay.new Axis(axis1, data.getMinValue(axis1) - data.getMaxValue(axis1), data.getMaxValue(axis1), data.getAxisLabel(axis1));
			newAxes[i]= newAxis1;

		}
		return newAxes;
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
		int mousex = e.getX()-startX;
		int mousey = e.getY()-startX;



		int axis1 = mousex/boxWidth;
		int axis2 = mousey/boxHeight;
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

	/**
	 * redraws the matrix view, the encoding side based on the amount of information loss.
	 * The size of the squares will be proportional to the loss. More size means less information loss.
	 *
	 */
	public void setEncodingFilter() {


		System.err.println("Encoding filter ON ********************** ");
		encodingFilter = true;
		repaint();

	}


	public void setDecodingFilter() {


		System.err.println("Decoding filter ON ********************** ");
		decodingFilter = true;
		repaint();

	}

	/**
	 * Reduced height after considering information loss.
	 *
	 * @param row
	 * @param col
	 * @return
	 */
	public float getReducedEncodingHeight(int row, int col)
	{

		ArrayList<AxisPairMetrics> amList =parameterizedDisplay.getMetricsList();
		Collections.sort(amList, new SortMetrics(MetaMetrics.KLDivergence));
		int firstQuartile = metricsList.size()/4;
		int thirdQuartile= (3*metricsList.size())/4;

		float newBoxHeight =0;

		for(int index=0; index<metricsList.size(); index++)
		{
			AxisPairMetrics am = metricsList.get(index);

//penalize high value
			if(am.getDimension1()==row & am.getDimension2()==col)
			{
				if(index<=firstQuartile)
					newBoxHeight = boxHeight*0.25f;

				else if(index>firstQuartile && index <= thirdQuartile)
					newBoxHeight = boxHeight*0.5f;
				else if(index>thirdQuartile)
					newBoxHeight = boxHeight*0.75f;
			}
		}

		return newBoxHeight;
	}

	/**
	 * Reduced height after considering pixel entropy.
	 *
	 * @param row
	 * @param col
	 * @return
	 */
	private float getReducedDecodingHeight(int row, int col) {

		ArrayList<AxisPairMetrics> amList =parameterizedDisplay.getMetricsList();
		Collections.sort(amList, new SortMetrics(MetaMetrics.DistanceEntropy));
		int firstQuartile = metricsList.size()/4;
		int thirdQuartile= (3*metricsList.size())/4;

		float newBoxHeight =0;
//penalize low value
		for(int index=0; index<metricsList.size(); index++)
		{
			AxisPairMetrics am = metricsList.get(index);
		//	System.err.println(" Entropy value  " + am.getColorEntropy());

			if(am.getDimension1()==row & am.getDimension2()==col)
			{
				if(index<=firstQuartile)
					newBoxHeight = boxHeight*0.75f;

				else if(index>firstQuartile && index <= thirdQuartile)
					newBoxHeight = boxHeight*0.5f;
				else if(index>thirdQuartile)
					newBoxHeight = boxHeight*0.25f;
			}
		}

		//System.err.println(" Decoding height  +++++++++ " +newBoxHeight);
		return newBoxHeight;
	}

}
