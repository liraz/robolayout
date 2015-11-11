package org.lirazs.robolayout.core.resource;

import org.robovm.apple.foundation.NSCache;
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
public final class XMLCache {
    private static XMLCache sharedInstance;
    static public XMLCache getSharedInstance() {
        if(sharedInstance == null) {
            sharedInstance = new XMLCache();
        }
        return sharedInstance;
    }

    private NSCache cache;

    protected XMLCache() {
        cache = new NSCache();
    }

    public Document getXML(NSURL url) throws IOException {
        Document document = null;

        NSData nsData = (NSData) cache.get(url.getAbsoluteString());

        try {
            if(nsData == null) { // trying to fetch (does not exist in cache)
                nsData = NSData.read(url);

                if(nsData == null) {
                    if(url.isFileURL())
                        throw new FileNotFoundException(url.getAbsoluteString());
                } else {
                    cache.put(url.getAbsoluteString(), nsData);
                }
            }

            if(nsData != null) {
                DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();

                byte[] bytes = nsData.getBytes();
                if(bytes != null) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                    DocumentBuilder db = dfactory.newDocumentBuilder();
                    InputSource inputSource = new InputSource(inputStream);
                    document = db.parse(inputSource);
                }
            }
        } catch (SAXException | ParserConfigurationException e) {
            e.printStackTrace();
        }

        return document;
    }

    public void purge() {
        cache.clear();
    }
}
