package com.update.labelsmessages.com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LabelsAndMessages
{

	private static File messagesEnFile = new File("Messages/messages.js");

	private static File labelsEnFile = new File("Labels/labels.js");

	private static File updateMessagesFile = new File("Messages/UpdateMessages.txt");

	private static File updateLabelsFile = new File("Labels/UpdateLabels.txt");

	private static File createMessagesFile = new File("Messages/CreateMessages.txt");

	private static File createLabelsFile = new File("Labels/CreateLabels.txt");

	private static File messagesEnScript = new File("Scripts/Messages_sc.sql");

	private static File labelsEnScript = new File("Scripts/Labels_sc.sql");

	private static File currentScript = new File("Scripts/Current_sc.sql");

	private static StringBuffer addScriptBuffer = new StringBuffer();

	private static String labelFile = "labels.js";

	private static String messageFile = "messages.js";

	/*
	 * private static String labelScriptFile = "Messages_sc.sql";
	 * 
	 * private static String messageScriptFile = "Labels_sc.sql";
	 * 
	 * private static String currentScriptFile = "Scripts/Current_sc.sql";
	 */

	private static final Logger log = LogManager.getLogger(LabelsAndMessages.class);

	public static void main(String[] args) {

		try {

			log.info("Entering main method...");

			BufferedReader updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateMessagesFile)));
			BufferedReader messagesEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(messagesEnFile)));
			BufferedReader labelsEnBr = new BufferedReader(new InputStreamReader(new FileInputStream(labelsEnFile)));
			BufferedReader currentScriptBr = new BufferedReader(new InputStreamReader(new FileInputStream(currentScript)));

			// For Messages
			log.info("Started to update messages...");
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer = readFileToBuffer(messagesEnBr, stringBuffer);
			stringBuffer = updateLabelsOrMessagesInJs(updateAndCreateMsgBr, stringBuffer, messageFile, messagesEnScript, false);
			updateAndCreateMsgBr.close();
			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createMessagesFile)));
			stringBuffer = addLabelsOrMessagesInJs(updateAndCreateMsgBr, stringBuffer, messageFile, messagesEnScript, false);
			updateAndCreateMsgBr.close();
			writeUpdatedFile(messagesEnFile, stringBuffer);

			// For Labels
			log.info("Started to update labels...");
			clearBuffer(stringBuffer);
			updateAndCreateMsgBr.close();
			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateLabelsFile)));
			stringBuffer = readFileToBuffer(labelsEnBr, stringBuffer);
			stringBuffer = updateLabelsOrMessagesInJs(updateAndCreateMsgBr, stringBuffer, labelFile, labelsEnScript, true);
			updateAndCreateMsgBr.close();
			updateAndCreateMsgBr = new BufferedReader(new InputStreamReader(new FileInputStream(createLabelsFile)));
			stringBuffer = addLabelsOrMessagesInJs(updateAndCreateMsgBr, stringBuffer, labelFile, labelsEnScript, true);
			updateAndCreateMsgBr.close();
			writeUpdatedFile(labelsEnFile, stringBuffer);

			// For Current Script File
			clearBuffer(stringBuffer);
			stringBuffer = readFileToBuffer(currentScriptBr, stringBuffer);
			stringBuffer.append(addScriptBuffer);
			writeUpdatedFile(currentScript, stringBuffer);

			messagesEnBr.close();
			labelsEnBr.close();

		} catch (IOException ex) {
			log.error("Error occured in main method :", ex);

		}
	}

	public static StringBuffer updateLabelsOrMessagesInJs(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer, String fileName, File fileScipt, Boolean isLabel) {

		log.info("Entering updateLabelsOrMessagesInJs method...");

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
						updateLabelsOrMessagesInScript(updateMessageKey, updateMessageValue, fileScipt, isLabel);
					}

				} else {
					System.out.println("The given key '" + updateMessageKey + "' is not present in " + fileName);
				}

			}
		} catch (IOException ex) {
			log.error("Error occured in updateLabelsOrMessagesInJs method :", ex);
		}

		return stringBuffer;

	}

	public static StringBuffer addLabelsOrMessagesInJs(BufferedReader updateAndCreateMsgBr, StringBuffer stringBuffer, String fileName, File fileScipt, Boolean isLabel) {

		log.info("Entering addLabelsOrMessagesInJs method...");

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
						insertLabelsOrMessagesInScript(updateMessageKey, updateMessageValue, fileScipt, isLabel);
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
						insertLabelsOrMessagesInScript(updateMessageKey, updateMessageValue, fileScipt, isLabel);
					}
				}
			}
		} catch (IOException ex) {
			log.error("Error occured in addLabelsOrMessagesInJs method :", ex);
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

	private static StringBuffer readFileToBuffer(BufferedReader messagesEnBr, StringBuffer stringBuffer) {
		log.info("Entering readFileToBuffer method...");
		try {
			String fileLine = "";
			while ((fileLine = messagesEnBr.readLine()) != null)
				stringBuffer.append(fileLine + "\r\n");
		} catch (IOException ex) {
			log.error("Error occured in readFileToBuffer method :", ex);
		}
		return stringBuffer;
	}

	private static void writeUpdatedFile(File messagesEnFile, StringBuffer stringBuffer) {
		log.info("Entering writeUpdatedFile method...");
		try {
			FileWriter writeFile = new FileWriter(messagesEnFile);
			writeFile.write(stringBuffer.toString());
			writeFile.close();
		} catch (IOException ex) {
			log.error("Error occured in writeUpdatedFile method :", ex);
		}
	}

	private static StringBuffer clearBuffer(StringBuffer stringBuffer) {
		stringBuffer.delete(0, stringBuffer.length());
		return stringBuffer;
	}

	public static StringBuffer insertQueryGenerator(boolean isLabel, String key, String value, String language) {
		StringBuffer query = new StringBuffer();
		query.append("INSERT INTO Fusion_New_Label(fusion_language_id , label_key , label_base_value , label_display_value , label_type_id) ");
		query.append("( SELECT FL.fusion_language_id , '");
		query.append(key);
		query.append("' , '");
		query.append(value);
		query.append("' , '");
		query.append(value);
		query.append("' , ");
		query.append("LT.label_type_id FROM fusion_language FL, label_type LT ");
		query.append("WHERE FL.language_value = '");
		query.append(language);
		query.append("' AND LT.label_type_name='");
		query.append(isLabel ? "labels" : "messages");
		query.append("');");

		return query;
	}

	public static StringBuffer updateQueryGenerator(boolean isLabel, String key, String value, String language) {
		StringBuffer query = new StringBuffer();
		query.append("Update Fusion_New_Label set Label_Display_Value = '");
		query.append(value);
		query.append("' WHERE label_key = '");
		query.append(key);
		query.append("' AND Fusion_Language_Id = (select Fusion_Language_Id from Fusion_Language where Language_Value = '");
		query.append(language);
		query.append("') AND Label_Type_Id = (select Label_Type_Id from Label_Type where Label_Type_Name = '");
		query.append(isLabel ? "labels" : "messages");
		query.append("');");

		return query;
	}

	public static void updateLabelsOrMessagesInScript(String updateMessageKey, String updateMessageValue, File fileScipt, Boolean isLabel) {
		log.info("Entering updateLabelsOrMessagesInScript method...");
		int startIndex = 0;
		int lastIndex = 0;
		int tempIndex = 0;
		StringBuffer stringBufferScript = new StringBuffer();
		try {
			BufferedReader scriptBr = new BufferedReader(new InputStreamReader(new FileInputStream(fileScipt)));
			stringBufferScript = readFileToBuffer(scriptBr, stringBufferScript);
			if (isContain(stringBufferScript.toString(), updateMessageKey)) {
				tempIndex = isFindIndex(stringBufferScript.toString(), updateMessageKey);
				tempIndex = stringBufferScript.indexOf(",", tempIndex + 1);
				startIndex = stringBufferScript.indexOf("'", tempIndex + 1);
				tempIndex = stringBufferScript.indexOf(",", startIndex + 1);
				lastIndex = stringBufferScript.lastIndexOf("'", tempIndex);
				stringBufferScript.replace(startIndex + 1, lastIndex, updateMessageValue);

				tempIndex = isFindIndex(stringBufferScript.toString(), updateMessageValue);
				tempIndex = stringBufferScript.indexOf(",", tempIndex + 1);
				startIndex = stringBufferScript.indexOf("'", tempIndex + 1);
				tempIndex = stringBufferScript.indexOf(",", startIndex + 1);
				lastIndex = stringBufferScript.lastIndexOf("'", tempIndex);
				stringBufferScript.replace(startIndex + 1, lastIndex, updateMessageValue);
			}
			writeUpdatedFile(fileScipt, stringBufferScript);
			addScriptBuffer.append("\n");
			addScriptBuffer.append(updateQueryGenerator(isLabel, updateMessageKey, updateMessageValue, "English"));
			scriptBr.close();
		} catch (IOException ex) {
			log.error("Error occured in updateLabelsOrMessagesInScript method :", ex);
		}
	}

	public static void insertLabelsOrMessagesInScript(String insertMessageKey, String insertMessageValue, File fileScipt, Boolean isLabel) {
		log.info("Entering insertLabelsOrMessagesInScript method...");
		StringBuffer stringBufferScript = new StringBuffer();
		try {
			BufferedReader scriptBr = new BufferedReader(new InputStreamReader(new FileInputStream(fileScipt)));
			stringBufferScript = readFileToBuffer(scriptBr, stringBufferScript);
			stringBufferScript.append(insertQueryGenerator(isLabel, insertMessageKey, insertMessageValue, "English"));
			writeUpdatedFile(fileScipt, stringBufferScript);
			addScriptBuffer.append("\n");
			addScriptBuffer.append(insertQueryGenerator(isLabel, insertMessageKey, insertMessageValue, "English"));
			scriptBr.close();
		} catch (IOException ex) {
			log.error("Error occured in insertLabelsOrMessagesInScript method :", ex);
		}
	}

}