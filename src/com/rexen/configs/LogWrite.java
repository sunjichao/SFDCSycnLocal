package com.rexen.configs;

import java.io.FileWriter;
import java.io.IOException;
/***
 * 写入日志信息
 * @author  Sam Rexen Technology
 *
 */
public class LogWrite {
   
	public void Writelog(String context, String fileName) {
		try {
			FileWriter bw1 = new FileWriter(fileName, true);
			bw1.write(context + System.getProperty("line.separator"));
			bw1.flush();
			bw1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
