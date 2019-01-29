import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class KMapSolver implements ActionListener {
	static int[] i1Values = new int[]{0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1};
	static int[] i2Values = new int[]{0,0,0,0,1,1,1,1,0,0,0,0,1,1,1,1};
	static int[] i3Values = new int[]{0,0,1,1,0,0,1,1,0,0,1,1,0,0,1,1};
	static int[] i4Values = new int[]{0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1};
	static int[] o1Values = new int[16];
	static JButton[] buttonList = new JButton[16];
	static JButton[] buttonList2 = new JButton[16];
	static JTextArea solveSpace = new JTextArea(5, 50);
	private int[][] firstList = new int[16][7];
	private int[][] secondList = new int[32][7];
	private int[][] thirdList = new int[16][7];
	private int[][] secondListMins = new int[32][9];
	private int[][] thirdListMins = new int[16][9];
	private int firstListLength = 0;
	private int secondListLength = 0;
	private int thirdListLength = 0;
	private int termsCount = 0;
	private int[][] chart = new int[16][16];
	private int[] finalTerms = new int[16];
	private int finalTermsCount = 0;
	private String answerString = "";

	public static void main(String[] args) {
		//Use the event dispatch thread for Swing components
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new KMapSolver();         
			}
		});
	}

	public KMapSolver(){
		JFrame frame = new JFrame("FrameDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("K-Map Solver");
		frame.setSize(300,300);

		JPanel gui = new JPanel(new BorderLayout(5,5));

		JPanel truthTablePane = new JPanel(new GridLayout(17,6));

		JLabel label0 = new JLabel(" ");
		truthTablePane.add(label0);
		JLabel label1 = new JLabel("A");
		truthTablePane.add(label1);
		JLabel label2 = new JLabel("B");
		truthTablePane.add(label2);
		JLabel label3 = new JLabel("C");
		truthTablePane.add(label3);
		JLabel label4 = new JLabel("D");
		truthTablePane.add(label4);
		JLabel label5 = new JLabel("X");
		truthTablePane.add(label5);

		for (int i = 0; i < 16; i++){
			JButton but = new JButton(String.valueOf(o1Values[i]));
			JButton but2 = new JButton(String.valueOf(o1Values[i]));
			but.setActionCommand("1 5 " + String.valueOf(i));
			but2.setActionCommand("0 5 " + String.valueOf(i));
			but.addActionListener(this);
			but2.addActionListener(this);
			buttonList[i] = but;
			buttonList2[i] = but2;
		}

		for (int i = 0; i < 16; i++){
			addLabel(truthTablePane, String.valueOf(i));
			addButton(truthTablePane, String.valueOf(i1Values[i]), 0, "1", i);
			addButton(truthTablePane, String.valueOf(i2Values[i]), 0, "2", i);
			addButton(truthTablePane, String.valueOf(i3Values[i]), 0, "3", i);
			addButton(truthTablePane, String.valueOf(i4Values[i]), 0, "4", i);
			truthTablePane.add(buttonList2[i]);
		}

		JPanel kMapPane = new JPanel(new GridLayout(5,5));

		JLabel klabel0 = new JLabel(" ");
		kMapPane.add(klabel0);
		JLabel klabel1 = new JLabel("A'B'");
		kMapPane.add(klabel1);
		JLabel klabel2 = new JLabel("A'B");
		kMapPane.add(klabel2);
		JLabel klabel3 = new JLabel("AB");
		kMapPane.add(klabel3);
		JLabel klabel4 = new JLabel("AB'");
		kMapPane.add(klabel4);

		addLabel(kMapPane, "C'D'");
		kMapPane.add(buttonList[0]);
		kMapPane.add(buttonList[1]);
		kMapPane.add(buttonList[3]);
		kMapPane.add(buttonList[2]);
		addLabel(kMapPane, "C'D");
		kMapPane.add(buttonList[4]);
		kMapPane.add(buttonList[5]);
		kMapPane.add(buttonList[7]);
		kMapPane.add(buttonList[6]);
		addLabel(kMapPane, "CD");
		kMapPane.add(buttonList[12]);
		kMapPane.add(buttonList[13]);
		kMapPane.add(buttonList[15]);
		kMapPane.add(buttonList[14]);
		addLabel(kMapPane, "CD'");
		kMapPane.add(buttonList[8]);
		kMapPane.add(buttonList[9]);
		kMapPane.add(buttonList[11]);
		kMapPane.add(buttonList[10]);

		JPanel bottomStuff = new JPanel(new BorderLayout(5,5));
		
		JPanel solveButtonPane = new JPanel();
		JButton solveButton = new JButton("Solve");
		solveButton.setActionCommand("Solve");
		solveButton.addActionListener(this);
		solveButtonPane.add(solveButton);
		
		JPanel solveSpacePane = new JPanel();
		solveSpace.setEditable(false);
		solveSpacePane.add(solveSpace);
		
		gui.add(truthTablePane, BorderLayout.WEST);
		gui.add(kMapPane, BorderLayout.EAST);
		bottomStuff.add(solveButtonPane, BorderLayout.NORTH);
		bottomStuff.add(solveSpacePane, BorderLayout.SOUTH);
		gui.add(bottomStuff, BorderLayout.SOUTH);
		
		frame.setContentPane(gui);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	private void addLabel(Container parent, String name)
	{
		JLabel lab = new JLabel(name);
		parent.add(lab);
	}

	private void addButton(Container parent, String name, int frame, String array, int i)
	{
		JButton but = new JButton(name);
		but.setActionCommand(frame + " " + array + " " + String.valueOf(i));
		but.addActionListener(this);
		but.setEnabled(false);
		parent.add(but);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		AbstractButton button = (AbstractButton) event.getSource();
		if (action.equals("Solve")){
			solve();
		}
		else if (action.charAt(2) == '5'){
			if (o1Values[Integer.parseInt(action.substring(4))] == 0){
				o1Values[Integer.parseInt(action.substring(4))] = 1;
			}
			else if (o1Values[Integer.parseInt(action.substring(4))] == 1){
				o1Values[Integer.parseInt(action.substring(4))] = 2;
			}
			else{
				o1Values[Integer.parseInt(action.substring(4))] = 0;
			}
			if (action.charAt(0) == '0'){
				button.setText(String.valueOf(o1Values[Integer.parseInt(action.substring(4))]));
				buttonList[Integer.parseInt(action.substring(4))].setText(String.valueOf(o1Values[Integer.parseInt(action.substring(4))]));
			}
			else if(action.charAt(0) == '1'){
				button.setText(String.valueOf(o1Values[Integer.parseInt(action.substring(4))]));
				buttonList2[Integer.parseInt(action.substring(4))].setText(String.valueOf(o1Values[Integer.parseInt(action.substring(4))]));
			}
		}
	}
	
	public void solve(){
		solveSpace.setText("Solving!");
		createFirstList();
		createSecondList();
		createThirdList();
		findTerms();
		solveChart();
		generateBoolean();
		System.out.println("firstListLength: " + firstListLength);
		for (int listCounter = 0; listCounter < firstListLength; listCounter++){
			System.out.println("firstList[" + String.valueOf(listCounter) + "]: " + Arrays.toString(firstList[listCounter]));
		}
		System.out.println("secondListLength: " + secondListLength);
		for (int listCounter = 0; listCounter < secondListLength; listCounter++){
			System.out.println("secondList[" + String.valueOf(listCounter) + "]: " + Arrays.toString(secondList[listCounter]));
		}
		System.out.println("thirdListLength: " + thirdListLength);
		for (int listCounter = 0; listCounter < thirdListLength; listCounter++){
			System.out.println("thirdList[" + String.valueOf(listCounter) + "]: " + Arrays.toString(thirdList[listCounter]));
		}
		System.out.println("secondListMinsLength: " + secondListLength);
		for (int listCounter = 0; listCounter < secondListLength; listCounter++){
			System.out.println("secondListMins[" + String.valueOf(listCounter) + "]: " + Arrays.toString(secondListMins[listCounter]));
		}
		System.out.println("thirdListMinsLength: " + thirdListLength);
		for (int listCounter = 0; listCounter < thirdListLength; listCounter++){
			System.out.println("thirdListMins[" + String.valueOf(listCounter) + "]: " + Arrays.toString(thirdListMins[listCounter]));
		}
		System.out.println("termsCount: " + termsCount);
		for (int listCounter = 0; listCounter < termsCount; listCounter++){
			System.out.println("chart[" + String.valueOf(listCounter) + "]: " + Arrays.toString(chart[listCounter]));
		}
	}
	
	public void createFirstList(){
		int indexAtCount = 0;
		int indexCount = 0;
		for (int i = 0; i < 16; i++){
			indexCount = 0;
			if (o1Values[i] == 1){
				if (i1Values[i] == 1){
					indexCount++;
				}
				if (i2Values[i] == 1){
					indexCount++;
				}
				if (i3Values[i] == 1){
					indexCount++;
				}
				if (i4Values[i] == 1){
					indexCount++;
				}
				//{Index,Value,Ticked,A,B,C,D}
				firstList[indexAtCount][0] = indexCount;
				firstList[indexAtCount][1] = i;
				firstList[indexAtCount][2] = 0;
				firstList[indexAtCount][3] = i1Values[i];
				firstList[indexAtCount][4] = i2Values[i];
				firstList[indexAtCount][5] = i3Values[i];
				firstList[indexAtCount][6] = i4Values[i];
				indexAtCount++;
			}
		}
		firstListLength = indexAtCount;
	}
	
	public void createSecondList(){
		int indexAtCount = 0;
		int indexCount = 0;
		for  (int i = 0; i < 5; i++){
			for (int j = 0; j < firstListLength; j++){
				if (firstList[j][0] == i)
				{
					for (int k = 0; k < firstListLength; k++){
						if (firstList[k][0] == i+1){
							int compareCounter = 0;
							boolean aDiff = false;
							boolean bDiff = false;
							boolean cDiff = false;
							boolean dDiff = false;
							if (firstList[j][3] == firstList[k][3]){
							}
							else{
								if (firstList[j][3] == 2 || firstList[k][3] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									aDiff = true;
								}
							}
							
							if (firstList[j][4] == firstList[k][4]){
							}
							else{
								if (firstList[j][4] == 2 || firstList[k][4] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									bDiff = true;
								}
							}
							
							if (firstList[j][5] == firstList[k][5]){
							}
							else{
								if (firstList[j][5] == 2 || firstList[k][5] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									cDiff = true;
								}
							}
							
							if (firstList[j][6] == firstList[k][6]){
							}
							else{
								if (firstList[j][6] == 2 || firstList[k][6] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									dDiff = true;
								}
							}
							
							if (compareCounter == 1){
								//{Index,Value,Ticked,A,B,C,D}
								firstList[j][2] = 1;
								firstList[k][2] = 1;
								secondList[indexAtCount][1] = firstList[j][1] + firstList[k][1];
								secondListMins[indexAtCount][0] = firstList[j][1];
								secondListMins[indexAtCount][1] = firstList[k][1];
								secondList[indexAtCount][2] = 0;
								if (aDiff){
									secondList[indexAtCount][3] = 2;
								}
								else{
									secondList[indexAtCount][3] = firstList[j][3];
								}
								if (bDiff){
									secondList[indexAtCount][4] = 2;
								}
								else{
									secondList[indexAtCount][4] = firstList[j][4];
								}
								if (cDiff){
									secondList[indexAtCount][5] = 2;
								}
								else{
									secondList[indexAtCount][5] = firstList[j][5];
								}
								if (dDiff){
									secondList[indexAtCount][6] = 2;
								}
								else{
									secondList[indexAtCount][6] = firstList[j][6];
								}
								indexCount = 0;
								if (secondList[indexAtCount][3] == 1){
									indexCount++;
								}
								if (secondList[indexAtCount][4] == 1){
									indexCount++;
								}
								if (secondList[indexAtCount][5] == 1){
									indexCount++;
								}
								if (secondList[indexAtCount][6] == 1){
									indexCount++;
								}
								secondList[indexAtCount][0] = indexCount;
								indexAtCount++;
							}
						}
					}
				}
			}
		}
		secondListLength = indexAtCount;
	}
	public void createThirdList(){
		int indexAtCount = 0;
		int indexCount = 0;
		for  (int i = 0; i < 5; i++){
			for (int j = 0; j < secondListLength; j++){
				if (secondList[j][0] == i)
				{
					for (int k = 0; k < secondListLength; k++){
						if (secondList[k][0] == i+1){
							int compareCounter = 0;
							boolean aDiff = false;
							boolean bDiff = false;
							boolean cDiff = false;
							boolean dDiff = false;
							if (secondList[j][3] == secondList[k][3]){
							}
							else{
								if (secondList[j][3] == 2 || secondList[k][3] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									aDiff = true;
								}
							}
							
							if (secondList[j][4] == secondList[k][4]){
							}
							else{
								if (secondList[j][4] == 2 || secondList[k][4] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									bDiff = true;
								}
							}
							
							if (secondList[j][5] == secondList[k][5]){
							}
							else{
								if (secondList[j][5] == 2 || secondList[k][5] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									cDiff = true;
								}
							}
							
							if (secondList[j][6] == secondList[k][6]){
							}
							else{
								if (secondList[j][6] == 2 || secondList[k][6] == 2){
									compareCounter+=2;
								}
								else{
									compareCounter++;
									dDiff = true;
								}
							}
							
							if (compareCounter == 1){
								//{Index,Value,Ticked,A,B,C,D}
								secondList[j][2] = 1;
								secondList[k][2] = 1;
								thirdList[indexAtCount][1] = secondList[j][1] + secondList[k][1];
								thirdListMins[indexAtCount][0] = secondListMins[j][0];
								thirdListMins[indexAtCount][1] = secondListMins[j][1];
								thirdListMins[indexAtCount][2] = secondListMins[k][0];
								thirdListMins[indexAtCount][3] = secondListMins[k][1];
								thirdList[indexAtCount][2] = 0;
								if (aDiff){
									thirdList[indexAtCount][3] = 2;
								}
								else{
									thirdList[indexAtCount][3] = secondList[j][3];
								}
								if (bDiff){
									thirdList[indexAtCount][4] = 2;
								}
								else{
									thirdList[indexAtCount][4] = secondList[j][4];
								}
								if (cDiff){
									thirdList[indexAtCount][5] = 2;
								}
								else{
									thirdList[indexAtCount][5] = secondList[j][5];
								}
								if (dDiff){
									thirdList[indexAtCount][6] = 2;
								}
								else{
									thirdList[indexAtCount][6] = secondList[j][6];
								}
								indexCount = 0;
								if (thirdList[indexAtCount][3] == 1){
									indexCount++;
								}
								if (thirdList[indexAtCount][4] == 1){
									indexCount++;
								}
								if (thirdList[indexAtCount][5] == 1){
									indexCount++;
								}
								if (thirdList[indexAtCount][6] == 1){
									indexCount++;
								}
								thirdList[indexAtCount][0] = indexCount;
								indexAtCount++;
							}
						}
					}
				}
			}
		}
		thirdListLength = indexAtCount;
	}
	
	public void findTerms(){
		for (int firstListCounter = 0; firstListCounter < firstListLength; firstListCounter++){
			if (firstList[firstListCounter][2] == 0)
			{
				//Add to terms
				chart[termsCount][firstList[firstListCounter][1]] = 1;
				termsCount++;
			}
		}
		for (int secondListCounter = 0; secondListCounter < secondListLength; secondListCounter++){
			if (secondList[secondListCounter][2] == 0)
			{
				//Add to terms
				chart[termsCount][secondListMins[secondListCounter][0]] = 1;
				chart[termsCount][secondListMins[secondListCounter][1]] = 1;
				termsCount++;
			}
		}
		for (int thirdListCounter = 0; thirdListCounter < thirdListLength; thirdListCounter++){
			//Add to terms
			if (termsCount == 0){
				chart[termsCount][thirdListMins[thirdListCounter][0]] = 1;
				chart[termsCount][thirdListMins[thirdListCounter][1]] = 1;
				chart[termsCount][thirdListMins[thirdListCounter][2]] = 1;
				chart[termsCount][thirdListMins[thirdListCounter][3]] = 1;
				termsCount++;
			}
			else{
				if (chart[termsCount-1][thirdListMins[thirdListCounter][0]] != 1 || chart[termsCount-1][thirdListMins[thirdListCounter][1]] != 1 || chart[termsCount-1][thirdListMins[thirdListCounter][2]] != 1 || chart[termsCount-1][thirdListMins[thirdListCounter][3]] != 1 ){
					chart[termsCount][thirdListMins[thirdListCounter][0]] = 1;
					chart[termsCount][thirdListMins[thirdListCounter][1]] = 1;
					chart[termsCount][thirdListMins[thirdListCounter][2]] = 1;
					chart[termsCount][thirdListMins[thirdListCounter][3]] = 1;
					termsCount++;
				}
			}
		}
	}
	
	public void solveChart(){
		int columnCount = 0;
		int possibleTerm = 0;
		boolean alreadyUsed = false;
		for (int i = 0; i < 16; i++){
			alreadyUsed = false;
			columnCount = 0;
			for (int j = 0; j < termsCount; j++){
				if (chart[j][i] == 1){
					columnCount++;
					possibleTerm = j;
				}
			}
			if (columnCount == 1){
				for (int x = 0; x < finalTermsCount; x++){
					if (possibleTerm == finalTerms[x]){
						System.out.println("Already");
						alreadyUsed = true;
					}
				}
				if(alreadyUsed){
					continue;
				}
				else{
					//Prime found
					finalTerms[finalTermsCount] = possibleTerm;
					finalTermsCount++;
				}
			}
		}
		List columnsMissing = new List();
		for (int k = 0; k < firstListLength; k++){
			columnsMissing.add(String.valueOf(firstList[k][1]));
		}
		for (int l = 0; l < finalTermsCount; l++){
			for (int m = 0; m < 16; m++){
				if (chart[finalTerms[l]][m] == 1){
					try{
					columnsMissing.remove(String.valueOf(m));
					}
					catch(Exception e){
						
					}
				}
			}
		}
		System.out.println(Arrays.toString(finalTerms));
		int removedCount = 0;
		boolean removedOne = false;
		boolean alreadyUsed2 = false;
		int missingCount = columnsMissing.getItemCount();
		System.out.println(String.valueOf(missingCount));
		for(int i=0;i<missingCount;i++)
		{
			removedOne = false;
			System.out.println(String.valueOf(termsCount));
			for (int l = 0; l < termsCount; l++){
				alreadyUsed2 = false;
				//System.out.println(columnsMissing.getItem(i));
					if (chart[finalTerms[l]][Integer.parseInt(columnsMissing.getItem(i))] == 1){
						for (int x = 0; x < finalTermsCount; x++){
							if (l == finalTerms[x]){
								System.out.println("Already2");
								alreadyUsed2 = true;
							}
						}
						if(alreadyUsed2){
							continue;
						}
						else{
							finalTerms[finalTermsCount] = l;
							finalTermsCount++;
							for (int m = 0; m < 16; m++){
								if (chart[finalTerms[l]][m] == 1){
									try{
									columnsMissing.remove(String.valueOf(m));
									removedOne = true;
									removedCount++;
									}
									catch(Exception e){
										
									}
								}
							}
						}
						
					}
					if (removedOne){
						//System.out.println("Removed");
						break;
					}
			}
			System.out.println(String.valueOf(removedCount));
			if (removedCount >= missingCount){
				//System.out.println("Broke");
				break;
			}
			//System.out.println(columnsMissing.getItem(i)); 
			//System.out.println(String.valueOf(finalTerms));
		}
		System.out.println(Arrays.toString(finalTerms));
		System.out.println("finalTermsCount: " + String.valueOf(finalTermsCount));
	}
	
	public void generateBoolean(){
		int whichListCount = 0;
		int[] whichListArray = new int[4];
		for (int i = 0; i < finalTermsCount; i++){
			whichListCount = 0;
			for (int j = 0; j < 16; j++){
				if (chart[finalTerms[i]][j] == 1){
					whichListArray[whichListCount] = j;
					whichListCount++;
				}
			}
			if (whichListCount == 1){
				for (int k = 0; k < firstListLength; k++){
					if (firstList[k][1] == finalTerms[i]){
						if (firstList[k][3] == 0){
							answerString = answerString + "A'";
						}
						else if (firstList[k][3] == 1){
							answerString = answerString + "A";
						}
						if (firstList[k][4] == 0){
							answerString = answerString + "B'";
						}
						else if (firstList[k][4] == 1){
							answerString = answerString + "B";
						}
						if (firstList[k][5] == 0){
							answerString = answerString + "C'";
						}
						else if (firstList[k][5] == 1){
							answerString = answerString + "C";
						}
						if (firstList[k][6] == 0){
							answerString = answerString + "D'";
						}
						else if (firstList[k][6] == 1){
							answerString = answerString + "D";
						}
					}
				}
			}
			else if (whichListCount == 2)
			{
				for (int k = 0; k < firstListLength; k++){
					if (secondListMins[k][0] == whichListArray[0] && secondListMins[k][1] == whichListArray[1]){
						if (secondList[k][3] == 0){
							answerString = answerString + "A'";
						}
						else if (secondList[k][3] == 1){
							answerString = answerString + "A";
						}
						if (secondList[k][4] == 0){
							answerString = answerString + "B'";
						}
						else if (secondList[k][4] == 1){
							answerString = answerString + "B";
						}
						if (secondList[k][5] == 0){
							answerString = answerString + "C'";
						}
						else if (secondList[k][5] == 1){
							answerString = answerString + "C";
						}
						if (secondList[k][6] == 0){
							answerString = answerString + "D'";
						}
						else if (secondList[k][6] == 1){
							answerString = answerString + "D";
						}
					}
				}
			}
			else if (whichListCount == 4){
				for (int k = 0; k < firstListLength; k++){
					if (thirdListMins[k][0] == whichListArray[0] && thirdListMins[k][1] == whichListArray[1] && thirdListMins[k][2] == whichListArray[2] && thirdListMins[k][3] == whichListArray[3]){
						if (thirdList[k][3] == 0){
							answerString = answerString + "A'";
						}
						else if (thirdList[k][3] == 1){
							answerString = answerString + "A";
						}
						if (thirdList[k][4] == 0){
							answerString = answerString + "B'";
						}
						else if (thirdList[k][4] == 1){
							answerString = answerString + "B";
						}
						if (thirdList[k][5] == 0){
							answerString = answerString + "C'";
						}
						else if (thirdList[k][5] == 1){
							answerString = answerString + "C";
						}
						if (thirdList[k][6] == 0){
							answerString = answerString + "D'";
						}
						else if (thirdList[k][6] == 1){
							answerString = answerString + "D";
						}
					}
				}
			}
			else{
				continue;
			}
			answerString = answerString + " + ";
		}
		answerString = answerString.substring(0, answerString.length() - 3);
		solveSpace.setText("X = " + answerString);
	}
}
