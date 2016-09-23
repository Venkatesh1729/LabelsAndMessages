package com.update.labelsmessages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AddUpdateOperation
{

	public static Map<String, String> newLabels = new HashMap<>();

	public static Map<String, String> updateLabels = new HashMap<>();

	public static Map<String, String> newMessages = new HashMap<>();

	public static Map<String, String> updateMessages = new HashMap<>();

	public static StringBuffer currentScriptBuffer = new StringBuffer();

	private static final Logger log = LogManager.getLogger(AddUpdateOperation.class);

	public void labels(File labelJs, File labelScript, File currentScript, File newLabels, File updateLabels) throws Exception {

		addLabelsOrMessagesInJs(newLabels, labelJs, true);
		updateLabelsOrMessagesInJs(updateLabels, labelJs, true);

		addLabelsOrMessagesInScript(labelScript, true);
		updateLabelsOrMessagesInScript(labelScript, true);

		currentSciprt(currentScript);

	}

	public void messages(File messageJs, File messageScript, File currentScript, File newMessages, File updateMessages) throws Exception {

		addLabelsOrMessagesInJs(newMessages, messageJs, false);
		updateLabelsOrMessagesInJs(updateMessages, messageJs, false);

		addLabelsOrMessagesInScript(messageScript, false);
		updateLabelsOrMessagesInScript(messageScript, false);

		currentSciprt(currentScript);

	}

	private void updateLabelsOrMessagesInJs(File updateLabelOrMessage, File jsLabelOrMessage, Boolean isLabel) {

		log.info("Entering updateLabelsOrMessagesInJs method..");

		int startIndex = 0;
		int lastIndex = 0;
		int tempIndex = 0;

		String newFileLine = "", updateKey = "", updateValue = "";
		StringBuffer stringBuffer = new StringBuffer();

		try {

			BufferedReader updateLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(updateLabelOrMessage)));
			BufferedReader jsLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(jsLabelOrMessage)));

			readFileToBuffer(jsLabelOrMessageBr, stringBuffer);

			while ((newFileLine = updateLabelOrMessageBr.readLine()) != null) {

				updateKey = newFileLine.split(":", 2)[0].trim();
				startIndex = newFileLine.indexOf("\"");
				lastIndex = newFileLine.lastIndexOf("\"");
				updateValue = newFileLine.substring(startIndex + 1, lastIndex);

				if (updateKey.length() > 0 && updateValue.length() > 0) {
					if (isLabel) {
						AddUpdateOperation.updateLabels.put(updateKey, updateValue);
					} else {
						AddUpdateOperation.updateMessages.put(updateKey, updateValue);
					}
				}

				if (updateValue.length() > 0 && isContain(stringBuffer.toString(), updateValue)) {
					System.out.println("The given key and value '" + updateKey + "' can't be updated, it's value already added in JS file");
				} else if (updateKey.length() > 0 && isContain(stringBuffer.toString(), updateKey)) {

					tempIndex = stringBuffer.indexOf(updateKey);
					startIndex = stringBuffer.indexOf("\"", tempIndex + 1);
					tempIndex = stringBuffer.indexOf(",", startIndex + 1);
					lastIndex = stringBuffer.lastIndexOf("\"", tempIndex);
					stringBuffer.replace(startIndex + 1, lastIndex, updateValue);

				} else {
					System.out.println("The given key '" + updateKey + "' is not present in the JS file");
				}

			}

			updateLabelOrMessageBr.close();
			jsLabelOrMessageBr.close();

			writeUpdatedFile(jsLabelOrMessage, stringBuffer);

		} catch (Exception ex) {
			log.error("Error occured in updateLabelsOrMessagesInJs method :", ex);
		}

	}

	private void addLabelsOrMessagesInJs(File createLabelOrMessage, File jsLabelOrMessage, Boolean isLabel) throws IOException {

		int commaIndex = 0;
		int startIndex = 0;
		int lastIndex = 0;

		String newFileLine = "", updateKey = "", updateValue = "";
		StringBuffer stringBuffer = new StringBuffer();

		try {

			BufferedReader createLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(createLabelOrMessage)));
			BufferedReader jsLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(jsLabelOrMessage)));

			readFileToBuffer(jsLabelOrMessageBr, stringBuffer);

			commaIndex = stringBuffer.indexOf(",", stringBuffer.lastIndexOf("\""));

			newFileLine = createLabelOrMessageBr.readLine();

			if (commaIndex == -1 && newFileLine != null) {
				stringBuffer.insert(stringBuffer.lastIndexOf("\"") + 1, ",");
			}

			while (newFileLine != null) {

				updateKey = newFileLine.split(":", 2)[0].trim();
				startIndex = newFileLine.indexOf("\"");
				lastIndex = newFileLine.lastIndexOf("\"");
				updateValue = newFileLine.substring(startIndex + 1, lastIndex);

				if (isLabel) {
					AddUpdateOperation.newLabels.put(updateKey, updateValue);
				} else {
					AddUpdateOperation.newMessages.put(updateKey, updateValue);
				}

				if (updateKey.length() > 0 && isContain(stringBuffer.toString(), updateKey)) {
					System.out.println("The given key and value '" + updateKey + "' can't be inserted, it's key already added in JS file");
				} else if (updateValue.length() > 0 && isContain(stringBuffer.toString(), updateValue)) {
					System.out.println("The given key and value '" + updateKey + "' can't be inserted, it's value already added in JS file");
				} else {
					stringBuffer.insert(stringBuffer.lastIndexOf(",") + 1, "\n" + newFileLine);
				}

				newFileLine = createLabelOrMessageBr.readLine();
			}

			createLabelOrMessageBr.close();
			jsLabelOrMessageBr.close();

			writeUpdatedFile(jsLabelOrMessage, stringBuffer);

		} catch (Exception ex) {

		}
	}

	private void updateLabelsOrMessagesInScript(File scriptLabelOrMessage, Boolean isLabel) throws IOException {

		int startIndex = 0;
		int lastIndex = 0;
		int tempIndex = 0;

		String updateKey = "", updateValue = "";
		StringBuffer stringBuffer = new StringBuffer();

		BufferedReader scriptLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(scriptLabelOrMessage)));

		try {

			readFileToBuffer(scriptLabelOrMessageBr, stringBuffer);
			Map<String, String> updateKeyValues = new HashMap<String, String>();

			if (isLabel) {
				updateKeyValues = AddUpdateOperation.updateLabels;
			} else {
				updateKeyValues = AddUpdateOperation.updateMessages;

			}

			for (Map.Entry<String, String> entry : updateKeyValues.entrySet()) {

				updateKey = entry.getKey();
				updateValue = entry.getValue();

				if (updateValue.length() > 0 && isContain(stringBuffer.toString(), updateValue)) {
					System.out.println("The given key and value '" + updateKey + "' can't be updated, it's value already added in DB script");
				} else if (isContain(stringBuffer.toString(), updateKey)) {

					tempIndex = stringBuffer.indexOf(updateKey);
					tempIndex = stringBuffer.indexOf(",", tempIndex + 1);
					startIndex = stringBuffer.indexOf("'", tempIndex + 1);
					tempIndex = stringBuffer.indexOf(",", startIndex + 1);
					lastIndex = stringBuffer.lastIndexOf("'", tempIndex);
					stringBuffer.replace(startIndex + 1, lastIndex, updateValue);

					tempIndex = stringBuffer.indexOf(updateValue);
					tempIndex = stringBuffer.indexOf(",", tempIndex + 1);
					startIndex = stringBuffer.indexOf("'", tempIndex + 1);
					tempIndex = stringBuffer.indexOf(",", startIndex + 1);
					lastIndex = stringBuffer.lastIndexOf("'", tempIndex);
					stringBuffer.replace(startIndex + 1, lastIndex, updateValue);

					AddUpdateOperation.currentScriptBuffer.append(updateQueryGenerator(isLabel, updateKey, updateValue, "english"));
					AddUpdateOperation.currentScriptBuffer.append("\n");

				} else {
					System.out.println("The given key '" + updateKey + "' is not present in the DB script");
				}
			}

			scriptLabelOrMessageBr.close();
			writeUpdatedFile(scriptLabelOrMessage, stringBuffer);

		} catch (IOException ex) {
			throw ex;
		} finally {
		}
	}

	private void addLabelsOrMessagesInScript(File scriptLabelOrMessage, Boolean isLabel) throws IOException {

		StringBuffer stringBuffer = new StringBuffer();
		String insertKey = "", insertvalue = "";

		BufferedReader scriptLabelOrMessageBr = new BufferedReader(new InputStreamReader(new FileInputStream(scriptLabelOrMessage)));

		try {

			readFileToBuffer(scriptLabelOrMessageBr, stringBuffer);
			Map<String, String> newKeyValues = new HashMap<String, String>();

			if (isLabel) {
				newKeyValues = AddUpdateOperation.newLabels;
			} else {
				newKeyValues = AddUpdateOperation.newMessages;
			}

			for (Map.Entry<String, String> entry : newKeyValues.entrySet()) {

				insertKey = entry.getKey();
				insertvalue = entry.getValue();

				if (insertKey.length() > 0 && isContain(stringBuffer.toString(), insertKey)) {
					System.out.println("The given key and value '" + insertKey + "' can't be inserted, it's key already added in DB script");
				} else if (insertvalue.length() > 0 && isContain(stringBuffer.toString(), insertvalue)) {
					System.out.println("The given key and value '" + insertvalue + "' can't be inserted, it's value already added in DB script");
				} else {
					stringBuffer.append(insertQueryGenerator(isLabel, insertKey, insertvalue, "english"));
					AddUpdateOperation.currentScriptBuffer.append(insertQueryGenerator(isLabel, insertKey, insertvalue, "english"));
					AddUpdateOperation.currentScriptBuffer.append("\n");
				}

			}

			scriptLabelOrMessageBr.close();
			writeUpdatedFile(scriptLabelOrMessage, stringBuffer);

		} catch (IOException ex) {
			throw ex;
		} finally {
		}
	}

	public void currentSciprt(File currentScript) throws Exception {

		try {

			StringBuffer stringBuffer = new StringBuffer();

			BufferedReader currentScriptBr = new BufferedReader(new InputStreamReader(new FileInputStream(currentScript)));

			readFileToBuffer(currentScriptBr, stringBuffer);

			stringBuffer.append(AddUpdateOperation.currentScriptBuffer);

			writeUpdatedFile(currentScript, stringBuffer);

		} catch (IOException ex) {
			throw ex;
		}

	}

	private boolean isContain(String source, String subItem) {
		String pattern = "\\b" + subItem + "\\b";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(source);
		return m.find();
	}

	private StringBuffer readFileToBuffer(BufferedReader messagesEnBr, StringBuffer stringBuffer) throws IOException {
		log.info("Started reading file..");
		try {
			String fileLine = "";
			while ((fileLine = messagesEnBr.readLine()) != null)
				stringBuffer.append(fileLine + "\r\n");
		} catch (IOException ex) {
			throw ex;
		}
		log.info("Finished reading file..");
		return stringBuffer;
	}

	private void writeUpdatedFile(File messagesEnFile, StringBuffer stringBuffer) throws IOException {
		try {
			FileWriter writeFile = new FileWriter(messagesEnFile);
			writeFile.write(stringBuffer.toString());
			writeFile.close();
		} catch (IOException ex) {
			throw ex;
		}
	}

	/*
	 * private StringBuffer clearBuffer(StringBuffer stringBuffer) { stringBuffer.delete(0, stringBuffer.length()); return stringBuffer; }
	 */

	private StringBuffer insertQueryGenerator(boolean isLabel, String key, String value, String language) {
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

		query.append("\n");

		return query;
	}

	private StringBuffer updateQueryGenerator(boolean isLabel, String key, String value, String language) {
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

		query.append("\n");

		return query;
	}

}
