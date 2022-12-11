import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ATAHAN_CALDIR_S018194 {
	
	private String input;
	private int lineCount;
	private String foundEpsilon = "";

	private Map<String, List<String>> rules = new LinkedHashMap<>();

	public static void main(String args[]) throws FileNotFoundException {
		String inputCFGFile = "G1.txt";

		String CFGrules = getCFGRules(inputCFGFile);
		String[] splittedRules = CFGrules.split("\n");

		ATAHAN_CALDIR_S018194 converter = new ATAHAN_CALDIR_S018194();

		converter.setParameters(CFGrules, splittedRules.length);
		converter.CFGtoCNF();
		converter.printResult();
	}

	public static String getCFGRules(String fileName) throws FileNotFoundException {
		String pRules = "";
		File inputFile = new File(fileName);
		Scanner scanner = new Scanner(inputFile);
		boolean ruleFlag = false;
		Map<String, String> map = new HashMap<String, String>();

		while (scanner.hasNextLine()) {
			String scannedLine = scanner.nextLine();

			if (scannedLine.equals("RULES")) {
				ruleFlag = true;
				continue;
			}
			if (scannedLine.equals("START")) {
				break;
			}
			if (ruleFlag) {
				String firstLetter = Character.toString(scannedLine.charAt(0));
				if (map.keySet().contains(firstLetter)) {
					String newValue = map.get(firstLetter) + "|" + scannedLine.substring(2);
					map.put(firstLetter, newValue);
				} else {
					String newValue = "->" + scannedLine.substring(2);
					map.put(firstLetter, newValue);
				}
			}
		}

		for (String name : map.keySet()) {
			pRules += name + map.get(name) + "\n";
		}
		pRules = pRules.substring(0, pRules.length() - 1);
		return pRules;
	}

	public void setParameters(String input, int lineCount) {
		this.input = input;
		this.lineCount = lineCount;

	}

	public void CFGtoCNF() {
		String newStart = "S0";
		ArrayList<String> newRule = new ArrayList<>();
		newRule.add("S");

		rules.put(newStart, newRule);

		stringtoMap();
		
		for (int i = 0; i < lineCount; i++) {
			removeEpsilon();
		}

		Iterator itr3 = rules.entrySet().iterator();

		while (itr3.hasNext()) {
			Map.Entry entry = (Map.Entry) itr3.next();
			ArrayList<String> ruleRow = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < ruleRow.size(); i++) {
				if (ruleRow.get(i).contains(entry.getKey().toString())) {
					ruleRow.remove(entry.getKey().toString());
				}
			}
		}
		
		eliminator("single_variable");
		onlyTwoTerminalandOneVariable();
		eliminator("three_terminal");
	}

	private void eliminator(String eliminationSubject){
		for(int i = 0; i < lineCount; i++){
			if(eliminationSubject.equals("single_variable")){
				removeSingleVariable();
			}
			else if(eliminationSubject.equals("three_terminal")){
				removeThreeTerminal();
			}
		}
	}

	private String[] splitEnter(String input) {

		String[] tmpArray = new String[lineCount];
		for (int i = 0; i < lineCount; i++) {
			tmpArray = input.split("\\n");
		}
		return tmpArray;
	}

	private void stringtoMap() {

		String[] splitedEnterInput = splitEnter(input);

		for (int i = 0; i < splitedEnterInput.length; i++) {

			String[] tempString = splitedEnterInput[i].split("->|\\|");
			String variable = tempString[0].trim();

			String[] rule = Arrays.copyOfRange(tempString, 1, tempString.length);
			List<String> ruleList = new ArrayList<String>();

			for (int k = 0; k < rule.length; k++) {
				rule[k] = rule[k].trim();
			}
			for (int j = 0; j < rule.length; j++) {
				ruleList.add(rule[j]);
			}
			rules.put(variable, ruleList);
		}
	}

	private void removeEpsilon() {

		Iterator itr = rules.entrySet().iterator();
		Iterator itr2 = rules.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> ruleRow = (ArrayList<String>) entry.getValue();

			if (ruleRow.contains("e")) {
				if (ruleRow.size() > 1) {
					ruleRow.remove("e");
					foundEpsilon = entry.getKey().toString();

				} else {
					foundEpsilon = entry.getKey().toString();
					rules.remove(foundEpsilon);
				}
			}
		}

		while (itr2.hasNext()) {

			Map.Entry entry = (Map.Entry) itr2.next();
			ArrayList<String> ruleList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < ruleList.size(); i++) {
				String temp = ruleList.get(i);

				for (int j = 0; j < temp.length(); j++) {
					if (foundEpsilon.equals(Character.toString(ruleList.get(i).charAt(j)))) {

						if (temp.length() == 2) {

							// remove specific character in string
							temp = temp.replace(foundEpsilon, "");

							if (!rules.get(entry.getKey().toString()).contains(temp)) {
								rules.get(entry.getKey().toString()).add(temp);
							}

						} else if (temp.length() == 3) {

							String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

							if (!rules.get(entry.getKey().toString()).contains(deletedTemp)) {
								rules.get(entry.getKey().toString()).add(deletedTemp);
							}

						} else if (temp.length() == 4) {

							String deletedTemp = new StringBuilder(temp).deleteCharAt(j).toString();

							if (!rules.get(entry.getKey().toString()).contains(deletedTemp)) {
								rules.get(entry.getKey().toString()).add(deletedTemp);
							}
						} else {

							if (!rules.get(entry.getKey().toString()).contains("e")) {
								rules.get(entry.getKey().toString()).add("e");
							}
						}
					}
				}
			}
		}
	}

	private void removeSingleVariable() {

		Iterator itr4 = rules.entrySet().iterator();
		String key = null;

		while (itr4.hasNext()) {

			Map.Entry entry = (Map.Entry) itr4.next();
			Set set = rules.keySet();
			ArrayList<String> keySet = new ArrayList<String>(set);
			ArrayList<String> ruleList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < ruleList.size(); i++) {
				String temp = ruleList.get(i);

				for (int j = 0; j < temp.length(); j++) {

					for (int k = 0; k < keySet.size(); k++) {
						if (keySet.get(k).equals(temp)) {

							key = entry.getKey().toString();
							List<String> ruleValue = rules.get(temp);
							ruleList.remove(temp);
							for (int l = 0; l < ruleValue.size(); l++) {
								if (!rules.get(key).contains(ruleValue.get(l)))
									rules.get(key).add(ruleValue.get(l));
							}
						}
					}
				}
			}
		}
	}

	private Boolean checkDuplicateInRuleList(Map<String, List<String>> map, String key) {

		Boolean notFound = true;

		Iterator itr = map.entrySet().iterator();
		outerloop:

		while (itr.hasNext()) {

			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> ruleList = (ArrayList<String>) entry.getValue();

			for (int i = 0; i < ruleList.size(); i++) {
				if (ruleList.size() < 2) {

					if (ruleList.get(i).equals(key)) {
						notFound = false;
						break outerloop;
					} else {
						notFound = true;
					}
				}
			}
		}

		return notFound;
	}

	private void onlyTwoTerminalandOneVariable() {

		Iterator itr5 = rules.entrySet().iterator();
		String key = null;
		int asciiBegin = 71;

		Map<String, List<String>> tempList = new LinkedHashMap<>();

		while (itr5.hasNext()) {

			Map.Entry entry = (Map.Entry) itr5.next();
			Set set = rules.keySet();

			ArrayList<String> keySet = new ArrayList<String>(set);
			ArrayList<String> ruleList = (ArrayList<String>) entry.getValue();
			Boolean found1 = false;
			Boolean found2 = false;
			Boolean found = false;

			for (int i = 0; i < ruleList.size(); i++) {
				String temp = ruleList.get(i);

				for (int j = 0; j < temp.length(); j++) {

					if (temp.length() == 3) {

						String newRule = temp.substring(1, 3);

						if (checkDuplicateInRuleList(tempList, newRule) && checkDuplicateInRuleList(rules, newRule)) {
							found = true;
						} else {
							found = false;
						}

						if (found) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newRule);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}

					} else if (temp.length() == 2) {

						for (int k = 0; k < keySet.size(); k++) {

							if (!keySet.get(k).equals(Character.toString(ruleList.get(i).charAt(j)))) {
								found = false;

							} else {
								found = true;
								break;
							}

						}

						if (!found) {
							String newRule = Character.toString(ruleList.get(i).charAt(j));

							if (checkDuplicateInRuleList(tempList, newRule)
									&& checkDuplicateInRuleList(rules, newRule)) {

								ArrayList<String> newVariable = new ArrayList<>();
								newVariable.add(newRule);
								key = Character.toString((char) asciiBegin);

								tempList.put(key, newVariable);

								asciiBegin++;

							}
						}
					} else if (temp.length() == 4) {

						String newRule1 = temp.substring(0, 2); 
						String newRule2 = temp.substring(2, 4); 

						if (checkDuplicateInRuleList(tempList, newRule1)
								&& checkDuplicateInRuleList(rules, newRule1)) {
							found1 = true;
						} else {
							found1 = false;
						}

						if (checkDuplicateInRuleList(tempList, newRule2)
								&& checkDuplicateInRuleList(rules, newRule2)) {
							found2 = true;
						} else {
							found2 = false;
						}

						if (found1) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newRule1);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}

						if (found2) {
							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newRule2);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);
							asciiBegin++;
						}
					} else if (temp.length() == 1) {
						String newRule = Character.toString(ruleList.get(i).charAt(j));
						if (checkDuplicateInRuleList(tempList, newRule)
								&& checkDuplicateInRuleList(rules, newRule)) {

							ArrayList<String> newVariable = new ArrayList<>();
							newVariable.add(newRule);
							key = Character.toString((char) asciiBegin);

							tempList.put(key, newVariable);

							asciiBegin++;

						}
					}
				}
			}
		}
		rules.putAll(tempList);
	}

	private void removeThreeTerminal() {

		Iterator itr = rules.entrySet().iterator();
		ArrayList<String> keyList = new ArrayList<>();
		Iterator itr2 = rules.entrySet().iterator();

		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			ArrayList<String> ruleRow = (ArrayList<String>) entry.getValue();

			if (ruleRow.size() < 2) {
				keyList.add(entry.getKey().toString());
			}
		}

		while (itr2.hasNext()) {

			Map.Entry entry = (Map.Entry) itr2.next();
			ArrayList<String> ruleList = (ArrayList<String>) entry.getValue();

			if (ruleList.size() > 1) {
				for (int i = 0; i < ruleList.size(); i++) {
					String temp = ruleList.get(i);

					for (int j = 0; j < temp.length(); j++) {

						if (temp.length() > 2) {
							String stringToBeReplaced1 = temp.substring(j, temp.length());
							String stringToBeReplaced2 = temp.substring(0, temp.length() - j);

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = rules.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								if (stringToBeReplaced1.equals(value)) {

									rules.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(stringToBeReplaced1, key);

									if (!rules.get(entry.getKey().toString()).contains(temp)) {
										rules.get(entry.getKey().toString()).add(i, temp);
									}
								} else if (stringToBeReplaced2.equals(value)) {

									rules.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(stringToBeReplaced2, key);

									if (!rules.get(entry.getKey().toString()).contains(temp)) {
										rules.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						} else if (temp.length() == 2) {

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = rules.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								for (int pos = 0; pos < temp.length(); pos++) {
									String tempChar = Character.toString(temp.charAt(pos));

									if (value.equals(tempChar)) {

										rules.get(entry.getKey().toString()).remove(temp);
										temp = temp.replace(tempChar, key);

										if (!rules.get(entry.getKey().toString()).contains(temp)) {
											rules.get(entry.getKey().toString()).add(i, temp);
										}
									}
								}
							}
						} else if (temp.length() == 1) {

							for (String key : keyList) {

								List<String> keyValues = new ArrayList<>();
								keyValues = rules.get(key);
								String[] values = keyValues.toArray(new String[keyValues.size()]);
								String value = values[0];

								if (value.equals(temp)) {

									rules.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(temp, key);

									if (!rules.get(entry.getKey().toString()).contains(temp)) {
										rules.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						}

					}
				}
			} else if (ruleList.size() == 1) {

				for (int i = 0; i < ruleList.size(); i++) {
					String temp = ruleList.get(i);

					if (temp.length() == 2) {

						for (String key : keyList) {

							List<String> keyValues = new ArrayList<>();
							keyValues = rules.get(key);
							String[] values = keyValues.toArray(new String[keyValues.size()]);
							String value = values[0];

							for (int pos = 0; pos < temp.length(); pos++) {
								String tempChar = Character.toString(temp.charAt(pos));

								if (value.equals(tempChar)) {

									rules.get(entry.getKey().toString()).remove(temp);
									temp = temp.replace(tempChar, key);

									if (!rules.get(entry.getKey().toString()).contains(temp)) {
										rules.get(entry.getKey().toString()).add(i, temp);
									}
								}
							}
						}

					}
				}
			}
		}
	}

	public void printResult() {
		Set nonterminals = rules.keySet();
		List<String> terminals = new ArrayList<String>();
		for (String key : rules.keySet()) {
			List<String> values = rules.get(key);
			for (String value : values) {
				if(value.length() == 1) {
					if(Character.isLowerCase(value.charAt(0)) || Character.isDigit(value.charAt(0))) {
						terminals.add(value);
					}
				}
			}
		}
		String nonterminalSection = "NON-TERMINAL\n";
		for (Object nonTerminal : nonterminals) {
			nonterminalSection += nonTerminal.toString() + "\n";
		}
		
		String terminalSection = "TERMINAL\n";
		for (String terminal : terminals) {
			terminalSection += terminal + "\n";
		}
		String rulesSection = "RULES\n";
		for (String key : rules.keySet()) {
			for (String value : rules.get(key)) {
				rulesSection += key + ":" + value + "\n";
			}
		}
		String firstElement = "START\n" + (String)nonterminals.iterator().next();
		System.out.println(nonterminalSection + terminalSection + rulesSection + firstElement);
		
	}
}
