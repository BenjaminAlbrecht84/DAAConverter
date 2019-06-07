package converter;

import io.DaaHit;
import io.DaaReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Converter {

    private static DaaReader daaReader;

    public static void toBlastPairwiseFormat(File daaFile, int cores) {
        ArrayList<DaaHit> allHits = parseDaaFile(daaFile, cores);

        for (DaaHit h : allHits) {
            System.out.println(getAccession(h));
            System.out.println(h.getTotalQueryDNA());
            System.out.println(h.getAlignment()[0] + "\n" + h.getAlignment()[1]);
        }

    }

    public static void toBlastXMLFormat(File daaFile, int cores) {

    }

    public static void toBLASTTabFormat(File daaFile, int cores) {

    }

    private static ArrayList<DaaHit> parseDaaFile(File daaFile, int cores) {
        daaReader = new DaaReader(daaFile, 0L, false);
        return daaReader.parseAllHits(cores);
    }

    private static String getAccession(DaaHit daaHit) {
        String acc = "UNKNOWN";
        try {
            acc = new String(daaReader.getHeader().getReferenceName(daaHit.getSubjectID()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return acc;
    }

}
