import java.io.*;
import java.util.*;

public class StatesMachine{
    private Map<String, State> states;
    private List<Decoded> decodeds;
    private String input;
    private float noise_rate;
    private String emit;

    public StatesMachine(String input, float noise){
        this.states = new HashMap<String, State>();
        this.decodeds = new ArrayList<Decoded>();
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
        decoder("00", emit, "", 0);
        ordenar();
    }


    public void encoder(String state, String in){
        if(in.equals("")) return;

        emit += states.get(state).get_emit(in.charAt(0));
        state = states.get(state).get_next(in.charAt(0));
        encoder(state , in.substring(1, in.length()));
    }


    public void decoder(String state, String encoded, String decoded, Integer erro){
        if(encoded.equals("")){
            decodeds.add(new Decoded(decoded, erro));
            return;
        }

        String atual = encoded.substring(0, 2);
        State s = states.get(state);
        Integer erro0 = erro;
        Integer erro1 = erro;

        //  Por 0
        String emited = s.get_emit('0');
        if(emited.charAt(0) != atual.charAt(0)) erro0 += 1;
        if(emited.charAt(1) != atual.charAt(1)) erro0 += 1;

        //  Por 1
        emited = s.get_emit('1');
        if(emited.charAt(0) != atual.charAt(0)) erro0 += 1;
        if(emited.charAt(1) != atual.charAt(1)) erro1 += 1;

        if(s.get_next_z().equals(s.get_next_o())){
            if(erro0 <= erro1)
                decoder(s.get_next_z(), encoded.substring(2, encoded.length()), decoded + "0", erro0);
            else
                decoder(s.get_next_o(), encoded.substring(2, encoded.length()), decoded + "1", erro1);
        }
        else{
            decoder(s.get_next_z(), encoded.substring(2, encoded.length()), decoded + "0", erro0);
            decoder(s.get_next_o(), encoded.substring(2, encoded.length()), decoded + "1", erro1);
        }
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


    public void print_decodeds(){
        for(Decoded d : decodeds){
            System.out.println(d.value + " - " + d.erro);
        }
    }


    public void ordenar(){
        Collections.sort(this.decodeds, new Comparator<Decoded>() {
    		@Override
    		public int compare(Decoded o1, Decoded o2) {
    			return o1.erro - o2.erro;
    		}
    	});
    }
}
