package org.mediavirus.parvis.gui.analysis;

import org.mediavirus.parvis.model.DataSet;

/**
 * 
 * This class is for processing the binned data for checking
 * whether it satisfies the requirements for being k-anonymous on
 * each axis which is a quasi-identifier.
 * @author Aritra Dasgupta
 *
 */
public class Kanonymity {

	DataSet model;

	AxisPair axispair;

	int originalnumBins;

	int modifiednumBins;

	int[][] histogram;

	public Kanonymity(DataSet pm, AxisPair ap, int numBins){

		model=pm;
		axispair=ap;
		originalnumBins=numBins;
	}

 
/** 
 * returns the modified binned data for each dimension. Assumption now is that
 * all dimensions are quasi-identifiers.This will change: we have to 
 * between sensitive attributes and quasi-identifiers.
 * Second assumption is all individual attributes are quasi-identifiers, but
 * we have to consider multi-dimensional quasi-identifiers too.
 * @return histogram
 */


	public int[][] getkanonymity(){

		histogram= new int[model.getNumDimensions()][originalnumBins];

		//get the original histogram based on initital binning.

		for(int i=0;i<model.getNumDimensions();i++)

		{  

			histogram[i]=model.getHistogram(i, originalnumBins);

		}

		for(int j=0;j<histogram.length;j++)
		{

			int[] hist=histogram[j];  

			for(int m=0;m<hist.length;m++)
			{

				int current =m;

				int sum=hist[m];

				//assuming k=5
				while(sum<5){

					hist[m]=sum+hist[current+1];

					current++;

				}
               m=current;
             }
			
			histogram[j]=hist;
         
		}


      return histogram;


	}













}
