package cmd;
//GSC Reader by ViveTheModder
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import gui.App;

public class Main 
{
	private static final int MAX_COLS = 440;
	public static int csvIndex;
	public static final String CSV_PATH = "./csv/";
	public static String currGame;
	public static String[] csvNames;
	public static String[][] csvContents;
	
	public static void writeGscOutputToLog(GSC gsc, String logPath, String[] args) throws IOException
	{
		boolean printText=false;
		if (args!=null)
		{
			if (args[0].equals("-c")) printText=true; 
		}
		
		File logFolder = new File(logPath);
		if (!logFolder.exists()) logFolder.mkdir();
		File log = new File(logPath+"/"+gsc.fileName+".log");
		FileWriter fw = new FileWriter(log);
		String output = gsc.read(printText);
		fw.write(output);
		fw.close();
	}
	public static void setCsvContentsAndNames(String paramType) throws IOException
	{
		File csvFolder = new File(CSV_PATH+currGame+"/");
		File[] csvFiles = csvFolder.listFiles((dir, name) -> (name.toLowerCase().endsWith(".csv")));
		
		int csvCnt = csvFiles.length;
		if (csvNames==null)
		{
			csvNames = new String[csvCnt];
			for (int i=0; i<csvNames.length; i++)
			{
				String fileName = csvFiles[i].getName();
				csvNames[i] = fileName.substring(0, fileName.length()-4);
			}
		}
		if (csvContents==null) csvContents = new String[csvCnt][MAX_COLS];
		
		csvIndex = Arrays.binarySearch(csvNames, paramType);
		if (csvIndex==-1) return;
		if (csvContents[csvIndex][0]==null)
		{
			Scanner sc = new Scanner(csvFiles[csvIndex]);
			while (sc.hasNextLine())
			{
				String line = sc.nextLine();
				String[] lineAsArr = line.split(",");
				int nameIndex = Integer.parseInt(lineAsArr[0]);
				csvContents[csvIndex][nameIndex] = lineAsArr[1];
			}
			sc.close();
		}
	}
	public static void main(String[] args) throws IOException
	{
		if (args.length>0)
		{
			if (args[0].equals("-c"))
			{
				File[] gscFileRefs=null;
				Scanner sc = new Scanner(System.in);
				String logPath=null;
				while (gscFileRefs==null)
				{
					System.out.println("Specify folder containing GSC files:");
					String path = sc.nextLine();
					File tmp = new File(path);
					if (tmp.isDirectory())
					{
						File[] tmpFiles = tmp.listFiles((dir, name) -> name.toLowerCase().endsWith(".gsc"));
						if (tmpFiles!=null && tmpFiles.length>0) gscFileRefs=tmpFiles;
					}
				}
				while (logPath==null)
				{
					System.out.println("Specify folder where to output .log files: ");
					String path = sc.nextLine();
					File tmp = new File(path);
					if (tmp.isDirectory()) logPath=path;
				}
				sc.close();
				
				GSC[] gscFiles = new GSC[gscFileRefs.length];
				for (int i=0; i<gscFiles.length; i++) gscFiles[i] = new GSC(gscFileRefs[i]);
				
				long start = System.currentTimeMillis();
				for (GSC gsc: gscFiles)
				{
					long gscStart = System.currentTimeMillis();
					String gscErrors = gsc.getGscErrors();
					if (gscErrors.equals("")) writeGscOutputToLog(gsc,logPath,args);
					else System.out.println("Skipping "+gsc.fileName+".gsc for the following reasons:\n"+gscErrors);
					long gscEnd = System.currentTimeMillis();
					System.out.println("Time required to read GSC info: "+(gscEnd-gscStart)/1000.0+" s");
				}
				long finish = System.currentTimeMillis();
				System.out.println("\nTotal time elapsed: "+(finish-start)/1000.0+" s");
			}
		}
		else App.main(null);
	}
}