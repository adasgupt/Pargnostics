package org.mediavirus.parvis.gui.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.PriorityQueue;

import org.mediavirus.parvis.gui.ParallelDisplay;
import org.mediavirus.parvis.gui.ParallelDisplay.Axis;
import org.mediavirus.parvis.gui.analysis.AxisPair.Metrics;
import org.mediavirus.parvis.model.DataSet;

public class BranchAndBoundOptimizer {

	private final class Path implements Comparable<Path> {
		int level = 0;
		float cost = 0;
		private float bound = -1;
		ArrayList<Integer> sequence;
		int lastDim;

		public Path(int dimension) {
			sequence = new ArrayList<Integer>(numDimensions);
			sequence.add(dimension);
			lastDim = dimension;
		}
		
		public Path(Path other, int dimension) {
			sequence = new ArrayList<Integer>(other.sequence);
			sequence.add(dimension);
			cost = other.cost+costs[other.lastDim][dimension];
			level = other.level+1;
			lastDim = dimension;
		}

		public float getBound() {
			if (bound < 0) {
				boolean mark[] = new boolean[numDimensions];
				Arrays.fill(mark, false);
				for (int dim : sequence)
					mark[dim] = true;
				mark[lastDim] = false;
				bound = cost;
				for (int i = 0; i < numDimensions; i++)
					if (!mark[i])
						bound += minCost[i];
			}
			return bound;
		}
		
		@Override
		public int compareTo(Path o) {
			return (int)Math.signum(getBound()-o.getBound());
		}
		
		@Override
		public boolean equals(Object o) {
			return (o instanceof Path) && (((Path)o).bound == bound);
		}
	}
	
	PriorityQueue<Path> queue = new PriorityQueue<Path>();
	
	float costs[][];

	float minCost[];
	
	int numDimensions;
	
	boolean axisPairInversions[];

	float minTotal = Float.POSITIVE_INFINITY;

	private Path minPath;
	
	private static final float TESTCOSTS[][] = {{Float.POSITIVE_INFINITY, 14, 4, 10, 20},
		 										{14, Float.POSITIVE_INFINITY, 7, 8, 7},
		 										{4, 5, Float.POSITIVE_INFINITY, 7, 16},
		 										{11, 7, 9, Float.POSITIVE_INFINITY, 2},
		 										{18, 7, 17, 4, Float.POSITIVE_INFINITY}};
	
	public BranchAndBoundOptimizer(ParallelDisplay display, boolean selected[], boolean maximize, int numBins) {
		
		DataSet model = display.getModel();
		numDimensions = display.getAxes().length;
		
		Axis[] currentAxes = display.getAxes();
		
		
		costs = new float[numDimensions][numDimensions];
		axisPairInversions = new boolean[numDimensions];
		boolean invert = false;
		
		Date start = new Date();
		
		for (int i = 0; i < numDimensions; i++) {
			costs[i][i] = Float.POSITIVE_INFINITY;
			for (int j = i + 1; j < numDimensions; j++) {
				
				
				float m = 0;
				float m_inverted = 0;
				for (Metrics metric : Metrics.values())
					if (selected[metric.ordinal()]) {
						if (maximize) {
							m +=  model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins);
							m_inverted += model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins);
						} else {
							m += (1 - model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins));
							m_inverted += (1 - model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins));
						}
					}
				if (m < m_inverted || !invert) {
					costs[i][j] = m;
					costs[j][i] = m;
					axisPairInversions[i] = false;
				} else {
					costs[i][j] = m_inverted;
					costs[j][i] = m_inverted;
					axisPairInversions[i] = true;
				}
//				System.err.print(m+"\t");
			}
//			System.err.println();
		}
		
		Date end = new Date();
		System.err.println("Matrix construction took "+(end.getTime()-start.getTime())+"ms");
		
		System.err.print("Inverted: ");
		for (int i = 0; i < numDimensions; i++)
			System.err.print(axisPairInversions[i]+", ");
		System.err.println();
		
		float min = Float.POSITIVE_INFINITY;
		for (int i = 0; i < numDimensions; i++)
			for (int j = 0; j < numDimensions; j++)
				if (costs[i][j] < min)
					min = costs[i][j];

		for (int i = 0; i < numDimensions; i++)
			for (int j = 0; j < numDimensions; j++)
				costs[i][j] -= min;
			
		makeMinCosts();
	}
	
//	public BranchAndBoundOptimizer(ParallelDisplay display, float[] weights, boolean maximize[], int numBins) {
//	
//		DataSet model = display.getModel();
//		numDimensions = model.getNumDimensions();
//		costs = new float[numDimensions][numDimensions];
//		axisPairInversions = new boolean[numDimensions];
//		boolean invert = false;
//		
//		Date start = new Date();
//		
//		for (int i = 0; i < numDimensions; i++) {
//			costs[i][i] = Float.POSITIVE_INFINITY;
//			for (int j = i + 1; j < numDimensions; j++) {
//				float m = 0;
//				float m_inverted = 0;
//				for (Metrics metric : Metrics.values())
//					if (weights[metric.ordinal()] != 0) {
//						if (maximize[metric.ordinal()]) {
//							m += weights[metric.ordinal()] * model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins);
//							m_inverted += weights[metric.ordinal()] * model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins);
//						} else {
//							m += weights[metric.ordinal()] * (1 - model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins));
//							m_inverted += weights[metric.ordinal()] * (1 - model.getAxisPair(i, j, display).getNormalizedMetric(metric, numBins));
//						}
//					}
//				if (m < m_inverted || !invert) {
//					costs[i][j] = m;
//					costs[j][i] = m;
//					axisPairInversions[i] = false;
//				} else {
//					costs[i][j] = m_inverted;
//					costs[j][i] = m_inverted;
//					axisPairInversions[i] = true;
//				}
////				System.err.print(m+"\t");
//			}
////			System.err.println();
//		}
//		
//		Date end = new Date();
//		System.err.println("Matrix construction took "+(end.getTime()-start.getTime())+"ms");
//		
//		System.err.print("Inverted: ");
//		for (int i = 0; i < numDimensions; i++)
//			System.err.print(axisPairInversions[i]+", ");
//		System.err.println();
//		
//		float min = Float.POSITIVE_INFINITY;
//		for (int i = 0; i < numDimensions; i++)
//			for (int j = 0; j < numDimensions; j++)
//				if (costs[i][j] < min)
//					min = costs[i][j];
//
//		for (int i = 0; i < numDimensions; i++)
//			for (int j = 0; j < numDimensions; j++)
//				costs[i][j] -= min;
//			
//		makeMinCosts();
//	}

	private void makeMinCosts() {
		float lowerBound = 0;
		minCost = new float[numDimensions];
		for (int i = 0; i < numDimensions; i++) {
			minCost[i] = Float.POSITIVE_INFINITY;
			for (int j = 0; j < numDimensions; j++) {
//				System.err.print(costs[i][j]+", ");
				if (costs[i][j] < minCost[i])
					minCost[i] = costs[i][j];
			}
//			System.err.println();
			lowerBound += minCost[i];
		}
		System.err.println("Lower bound: "+lowerBound);
	}
	
	/**
	 * Propagate axis inversions from AxisPair inversions to get per-axis inversions.
	 */
	public boolean[] getAxisInversions() {
		boolean[] inversions = new boolean[numDimensions];
		boolean invert = false;
		for (int i = 0; i < numDimensions; i++) {
			inversions[i] = invert;
			invert ^= axisPairInversions[i];
		}
		return inversions;
	}
	
	// this is purely for testing
	private BranchAndBoundOptimizer() {
		numDimensions = 5;
		costs = TESTCOSTS;
		makeMinCosts();
	}
	
	// based on http://max.cs.kzoo.edu/cs215/lectures/m6-heap-branch-bound.pdf
	// and http://www.academic.marist.edu/~jzbv/algorithms/Branch%20and%20Bound.htm
	public ArrayList<Integer> optimize() {
		for (int i = 0; i < numDimensions; i++) {
			Path p = new Path(i);
			queue.add(p);
		}
		System.err.print("B&B optimization: ");
		int queued = 0;
		int queued_complete = 0;
		int candidates = 0;
		while (!queue.isEmpty()) {
			Path p = queue.remove();
//			System.err.print("-");
			if (p.getBound() < minTotal) {
				for (int nextDim = 0; nextDim < numDimensions; nextDim++) {
					if (!p.sequence.contains(nextDim)) {
						Path u = new Path(p, nextDim);
						if (u.level == numDimensions-1) {
							if (u.cost < minTotal) {
								minTotal = u.cost;
								minPath = u;
//								System.err.print("!");
								candidates++;
							}
						} else if (u.getBound() < minTotal) {
							queue.add(u);
//							System.err.print("+");
							queued++;
							if (u.sequence.size() == numDimensions-1)
								queued_complete++;
						}
					}
				}
			}
		}
		System.err.println();
		System.err.println(queued+" sequences queued ("+queued_complete+" complete), "+candidates+" possible optimum solutions.");
		System.err.print("B&B minimum (fitness "+minTotal+"): ");
		for (int n : minPath.sequence)
			System.err.print(n+", ");
		System.err.println();
		
		return minPath.sequence;
	}

	public static void main(String args[]) {
		BranchAndBoundOptimizer bb = new BranchAndBoundOptimizer();
		bb.optimize();
	}
	
}
