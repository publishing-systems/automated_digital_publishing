/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2html1.
 *
 * odt2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/ODTManifestProcessor.java
 * @brief Processor to operate on the 'META-INF/manifest.xml' file of the
 *     unpacked ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-19
 */



import java.io.File;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import java.util.HashMap;



class ODTManifestProcessor
{
    public ODTManifestProcessor(File manifestFile)
    {
        this.manifestFile = manifestFile;
        this.manifestInfo = new HashMap<String, String>();
    }
    
    public int Run()
    {
        this.manifestInfo.clear();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.manifestFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("manifest:file-entry") == true)
                    {
                        Attribute attributeFullPath = event.asStartElement().getAttributeByName(new QName(ODT_MANIFEST_NAMESPACE_URI, "full-path", "manifest"));
                                
                        if (attributeFullPath != null)
                        {
                            if (attributeFullPath.getValue().equalsIgnoreCase("/") == true)
                            {
                                Attribute attributeVersion = event.asStartElement().getAttributeByName(new QName(ODT_MANIFEST_NAMESPACE_URI, "version", "manifest"));
                                
                                if (attributeVersion != null)
                                {
                                    String manifestVersion = attributeVersion.getValue();
                                    
                                    if (this.manifestInfo.containsKey("manifestVersion") != true)
                                    {
                                        this.manifestInfo.put("manifestVersion", manifestVersion);
                                    }
                                    else
                                    {
                                        System.out.println("odt2html1: ODT manifest version specified more than once.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return -1;
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            return -2;
        }
        
        return 0;
    }
    
    public Map<String, String> GetManifestInfo()
    {
        return this.manifestInfo;
    }

    static final String ODT_MANIFEST_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:manifest:1.0";

    private File manifestFile;
    private Map<String, String> manifestInfo;
}
