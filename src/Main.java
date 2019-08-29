import converter.Converter;
import io.DaaReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        File daaFile = new File(args[0]);
        int cores = Integer.parseInt(args[1]);

//        Converter.toBlastPairwiseFormat(daaFile, cores);

        DaaReader daaReader = new DaaReader(daaFile, 0L, false);

        try {
            Converter.toBlastTabFormat(daaFile, cores, "res/test7.tab", true);
            Converter.toBlastXMLFormat(daaFile, cores, "res/test7.xml");
            Converter.toBlastXML2Format(daaFile, cores, "res/test_new7.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = docBuilder.newDocument();
        doc.setXmlVersion("1.0");
        Element root = doc.createElement("BlastXML2");
        root.setAttributeNS("http://www.ncbi.nlm.nih.gov",
                "xs:schemaLocation", "http://www.ncbi.nlm.nih.gov http://www.ncbi.nlm.nih.gov/data_specs/schema_alt/NCBI_BlastOutput2.xsd");
        root.appendChild(doc.createElement("foo"));
        doc.appendChild(root);
// see result
        DOMImplementationLS dls = (DOMImplementationLS) doc.getImplementation();
        System.out.println(dls.createLSSerializer().writeToString(doc));



        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = documentBuilder.newDocument();

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();

            document.setXmlVersion("1.0");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            Element blastOutput = document.createElement("BlastOutput");
            blastOutput.setAttributeNS("http://www.ncbi.nlm.nih.gov",
                    "xs:schemaLocation", "http://www.ncbi.nlm.nih.gov http://www.ncbi.nlm.nih.gov/data_specs/schema_alt/NCBI_BlastOutput2.xsd");
            Element blastOutputParam = document.createElement("BlastOutput_param");
            Element blastOutputIterations = document.createElement("BlastOutput_iterations");

            blastOutput.appendChild(blastOutputParam);
            blastOutput.appendChild(blastOutputIterations);

            document.appendChild(blastOutput);

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(System.out);

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }*/
    }

}
