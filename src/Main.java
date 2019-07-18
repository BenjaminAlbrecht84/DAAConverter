import converter.Converter;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        File daaFile = new File(args[0]);
        int cores = Integer.parseInt(args[1]);

//        Converter.toBlastPairwiseFormat(daaFile, cores);
        try {
            Converter.toBlastTabFormat(daaFile, cores, "res/test.tab", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
