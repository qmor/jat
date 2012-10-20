package jat.core.util;

import java.util.ArrayList;

import javax.swing.JTextArea;

public class jatMessages {

	ArrayList<String> jatMessageList = new ArrayList<String>();

	public void add(String message) {

		jatMessageList.add(message);
	}

	public void addln(String message) {
		jatMessageList.add(message+'\n');		
	}

	public void addln(String className, String message) {
		jatMessageList.add(message+'\n');		
	}

	public void printMessages() {
		for (int i = 0; i < jatMessageList.size(); i++) {
			System.out.print(jatMessageList.get(i));
		}
	}

	public void printMessagesToTextArea(JTextArea textArea) {
		for (int i = 0; i < jatMessageList.size(); i++) {
			textArea.append(jatMessageList.get(i));
			//System.out.print(jatMessageList.get(i));
		}
	}


}
