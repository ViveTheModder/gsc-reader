package gui;
//GSC Reader v1.1 GUI by ViveTheModder
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import cmd.GSC;
import cmd.Main;

public class App 
{
	public static JProgressBar[] progBars;
	public static JLabel[] progLabels;
	private static GSC[] gscFiles=null;
	private static File logFolder=null;
	private static String[] folderPaths = {"",""};
	private static final Font BOLD_L = new Font("Tahoma", Font.BOLD, 20);
	private static final Font BOLD_M = new Font("Tahoma", Font.BOLD, 14);
	private static final String HTML_DIV_START = "<html><div style='text-align: center; color: #dd4015;'>";
	private static final String HTML_DIV_END = "</div></html>";
	private static final String WINDOW_TITLE = "GSC Reader v1.1";
	private static final Toolkit DEF_TOOLKIT = Toolkit.getDefaultToolkit();
	private static final Image ICON_IMG = DEF_TOOLKIT.getImage(ClassLoader.getSystemResource("img/icon.png"));
	
	private static File getLogFolderFromChooser()
	{
		File logFolder=null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select folder to save LOG files...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while (logFolder==null)
		{
			int result = chooser.showOpenDialog(null);
			if (result==0)
			{
				File tmp = chooser.getSelectedFile();
				if (tmp.isDirectory()) logFolder=tmp;
				else 
				{
					errorBeep();
					JOptionPane.showMessageDialog(chooser, "This does NOT point to a folder. Try again!", WINDOW_TITLE, 0);
				}
			}
		}
		folderPaths[1] = logFolder.getAbsolutePath();
		return logFolder;
	}
	private static GSC[] getGscFilesFromChooser()
	{
		File[] gscFileRefs=null;
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select folder containing GSC Files...");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while (gscFileRefs==null)
		{
			int result = chooser.showOpenDialog(null);
			if (result==0)
			{
				File tmp = chooser.getSelectedFile();
				File[] tmpFiles = tmp.listFiles((dir, name) -> name.toLowerCase().endsWith(".gsc"));
				if (tmpFiles!=null && tmpFiles.length>0) gscFileRefs=tmpFiles;
				else
				{
					errorBeep();
					JOptionPane.showMessageDialog(chooser, "This folder does NOT have GSC files! Try again!", WINDOW_TITLE, 0);
				}
			}
			else
			{
				errorBeep();
				JOptionPane.showMessageDialog(chooser, "This does NOT point to a folder. Try again!", WINDOW_TITLE, 0);
			}
		}
		
		folderPaths[0] = gscFileRefs[0].toPath().getParent().toString();
		
		GSC[] gscFiles = new GSC[gscFileRefs.length];
		for (int i=0; i<gscFiles.length; i++)
			gscFiles[i] = new GSC(gscFileRefs[i]);
		return gscFiles;
	}
	private static void displayProgress(GSC[] gscFiles, File logFolder)
	{
		String[] progLabelTxt = {"Total GSC Progress","Current GSC Progress"};
		//change settings for all progress bars (must be done before declaring them)
	    UIManager.put("ProgressBar.background", Color.WHITE);
	    UIManager.put("ProgressBar.foreground", Color.GREEN);
	    UIManager.put("ProgressBar.selectionBackground", Color.BLACK);
	    UIManager.put("ProgressBar.selectionForeground", Color.BLACK);
		//initialize components
	    progLabels = new JLabel[2];
	    progBars = new JProgressBar[2];
	    Dimension progBarDim = new Dimension(256,32);
	    GridBagConstraints gbc = new GridBagConstraints();
	    Image img = ICON_IMG.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
	    ImageIcon imgIcon = new ImageIcon(img);
		JFrame frame = new JFrame();
		JPanel panel = new JPanel(new GridBagLayout());
		//set component properties
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		for (int i=0; i<2; i++)
		{
			progBars[i] = new JProgressBar();
			progLabels[i] = new JLabel(progLabelTxt[i]);
			progBars[i].setValue(0);
			progBars[i].setStringPainted(true);
			progBars[i].setBorderPainted(true);
			progBars[i].setFont(BOLD_M);
			progBars[i].setMinimumSize(progBarDim);
			progBars[i].setMaximumSize(progBarDim);
			progBars[i].setPreferredSize(progBarDim);
			progLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
			progLabels[i].setFont(BOLD_L);
		}
		//add components
		for (int i=0; i<2; i++)
		{
			panel.add(progLabels[i],gbc);
			panel.add(new JLabel(" "),gbc);
			panel.add(progBars[i],gbc);
			panel.add(new JLabel(" "),gbc);
		}
		frame.add(panel);
		//set frame properties
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setIconImage(ICON_IMG);
		frame.setLocationRelativeTo(null);
		frame.setSize(512,256);
		frame.setTitle(WINDOW_TITLE);
		frame.setVisible(true);
		
		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception 
			{
				int gscCnt=0, gscTotal = gscFiles.length;				
				long start = System.currentTimeMillis();
				String gscFilesMsg = " GSC files have ";
				for (int i=0; i<gscFiles.length; i++)
				{
					if (gscFiles[i].getGscErrors()=="")
					{
						gscCnt++;
						progLabels[1].setText(gscFiles[i].fileName+".gsc Progress");
						Main.writeGscOutputToLog(gscFiles[i], logFolder.getAbsolutePath(), null);
						progBars[0].setValue(gscCnt);
					}
					else gscTotal--;
					progBars[0].setMaximum(gscTotal);
				}
				long end = System.currentTimeMillis();
				double time = (end-start)/1000.0;
				if (gscTotal==1) gscFilesMsg = gscFilesMsg.replace("files have", "file has");
				
				frame.setVisible(false); 
				frame.dispose();
				DEF_TOOLKIT.beep();
				JOptionPane.showMessageDialog(null, gscTotal+gscFilesMsg+"been parsed successfully in "+time+" s!", WINDOW_TITLE, 1, imgIcon);
				return null;
			}
		};
		worker.execute();
	}
	private static void errorBeep()
	{
		Runnable runWinErrorSnd = (Runnable) DEF_TOOLKIT.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd!=null) runWinErrorSnd.run();
	}
	private static void setApp()
	{
		String[] folderNames = {"in","out"};
		String[] pathLblText = {"Source Folder (GSC)","Destination Folder (LOG)"};
		//initialize components
		Box titleBox = Box.createHorizontalBox();
		Box[] pathBoxes = new Box[2];
		Dimension txtFieldSize = new Dimension(256,24);
		Image img = ICON_IMG.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
		ImageIcon imgIcon = new ImageIcon(img);
		JButton readBtn = new JButton(HTML_DIV_START+"Read GSCs from Source"+HTML_DIV_END);
		JButton[] openBtns = new JButton[2];
		JCheckBox[] pathChkBoxes = new JCheckBox[2];
		JFrame frame = new JFrame();
		JTextField[] pathTxtFields = new JTextField[2];
		JLabel iconLbl = new JLabel(" "), titleLbl = new JLabel(HTML_DIV_START+WINDOW_TITLE+HTML_DIV_END);
		JLabel[] pathLbls = new JLabel[2];
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		//set component properties
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		iconLbl.setIcon(imgIcon);
		readBtn.setFont(BOLD_L);
		readBtn.setToolTipText("Faulty GSC files will be skipped.");
		titleBox.setToolTipText("Made by ViveTheModder.");
		titleLbl.setFont(new Font("Tahoma", Font.BOLD, 24));
		//add components
		titleBox.add(iconLbl);
		titleBox.add(titleLbl);
		panel.add(titleBox,gbc);
		panel.add(new JLabel(" "),gbc);
		//set & add components
		for (int i=0; i<2; i++)
		{
			final int index=i;
			String defDir = new File("").getAbsolutePath()+File.separator+folderNames[i];
			openBtns[i] = new JButton("+");
			pathBoxes[i] = Box.createHorizontalBox();
			pathChkBoxes[i] = new JCheckBox("Use Default Directory");
			pathTxtFields[i] = new JTextField();
			pathLbls[i] = new JLabel(pathLblText[i]);
			
			pathChkBoxes[i].setToolTipText("Default Directory: "+defDir);
			pathLbls[i].setFont(BOLD_M);
			pathTxtFields[i].setMaximumSize(txtFieldSize);
			pathTxtFields[i].setMinimumSize(txtFieldSize);
			pathTxtFields[i].setPreferredSize(txtFieldSize);
			
			pathBoxes[i].add(pathTxtFields[i]);
			pathBoxes[i].add(Box.createHorizontalStrut(8));
			pathBoxes[i].add(openBtns[i]);
			panel.add(pathLbls[i],gbc);
			panel.add(pathChkBoxes[i],gbc);
			panel.add(pathBoxes[i],gbc);
			panel.add(new JLabel(" "),gbc);
			
			openBtns[i].addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if (index>0) 
					{
						logFolder = getLogFolderFromChooser();
						if (logFolder!=null) 
						{
							pathTxtFields[index].setText(folderPaths[1]);
							pathTxtFields[index].setEditable(false);
						}
					}
					else 
					{
						gscFiles = getGscFilesFromChooser();
						if (gscFiles!=null) 
						{
							pathTxtFields[index].setText(folderPaths[0]);
							pathTxtFields[index].setEditable(false);
						}
					}
				}
			});
			pathChkBoxes[i].addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					if (pathChkBoxes[index].isSelected())
					{
						pathTxtFields[index].setText("");
						pathTxtFields[index].setEditable(false);
						folderPaths[index] = new File("").getAbsolutePath()+File.separator+folderNames[index];
						new File(folderPaths[index]).mkdir();
					}
					else 
					{
						folderPaths[index]="";
						pathTxtFields[index].setEditable(true);
					}
				}
			});
		}
		//add final action listener
		readBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String msg="";
				
				if (folderPaths[0].equals("")) folderPaths[0] = pathTxtFields[0].getText();
				File tmp = new File(folderPaths[0]);
				if (tmp.isDirectory())
				{
					File[] tmpFiles = tmp.listFiles((dir, name) -> name.toLowerCase().endsWith(".gsc"));
					if (tmpFiles!=null && tmpFiles.length>0)
					{
						gscFiles = new GSC[tmpFiles.length];
						for (int i=0; i<gscFiles.length; i++)
							gscFiles[i] = new GSC(tmpFiles[i]);
					}
					else 
					{
						gscFiles=null;
						msg+="Source does NOT contain GSC files!\n";
					}
				}
				else 
				{
					gscFiles=null;
					msg+="Source does NOT point to a folder!\n";
				}
				
				if (folderPaths[1].equals("")) folderPaths[1] = pathTxtFields[1].getText();
				tmp = new File(folderPaths[1]);
				if (tmp.isDirectory()) logFolder=tmp;
				else 
				{
					logFolder=null;
					msg+="Destination does NOT point to a folder!\n";
				}
				
				if (msg.equals(""))
				{
					frame.setEnabled(false);
					displayProgress(gscFiles,logFolder);
					frame.setEnabled(true);
				}
				else 
				{
					errorBeep();
					JOptionPane.showMessageDialog(frame, msg, WINDOW_TITLE, 0);
				}
			}
		});
		//add components
		panel.add(readBtn,gbc);
		frame.add(panel);		
		//set frame properties
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setIconImage(ICON_IMG);
		frame.setLocationRelativeTo(null);
		frame.setSize(512,384);
		frame.setTitle(WINDOW_TITLE);
		frame.setVisible(true);
	}
	public static void main(String[] args) 
	{	
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setApp();
		} 
		catch (Exception e) 
		{
			errorBeep();
			JOptionPane.showMessageDialog(null, e.getClass().getSimpleName()+": "+e.getMessage(), "Exception", 0);		
		}
	}
}