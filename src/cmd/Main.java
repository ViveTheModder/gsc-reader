package cmd;
//GSC Reader v1.3 by ViveTheJoestar
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import gui.App;

public class Main {
	private static final int MAX_COLS = 440;
	public static boolean recursive=false;
	public static int csvIndex, recursiveGscCnt;
	public static final String CSV_PATH = "./csv/";
	public static String currGame;
	public static String[] csvNames;
	public static String[][] csvContents;
	
	public static void parseGsc(GSC gsc, String logPath, String[] args) throws IOException {
		long gscStart = System.currentTimeMillis();
		String gscErrors = gsc.getGscErrors();
		if (gscErrors.equals("")) writeGscOutputToLog(gsc,logPath,args);
		else System.out.println("Skipping "+gsc.fileName+".gsc for the following reasons:\n"+gscErrors);
		long gscEnd = System.currentTimeMillis();
		System.out.println("Time required to read GSC info: "+(gscEnd-gscStart)/1000.0+" s");
	}
	public static void setCsvContentsAndNames(String paramType) throws IOException {
		File csvFolder = new File(CSV_PATH+currGame+"/");
		File[] csvFiles = csvFolder.listFiles((dir, name) -> (name.toLowerCase().endsWith(".csv")));
		
		int csvCnt = csvFiles.length;
		if (csvNames==null) {
			csvNames = new String[csvCnt];
			for (int i=0; i<csvNames.length; i++) {
				String fileName = csvFiles[i].getName();
				csvNames[i] = fileName.substring(0, fileName.length()-4);
			}
		}
		if (csvContents==null) csvContents = new String[csvCnt][MAX_COLS];
		
		csvIndex = Arrays.binarySearch(csvNames, paramType);
		if (csvIndex==-1) return;
		if (csvContents[csvIndex][0]==null) {
			Scanner sc = new Scanner(csvFiles[csvIndex]);
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] lineAsArr = line.split(",");
				int nameIndex = Integer.parseInt(lineAsArr[0]);
				csvContents[csvIndex][nameIndex] = lineAsArr[1];
			}
			sc.close();
		}
	}
	public static void traverse(File src, String logPath, String[] args) throws IOException {
		if (src.isDirectory()) {
			File[] gscPaths = src.listFiles();
			if (gscPaths!=null) {
				for (File path: gscPaths) traverse(path,logPath,args);
			}
		}
		else if (src.isFile()) {
			if (src.getName().toLowerCase().endsWith(".gsc")) {
				recursiveGscCnt++;
				GSC gsc = new GSC(src);
				parseGsc(gsc,logPath,args);
			}
		}
	}
	public static void writeGscOutputToLog(GSC gsc, String logPath, String[] args) throws IOException {
		boolean printText=false;
		if (args!=null) {
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
	public static void main(String[] args) throws IOException {
		if (args.length>0) {
			if (args[0].equals("-c")) {
				File gscDir=null;
				File[] gscFileRefs=null;
				Scanner sc = new Scanner(System.in);
				String logPath=null;
				while (gscDir==null && recursive==false) {
					System.out.println("Specify folder containing GSC files.\nPress Enter to use the default path.");
					String path = sc.nextLine();
					if (path.equals("")) {
						path = new File("").getAbsolutePath()+File.separator+"in";
						new File(path).mkdir();
					}
					else {
						System.out.println("If needed, enter Y to enable recursive GSC search.");
						String option = sc.nextLine();
						if (option.toUpperCase().equals("Y")) recursive=true;
					}
					gscDir = new File(path);
					if (gscDir.isDirectory()) {
						if (!recursive) {
							File[] tmpFiles = gscDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".gsc"));
							if (tmpFiles!=null && tmpFiles.length>0) gscFileRefs=tmpFiles;
							else {
								gscDir=null;
								System.out.println("Folder contains no GSC files!");
							}
						}
					}
				}
				while (logPath==null) {
					System.out.println("Specify folder where to output .log files.\nPress Enter to use the default path.");
					String path = sc.nextLine();
					if (path.equals("")) {
						path = new File("").getAbsolutePath()+File.separator+"out";
						new File(path).mkdir();
					}
					File tmp = new File(path);
					if (tmp.isDirectory()) logPath=path;
				}
				sc.close();
				long start = System.currentTimeMillis();
				if (!recursive) {
					GSC[] gscFiles = new GSC[gscFileRefs.length];
					for (int i=0; i<gscFiles.length; i++) gscFiles[i] = new GSC(gscFileRefs[i]);
					for (GSC gsc: gscFiles) parseGsc(gsc,logPath,args);
				}
				else traverse(gscDir,logPath,args);
				long finish = System.currentTimeMillis();
				System.out.println("\nTotal time elapsed: "+(finish-start)/1000.0+" s");
			}
		}
		else App.main(null);
	}
}