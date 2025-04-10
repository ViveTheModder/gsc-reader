package gui;
//GSC Reader GUI by ViveTheModder
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import cmd.GSC;
import cmd.Main;

public class App 
{
	public static JProgressBar[] progBars;
	public static JLabel[] progLabels;
	private static final String WINDOW_TITLE = "GSC Reader";
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
					JOptionPane.showMessageDialog(chooser, "This does NOT point to a folder! Try again!", WINDOW_TITLE, 0);
				}
			}
		}
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
			}
			else
			{
				errorBeep();
				JOptionPane.showMessageDialog(chooser, "This folder does NOT have GSC files! Try again!", WINDOW_TITLE, 0);
			}
		}
		
		GSC[] gscFiles = new GSC[gscFileRefs.length];
		for (int i=0; i<gscFiles.length; i++)
			gscFiles[i] = new GSC(gscFileRefs[i]);
		return gscFiles;
	}
	private static void displayProgress(GSC[] gscFiles, File logFolder)
	{
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
		String[] progLabelTxt = {"Total GSC Progress","Current GSC Progress"};
		//set component properties
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		for (int i=0; i<2; i++)
		{
			progBars[i] = new JProgressBar();
			progLabels[i] = new JLabel(progLabelTxt[i]);
			progBars[i].setValue(0);
			progBars[i].setStringPainted(true);
			progBars[i].setBorderPainted(true);
			progBars[i].setFont(new Font("Tahoma", Font.BOLD, 14));
			progBars[i].setMinimumSize(progBarDim);
			progBars[i].setMaximumSize(progBarDim);
			progBars[i].setPreferredSize(progBarDim);
			progLabels[i].setHorizontalAlignment(SwingConstants.CENTER);
			progLabels[i].setFont(new Font("Tahoma", Font.BOLD, 20));
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

				frame.setVisible(false); 
				frame.dispose();
				DEF_TOOLKIT.beep();
				JOptionPane.showMessageDialog(null, gscTotal+" GSC files have been parsed successfully in "+time+" s!", WINDOW_TITLE, 1, imgIcon);
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
	public static void main(String[] args) 
	{	
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			GSC[] gscFiles = getGscFilesFromChooser();
			File logFolder = getLogFolderFromChooser();
			displayProgress(gscFiles,logFolder);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) 
		{
			e.printStackTrace();
		}
	}
}