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
 * @file $/ODTContentProcessor.java
 * @brief Processor to operate on the 'content.xml' file of the unpacked
 *     ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-20
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



class ODTContentProcessor
{
    public ODTContentProcessor(File contentFile)
    {
        this.contentFile = contentFile;
        this.contentInfo = new HashMap<String, String>();
    }
    
    public int Analyze()
    {
        this.contentInfo.clear();
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.contentFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:document-content") == true)
                    {
                        Attribute attributeVersion = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_OFFICE_NAMESPACE_URI, "version", "office"));
                                
                        if (attributeVersion != null)
                        {
                            String contentVersion = attributeVersion.getValue();
                            
                            if (this.contentInfo.containsKey("contentVersion") != true)
                            {
                                this.contentInfo.put("contentVersion", contentVersion);
                            }
                            else
                            {
                                System.out.println("odt2html1: ODT content version specified more than once.");
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
    
    public int Run(File xhtmlOutFile)
    {
        this.contentInfo.clear();
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.contentFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(xhtmlOutFile.getAbsolutePath()),
                                    "UTF8"));


            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
            writer.write("<!-- This file was created by odt2html1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
            /** @todo: Specify language of the content with lang-attribute. */
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("  <head>\n");
            /** @todo: Provide title of the document. */
            writer.write("    <title></title>\n");
            writer.write("  </head>\n");
            writer.write("  <body>\n");


            boolean body = false;
            boolean text = false;
            boolean paragraph = false;
            

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:body") == true)
                    {
                        body = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("office:text") == true &&
                             body == true)
                    {
                        text = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:p") == true &&
                             body == true &&
                             text == true)
                    {
                        paragraph = true;
                        
                        writer.write("<p>");
                    }
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:body") == true)
                    {
                        paragraph = false;
                        text = false;
                        body = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("office:text") == true &&
                             body == true)
                    {
                        paragraph = false;
                        text = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:p") == true &&
                             body == true &&
                             text == true)
                    {
                        writer.write("</p>");
                    
                        paragraph = false;
                    }
                }
                else if (event.isCharacters() == true)
                {
                    if (paragraph == true)
                    {
                        event.writeAsEncodedUnicode(writer);
                    }
                } 
            }
            
            writer.write("\n");
            writer.write("  </body>\n");
            writer.write("</html>\n");
            
            writer.flush();
            writer.close();
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
        catch (IOException ex)
        {
            ex.printStackTrace();
            return -3;
        }
        
        return 0;
    }
    
    public Map<String, String> GetContentInfo()
    {
        return this.contentInfo;
    }

    static final String ODT_CONTENT_OFFICE_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";

    private File contentFile;
    private Map<String, String> contentInfo;
}
