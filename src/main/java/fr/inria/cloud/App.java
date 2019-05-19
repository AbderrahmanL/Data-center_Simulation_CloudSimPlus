package org.scenario;			

import java.io.IOException;

import org.scenario.Utils.Utils;

import jxl.read.biff.BiffException;
import jxl.write.WriteException;



public class App {

	public static void main(String[] args) {
//		if(0 == 10){ //just making my life easier 
//			try {
//				System.setOut(new PrintStream(new FileOutputStream("Log")));
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}		
//		}
        try {
			new RunReplicationScenario().run();
		} catch (WriteException | IOException e) {
			e.printStackTrace();
		}
        catch (BiffException e) {
        	e.printStackTrace();
        }
//        System.out.println(Utils.getuniformIntegerDist(0, 5).sample());
	}
}
