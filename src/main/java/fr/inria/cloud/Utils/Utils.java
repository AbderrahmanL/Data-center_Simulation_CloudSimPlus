package org.scenario.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Utils {
	
	public static UniformRealDistribution getuniformRealDist( ) {
		return new UniformRealDistribution();
	}
	
	public static UniformRealDistribution getuniformRealDist(double left, double right ) {
		return new UniformRealDistribution(left, right);
	}
	
	public static UniformIntegerDistribution getuniformIntegerDist(int left, int right ) {
		return new UniformIntegerDistribution(left, right);
	}
	
	
	public static void writeInAGivenFile(String path , String text, boolean append) {
    try {
		System.setOut(new PrintStream(new FileOutputStream(path,append)));
		System.out.print(text);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
		System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
	}
	
	public static UUID getUUIDFromString(String string) {
		
		byte[] b = 	{(byte)1};
		try {
			b = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return UUID.nameUUIDFromBytes(b);
	}
	
	public static void writeToXls(Map<String,Integer> routeWeight, int twoRows) throws BiffException, IOException, WriteException {

	    WritableWorkbook copy = Workbook.createWorkbook(new File("CloudletTime"+ twoRows+".xls"));
	    WritableSheet sheet = copy.createSheet("sheet 1",0);
	    int currentcolumn = 2;
	    
		for(String key : routeWeight.keySet()) {
			WritableCell cell0 = new Label(currentcolumn, twoRows, key.substring(11)); sheet.addCell(cell0);
			WritableCell cell1 = new Number(currentcolumn, twoRows+1, routeWeight.get(key)); sheet.addCell(cell1);
			currentcolumn++;
		}

	    copy.write();
	    copy.close();
		
	}
}
