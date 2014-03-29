/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html2epub1.
 *
 * html2epub1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/gui/html2epub1_config_create_new/html2epub1_config_create_new.java
 * @brief Creates a new configuration file for html2epub1.
 * @author Stephan Kreutzer
 * @since 2014-03-12
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



public class html2epub1_config_create_new
{
    public static void main(String[] args)
    {
        System.out.print("html2epub1_config_create_new  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    

        File configFile = null;
    
        if (args.length >= 1)
        {
            configFile = new File(args[0]);
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Create new html2epub1 configuration file");
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

            FileFilter fileFilter = new FileNameExtensionFilter("html2epub1 configuration file", "xml");
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
            System.out.print("html2epub1_config_create_new: '" + configFile.getAbsolutePath() + "' does already exist.\n");
            System.exit(1);
        }
        
        File parent = configFile.getParentFile();
        
        if (parent.exists() != true)
        {
            System.out.print("html2epub1_config_create_new: Won't create parent directory '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
        
        if (parent.isDirectory() != true)
        {
            System.out.print("html2epub1_config_create_new: Parent '" + parent.getAbsolutePath() + "' isn't a directory.\n");
            System.exit(-2);
        }
        
        if (parent.canWrite() != true)
        {
            System.out.print("html2epub1_config_create_new: Can't create '" + configFile.getName() + "' in '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-3);
        }
        
        
        try
        {
            configFile.createNewFile();
            
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(configFile.getAbsolutePath()),
                                    "UTF8"));
            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by html2epub1_config_create_new of html2epub1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
            writer.write("<html2epub1-config>\n");
            writer.write("  <in>\n");
            writer.write("    <xhtmlSchemaValidation>true</xhtmlSchemaValidation>\n");
            writer.write("    <!-- javax.xml.stream.isValidating: Turns on/off implementation specific DTD validation. -->\n");
            writer.write("    <xhtmlReaderDTDValidation>true</xhtmlReaderDTDValidation>\n");
            writer.write("    <!-- javax.xml.stream.isNamespaceAware: Turns on/off namespace processing for XML 1.0 support. -->\n");
            writer.write("    <xhtmlReaderNamespaceProcessing>true</xhtmlReaderNamespaceProcessing>\n");
            writer.write("    <!-- javax.xml.stream.isCoalescing: Requires the processor to coalesce adjacent character data. -->\n");
            writer.write("    <xhtmlReaderCoalesceAdjacentCharacterData>true</xhtmlReaderCoalesceAdjacentCharacterData>\n");
            writer.write("    <!-- javax.xml.stream.isReplacingEntityReferences: replace internal entity references with their replacement text and report them as characters. -->\n");
            writer.write("    <!-- Always true! -->\n");
            writer.write("    <!-- javax.xml.stream.isSupportingExternalEntities: Resolve external parsed entities. -->\n");
            writer.write("    <xhtmlReaderResolveExternalParsedEntities>true</xhtmlReaderResolveExternalParsedEntities>\n");
            writer.write("    <!-- javax.xml.stream.supportDTD: Use this property to request processors that do not support DTDs. -->\n");
            writer.write("    <xhtmlReaderUseDTDNotDTDFallback>false</xhtmlReaderUseDTDNotDTDFallback>\n");
            writer.write("  </in>\n");
            writer.write("  <out>\n");
            writer.write("    <outDirectory></outDirectory>\n");
            writer.write("    <metaData>\n");
            writer.write("      <title></title>\n");
            writer.write("      <creator></creator>\n");
            writer.write("      <subject></subject>\n");
            writer.write("      <description></description>\n");
            writer.write("      <publisher></publisher>\n");
            writer.write("      <contributor></contributor>\n");
            writer.write("      <identifier></identifier>\n");
            writer.write("      <source></source>\n");
            writer.write("      <language></language>\n");
            writer.write("      <coverage></coverage>\n");
            writer.write("      <rights></rights>\n");
            writer.write("    </metaData>\n");
            writer.write("  </out>\n");
            writer.write("</html2epub1-config>\n");

            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        
        System.out.println("html2epub1_config_create_new: '" + configFile.getAbsolutePath() + "' created.");

        System.exit(0);
    }
}
