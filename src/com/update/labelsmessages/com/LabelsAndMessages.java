package com.update.labelsmessages.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LabelsAndMessages
{

	public static void main(String[] args) throws IOException {

		File messagesEnFile = new File("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\messages_en.js");
		File labelsEnFile = new File("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\labels_en.js");

		File updateMessagesFile = new File("C:\\Users\\vsethu\\Desktop\\Labels and Messages\\UpdateMessages.txt");
		File updateLabelsFile = new File("C:\\Users\\vsethu\\Desktop\\UpdateLabels.txt");

		File createMessagesFile = new File("C:\\Users\\vsethu\\Desktop\\Labels and Messages\\CreateMessages.txt");
		File createLabelsFile = new File("C:\\Users\\vsethu\\Desktop\\CreateLabels.txt");

		FileWriter writerMessage = new FileWriter("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\messages_en.js");
		FileWriter writerLabel = new FileWriter("D:\\EPM 5.0\\staticContent\\ui\\js\\core\\common\\labels_en.js");

		BufferedReader updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateMessagesFile)));
		BufferedReader messagesEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(messagesEnFile)));
		BufferedReader labelsEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(labelsEnFile)));

		try {

			StringBuffer stringBuffer = new StringBuffer();
			String messageFileLine = "";

			while ((messageFileLine = messagesEnBr.readLine()) != null)
				stringBuffer.append(messageFileLine + "\r\n");

			stringBuffer = updateLabelsOrMessages(updateAndCreateMsgBr, stringBuffer);
			updateAndCreateMsgBr.close();

			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createMessagesFile)));
			stringBuffer = createLabelsOrMessages(updateAndCreateMsgBr, stringBuffer);
			writerMessage.write(stringBuffer.toString());
			updateAndCreateMsgBr.close();

			String labelFileLine = "";
			stringBuffer.delete(0, stringBuffer.length());
			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateLabelsFile)));
			while ((labelFileLine = labelsEnBr.readLine()) != null)
				stringBuffer.append(labelFileLine + "\r\n");

			stringBuffer = updateLabelsOrMessages(updateAndCreateMsgBr, stringBuffer);
			updateAndCreateMsgBr.close();

			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createLabelsFile)));
			stringBuffer = createLabelsOrMessages(updateAndCreateMsgBr, stringBuffer);
			writerLabel.write(stringBuffer.toString());
			updateAndCreateMsgBr.close();

		} catch (IOException ex) {

		} finally {
			messagesEnBr.close();
			labelsEnBr.close();
			writerMessage.close();
			writerLabel.close();
		}
	}

	public static StringBuffer updateLabelsOrMessages(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer) {

		String newFileLine = "", updateMessageKey = "", updateMessageValue = "";

		int startIndex = 0;
		int lastIndex = 0;
		int tempIndex = 0;

		try {
			while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {

				updateMessageKey = newFileLine.split(":", 2)[0].trim();
				startIndex = newFileLine.indexOf("\"");
				lastIndex = newFileLine.lastIndexOf("\"");
				updateMessageValue = newFileLine.substring(startIndex + 1, lastIndex);
				if (updateMessageKey.length() > 0 && isContain(stringBuffer.toString(), updateMessageKey)) {

					tempIndex = stringBuffer.indexOf(updateMessageKey);
					startIndex = stringBuffer.indexOf("\"", tempIndex + 1);
					tempIndex = stringBuffer.indexOf(",", startIndex + 1);
					lastIndex = stringBuffer.lastIndexOf("\"", tempIndex);

					stringBuffer.replace(startIndex + 1, lastIndex, updateMessageValue);

				} else {
					System.out.println("It the given key '" + updateMessageKey + "' is not present in the file");
				}

			}
		} catch (IOException ex) {

		}

		return stringBuffer;

	}

	public static StringBuffer createLabelsOrMessages(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer) throws IOException {

		int commaIndex = 0;
		int startIndex = 0;
		int lastIndex = 0;

		String newFileLine = "", updateMessageKey = "", updateMessageValue = "";
		try {
			commaIndex = stringBuffer.indexOf(",", stringBuffer.lastIndexOf("\""));
			if (commaIndex > -1) {
				while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {
					updateMessageKey = newFileLine.split(":", 2)[0].trim();
					startIndex = newFileLine.indexOf("\"");
					lastIndex = newFileLine.lastIndexOf("\"");
					updateMessageValue = newFileLine.substring(startIndex + 1, lastIndex);
					if (updateMessageKey.length() > 0 && isContain(stringBuffer.toString(), updateMessageKey)) {
						System.out.println("The given key and value '" + updateMessageKey + "' can't be inserted, it's key already added");
					} else if (updateMessageValue.length() > 0 && isContain(stringBuffer.toString(), updateMessageValue)) {
						System.out.println("The given key and value '" + updateMessageKey + "' can't be inserted, it's value already added");
					} else {
						stringBuffer.insert(stringBuffer.lastIndexOf(",") + 1, "\n" + newFileLine);
					}
				}
			} else if (commaIndex == -1) {
				stringBuffer.insert(stringBuffer.lastIndexOf("\"") + 1, ",");
				while ((newFileLine = updateAndCreateMsgBr.readLine()) != null) {
					updateMessageKey = newFileLine.split(":", 2)[0].trim();
					startIndex = newFileLine.indexOf("\"");
					lastIndex = newFileLine.lastIndexOf("\"");
					updateMessageValue = newFileLine.substring(startIndex + 1, lastIndex);
					if (updateMessageKey.length() > 0 && isContain(stringBuffer.toString(), updateMessageKey)) {
						System.out.println("The given key and value '" + updateMessageKey + "' can't be inserted, it's key already added");
					} else if (updateMessageValue.length() > 0 && isContain(stringBuffer.toString(), updateMessageValue)) {
						System.out.println("The given key and value '" + updateMessageKey + "' can't be inserted, it's value already added");
					} else {
						stringBuffer.insert(stringBuffer.lastIndexOf(",") + 1, "\n" + newFileLine);
					}
				}
			}
		} catch (IOException ex) {

		}
		return stringBuffer;

	}

	private static boolean isContain(String source, String subItem) {
		String pattern = "\\b" + subItem + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		return m.find();
	}

}