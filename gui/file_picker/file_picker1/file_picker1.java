/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of automated_digital_publishing.
 *
 * automated_digital_publishing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * automated_digital_publishing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with automated_digital_publishing. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/gui/file_picker/file_picker1/file_picker1.java
 * @brief Universal GUI file picker.
 * @author Stephan Kreutzer
 * @since 2014-05-01
 */



import java.io.File;
import java.util.Map;
import java.util.LinkedHashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;



public class file_picker1
{
    public static void main(String[] args)
    {
        System.out.print("file_picker1  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");


        String programPath = file_picker1.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        File configFile = null;

        if (args.length >= 1)
        {
            configFile = new File(args[0]);
        }
        else
        {
            configFile = new File(programPath + "config.xml");
        }

        if (configFile.exists() != true)
        {
            System.out.print("file_picker1: Configuration file '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (configFile.isFile() != true)
        {
            System.out.print("file_picker1: Configuration path '" + configFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (configFile.canRead() != true)
        {
            System.out.print("file_picker1: Configuration file '" + configFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }
        
        File startDirectory = null;
        
        if (args.length >= 2)
        {
            startDirectory = new File(args[1]);

            if (startDirectory.isDirectory() != true)
            {
                if (startDirectory.exists() != true)
                {
                    System.out.print("file_picker1: Start directory '" + startDirectory.getAbsolutePath() + "' doesn't exist.\n");
                    System.exit(-1);
                }

                if (configFile.isDirectory() != true)
                {
                    System.out.print("file_picker1: Start path '" + startDirectory.getAbsolutePath() + "' isn't a directory.\n");
                    System.exit(-2);
                }
            }
        }


        Map<String, String> fileExtensions = new LinkedHashMap<String, String>();

        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;


            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();
                
                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String elementNameString = elementName.getLocalPart();

                    if (elementNameString.equalsIgnoreCase("extension") == true)
                    {
                        Attribute attributeExtension = event.asStartElement().getAttributeByName(new QName("extension"));
                                
                        if (attributeExtension == null)
                        {
                            System.out.println("file_picker1: Extension definition is missing the 'extension' attribute in '" + configFile.getAbsolutePath() + "'.");                            
                            continue;
                        }
                        
                        String extension = attributeExtension.getValue();
                        String extensionDisplayName = new String();
                        
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();
                            
                            if (event.isCharacters() == true)
                            {
                                extensionDisplayName += event.asCharacters();
                            }
                            else if (event.isEndElement() == true)
                            {
                                QName elementEndName = event.asEndElement().getName();
                                String elementEndNameString = elementEndName.getLocalPart();
                                
                                if (elementEndNameString.equalsIgnoreCase("extension") == true)
                                {
                                    break;
                                }
                            }
                        }
                        
                        if (fileExtensions.containsKey(extension) != true)
                        {
                            fileExtensions.put(extension, extensionDisplayName);
                        }
                        else
                        {
                            System.out.println("file_picker1: Extension '" + extension + "' specified more than once.");                            
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-5);
        }


        final JFileChooser chooser = new JFileChooser("Select File");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (startDirectory != null)
        {
            chooser.setCurrentDirectory(startDirectory);
        }

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    final File resultFile = (File) e.getNewValue();
                }
            }
        });


        boolean first = true;

        for (Map.Entry<String, String> entry : fileExtensions.entrySet())
        {
            FileFilter fileFilter = new FileNameExtensionFilter(entry.getValue(), entry.getKey());
            chooser.addChoosableFileFilter(fileFilter);
            
            if (first == true)
            {
                chooser.setFileFilter(fileFilter);
                first = false;
            }
        }


        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        File selectedFile = null;

        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = chooser.getSelectedFile();
        }
        else
        {
            System.exit(2);
        }


        System.out.println("file_picker1: '" + selectedFile.getAbsolutePath() + "' selected.");

        System.exit(0);
    }
}
