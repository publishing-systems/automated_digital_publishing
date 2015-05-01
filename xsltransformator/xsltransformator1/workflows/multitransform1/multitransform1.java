/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of xsltransformator1.
 *
 * xsltransformator1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * xsltransformator1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with xsltransformator1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/multitransform1/multitransform1.java
 * @brief Transforms a list of input files.
 * @author Stephan Kreutzer
 * @since 2014-10-04
 */



import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.util.Scanner;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;



public class multitransform1
{
    public static void main(String args[])
    {
        System.out.print("multitransform1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");

        String programPath = multitransform1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\tmultitransform1 input-list stylesheet\n\n");

            System.exit(1);
        }

        File inputListFile = new File(args[0]);
        
        if (inputListFile.exists() != true)
        {
            System.out.print("multitransform1: '" + inputListFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inputListFile.isFile() != true)
        {
            System.out.print("multitransform1: '" + inputListFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (inputListFile.canRead() != true)
        {
            System.out.print("multitransform1: '" + inputListFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }
        
        File stylesheetFile = new File(args[1]);
        
        if (stylesheetFile.exists() != true)
        {
            System.out.print("multitransform1: '" + stylesheetFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (stylesheetFile.isFile() != true)
        {
            System.out.print("multitransform1: '" + stylesheetFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (stylesheetFile.canRead() != true)
        {
            System.out.print("multitransform1: '" + stylesheetFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(inputListFile);
            document.getDocumentElement().normalize();
              
            NodeList inputFilesNodeList = document.getElementsByTagName("input-file");
            int inputFilesNodeListLength = inputFilesNodeList.getLength();

            if (inputFilesNodeListLength > 0)
            {
                for (int i = 0; i < inputFilesNodeListLength; i++)
                {
                    Node inputFileNode = inputFilesNodeList.item(i);

                    NamedNodeMap inputFileNodeAttributes = inputFileNode.getAttributes();
                    
                    if (inputFileNodeAttributes == null)
                    {
                        System.out.print("multitransform1: Entry in '" + inputListFile.getAbsolutePath() + "' has no attributes.\n");
                        System.exit(-1);
                    }

                    Node sourceAttribute = inputFileNodeAttributes.getNamedItem("source");
                    
                    if (sourceAttribute == null)
                    {
                        System.out.print("multitransform1: Entry in '" + inputListFile.getAbsolutePath() + "' is missing the 'source' attribute.\n");
                        System.exit(-1);
                    }
                    
                    Node destinationAttribute = inputFileNodeAttributes.getNamedItem("destination");
                    
                    if (sourceAttribute == null)
                    {
                        System.out.print("multitransform1: Entry in '" + inputListFile.getAbsolutePath() + "' is missing the 'destination' attribute.\n");
                        System.exit(-1);
                    }

                    File sourceFile = new File(sourceAttribute.getTextContent());
                    
                    if (sourceFile.isAbsolute() != true)
                    {
                        sourceFile = new File(inputListFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + sourceAttribute.getTextContent());
                    }
                       
                    if (sourceFile.exists() != true)
                    {
                        System.out.print("multitransform1: Source file '" + sourceFile.getAbsolutePath() + "' doesn't exist.\n");
                        System.exit(-1);
                    }

                    if (sourceFile.isFile() != true)
                    {
                        System.out.print("multitransform1: Source path '" + sourceFile.getAbsolutePath() + "' isn't a file.\n");
                        System.exit(-1);
                    }

                    if (sourceFile.canRead() != true)
                    {
                        System.out.print("multitransform1: Source file '" + sourceFile.getAbsolutePath() + "' isn't readable.\n");
                        System.exit(-1);
                    }

                    File destinationFile = new File(destinationAttribute.getTextContent());

                    if (destinationFile.isAbsolute() != true)
                    {
                        destinationFile = new File(inputListFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + destinationAttribute.getTextContent());
                    }


                    ProcessBuilder builder = new ProcessBuilder("java", "xsltransformator1", sourceFile.getAbsolutePath(), stylesheetFile.getAbsolutePath(), destinationFile.getAbsolutePath());
                    builder.directory(new File(programPath + "../../"));

                    try
                    {
                        Process process = builder.start();
                        Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                        
                        while (scanner.hasNext() == true)
                        {
                            System.out.println(scanner.next());
                        }
                        
                        scanner.close();
                    }
                    catch (IOException ex)
                    {
                        ex.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
            else
            {
                System.out.print("multitransform1: No input files.\n");
                System.exit(-1);
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
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return;
    }
}
