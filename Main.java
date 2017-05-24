
import java.io.*;
import java.util.*;

public class Main{
	public static void main(String[] args){
        StatesMachine sm = new StatesMachine(args[1]);
        sm.run();
    }
}
