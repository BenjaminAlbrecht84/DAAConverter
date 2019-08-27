package converter;

import io.BlastXML2File;
import io.BlastXMLFile;
import io.DaaHit;
import io.DaaReader;
import utils.BlastStatisticsHelper;

import javax.xml.transform.TransformerException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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

    public static void toBlastXMLFormat(File daaFile, int cores, String filepath) {
        ArrayList<DaaHit> allHits = parseDaaFile(daaFile, cores);

        initializeStatisticsHelper();

        BlastXMLFile blastXMLFile = new BlastXMLFile();
        blastXMLFile.initializeFile("blastx", "UNKNOWN", "UNKNOWN", "UNKNOWN", "UNKNOWN",
                "UNKNOWN", String.valueOf(allHits.get(0).getTotalQueryLength()));
        blastXMLFile.addUsedProgramParameters(daaReader.getHeader().getScoreMatrixName(), "UNKNOWN",
                String.valueOf(daaReader.getHeader().getGapOpen()), String.valueOf(daaReader.getHeader().getGapExtend()),
                "UNKNOWN");

        int iteration = 1;
        for (String queryId : daaReader.getReadId2Hits().keySet()) {
            blastXMLFile.addHitIteration(iteration, daaReader.getReadId2Hits().get(queryId));
            iteration++;
        }

        try {
            blastXMLFile.writeXML(filepath);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void toBlastXML2Format(File daaFile, int cores, String filepath) {
        ArrayList<DaaHit> allHits = parseDaaFile(daaFile, cores);

        initializeStatisticsHelper();

        BlastXML2File blastXML2File = new BlastXML2File();

        int iteration = 1;
        for (String queryId : daaReader.getReadId2Hits().keySet()) {
            blastXML2File.addQueryEntry(daaReader.getReadId2Hits().get(queryId), "blastx", "TEST",
                    "UNKNOWN", "UNKNOWN", daaReader.getHeader().getScoreMatrixName(), "TEST",
                    String.valueOf(daaReader.getHeader().getGapOpen()), String.valueOf(daaReader.getHeader().getGapExtend()),
                    "UNKNOWN", "");
            iteration++;
        }

        try {
            blastXML2File.writeXML(filepath);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void toBlastTabFormat(File daaFile, int cores, String outFile, boolean writeHeader) throws IOException {
        ArrayList<DaaHit> allHits = parseDaaFile(daaFile, cores);

        initializeStatisticsHelper();

        String header = "qseqid" + "\t" + "qlen" + "\t" + "sseqid" + "\t" + "slen" + "\t"
                + "qstart" + "\t" + "qend" + "\t" + "sstart" + "\t" + "send" + "\t" + "qseq" + "\t" + "sseq" + "\t"
                + "full_sseq" + "\t" + "evalue" + "\t" + "bitscore" + "\t" + "score" + "\t" + "length" + "\t"
                + "pident" + "\t" + "nident" + "\t" + "mismatch" + "\t" + "positive" + "\t" + "gapopen" + "\t"
                + "gaps" + "\t" + "ppos" + "\t" + "qframe" + "\n";

        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

        if (writeHeader) {
            writer.write(header);
        }

        for (DaaHit hit : allHits) {

            String seq1 = hit.getAlignment()[0];
            String seq2 = hit.getAlignment()[1];


            String qseqid = hit.getQueryName(); // qseqid
            int qlen = hit.getTotalQueryLength(); // qlen

            String sseqid = getAccession(hit); // sseqid
//            String sallseqid = getAccession(hit); // sallseqid
            int slen = hit.getTotalRefLength(); //slen

            int qstart = hit.getQueryStart() + 1; // qstart

            int qend;

            if (hit.getFrame() < 0) {
                qend = hit.getQueryStart() - hit.getQueryLength() + 2; // qend
            } else {
                qend = hit.getQueryStart() + hit.getQueryLength(); // qend
            }

            int sstart = hit.getRefStart() + 1; // sstart
            int send = hit.getRefStart() + hit.getRefLength(); // send

            String qseq = hit.getQueryDNA(); // qseq
            String sseq = null; // sseq
            String full_sseq = null; // full_sseq

            double evalue = BlastStatisticsHelper.getEValue(hit.getRawScore(), hit.getQueryLength()); // evalue
            double bitscore = BlastStatisticsHelper.getBitScore(hit.getRawScore()); // bitscore
            int score = hit.getRawScore(); // score

            int length = hit.getRefLength(); // length

            int nident = 0; // nident
            int mismatch = 0; // mismatch

            for (int i = 0; i < length; i++) {
                if (seq1.charAt(i) != '-' && seq1.charAt(i) != '/' && seq1.charAt(i) != '\\' && seq2.charAt(i) != '-' && seq2.charAt(i) != '/' && seq2.charAt(i) != '\\') {
                    if (seq1.charAt(i) == seq2.charAt(i)) {
                        nident++;
                    } else {
                        mismatch++;
                    }
                }
            }

            double pident = computePercentIdentity(nident, length); // pident

            int positive = 0; // positive (for now this is not used)

            int gapopen = 0; // gapopen
            int gaps = 0; // gaps

            boolean insideGap = false;

            for (int i = 0; i < length; i++) {
                if (seq1.charAt(i) == '-' && !insideGap) {
                    insideGap = true;
                    gapopen++;
                } else if (seq1.charAt(i) == '-' && insideGap) {
                    gaps++;
                } else if (seq1.charAt(i) != '-') {
                    insideGap = false;
                }
            }

            insideGap = false;

            for (int i = 0; i < length; i++) {
                if (seq2.charAt(i) == '-' && !insideGap) {
                    insideGap = true;
                    gapopen++;
                    gaps++;
                } else if (seq2.charAt(i) == '-' && insideGap) {
                    gaps++;
                } else if (seq2.charAt(i) != '-') {
                    insideGap = false;
                }
            }

            double ppos = 0.0; // ppos

            int qframe = hit.getFrame(); // qframe

//            String btop = null; // btop

//            String staxids = null; // staxids

//            String stitle = hit.getReferenceName(); // stitle
//            String salltitles = hit.getReferenceName(); // salltitles

//            double qcovhsp = 0.0; // qcovhsp

//            String qtitle = hit.getQueryName(); // qtitle

            String lineToWrite = qseqid + "\t" + qlen + "\t" + sseqid + "\t" + slen + "\t" + qstart
                    + "\t" + qend + "\t" + sstart + "\t" + send + "\t" + qseq + "\t" + sseq + "\t" + full_sseq + "\t"
                    + evalue + "\t" + bitscore + "\t" + score + "\t" + length + "\t" + pident + "\t" + nident + "\t"
                    + mismatch + "\t" + positive + "\t" + gapopen + "\t" + gaps + "\t" + ppos + "\t" + qframe + "\n";

            writer.write(lineToWrite);

        }

        writer.close();
    }

    private static void initializeStatisticsHelper() {
        String matrix = daaReader.getHeader().getScoreMatrixName();
        int gapOpen = daaReader.getHeader().getGapOpen();
        int gapExtend = daaReader.getHeader().getGapExtend();
        long dbLetters = daaReader.getHeader().getDbLetters().longValue();

        if (matrix != null) {
            if (matrix.startsWith("blosum45") || matrix.startsWith("Blosum45") || matrix.startsWith("BLOSUM45")) {
                BlastStatisticsHelper.init("BLOSUM45", gapOpen, gapExtend, dbLetters);
            } if (matrix.startsWith("blosum50") || matrix.startsWith("Blosum50") || matrix.startsWith("BLOSUM50")) {
                BlastStatisticsHelper.init("BLOSUM50", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("blosum62") || matrix.startsWith("Blosum62") || matrix.startsWith("BLOSUM62")) {
                BlastStatisticsHelper.init("BLOSUM62", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("blosum80") || matrix.startsWith("Blosum80") || matrix.startsWith("BLOSUM80")) {
                BlastStatisticsHelper.init("BLOSUM80", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("blosum90") || matrix.startsWith("Blosum90") || matrix.startsWith("BLOSUM90")) {
                BlastStatisticsHelper.init("BLOSUM90", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("pam30") || matrix.startsWith("Pam30") || matrix.startsWith("PAM30")) {
                BlastStatisticsHelper.init("PAM30", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("pam70") || matrix.startsWith("Pam70") || matrix.startsWith("PAM70")) {
                BlastStatisticsHelper.init("PAM70", gapOpen, gapExtend, dbLetters);
            } else if (matrix.startsWith("pam250") || matrix.startsWith("Pam250") || matrix.startsWith("PAM250")) {
                BlastStatisticsHelper.init("PAM250", gapOpen, gapExtend, dbLetters);
            } else {
                System.err.println("Warning: ScoringMatrix could not be identified! \n" +
                        "For the calculation of the Bitscore and E-Value the BLOSUM62 matrix will be used with the default values for GapOpen (11) and GapExtend (1).");
                BlastStatisticsHelper.init("BLOSUM62", 11,1, dbLetters);
            }
        } else {
            System.err.println("Warning: ScoringMatrix could not be identified! \n" +
                    "For the calculation of the Bitscore and E-Value the BLOSUM62 matrix will be used with the default values for GapOpen (11) and GapExtend (1).");
            BlastStatisticsHelper.init("BLOSUM62", 11,1, dbLetters);
        }
    }

    private static ArrayList<DaaHit> parseDaaFile(File daaFile, int cores) {
        daaReader = new DaaReader(daaFile, 0L, false);

        daaReader.getHeader().print();

        return daaReader.parseAllHits(cores);
    }

    private static double round(double numToRound, int numDigits) {
        DecimalFormat df = new DecimalFormat("#." + String.format("%" + numDigits + "c", ' ').replaceAll(" ", "\\" + '#'));
        df.setRoundingMode(RoundingMode.HALF_UP);

        return Double.parseDouble(df.format(numToRound));
    }

    private static double computePercentIdentity(int numMatches, int length) {

        return ((numMatches * 100) / (double) length);
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

    private static double getK() {

        return daaReader.getHeader().getK();
    }

    private static double getLambda() {

        return daaReader.getHeader().getLambda();
    }

}
