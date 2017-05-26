import java.io.*;
import java.util.*;

public class Line{
    public String state;
    public Integer erro;
    public String decoded;



    public Line(String state, String decoded, Integer erro){
        this.state = state;
        this.decoded = decoded;
        this.erro = erro;
    }
}
