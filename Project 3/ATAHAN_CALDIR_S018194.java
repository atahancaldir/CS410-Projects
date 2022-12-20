import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class ATAHAN_CALDIR_S018194 {
    public static void main(String[] args)
	{
        Machine m = new Machine();

        try {
			Scanner reader = new Scanner(new File("Input_ATAHAN_CALDIR_S018194.txt"));

			m.buildMachine(reader);
            m.buildTape();
			m.startMachine(31);
            m.printResult();
		}
		catch (FileNotFoundException | InterruptedException e) {
			System.out.println(e);
			System.exit(0);
		}
    }
}

class Transition {
    String currentState;
    char read;
    char write;
    char shift;
    String nextState;

    Transition(String s){
        currentState = s.substring(0, 2);
        read = s.charAt(3);
        write = s.charAt(5);
        shift = s.charAt(7);
        nextState = s.substring(9);
    }

    boolean isEqual(Transition t){
        if(t.currentState.equals(this.currentState) &&
           t.read == this.read &&
           t.write == this.write &&
           t.shift == this.shift &&
           t.nextState.equals(this.nextState)){
            return true;
           }
           return false;
    }
}

class State {
	ArrayList<Transition> transitions;

	State(ArrayList<Transition> ts){ 
        transitions = ts; 
    }
}

class Machine {
    Scanner reader;

    int inputVarCount; // input variable count
    int tapeVarCount; // tape variables count
    char blankSymbol;
    int stateCount;
    int transitionCount;
    String currentState;
    String acceptState;
    String rejectState;
    String inputString;

    String loopState = "qLoop";

    String traversedStates = "";

    StringBuffer Tape;

    ArrayList<State> states = new ArrayList<>();

    HashMap<String, Integer> stateIdMap = new HashMap<>();

    void buildMachine(Scanner reader){
        this.reader = reader;

        inputVarCount = Integer.parseInt(reader.nextLine());
        reader.nextLine(); // the input alphabet
        tapeVarCount = Integer.parseInt(reader.nextLine());
        reader.nextLine(); // the tape alphabet
        blankSymbol = reader.nextLine().charAt(0);
        stateCount = Integer.parseInt(reader.nextLine());
        reader.nextLine(); // states
        currentState = reader.nextLine(); // currentState = start state (for the beginning)
        acceptState = reader.nextLine();
        rejectState = reader.nextLine();

        transitionCount = (stateCount-2)*(tapeVarCount+1);

        for(int i=0; i<stateCount-2; i++){
            addState(i);
        }

        inputString = reader.nextLine();
    }

    void addState(int stateId){
        ArrayList<Transition> transitions = new ArrayList<>();

        for(int j=0; j<tapeVarCount+1; j++){
            Transition transition = new Transition(reader.nextLine());
            transitions.add(transition);
        }

        stateIdMap.put(transitions.get(0).currentState, stateId);

        State state = new State(transitions);
        states.add(state);
    }

    void buildTape(){
        String tapeString = "$";

        for(int i=0; i<30; i++){
            tapeString += blankSymbol;
        }

        tapeString = tapeString.concat(inputString);

        for(int i=0; i<30; i++){
            tapeString += blankSymbol;
        }

        tapeString += '$';

        Tape = new StringBuffer(tapeString);
    }

    void startMachine(int index) throws InterruptedException{

        ArrayList<Transition> lastThreeTransitions = new ArrayList<>();

        while (!currentState.equals(acceptState) && !currentState.equals(rejectState)
                && !currentState.equals(loopState)){
            traversedStates = traversedStates + currentState + " ";
            
            State state = states.get(stateIdMap.get(currentState));

            for(Transition transition : state.transitions){
                if(transition.read == Tape.charAt(index)){
                    
                    if(lastThreeTransitions.size() == 3 &&
                       ((transition.isEqual(lastThreeTransitions.get(1)) &&
                       lastThreeTransitions.get(0).isEqual(lastThreeTransitions.get(2))) ||
                       (transition.isEqual(lastThreeTransitions.get(2))))){
        
                        currentState = loopState;
                        break;
                    }
                    
                    if(lastThreeTransitions.size() == 3){
                        lastThreeTransitions.set(0, lastThreeTransitions.get(1));
                        lastThreeTransitions.set(1, lastThreeTransitions.get(2));
                        lastThreeTransitions.set(2, transition);
                    }
                    else{
                        lastThreeTransitions.add(transition);
                    }

                    Tape.replace(index, index+1, 
                                String.valueOf(transition.write));
                    
                    currentState = transition.nextState;

                    if(transition.shift == 'R'){
                        index++;
                    }
                    else if(transition.shift == 'L'){
                        index--;
                    }
                    else{
                        throw new InterruptedException("ERROR: Wrong shift symbol!");
                    }

                    break;

                }
            }
        }

        traversedStates += currentState;
    }

    void printResult(){
        System.out.println("ROUT: " + traversedStates);
        
        if(currentState.equals(acceptState)){
            System.out.println("RESULT: accepted");
        }
        else if(currentState.equals(rejectState)){
            System.out.println("RESULT: rejected");
        }
        else{
            System.out.println("RESULT: looped");
        }
    }
}