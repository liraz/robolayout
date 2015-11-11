package org.lirazs.robolayout.core.util;

import org.robovm.apple.foundation.NSData;
import org.robovm.apple.foundation.NSURL;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created on 7/30/2015.
 */
public class XMLUtil {

    public static Document getXML(NSURL url) throws IOException {
        Document document = null;
        NSData nsData = NSData.read(url);

        if(nsData == null) {
            if(url.isFileURL())
                throw new FileNotFoundException(url.getAbsoluteString());
        }

        if(nsData != null) {
            document = getXML(nsData);
        }
        return document;
    }

    public static Document getXML(NSData nsData) {
        Document document = null;
        DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

        byte[] bytes = nsData.getBytes();
        if(bytes != null) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

            try {
                DocumentBuilder db = dfactory.newDocumentBuilder();
                InputSource inputSource = new InputSource(inputStream);
                document = db.parse(inputSource);
            } catch (SAXException | ParserConfigurationException | IOException e) {
                e.printStackTrace();
            }
        }
        return document;
    }
}
