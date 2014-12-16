/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2all2 workflow.
 *
 * odt2all2 workflow is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2all2 workflow is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2all2 workflow. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/odt2all2.java
 * @brief Workflow to automatically process one or more semantic ODT input
 *     file(s) based on template1 of odt2html to HTML, EPUB2 and PDF, guided
 *     by a GUI wizzard.
 * @author Stephan Kreutzer
 * @since 2014-12-09
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
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent; 
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;



public class odt2all2
{
    public static void main(String args[])
    {
        System.out.print("odt2all2 workflow  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");
    
        String programPath = odt2all2.class.getProtectionDomain().getCodeSource().getLocation().getFile();


        File configFile = null;

        {
            final JFileChooser chooser = new JFileChooser("Select/Create odt2all1 Configuration File");
            chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setApproveButtonText("Select/Create");

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            FileFilter fileFilter = new FileNameExtensionFilter("odt2all1 configuration file (*.xml)", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showDialog(null, null);
            chooser.setVisible(false); 

            if (result != JFileChooser.APPROVE_OPTION)
            {
                System.exit(2);
            }

            configFile = chooser.getSelectedFile();
        }
        
        if (configFile == null)
        {
            System.out.println("odt2all2 workflow: No input file.");
            System.exit(-1);
        }

        if (configFile.exists() != true)
        {
            try
            {
                configFile.createNewFile();
                
                BufferedWriter writer = new BufferedWriter(
                                        new OutputStreamWriter(
                                        new FileOutputStream(configFile),
                                        "UTF8"));
                
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<!-- This file was created by odt2all2 workflow, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
                writer.write("<odt2all1-config>\n");
                writer.write("</odt2all1-config>\n");

                writer.flush();
                writer.close();
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
                System.exit(-1);
            }
            
            System.out.println("odt2all2 workflow: '" + configFile.getAbsolutePath() + "' created.");
        }
        else
        {
            if (configFile.isFile() != true)
            {
                System.out.println("odt2all2 workflow: Path to odt2all1 configuration '" + configFile.getAbsolutePath() + "' doesn't reference a file.");
                System.exit(-1);
            }
        
            if (configFile.canRead() != true)
            {
                System.out.print("odt2all2 workflow: odt2all1 configuration file '" + configFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }


        ProcessBuilder builder = new ProcessBuilder("java", "odt2all1_config_edit2", configFile.getAbsolutePath());
        builder.directory(new File(programPath + "gui/odt2all1_config_edit2"));
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


        File html2epub1ConfigFile = null;

        {
            final JFileChooser chooser = new JFileChooser("Select/Create html2epub1 Configuration File");
            chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setApproveButtonText("Select/Create");

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            FileFilter fileFilter = new FileNameExtensionFilter("html2epub1 configuration file (*.xml)", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showDialog(null, null);
            chooser.setVisible(false); 

            if (result != JFileChooser.APPROVE_OPTION)
            {
                System.exit(2);
            }

            html2epub1ConfigFile = chooser.getSelectedFile();
        }
        
        if (html2epub1ConfigFile == null)
        {
            System.out.println("odt2all2 workflow: No html2epub1 configuration file.");
            System.exit(-1);
        }

        if (html2epub1ConfigFile.exists() != true)
        {
            builder = new ProcessBuilder("java", "html2epub1_config_create_new", html2epub1ConfigFile.getPath());
            builder.directory(new File(programPath + "../html2epub/html2epub1/gui/html2epub1_config_create_new"));
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
            
            if (html2epub1ConfigFile.exists() != true)
            {
                System.out.print("odt2all2 workflow: html2epub1 configuration file '" + html2epub1ConfigFile.getAbsolutePath() + "' doesn't exist, but should by now.\n");
                System.exit(-1);
            }
            
            if (html2epub1ConfigFile.isFile() != true)
            {
                System.out.println("odt2all2 workflow: html2epub1 configuration path '" + html2epub1ConfigFile.getAbsolutePath() + "' doesn't reference a file.");
                System.exit(-1);
            }
        
            if (html2epub1ConfigFile.canRead() != true)
            {
                System.out.print("odt2all2 workflow: html2epub1 configuration file '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }
        else
        {
            if (html2epub1ConfigFile.isFile() != true)
            {
                System.out.println("odt2all2 workflow: html2epub1 configuration path '" + html2epub1ConfigFile.getAbsolutePath() + "' doesn't reference a file.");
                System.exit(-1);
            }
        
            if (html2epub1ConfigFile.canRead() != true)
            {
                System.out.print("odt2all2 workflow: html2epub1 configuration file '" + html2epub1ConfigFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-1);
            }
        }


        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(configFile);
            document.setXmlStandalone(true);
            document.getDocumentElement().normalize();


            NodeList rootNodeList = document.getElementsByTagName("odt2all1-config");

            if (rootNodeList.getLength() <= 0)
            {
                System.out.println("odt2all2 workflow: '" + configFile.getAbsolutePath() + "' isn't a valid odt2all1 configuration file.");
                System.exit(-1);
            }

            NodeList rootSubNodeList = rootNodeList.item(0).getChildNodes();

            for (int i = rootSubNodeList.getLength() - 1; i >= 0; i--)
            {
                Node subNode = rootSubNodeList.item(i);

                if (subNode.getNodeName().equalsIgnoreCase("html2epub1-config-file") == true)
                {
                    rootNodeList.item(0).removeChild(subNode);
                }
            }
            
            Element html2epub1ConfigNode = document.createElement("html2epub1-config-file");
            html2epub1ConfigNode.setAttribute("path", html2epub1ConfigFile.getPath());
            
            rootNodeList.item(0).appendChild(html2epub1ConfigNode);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult = new StreamResult(configFile);
            transformer.transform(source, streamResult);
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
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }


        builder = new ProcessBuilder("java", "html2epub1_config_metadata_editor", html2epub1ConfigFile.getPath());
        builder.directory(new File(programPath + "../html2epub/html2epub1/gui/html2epub1_config_metadata_editor"));
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


        File outDirectory = null;

        {
            final JFileChooser chooser = new JFileChooser("Select/Create html2epub1 Configuration File");
            chooser.setDialogType(JFileChooser.CUSTOM_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setApproveButtonText("Select/Create");

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            chooser.setVisible(true);
            final int result = chooser.showDialog(null, null);
            chooser.setVisible(false); 

            if (result != JFileChooser.APPROVE_OPTION)
            {
                System.exit(2);
            }

            outDirectory = chooser.getSelectedFile();
        }
        
        if (outDirectory == null)
        {
            System.out.println("odt2all2 workflow: No out directory.");
            System.exit(-1);
        }
        
        if (outDirectory.exists() != true)
        {
            if (outDirectory.mkdirs() != true)
            {
                System.out.println("odt2all2 workflow: Can't create out directory '" + outDirectory.getAbsolutePath() + "'.");
                System.exit(-1);
            }
        }
        
        if (outDirectory.isDirectory() != true)
        {
            System.out.println("odt2all2 workflow: Output path '" + outDirectory.getAbsolutePath() + "' isn't a directory.");
            System.exit(-1);
        }
        
        if (outDirectory.canWrite() != true)
        {
            System.out.println("odt2all2 workflow: Output directory '" + outDirectory.getAbsolutePath() + "' isn't writable.");
            System.exit(-1);
        }

        
        builder = new ProcessBuilder("java", "odt2all1", configFile.getAbsolutePath(), outDirectory.getAbsolutePath());
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

        return;
    }
}
