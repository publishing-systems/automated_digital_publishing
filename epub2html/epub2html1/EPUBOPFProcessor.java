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
 * @file $/EPUBOPFProcessor.java
 * @brief Processor to operate on the OPF file of the unpacked EPUB file.
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
import java.util.ArrayList;
import java.io.IOException;



class EPUBOPFProcessor
{
    public EPUBOPFProcessor(File opfFile, File epubDirectory)
    {
        this.opfFile = opfFile;
        this.epubDirectory = epubDirectory;
        this.opfInfo = new HashMap<String, String>();
        this.manifest = new HashMap<String, OPFManifestEntry>();
        this.order = new ArrayList<String>();
    }
    
    public int Run()
    {
        this.opfInfo.clear();
        this.manifest.clear();
        this.order.clear();

        /**
         * @todo Check this.opfFile, this.epubDirectory (and that the first
         *     is a subdirectory of the latter).
         */
        
        {
            String version = GetVersion();
            
            if (version.length() <= 0)
            {
                System.out.println("epub2html1: No OPF version specified.");
                return -1;
            }
            
            if (!version.equalsIgnoreCase("2.0"))
            {
                System.out.println("epub2html1: OPF version 2.0 expected, version '" + version + "' found.");
                return -1;
            }
        }

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.opfFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            boolean manifest = false;
            boolean spine = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getLocalPart();
                    
                    if (elementName.getPrefix().length() > 0)
                    {
                        fullElementName = elementName.getPrefix() + ":" + fullElementName;
                    }

                    if (fullElementName.equalsIgnoreCase("manifest") == true)
                    {
                        manifest = true;
                        spine = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("item") == true &&
                             manifest == true)
                    {
                        Attribute attributeID = event.asStartElement().getAttributeByName(new QName("id"));
                        Attribute attributeHRef = event.asStartElement().getAttributeByName(new QName("href"));
                        Attribute attributeMediaType = event.asStartElement().getAttributeByName(new QName("media-type"));
                        
                        if (attributeID == null)
                        {
                            System.out.println("epub2html1: OPF manifest item in '" + this.opfFile.getAbsolutePath() + "' is missing its ID.");
                            return -1;
                        }
                        
                        if (attributeHRef == null)
                        {
                            System.out.println("epub2html1: OPF manifest item in '" + this.opfFile.getAbsolutePath() + "' is missing its reference.");
                            return -1;
                        }
                        
                        if (attributeMediaType == null)
                        {
                            System.out.println("epub2html1: OPF manifest item in '" + this.opfFile.getAbsolutePath() + "' is missing its media type.");
                            return -1;
                        }

                        File referencedFile = new File(attributeHRef.getValue());
                        
                        if (referencedFile.isAbsolute() == true)
                        {
                            System.out.println("epub2html1: Absolute path '" + attributeHRef.getValue() + "' specified in OPF manifest.");
                            return -1;
                        }
                        
                        referencedFile = new File(this.opfFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + attributeHRef.getValue());
                        
                        try
                        {
                            if (referencedFile.getCanonicalPath().substring(0, this.epubDirectory.getCanonicalPath().length()).equalsIgnoreCase(this.epubDirectory.getCanonicalPath()) != true)
                            {
                                System.out.println("epub2html1: OPF manifest item '" + referencedFile.getCanonicalPath() + "' isn't located in EPUB directory '" + this.epubDirectory.getCanonicalPath() + "'.");
                                return -1;
                            }
                        }
                        catch (IOException ex)
                        {
                            ex.printStackTrace();
                            System.exit(-1);
                        }
                        
                        if (referencedFile.exists() != true)
                        {
                            System.out.println("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in OPF '" + this.opfFile.getAbsolutePath() + "', doesn't exist.");
                            return -1;
                        }

                        if (referencedFile.isFile() != true)
                        {
                            System.out.println("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in OPF '" + this.opfFile.getAbsolutePath() + "', isn't a file.");
                            return -1;
                        }

                        if (referencedFile.canRead() != true)
                        {
                            System.out.println("epub2html1: '" + referencedFile.getAbsolutePath() + "', referenced in OPF '" + this.opfFile.getAbsolutePath() + "', isn't readable.");
                            return -1;
                        }

                        String id = attributeID.getValue();
                        
                        if (this.manifest.containsKey(id) != true)
                        {
                            this.manifest.put(id, new OPFManifestEntry(id,
                                                                       referencedFile,
                                                                       attributeMediaType.getValue()));
                        }
                        else
                        {
                            System.out.println("epub2html1: OPF manifest item ID '" + attributeID.getValue() + "' specified more than once in '" + this.opfFile.getAbsolutePath() + "'.");
                            return -1;
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("spine") == true)
                    {
                        spine = true;
                        manifest = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("itemref") == true &&
                             spine == true)
                    {
                        Attribute attributeIDRef = event.asStartElement().getAttributeByName(new QName("idref"));
                        
                        if (attributeIDRef == null)
                        {
                            System.out.println("epub2html1: OPF spine item in '" + this.opfFile.getAbsolutePath() + "' is missing its ID reference.");
                            return -1;
                        }
                        
                        if (this.order.contains(attributeIDRef.getValue()) != true)
                        {
                            this.order.add(attributeIDRef.getValue());
                        }
                        else
                        {
                            System.out.println("epub2html1: OPF spine item ID reference '" + attributeIDRef.getValue() + "' specified more than once in '" + this.opfFile.getAbsolutePath() + "'.");
                            return -1;
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getLocalPart();
                    
                    if (elementName.getPrefix().length() > 0)
                    {
                        fullElementName = elementName.getPrefix() + ":" + fullElementName;
                    }

                    if (fullElementName.equalsIgnoreCase("manifest") == true)
                    {
                        manifest = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("spine") == true)
                    {
                        spine = false;
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
    
    public String GetVersion()
    {
        return GetVersion(false);
    }
    
    public String GetVersion(boolean refresh)
    {
        if (this.opfInfo.containsKey("version") == true &&
            refresh != true)
        {
            return this.opfInfo.get("version");
        }
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.opfFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getLocalPart();
                    
                    if (elementName.getPrefix().length() > 0)
                    {
                        fullElementName = elementName.getPrefix() + ":" + fullElementName;
                    }

                    if (fullElementName.equalsIgnoreCase("package") == true)
                    {
                        Attribute attributeVersion = event.asStartElement().getAttributeByName(new QName("version"));
                        
                        if (attributeVersion != null)
                        {
                            this.opfInfo.put("version", attributeVersion.getValue());
                            return attributeVersion.getValue();
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return "";
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            return "";
        }
        
        return "";
    }
    
    public Map<String, String> GetOPFInfo()
    {
        return this.opfInfo;
    }
    
    public Map<String, OPFManifestEntry> GetManifest()
    {
        return this.manifest;
    }
    
    public ArrayList<String> GetOrder()
    {
        return this.order;
    }

    private File opfFile;
    private File epubDirectory;
    private Map<String, String> opfInfo;
    private Map<String, OPFManifestEntry> manifest;
    private ArrayList<String> order;
}

