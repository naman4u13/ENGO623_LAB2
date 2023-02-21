package com.ENGO623_LAB2.Util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Parser {
	
	public static ArrayList<double[]> getLTNData(String fileName) throws Exception {
		URL resource = Parser.class.getClassLoader().getResource(fileName);
		File file = Paths.get(resource.toURI()).toFile();
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		ArrayList<double[]> dataList = new ArrayList<double[]>();
		while (in.available() > 0) {
			double[] data = new double[7];
			for (int i = 0; i < 7; i++) {
					long l = in.readLong();
					data[i] = Double.longBitsToDouble(Long.reverseBytes(l));
			}
			dataList.add(data);
		}
		return dataList;
	}
	
	public static ArrayList<double[]> getMEMSData(String fileName) throws Exception {
		URL resource = Parser.class.getClassLoader().getResource(fileName);
		File file = Paths.get(resource.toURI()).toFile();
		Scanner input = new Scanner(file);
		ArrayList<double[]> dataList = new ArrayList<double[]>();
		while (input.hasNextLine()) {
			// Remove leading and trailing whitespace, as split method adds them
			String line = input.nextLine().trim();
			String[] strData = line.split("\\s+");
			if(strData.length!=7)
			{
				throw new Exception("Fatal Error: no. of params is not equal to what was expected");
			}
			double[] data = new double[7];
			for (int i = 0; i < 7; i++) {
					data[i] = Double.parseDouble(strData[i]);
			}
			dataList.add(data);
		}
		return dataList;
	}

}
