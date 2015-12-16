/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of odt2html2 workflow.
 *
 * odt2html2 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2html2 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2html2 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2html2.java
 * @brief Workflow to automatically process semantic ODT input file(s) to
 *     structural HTML according to a processing job description file.
 * @author Stephan Kreutzer
 * @since 2014-06-09
 */



import java.io.File;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.net.URLDecoder;



public class odt2html2
{
    public static void main(String args[])
    {
        System.out.print("odt2html2 workflow  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        String programPath = odt2html2.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
            programPath = URLDecoder.decode(programPath, "UTF-8");
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

        File jobDescriptionFile = null;

        if (args.length > 0)
        {
            jobDescriptionFile = new File(args[0]);

            if (jobDescriptionFile.exists() != true)
            {
                System.out.print("odt2html2 workflow: '" + jobDescriptionFile.getAbsolutePath() + "' doesn't exist.\n");
                jobDescriptionFile = null;
            }

            if (jobDescriptionFile != null)
            {
                if (jobDescriptionFile.isFile() != true)
                {
                    System.out.print("odt2html2 workflow: '" + jobDescriptionFile.getAbsolutePath() + "' isn't a file.\n");
                    jobDescriptionFile = null;
                }
            }

            if (jobDescriptionFile != null)
            {
                if (jobDescriptionFile.canRead() != true)
                {
                    System.out.print("odt2html2 workflow: '" + jobDescriptionFile.getAbsolutePath() + "' isn't readable.\n");
                    jobDescriptionFile = null;
                }
            }
        }
        else
        {
            try
            {
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(new File(programPath + "../gui/file_picker/file_picker1/config.xml")),
                                        "UTF8"));

                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was created by odt2html2 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
                writer.write("<file-picker1-config>\n");
                writer.write("  <extension extension=\"xml\">Processing job description file (.xml)</extension>\n");
                writer.write("</file-picker1-config>\n");
                
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
                System.exit(-2);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-3);
            }


            ProcessBuilder builder = new ProcessBuilder("java", "file_picker1");
            builder.directory(new File(programPath + "../gui/file_picker/file_picker1"));
            builder.redirectErrorStream(true);

            try
            {
                Process process = builder.start();
                Scanner scanner = new Scanner(process.getInputStream()).useDelimiter("\n");
                
                while (scanner.hasNext() == true)
                {
                    String line = scanner.next();
                    
                    System.out.println(line);
                    
                    if (line.contains("' selected.") == true)
                    {
                        StringTokenizer tokenizer = new StringTokenizer(line, "'");
                        
                        if (tokenizer.countTokens() >= 2)
                        {
                            tokenizer.nextToken();
                            jobDescriptionFile = new File(tokenizer.nextToken());
                        }
                    }
                }
                
                scanner.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-4);
            }
        }

        if (jobDescriptionFile == null)
        {
            System.out.println("odt2html2 workflow: No input ODT file.");
            System.exit(-5);
        }


        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("odt2html2 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-6);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("odt2html2 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-7);
            }
        }


        List<File> inputODTFiles = new ArrayList<File>();
        File outputHTMLFile = null;

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(jobDescriptionFile);
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
                        System.out.print("odt2html2 workflow: Misconfigured ODT input file entry in '" + jobDescriptionFile.getAbsolutePath() + "'.\n");
                        System.exit(-8);
                    }
                       
                    Node inputODTFilePathNode = attributes.getNamedItem("path");
                       
                    if (inputODTFilePathNode == null)
                    {
                        System.out.print("odt2html2 workflow: Misconfigured ODT input file entry in '" + jobDescriptionFile.getAbsolutePath() + "'.\n");
                        System.exit(-9);
                    }
                       
                    File inputODTFile = new File(inputODTFilePathNode.getTextContent());
                       
                    if (inputODTFile.isAbsolute() != true)
                    {
                        inputODTFile = new File(jobDescriptionFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + inputODTFilePathNode.getTextContent());
                    }
                       
                    if (inputODTFile.exists() != true)
                    {
                        System.out.print("odt2html2 workflow: '" + inputODTFile.getAbsolutePath() + "' doesn't exist.\n");
                        System.exit(-10);
                    }

                    if (inputODTFile.isFile() != true)
                    {
                        System.out.print("odt2html2 workflow: '" + inputODTFile.getAbsolutePath() + "' isn't a file.\n");
                        System.exit(-11);
                    }

                    if (inputODTFile.canRead() != true)
                    {
                        System.out.print("odt2html2 workflow: '" + inputODTFile.getAbsolutePath() + "' isn't readable.\n");
                        System.exit(-12);
                    }

                    inputODTFiles.add(inputODTFile);
                }
            }
            else
            {
                System.out.print("odt2html2 workflow: No input ODT files.\n");
                System.exit(-13);
            }
            
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


        for (int i = 0; i < inputODTFiles.size(); i++)
        {
            ProcessBuilder builder = new ProcessBuilder("java", "odt2html1", inputODTFiles.get(i).getAbsolutePath(), tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_1_" + (i + 1), "output_1_" + (i + 1) + ".html");
            builder.directory(new File(programPath + "../odt2html/odt2html1"));
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
                System.exit(-19);
            }
        
            File resultFile = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_1_" + (i + 1) + File.separator + "output_1_" + (i + 1) + ".html");
            
            if (resultFile.exists() != true)
            {
                System.out.print("odt2html2 workflow: '" + resultFile.getAbsolutePath() + "' doesn't exist, but should be generated.\n");
                System.exit(-20);
            }

            if (resultFile.isFile() != true)
            {
                System.out.print("odt2html2 workflow: '" + resultFile.getAbsolutePath() + "' isn't a file, but should be.\n");
                System.exit(-1);
            }  
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + File.separator + "html_concatenate1_jobfile.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by odt2html2 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<html-concatenate1-jobfile>\n");

            for (int i = 0; i < inputODTFiles.size(); i++)
            {
                File inputFile = inputODTFiles.get(i);

                if (i == 0)
                {
                    writer.write("  <head-file path=\"./output_1/output_1_" + (i + 1) + "/output_1_" + (i + 1) + ".html\"/>\n");
                }

                writer.write("  <input-file path=\"./output_1/output_1_" + (i + 1) + "/output_1_" + (i + 1) + ".html\"/>\n");
            }

            writer.write("  <output-file path=\"./output_1/output_1.html\"/>\n");
            writer.write("</html-concatenate1-jobfile>\n");

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
        
        ProcessBuilder builder = new ProcessBuilder("java", "html_concatenate1", tempDirectory.getAbsolutePath() + File.separator + "html_concatenate1_jobfile.xml");
        builder.directory(new File(programPath + "../html_concatenate/html_concatenate1"));
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
            File from = new File(programPath + "../xsltransformator/xsltransformator1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../xsltransformator/xsltransformator1/entities/config.xml");
            
            if (odt2html2.CopyFile(from, to) != 0)
            {
                System.exit(-23);
            }
        }

        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_1.html", programPath + "../odt2html/templates/template1/prepare4hierarchical.xsl", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_2.html");
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
            System.exit(-24);
        }


        {
            File from = new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/config_xhtml1-strict.xml");
            File to = new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/config.xml");
            
            if (odt2html2.CopyFile(from, to) != 0)
            {
                System.exit(-25);
            }
        }


        builder = new ProcessBuilder("java", "html_flat2hierarchical1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_2.html", programPath + "../odt2html/templates/template1/html_flat2hierarchical1_config.xml", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_3.html");
        builder.directory(new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1"));
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
            System.exit(-26);
        }


        builder = new ProcessBuilder("java", "xsltransformator1", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_3.html", programPath + "../odt2html/templates/template1/html_clean.xsl", tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html");
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
            System.exit(-27);
        }
        
        
        if (outputHTMLFile != null)
        {
            File from = new File(tempDirectory.getAbsolutePath() + File.separator + "output_1" + File.separator + "output_4.html");
            
            if (odt2html2.CopyFile(from, outputHTMLFile) != 0)
            {
                System.exit(-28);
            }
        }

        return;
    }
    
    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("odt2html2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("odt2html2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("odt2html2 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
            System.exit(-29);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-30);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-31);
        }
    
        return 0;
    }
}
