/* Copyright (C) 2013-2014  Stephan Kreutzer
 *
 * This file is part of html2epub1.
 *
 * html2epub1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/ConfigProcessor.java
 * @brief Processor to read the configuration file.
 * @author Stephan Kreutzer
 * @since 2013-12-10
 */



import java.util.ArrayList;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;



class ConfigProcessor
{
    public ConfigProcessor(String configFilePath)
    {
        this.configFile = new File(configFilePath);
        this.inFiles = new ArrayList<File>();
        this.inFileTitles = new ArrayList<String>();
        this.outDirectory = null;
        this.metaData = new HashMap<String, String>();
        this.xhtmlSchemaValidation = true;
        this.xhtmlReaderDTDValidation = true;
        this.xhtmlReaderNamespaceProcessing = true;
        this.xhtmlReaderCoalesceAdjacentCharacterData = false;
        this.xhtmlReaderResolveExternalParsedEntities = true;
        this.xhtmlReaderUseDTDNotDTDFallback = true;
    }

    public int run()
    {
        if (this.configFile.exists() != true)
        {
            System.out.print("html2epub1: '" + this.configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (this.configFile.isFile() != true)
        {
            System.out.print("html2epub1: '" + this.configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (this.configFile.canRead() != true)
        {
            System.out.print("html2epub1: '" + this.configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }


        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext() == true)
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("inFile") == true)
                    {
                        Attribute title = event.asStartElement().getAttributeByName(new QName("title"));
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            File inFile = new File(event.asCharacters().getData());
                            
                            if (inFile.isAbsolute() != true)
                            {
                                inFile = new File(this.configFile.getAbsoluteFile().getParent() +
                                                  System.getProperty("file.separator") +
                                                  event.asCharacters().getData());
                            }

                            if (inFile.exists() != true)
                            {
                                System.out.print("html2epub1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
                                System.exit(-7);
                            }

                            if (inFile.isFile() != true)
                            {
                                System.out.print("html2epub1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
                                System.exit(-8);
                            }

                            if (inFile.canRead() != true)
                            {
                                System.out.print("html2epub1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
                                System.exit(-9);
                            }
                            
                            if (title == null)
                            {
                                System.out.print("html2epub1: Missing title for '" + inFile.getAbsolutePath() + "' in configuration.\n");
                                System.exit(-10);
                            }

                            this.inFiles.add(inFile);
                            this.inFileTitles.add(title.getValue());
                        }
                    }
                    else if (tagName.equalsIgnoreCase("outDirectory") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            if (this.outDirectory != null)
                            {
                                System.out.print("html2epub1: Multiple out directories defined. Last one will win.\n");
                            }

                            this.outDirectory = new File(event.asCharacters().getData());
                            
                            if (this.outDirectory.isAbsolute() != true)
                            {
                                this.outDirectory = new File(this.configFile.getAbsoluteFile().getParent() +
                                                             System.getProperty("file.separator") +
                                                             event.asCharacters().getData());
                            }

                            if (outDirectory.exists() != true)
                            {
                                System.out.print("html2epub1: '" + outDirectory.getAbsolutePath() + "' doesn't exist.\n");
                                System.exit(-11);
                            }

                            if (outDirectory.isDirectory() != true)
                            {
                                System.out.print("html2epub1: '" + outDirectory.getAbsolutePath() + "' isn't a directory.\n");
                                System.exit(-12);
                            }

                            if (outDirectory.canWrite() != true)
                            {
                                System.out.print("html2epub1: '" + outDirectory.getAbsolutePath() + "' isn't writable.\n");
                                System.exit(-13);
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("metaData") == true)
                    {
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();
                            
                            if (event.isStartElement() == true)
                            {
                                tagName = event.asStartElement().getName().getLocalPart();
                                event = eventReader.nextEvent();
                                
                                if (event.isCharacters() == true)
                                {
                                    if (this.metaData.containsKey(tagName) == true)
                                    {
                                        this.metaData.put(tagName, this.metaData.get(tagName) + "<separator/>" + event.asCharacters().getData());
                                    }
                                    else
                                    {
                                        this.metaData.put(tagName, event.asCharacters().getData());
                                    }
                                }
                            }
                            else if (event.isEndElement() == true)
                            {
                                tagName = event.asEndElement().getName().getLocalPart();
                                
                                if (tagName.equalsIgnoreCase("metaData") == true)
                                {
                                    break;
                                }
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlSchemaValidation") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlSchemaValidation = event.asCharacters().getData();

                            if (xhtmlSchemaValidation.equalsIgnoreCase("false") == true ||
                                xhtmlSchemaValidation.equalsIgnoreCase("0") == true)
                            {
                                this.xhtmlSchemaValidation = false;
                            }
                            else
                            {
                                this.xhtmlSchemaValidation = true;
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlReaderDTDValidation") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlReaderDTDValidation = event.asCharacters().getData();

                            if (xhtmlReaderDTDValidation.equalsIgnoreCase("false") == true ||
                                xhtmlReaderDTDValidation.equalsIgnoreCase("0") == true)
                            {
                                this.xhtmlReaderDTDValidation = false;
                            }
                            else
                            {
                                this.xhtmlReaderDTDValidation = true;
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlReaderNamespaceProcessing") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlReaderNamespaceProcessing = event.asCharacters().getData();

                            if (xhtmlReaderNamespaceProcessing.equalsIgnoreCase("false") == true ||
                                xhtmlReaderNamespaceProcessing.equalsIgnoreCase("0") == true)
                            {
                                this.xhtmlReaderNamespaceProcessing = false;
                            }
                            else
                            {
                                this.xhtmlReaderNamespaceProcessing = true;
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlReaderCoalesceAdjacentCharacterData") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlReaderCoalesceAdjacentCharacterData = event.asCharacters().getData();

                            if (xhtmlReaderCoalesceAdjacentCharacterData.equalsIgnoreCase("true") == true ||
                                xhtmlReaderCoalesceAdjacentCharacterData.equalsIgnoreCase("1") == true)
                            {
                                this.xhtmlReaderCoalesceAdjacentCharacterData = true;
                            }
                            else
                            {
                                this.xhtmlReaderCoalesceAdjacentCharacterData = false;
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlReaderResolveExternalParsedEntities") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlReaderResolveExternalParsedEntities = event.asCharacters().getData();

                            if (xhtmlReaderResolveExternalParsedEntities.equalsIgnoreCase("false") == true ||
                                xhtmlReaderResolveExternalParsedEntities.equalsIgnoreCase("0") == true)
                            {
                                this.xhtmlReaderResolveExternalParsedEntities = false;
                            }
                            else
                            {
                                this.xhtmlReaderResolveExternalParsedEntities = true;
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("xhtmlReaderUseDTDNotDTDFallback") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            String xhtmlReaderUseDTDNotDTDFallback = event.asCharacters().getData();

                            if (xhtmlReaderUseDTDNotDTDFallback.equalsIgnoreCase("false") == true ||
                                xhtmlReaderUseDTDNotDTDFallback.equalsIgnoreCase("0") == true)
                            {
                                this.xhtmlReaderUseDTDNotDTDFallback = false;
                            }
                            else
                            {
                                this.xhtmlReaderUseDTDNotDTDFallback = true;
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }

        if (this.inFiles.size() <= 0)
        {
            System.out.print("html2epub1: No input files configured.\n");
            System.exit(-16);
        }

        if (this.outDirectory == null)
        {
            System.out.print("html2epub1: No output directory configured.\n");
            System.exit(-17);
        }
        
        if (this.metaData.containsKey("title") == false)
        {
            System.out.print("html2epub1: Title is missing in the metadata.\n");
            System.exit(-18);
        }
        
        /**
         * @todo Check if this.inFiles contains the same files (in terms of
         *     absolute paths) several times in the configuration.
         */

        return 0;
    }

    public ArrayList<File> GetInFiles()
    {
        return this.inFiles;
    }
    
    public ArrayList<String> GetInFileTitles()
    {
        return this.inFileTitles;
    }

    public File GetOutDirectory()
    {
        return this.outDirectory;
    }

    public Map<String, String> GetMetaData()
    {
        return this.metaData;
    }
    
    public boolean GetXHTMLSchemaValidation()
    {
        return this.xhtmlSchemaValidation;
    }
    
    public boolean GetXHTMLReaderDTDValidation()
    {
        return this.xhtmlReaderDTDValidation;
    }
    
    public boolean GetXHTMLReaderNamespaceProcessing()
    {
        return this.xhtmlReaderNamespaceProcessing;
    }
    
    public boolean GetXHTMLReaderCoalesceAdjacentCharacterData()
    {
        return this.xhtmlReaderCoalesceAdjacentCharacterData;
    }
    
    public boolean GetXHTMLReaderReplaceEntityReferences()
    {
        // Always true, XHTML input and output should always use UTF-8
        // character encodings.
        return true;
    }
    
    public boolean GetXHTMLReaderResolveExternalParsedEntities()
    {
        return this.xhtmlReaderResolveExternalParsedEntities;
    }
    
    public boolean GetXHTMLReaderUseDTDNotDTDFallback()
    {
        return this.xhtmlReaderUseDTDNotDTDFallback;
    }


    private File configFile;
    private ArrayList<File> inFiles;
    private ArrayList<String> inFileTitles;
    private File outDirectory;
    private Map<String, String> metaData;
    private boolean xhtmlSchemaValidation;
    private boolean xhtmlReaderDTDValidation;
    private boolean xhtmlReaderNamespaceProcessing;
    private boolean xhtmlReaderCoalesceAdjacentCharacterData;
    private boolean xhtmlReaderResolveExternalParsedEntities;
    private boolean xhtmlReaderUseDTDNotDTDFallback;
}
