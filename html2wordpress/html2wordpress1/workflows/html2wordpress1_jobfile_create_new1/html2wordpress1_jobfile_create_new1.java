/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of html2wordpress1.
 *
 * html2wordpress1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2wordpress1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2wordpress1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/html2wordpress1_jobfile_create_new1/html2wordpress1_jobfile_create_new1.java
 * @brief Creates a new job configuration file for html2wordpress1.
 * @author Stephan Kreutzer
 * @since 2014-10-01
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
import java.util.TimeZone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;



public class html2wordpress1_jobfile_create_new1
{
    public static void main(String[] args)
    {
        System.out.print("html2wordpress1_jobfile_create_new1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");
    

        File jobFile = null;
    
        if (args.length >= 1)
        {
            jobFile = new File(args[0]);
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Create new html2wordpress1 job configuration file");
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

            FileFilter fileFilter = new FileNameExtensionFilter("html2wordpress1 job configuration file", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showSaveDialog(null);
            chooser.setVisible(false); 

            if (result == JFileChooser.APPROVE_OPTION)
            {
                jobFile = chooser.getSelectedFile();
            }
            else
            {
                System.exit(2);
            }
        }
        
        if (jobFile.getName().indexOf(".") == -1)
        {
            jobFile = new File(jobFile.getAbsolutePath() + ".xml");
        }
        
        if (jobFile.exists() == true)
        {
            System.out.print("html2wordpress1_jobfile_create_new1: '" + jobFile.getAbsolutePath() + "' does already exist.\n");
            System.exit(1);
        }
        
        File parent = jobFile.getAbsoluteFile().getParentFile();
        
        if (parent.exists() != true)
        {
            System.out.print("html2wordpress1_jobfile_create_new1: Won't create parent directory '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-1);
        }
        
        if (parent.isDirectory() != true)
        {
            System.out.print("html2wordpress1_jobfile_create_new1: Parent '" + parent.getAbsolutePath() + "' isn't a directory.\n");
            System.exit(-2);
        }
        
        if (parent.canWrite() != true)
        {
            System.out.print("html2wordpress1_jobfile_create_new1: Can't create '" + jobFile.getName() + "' in '" + parent.getAbsolutePath() + "'.\n");
            System.exit(-3);
        }
        
        
        try
        {
            jobFile.createNewFile();
            
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(jobFile),
                                    "UTF8"));
            
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was created by html2wordpress1_jobfile_create_new1 of html2wordpress1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/). -->\n");
            writer.write("<html2wordpress1-job>\n");
            writer.write("  <input-html-file>input.html</input-html-file>\n");
            writer.write("  <wordpress-xmlrpc-url>http://www.example.org/wordpress/xmlrpc.php</wordpress-xmlrpc-url>\n");
            writer.write("  <!-- User authentication requires the fixed version of the \"Secure XML-RPC\" WordPress plugin by Eric Mann from https://github.com/publishing-systems/secure-xmlrpc (original located here: https://wordpress.org/plugins/secure-xml-rpc/). -->\n");
            writer.write("  <wordpress-user-public-key>b730db0864b0d4453ba6a26ad6613cd4</wordpress-user-public-key>\n");
            writer.write("  <wordpress-user-private-key>7647a19f5bf3e9fd001419900ad48a54</wordpress-user-private-key>\n");
            writer.write("  <wordpress-post-user-id>1</wordpress-post-user-id>\n");
            writer.write("  <wordpress-post-type>post</wordpress-post-type>\n");
            writer.write("  <wordpress-post-status>publish</wordpress-post-status>\n");
            writer.write("  <wordpress-post-title>Title</wordpress-post-title>\n");
            writer.write("  <wordpress-post-excerpt>This is the post excerpt.</wordpress-post-excerpt>\n");
            
            String now = "20140816T10:14:58Z";

            {
                TimeZone timeZone = TimeZone.getTimeZone("UTC");
                DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss'Z'");
                dateFormat.setTimeZone(timeZone);
                now = dateFormat.format(new Date());
            }
            
            writer.write("  <wordpress-post-date>" + now + "</wordpress-post-date>\n");
            writer.write("  <wordpress-post-format>Standard</wordpress-post-format>\n");
            writer.write("  <wordpress-post-name-slug>htmlpost</wordpress-post-name-slug>\n");
            writer.write("  <wordpress-post-comment-default-status>closed</wordpress-post-comment-default-status>\n");
            writer.write("  <wordpress-post-ping-default-status>closed</wordpress-post-ping-default-status>\n");
            writer.write("  <wordpress-post-sticky>0</wordpress-post-sticky>\n");
            writer.write("  <wordpress-post-thumbnail-id>0</wordpress-post-thumbnail-id>\n");
            writer.write("  <wordpress-post-parent-id>0</wordpress-post-parent-id>\n");
            writer.write("  <wordpress-post-custom-fields>\n");
            writer.write("    <wordpress-post-custom-field name=\"metafield\">metadata</wordpress-post-custom-field>\n");
            writer.write("  </wordpress-post-custom-fields>\n");
            writer.write("  <wordpress-post-taxonomies>\n");
            writer.write("    <wordpress-post-taxonomy-hierarchy name=\"category\">\n");
            writer.write("      <wordpress-post-taxonomy-id>1</wordpress-post-taxonomy-id>\n");
            writer.write("      <wordpress-post-taxonomy-id>3</wordpress-post-taxonomy-id>\n");
            writer.write("    </wordpress-post-taxonomy-hierarchy>\n");
            writer.write("    <wordpress-post-taxonomy-tags name=\"post_tag\">\n");
            writer.write("      <wordpress-post-taxonomy-name>tag</wordpress-post-taxonomy-name>\n");
            writer.write("      <wordpress-post-taxonomy-name>tag2</wordpress-post-taxonomy-name>\n");
            writer.write("    </wordpress-post-taxonomy-tags>\n");
            writer.write("  </wordpress-post-taxonomies>\n");
            writer.write("</html2wordpress1-job>\n");

            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        
        System.out.println("html2wordpress1_jobfile_create_new1: '" + jobFile.getAbsolutePath() + "' created.");

        System.exit(0);
    }
}
