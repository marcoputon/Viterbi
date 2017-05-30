import java.io.*;
import java.util.*;

public class StatesMachine{
    private Map<String, State> states;
    private List<Decoded> decodeds;
    private String input;
    private float noise_rate;
    private String emit0;
    private String emit;
    private HashMap<String, Line> lines;
    private PrintWriter writer;



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
        this.emit0 = "";
        this.emit = "";

        this.writer = null;
        try{
            writer = new PrintWriter("tabela.html", "UTF-8");
        }
        catch(Exception E){
            System.out.println("PERDI");
        }
    }


    public void run(){
        writer.println("<!DOCTYPE html>\n<html>\n<head><meta charset=\"utf-8\"></head>\n<body>\n<table style=\"width:100%\">");
        writer.printf("<p><b>erro:</b> %f</p>\n", this.noise_rate);
        writer.printf("<p><b>entrada:</b> %s</p>\n", this.input);
        writer.print("<table style=\"width:100%\">");

        encoder("00" , input);
        writer.print("<tr><td><b>Codificado</b></td></tr>\n");
        writer.printf("<tr><td><b>s/ erro: </b>%s</td></tr>\n", this.emit);
        writer.printf("<tr><td><b>c/ erro: </b>");
        noise();
        writer.printf("</td></tr>\n");
        writer.print("</table>\n");
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
                if(this.emit.charAt(i) == '0'){
                    noisy += "1";
                    writer.print("<font color=\"red\">1</font>");
                }
                else{
                    noisy += "0";
                    writer.print("<font color=\"red\">0</font>");
                }
            }
            else{
                noisy += this.emit.charAt(i);
                writer.print(this.emit.charAt(i));
            }
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

        System.out.printf ("%-30s %-10s %s\n", "<decod>", "<dif>", "<erro>");
        for(String key : lines.keySet()){
            dif = 0;
            l = lines.get(key);
            oi = l.decoded.substring(0, l.decoded.length()-2);

            for(int i = 0; i < oi.length(); i++){
                if(e.charAt(i) != oi.charAt(i))
                    dif += 1;
            }
            System.out.printf ("%-30s %-10d %d\n", l.decoded.substring(0, l.decoded.length()-2), dif, l.erro);
            //System.out.println(l.decoded + " | " + dif + " | " + l.erro);
        }
    }

    public void table(String e){
        writer.print("<br><table style=\"width:100%\">");

        Line l;
        Line best = null;
        Integer dif, dif_best = input.length() + 10;
        String oi = "";

        System.out.printf ("%-30s %-10s %s\n", "<decod>", "<dif>", "<erro>");
        writer.println("<tr>\n<td>" + "<b>Decodificado</b>" + "</td>\n<td>" + "<b>Diferença</b>" + "</td>\n<td>" + "<b>Erro</b>" + "</td>\n</td>");

        for(String key : lines.keySet()){
            writer.print("<tr>\n<td>");

            dif = 0;
            l = lines.get(key);
            oi = l.decoded.substring(0, l.decoded.length()-2);

            for(int i = 0; i < oi.length(); i++){
                if(e.charAt(i) != oi.charAt(i)){
                    dif += 1;
                    writer.print("<font color=\"red\">" + oi.charAt(i) + "</font>");
                }
                else{
                    writer.print(oi.charAt(i));
                }
            }
            System.out.printf ("%-30s %-10d %d\n", l.decoded.substring(0, l.decoded.length()-2), dif, l.erro);
            writer.println("</td>\n<td>" + dif + "</td>\n<td>" + l.erro + "</td>\n</tr>");

            if(dif < dif_best){
                dif_best = dif;
                best = lines.get(key);
            }
        }

        writer.println("</table>\n</body>\n</html>");

        writer.print("<h2><b>Resultado</b></h2>");
        for(int i = 0; i < best.decoded.length() - 2; i++){
            if(e.charAt(i) != best.decoded.charAt(i)){
                writer.print("<font color=\"red\">" + best.decoded.charAt(i) + "</font>");
            }
            else{
                writer.print(best.decoded.charAt(i));
            }
        }

        writer.close();
    }
}
