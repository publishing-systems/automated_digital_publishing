/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of odt2all1 workflow.
 *
 * odt2all1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2all1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2all1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2all1.java
 * @brief Workflow to automatically process one or more semantic ODT input
 *     file(s) based on template1 of odt2html to HTML, EPUB2 and PDF.
 * @author Stephan Kreutzer
 * @since 2014-12-07
 */



import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



public class odt2all1
{
    public static void main(String args[])
    {
        System.out.print("odt2all1 workflow  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");
    
        String programPath = odt2all1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        if (args.length < 2)
        {
            System.out.print("Usage:\n" +
                             "\todt2all1 config-file out-directory\n\n");
            System.exit(1);
        }


        File configFile = new File(args[0]);
        
        if (configFile.exists() != true)
        {
            System.out.print("odt2all1 workflow: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (configFile.isFile() != true)
        {
            System.out.print("odt2all1 workflow: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (configFile.canRead() != true)
        {
            System.out.print("odt2all1 workflow: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }
        
        File outputDirectory = new File(args[1]);

        if (outputDirectory.exists() == true)
        {
            if (outputDirectory.isDirectory() == true)
            {
                if (outputDirectory.canWrite() != true)
                {
                    System.out.println("odt2all1: Can't write to directory '" + outputDirectory.getAbsolutePath() + "'.");
                    System.exit(-1);
                }
            }
            else
            {
                System.out.println("odt2all1: Out path '" + outputDirectory.getAbsolutePath() + "' isn't a directory.");
                System.exit(-1);
            }
        }
        else
        {
            if (outputDirectory.mkdirs() != true)
            {
                System.out.println("odt2all1: Can't create out directory '" + outputDirectory.getAbsolutePath() + "'.");
                System.exit(-1);
            }
        }


        List<File> inputFiles = new ArrayList<File>();
        File html2epub1ConfigFile = null;
        File html2pdf1ReplacementDictionaryFile = null;

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(configFile);
            document.getDocumentElement().normalize();


            {
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
                            System.out.print("odt2all1 workflow: Input file entry #" + (i + 1) + " in '" + configFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = inputFileNodeAttributes.getNamedItem("path");

                        if (pathAttribute == null)
                        {
                            System.out.print("odt2all1 workflow: Input file entry #" + (i + 1) + " in '" + configFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        File inputFile = new File(pathAttribute.getTextContent());
                        
                        if (inputFile.isAbsolute() != true)
                        {
                            inputFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());
                        }
                           
                        if (inputFile.exists() != true)
                        {
                            System.out.print("odt2all1 workflow: Input file '" + inputFile.getAbsolutePath() + "' doesn't exist.\n");
                            System.exit(-1);
                        }

                        if (inputFile.isFile() != true)
                        {
                            System.out.print("odt2all1 workflow: Input path '" + inputFile.getAbsolutePath() + "' isn't a file.\n");
                            System.exit(-1);
                        }

                        if (inputFile.canRead() != true)
                        {
                            System.out.print("odt2all1 workflow: Input file '" + inputFile.getAbsolutePath() + "' isn't readable.\n");
                            System.exit(-1);
                        }
                        
                        inputFiles.add(inputFile);
                    }
                }
                else
                {
                    System.out.print("odt2all1 workflow: No input files configured.\n");
                    System.exit(-1);
                }
            }

            {
                NodeList html2epub1ConfigFileNodeList = document.getElementsByTagName("html2epub1-config-file");
                int html2epub1ConfigFileNodeListLength = html2epub1ConfigFileNodeList.getLength();

                if (html2epub1ConfigFileNodeListLength == 1)
                {
                    for (int i = 0; i < html2epub1ConfigFileNodeListLength; i++)
                    {
                        Node html2epub1ConfigFileNode = html2epub1ConfigFileNodeList.item(i);

                        NamedNodeMap html2epub1ConfigFileNodeAttributes = html2epub1ConfigFileNode.getAttributes();
                        
                        if (html2epub1ConfigFileNodeAttributes == null)
                        {
                            System.out.print("odt2all1 workflow: html2epub1 config file entry in '" + configFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = html2epub1ConfigFileNodeAttributes.getNamedItem("path");

                        if (pathAttribute == null)
                        {
                            System.out.print("odt2all1 workflow: html2epub1 config file entry in '" + configFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        html2epub1ConfigFile = new File(pathAttribute.getTextContent());

                        if (html2epub1ConfigFile.isAbsolute() != true)
                        {
                            html2epub1ConfigFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());
                        }
                    }
                }
                else if (html2epub1ConfigFileNodeListLength < 1)
                {
                    System.out.print("odt2all1 workflow: No html2epub1 config file configured in '" + configFile.getAbsolutePath() + "'.\n");
                    System.exit(-1);
                }
                else
                {
                    System.out.print("odt2all1 workflow: More than one html2epub1 config file configured in '" + configFile.getAbsolutePath() + "'.\n");
                    System.exit(-1);
                }
            }

            {
                NodeList html2pdf1ReplacementDictionaryNodeList = document.getElementsByTagName("html2pdf1-workflow-txtreplace1-replacement-dictionary");
                int html2pdf1ReplacementDictionaryNodeListLength = html2pdf1ReplacementDictionaryNodeList.getLength();

                if (html2pdf1ReplacementDictionaryNodeListLength == 1)
                {
                    for (int i = 0; i < html2pdf1ReplacementDictionaryNodeListLength; i++)
                    {
                        Node html2pdf1ReplacementDictionaryNode = html2pdf1ReplacementDictionaryNodeList.item(i);

                        NamedNodeMap html2pdf1ReplacementDictionaryNodeAttributes = html2pdf1ReplacementDictionaryNode.getAttributes();
                        
                        if (html2pdf1ReplacementDictionaryNodeAttributes == null)
                        {
                            System.out.print("odt2all1 workflow: html2pdf1 workflow txtreplace1 replacement dictionary file entry in '" + configFile.getAbsolutePath() + "' has no attributes.\n");
                            System.exit(-1);
                        }

                        Node pathAttribute = html2pdf1ReplacementDictionaryNodeAttributes.getNamedItem("path");

                        if (pathAttribute == null)
                        {
                            System.out.print("odt2all1 workflow: html2pdf1 workflow txtreplace1 replacement dictionary file entry in '" + configFile.getAbsolutePath() + "' is missing the 'path' attribute.\n");
                            System.exit(-1);
                        }

                        html2pdf1ReplacementDictionaryFile = new File(pathAttribute.getTextContent());

                        if (html2pdf1ReplacementDictionaryFile.isAbsolute() != true)
                        {
                            html2pdf1ReplacementDictionaryFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + pathAttribute.getTextContent());
                        }
                    }
                }
                else if (html2pdf1ReplacementDictionaryNodeListLength > 1)
                {
                    System.out.print("odt2all1 workflow: More than one html2pdf1 workflow txtreplace1 replacement dictionary file configured in '" + configFile.getAbsolutePath() + "'.\n");
                    System.exit(-1);
                }
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

        if (html2epub1ConfigFile == null)
        {
            System.out.println("odt2all1 workflow: html2epub1 config file isn't set.");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.exists() != true)
        {
            System.out.print("odt2all1 workflow: Input file '" + html2epub1ConfigFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.isFile() != true)
        {
            System.out.print("odt2all1 workflow: Input path '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.canRead() != true)
        {
            System.out.print("odt2all1 workflow: Input file '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        if (html2pdf1ReplacementDictionaryFile != null)
        {
            if (html2pdf1ReplacementDictionaryFile.exists() != true)
            {
                System.out.print("odt2all1 workflow: html2pdf1 workflow txtreplace1 replacement dictionary file '" + html2pdf1ReplacementDictionaryFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (html2pdf1ReplacementDictionaryFile.isFile() != true)
            {
                System.out.print("odt2all1 workflow: html2pdf1 workflow txtreplace1 replacement dictionary path '" + html2pdf1ReplacementDictionaryFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (html2pdf1ReplacementDictionaryFile.canRead() != true)
            {
                System.out.print("odt2all1 workflow: html2pdf1 workflow txtreplace1 replacement dictionary file '" + html2pdf1ReplacementDictionaryFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }

        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("odt2all1 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2all1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "odt2epub3_workflow_config.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by odt2all1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<odt2epub3-config>\n");
            
            for (int i = 0; i < inputFiles.size(); i++)
            {
                writer.write("  <input-file path=\"" + inputFiles.get(i).getAbsolutePath() + "\"/>\n");
            }

            writer.write("  <html2epub1-config-file path=\"" + html2epub1ConfigFile.getAbsolutePath() + "\"/>\n");
            writer.write("</odt2epub3-config>\n");
            
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        ProcessBuilder builder = new ProcessBuilder("java", "odt2epub3", tempDirectory.getAbsolutePath() + File.separator + "odt2epub3_workflow_config.xml", outputDirectory.getAbsolutePath() + File.separator + "out.epub");
        builder.directory(new File(programPath));
        builder.redirectErrorStream(true);

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

        {
            File from = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "html_concatenated.html");
            File to = new File(outputDirectory.getAbsolutePath() + File.separator + "out.html");

            if (odt2all1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "odt2pdf2_workflow_config.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by odt2all1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<odt2pdf2-config>\n");
            
            for (int i = 0; i < inputFiles.size(); i++)
            {
                writer.write("  <inFile path=\"" + inputFiles.get(i).getAbsolutePath() + "\"/>\n");
            }

            if (html2pdf1ReplacementDictionaryFile != null)
            {
                writer.write("  <html2pdf1-workflow-txtreplace1-replacement-dictionary path=\"" + html2pdf1ReplacementDictionaryFile.getAbsolutePath() + "\"/>\n");
            }

            writer.write("</odt2pdf2-config>\n");
            
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        builder = new ProcessBuilder("java", "odt2pdf2", tempDirectory.getAbsolutePath() + File.separator + "odt2pdf2_workflow_config.xml", outputDirectory.getAbsolutePath() + File.separator + "out.pdf");
        builder.directory(new File(programPath));
        builder.redirectErrorStream(true);

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

        {
            File from = new File(tempDirectory.getAbsolutePath() + File.separator + "pdf" + File.separator + "output.tex");
            File to = new File(outputDirectory.getAbsolutePath() + File.separator + "out.tex");

            if (odt2all1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        return;
    }
    
    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2all1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2all1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2all1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
            return -3;
        }
    
    
        char[] buffer = new char[1024];

        try
        {
            to.createNewFile();
        
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(from),
                                    "UTF8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(to),
                                    "UTF8"));
            int charactersRead = reader.read(buffer, 0, buffer.length);

            while (charactersRead > 0)
            {
                writer.write(buffer, 0, charactersRead);
                charactersRead = reader.read(buffer, 0, buffer.length);
            }
            
            writer.close();
            reader.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
    
        return 0;
    }
}
