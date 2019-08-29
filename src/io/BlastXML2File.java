package io;

import com.sun.corba.se.spi.monitoring.StringMonitoredAttributeBase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utils.BlastStatisticsHelper;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;

public class BlastXML2File {

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder documentBuilder;
    private Document document;
    private TransformerFactory transformerFactory;
    private Transformer transformer;
    private Element blastXML2;


    public BlastXML2File() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();

            transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();

            document.setXmlVersion("1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            blastXML2 = document.createElement("BlastXML2");
            blastXML2.setAttributeNS("http://www.ncbi.nlm.nih.gov",
                    "xs:schemaLocation", "http://www.ncbi.nlm.nih.gov http://www.ncbi.nlm.nih.gov/data_specs/schema_alt/NCBI_BlastOutput2.xsd");

            document.appendChild(blastXML2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addQueryEntry(ArrayList<DaaHit> daaHits, String program, String version, String reference,
                              String targetDatabase, String matrix, String expect, String gapOpen, String gapExtend,
                              String filter, String errorCode, BigInteger dbLetters, long dbSeqs) {
        Element blastOutput2 = document.createElement("BlastOutput2");

        blastOutput2.appendChild(addBlastOutput2Report(daaHits, program, version, reference, targetDatabase, matrix,
                expect, gapOpen, gapExtend, filter, dbLetters, dbSeqs));
        blastOutput2.appendChild(addBlastOutput2Error(errorCode));

        blastXML2.appendChild(blastOutput2);
    }

    private Element addBlastOutput2Report(ArrayList<DaaHit> daaHits, String program, String version, String reference,
                                          String targetDatabase, String matrix, String expect, String gapOpen, String gapExtend,
                                          String filter, BigInteger dbLetters, long dbSeqs) {
        Element blastOutput2_report = document.createElement("BlastOutput2_report");
        Element report = document.createElement("Report");

        Element reportProgram = document.createElement("Report_program");
        Element reportVersion = document.createElement("Report_version");
        Element reportReference = document.createElement("Report_reference");

        reportProgram.setTextContent(program);
        reportVersion.setTextContent(version);
        reportReference.setTextContent(reference);

        report.appendChild(reportProgram);
        report.appendChild(reportVersion);
        report.appendChild(reportReference);
        report.appendChild(addReportSearchTarget(targetDatabase));
        report.appendChild(addUsedProgramParameters(matrix, expect, gapOpen, gapExtend, filter));
        report.appendChild(addSearchResults(daaHits, dbLetters, dbSeqs));

        blastOutput2_report.appendChild(report);

        return blastOutput2_report;
    }

    private Element addBlastOutput2Error(String errorCode) {
        Element blastOutput2_error = document.createElement("BlastOutput2_error");
        Element err = document.createElement("Err");
        Element errCode = document.createElement("Err_code");

        errCode.setTextContent(errorCode);

        err.appendChild(errCode);
        blastOutput2_error.appendChild(err);

        return blastOutput2_error;
    }

    private Element addReportSearchTarget(String targetDatabase) {
        Element reportSearchTarget = document.createElement("Report_search-target");
        Element target = document.createElement("Target");
        Element targetDB = document.createElement("Target_db");

        targetDB.setTextContent(targetDatabase);
        target.appendChild(targetDB);
        reportSearchTarget.appendChild(target);

        return reportSearchTarget;
    }

    private Element addUsedProgramParameters(String matrix, String expect, String gapOpen, String gapExtend, String filter) {
        Element reportParams = document.createElement("Report_params");
        Element parameters = document.createElement("Parameters");

        Element parametersMatrix = document.createElement("Parameters_matrix");
        Element parametersExpect = document.createElement("Parameters_expect");
        Element parametersGapOpen = document.createElement("Parameters_gap-open");
        Element parametersGapExtend = document.createElement("Parameters_gap-extend");
        Element parametersFilter = document.createElement("Parameters_filter");

        parametersMatrix.setTextContent(matrix);
        parametersExpect.setTextContent(expect);
        parametersGapOpen.setTextContent(gapOpen);
        parametersGapExtend.setTextContent(gapExtend);
        parametersFilter.setTextContent(filter);

        parameters.appendChild(parametersMatrix);
        parameters.appendChild(parametersExpect);
        parameters.appendChild(parametersGapOpen);
        parameters.appendChild(parametersGapExtend);
        parameters.appendChild(parametersFilter);

        reportParams.appendChild(parameters);

        return reportParams;
    }

    private Element addSearchResults(ArrayList<DaaHit> daaHits, BigInteger dbLetters, long dbSeqs) {
        Element reportResults = document.createElement("Report_results");
        Element results = document.createElement("Results");
        Element resultsSearch = document.createElement("Results_search");
        Element search = document.createElement("Search");
        Element searchQueryID = document.createElement("Search_query-id");
        Element searchQueryTitle = document.createElement("Search_query-title");
        Element searchQueryLen = document.createElement("Search_query-len");
        Element searchQueryMasking = document.createElement("Search_query-masking");
        Element searchHits = document.createElement("Search_hits");

        int hitIter = 1;
        for (DaaHit daaHit : daaHits) {
            Element hit = document.createElement("Hit");

            Element hitNum = document.createElement("Hit_num");
            Element hitId = document.createElement("Hit_id");
            Element hitDef = document.createElement("Hit_def");
            Element hitAccession = document.createElement("Hit_accession");
            Element hitLen = document.createElement("Hit_len");

            Element hitHsps = document.createElement("Hit_hsps");

            Element hsp = document.createElement("Hsp");

            Element hspNum = document.createElement("Hsp_num");
            Element hspBitScore = document.createElement("Hsp_bit-score");
            Element hspScore = document.createElement("Hsp_score");
            Element hspEvalue = document.createElement("Hsp_evalue");
            Element hspQueryFrom = document.createElement("Hsp_query-from");
            Element hspQueryTo = document.createElement("Hsp_query-to");
            Element hspHitFrom = document.createElement("Hsp_hit-from");
            Element hspHitTo = document.createElement("Hsp_hit-to");
            Element hspQueryFrame = document.createElement("Hsp_query-frame");
            Element hspHitFrame = document.createElement("Hsp_hit-frame");
            Element hspIdentity = document.createElement("Hsp_identity");
            Element hspPositive = document.createElement("Hsp_positive");
            Element hspGaps = document.createElement("Hsp_gaps");
            Element hspAlignLen = document.createElement("Hsp_align-len");
            Element hspQseq = document.createElement("Hsp_qseq");
            Element hspHseq = document.createElement("Hsp_hseq");
            Element hspMidline = document.createElement("Hsp_midline");

            hitNum.setTextContent(String.valueOf(hitIter));
            hitId.setTextContent(daaHit.getReferenceName());
            hitDef.setTextContent("UNKNOWN");
            hitAccession.setTextContent("UNKNOWN");
            hitLen.setTextContent(String.valueOf(daaHit.getTotalRefLength()));

            hspNum.setTextContent(String.valueOf(1));
            hspBitScore.setTextContent(String.valueOf(BlastStatisticsHelper.getBitScore(daaHit.getRawScore())));
            hspScore.setTextContent(String.valueOf(daaHit.getRawScore()));
            hspEvalue.setTextContent(String.valueOf(BlastStatisticsHelper.getEValue(daaHit.getRawScore(), daaHit.getQueryLength())));
            hspQueryFrom.setTextContent(String.valueOf(daaHit.getQueryStart() + 1));
            if (daaHit.getFrame() < 0) {
                hspQueryTo.setTextContent(String.valueOf(daaHit.getQueryStart() - daaHit.getQueryLength() + 2)); // qend
            } else {
                hspQueryTo.setTextContent(String.valueOf(daaHit.getQueryStart() + daaHit.getQueryLength())); // qend
            }
            hspHitFrom.setTextContent(String.valueOf(daaHit.getRefStart() + 1));
            hspHitTo.setTextContent(String.valueOf(daaHit.getRefStart() + daaHit.getRefLength()));
            hspQueryFrame.setTextContent(String.valueOf(daaHit.getFrame()));
            hspHitFrame.setTextContent(String.valueOf(0));

            String seq1 = daaHit.getAlignment()[0];
            String seq2 = daaHit.getAlignment()[1];

            int nident = 0; // nident

            for (int i = 0; i < daaHit.getRefLength(); i++) {
                if (seq1.charAt(i) != '-' && seq1.charAt(i) != '/' && seq1.charAt(i) != '\\' && seq2.charAt(i) != '-' && seq2.charAt(i) != '/' && seq2.charAt(i) != '\\') {
                    if (seq1.charAt(i) == seq2.charAt(i)) {
                        nident++;
                    }
                }
            }

            hspIdentity.setTextContent(String.valueOf(nident));
            hspPositive.setTextContent("UNKNOWN");

            int gapopen = 0; // gapopen
            int gaps = 0; // gaps

            boolean insideGap = false;

            for (int i = 0; i < daaHit.getRefLength(); i++) {
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

            for (int i = 0; i < daaHit.getRefLength(); i++) {
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

            hspGaps.setTextContent(String.valueOf(gaps));
            hspAlignLen.setTextContent(String.valueOf(daaHit.getAlignment()[0].length()));
            hspQseq.setTextContent(seq1);
            hspHseq.setTextContent(seq2);
            hspMidline.setTextContent("UNKNOWN");

            hsp.appendChild(hspNum);
            hsp.appendChild(hspBitScore);
            hsp.appendChild(hspScore);
            hsp.appendChild(hspEvalue);
            hsp.appendChild(hspQueryFrom);
            hsp.appendChild(hspQueryTo);
            hsp.appendChild(hspHitFrom);
            hsp.appendChild(hspHitTo);
            hsp.appendChild(hspQueryFrame);
            hsp.appendChild(hspHitFrame);
            hsp.appendChild(hspIdentity);
            hsp.appendChild(hspPositive);
            hsp.appendChild(hspGaps);
            hsp.appendChild(hspAlignLen);
            hsp.appendChild(hspQseq);
            hsp.appendChild(hspHseq);
            hsp.appendChild(hspMidline);

            hitHsps.appendChild(hsp);

            hit.appendChild(hitNum);
            hit.appendChild(hitId);
            hit.appendChild(hitDef);
            hit.appendChild(hitAccession);
            hit.appendChild(hitLen);
            hit.appendChild(hitHsps);

            searchHits.appendChild(hit);

            hitIter++;
        }

        search.appendChild(searchQueryID);
        search.appendChild(searchQueryTitle);
        search.appendChild(searchQueryLen);
        search.appendChild(searchQueryMasking);
        search.appendChild(searchHits);
        search.appendChild(addSearchStats(dbLetters, dbSeqs));

        resultsSearch.appendChild(search);
        results.appendChild(resultsSearch);
        reportResults.appendChild(results);

        return reportResults;
    }

    private Element addSearchStats(BigInteger dbLetters, long dbSeqs) {
        Element searchStat = document.createElement("Search_stat");

        Element statistics = document.createElement("Statistics");

        Element statisticsDbNum = document.createElement("Statistics_db-num");
        Element statisticsDbLen = document.createElement("Statistics_db-len");
        Element statisticsHspLen = document.createElement("Statistics_hsp-len");
        Element statisticsEffSpace = document.createElement("Statistics_eff-space");
        Element statisticsKappa = document.createElement("Statistics_kappa");
        Element statisticsLambda = document.createElement("Statistics_lambda");
        Element statisticsEntropy = document.createElement("Statistics_entropy");

        statisticsDbNum.setTextContent(String.valueOf(dbSeqs));
        statisticsDbLen.setTextContent(String.valueOf(dbLetters));
        statisticsHspLen.setTextContent(String.valueOf(0));
        statisticsEffSpace.setTextContent(String.valueOf(0));
        statisticsKappa.setTextContent(String.valueOf(BlastStatisticsHelper.K));
        statisticsLambda.setTextContent(String.valueOf(BlastStatisticsHelper.LAMBDA));
        statisticsEntropy.setTextContent(String.valueOf(0));

        statistics.appendChild(statisticsDbNum);
        statistics.appendChild(statisticsDbLen);
        statistics.appendChild(statisticsHspLen);
        statistics.appendChild(statisticsEffSpace);
        statistics.appendChild(statisticsKappa);
        statistics.appendChild(statisticsLambda);
        statistics.appendChild(statisticsEntropy);

        searchStat.appendChild(statistics);

        return searchStat;
    }

    public void writeXML(String filepath) throws TransformerException {
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File(filepath));
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(domSource, streamResult);
    }
}
