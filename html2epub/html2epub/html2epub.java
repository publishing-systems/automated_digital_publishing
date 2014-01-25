/* Copyright (C) 2013-2014  Stephan Kreutzer
 *
 * This file is part of html2epub.
 *
 * html2epub is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/html2epub.java
 * @brief The main module that delegates to the specific processors.
 * @author Stephan Kreutzer
 * @since 2013-12-10
 */



import java.util.ArrayList;
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.validation.SchemaFactory;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.util.ListIterator;
import org.w3c.dom.Document;
import javax.xml.transform.dom.DOMSource;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;



public class html2epub
{
    public static void main(String args[])
    {
        System.out.print("html2epub  Copyright (C) 2013-2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/text_processing/\n\n");

        if (args.length != 1)
        {
            System.out.print("Usage:\n" +
                             "\thtml2epub config-file\n\n");

            System.exit(1);
        }


        System.out.print("html2epub: Reading configuration.\n");

        ConfigProcessor configuration = new ConfigProcessor(args[0]);
        configuration.run();

        ArrayList<File> xhtmlInFiles = configuration.GetInFiles();
        ArrayList<String> xhtmlInFileTitles = configuration.GetInFileTitles();

        if (configuration.GetXHTMLSchemaValidation() == true)
        {
            File schemaFile = new File("xhtml1-strict.xsd");

            if (schemaFile.exists() != true)
            {
                schemaFile = null;
            }

            if (schemaFile != null)
            {
                if (schemaFile.isFile() != true)
                {
                    schemaFile = null;
                }
            }

            if (schemaFile != null)
            {
                if (schemaFile.canRead() != true)
                {
                    schemaFile = null;
                }
            }

            if (schemaFile != null)
            {
                System.out.print("html2epub: Validating XHTML files.\n");
            
                try
                {
                    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                    documentFactory.setNamespaceAware(true);
                    DocumentBuilder builder = documentFactory.newDocumentBuilder();

                    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                    Source schemaSource = new StreamSource(schemaFile);
                    Schema schema = schemaFactory.newSchema(schemaSource);
                    Validator validator = schema.newValidator();
                    

                    ListIterator<File> inFileIter = xhtmlInFiles.listIterator();
                    
                    while (inFileIter.hasNext())
                    {
                        File inFile = inFileIter.next();

                        System.out.print("html2epub: Validating '" + inFile.getAbsolutePath() + "'.\n");

                        Document document = builder.parse(inFile);
                        validator.validate(new DOMSource(document));
                    }
                }
                catch (ParserConfigurationException ex)
                {
                    ex.printStackTrace();
                    System.exit(-1);
                }
                catch (SAXException ex)
                {
                    ex.printStackTrace();
                    System.exit(-2);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    System.exit(-3);
                }
            }
            else
            {
                System.out.print("html2epub: Can't validate XHTML files - schema 'xhtml1-strict.xsd' is missing.\n");
            }
        }

        File outDirectory = configuration.GetOutDirectory();
        boolean xhtmlReaderDTDValidation = configuration.GetXHTMLReaderDTDValidation();
        boolean xhtmlReaderNamespaceProcessing = configuration.GetXHTMLReaderNamespaceProcessing();
        boolean xhtmlReaderCoalesceAdjacentCharacterData = configuration.GetXHTMLReaderCoalesceAdjacentCharacterData();
        boolean xhtmlReaderReplaceEntityReferences = configuration.GetXHTMLReaderReplaceEntityReferences();
        boolean xhtmlReaderResolveExternalParsedEntities = configuration.GetXHTMLReaderResolveExternalParsedEntities();
        boolean xhtmlReaderUseDTDNotDTDFallback = configuration.GetXHTMLReaderUseDTDNotDTDFallback();

        if (xhtmlReaderDTDValidation == false)
        {
            /**
             * @todo Without this adjustment, the XML reader would take very much time in an
             *     environment where DTD validation is not supported by the parser. It
             *     should be checked if there are no other reasons for time consumption than
             *     this inconsistency of settings. Only occurs if the XML file contains a
             *     DOCTYPE declaration.
             */
            xhtmlReaderUseDTDNotDTDFallback = false;
        }

        System.out.print("html2epub: Setting up EPUB2.\n");
        
        EPUBSetup epubSetup = new EPUBSetup();
        epubSetup.run(outDirectory,
                      xhtmlInFiles,
                      xhtmlInFileTitles,
                      configuration.GetMetaData(),
                      xhtmlReaderDTDValidation,
                      xhtmlReaderNamespaceProcessing,
                      xhtmlReaderCoalesceAdjacentCharacterData,
                      xhtmlReaderReplaceEntityReferences,
                      xhtmlReaderResolveExternalParsedEntities,
                      xhtmlReaderUseDTDNotDTDFallback);

        XHTMLProcessor xhtmlProcessor = new XHTMLProcessor();
        ArrayList<File> xhtmlOutFiles = new ArrayList<File>();
        ArrayList<File> imageOutFiles = new ArrayList<File>();

        for (int currentXHTMLFile = 1; currentXHTMLFile <= xhtmlInFiles.size(); currentXHTMLFile++)
        {
            File inFile = xhtmlInFiles.get(currentXHTMLFile-1);
            File outFile = new File(outDirectory.getAbsolutePath() + System.getProperty("file.separator") + "page_" + currentXHTMLFile + ".xhtml");
            
            String title = xhtmlInFileTitles.get(currentXHTMLFile-1);

            System.out.print("html2epub: Processing '" + inFile.getAbsolutePath() + "'.\n");
            
            xhtmlProcessor.processFile(inFile,
                                       outFile,
                                       title,
                                       xhtmlInFiles,
                                       epubSetup.GetReferencedImageFiles(),
                                       epubSetup.GetReferencedCSSFiles(),
                                       xhtmlReaderDTDValidation,
                                       xhtmlReaderNamespaceProcessing,
                                       xhtmlReaderCoalesceAdjacentCharacterData,
                                       xhtmlReaderReplaceEntityReferences,
                                       xhtmlReaderResolveExternalParsedEntities,
                                       xhtmlReaderUseDTDNotDTDFallback);

            xhtmlOutFiles.add(outFile);
        }
        
        System.out.print("html2epub: Packing to EPUB2.\n");
        
        // Note that it is actually possible to have one single XHTML file
        // configured multiple times as input, so it can be re-used, while
        // referenced image files will always be present only once.
        ArrayList<File> packageFiles = new ArrayList<File>(xhtmlOutFiles);
        packageFiles.addAll(epubSetup.GetImageOutFiles());
        packageFiles.addAll(epubSetup.GetCSSOutFiles());
        
        ZipProcessor zipProcessor = new ZipProcessor();
        zipProcessor.Run(packageFiles, outDirectory);

        System.exit(0);
    }
}
