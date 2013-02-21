package org.mediavirus.parvis.gui.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class DataSpaceMetrics {

	
// from http://snippets.dzone.com/posts/show/4910
	
	 public double getPearsonCorrelation(double[] scores1, double[] scores2){
	        double result = 0;
	        double sum_sq_x = 0;
	        double sum_sq_y = 0;
	        double sum_coproduct = 0;
	        double mean_x = scores1[0];
	        double mean_y = scores2[0];
	        
	        for(int i=2;i<scores1.length+1;i+=1){
	            double sweep =Double.valueOf(i-1)/i;
	            double delta_x = scores1[i-1]-mean_x;
	            double delta_y = scores2[i-1]-mean_y;
	            sum_sq_x += delta_x * delta_x * sweep;
	            sum_sq_y += delta_y * delta_y * sweep;
	            sum_coproduct += delta_x * delta_y * sweep;
	            mean_x += delta_x / i;
	            mean_y += delta_y / i;
	        }
	        double pop_sd_x = (double) Math.sqrt(sum_sq_x/scores1.length);
	        double pop_sd_y = (double) Math.sqrt(sum_sq_y/scores1.length);
	        double cov_x_y = sum_coproduct / scores1.length;
	        result = cov_x_y / (pop_sd_x*pop_sd_y);
	        
	      //  System.err.println(" V ");
	        
	        result= (result+1)/2;
	        return result;
	    }
	 
	//http://whaticode.com/2010/05/24/a-java-implementation-for-shannon-entropy
	 
	 public double calculateShannonEntropy(List<Double> values) {
		  HashMap<Double, Integer> map = new HashMap<Double, Integer>();
		  // count the occurrences of each value
		  for (Double sequence : values) {
		    if (!map.containsKey(sequence)) {
		      map.put(sequence, 0);
		    }
		    map.put(sequence, map.get(sequence) + 1);
		  }

		  // calculate the entropy
		  Double result = 0.0;
		  for (Double sequence : map.keySet()) {
		    Double frequency = (double) map.get(sequence) / values.size();
		    result -= frequency * (Math.log(frequency) / Math.log(2));
		  }

		  return result;
		}

	 // testing
//	 public static void main(String args[]){
//		 
//		 double[] arr1 = {9.0, 8.0, 7.0, 6.0, 5.0};
//		 Double[] arr2 = {5.2, 5.1, 5.3, 5.1, 5.4};
//		 
//		 List<Double> doublearr2 = Arrays.asList(arr2);
//		 
//		 for(Double val: doublearr2){
//			 
//			 System.err.println("test value " + val);
//		 }
//		 
//		 Double entropy = calculateShannonEntropy(doublearr2);
//		 
//		 System.err.println("Entropy " + entropy);
//		 
//		 Double correlation = getPearsonCorrelation(arr1, arr2);
//		 correlation= (correlation+1)/2;
//		 System.err.println("Correlation  " + correlation);
//		 
//		 
//	 }
}
