/* Copyright (C) 2013-2014  Stephan Kreutzer
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
 * @file $/ConfigProcessor.java
 * @brief Processor to read the configuration file.
 * @author Stephan Kreutzer
 * @since 2014-07-08
 */



import java.io.File;
import java.util.ArrayList;
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
        this.inFile = null;
        this.outDirectory = null;
    }

    public int run()
    {
        if (this.configFile.exists() != true)
        {
            System.out.print("epub2html1: '" + this.configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (this.configFile.isFile() != true)
        {
            System.out.print("epub2html1: '" + this.configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (this.configFile.canRead() != true)
        {
            System.out.print("epub2html1: '" + this.configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext() == true)
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("inFile") == true)
                    {
                        if (this.inFile != null)
                        {
                            System.out.println("epub2html1: More than one input file specified in configuration file '" + this.configFile.getAbsolutePath() + "'.");
                            return -1;
                        }
                    
                        Attribute type = event.asStartElement().getAttributeByName(new QName("type"));
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
                                System.out.print("epub2html1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
                                System.exit(-1);
                            }

                            if (inFile.isFile() != true)
                            {
                                System.out.print("epub2html1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
                                System.exit(-1);
                            }

                            if (inFile.canRead() != true)
                            {
                                System.out.print("epub2html1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
                                System.exit(-1);
                            }
                            
                            if (type == null)
                            {
                                System.out.print("epub2html1: Missing type for '" + inFile.getAbsolutePath() + "' in configuration.\n");
                                System.exit(-1);
                            }

                            this.inFile = inFile;
                            this.inFileType = type.getValue();
                        }
                    }
                    else if (tagName.equalsIgnoreCase("outDirectory") == true)
                    {
                        event = eventReader.nextEvent();

                        if (event.isCharacters() == true)
                        {
                            if (this.outDirectory != null)
                            {
                                System.out.print("epub2html1: More than one output directory specified.\n");
                                continue;
                            }

                            this.outDirectory = new File(event.asCharacters().getData());
                            
                            if (this.outDirectory.isAbsolute() != true)
                            {
                                this.outDirectory = new File(this.configFile.getAbsoluteFile().getParent() +
                                                             System.getProperty("file.separator") +
                                                             event.asCharacters().getData());
                            }

                            if (this.outDirectory.exists() == true)
                            {
                                if (this.outDirectory.isDirectory() == true)
                                {
                                    if (this.outDirectory.canWrite() != true)
                                    {
                                        System.out.println("epub2html1: Can't write to directory '" + this.outDirectory.getAbsolutePath() + "'.");
                                        System.exit(-1);
                                    }
                                }
                                else
                                {
                                    System.out.println("epub2html1: Out path '" + this.outDirectory.getAbsolutePath() + "' isn't a directory.");
                                    System.exit(-1);
                                }
                            }
                            else
                            {
                                if (this.outDirectory.mkdir() != true)
                                {
                                    System.out.println("epub2html1: Can't create out directory '" + this.outDirectory.getAbsolutePath() + "'.");
                                    System.exit(-1);
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
            System.exit(-1);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return 0;
    }

    public File GetInFile()
    {
        return this.inFile;
    }
    
    public String GetInFileType()
    {
        return this.inFileType;
    }

    public File GetOutDirectory()
    {
        return this.outDirectory;
    }

    private File configFile;
    private File inFile;
    private String inFileType;
    private File outDirectory;
}
