package utils;

import java.util.ArrayList;
import java.util.HashMap;

public class BlastStatisticsHelper {

    public static long n;
    public static double lnK;
    public static double K;
    public static double LAMBDA;
    public static double LN2 = (float) Math.log(2);

    private static String[] table = new String[]{"Matrix open extension lambda K H",
            "BLOSUM80  11 2  0.365249  0.168504 0.350",
            "BLOSUM80  32767  32767  0.343  0.177  0.657",
            "BLOSUM80  25 2  0.342  0.170  0.660", "BLOSUM80  13 2  0.336  0.150  0.570", "BLOSUM80  9  2  0.319  0.110  0.420",
            "BLOSUM80  8  2  0.308  0.0900 0.350", "BLOSUM80  7  2  0.293  0.0700 0.270", "BLOSUM80  6  2  0.268  0.0450 0.190",
            "BLOSUM80  11 1  0.314  0.0950 0.350", "BLOSUM80  10 1  0.299  0.0710 0.270", "BLOSUM80  9  1  0.279  0.0480 0.200",
            "BLOSUM62  11 1  0.267  0.0410 0.140",
            "BLOSUM62  32767  32767  0.318  0.134  0.401", "BLOSUM62  11 2  0.297  0.0820 0.270", "BLOSUM62  10 2  0.291  0.0750 0.230",
            "BLOSUM62  9  2  0.279  0.0580 0.190", "BLOSUM62  8  2  0.264  0.0450 0.150", "BLOSUM62  7  2  0.239  0.0270 0.100",
            "BLOSUM62  6  2  0.201  0.0120 0.0610", "BLOSUM62  13 1  0.292  0.0710 0.230", "BLOSUM62  12 1  0.283  0.0590 0.190",
            "BLOSUM62  10 1  0.243  0.0240 0.100", "BLOSUM62  9  1  0.206  0.0100 0.0520",
            "BLOSUM50  32767  32767  0.232  0.112  0.336", "BLOSUM50  13 3  0.212  0.0630 0.190", "BLOSUM50  12 3  0.206  0.0550 0.170",
            "BLOSUM50  11 3  0.197  0.0420 0.140", "BLOSUM50  10 3  0.186  0.0310 0.110", "BLOSUM50  9  3  0.172  0.0220 0.0820",
            "BLOSUM50  16 2  0.215  0.0660 0.200", "BLOSUM50  15 2  0.210  0.0580 0.170", "BLOSUM50  14 2  0.202  0.0450 0.140",
            "BLOSUM50  13 2  0.193  0.0350 0.120", "BLOSUM50  12 2  0.181  0.0250 0.0950", "BLOSUM50  19 1  0.212  0.0570 0.180",
            "BLOSUM50  18 1  0.207  0.0500 0.150", "BLOSUM50  17 1  0.198  0.0370 0.120", "BLOSUM50  16 1  0.186  0.0250 0.100",
            "BLOSUM50  15 1  0.171  0.0150 0.0630", "BLOSUM45  32767  32767  0.229  0.0924 0.251", "BLOSUM45  13 3  0.207  0.0490 0.140",
            "BLOSUM45  12 3  0.199  0.0390 0.110", "BLOSUM45  11 3  0.190  0.0310 0.0950", "BLOSUM45  10 3  0.179  0.0230 0.0750",
            "BLOSUM45  16 2  0.210  0.0510 0.140", "BLOSUM45  15 2  0.203  0.0410 0.120", "BLOSUM45  14 2  0.195  0.0320 0.100",
            "BLOSUM45  13 2  0.185  0.0240 0.0840", "BLOSUM45  12 2  0.171  0.0160 0.0610", "BLOSUM45  19 1  0.205  0.0400 0.110",
            "BLOSUM45  18 1  0.198  0.0320 0.100", "BLOSUM45  17 1  0.189  0.0240 0.0790", "BLOSUM45  16 1  0.176  0.0160 0.0630",
            "BLOSUM90  32767  32767  0.335  0.190  0.755", "BLOSUM90  9  2  0.310  0.120  0.460", "BLOSUM90  8  2  0.300  0.0990 0.390",
            "BLOSUM90  7  2  0.283  0.0720 0.300", "BLOSUM90  6  2  0.259  0.0480 0.220", "BLOSUM90  11 1  0.302  0.0930 0.390",
            "BLOSUM90  10 1  0.290  0.0750 0.280", "BLOSUM90  9  1  0.265  0.0440 0.200", "PAM250	32767	32767	0.218	0.0877	0.287",
            "PAM250	15	3	0.205	0.049	0.13", "PAM250	17	2	0.204	0.047	0.12", "PAM250	14	3	0.200	0.043	0.12",
            "PAM250	21	1	0.204	0.045	0.11", "PAM250	16	2	0.198	0.038	0.11", "PAM250	20	1	0.199	0.037	0.10",
            "PAM250	13	3	0.194	0.036	0.10", "PAM250	15	2	0.191	0.031	0.087", "PAM250	12	3	0.186	0.029	0.085",
            "PAM250	19	1	0.192	0.029	0.083", "PAM250	14	2	0.182	0.024	0.073", "PAM250	18	1	0.183	0.021	0.070",
            "PAM250	11	3	0.174	0.020	0.070", "PAM250	13	2	0.171	0.017	0.059", "PAM250	17	1	0.171	0.014	0.052",
            "PAM30	32767	32767	0.336	0.277	1.82", "PAM30	10	1	0.309	0.15	0.88", "PAM30	7	2	0.305	0.15	0.87",
            "PAM30	6	2	0.287	0.11	0.68", "PAM30	9	1	0.294	0.11	0.61", "PAM30	5	2	0.264	0.079	0.45",
            "PAM30	8	1	0.270	0.072	0.40", "PAM70	32767	32767	0.328	0.222	1.11", "PAM70	8	2	0.301	0.12	0.54",
            "PAM70	11	1	0.305	0.12	0.52", "PAM70	7	2	0.286	0.093	0.43", "PAM70	10	1	0.291	0.91	0.41",
            "PAM70	6	2	0.264	0.064	0.29", "PAM70	9	1	0.270	0.060	0.28",
            "MINSUM90  20 4  0.310478  0.338102 0.350"};

    public static ArrayList<String> matrixList = new ArrayList<String>();
    public static HashMap<String, ArrayList<String>> matrix2penalties = new HashMap<String, ArrayList<String>>();

    static {
        for (int i = 1; i < table.length; i++) {
            String[] split = table[i].split("\\s+");
            String name = split[0];
            if (!matrixList.contains(name))
                matrixList.add(name);
            matrix2penalties.putIfAbsent(name, new ArrayList<String>());
            String penalties = split[1] + "/" + split[2];
            matrix2penalties.get(name).add(penalties);
        }
    }

    public static void init(String matrixType, int gapOpen, int gapExtend, long referenceLetters) {
        n = referenceLetters;
        double[] blastStatistics = getStatistics(matrixType, gapOpen, gapExtend);
        if (blastStatistics != null) {
            LAMBDA = blastStatistics[0];
            K = blastStatistics[1];
            lnK = Math.log(K);
        } else {
            LAMBDA = -1;
            K = -1;
            lnK = -1;
            System.exit(0);
        }
    }

    private static double[] getStatistics(String matrixType, int gapOpen, int gapExtend) {
        for (String line : table) {
            if (line.startsWith(matrixType)) {
                String[] tokens = line.split("\\s+");
                if (tokens.length == 6) {
                    int gop = Integer.parseInt(tokens[1]);
                    int gep = Integer.parseInt(tokens[2]);
                    if (gop == gapOpen && gep == gapExtend) {
                        double lambda = Double.parseDouble(tokens[3]);
                        double K = Double.parseDouble(tokens[4]);
                        double H = Double.parseDouble(tokens[5]);
                        double[] statstics = {lambda, K, H};
                        return statstics;
                    }
                }
            }
        }
        System.err.println("Unknowm combinations of matrix type and gap penalties - the known ones are: " + printTable());
        return null;
    }

    private static String printTable() {
        StringBuilder build = new StringBuilder();
        for (String s : table)
            build.append(s + "\n");
        return build.toString();
    }

    public static double getEValue(int alignmentScore, int queryLength) {
        return K * n * queryLength * Math.exp(-LAMBDA * alignmentScore);
    }

    public static double getBitScore(int alignmentScore) {
        return (LAMBDA * alignmentScore - lnK) / LN2;
    }

}
