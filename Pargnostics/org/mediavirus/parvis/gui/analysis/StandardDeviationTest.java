package org.mediavirus.parvis.gui.analysis;

public class StandardDeviationTest {
	

	public static double StandardDeviationMean ( double[] data ) 
	{ 
	// sd is sqrt of sum of (values-mean) squared divided by n - 1 
	// Calculate the mean 
	double mean = 0; 
	final int n = data.length; 
	if ( n < 2 ) 
	{ 
	return Double.NaN; 
	} 
	for ( int i=0; i<n; i++ ) 
	{ 
	mean += data[i]; 
	} 
	mean /= n; 
	// calculate the sum of squares 
	double sum = 0; 
	for ( int i=0; i<n; i++ ) 
	{ 
	final double v = data[i] - mean; 
	sum += v * v; 
	} 
	// Change to ( n - 1 ) to n if you have complete data instead of a sample. 
	return Math.sqrt( sum / ( n - 1 ) ); 
	} 
	
	
	public static void main(String args[]){
		
		double[] data ={1.2, 3.4, 5,6,7,5,8.56,5.45,2.33,4,9};
		
		double stddev = StandardDeviationMean(data);
		
		System.err.println(stddev);
	}

}
