
import java.io.*;
import java.util.*;

public class Main{
	public static void main(String[] args){
		String entrada = "";
		float ruido = 0;
		try{
			ruido = Float.valueOf(args[0]);
			System.out.println("Ruido: " + ruido);
			entrada = args[1];
			System.out.println("Entrada: " + entrada);
		}
		catch(Exception e){
			System.out.println("erro: entrada inválida");
			System.exit(-1);
		}


        StatesMachine sm = new StatesMachine(entrada);
        sm.run();
    }
}
