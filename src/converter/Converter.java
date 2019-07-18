package converter;

import io.DaaHit;
import io.DaaReader;
import utils.BlastStatisticsHelper;

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

    public static void toBlastXMLFormat(File daaFile, int cores) {

    }

    public static void toBlastTabFormat(File daaFile, int cores, String outFile, boolean writeHeader) throws IOException {
        ArrayList<DaaHit> allHits = parseDaaFile(daaFile, cores);

        String matrix = daaReader.getHeader().getScoreMatrixName();

        if (matrix != null) {
            if (matrix.startsWith("blosum62") || matrix.startsWith("Blosum62") || matrix.startsWith("BLOSUM62")) {
                BlastStatisticsHelper.init("BLOSUM62", daaReader.getHeader().getGapOpen(),
                        daaReader.getHeader().getGapExtend(), daaReader.getHeader().getDbLetters().longValue());
            } else {
                System.err.println("Warning: ScoringMatrix could not be identified! \n" +
                        "For the calculation of the Bitscore and E-Value the BLOSUM62 matrix will be used.");
                BlastStatisticsHelper.init("BLOSUM62", daaReader.getHeader().getGapOpen(),
                        daaReader.getHeader().getGapExtend(), daaReader.getHeader().getDbLetters().longValue());
            }
        } else {
            System.err.println("Warning: ScoringMatrix could not be identified! \n" +
                    "For the calculation of the Bitscore and E-Value the BLOSUM62 matrix will be used.");
            BlastStatisticsHelper.init("BLOSUM62", daaReader.getHeader().getGapOpen(),
                    daaReader.getHeader().getGapExtend(), daaReader.getHeader().getDbLetters().longValue());
        }

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
