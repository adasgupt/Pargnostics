package org.mediavirus.parvis.gui;

import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;

public interface MetricsListener {

	public void setMetric(Metrics metric);

	public void setCurrentAxis(int axis);
	
}
