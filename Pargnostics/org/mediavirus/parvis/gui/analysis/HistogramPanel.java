package org.mediavirus.parvis.gui.analysis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.mediavirus.parvis.gui.MetricsListener;
import org.mediavirus.parvis.gui.ParallelDisplay;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;

@SuppressWarnings("serial")
public class HistogramPanel extends JPanel implements MetricsListener {

	private int[] data;
	private float min;
	private float max;
	private AxisPair.Metrics metric = AxisPair.Metrics.Parallelism;
	private int axis1;
	private int axis2;
	private JComboBox metricCB;
	private Metrics metricSelection[] = {Metrics.Parallelism, Metrics.MutualInformation};
	
	public static HistogramPanel instance;
	
	private ParallelDisplay parallelDisplay;
	
	public HistogramPanel(ParallelDisplay parallelDisplay) {
		super();
		this.parallelDisplay = parallelDisplay;
		
		setBackground(Color.WHITE);
		
		metricCB = new JComboBox(metricSelection);
		metricCB.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setMetric(metricSelection[metricCB.getSelectedIndex()]);
			}
		});

		add(metricCB);
		
		instance = this;
	}

	private void setData() {
		if (parallelDisplay.getModel() == null || axis2 == 0)
			return;
		
		switch (metric) {
		case Parallelism:
//			data = parallelDisplay.getModel().getAxisPair(axis1, axis2, parallelDisplay).getDistanceHistogram(parall);
			
			break;
//		case AngleOfCrossings:
//			data = parallelDisplay.getModel().getAxisPair(axis1, axis2, parallelDisplay).getAngleOfCrossingHistogram(parallelDisplay.getHeight() - 2*parallelDisplay.getBorderV(), false);
//			break;
		}

		min = Float.POSITIVE_INFINITY;
		max = Float.NEGATIVE_INFINITY;
		if (data != null)
			for (int f : data) {
				if (f < min)
					min = f;
				if (f > max)
					max = f;
			}
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		if (data != null) {
			g.setColor(Color.BLACK);
			float w = getWidth();
			int borderV = parallelDisplay.getBorderV();
			if (metric == Metrics.MutualInformation) {
				int h = data.length;
				for (int y = 0; y < data.length; y++)
					//g.fillRect(0, (h - y) * 3 + borderV, (int)(data[y] / max * w), 3);
			          g.drawLine(0, (h - y)*5 + borderV, (int)(data[y] / max * w), (h - y)*5 + borderV);
			} else {
				int h = data.length/2;
				for (int y = 0; y < data.length/2; y++) {
					float val = (data[y * 2] + data[y * 2 + 1]) / 2;
					int linelen = (int) (val / max * w);
					g.drawLine(0, h - y + borderV, linelen, h - y + borderV);
				}
			}
		}
	}

	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(180, 400);
	}

	public void setMetric(Metrics newMetric) {
		metric = newMetric;
		setData();
	}

	public void setAxes(int a1, int a2, int bV) {
		axis1 = a1;
		axis2 = a2;
		setData();
	}

	@Override
	public void setCurrentAxis(int axis) {
		// TODO Auto-generated method stub
		
	}

}
