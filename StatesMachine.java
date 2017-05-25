import java.io.*;
import java.util.*;

public class StatesMachine{
    private Map<String, State> states;
    private String input;
    private float noise_rate;
    private String emit;

    public StatesMachine(String input, float noise){
        this.states = new HashMap<String, State>();
        this.states.put("00", new State("00", "00", "11", "00", "10"));
        this.states.put("01", new State("01", "11", "00", "00", "10"));
        this.states.put("10", new State("10", "10", "01", "01", "11"));
        this.states.put("11", new State("11", "01", "10", "01", "11"));
        this.input = input + "00";
        this.noise_rate = noise;
        this.emit = "";
    }

    public void run(){
        encoder("00" , input);
        noise();
    }

    public void encoder(String state, String in){
        if(in.equals("")) return;

        emit += states.get(state).get_emit(in.charAt(0));
        state = states.get(state).get_next(in.charAt(0));
        encoder(state , in.substring(1, in.length()));
    }

    public void noise(){
        Random gerador = new Random();
        String noisy = "";

        for(int i = 0; i < this.emit.length(); i++){
            if(gerador.nextDouble() < this.noise_rate){
                if(this.emit.charAt(i) == '0')
                    noisy += "1";
                else
                    noisy += "0";
            }
            else
                noisy += this.emit.charAt(i);
        }
        this.emit = noisy;
    }

}
