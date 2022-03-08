package implementation.bean.XML;

import implementation.bean.Bean;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ClassApplicationContextFromXML {

    public ClassApplicationContextFromXML(String configLocation) {
        File file = new File(configLocation);

        ArrayList<Bean> beans = parse(file);

        var a = 5;
    }

    private ArrayList<Bean> parse(File file) {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        ParserXMLHandler handler = new ParserXMLHandler();
        SAXParser parser;

        try {
            parser = factory.newSAXParser();
        } catch (SAXException | ParserConfigurationException e) {
            System.out.println("Sax parser opening error " + e.getMessage());
            return null;
        }

        try {
            parser.parse(file, handler);
        } catch (SAXException e) {
            System.out.println("Sax parsing error " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.out.println("IO parsing error " + e.getMessage());
            return null;
        }

        return handler.getBeans();
    }

}
