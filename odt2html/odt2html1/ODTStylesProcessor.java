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
 * @file $/ODTStylesProcessor.java
 * @brief Processor to operate on the 'styles.xml' file of the unpacked
 *     ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-22
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
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;



class ODTStylesProcessor
{
    public ODTStylesProcessor(File stylesFile)
    {
        this.stylesFile = stylesFile;
        this.stylesInfo = new HashMap<String, String>();
        this.styleMappings = new HashMap<String, String>();
    }

    public int Analyze()
    {
        this.stylesInfo.clear();
        this.styleMappings.clear();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.stylesFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            boolean styles = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:document-styles") == true)
                    {
                        Attribute attributeVersion = event.asStartElement().getAttributeByName(new QName(ODT_STYLES_OFFICE_NAMESPACE_URI, "version", "office"));
                                
                        if (attributeVersion != null)
                        {
                            String stylesVersion = attributeVersion.getValue();
                            
                            if (this.stylesInfo.containsKey("stylesVersion") != true)
                            {
                                this.stylesInfo.put("stylesVersion", stylesVersion);
                            }
                            else
                            {
                                System.out.println("odt2html1: ODT styles version specified more than once.");
                            }
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("office:styles") == true)
                    {
                        styles = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("style:style") == true &&
                             styles == true)
                    {
                        Attribute attributeInternalName = event.asStartElement().getAttributeByName(new QName(ODT_STYLES_STYLE_NAMESPACE_URI, "name", "style"));
                        Attribute attributeDisplayName = event.asStartElement().getAttributeByName(new QName(ODT_STYLES_STYLE_NAMESPACE_URI, "display-name", "style"));

                        if (attributeDisplayName == null)
                        {
                            attributeDisplayName = attributeInternalName;
                        }

                        if (attributeInternalName != null &&
                            attributeDisplayName != null)
                        {
                            String internalName = attributeInternalName.getValue();
                            String displayName = attributeDisplayName.getValue();
                            
                            //displayName = displayName.replaceAll(" ", "_");

                            if (this.styleMappings.containsKey(internalName) != true)
                            {
                                this.styleMappings.put(internalName, displayName);
                            }
                            else
                            {
                                System.out.println("odt2html1: ODT style '" + internalName + "' specified more than once.");
                            }
                        }
                    }   
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:styles") == true)
                    {
                        styles = false;
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
    
    public Map<String, String> GetStylesInfo()
    {
        return this.stylesInfo;
    }
    
    public Map<String, String> GetStyleMappings()
    {
        return this.styleMappings;
    }

    static final String ODT_STYLES_OFFICE_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    static final String ODT_STYLES_STYLE_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";

    private File stylesFile;
    private Map<String, String> stylesInfo;
    private Map<String, String> styleMappings;
}
