package org.mediavirus.parvis.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.mediavirus.parvis.gui.MatrixMetaView.MetaMetrics;
import org.mediavirus.parvis.gui.MatrixMetaView.SortMetrics;
import org.mediavirus.parvis.gui.analysis.AxisPair;
import org.mediavirus.parvis.model.DataSet;

public class ParameterizedDisplay extends JPanel implements MouseListener, MouseMotionListener {
	


	/*
	 *  Parameters that control the computation of metrics-width, height of screen
	 */
	Point2D.Float param = new Point2D.Float(200, 600);
	DataSet data = null;
	//private BufferedImage[] imgArray;

	private boolean useColor = false;

	FileWriter entropyOutput = null;
	BufferedWriter bw = null;
	ParallelDisplay mainDisplay;

	protected static class AxisPairMetrics implements Comparable, Cloneable{

		private int axis1;
		private int axis2;
        private float jointEntropy;
		private float grayEntropy;
		private float colorEntropy;
		private float distanceEntropy;
		private float weightedGrayEntropy;
		private float weightedColorEntropy;
		private float klDiv;

		private BufferedImage img;

		public AxisPairMetrics(int dim1, int dim2){
			axis1 = dim1;
			axis2 = dim2;

		}
		public void setAxes(int dim1, int dim2){
			axis1 = dim1;
			axis2 = dim2;

		}
		public int getDimension1(){
			return axis1;
		}
		public int getDimension2(){
			return axis2;
		}
		public void setJointEntropy(float je){
			jointEntropy = je;

		}
		public void setGrayEntropy(float pe){
			grayEntropy = pe;

		}
		public void setColorEntropy(float pe){
			colorEntropy = pe;

		}
		public void setDistanceEntropy(float de){
			distanceEntropy =de;

		}
		public void setWeightedGrayEntropy(float de){
			weightedGrayEntropy =de;

		}
		public void setWeightedColorEntropy(float de){
			weightedColorEntropy =de;

		}
		public float getJointEntropy(){
			return jointEntropy;
		}

		public float getGrayEntropy(){
			//System.err.println(" Gray entropy   " +grayEntropy);
			return grayEntropy;
		}
		public float getColorEntropy(){
			return colorEntropy;
		}


		public float getDistanceEntropy(){
			return distanceEntropy;
		}

		public float getWeightedGrayEntropy(){
			return weightedGrayEntropy;
		}

		public float getWeightedColorEntropy(){
			return weightedColorEntropy;
		}
		
		
		
		public void setKLDivergence(float kld){
			klDiv = kld;
		}
		
		public float getKLDivergence(){
			return klDiv;
		}

		
		public void storeImage(BufferedImage bufferImg){

			img = bufferImg;
		}
		public BufferedImage getImage(){

			return img;

		}


		@Override
		public int compareTo(Object arg0) {
			// TODO Auto-generated method stub.
			return 0;
		}

	}


	private ArrayList<AxisPairMetrics> metricsList = new ArrayList<AxisPairMetrics>();

	private ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();


	public static final float RECORD_COLOR_HIGH[] = {251/255f, 106/255f, 74/255f, .9f};

	public static final float RECORD_COLOR_MID[] = {0.874509804f, 0.760784314f, 0.490196078f, .1f};

	public static final float RECORD_COLOR_LOW[] = {49/255f, 130/255f, 189/255f, .9f};

	//a hack to stop calling the paint code repeatedly
	int callCount= 0;

	public static int totalNumberOfCombinations=0;

	/*
	 *  drawing mode can be PC or SP
	 */
	String drawingMode = "PC";
	private int numDimensions;

	/*
	 * The axis pairs
	 */
	private int dimension1;
	private int dimension2;
	/*
	 * parameters for scaling
	 */
	//	private float axisOffset1;
	//	private float axisOffset2;
	//	private float scale1;
	//	private float scale2;

	/*
	 * lists for metrics
	 */
	private ArrayList<Float> distanceEntropyList = new ArrayList<Float>();
	private ArrayList<Float> jointEntropyList = new ArrayList<Float>();
	private ArrayList<Float> pixelEntropyList = new ArrayList<Float>();

	public static final double LOG_BASE_2 = Math.log(2);

	//	BufferedImage bufferImg = null ;

	public ParameterizedDisplay(){

		addMouseListener(this);
		addMouseMotionListener(this);
	}


	//	public void initBinning(int numBins) {
	//
	//		numDimensions = data.getNumDimensions();
	//		axisOffset1 = data.getMinValue(dimension1);
	//		axisOffset2 = data.getMinValue(dimension2);
	//		scale1 = numBins / (data.getMaxValue(dimension1) - data.getMinValue(dimension1));
	//		scale2 = numBins / (data.getMaxValue(dimension2) - data.getMinValue(dimension2));
	//	}

	public void initialize(DataSet data, ParallelDisplay mainDisplay)
	{
		this.data = data;
		this.mainDisplay = mainDisplay;
		totalNumberOfCombinations = (data.getNumDimensions()*(data.getNumDimensions()-1))/2;
		//imgArray = new BufferedImage[data.getNumDimensions()*data.getNumDimensions()];
	//	System.err.println("Data dimensions  " +data.getNumDimensions());
		
		System.err.println("Repaint");
		repaint();


		//computeEntropy(imgArray[0]);

	}


	public void paint(Graphics g){

		super.paint(g);
		Graphics2D g2= (Graphics2D)g;




		//System.err.println("Painting");



		if(data!=null){


			for(int dim1=1; dim1<data.getNumDimensions(); dim1++)
			{
				for(int dim2=0; dim2< dim1; dim2++)
				{
					callCount++;
					if(callCount>= 2*totalNumberOfCombinations)
						return;
					else
					{

						//drawScatterplot(g2, data, dim1, dim2);
						drawParallelCoordinatesplot(g2, data, dim1, dim2);

					}
				}

			}

			processMetrics();


			//System.err.println("Call count " +callCount);

		}
		else 
			return;
	

	}


	/**
	 * TODO Put here a description of what this method does.
	 *
	 */
	public void processMetrics() {
		try{
			// Compute entropy and Create file 
			//FileWriter fstream = new FileWriter("sortorder.txt");
			//FileWriter fstream = new FileWriter("entropy1.csv");

			//BufferedWriter out = new BufferedWriter(fstream);

			System.err.println("Image list size " +imageList.size());
			Collections.sort(metricsList, new SortMetrics(MetaMetrics.ColorEntropy));
			String heading = "Axis pair"+ ","+"gray entropy"+","+"color entropy"+","+"weighted gray"+","+"weighted color"+","+"joint entropy";
		//	out.write(heading);
		//	out.newLine();
			
//			for(int i=imageList.size()-1; i>(imageList.size()-10); i--)
//			{
			for(int i= 0; i<10; i++)
				{
				AxisPairMetrics metricObject = metricsList.get(i);
				String label1 = data.getAxisLabel(metricObject.getDimension1());
				String label2 = data.getAxisLabel(metricObject.getDimension2());
				
				Graphics g1 = metricObject.getImage().getGraphics();
				g1.setFont(g1.getFont().deriveFont(20f));
				g1.drawString(data.getAxisLabel(metricObject.getDimension1())+ "  "+ data.getAxisLabel(metricObject.getDimension2()), 230, 100);
				g1.dispose();

				File outputfile = new File(i+"saved" + metricObject.getDimension1()+metricObject.getDimension2() + ".png");
				ImageIO.write(metricObject.getImage(), "png", outputfile);
			//	out.write(i+"saved" + metricObject.getDimension1()+metricObject.getDimension2() + ".png");
			//	out.newLine();

				//what weights to choose for the entropy metric?
				
			//	System.err.println("Distance Entropy +++++++++  "+metricObject.getDistanceEntropy());

//				double grayentropy = metricObject.getGrayEntropy();
//				//	double weightedGrayMetric = ((2*(metricObject.getDistanceEntropy()/10))+metricObject.getGrayEntropy())/3;
//				double weightedGrayMetric = (((metricObject.getDistanceEntropy()/100))+(1-metricObject.getGrayEntropy()))/2;
//
//				double colorEntropy = metricObject.getColorEntropy();
//				//	double weightedColorMetric = ((2*(metricObject.getDistanceEntropy()/10))+metricObject.getColorEntropy())/3;
//				double weightedColorMetric = (((metricObject.getDistanceEntropy()/10))+(1-metricObject.getColorEntropy()))/2;
//
//
//				double jointEntropy = metricObject.getJointEntropy();

//
//				String text= label1+" "+label2+","+grayentropy+","+colorEntropy+","+weightedGrayMetric+","+weightedColorMetric+","+jointEntropy;
			//	out.write(text);
			//	out.newLine();
				

			}

		//	out.close();



		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	protected Color getRecordColor(float point1, float point2, int numBins){

		float norm = (float)((point1/(float)numBins));
		float mult[] = {RECORD_COLOR_HIGH[0] * norm + RECORD_COLOR_LOW[0]*(1-norm), RECORD_COLOR_HIGH[1] * norm + RECORD_COLOR_LOW[1]*(1-norm), 
				RECORD_COLOR_HIGH[2] * norm + RECORD_COLOR_LOW[2]*(1-norm), 0.2f};

		Color color = new Color(mult[0], mult[1], mult[2]);

		return color;

	}

	public double computeEntropy(BufferedImage bufferImg) {


		//	System.err.println("Called Entropy");
		int colorHistogramArray[] = computeImageHistogram(bufferImg);


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
			
		//System.err.println(" Test Entropy    *******  " +sumOfEntropy);


		}
		return sumOfEntropy;

	}

	int[] computeImageHistogram(BufferedImage bufferImg) {

		// System.out.println("Testing"+ bufferImg.getHeight());
		//		int imageWidth = bufferImg.getWidth();
		int imageHeight = bufferImg.getHeight();
		double sumOfColorVal = 0;
		//		int numPixels = imageHeight * imageWidth;
		int sizeOfArray = 4096;
		int colorHistogramArray[] = new int[sizeOfArray];
		/**
		 * the colorval that is used to compute the histogram
		 */
		int colorVal =0;

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



		//BufferedImage bufferImg = componentToImage(this);

		for (int x = 0; x < bufferImg.getWidth(); x++) {

			for (int y = 0; y < bufferImg.getHeight() ; y++) {

				//int colorVal = (bufferImg.getRGB(x, y) & (0xFF));

				//				int Rval = ((bufferImg.getRGB(x, y)<<16)& (0xFF));
				//				int Gval = ((bufferImg.getRGB(x, y)<<8) & (0xFF));
				//				int Bval = ((bufferImg.getRGB(x, y))& (0xFF));


				int shiftedVal = ((bufferImg.getRGB(x, y))&(0xFF))>>4;

			colorVal= (shiftedVal << 8) + (shiftedVal << 4)+ shiftedVal;


			if(useColor)
				//	colorVal = Rval+Gval+Bval;
				colorVal= (shiftedVal << 8) + (shiftedVal << 4)+ shiftedVal;
			else
				//colorVal= bufferImg.getRGB(x, y) & (0xFF);
				colorVal = shiftedVal;

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

	public float computeDistanceEntropy(int axis1, int axis2){


		int numBins = (int)param.y;
		int[] distanceHistogram = mainDisplay.getModel().getAxisPair(axis1, axis2, mainDisplay).getDistanceHistogram(numBins, false);

		double probabilityValue = 0;
		double logProbabilityValue = 0;
		double entropy = 0;
		float sumEntropy = 0;
		
		for (int i = 0; i < distanceHistogram.length; i++) {
			probabilityValue = distanceHistogram[i] /(float)distanceHistogram.length;
			if (probabilityValue > 0)
				logProbabilityValue = (Math.log(probabilityValue))/ LOG_BASE_2;
			entropy = -(probabilityValue * logProbabilityValue);
			sumEntropy =(float)( sumEntropy + entropy);
		}

		System.err.println("Distance Entropy   " + sumEntropy);
		return sumEntropy;

	}




	//	private static int[] computeImageHistogram(BufferedImage image) {
	//		
	//		byte[] pixels = (byte[])image.getRaster().getDataElements(0, 0, image.getWidth(), image.getHeight(), null);
	//	//	int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
	//		final int width = image.getWidth();
	//		final int height = image.getHeight();
	//		final boolean hasAlphaChannel = image.getAlphaRaster() != null;
	//		//set the histogram array
	//		int colorHistogramArray[] = new int[256];
	//
	//		//int[][] result = new int[height][width];
	//		if (hasAlphaChannel) {
	//			final int pixelLength = 4;
	//			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	//				int argb = 0;
	//				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
	//				argb += ((int) pixels[pixel + 1] & 0xff); // blue
	//				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
	//				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
	//				// result[row][col] = argb;
	//				colorHistogramArray[argb]++;
	//				col++;
	//				if (col == width) {
	//					col = 0;
	//					row++;
	//				}
	//
	//
	//			}
	//		} else {
	//			final int pixelLength = 3;
	//			for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
	//				int argb = 0;
	//				argb += -16777216; // 255 alpha
	//				argb += ((int) pixels[pixel] & 0xff); // blue
	//				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
	//				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
	//				//  result[row][col] = argb;
	//				colorHistogramArray[argb]++;
	//				col++;
	//				if (col == width) {
	//					col = 0;
	//					row++;
	//				}
	//
	//
	//			}
	//		}
	//
	//		return colorHistogramArray;
	//	}



	
	private void drawParallelCoordinatesplot(Graphics g,DataSet data, int axis1, int axis2){

		//float axisOffset1 = parallelDisplay.getAxisOffset(axis1);
		//float axisOffset2 = parallelDisplay.getAxisOffset(axis2);
		//float scale1 = parallelDisplay.getAxisScale(axis1);
		//float scale2 = parallelDisplay.getAxisScale(axis2);
		Graphics2D ig = (Graphics2D)g;
		Graphics2D g2 = (Graphics2D)g;


		String[] dimNamesArray = new String[2];

		int w = this.getWidth();

		int h = this.getHeight();

		BufferedImage bufferImg = new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
		bufferImg = (BufferedImage)(this.createImage(w, h));



		//setting up the BufferedImage properties
		ig = bufferImg.createGraphics();
		ig.setColor(this.getBackground());
		ig.fillRect(0, 0, this.getWidth(), this.getHeight());

		float scale1 = (data.getMaxValue(axis1) - data.getMinValue(axis1));
		float scale2 = (data.getMaxValue(axis2) - data.getMinValue(axis2));
		float axisOffset1 = data.getMinValue(axis1);
		float axisOffset2 = data.getMinValue(axis2);

		//Color code background for Crossings:Low medium and high
		//		if(filterFlag !=-1)
		//		{
		//			float val = getFilteredValue( axis1, axis2 );
		//
		//			//Color code backgorund for Crossings:Low medium and high
		//			Color backGroundColor = getColor(val);
		//
		//
		//			g2d.setColor(backGroundColor);
		//			g2d.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);
		//
		//		}


        ig.drawLine(0, 0, 0, (int)param.y);
        ig.drawLine((int)param.x, 0, (int)param.x, (int)param.y);

		/*
		 * the loop for rendering all the lines in parallel coordinates
		 */
		for(float[]dataRow : data){

			int v1 = (int)((dataRow[axis1] - axisOffset1) * (param.y) / scale1);
			int v2 = (int)((dataRow[axis2] - axisOffset2) * (param.y) / scale2);
			if(useColor)
				ig.setColor(getRecordColor(v1,v2, (int)param.y));
			else
				ig.setColor(new Color(120,120,120,160));
			ig.drawLine(0, v1, (int)param.x, v2);	
		}

		g2.drawImage(bufferImg, null, 0, 0);
		imageList.add(bufferImg);

		AxisPairMetrics metricObject = new AxisPairMetrics(axis1, axis2);

		metricObject.setDistanceEntropy(computeDistanceEntropy(axis1,axis2));
		metricObject.setJointEntropy(mainDisplay.getModel().getAxisPair(axis1, axis2, mainDisplay).getJointEntropy((int)param.y));
		metricObject.setGrayEntropy((float)computeEntropy(bufferImg));
		metricObject.setColorEntropy((float)computeEntropy(bufferImg));
		metricObject.setWeightedColorEntropy(((metricObject.getDistanceEntropy()/100)+(2*(1-(metricObject.getGrayEntropy()/10))))/3);
		metricObject.setKLDivergence(getKLDivergence(axis1, axis2));
		
		//setUseColor(true);

		metricObject.storeImage(bufferImg);


		metricsList.add(metricObject);


		/*
		 * Verify by copying and saving the image.
		 */




		//output to the file a line



		//		try {
		//			// retrieve image
		//
		//			File outputfile = new File("saved" + axis1+axis2 + ".png");
		//			ImageIO.write(bufferImg, "png", outputfile);
		//
		//			//            double entropy = computeEntropy(bufferImg);
		//			//            String text= " "+entropy;
		//			//            bw.write(text);
		//			//            
		//			//            System.err.println("Entropy " +entropy);
		//
		//		} catch (IOException e) {
		//
		//		}


		//System.err.println("Images created");

	}
	
	private void drawScatterplot(Graphics g,DataSet data, int axis1, int axis2){

		//float axisOffset1 = parallelDisplay.getAxisOffset(axis1);
		//float axisOffset2 = parallelDisplay.getAxisOffset(axis2);
		//float scale1 = parallelDisplay.getAxisScale(axis1);
		//float scale2 = parallelDisplay.getAxisScale(axis2);
		Graphics2D ig = (Graphics2D)g;
		Graphics2D g2 = (Graphics2D)g;


		String[] dimNamesArray = new String[2];

		int w = this.getWidth();

		int h = this.getHeight();

		BufferedImage bufferImg = new BufferedImage(this.getWidth(), this.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
		bufferImg = (BufferedImage)(this.createImage(w, h));



		//setting up the BufferedImage properties
		ig = bufferImg.createGraphics();
		ig.setColor(this.getBackground());
		ig.fillRect(0, 0, this.getWidth(), this.getHeight());

		float scale1 = (data.getMaxValue(axis1) - data.getMinValue(axis1));
		float scale2 = (data.getMaxValue(axis2) - data.getMinValue(axis2));
		float axisOffset1 = data.getMinValue(axis1);
		float axisOffset2 = data.getMinValue(axis2);

		//Color code background for Crossings:Low medium and high
		//		if(filterFlag !=-1)
		//		{
		//			float val = getFilteredValue( axis1, axis2 );
		//
		//			//Color code backgorund for Crossings:Low medium and high
		//			Color backGroundColor = getColor(val);
		//
		//
		//			g2d.setColor(backGroundColor);
		//			g2d.fillRect( locX, locY, scatterInstanceWidth, scatterInstanceHeight);
		//
		//		}

		ig.setColor(new Color(0,0,0));
         ig.drawLine(0, 0, 0, (int)param.y);
         ig.drawLine(0, (int)param.y, (int)param.x, (int)param.y);

		/*
		 * the loop for rendering all the lines in parallel coordinates
		 */
		for(float[]dataRow : data){

			int v1 = (int)((dataRow[axis1] - axisOffset1) * (param.x) / scale1);
			int v2 = (int)((dataRow[axis2] - axisOffset2) * (param.y) / scale2);
//			if(useColor)
//				ig.setColor(getRecordColor(v1,v2, (int)param.y));
//			else
				ig.setColor(new Color(0,0,0));
			
			//ig.drawLine((int)(v1), (int)(param.y-v2), (int)(v1)+2,(int)(param.y-v2)+2);	
			ig.drawOval((int)(v1), (int)(param.y-v2), 2, 2);
		}

		g2.drawImage(bufferImg, null, 0, 0);
		imageList.add(bufferImg);

		AxisPairMetrics metricObject = new AxisPairMetrics(axis1, axis2);

		metricObject.setDistanceEntropy(computeDistanceEntropy(axis1,axis2));
		metricObject.setJointEntropy(mainDisplay.getModel().getAxisPair(axis1, axis2, mainDisplay).getJointEntropy((int)param.y));
		metricObject.setGrayEntropy((float)computeEntropy(bufferImg));
		metricObject.setColorEntropy((float)computeEntropy(bufferImg));
		metricObject.setWeightedColorEntropy(((metricObject.getDistanceEntropy()/10)+(2*(1-metricObject.getGrayEntropy())))/3);
		//setUseColor(true);

		metricObject.storeImage(bufferImg);


		metricsList.add(metricObject);


		/*
		 * Verify by copying and saving the image.
		 */




		//output to the file a line



//				try {
//					// retrieve image
//		
//					File outputfile = new File("saved" + axis1+axis2 + ".png");
//					ImageIO.write(bufferImg, "png", outputfile);
//		
//					//            double entropy = computeEntropy(bufferImg);
//					//            String text= " "+entropy;
//					//            bw.write(text);
//					//            
//					//            System.err.println("Entropy " +entropy);
//		
//				} catch (IOException e) {
//		
//				}


		//System.err.println("Images created");

	}
	
	public float getKLDivergence(int axis1, int axis2){
		
		int numPixelBins = (int)param.y;
		int numDataBins = data.getNumRecords();
		int[][] imageHist= data.get2DHistogram(axis1, axis2, (int)param.y);
		int[][] dataHist = data.get2DHistogram(axis1, axis2, data.getNumRecords());
		
		
		float[] imageProbabilityArray = new float[data.getValues().size()];
		float[] dataProbabilityArray =  new float[data.getValues().size()];
		
		for(int recordNum=0; recordNum<data.getValues().size(); recordNum++)
		{
			float val1 = data.getValues().get(recordNum)[axis1];
			float val2 = data.getValues().get(recordNum)[axis2];
			val1 = val1 - data.getMinValue(axis1);
			val2 = val2 - data.getMinValue(axis2);
			
			int pixelbin1 = (int) (numPixelBins * (val1 / (data.getMaxValue(axis1)-data.getMinValue(axis1))));
			int pixelbin2 = (int) (numPixelBins * (val2 / (data.getMaxValue(axis2)-data.getMinValue(axis2))));
			
			int databin1 = (int) (numDataBins * (val1 / (data.getMaxValue(axis1)-data.getMinValue(axis1))));
			int databin2 = (int) (numDataBins * (val2 / (data.getMaxValue(axis2)-data.getMinValue(axis2))));
			
			imageProbabilityArray[recordNum] = imageHist[pixelbin1][pixelbin2]/(float)(imageHist.length);
			dataProbabilityArray[recordNum] =  dataHist[databin1][databin2]/(float)(dataHist.length);
			
			
		}
		
		
		float klDiv = 0f;

	      for (int i = 0; i < 	imageProbabilityArray.length; ++i) {
	        if (imageProbabilityArray[i] == 0) { continue; }
	        if (dataProbabilityArray[i] == 0.0) { continue; } 

	      klDiv += imageProbabilityArray[i] * Math.log( imageProbabilityArray[i] / dataProbabilityArray[i] );
	      }

	      System.err.println(" KLDiv    *********************  " +klDiv);
	      klDiv= (float)(klDiv/LOG_BASE_2);
		
	    return klDiv;
	}

	public ArrayList<BufferedImage> getBufferedImageList(){
		return imageList;
	}

	public void setUseColor(Boolean b){
		useColor = true;


	}


	@Override
	public void mouseDragged(MouseEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		//		double e= 0;


	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}



	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	public ArrayList<AxisPairMetrics> getMetricsList(){

		return metricsList;
	}






}
