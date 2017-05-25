
import java.io.*;
import java.util.*;

public class Main{
	public static void main(String[] args){
		try{
			float ruido = Float.valueOf(args[0]);
			System.out.println("Ruido: " + ruido);
			String entrada = args[1];
			System.out.println("Entrada: " + entrada);
		}
		catch(Exception e){
			System.out.println("erro: entrada inválida");
			System.exit(-1);
		}


        StatesMachine sm = new StatesMachine(args[1]);
        sm.run();
    }
}
