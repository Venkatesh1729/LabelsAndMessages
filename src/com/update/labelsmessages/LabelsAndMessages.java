package com.update.labelsmessages;

import java.io.File;

public class LabelsAndMessages
{

	private static File labelsJs = new File("Labels/labels.js");

	private static File messagesJs = new File("Messages/messages.js");

	private static File labelsScript = new File("Scripts/Labels_sc.sql");

	private static File messagesScript = new File("Scripts/Messages_sc.sql");

	private static File currentScript = new File("Scripts/Current_sc.sql");

	private static File newLabelsFile = new File("Labels/CreateLabels.txt");

	private static File updateLabelsFile = new File("Labels/UpdateLabels.txt");

	private static File newMessagesFile = new File("Messages/CreateMessages.txt");

	private static File updateMessagesFile = new File("Messages/UpdateMessages.txt");

	public static void main(String[] args) {

		try {

			AddUpdateOperation addUpdateOperation = new AddUpdateOperation();

			addUpdateOperation.labels(labelsJs, labelsScript, currentScript, newLabelsFile, updateLabelsFile);

			addUpdateOperation.messages(messagesJs, messagesScript, currentScript, newMessagesFile, updateMessagesFile);

		} catch (Exception ex) {
		}
	}
}
