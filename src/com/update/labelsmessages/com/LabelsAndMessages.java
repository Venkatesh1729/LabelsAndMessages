package com.update.labelsmessages.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class LabelsAndMessages
{

	public static void main(String[] args) throws IOException {

		File updateMessagesFile = new File("C:\\Users\\vsethu\\Desktop\\LabelsAndMessages.txt");
		File messagesEnFile = new File("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\messages_en.js");

		/*
		 * File updateLabelsFile = new File("C:\\Users\\vsethu\\Desktop\\LabelsAndMessages.txt"); File labelsEnFile = new File("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\labels_en.js");
		 * 
		 * File createLabelsFile = new File("C:\\Users\\vsethu\\Desktop\\LabelsAndMessages.txt");
		 */
		File createMessagesFile = new File("C:\\Users\\vsethu\\Desktop\\LabelsAndMessages.txt");

		BufferedReader updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateMessagesFile)));
		BufferedReader messagesEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(messagesEnFile)));

		StringBuffer stringBuffer = null;

		String newFileLine = "", messageFileLine = "", fileContent = "", updateMessageKey = "", updateMessageValue = "";
		// String flag = "";
		int startIndex = 0;
		int lastIndex = 0;

		int commaIndex = 0;

		while ((messageFileLine = messagesEnBr.readLine()) != null)
			fileContent += messageFileLine + "\r\n";

		while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {

			updateMessageKey = newFileLine.split(" ", 2)[0];
			startIndex = newFileLine.indexOf("\"");
			lastIndex = newFileLine.indexOf("\"", startIndex + 1);
			updateMessageValue = newFileLine.substring(startIndex + 1, lastIndex);
			if (updateMessageKey.length() > 0 && fileContent.contains(updateMessageKey)) {

				int tempIndex = fileContent.indexOf(updateMessageKey);

				startIndex = fileContent.indexOf("\"", tempIndex + 1);
				lastIndex = fileContent.indexOf("\"", startIndex + 1);

				// fileContent = fileContent.replace(fileContent.substring(startIndex+1,lastIndex), updateMessageValue);

			}

		}
		updateAndCreateMsgBr.close();

		updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createMessagesFile)));
		stringBuffer = new StringBuffer(fileContent) ;
		commaIndex = fileContent.indexOf(",", fileContent.lastIndexOf("\""));
		if (commaIndex > -1) {
			while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {
				stringBuffer.insert(fileContent.lastIndexOf("\"") + 1, "\n" + newFileLine);
			}
		} else if (commaIndex == -1) {
			stringBuffer.insert(fileContent.lastIndexOf("\"") + 1, ",");
			while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {
				stringBuffer.insert(fileContent.lastIndexOf("\"") + 1, "\n" + newFileLine);
			}
		}

		System.out.println(stringBuffer.toString());

		updateAndCreateMsgBr.close();
		messagesEnBr.close();
		FileWriter writer = new FileWriter("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\messages_en.js");
		writer.write(fileContent);
		writer.close();
	}

}
