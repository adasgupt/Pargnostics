package org.mediavirus.parvis.gui;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;


import org.mediavirus.parvis.gui.ParallelDisplay.Axis;
import org.mediavirus.parvis.gui.analysis.BranchAndBoundOptimizer;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;
import org.mediavirus.parvis.model.Brush;
import org.mediavirus.parvis.model.DataSet;

@SuppressWarnings("serial")
public class OptimizationPanel extends JPanel {

	Brush dragBrush;
	boolean inBrush=false;
	
	private JButton optimizationButton;
	private JSlider sliderBar[] = new JSlider[3];
	String comboBoxItems[];
	private ArrayList<JButton> buttons = new ArrayList<JButton>();
	JRadioButton sliderButton[]= new JRadioButton[3];
	JLabel sliderLabel[] = new JLabel[3];
	
    
	private ParallelDisplay parallelDisplay;
	private JCheckBox[] maximizers = new JCheckBox[Metrics.values().length];
	
	private JComboBox comboBox; 
	int minThreshold = 20;
	//private JSlider sliderBar[]=new JSlider[4];
	private RangeSlider rangeSliderBar[] = new RangeSlider[4];

	private DataSet data;
	int numBins;
	

	private JButton clumpingButton;
	
	private String[] radioButtonNames={"PrincipleDirection", "Parallelism", "Clumping"};
	private int brushIndex;
	private JPanel buttonBox;

	public OptimizationPanel(ParallelDisplay parallelDisplay) {
		super();
		this.parallelDisplay = parallelDisplay;
		this.data = parallelDisplay.getModel();
		numBins = parallelDisplay.getHeight();

		MigLayout mig=new MigLayout();
		//setLayout(new BorderLayout());
		setLayout(mig);
		JPanel sliderPanel= new JPanel();
		sliderPanel.setLayout(new MigLayout());

		Box mainBox = new Box(BoxLayout.Y_AXIS);
		sliderPanel.add(mainBox,"wrap,  w 220, h 600");
		
		// setting up the radio buttons for brushing
		for(int i=0;i<sliderButton.length;i++){
			sliderLabel[i] = new JLabel(Metrics.values()[i].name());
			sliderBar[i] = new JSlider(0, 5, 0);
			sliderBar[i].setMinorTickSpacing(1);
			sliderBar[i].setMajorTickSpacing(5);
			sliderBar[i].setPaintTicks(true);
			sliderBar[i].setSnapToTicks(true);
			//sliderBar[i].setPaintTrack(false);
			sliderBar[i].setPaintLabels(true);
			sliderBar[i].setEnabled(true);
//			sliderBar[i].addChangeListener(new SliderListener());
			maximizers[i] = new JCheckBox("Max");
			Box b = new Box(BoxLayout.X_AXIS);
			b.add(sliderBar[i]);
			b.add(maximizers[i]);
			mainBox.add(sliderLabel[i]);
			mainBox.add(b);
//			mainBox.add(sliderBar[i]);
//			mainBox.add(maximizers[i]);
			//group.add(sliderButton[i]);

		}

	//	add(sliderPanel, "wrap");


		//Dimension optimDimension=new Dimension();

		//JPanel optimPanel=new JPanel();
		//optimPanel.setLayout(new MigLayout());

	//	add(optimPanel);

	//	Box metricBox=new Box(BoxLayout.Y_AXIS);
	//	optimPanel.add(metricBox,"wrap, growY");

		optimizationButton=new JButton("Optimize!");
		optimizationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				optimize();
			}
		});
		mainBox.add(optimizationButton);
		
		
//		metricBox.setBorder(BorderFactory.createLineBorder(Color.black));

		
	
		
		
		
		buttonBox = new JPanel(new MigLayout());

		
		
//		for(int i = 0; i < Metrics.values().length; i++) {
//			JButton b = new JButton(Metrics.values()[i].name()); 
//			b.setBackground(mD.metricColor[i]);
//			buttonBox.add(b, "height 22!, gap 0, growx, wrap");
//
//			buttons.add(b);
//		}

	//	Box mainBox = Box.createVerticalBox();
	//	sliderPanel.add(mainBox,"h 300");
		


		setComboBoxItems(0);

		ButtonGroup group= new ButtonGroup();
		comboBox.addItemListener(new ComboBoxItemListener());
		comboBox.setPreferredSize(new Dimension(50,30));

		Box labelBox1 = Box.createHorizontalBox();

		JLabel axisPairLabel = new JLabel();
		//axisPairLabel.setPreferredSize(new Dimension(100, 20));


		labelBox1.add(axisPairLabel);

		axisPairLabel.setText("Select Axis-pair");
		//axisPairLabel.setHorizontalTextPosition(JLabel.CENTER);
		mainBox.add(labelBox1, BorderLayout.CENTER);
		mainBox.add(comboBox);

		mainBox.createVerticalStrut(40);

		Box labelBox2 = Box.createHorizontalBox();
		JLabel brushLabel = new JLabel("Select Brushing Metric", JLabel.CENTER);
		labelBox2.add(brushLabel);
		mainBox.add(labelBox2);

		Box[] buttonBox = new Box[sliderButton.length];

		Hashtable labelTable = null;
		// setting up the radio buttons for brushing
		for(int i=0;i<sliderButton.length;i++){
            
			//sliderLabel[i] = new JLabel(Metrics.values()[i].name());			
			sliderButton[i]=new JRadioButton(radioButtonNames[i], false);
			sliderButton[i].setPreferredSize(new Dimension(10, 100));
			buttonBox[i] = new Box(BoxLayout.Y_AXIS);

			//sliderBar[i]=new JSlider();
			if(i==0)
			{
				rangeSliderBar[i] = new RangeSlider(0,100);
				rangeSliderBar[i].setValue(20);
				//	rangeSliderBar[i].setBorder(BorderFactory.createLineBorder(Color.gray));
				rangeSliderBar[i].setUpperValue(60);
				rangeSliderBar[i].setPreferredSize(new Dimension(5, 100));
				rangeSliderBar[i].setMinorTickSpacing(1);
				rangeSliderBar[i].setMajorTickSpacing(100);
				rangeSliderBar[i].setSnapToTicks(true);
				rangeSliderBar[i].setPaintLabels(true);
				rangeSliderBar[i].setEnabled(true);

				rangeSliderBar[i].addChangeListener(new SliderListener());


				labelTable = new Hashtable();
				labelTable.put( new Integer( 0 ), new JLabel("Down") );
				labelTable.put( new Integer( 200 ), new JLabel("Up") );
				//labelTable.put( new Integer( FPS_MAX ), new JLabel("Fast") );
				rangeSliderBar[i].setLabelTable( labelTable );
			}
			else if(i==1)
			{
				rangeSliderBar[i] = new RangeSlider(0,100);
				//	rangeSliderBar[i].setBorder(BorderFactory.createLineBorder(Color.gray));
				rangeSliderBar[i].setPreferredSize(new Dimension(5, 100));
				rangeSliderBar[i].setValue(50);
				rangeSliderBar[i].setUpperValue(75);
				rangeSliderBar[i].setMinorTickSpacing(1);
				rangeSliderBar[i].setMajorTickSpacing(50);
				rangeSliderBar[i].setSnapToTicks(true);
				rangeSliderBar[i].setPaintLabels(true);
				rangeSliderBar[i].setEnabled(true);

				rangeSliderBar[i].addChangeListener(new SliderListener());

				labelTable = new Hashtable();
				labelTable.put( new Integer( 0 ), new JLabel("Low") );
				labelTable.put( new Integer( 100 ), new JLabel("High") );
				//labelTable.put( new Integer( FPS_MAX ), new JLabel("Fast") );
				rangeSliderBar[i].setLabelTable( labelTable );

			}

			else
			{

				rangeSliderBar[i] = new RangeSlider(1,100);
				//	rangeSliderBar[i].setBorder(BorderFactory.createLineBorder(Color.gray));
				rangeSliderBar[i].setPreferredSize(new Dimension(5, 200));
				rangeSliderBar[i].setValue(50);
				rangeSliderBar[i].setUpperValue(75);
				rangeSliderBar[i].setMinorTickSpacing(1);
				rangeSliderBar[i].setMajorTickSpacing(50);
				rangeSliderBar[i].setSnapToTicks(true);
				rangeSliderBar[i].setPaintLabels(true);
				rangeSliderBar[i].setEnabled(true);

				rangeSliderBar[i].addChangeListener(new SliderListener());		

				labelTable = new Hashtable();
				labelTable.put( new Integer( 0 ), new JLabel("Low") );
				labelTable.put( new Integer( 100 ), new JLabel("High") );
				//labelTable.put( new Integer( FPS_MAX ), new JLabel("Fast") );
				rangeSliderBar[i].setLabelTable( labelTable );



			}
			//sliderBar[i].setMinorTickSpacing(1);
			//sliderBar[i].setMajorTickSpacing(50);


			//sliderBar[i].setPaintTicks(true);
			//sliderBar[i].setSnapToTicks(true);
			//sliderBar[i].setPaintTrack(false);
			//sliderBar[i].setPaintLabels(true);
			//sliderBar[i].setEnabled(true);
			//	maximizers[i] = new JCheckBox("Max");
			//	sliderBar[i].addChangeListener(new SliderListener());



			//sliderBar[i].setPaintTrack(false);

			//			Box b = new Box(BoxLayout.X_AXIS);
			//			b.add(sliderBar[i]);
			//			b.add(maximizers[i]);
			//			mainBox.add(sliderLabel[i]);
			//			mainBox.add(b);

			buttonBox[i].add(sliderButton[i]);
			buttonBox[i].add(rangeSliderBar[i]);
			buttonBox[i].setBorder(BorderFactory.createLineBorder(Color.gray));
			mainBox.add(buttonBox[i]);
			//			mainBox.add(sliderButton[i]);
			//			mainBox.add(rangeSliderBar[i]);
			group.add(sliderButton[i]);

		}


		//rangeSliderBar[0].setMinimum(-1);
		//rangeSliderBar[0].setMaximum(1);
		clumpingButton = new JButton("Show Clumping");
		clumpingButton.setBackground(Color.LIGHT_GRAY);
		clumpingButton.setPreferredSize(new Dimension(20, 20));

		ButtonHandler handler=new ButtonHandler();

		ButtonHandler clumpingButtonHandler = new ButtonHandler();
		clumpingButton.addActionListener(clumpingButtonHandler);

		// mainBox.add(clumpingButton);

		RadioButtonHandler radiohandler=new RadioButtonHandler();
		for(int i=0;i<sliderButton.length;i++)
			sliderButton[i].addItemListener(radiohandler);

		//	sliderPanel.add(northBox, BorderLayout.NORTH);
		//	sliderPanel.add(mainBox,BorderLayout.CENTER);
		//sliderPanel.setPreferredSize(sliderDimension);

		//ADD TO THE MAIN PANEL
		//	add(sliderPanel, BorderLayout.CENTER);
		add(sliderPanel, "wrap");


		//Dimension optimDimension=new Dimension();

	//	JPanel optimPanel=new JPanel();
	//	optimPanel.setLayout(new MigLayout());

		//ADD TO THE MAIN PANEL
		//	add(optimPanel, BorderLayout.SOUTH);
//		add(optimPanel);
		//optimPanel.setLayout(new BorderLayout());

		//	Box metricBox=new Box(BoxLayout.Y_AXIS);
		//	optimPanel.add(metricBox,"wrap, growY");

		//	JLabel optimDisplay=new JLabel("Select Optimization Criterion");
		//	metricBox.add(optimDisplay);
		//	minimizeCB = new JCheckBox("Minimize", true);
		//	metricBox.add(minimizeCB);
		//		minimizeCB.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				minimize = minimizeCB.isSelected();
		//			}
		//		});
		//optimPanel.add(metricBox,"wrap, growY");


		mainBox.createVerticalStrut(20);
		
		
		
		
}

	protected void setAlpha() {
		
	//	parallelDisplay.getDisplayUI().setAlphaParameters(true);
		parallelDisplay.repaint();
	}

	public float[] getWeights() {
		float weights[] = new float[Metrics.values().length];
		float sum = 0;
		
		for (int i = 0; i < sliderBar.length; i++) {
			weights[i] = sliderBar[i].getValue();
			sum += weights[i];
		}
		
		if (sum == 0)
			sum = 1;
		
		for (int i = 0; i < sliderBar.length; i++)
			weights[i] /= sum;
		
		return weights;
	}
	
	
	public float getNetMutualInformationContent(){

		Axis currentAxes[] = parallelDisplay.axes;
		float pairWiseMutualInfo=0.0f;
		float netMutualInfo=0.0f;
		for(int i=0;i<currentAxes.length-1;i++){

			//compute the mutual information between each pair

			pairWiseMutualInfo = parallelDisplay.getModel().getAxisPair(i, i+1, parallelDisplay).getMutualInformation(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV()).getValue();
			netMutualInfo=netMutualInfo+pairWiseMutualInfo;

		}


		return 0;
	}


//	public void setBrushParameters(int sliderNum, int brushVal){
//
//		dragBrush=new Brush(parallelDisplay.getNumRecords(), Color.RED);
//
//		switch(sliderNum){
//
//		// brush for angles of Crossing
//		case 0:
//			parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getNumCrossings(false);
//			List<Float> angles = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getAnglesList();
//
//			for(int i = 0; i < parallelDisplay.getNumRecords(); i++) {
//
//				int angleVal=(int)angles.get(i).doubleValue();
////				System.err.println("Angle  "  + angleVal);
//				if(angleVal<=brushVal) {
//					// System.err.println("Brushing");
//					dragBrush.setBrushValue(i, 1.0f);
//				} else {
//					dragBrush.setBrushValue(i, 0.0f);
//				}
//
//			}
//			doBrushing(dragBrush);
//			break;
//
//			// brush for parallelism
//		case 1:
//
//			float median = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getParallelismMedian();
//			List<Double> distanceList = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues();
//			Collections.sort(distanceList);
//			float highestVal=(float)distanceList.get(distanceList.size()-1).doubleValue();
//			float normalizedMedian = median/highestVal;
//			//to normalize within 0-1 range divide by the highest val
//
//			System.err.println("median = "+median+", normalizedMedian = "+normalizedMedian);
//
//			distanceList = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues();
//			for(int i=0;i<parallelDisplay.getNumRecords();i++){
//				float distanceVal=((float)distanceList.get(i).doubleValue())/highestVal;
//
//				float deviationFromMedian = normalizedMedian-distanceVal;
//				if(deviationFromMedian < 0)
//					deviationFromMedian = -(deviationFromMedian);
//
//				int int_deviationFromMedian = (int)(deviationFromMedian*100);
//
////				System.err.println("Val " + int_deviationFromMedian);
////				System.err.println("BrushVal "  +brushVal);
//
//				if(int_deviationFromMedian <= brushVal&& brushVal<=int_deviationFromMedian+5){
//					dragBrush.setBrushValue(i, 1.0f);
//				} else {
//					dragBrush.setBrushValue(i, 0.0f);
//				}
//
//
//			} 
//			doBrushing(dragBrush);
//			break; 
//
//
//		case 2:
//			//brush for convergence
//
//			int[] degreeOfConvergence = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDegree(true);
//			AxisPair ap = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay);
//
//			float highestConv = 0;
////			float highestDiv = 0;
//			for (int i = 0; i < degreeOfConvergence.length; i++) {
//				if (degreeOfConvergence[i] > highestConv)
//					highestConv = degreeOfConvergence[i];
//
//			}
//
//			for (int i = 0; i < parallelDisplay.getNumRecords(); i++) {
//
//				float val = (float)degreeOfConvergence[ap.value2pixel(parallelDisplay.getModel().getValue(i, brushIndex+1), false, false)] / highestConv;
//
//				if (val * 100 >= brushVal) {
//					dragBrush.setBrushValue(i, 1.0f);
//				} else {
//					dragBrush.setBrushValue(i, 0.0f);
//				}
//
//			}
//			doBrushing(dragBrush);
//
//			break;
//
//		case 3:
//			//brush for divergence
//
//
//			int[] degreeOfDivergence =  parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDegree(false);
//			AxisPair ap2 = parallelDisplay.getModel().getAxisPair(brushIndex, brushIndex+1, parallelDisplay);
//
//			float highestDiv1 = 0;
//			for (int i = 0; i < degreeOfDivergence.length; i++) {
//
//				if (degreeOfDivergence[i] > highestDiv1)
//					highestDiv1 = degreeOfDivergence[i];
//			}
//
//			for (int i = 0; i < parallelDisplay.getNumRecords(); i++) {
//
//				float val = (float)degreeOfDivergence[ap2.value2pixel(parallelDisplay.getModel().getValue(i, brushIndex),  true, false)] / highestDiv1;
//
//				if (val * 100 >= brushVal) {
//					dragBrush.setBrushValue(i, 1.0f);
//				} else {
//					dragBrush.setBrushValue(i, 0.0f);
//				}
//
//			}
//			doBrushing(dragBrush);
//
//			break;
//		}
//
//	}
	
	private class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			//JSlider source = (JSlider)e.getSource();
			int lowerBrushVal = -1;
			int upperBrushVal = -1;

			if(e.getSource()== rangeSliderBar[0])
			{	
				if (!rangeSliderBar[0].getValueIsAdjusting()) {

					lowerBrushVal = (int)rangeSliderBar[0].getValue();
					upperBrushVal = (int)rangeSliderBar[0].getUpperValue();

					setBrushParameters(0, lowerBrushVal, upperBrushVal);

				} 
			}

			if(e.getSource()== rangeSliderBar[1])
			{	
				if (!rangeSliderBar[1].getValueIsAdjusting()) {

					lowerBrushVal = (int)rangeSliderBar[1].getValue();
					upperBrushVal = (int)rangeSliderBar[1].getUpperValue();

					setBrushParameters(1, lowerBrushVal, upperBrushVal);

				} 
			} 
			if(e.getSource()== rangeSliderBar[2])
			{	
				if (!rangeSliderBar[2].getValueIsAdjusting()) {

					lowerBrushVal = (int)rangeSliderBar[2].getValue();
					upperBrushVal = (int)rangeSliderBar[2].getUpperValue();

					setBrushParameters(2, lowerBrushVal, upperBrushVal);

				} 
			}   
			if(e.getSource()== rangeSliderBar[3])
			{	
				if (!rangeSliderBar[3].getValueIsAdjusting()) {

					lowerBrushVal = (int)rangeSliderBar[3].getValue();
					upperBrushVal = (int)rangeSliderBar[3].getUpperValue();

					//	setBrushParameters(3, lowerBrushVal, upperBrushVal);

				} 
			}   

		}
	}

	
	private class RadioButtonHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e){

			if(e.getSource()==sliderButton[0] && e.getStateChange()==ItemEvent.SELECTED)
			{

				rangeSliderBar[0].setEnabled(true);
				rangeSliderBar[1].setEnabled(false);
				rangeSliderBar[2].setEnabled(false);
				//rangeSliderBar[3].setEnabled(false);

			}

			if(e.getSource()==sliderButton[1] && e.getStateChange()==ItemEvent.SELECTED)
			{

				rangeSliderBar[0].setEnabled(false);
				rangeSliderBar[1].setEnabled(true);
				rangeSliderBar[2].setEnabled(false);
				//	rangeSliderBar[3].setEnabled(false);
			}
			if(e.getSource()==sliderButton[2] && e.getStateChange()==ItemEvent.SELECTED)
			{
				rangeSliderBar[0].setEnabled(false);
				rangeSliderBar[1].setEnabled(false);
				rangeSliderBar[2].setEnabled(true);
				//	rangeSliderBar[3].setEnabled(false);
			}


		}

	}

	//perform brushing based on dragbrush settings

	public void doBrushing(Brush dragbrush){


		RenderThread brushThread = parallelDisplay.getDisplayUI().brushThread;

		dragBrush.setColor(Color.BLUE);
		parallelDisplay.brushChanged=true;
		parallelDisplay.setCurrentBrush(dragBrush);
		parallelDisplay.fireBrushModified(dragBrush);					 

		if(dragBrush.getNumBrushed()>0)
		{ 
			parallelDisplay.getDisplayUI().inBrush=true;
			brushThread.setRegion(0, parallelDisplay.getNumAxes()- 1);
			brushThread.render();

			parallelDisplay.repaint();

		}  
	}

//	Optimization criteria	

	private void optimize() {
		System.err.println("********************");
	//	System.err.println("Optimizing new");
		float weights[] = getWeights();
	    boolean maximize[] = getMaximize();
	    
		System.err.print("Weights: ");
		for (float f : weights)
			System.err.print(f+", ");
		System.err.println();
		BranchAndBoundOptimizer bb = new BranchAndBoundOptimizer(parallelDisplay, weights, maximize, parallelDisplay.getHeight()-2*parallelDisplay.getBorderV());
		ArrayList<Integer> bestPerm = bb.optimize();

		int bestPermArray[] = new int[bestPerm.size()];
		for (int i = 0; i < bestPermArray.length; i++)
			bestPermArray[i] = bestPerm.get(i);
		
		parallelDisplay.reOrder(bestPermArray, bb.getAxisInversions());

	}


	private boolean[] getMaximize() {
		boolean maximize[] = new boolean[5];
		
//		for(int i=0; i<maximizers.length; i++)
//		{
//			
//			System.err.println("VALLL " +maximizers[i]);
//		}
//		
//		for (int i = 0; i < Metrics.values().length; i++)
//			maximize[i] = maximizers[i].isSelected();

		return maximize;
	}

	public JPanel getButtonBox() {
		return buttonBox;
	}
	
	private class ButtonHandler implements ActionListener{
		public void actionPerformed(ActionEvent e)
		{

			int index = buttons.indexOf(e.getSource());
			int brushAxis =0;
			//if (index >= 0)
			//optimize(Metrics.values()[index]);

			// for brushing on outliers

			int dim1 = brushIndex;
			int dim2 = brushIndex+1;

			if(e.getSource()== clumpingButton)
				setSubSpaceParametersForBrushing(dim1, dim2, minThreshold);


		}
	}

	public void setSubSpaceParametersForBrushing(int dim1, int dim2, int minThreshold) {

		int brushAxis;

        HashMap<Point2D, ArrayList<Integer>> clumpingMap = data.getAxisPair(dim1, dim2, parallelDisplay).getClumpingMap(numBins, minThreshold);

		//chosen Axis 1- first axis, 2 - second axis
		ArrayList<Integer> brushedRecordsList = new ArrayList<Integer>();

		Iterator iter = clumpingMap.keySet().iterator();

		//System.err.println("SIZE ************************************* " + clumpingMap.keySet().size());
		while(iter.hasNext()){

			Point2D.Float key = (Point2D.Float)iter.next();
			System.err.println("Chosen axis " + key.x);

			if(key.x == 1)
				brushAxis = dim1;
			else 
				brushAxis = dim2;

			float startBin = key.y;
			ArrayList<Integer> neighBorsList = clumpingMap.get(key);

			//	System.err.println("Number of neighbors  " + clumpingMap.get(key).size());
			Brush selectionBrush = new Brush(parallelDisplay.getNumRecords(), Color.red);

			HashMap<Integer, HashMap<Integer,Float>> pixelMapForData = parallelDisplay.getMap();

			HashMap<Integer, Float> binDataMapPerDimension = pixelMapForData.get(brushAxis);

			Iterator binIter = binDataMapPerDimension.keySet().iterator();



			while(binIter.hasNext())
			{

				Integer recordNum = (Integer)binIter.next();
				Float value = binDataMapPerDimension.get(recordNum);

				float v = value.floatValue();

				int intv = (int)v;

				//System.err.println(" BInned val  " + value + "Bin number  " + startBin);
				if(intv == startBin)
				{   

					brushedRecordsList.add(recordNum);	
					// add the neighboring points as well, modify this

					for(int i=0; i<neighBorsList.size(); i++)
					{
						while(binIter.hasNext())
						{
							recordNum = (Integer)binIter.next();
							value = binDataMapPerDimension.get(recordNum);
							v = value.floatValue();
							intv = (int)v;

							int searchBin = neighBorsList.get(i);
							//recordNum = recordNum +i;
							if( searchBin == intv)
								brushedRecordsList.add(recordNum);	

						}
					}

				}
			}

			System.err.println("Brushed Records List ++++++++++++++++++  " +brushedRecordsList.size());

			for(int brushedRecord: brushedRecordsList)
			{
				parallelDisplay.setCurrentBrush(selectionBrush);
				selectionBrush.setBrushValue(brushedRecord, 1);


			}

			parallelDisplay.repaint();



		}
	}


	public void setComboBoxItems(int flag) {


		int prevDimensions= parallelDisplay.getNumAxes();
		comboBoxItems= new String[prevDimensions];
		System.err.println("Length"  + comboBoxItems.length);
		Vector<String> v=new Vector<String>();

		System.err.println(flag);


		for(int j=0;j< parallelDisplay.getNumAxes()-1;j++){


			String dim1Label= parallelDisplay.getAxisLabel(j);
			String dim2Label= parallelDisplay.getAxisLabel(j+1);
			String s = ""+ dim1Label + "--" + dim2Label;
			comboBoxItems[j] = s;
			v.add(comboBoxItems[j]);
			System.err.println("Dim  "  + parallelDisplay.axes[j].dimension);
		}
		List<String> list=Arrays.asList(comboBoxItems);
		v.addAll(list);
		comboBox = new JComboBox(new MyComboBoxModel(v));
		comboBox.setEditable(true);
		comboBox.setEnabled(true);

	}
	private class ComboBoxItemListener implements ItemListener{

		public void itemStateChanged(ItemEvent evt) {

			String item= evt.getItem().toString();
			int index=0;
			for(int i=0;i< parallelDisplay.axes.length-1;i++){
				if(comboBoxItems[i].equals(item))
					index=i;	
			}
			if (evt.getStateChange() == ItemEvent.SELECTED) {

				setBrushIndex(index);
				//commented
				//	parallelDisplay.setActiveRegion(brushIndex, brushIndex+1);

			}
		}
	}
	
	private class MyComboBoxModel extends AbstractListModel implements ComboBoxModel {

		String selection = null;
		private Vector<String> displayedObjects = new Vector<String>();

		public MyComboBoxModel(Vector<String> items){

			for(int i=0;i<items.size();i++)
			{      

				this.selection=items.get(i);
				displayedObjects.add(items.get(i));
				//   System.err.println("Labels "  + displayedObjects.get(i));
			}
			this.displayedObjects.removeAll(items);
			this.displayedObjects.addAll(items);

			this.fireContentsChanged(displayedObjects,0,displayedObjects.size()-1);
		}

		public Object getElementAt(int index) {
			return comboBoxItems[index];
		}

		public int getSize() {
			return comboBoxItems.length;
		}

		public void setSelectedItem(Object anItem) {
			selection = (String) anItem; // to select and register an
		} // item from the pull-down list

		// Methods implemented from the interface ComboBoxModel
		public Object getSelectedItem() {
			return selection; // to add the selection to the combo box
		}
	}	

	
	public void setBrushParameters(int sliderNum, int lowerBrushVal, int upperBrushVal){

		dragBrush=new Brush(data.getNumRecords(), Color.RED);
		//  int numBins = parallelDisplay.getHeight() - (parallelDisplay.getUpperMargin()+parallelDisplay.getLowerMargin());

		switch(sliderNum){

		// brush for principle direction
		case 0:
			//	float median = data.getAxisPair(brushIndex, brushIndex+1, parcoords).getParallelism(parcoords.getHeight()).x;
			//commented
			List<Double> distanceList = data.getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues(numBins);
			Collections.sort(distanceList);
			float highestVal=(float)distanceList.get(distanceList.size()-1).doubleValue();
			//float normalizedMedian = median/highestVal;
			//to normalize within 0-1 range divide by the highest val

			//	System.err.println("median = "+median+", normalizedMedian = "+normalizedMedian);

			//commented
			distanceList = data.getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues(numBins);

			for(int i=0;i<data.getNumRecords();i++){
				//commented
				float distanceVal=((float)distanceList.get(i).doubleValue());
				System.err.println("Distance  " +distanceVal);
				float normalizedDistanceVal = (((distanceVal)/highestVal)+1)*100;


				if(( lowerBrushVal<= (normalizedDistanceVal) && (normalizedDistanceVal) <= upperBrushVal)){
					dragBrush.setBrushValue(i, 1.0f);

				} 

				else {

					dragBrush.setBrushValue(i, 0.0f);
				}

			} 

			doBrushing(dragBrush);
			break;

			// brush for parallelism
		case 1:

			// median values between 0 and 1
			//commented
			float median = data.getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getParallelism(numBins).x;
			distanceList = data.getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues(numBins);
			Collections.sort(distanceList);
			highestVal=(float)distanceList.get(distanceList.size()-1).doubleValue();

			// normalize highest value for range between 0 to 1 becuase median is already normalized
			//  highestVal = highestVal/(2*(parcoords.getHeight()-(parcoords.getUpperMargin()+parcoords.getLowerMargin())));

			//commented
	      	float normalizedMedian = median;
			//to normalize within 0-1 range divide by the highest val

			//	System.err.println("median = "+median+", normalizedMedian = "+normalizedMedian);	      

			//commented
	//		distanceList = data.getAxisPair(brushIndex, brushIndex+1, parallelDisplay).getDistanceValues(numBins);
			for(int i=0;i<data.getNumRecords();i++)
			{

				// values between -numBins to + +numBins
				float distanceVal=(float)(distanceList.get(i).doubleValue());
				//  System.err.println("Distance  " +distanceVal);
				float normalizedDistanceVal = (distanceVal + numBins)/(float)(2*numBins);
				//commented
				float deviationFromMedian = normalizedMedian - normalizedDistanceVal;
				if(deviationFromMedian < 0)
					deviationFromMedian = -(deviationFromMedian);

				int int_deviationFromMedian = (int)(deviationFromMedian*100);

				//				System.err.println("Val " + int_deviationFromMedian);
				//				System.err.println("BrushVal "  +brushVal);

				//	System.err.println("median " + normalizedMedian + "  distance " + normalizedDistanceVal);
				//commented
				if(( lowerBrushVal<= (100-int_deviationFromMedian) && (100-int_deviationFromMedian) <= upperBrushVal )){
					dragBrush.setBrushValue(i, 1.0f);
				} else {
					dragBrush.setBrushValue(i, 0.0f);

				}

			} 
			doBrushing(dragBrush);

			break; 
			// brush for clumping
		case 2:

			//commented
			float avgClumping= data.getUniVariateSubSpaceConcentration(brushIndex, numBins, lowerBrushVal);

			if(minThreshold+lowerBrushVal < upperBrushVal)
				setSubSpaceParametersForBrushing(brushIndex, brushIndex+1, lowerBrushVal);

		}



		//			//brush for convergence
		//
		//			int[] degreeOfConvergence = getDegree(brushIndex, brushIndex+1, parcoords.getHeight() - 2 * borderV, true);
		//			AxisPair ap = ParallelDisplay.getInstance().getModel().getAxisPair(brushIndex, brushIndex+1);
		//			ap.initBinning();
		//			
		//			float highestConv = 0;
		////			float highestDiv = 0;
		//			for (int i = 0; i < degreeOfConvergence.length; i++) {
		//				if (degreeOfConvergence[i] > highestConv)
		//					highestConv = degreeOfConvergence[i];
		//				
		//			}
		//			
		//			for (int i = 0; i < ParallelDisplay.getInstance().getNumRecords(); i++) {
		//
		//				float val = (float)degreeOfConvergence[ap.value2pixel(ParallelDisplay.getInstance().getModel().getValue(i, brushIndex+1), false, false)] / highestConv;
		//				
		//				if (val * 100 >= brushVal) {
		//					dragBrush.setBrushValue(i, 1.0f);
		//				} else {
		//					dragBrush.setBrushValue(i, 0.0f);
		//				}
		//
		//			}
		//			doBrushing(dragBrush);
		//
		//			break;
		//			
		//		case 3:
		//			//brush for divergence
		//
		//			
		//			int[] degreeOfDivergence =  ParallelDisplay.getInstance().getModel().getDegree(brushIndex, brushIndex+1, MainFrame.mainFrame.getParallelDisplay().getHeight() - 2 * borderV,false);
		//			AxisPair ap2 = ParallelDisplay.getInstance().getModel().getAxisPair(brushIndex, brushIndex+1);
		//			ap2.initBinning();
		//			
		//		    float highestDiv1 = 0;
		//			for (int i = 0; i < degreeOfDivergence.length; i++) {
		//				
		//				if (degreeOfDivergence[i] > highestDiv1)
		//					highestDiv1 = degreeOfDivergence[i];
		//			}
		//			
		//			for (int i = 0; i < ParallelDisplay.getInstance().getNumRecords(); i++) {
		//
		//				float val = (float)degreeOfDivergence[ap2.value2pixel(ParallelDisplay.getInstance().getModel().getValue(i, brushIndex),  true, false)] / highestDiv1;
		//				
		//				if (val * 100 >= brushVal) {
		//					dragBrush.setBrushValue(i, 1.0f);
		//				} else {
		//					dragBrush.setBrushValue(i, 0.0f);
		//				}
		//
		//			}
		//			doBrushing(dragBrush);
		//
		//			break;
		//	
		//			
		//			
		//			
		//
	}

	public void setBrushIndex(int i){

		brushIndex=i;

		System.err.println("Brush Index  "  + brushIndex);

	}

	public int getBrushIndex(){

		return brushIndex;
	}

	

}
