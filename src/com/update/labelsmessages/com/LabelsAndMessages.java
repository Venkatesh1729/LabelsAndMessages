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

		File messagesEnFile = new File("Messages/messages.js");
		File labelsEnFile = new File("Labels/labels.js");

		File updateMessagesFile = new File("Messages/UpdateMessages.txt");
		File updateLabelsFile = new File("Labels/UpdateLabels.txt");

		File createMessagesFile = new File("Messages/CreateMessages.txt");
		File createLabelsFile = new File("Labels/CreateLabels.txt");

		BufferedReader updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateMessagesFile)));
		BufferedReader messagesEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(messagesEnFile)));
		BufferedReader labelsEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(labelsEnFile)));

		try {

			// For Messages
			StringBuffer stringBuffer = new StringBuffer();
			String messageFileLine = "";

			while ((messageFileLine = messagesEnBr.readLine()) != null)
				stringBuffer.append(messageFileLine + "\r\n");

			stringBuffer = updateLabelsOrMessages(updateAndCreateMsgBr, stringBuffer, "messages.js");
			updateAndCreateMsgBr.close();

			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createMessagesFile)));
			stringBuffer = createLabelsOrMessages(updateAndCreateMsgBr, stringBuffer, "messages.js");
			FileWriter writerMessage = new FileWriter(messagesEnFile);
			writerMessage.write(stringBuffer.toString());
			writerMessage.close();
			updateAndCreateMsgBr.close();

			// For Labels
			String labelFileLine = "";
			stringBuffer.delete(0, stringBuffer.length());
			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateLabelsFile)));
			while ((labelFileLine = labelsEnBr.readLine()) != null)
				stringBuffer.append(labelFileLine + "\r\n");

			stringBuffer = updateLabelsOrMessages(updateAndCreateMsgBr, stringBuffer, "labels.js");
			updateAndCreateMsgBr.close();

			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createLabelsFile)));
			stringBuffer = createLabelsOrMessages(updateAndCreateMsgBr, stringBuffer, "labels.js");
			FileWriter writerLabel = new FileWriter(labelsEnFile);
			writerLabel.write(stringBuffer.toString());
			writerLabel.close();
			updateAndCreateMsgBr.close();

		} catch (IOException ex) {

		} finally {
			messagesEnBr.close();
			labelsEnBr.close();
		}
	}

	public static StringBuffer updateLabelsOrMessages(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer, String fileName) {

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

					tempIndex = isFindIndex(stringBuffer.toString(), updateMessageKey);
					startIndex = stringBuffer.indexOf("\"", tempIndex + 1);
					tempIndex = stringBuffer.indexOf(",", startIndex + 1);
					lastIndex = stringBuffer.lastIndexOf("\"", tempIndex);
					if (updateMessageValue.length() > 0 && isContain(stringBuffer.toString(), updateMessageValue)) {
						System.out.println("The given key '" + updateMessageKey + "' and it's value can't be updated, because it's value is already added in " + fileName);
					} else {
						stringBuffer.replace(startIndex + 1, lastIndex, updateMessageValue);
					}

				} else {
					System.out.println("The given key '" + updateMessageKey + "' is not present in " + fileName);
				}

			}
		} catch (IOException ex) {

		}

		return stringBuffer;

	}

	public static StringBuffer createLabelsOrMessages(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer, String fileName) throws IOException {

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
						System.out.println("The given key '" + updateMessageKey + "' and it's value can't be inserted, because it's key is already added in " + fileName);
					} else if (updateMessageValue.length() > 0 && isContain(stringBuffer.toString(), updateMessageValue)) {
						System.out.println("The given key '" + updateMessageKey + "' and it's value can't be inserted, because it's value is already added in " + fileName);
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
						System.out.println("The given key '" + updateMessageKey + "' and it's value can't be inserted, because it's key is already added in " + fileName);
					} else if (updateMessageValue.length() > 0 && isContain(stringBuffer.toString(), updateMessageValue)) {
						System.out.println("The given key '" + updateMessageKey + "' and it's value can't be inserted, because it's value is already added in " + fileName);
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

	private static int isFindIndex(String source, String subItem) {
		int index = 0;
		String pattern = "\\b" + subItem + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		while (m.find()) {
			index = m.start();
		}
		return index;
	}

}