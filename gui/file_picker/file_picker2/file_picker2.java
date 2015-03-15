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
 * @file $/gui/file_picker/file_picker2/file_picker2.java
 * @brief Universal GUI directory picker.
 * @author Stephan Kreutzer
 * @since 2015-02-25
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



public class file_picker2
{
    public static void main(String[] args)
    {
        System.out.print("file_picker2  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "and the project website http://www.publishing-systems.org.\n\n");


        String programPath = file_picker2.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        File startDirectory = null;
        
        if (args.length >= 1)
        {
            startDirectory = new File(args[0]);

            if (startDirectory.isDirectory() != true)
            {
                if (startDirectory.exists() != true)
                {
                    System.out.print("file_picker2: Start directory '" + startDirectory.getAbsolutePath() + "' doesn't exist.\n");
                    System.exit(-1);
                }

                if (startDirectory.isDirectory() != true)
                {
                    System.out.print("file_picker2: Start path '" + startDirectory.getAbsolutePath() + "' isn't a directory.\n");
                    System.exit(-2);
                }
            }
        }


        final JFileChooser chooser = new JFileChooser("Select Directory");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

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

        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        File selectedDirectory = null;

        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedDirectory = chooser.getSelectedFile();
        }
        else
        {
            System.exit(2);
        }

        System.out.println("file_picker2: '" + selectedDirectory.getAbsolutePath() + "' selected.");

        System.exit(0);
    }
}
