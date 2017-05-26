import java.io.*;
import java.util.*;

public class StatesMachine{
    private Map<String, State> states;
    private List<Decoded> decodeds;
    private String input;
    private float noise_rate;
    private String emit;
    private HashMap<String, Line> lines;


    public StatesMachine(String input, float noise){
        this.states = new HashMap<String, State>();
        this.decodeds = new ArrayList<Decoded>();
        this.lines = new HashMap<String, Line>();
        this.lines.put("00", new Line("00", "", 0));

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
        System.out.println("\nCodificado: " + emit);
        noise();
        System.out.println("Com ruído:  " + emit + "\n");
        decoder(emit);
    }


    public void encoder(String state, String in){
        if(in.equals("")) return;

        emit += states.get(state).get_emit(in.charAt(0));
        state = states.get(state).get_next(in.charAt(0));
        encoder(state , in.substring(1, in.length()));
    }


    public void decoder(String encoded){
        if(encoded.equals("")){
            return;
        }
        HashMap<String, Line> iter_lines = new HashMap<String, Line>();
        Line l;

        for(String key : lines.keySet()){
            l = lines.get(key);
            String level_in = encoded.substring(0, 2);

            Line zero = this.next_level(l, level_in, '0');
            Line one  = this.next_level(l, level_in, '1');

            Line n = iter_lines.get(zero.state);
            if(n != null){
                if(zero.erro <= n.erro)
                    iter_lines.put(zero.state, zero);
            }
            else
                iter_lines.put(zero.state, zero);

            n = iter_lines.get(one.state);
            if(n != null){
                if(one.erro <= n.erro)
                    iter_lines.put(one.state, one);
            }
            else
                iter_lines.put(one.state, one);
        }
        lines.putAll(iter_lines);
        decoder(encoded.substring(2, encoded.length()));
    }


    public Line next_level(Line l, String e_string, char input){;
        State s = this.states.get(l.state);
        Integer erro = 0;

        if(e_string.charAt(0) != s.get_emit(input).charAt(0)) erro += 1;
        if(e_string.charAt(1) != s.get_emit(input).charAt(1)) erro += 1;

        return new Line(s.get_next(input), l.decoded + input, l.erro + erro);
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


    public void print_lines(){
        Line l;
        for(String key : lines.keySet()){
            l = lines.get(key);
            System.out.println(l.decoded + " - " + l.erro);
        }
    }

    public void compare(String e){
        Line l;
        Integer dif;
        String oi = "";

        for(String key : lines.keySet()){
            dif = 0;
            l = lines.get(key);
            oi = l.decoded.substring(0, l.decoded.length()-2);

            for(int i = 0; i < oi.length(); i++){
                if(e.charAt(i) != oi.charAt(i))
                    dif += 1;
            }

            System.out.println(l.decoded + " | " + dif);
        }
    }
}
