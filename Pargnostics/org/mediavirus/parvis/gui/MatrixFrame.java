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
		multiDimButton = new JButton[3];

		multiDimButton[0] = new JButton("Reduce semantic uncertainty");
		multiDimButton[1] = new JButton("Reduce visual uncertainty");
		multiDimButton[2] = new JButton("Reduce both");

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

		}

	}

	private class FilterRadioButtonHandler implements ActionListener{

		public void actionPerformed(ActionEvent e){

			if(e.getSource()==filterButton[0])
			{



			}

			if(e.getSource()==filterButton[1])
			{



			}



		}

	}

}