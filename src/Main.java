import converter.Converter;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        File daaFile = new File(args[0]);
        int cores = Integer.parseInt(args[1]);

//        Converter.toBlastPairwiseFormat(daaFile, cores);
        Converter.toBlastTabFormat(daaFile, cores);
    }

}
