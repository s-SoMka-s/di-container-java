package implementation.bean.XML;

import implementation.bean.Bean;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class ParserXMLHandler extends DefaultHandler implements Constants {
    private String id = null;
    private String aClass = null;
    private String lifeCycle = null;

    private String currentTagName;

    ArrayList<Bean> beans = new ArrayList<>();

    public ArrayList<Bean> getBeans() {
        return beans;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        currentTagName = qName;

        if (attributes.getValue(ATTR_ID) != null) {
            id = attributes.getValue(ATTR_ID).trim();
        }
        if (attributes.getValue(ATTR_CLASS) != null) {
            aClass = attributes.getValue(ATTR_CLASS).trim();
        }
        if (attributes.getValue(ATTR_LIFECYCLE) != null) {
            lifeCycle = attributes.getValue(ATTR_LIFECYCLE).trim();
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(TAG_BEAN)) {
             validateBean();

            try {
                beans.add(new Bean(Class.forName(aClass), id, lifeCycle));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            clearVariables();
        }

        currentTagName = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
    }

    private void clearVariables() {
        id = null;
        aClass = null;
        lifeCycle = null;
    }

    // Убеждемся, что пользователь корректно указал бин.
    private void validateBean() {

    }
}
