package org.mediavirus.parvis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.JPanel;

import org.mediavirus.parvis.gui.PargnosticsPanel.Views;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;

@SuppressWarnings("serial")
public class DisplayMetricBarsPanel extends JPanel implements MetricsListener {

	private float[][] metricfillWidths;
	private int[][] degreePerDimension;
	public int previousNumDimensions;
    int height;
	
    int axis1;
    int axis2;
	public Color[] metricColor = { new Color(0x66C2A5),
			new Color(0xFC8D62), new Color(0x8DA0CB),
			new Color(0xE78AC3), new Color(0xA6D854),
			new Color(0xFFD92F), new Color(0xE5C494) };

	private static DisplayMetricBarsPanel metricsInstance;

	private ParallelDisplay parallelDisplay;
	private int maxDegree=-1;

	public DisplayMetricBarsPanel(ParallelDisplay display) {
		super();

		parallelDisplay = display;

//		System.err.println("Metrics Display");
		// createMetricsDisplay();
		Dimension d = new Dimension(200,480);
		height = d.height;
		setPreferredSize(d);
		setBackground(Color.white);
		setVisible(true);
		metricsInstance = this;
	}


	/*public void setMetric(int axis, float metrics[]) {

		if (metricfillWidths == null || previousNumDimensions != parallelDisplay.getModel().getNumDimensions()) {
			metricfillWidths = new float[parallelDisplay.getModel().getNumDimensions() - 1][metrics.length];
			previousNumDimensions = parallelDisplay.getModel().getNumDimensions();
		}

		for (int i = 0; i < metrics.length; i++) {
			metricfillWidths[axis][i] = (float) metrics[i] / Metrics.values()[i].getRange(parallelDisplay.getModel());
			// System.err.println("Diagnosing  " + i + metricfillWidth[i]);

		}
		//Diagnostics for negative parallelism value
		System.err.println("parallelism" +metricfillWidths[axis][2]);
		repaint();
	}*/

	public void setHistArray( int ax1, int degree[] ){

		if ( degreePerDimension == null || previousNumDimensions != parallelDisplay.getModel().getNumDimensions()) {
			 degreePerDimension = new int[parallelDisplay.getModel().getNumDimensions()][degree.length];
			 previousNumDimensions = parallelDisplay.getModel().getNumDimensions();
		}
		
		degreePerDimension[ax1] = degree;

		repaint();

	}

	public void paint(Graphics g) {

		super.paint(g);

		//if (metricfillWidths == null)
		//	return;

		// MetricsDisplayPanel comp=(MetricsDisplayPanel)c;
		//int numRows = metricfillWidths[0].length;
		int numCols = parallelDisplay.getNumAxes() - 1;
		//int widthOfRectangle = (getWidth()-(2*parallelDisplay.getDisplayUI().borderH)) / numCols;
		//int heightOfRectangle = getHeight() / numRows;
		//System.err.println("heightDisplay   " + heightOfRectangle);
		int startX = parallelDisplay.getDisplayUI().borderH;
		int startY = 0;
		String pattern= "##.#";
	/*	for (int i = 0; i < numCols; i++) {
			for (int j = 0; j < numRows; j++) {
				g.setColor(Color.gray);
				//	g.drawRect(startX, startY, widthOfRectangle, heightOfRectangle);
				//	g.drawRect(startX, startY, widthOfRectangle-26, heightOfRectangle);

				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(startX, startY, 26, heightOfRectangle);
				g.setColor(Color.darkGray);
				//	g.drawRect(startX+widthOfRectangle-26, startY, 26, heightOfRectangle);
				DecimalFormat myFormatter = new DecimalFormat(pattern);
				String stringVal = myFormatter.format(metricfillWidths[i][j]);
				g.setColor(Color.black);
				g.drawString(stringVal, startX, startY+14);
				startY = startY + heightOfRectangle;

			}

			startX = startX + widthOfRectangle;
			startY = 0;
		}

		System.err.println("REPAINTING");
		startX = parallelDisplay.getDisplayUI().borderH;
		startY = 0;
		for (int i = 0; i < metricfillWidths.length; i++) {
			for (int j = 0; j < numRows; j++) {
				int fillWidth = (int) (metricfillWidths[i][j] * (widthOfRectangle-26));

				//g.drawRect(startX, startY, fillWidth, heightOfRectangle);
				g.setColor(metricColor[j]);
				g.fillRect(startX+26, startY, fillWidth, heightOfRectangle);
				startY = startY + heightOfRectangle;

			}
			startX = startX + widthOfRectangle;
			startY = 0;
		}*/
		if(degreePerDimension != null) {

			//Calculating the global maximum for scaling the histograms
			g.setColor(Color.black);
			
			if( maxDegree == -1 )
			{
				for(int row = 0; row < parallelDisplay.getModel().getNumDimensions(); row++) {
					for(int col = row+1; col < parallelDisplay.getModel().getNumDimensions(); col++) {
						int degree[] = parallelDisplay.getModel().getAxisPair(col, row, parallelDisplay).getDegree(parallelDisplay.getHeight()-2*parallelDisplay.getBorderV(), false);	

						for(int f : degree)
							if(f > maxDegree )
								maxDegree = f;
					}
				}
				
			
			}
			
			
			int startXc = parallelDisplay.getDisplayUI().borderH;
			int startYc = 0;
			int histWidth = (getWidth()-(2*parallelDisplay.getDisplayUI().borderH)) / numCols;
				for (int i = 0; i < numCols; i++) {

					// drawLines
					g.drawLine( startXc+(i*histWidth), startYc, startXc+(i*histWidth), startYc+getHeight());
				
			      for( int k=0; k < degreePerDimension[i].length; k++ )
				   {
                     float len = (float)(degreePerDimension[i][k]) / maxDegree * histWidth;
			    	 int linelen = (int)(len);
					
                     g.drawLine( startXc +(histWidth*(i)),getHeight()-k, startXc +( histWidth*(i))+linelen,getHeight()-k );

					}
				
				}
		}
	}

	public static DisplayMetricBarsPanel getInstance() {
		return metricsInstance;
	}


	

	@Override
	public void setMetric(Metrics metric) {


	}

    public int getHeight(){
    	
    	return height;
    }
	@Override
	public void setCurrentAxis(int axis) {
		// TODO Auto-generated method stub

	}

}
