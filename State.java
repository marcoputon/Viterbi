import java.io.*;
import java.util.*;

public class State{
    private String number;
    private String zero;
    private String one;
    private String next_zero;
    private String next_one;



    public State(String num, String zero, String one, String nz, String no){
        this.number = num;

        this.zero = zero;
        this.one = one;

        this.next_zero = nz;
        this.next_one = no;
    }

    public String get_next(char in){
        if(in == '0'){
            return this.next_zero;
        } else {
            return this.next_one;
        }
    }

    public String get_emit(char in){
        if(in == '0'){
            return this.zero;
        } else {
            return this.one;
        }
    }

}
