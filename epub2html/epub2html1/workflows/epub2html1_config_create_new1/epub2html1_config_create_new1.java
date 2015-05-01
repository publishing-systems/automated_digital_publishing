/* Copyright (C) 2014  Stephan Kreutzer
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
 * @file $/workflows/epub2html1_config_create_new1/epub2html1_config_create_new1.java
 * @brief Creates a new configuration file for epub2html1.
 * @author Stephan Kreutzer
 * @since 2014-10-03
 */



import java.io.File;
import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;



public class epub2html1_config_create_new1
{
    public static void main(String[] args)
    {
        System.out.print("epub2html1_config_create_new1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");
    

        File configFile = null;
    
        if (args.length >= 1)
        {
            configFile = new File(args[0]);
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Create new epub2html1 configuration file");
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            FileFilter fileFilter = new FileNameExtensionFilter("epub2html1 configuration file", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showSaveDialog(null);
            chooser.setVisible(false); 

            if (result == JFileChooser.APPROVE_OPTION)
            {
                configFile = chooser.getSelectedFile();
            }
            else
            {
                System.exit(2);
            }
        }
        
        if (configFile.getName().indexOf(".") == -1)
        {
            configFile = new File(configFile.getAbsolutePath() + ".xml");
        }
        
        if (configFile.exists() == true)
        {
            System.out.print("epub2html1_config_create_new1: '" + configFile.getAbsolutePath() + "' does already exist.\n");
            System.exit(1);
        }
        
        File parent = configFile.getAbsoluteFile().getParentFile();
        
        if (parent.exists() != true)
        {
            System.out.print("epub2html1_config_create_new1: Won't create parent directory '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
        
        if (parent.isDirectory() != true)
        {
            System.out.print("epub2html1_config_create_new1: Parent '" + parent.getAbsolutePath() + "' isn't a directory.\n");
            System.exit(-2);
        }
        
        if (parent.canWrite() != true)
        {
            System.out.print("epub2html1_config_create_new1: Can't create '" + configFile.getName() + "' in '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-3);
        }
        
        
        try
        {
            configFile.createNewFile();
            
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(configFile),
                                    "UTF8"));
            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by epub2html1_config_create_new1 of epub2html1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/). -->\n");
            writer.write("<epub2html1-config>\n");
            writer.write("  <in>\n");
            writer.write("    <inFile type=\"epub\">./input.epub</inFile>\n");
            writer.write("  </in>\n");
            writer.write("  <out>\n");
            writer.write("    <outDirectory>./output/</outDirectory>\n");
            writer.write("  </out>\n");
            writer.write("</epub2html1-config>\n");

            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        
        System.out.println("epub2html1_config_create_new1: '" + configFile.getAbsolutePath() + "' created.");

        System.exit(0);
    }
}
