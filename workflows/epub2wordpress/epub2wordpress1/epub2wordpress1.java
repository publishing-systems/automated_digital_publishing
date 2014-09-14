/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of epub2wordpress1 workflow.
 *
 * epub2wordpress1 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epub2wordpress1 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epub2wordpress1 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/epub2wordpress/epub2wordpress1.java
 * @brief Workflow to submit an EPUB2 file to a WordPress installation.
 * @author Stephan Kreutzer
 * @since 2014-08-30
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
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.util.ArrayList;



public class epub2wordpress1
{
    public static void main(String args[])
    {
        System.out.print("epub2wordpress1 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");

        String programPath = epub2wordpress1.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        if (args.length != 2)
        {
            System.out.print("Usage:\n" +
                             "\tepub2wordpress1 input-epub-file input-upload-jobfile\n\n");

            System.exit(1);
        }

        File inputEPUBFile = new File(args[0]);

        if (inputEPUBFile.exists() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputEPUBFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inputEPUBFile.isFile() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputEPUBFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (inputEPUBFile.canRead() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputEPUBFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }
        
        File inputUploadJobfile = new File(args[1]);
        
        if (inputUploadJobfile.exists() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputUploadJobfile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (inputUploadJobfile.isFile() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputUploadJobfile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (inputUploadJobfile.canRead() != true)
        {
            System.out.print("epub2wordpress1 workflow: '" + inputUploadJobfile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }

        File tempDirectory = new File(programPath + "temp");
        
        if (tempDirectory.exists() != true)
        {
            if (tempDirectory.mkdir() != true)
            {
                System.out.print("epub2wordpress1 workflow: Can't create temp directory '" + tempDirectory.getAbsolutePath() + "'.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (tempDirectory.isDirectory() != true)
            {
                System.out.print("epub2wordpress1 workflow: Temp directory path '" + tempDirectory.getAbsolutePath() + "' exists, but isn't a directory.\n");
                System.exit(-1);
            }
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(tempDirectory.getAbsolutePath() + "/config.xml")),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by epub2wordpress1 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/). -->\n");
            writer.write("<epub2html1-config>\n");
            writer.write("  <in>\n");
            writer.write("    <inFile type=\"epub\">" + inputEPUBFile.getAbsolutePath() + "</inFile>\n");
            writer.write("  </in>\n");
            writer.write("  <out>\n");
            writer.write("    <outDirectory>" + tempDirectory.getAbsolutePath() + "</outDirectory>\n");
            writer.write("  </out>\n");
            writer.write("</epub2html1-config>\n");
            
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


        {
            File from = new File(programPath + "../../../epub2html/epub2html1/entities/config_xhtml1_1.xml");
            File to = new File(programPath + "../../../epub2html/epub2html1/entities/config.xml");
            
            if (epub2wordpress1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        ProcessBuilder builder = new ProcessBuilder("java", "epub2html1", tempDirectory.getAbsolutePath() + "/config.xml");
        builder.directory(new File(programPath + "../../../epub2html/epub2html1"));

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

        File xhtmlListFile = new File(tempDirectory.getAbsolutePath() + "/index.xml");
        
        if (xhtmlListFile.exists() != true)
        {
            System.out.print("epub2wordpress1 workflow: Extracted XHTML files list XML file '" + xhtmlListFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (xhtmlListFile.isFile() != true)
        {
            System.out.print("epub2wordpress1 workflow: Extracted XHTML files list XML path '" + xhtmlListFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (xhtmlListFile.canRead() != true)
        {
            System.out.print("epub2wordpress1 workflow: Extracted XHTML files list XML file '" + xhtmlListFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        ArrayList<File> xhtmlList = new ArrayList<File>();

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	           DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	           Document document = documentBuilder.parse(xhtmlListFile);
	           document.getDocumentElement().normalize();
              
	           NodeList xhtmlFilesNodeList = document.getElementsByTagName("file");
	           int xhtmlFilesNodeListLength = xhtmlFilesNodeList.getLength();
	           
	           if (xhtmlFilesNodeListLength > 0)
	           {
	               for (int i = 0; i < xhtmlFilesNodeListLength; i++)
	               {
	                   Node xhtmlFileNode = xhtmlFilesNodeList.item(i);
	                   
	                   File xhtmlFile = new File(xhtmlFileNode.getTextContent());
	                   
	                   if (xhtmlFile.isAbsolute() != true)
	                   {
	                       xhtmlFile = new File(xhtmlListFile.getAbsoluteFile().getParent() + System.getProperty("file.separator") + xhtmlFileNode.getTextContent());
	                   }
	                   
                    if (xhtmlFile.exists() != true)
                    {
                        System.out.print("epub2wordpress1 workflow: Referenced XHTML file '" + xhtmlFile.getAbsolutePath() + "' doesn't exist.\n");
                        System.exit(-1);
                    }

                    if (xhtmlFile.isFile() != true)
                    {
                        System.out.print("epub2wordpress1 workflow: Referenced XHTML path '" + xhtmlFile.getAbsolutePath() + "' isn't a file.\n");
                        System.exit(-1);
                    }

                    if (xhtmlFile.canRead() != true)
                    {
                        System.out.print("epub2wordpress1 workflow: Referenced XHTML file '" + xhtmlFile.getAbsolutePath() + "' isn't readable.\n");
                        System.exit(-1);
                    }

                    xhtmlList.add(xhtmlFile);
                }
            }
            else
            {
                System.out.print("epub2wordpress1 workflow: No extracted XHTML files.\n");
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


        if (xhtmlList.isEmpty())
        {
            System.out.print("epub2wordpress1 workflow: No XHTML files.\n");
            System.exit(0);
        }

        {
            File from = new File(programPath + "../../../xsltransformator/xsltransformator1/entities/config_xhtml1_1.xml");
            File to = new File(programPath + "../../../xsltransformator/xsltransformator1/entities/config.xml");
            
            if (epub2wordpress1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        for (int i = 1; i <= xhtmlList.size(); i++)
        {
            System.out.println("epub2wordpress1 workflow: Transforming '" + xhtmlList.get(i - 1).getAbsolutePath() + "'...");
        
            builder = new ProcessBuilder("java", "xsltransformator1", xhtmlList.get(i - 1).getAbsolutePath(), programPath + "html2wordpress1.xsl", tempDirectory.getAbsolutePath() + File.separator + xhtmlList.get(i - 1).getName() + "_transformed.html");
            builder.directory(new File(programPath + "../../../xsltransformator/xsltransformator1"));

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


        {
            File from = new File(programPath + "../../../html2wordpress/html2wordpress1/entities/config_xhtml1_1.xml");
            File to = new File(programPath + "../../../html2wordpress/html2wordpress1/entities/config.xml");
            
            if (epub2wordpress1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../../../html2wordpress/html2wordpress1/schemata/config_xhtml1_1.xml");
            File to = new File(programPath + "../../../html2wordpress/html2wordpress1/schemata/config.xml");
            
            if (epub2wordpress1.CopyFile(from, to) != 0)
            {
                System.exit(-1);
            }
        }

        for (int i = 1; i <= xhtmlList.size(); i++)
        {
            File xhtmlTransformed = new File(tempDirectory.getAbsolutePath() + File.separator + xhtmlList.get(i - 1).getName() + "_transformed.html");
            
            if (xhtmlTransformed.exists() != true)
            {
                System.out.print("epub2wordpress1 workflow: Transformed XHTML file '" + xhtmlTransformed.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (xhtmlTransformed.isFile() != true)
            {
                System.out.print("epub2wordpress1 workflow: Transformed XHTML path '" + xhtmlTransformed.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-1);
            }

            if (xhtmlTransformed.canRead() != true)
            {
                System.out.print("epub2wordpress1 workflow: Transformed XHTML file '" + xhtmlTransformed.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }

            System.out.println("epub2wordpress1 workflow: Submitting '" + xhtmlTransformed.getAbsolutePath() + "' to WordPress...");

            {
                File to = new File(programPath + "../../../html2wordpress/html2wordpress1/input.html");
                
                if (epub2wordpress1.CopyFile(xhtmlTransformed, to) != 0)
                {
                    System.exit(-1);
                }
            }

            builder = new ProcessBuilder("java", "html2wordpress1", inputUploadJobfile.getAbsolutePath());
            builder.directory(new File(programPath + "../../../html2wordpress/html2wordpress1"));

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

        return;
    }

    public static int CopyFile (File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("epub2wordpress1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("epub2wordpress1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("epub2wordpress1 workflow: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
