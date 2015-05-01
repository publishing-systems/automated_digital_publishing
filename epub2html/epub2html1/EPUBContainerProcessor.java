/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of epub2html1.
 *
 * epub2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epub2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epub2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/EPUBContainerProcessor.java
 * @brief Processor to operate on the 'META-INF/container.xml' file of the
 *     unpacked EPUB file.
 * @author Stephan Kreutzer
 * @since 2014-07-12
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



class EPUBContainerProcessor
{
    public EPUBContainerProcessor(File containerFile)
    {
        this.containerFile = containerFile;
        this.containerInfo = new HashMap<String, String>();
    }
    
    public int Run()
    {
        this.containerInfo.clear();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.containerFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getLocalPart();

                    if (fullElementName.equalsIgnoreCase("rootfile") == true)
                    {
                        Attribute attributeMediaType = event.asStartElement().getAttributeByName(new QName("media-type"));
                        
                        if (attributeMediaType == null)
                        {
                            continue;
                        }

                        if (!attributeMediaType.getValue().equalsIgnoreCase("application/oebps-package+xml"))
                        {
                            continue;
                        }

                        Attribute attributeFullPath = event.asStartElement().getAttributeByName(new QName("full-path"));
                                
                        if (attributeFullPath == null)
                        {
                            continue;
                        }
                        
                        if (attributeFullPath.getValue().contains("..") == true)
                        {
                            System.out.println("epub2html1: Invalid OPF path '" + attributeFullPath.getValue() + "'.");
                            System.exit(-1);
                        }
                        
                        if (this.containerInfo.containsKey("opfPath") != true)
                        {
                            this.containerInfo.put("opfPath", attributeFullPath.getValue());
                        }
                        else
                        {
                            System.out.println("epub2html1: OPF path specified more than once.");
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
    
    public Map<String, String> GetContainerInfo()
    {
        return this.containerInfo;
    }

    private File containerFile;
    private Map<String, String> containerInfo;
}
