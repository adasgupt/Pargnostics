package org.mediavirus.parvis.gui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class MatrixFrame  extends JFrame{

	MatrixMetaView matrixView;
	JRadioButton[] filterButton;
	JRadioButton[] multiDimButton;

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
		filterButton = new JRadioButton[2];

		filterButton[0] = new JRadioButton("Filter by information loss");
		filterButton[1] = new JRadioButton("Filter by visual complexity");

		filterBox.add(filterLabel);

		FilterRadioButtonHandler radiohandler=new FilterRadioButtonHandler();
		for(int i=0;i<filterButton.length;i++){
			filterButton[i].addItemListener(radiohandler);
			filterBox.add(filterButton[i]);
		}

		mainBox.add(filterBox);


		Box multiDimBox = new Box(BoxLayout.Y_AXIS);
		JLabel multiDimLabel = new JLabel("Build Multi-dimensional Plot");
		multiDimButton = new JRadioButton[3];

		multiDimButton[0] = new JRadioButton("Reduce semantic uncertainty");
		multiDimButton[1] = new JRadioButton("Reduce visual uncertainty");
		multiDimButton[2] = new JRadioButton("Reduce both");

		multiDimBox.add(multiDimLabel);

        JLabel blankLabel = new JLabel("");
        mainBox.add(blankLabel);

		MultiDimRadioButtonHandler multiDimradiohandler= new MultiDimRadioButtonHandler();
		for(int i=0;i<multiDimButton.length;i++){
			multiDimButton[i].addItemListener(multiDimradiohandler);
			multiDimBox.add(multiDimButton[i]);
		}

		mainBox.add(multiDimBox);
		suggestionPanel.add(mainBox);
		add(suggestionPanel, BorderLayout.EAST);


	}

	private class MultiDimRadioButtonHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e){

			if(e.getSource()== multiDimButton[0] && e.getStateChange()==ItemEvent.SELECTED)
			{



			}

			if(e.getSource()== multiDimButton[1] && e.getStateChange()==ItemEvent.SELECTED)
			{



			}

			if(e.getSource()== multiDimButton[2] && e.getStateChange()==ItemEvent.SELECTED)
			{



			}



		}

	}

	private class FilterRadioButtonHandler implements ItemListener{

		public void itemStateChanged(ItemEvent e){

			if(e.getSource()==filterButton[0] && e.getStateChange()==ItemEvent.SELECTED)
			{



			}

			if(e.getSource()==filterButton[1] && e.getStateChange()==ItemEvent.SELECTED)
			{



			}



		}

	}

}