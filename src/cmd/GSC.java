package cmd;
//GSC Object by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Scanner;

import gui.App;

public class GSC {
	public String fileName;
	private int gscSize;
	private RandomAccessFile gsc;
	private String game;
	private static final int EOFC = 0x454F4643, GSAC = 0x47534143, GSCD = 0x47534344, GSCF = 0x47534346;
	private static final int GSHD = 0x47534844, GSDT = 0x47534454, SCENE_MINUS_1 = 0xFFFFFFFF;
	private static final String[] GAMES = { "bt2", "bt3", "rb1" };

	public GSC(File f) {
		try {
			gsc = new RandomAccessFile(f, "r");
			gscSize = getFileSize();
			fileName = f.getName().substring(0, f.getName().length() - 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] getGsdt(int gsdtPos) throws IOException {
		byte[] gsdt;
		gsc.seek(gsdtPos - 8);
		int gsdtSize = LittleEndian.getInt(gsc.readInt());
		gsdt = new byte[gsdtSize];
		gsc.seek(gsdtPos);
		gsc.read(gsdt);
		return gsdt;
	}

	private float getFloatFromGsdt(byte[] gsdt, short offset) {
		byte[] valBytes = new byte[4];
		int addr = (offset & 0xFFFF) * 4;
		if (addr >= gsdt.length) return Float.MAX_VALUE;
		System.arraycopy(gsdt, addr, valBytes, 0, 4);
		return LittleEndian.getFloatFromByteArray(valBytes);
	}
	/* Normally, gsc.length() would do the job - however, for modders who use Game Utility to edit
	 * their GSC files (looking at you, Valen2006), they may have to rely on adding bytes of zero
	 * at the end of the file (what we call padding, or in the case of AFS Explorer, reserved space),
	 * since Game Utility does not allow for file sizes to be modified from the AFS.
	 * As a result, padding must be skipped when computing the file size. */
	private int getFileSize() throws IOException {
		int fullSize = (int) gsc.length();
		int pos = fullSize - 16;
		//for performance reasons, only up to 2 KiBs of the GSC are read backwards (from bottom to top)
		while (pos > fullSize - 2048) {
			gsc.seek(pos);
			int input = gsc.readInt();
			if (input == EOFC) break;
			pos -= 4;
		}
		gsc.seek(0); //reset position just in case
		return pos + 16;
	}
	private int getIntFromGsdt(byte[] gsdt, short offset) {
		byte[] valBytes = new byte[4];
		int addr = (offset & 0xFFFF) * 4;
		if (addr >= gsdt.length) return Integer.MAX_VALUE;
		System.arraycopy(gsdt, addr, valBytes, 0, 4);
		return LittleEndian.getIntFromByteArray(valBytes);
	}
	private int getSectorPos(int sectorId, int initPos) throws IOException {
		int curr;
		while (initPos != gscSize) {
			curr = gsc.readInt();
			if (curr == sectorId) break;
			initPos += 4;
			gsc.seek(initPos);
		}
		return initPos + 16;
	}
	private String readParams(byte[] gsdt, String[] info, short currFuncId, int numParams, boolean isFunc) throws IOException {
		String output = "", opp = "";
		for (int i = 0; i < numParams; i++) {
			byte[] offsetBytes = new byte[2];
			byte[] paramData = new byte[4];
			gsc.read(paramData);
			int paramDataType = paramData[0];
			System.arraycopy(paramData, 1, offsetBytes, 0, 2);
			short offset = LittleEndian.getShortFromByteArray(offsetBytes);
			//check if parameter data type is an integer or a float
			if (paramDataType == 10) {
				int paramVal = getIntFromGsdt(gsdt, offset);
				String param = paramVal + "";
				byte[] paramValBytes = LittleEndian.getByteArrayFromInt(paramVal);
				//replace the 80 in condition IDs (or indices) applied to opponents with zero
				//to prevent out of range index
				if (paramValBytes[2] == -128) {
					opp = " (Opp.)";
					paramValBytes[2] = 0;
					paramVal = ByteBuffer.wrap(paramValBytes).getInt();
				} else opp = "";
				if (info != null && info.length >= numParams + 2) {
					int paramTypeIndex = i + 2;
					if (!isFunc) paramTypeIndex = i + 3;
					String paramType = "";
					if (!isFunc) {
						if (paramTypeIndex >= info.length) paramType = "num";
						else paramType = info[paramTypeIndex];
					} else paramType = info[paramTypeIndex];

					if (!paramType.equals("num")) {
						if (paramType.equals("bool")) {
							if (paramVal == 1) param = "True";
							else param = "False";
						} else {
							Main.setCsvContentsAndNames(paramType);
							if (paramVal == 65535) param = "Immediate"; //65535 (unsigned), -1 (signed)
							else if (paramVal >= 0) {
								if (paramVal < 440) {
									if (Main.csvContents[Main.csvIndex][paramVal] == null)
										param = "Unknown (Value: " + paramVal + ")";
									else param = Main.csvContents[Main.csvIndex][paramVal] + opp;
								} else param = "Invalid (Value: " + paramVal + ")";
							} else param = "N/A";
						}
					}
				}
				String paramName = "* Parameter " + i;
				if (info != null) {
					int paramNameIndex = i + 1;
					if (!isFunc) paramNameIndex = i + 2;
					String[] paramNameInfo = getInfoFromCall(Short.parseShort(info[0]), currFuncId, isFunc, true);
					if (paramNameInfo != null) paramName = "* " + paramNameInfo[paramNameIndex];
				}
				if (paramName.endsWith("Index")) param = paramVal + opp;
				output += paramName + ": " + param + "\n";
			} else if (paramDataType == 26) {
				float param = getFloatFromGsdt(gsdt, offset);
				String paramName = "* Parameter " + i;
				if (info != null) {
					int paramNameIndex = i + 1;
					if (!isFunc) paramNameIndex = i + 2;
					String[] paramNameInfo = getInfoFromCall(Short.parseShort(info[0]), currFuncId, isFunc, true);
					if (paramNameInfo != null) paramName = "* " + paramNameInfo[paramNameIndex];
				}
				output += paramName + ": " + param + "\n";
			}
		}
		return output;
	}
	private void setGame() throws IOException {
		game = "unk";
		gsc.seek(32);
		int gscVerNum1 = LittleEndian.getInt(gsc.readInt());
		int gscVerNum2 = LittleEndian.getInt(gsc.readInt());
		if (gscVerNum1 == 3) {
			if (gscVerNum2 > 0 && gscVerNum2 < 4) game = GAMES[gscVerNum2 - 1];
		}
	}
	private String[] getInfoFromCall(short callId, short functionIdForProp, boolean isFunc, boolean isParam) throws IOException {
		String fileNameSuffix = "-funcs";
		if (!isFunc) fileNameSuffix = "-props";
		if (isParam) fileNameSuffix = fileNameSuffix.substring(0, 5) + "-params";
		File csv = new File(Main.CSV_PATH + game + fileNameSuffix + ".csv");
		if (game.equals("bt2")) return null;
		Scanner sc = new Scanner(csv);
		String[] info = null;
		if (sc.hasNextLine()) sc.nextLine(); //skip header
		else {
			sc.close();
			return info;
		}
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String[] lineAsArray = line.split(",");
			int callIdFromLine = Short.parseShort(lineAsArray[0]);
			if (callIdFromLine == callId) {
				if (isFunc) info = lineAsArray;
				else {
					/* A whole 'nother parameter had to be added to this method because of how evil
					 * Spike devs were for adding properties with different purposes that end up
					 * sharing the same ID (hence why the current function ID is needed) */
					if (functionIdForProp == Short.parseShort(lineAsArray[1])) info = lineAsArray;
				}
			}
		}
		sc.close();
		return info;
	}
	public String getGscErrors() throws IOException {
		int[] gscHeaderInfo = { GSCF, GSHD, GSCD }, gscHeaderAddr = { 0, 16, 64 };
		String gscError = "";
		String[] gscHeaderInfoStr = { "GSCF", "GSHD", "GSCD" };
		//file size checks
		if (gscSize > 36864) gscError += "* GSC exceeds file size limit of 36 KiB!\n";
		gsc.seek(8);
		int sizeFromGscf = LittleEndian.getShort(gsc.readShort()) & 0xFFFF;
		if (sizeFromGscf + 32 != gscSize) {
			gscError += "* GSC file size (" + gscSize + ") does NOT match file size (" + (sizeFromGscf + 32);
			gscError += ") specified in header!\n";
		}
		for (int i = 0; i < gscHeaderInfo.length; i++) {
			gsc.seek(gscHeaderAddr[i]);
			int input = gsc.readInt();
			if (input != gscHeaderInfo[i])
				gscError += "* GSC does not contain " + gscHeaderInfoStr[i] + " in header!\n";
		}
		gsc.seek(72);
		short gsacTotalSize = LittleEndian.getShort(gsc.readShort());
		gsc.seek(gsacTotalSize + 96); //112 is the actual size of the header, but I put 96 due to the next check
		if (gsc.readInt() != GSDT)
			gscError += "* GSC does NOT contain GSDT indicator!\n";
		return gscError;
	}
	public String read(boolean printText) throws IOException {
		String output = "";
		if (printText) System.out.println("Reading " + fileName + ".gsc...");
		String gameVerbose = "Unknown";
		setGame();
		Main.currGame = game;
		if (game.startsWith("bt")) gameVerbose = game.replace("bt", "Budokai Tenkaichi ");
		else if (game.startsWith("rb")) gameVerbose = game.replace("rb", "Raging Blast ");
		output += "GSC Origin: " + gameVerbose + "\n";
		short currFuncId = 0;
		int gsdtPos = getSectorPos(GSDT, 0);
		byte[] gsdt = getGsdt(gsdtPos);
		int posOfScene3 = getSectorPos(SCENE_MINUS_1, 0) - 28;
		int gsacSize = gsdtPos - posOfScene3;
		gsc.seek(posOfScene3);
		//quartet refers to a set of 4 bytes
		if (App.progBars != null) App.progBars[1].setMaximum(gsacSize / 4);
		for (int quartet = 0; quartet < gsacSize / 4; quartet++) {
			byte[] input = new byte[4];
			gsc.read(input);
			//check if input refers to a GSAC (scene)'s header
			int inputAsInt = LittleEndian.getIntFromByteArray(input);
			//int has to be returned back to Big Endian for the comparison to work
			if (LittleEndian.getInt(inputAsInt) == GSAC) {
				gsc.readLong(); //skip 8 bytes
				int sceneId = LittleEndian.getInt(gsc.readInt());
				output += "\n===== SCENE " + sceneId + " =====" + "\n";
				quartet += 3;
			}
			//check if input contains a function call or property call
			if (input[0] == 1) { //function call
				int numParams = input[1];
				byte[] funcIdBytes = new byte[2];
				System.arraycopy(input, 2, funcIdBytes, 0, 2);
				short funcId = LittleEndian.getShortFromByteArray(funcIdBytes);
				currFuncId = funcId;
				String funcName = "FUNCTION " + funcId;
				String[] info = getInfoFromCall(funcId, funcId, true, false);
				if (info != null) funcName = info[1];
				String suffix = " parameters]";
				if (numParams == 1) suffix = suffix.replace("s]", "]");
				else if (numParams == 0) suffix = "]";
				String numParamsText = ": " + numParams + suffix;
				if (numParams == 0) numParamsText = "]";
				output += "\n[" + funcName + numParamsText + "\n";
				output += readParams(gsdt, info, currFuncId, numParams, true);
				quartet += numParams;
			} else if (input[0] == 8) { //property call
				int numParams = input[2];
				short propId = input[1];
				String propName = "PROPERTY " + propId;
				String[] info = getInfoFromCall(propId, currFuncId, false, false);
				if (info != null) propName = info[2];
				String suffix = " parameters";
				if (numParams == 1) suffix = suffix.substring(0, suffix.length() - 1);
				else if (numParams == 0) suffix = "";
				String numParamsText = ": " + numParams + suffix;
				if (numParams == 0) numParamsText = "";
				output += "-> " + propName + numParamsText + "\n";
				output += readParams(gsdt, info, currFuncId, numParams, false);
				quartet += numParams;
			}
			if (App.progBars != null) App.progBars[1].setValue(quartet + 1);
		}
		return output;
	}
}