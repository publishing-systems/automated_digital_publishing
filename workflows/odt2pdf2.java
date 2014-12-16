/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2pdf2 workflow.
 *
 * odt2pdf2 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2pdf2 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2pdf2 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2pdf2.java
 * @brief Workflow to automatically process a semantic ODT input file based on
 *     template1 of odt2html to a PDF.
 * @author Stephan Kreutzer
 * @since 2014-12-07
 */



import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;



public class odt2pdf2
{
    public static void main(String args[])
    {
        System.out.print("odt2pdf2 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");
    
        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\todt2pdf2 config-file output-pdf-file\n\n");
 
            System.exit(1);
        }

        String programPath = odt2pdf2.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        File configFile = new File(args[0]);
        
        if (configFile.exists() != true)
        {
            System.out.print("odt2pdf2 workflow: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (configFile.isFile() != true)
        {
            System.out.print("odt2pdf workflow: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (configFile.canRead() != true)
        {
            System.out.print("odt2html2 workflow: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        List<File> inputODTFiles = new ArrayList<File>();

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(configFile);
            document.getDocumentElement().normalize();
              
            NodeList inputODTFilesNodeList = document.getElementsByTagName("inFile");
            int inputODTFilesNodeListLength = inputODTFilesNodeList.getLength();
               
            if (inputODTFilesNodeListLength > 0)
            {
                for (int i = 0; i < inputODTFilesNodeListLength; i++)
                {
                    Node inputODTFileNode = inputODTFilesNodeList.item(i);
                    NamedNodeMap attributes = inputODTFileNode.getAttributes();
                       
                    if (attributes == null)
                    {
                        System.out.print("odt2pdf2 workflow: Misconfigured ODT input file entry in '" + configFile.getAbsolutePath() + "'.\n");
                        System.exit(-1);
                    }
                       
                    Node inputODTFilePathNode = attributes.getNamedItem("path");
                       
                    if (inputODTFilePathNode == null)
                    {
                        System.out.print("odt2pdf2 workflow: Misconfigured ODT input file entry in '" + configFile.getAbsolutePath() + "'.\n");
                        System.exit(-1);
                    }
                       
                    File inputODTFile = new File(inputODTFilePathNode.getTextContent());
                       
                    if (inputODTFile.isAbsolute() != true)
                    {
                        inputODTFile = new File(configFile.getAbsoluteFile().getParent() + File.separator + inputODTFilePathNode.getTextContent());
                    }
                       
                    if (inputODTFile.exists() != true)
                    {
                        System.out.print("odt2pdf2 workflow: '" + inputODTFile.getAbsolutePath() + "' doesn't exist.\n");
                        System.exit(-10);
                    }

                    if (inputODTFile.isFile() != true)
                    {
                        System.out.print("odt2pdf2 workflow: '" + inputODTFile.getAbsolutePath() + "' isn't a file.\n");
                        System.exit(-11);
                    }

                    if (inputODTFile.canRead() != true)
                    {
                        System.out.print("odt2pdf2 workflow: '" + inputODTFile.getAbsolutePath() + "' isn't readable.\n");
                        System.exit(-12);
                    }

                    inputODTFiles.add(inputODTFile);
                }
            }
            else
            {
                System.out.print("odtpdf2 workflow: No input ODT files.\n");
                System.exit(-13);
            }
            /*
            NodeList outputHTMLFileNodeList = document.getElementsByTagName("outFile");
            
            if (outputHTMLFileNodeList.getLength() > 0)
            {
                Node outputHTMLFileNode = outputHTMLFileNodeList.item(0);
                NamedNodeMap attributes = outputHTMLFileNode.getAttributes();

                if (attributes == null)
                {
                    System.out.print("odt2html2 workflow: Misconfigured ODT input file entry in '" + jobDescriptionFile.getAbsolutePath() + "'.\n");
                    System.exit(-14);
                }
                   
                Node outputHTMLFilePathNode = attributes.getNamedItem("path");
                
                if (outputHTMLFilePathNode == null)
                {
                    System.out.print("odt2html2 workflow: Misconfigured ODT input file entry in '" + jobDescriptionFile.getAbsolutePath() + "'.\n");
                    System.exit(-15);
                }
                   
                outputHTMLFile = new File(outputHTMLFilePathNode.getTextContent());
                
                if (outputHTMLFile.isAbsolute() != true)
                {
                    outputHTMLFile = new File(jobDescriptionFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + outputHTMLFilePathNode.getTextContent());
                }
            }
            {
                Kein else?
            }
            */
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-17);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-18);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("odt2pdf2 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-6);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2pdf2 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + "/odt2html2_config.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by odt2pdf2 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<odt2html2-config>\n");

            for (int i = 0; i < inputODTFiles.size(); i++)
            {
                writer.write("  <inFile path=\"" + inputODTFiles.get(i).getAbsolutePath() + "\"/>\n");
            }

            writer.write("  <outFile path=\"" + tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "out.html\"/>\n");
            writer.write("</odt2html2-config>\n");

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


        ProcessBuilder builder = new ProcessBuilder("java", "odt2html2", tempDirectory.getAbsolutePath() + "/odt2html2_config.xml");
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


        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html", programPath + "../odt2html/templates/template1/prepare4html2latex1_layout1.xsl", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_5.html");
        builder.directory(new File(programPath + "../xsltransformator/xsltransformator1"));
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
            System.exit(-15);
        }


        builder = new ProcessBuilder("java", "html2pdf1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_5.html");
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
            System.exit(-7);
        }

        {
            File from = new File(tempDirectory.getAbsolutePath() + File.separator + "pdf" + File.separator + "output.pdf");
            File to = new File(args[1]);

            if (odt2pdf2.CopyFileBinary(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        return;
    }

    public static int CopyFileBinary(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2pdf2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2pdf2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2pdf2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
            return -3;
        }
    
    
        byte[] buffer = new byte[1024];

        try
        {
            to.createNewFile();

            FileInputStream reader = new FileInputStream(from);
            FileOutputStream writer = new FileOutputStream(to);
            
            int bytesRead = reader.read(buffer, 0, buffer.length);
            
            while (bytesRead > 0)
            {
                writer.write(buffer, 0, bytesRead);
                bytesRead = reader.read(buffer, 0, buffer.length);
            }
            
            writer.close();
            reader.close();
        }
        catch (FileNotFoundException ex)
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
