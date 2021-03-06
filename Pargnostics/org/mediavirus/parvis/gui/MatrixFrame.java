package org.mediavirus.parvis.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.mediavirus.parvis.gui.MatrixMetaView.MetaMetrics;

public class MatrixFrame  extends JFrame{

	MatrixMetaView matrixView;
	JButton[] filterButton;
	JButton[] multiDimButton;

	public MatrixFrame(MatrixMetaView matrixView){

		setSize(1000, 1000);
		this.matrixView = matrixView;
		add(matrixView, BorderLayout.CENTER);
		setVisible(true);
		initComponents();
	}

	public void initComponents(){

		JPanel suggestionPanel = new JPanel();
		Box mainBox = new Box(BoxLayout.Y_AXIS);

		Box filterBox = new Box(BoxLayout.Y_AXIS);

		JLabel filterLabel = new JLabel("Control Uncertainty");
		filterButton = new JButton[2];

		filterButton[0] = new JButton("Filter by information loss");
		filterButton[1] = new JButton("Filter by visual complexity");

		filterBox.add(filterLabel);

		FilterRadioButtonHandler radiohandler=new FilterRadioButtonHandler();
		for(int i=0;i<filterButton.length;i++){
			filterButton[i].addActionListener(radiohandler);
			filterBox.add(filterButton[i]);
		}

		mainBox.add(filterBox);


		Box multiDimBox = new Box(BoxLayout.Y_AXIS);
		JLabel multiDimLabel = new JLabel("Build Multi-dimensional Plot");
		multiDimButton = new JButton[5];

		multiDimButton[0] = new JButton("Maximize information content");
		multiDimButton[1] = new JButton("Maximize visual saliency");
		multiDimButton[2] = new JButton("Maximize both");
		multiDimButton[3] = new JButton("Minimize information loss");
		multiDimButton[4] = new JButton("Maximize color effect");

		multiDimBox.add(multiDimLabel);

		JLabel blankLabel = new JLabel("");
		mainBox.add(blankLabel);

		MultiDimRadioButtonHandler multiDimradiohandler= new MultiDimRadioButtonHandler();
		for(int i=0;i<multiDimButton.length;i++){
			multiDimButton[i].addActionListener(multiDimradiohandler);
			multiDimBox.add(multiDimButton[i]);
		}

		mainBox.add(multiDimBox);
		suggestionPanel.add(mainBox);
		add(suggestionPanel, BorderLayout.EAST);


	}

	private class MultiDimRadioButtonHandler implements ActionListener{



		@Override
		public void actionPerformed(ActionEvent e) {

			if(e.getSource() == multiDimButton[0] )
			{
				matrixView.suggestAxisPairs(MetaMetrics.JointEntropy);
				//	System.err.println("Reduce semantic uncertainty");

			}

			if(e.getSource() == multiDimButton[1] )
			{
				matrixView.suggestAxisPairs(MetaMetrics.ImageEntropy);
				System.err.println("Reduce visual uncertainty");

			}

			if(e.getSource() == multiDimButton[2] )
			{

				matrixView.suggestAxisPairs(MetaMetrics.SumofJointImageEntropy);
				System.err.println("Reduce both");

			}
			if(e.getSource() == multiDimButton[3] )
			{

				matrixView.suggestAxisPairs(MetaMetrics.InformationLoss);
				System.err.println("Reduce info loss");

			}
			if(e.getSource() == multiDimButton[4] )
			{

				matrixView.suggestAxisPairs(MetaMetrics.DistanceEntropy);
				System.err.println("Suggst color");

			}

		}

	}

	private class FilterRadioButtonHandler implements ActionListener{

		public void actionPerformed(ActionEvent e){

			if(e.getSource()==filterButton[0])
			{

				matrixView.setEncodingFilter();

			}

			if(e.getSource()==filterButton[1])
			{

				matrixView.setDecodingFilter();


			}



		}

	}

}