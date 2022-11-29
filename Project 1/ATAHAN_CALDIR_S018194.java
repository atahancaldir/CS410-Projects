import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ATAHAN_CALDIR_S018194 {

    static String NFAFileName = "NFA1.txt";
    static ArrayList<String> alphabet = new ArrayList<String>();

    public static HashMap<String, HashMap<String, ArrayList<String>>> faNullConverter(HashMap<String, HashMap<String, ArrayList<String>>> FA, ArrayList<String> alphabet){
        boolean addNullState = false;
        for(String state: FA.keySet()){
            for(String symbol: alphabet){
                if(FA.get(state).get(symbol).size() == 0){
                    FA.get(state).get(symbol).add("Ø");
                    addNullState = true;
                }
            }
        }
        if(addNullState){
            FA.put("Ø", new HashMap<String, ArrayList<String>>());
            for(String symbol : alphabet){
                FA.get("Ø").put(symbol, new ArrayList<String>(Arrays.asList("Ø")));
            }
        }
        return FA;
    }

    public static void main(String[] args) {
        ArrayList<String> keywords = new ArrayList<String>(
            Arrays.asList("ALPHABET", "STATES", "START", "FINAL", "TRANSITIONS", "END"));
        
        HashMap<String, HashMap<String, ArrayList<String>>> NFA = new HashMap<String, HashMap<String, ArrayList<String>>>();
        HashMap<String, HashMap<String, ArrayList<String>>> DFA = new HashMap<String, HashMap<String, ArrayList<String>>>();

        String start_state = "";
        ArrayList<String> NFAFinalStates = new ArrayList<String>();
        ArrayList<String> DFAFinalStates = new ArrayList<String>();

        try {
            Scanner reader = new Scanner(new File(NFAFileName));
            String reader_key = "";
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if(keywords.contains(data)){
                    reader_key = data;
                    continue;
                }
                if(reader_key.equals("ALPHABET")){
                    alphabet.add(data);
                }
                else if(reader_key.equals("STATES")){
                    NFA.put(data, new HashMap<String, ArrayList<String>>());
                    for(String symbol : alphabet){
                        NFA.get(data).put(symbol, new ArrayList<String>());
                    }
                }
                else if(reader_key.equals("START")){
                    start_state = data;
                    DFA.put(start_state, new HashMap<String, ArrayList<String>>());
                    for(String symbol : alphabet){
                        DFA.get(start_state).put(symbol, new ArrayList<String>());
                    }
                }
                else if(reader_key.equals("FINAL")){
                    NFAFinalStates.add(data);
                }
                else if(reader_key.equals("TRANSITIONS")){
                    String[] transition = data.split(" ");
                    NFA.get(transition[0]).get(transition[1]).add(transition[2]);
                    if(transition[0].equals(start_state)){
                        DFA.get(start_state).get(transition[1]).add(transition[2]);
                    }
                }
                else{
                    break;
                }
            }
            reader.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        ArrayList<String> checkedStates = new ArrayList<String>();

        boolean allChecked = false;

        while(!allChecked){
            allChecked = true;

            List<String> listKeySet = new ArrayList<>(DFA.keySet());

            for(String state: listKeySet){
                for(String symbol : alphabet){
                    if(checkedStates.contains(state+symbol)){
                        continue;
                    }
                    allChecked = false;

                    if(DFA.get(state).get(symbol).size() > 1){
                        String newStateName = "";
                        ArrayList<String> sortedStateNames = new ArrayList<>(DFA.get(state).get(symbol));
                        Collections.sort(sortedStateNames);
                        for(String stateName : sortedStateNames){
                            newStateName += stateName;
                        }

                        ArrayList<String> DFATargetStates = DFA.get(state).get(symbol);
                        DFA.get(state).put(symbol, new ArrayList<String>(Arrays.asList(newStateName)));

                        if(!DFA.keySet().contains(newStateName)){
                            for(String NFAFinalState : NFAFinalStates){
                                if(DFATargetStates.contains(NFAFinalState) && !DFAFinalStates.contains(newStateName)){
                                    DFAFinalStates.add(newStateName);
                                }
                            }

                            DFA.put(newStateName, new HashMap<String, ArrayList<String>>());
                            for(String symbol2 : alphabet){
                                DFA.get(newStateName).put(symbol2, new ArrayList<String>());
                                for(String stateName : DFATargetStates){
                                    for(String targetState : NFA.get(stateName).get(symbol2)){
                                        if(!DFA.get(newStateName).get(symbol2).contains(targetState)){
                                            DFA.get(newStateName).get(symbol2).add(targetState);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else if(DFA.get(state).get(symbol).size() == 1){
                        if(!DFA.keySet().contains(DFA.get(state).get(symbol).get(0))){
                            DFA.put(DFA.get(state).get(symbol).get(0), new HashMap<String, ArrayList<String>>());
                            for(String symbol2 : alphabet){
                                DFA.get(DFA.get(state).get(symbol).get(0)).put(symbol2, NFA.get(DFA.get(state).get(symbol).get(0)).get(symbol2));
                            }
                        }
                    }

                    checkedStates.add(state+symbol);
                }
            }
        }

        DFA = faNullConverter(DFA, alphabet);

        for(String keyword : keywords){
            System.out.println(keyword);
            if(keyword.equals("ALPHABET")){
                for(String symbol : alphabet){
                    System.out.println(symbol);
                }
            }
            else if(keyword.equals("STATES")){
                for(String state : DFA.keySet()){
                    System.out.println(state);
                }
            }
            else if(keyword.equals("START")){
                System.out.println(start_state);
            }
            else if(keyword.equals("FINAL")){
                for(String finalState : DFAFinalStates){
                    System.out.println(finalState);
                }
            }
            else if(keyword.equals("TRANSITIONS")){
                for(String state : DFA.keySet()){
                    for(String symbol : alphabet){
                        if(DFA.get(state).get(symbol).size() != 0){
                            System.out.println(state + " " + symbol + " " + DFA.get(state).get(symbol).get(0));
                        }
                    }
                }
            }
        }

    }
}