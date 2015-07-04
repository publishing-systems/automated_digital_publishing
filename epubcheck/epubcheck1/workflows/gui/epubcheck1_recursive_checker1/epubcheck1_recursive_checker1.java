/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of epubcheck1_recursive_checker1 GUI.
 *
 * epubcheck1_recursive_checker1 GUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * epubcheck1_recursive_checker1 GUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with epubcheck1_recursive_checker1 GUI. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/epubcheck/epubcheck1/workflows/gui/epubcheck1_recursive_checker1/epubcheck1_recursive_checker1.java
 * @brief GUI for an epubcheck1_recursive_checker1 workflow job file.
 * @todo I10n needs to specify window and window area sizes which are specific for the
 *     current locale.
 * @author Stephan Kreutzer
 * @since 2015-06-24
 */



import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent; 
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.text.MessageFormat;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;



public class epubcheck1_recursive_checker1
  extends JFrame
  implements ActionListener
{
    public static void main(String[] args)
    {
        System.out.print("epubcheck1_recursive_checker1 GUI Copyright (C) 2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        /**
         * @todo Load a jobfile specified by command line argument.
         */

        epubcheck1_recursive_checker1 frame = new epubcheck1_recursive_checker1();
        frame.setLocation(100, 100);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }

    public epubcheck1_recursive_checker1()
    {
        super("epubcheck1_recursive_checker1 GUI");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event)
            {
                event.getWindow().setVisible(false);
                event.getWindow().dispose();
                System.exit(2);
            }
        });
        
        this.workingDirectory = new File(System.getProperty("user.home"));

        this.tabbedPane = new JTabbedPane();
    
        {
            JPanel panelInput = new JPanel();
    
            String columnCaptions[] = { getI10nString("tableInputColumnPathCaption"),
                                        getI10nString("tableInputColumnRecursiveCaption") };
            this.inputTableModel = new InputTableModel(columnCaptions);
            this.inputTable = new JTable(this.inputTableModel);
            JScrollPane scrollPane = new JScrollPane(this.inputTable);
          
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 1.0;

            panelInput.setLayout(new BorderLayout(5, 5));
            panelInput.add(scrollPane, BorderLayout.CENTER);


            JPanel panelButtons = new JPanel();
            
            this.buttonLoad = new JButton(getI10nString("buttonLoad"));
            this.buttonLoad.addActionListener(this);
            panelButtons.add(this.buttonLoad);
            
            this.buttonSave = new JButton(getI10nString("buttonSave"));
            this.buttonSave.addActionListener(this);
            panelButtons.add(this.buttonSave);
            
            this.buttonAdd = new JButton(getI10nString("buttonAdd"));
            this.buttonAdd.addActionListener(this);
            panelButtons.add(this.buttonAdd);
            
            this.buttonRemove = new JButton(getI10nString("buttonRemove"));
            this.buttonRemove.addActionListener(this);
            panelButtons.add(this.buttonRemove);
            
            panelInput.add(panelButtons, BorderLayout.PAGE_END);

            tabbedPane.addTab(getI10nString("tabInputCaption"), panelInput);
        }

        {
            JPanel panelResults = new JPanel();

            String columnCaptions[] = { getI10nString("tableResultsColumnFileCaption"),
                                        getI10nString("tableResultsColumnResultCaption") };
            this.resultsTableModel = new ResultsTableModel(columnCaptions);
            this.resultsTable = new JTable(this.resultsTableModel);
          
            this.resultsTable.setFillsViewportHeight(true);
            this.resultsTable.setPreferredScrollableViewportSize(new Dimension(50, 100));

            JScrollPane scrollPaneResultEntries = new JScrollPane(this.resultsTable);
          
            panelResults.setLayout(new BorderLayout(5, 5));
            panelResults.add(scrollPaneResultEntries, BorderLayout.PAGE_START);


            JPanel panelDescription = new JPanel();
            GridBagLayout gridbag = new GridBagLayout();
            panelDescription.setLayout(gridbag);
      
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.anchor = GridBagConstraints.NORTH;
            gridbagConstraints.weightx = 1.0;
            gridbagConstraints.weighty = 1.0;
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.gridheight = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.BOTH;
             
            this.textAreaResult = new JTextArea();
            this.textAreaResult.setEditable(false);
            this.textAreaResult.setLineWrap(true);
            this.textAreaResult.setWrapStyleWord(true);

            panelDescription.setPreferredSize(new Dimension(200, 150));

       
            JScrollPane scrollPaneResultLog = new JScrollPane(this.textAreaResult);
            panelDescription.add(scrollPaneResultLog, gridbagConstraints);
            
            panelResults.add(panelDescription, BorderLayout.CENTER);


            JPanel panelButtons = new JPanel();
            
            this.buttonCheck = new JButton(getI10nString("buttonCheck"));
            this.buttonCheck.addActionListener(this);
            panelButtons.add(this.buttonCheck);
            
            panelResults.add(panelButtons, BorderLayout.PAGE_END);
              
            tabbedPane.addTab(getI10nString("tabResultsCaption"), panelResults);
        }

        {
            JPanel panelTabInfo = new JPanel();
            panelTabInfo.setLayout(new BorderLayout(5, 5));
          
            JPanel panelInfo = new JPanel();

            GridBagLayout gridbag = new GridBagLayout();
            panelInfo.setLayout(gridbag);
      
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.anchor = GridBagConstraints.NORTH;
            gridbagConstraints.weightx = 1.0;
            gridbagConstraints.weighty = 1.0;
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.gridheight = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.BOTH;

            panelInfo.add(Box.createRigidArea(new Dimension(0, 15)));

            JLabel info = new JLabel("<html><head><title>" + getI10nString("infoHTMLTitle") + "</title></head><body>" +
                                       getI10nString("infoHTMLText") +
                                       "</body></html>");
                                     
            info.setFont(info.getFont().deriveFont(info.getFont().getStyle() & ~Font.BOLD));
            info.setAlignmentX(0.5f);
            info.setBorder(BorderFactory.createEtchedBorder());
            
            // Without JScrollPane, info would word-wrap.
            JScrollPane scrollPane2 = new JScrollPane(info);
            
            
            panelInfo.add(scrollPane2, gridbagConstraints);

            panelInfo.add(Box.createRigidArea(new Dimension(0, 10)));

            /**
             * @todo This originally was a settings tab, and maybe needs to be
             *     revived as such in the future to provide a button that calls
             *     a GUI for editing the epubcheck1_recursive_checker1 workflow
             *     config file (for setting the path to the epubcheck jar file
             *     and command line arguments).
             */  
            /*
          
            JPanel panelSettings = new JPanel();
            
            GridBagLayout gridbagLayout = new GridBagLayout();
            panelSettings.setLayout(gridbagLayout);

            JLabel label = new JLabel(getI10nString("tabSettingsEpubcheckJarCaption"),
                                      SwingConstants.LEFT);

            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.gridwidth = 1;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 0.0;
            panelSettings.add(label, gridbagConstraints);
        
            JTextField textField = new JTextField();

            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 1.0;
            panelSettings.add(textField, gridbagConstraints);
            
            panelSettings.setBorder(BorderFactory.createEtchedBorder()); 
            panelTabSettings.add(panelSettings, BorderLayout.PAGE_START);
            */
             

            panelTabInfo.add(panelInfo, BorderLayout.PAGE_START);

            JPanel panelButtons = new JPanel();
            
            this.buttonExit = new JButton(getI10nString("buttonExit"));
            this.buttonExit.addActionListener(this);
            panelButtons.add(this.buttonExit);
            
            this.buttonAbout = new JButton(getI10nString("buttonAbout"));
            this.buttonAbout.addActionListener(this);
            panelButtons.add(this.buttonAbout);
            
            /*
            this.buttonApply = new JButton(getI10nString("buttonApply"));
            this.buttonApply.addActionListener(this);
            panelButtons.add(this.buttonApply);
            */
            
            panelTabInfo.add(panelButtons, BorderLayout.PAGE_END);

          
            tabbedPane.addTab(getI10nString("tabInfoCaption"), panelTabInfo);
        }

        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public void actionPerformed(ActionEvent event)
    {
        Object source = event.getSource();
        
        if (source == this.buttonLoad)
        {
            loadJobfile();
        }
        else if (source == this.buttonSave)
        {
            saveJobfile(null);
        }
        else if (source == this.buttonAdd)
        {
            addInput();
        }
        else if (source == this.buttonRemove)
        {
            removeInput();
        }
        else if (source == this.buttonCheck)
        {
            check();
        }
        else if (source == this.buttonAbout)
        {
            AboutDialog aboutDialog = new AboutDialog(this);
            aboutDialog.setVisible(true);
        }
        else if (source == this.buttonExit)
        {
            System.exit(0);
        }
    }
    
    public void loadJobfile()
    {
        final JFileChooser chooser = new JFileChooser(getI10nString("fileChooserLoadJobfileCaption"));
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if (this.workingDirectory != null)
        {
            if (this.workingDirectory.exists() == true)
            {
                if (this.workingDirectory.isDirectory() == true)
                {
                    chooser.setCurrentDirectory(this.workingDirectory);
                }   
            }
        }

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    final File resultFile = (File) e.getNewValue();
                }
            }
        });

        FileFilter fileFilter = new FileNameExtensionFilter(getI10nString("fileChooserLoadJobfileFiletype"), "xml");
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);

        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        if (result != JFileChooser.APPROVE_OPTION)
        {
            return;
        }
        
        File jobFile = chooser.getSelectedFile();

        if (jobFile.exists() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageLoadJobfileFileDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        if (jobFile.isFile() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageLoadJobfileIsntAFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        if (jobFile.canRead() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageLoadJobfileCantReadFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        this.workingDirectory = jobFile.getParentFile();

        this.inputTableModel.setRowCount(0);
        this.inputTable.revalidate();

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(jobFile);
            document.getDocumentElement().normalize();

            NodeList inputNodeList = document.getElementsByTagName("input");

            if (inputNodeList.getLength() <= 0)
            {
                return;
            }

            for (int i = 0; i < inputNodeList.getLength(); i++)
            {
                Node inputNode = inputNodeList.item(i);
                NamedNodeMap attributes = inputNode.getAttributes();

                if (attributes == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageLoadJobfileInputEntryMissingAttributes"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
                    
                    this.inputTableModel.addRow(new Object[]{"", ""});
                    continue;
                }
                
                Node attributePath = attributes.getNamedItem("path");
                
                if (attributePath == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  new String("path"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageLoadJobfileInputEntryMissingPathAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.inputTableModel.addRow(new Object[]{"", ""});
                    continue;
                }

                File inputFile = new File(attributePath.getTextContent());

                if (inputFile.isAbsolute() != true)
                {
                    String relativePath = jobFile.getAbsoluteFile().getParent();

                    if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                    {
                        // Remove dot that references the local, current directory.
                        relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                    }
                    
                    if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                    {
                        relativePath += File.separator;
                    }
                    
                    relativePath += attributePath.getTextContent();
                    inputFile = new File(relativePath);
                }

                if (inputFile.exists() != true)
                {
                    Object[] messageArguments = { inputFile.getAbsolutePath(),
                                                  new String("input"),
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageLoadJobfileInputEntryReferencedFileDoesntExist"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.inputTableModel.addRow(new Object[]{"", ""});
                    continue;
                }
                else
                {
                    // Keep relative paths, even if they were temporarily made absolute for
                    // the checking above.
                    inputFile = new File(attributePath.getTextContent());
                }

                Node attributeRecursive = attributes.getNamedItem("recursive");
                
                if (attributeRecursive == null)
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  new String("recursive"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageLoadJobfileInputEntryMissingRecursiveAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.inputTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), ""});
                    continue;
                }

                String recursive = attributeRecursive.getTextContent();

                if (recursive.equals("true") == true)
                {
                    recursive = getI10nString("tableInputColumnRecursiveCellValueYes");
                }
                else if (recursive.equals("false") == true)
                {
                    recursive = getI10nString("tableInputColumnRecursiveCellValueNo");
                }
                else
                {
                    Object[] messageArguments = { new String("input"),
                                                  new Integer(i + 1),
                                                  recursive,
                                                  new String("recursive"),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageLoadJobfileInputEntryRecursiveAttributeInvalidValue"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.inputTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), ""});
                    continue;
                }

                this.inputTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), recursive});
            }
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-5);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-6);
        }
    }
    
    public void saveJobfile(File jobFile)
    {
        boolean jobfileWasSpecified = false;

        if (jobFile != null)
        {
            jobfileWasSpecified = true;
        }
        else
        {
            final JFileChooser chooser = new JFileChooser(getI10nString("fileChooserSaveJobfileCaption"));
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            chooser.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                    {
                        final File resultFile = (File) e.getNewValue();
                    }
                }
            });

            FileFilter fileFilter = new FileNameExtensionFilter(getI10nString("fileChooserSaveJobfileFiletype"), "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            if (this.workingDirectory != null)
            {
                if (this.workingDirectory.exists() == true)
                {
                    if (this.workingDirectory.isDirectory() == true)
                    {
                        chooser.setCurrentDirectory(this.workingDirectory);
                    }   
                }
            }

            chooser.setVisible(true);
            final int result = chooser.showOpenDialog(null);
            chooser.setVisible(false); 

            if (result != JFileChooser.APPROVE_OPTION)
            {
                return;
            }
            
            jobFile = chooser.getSelectedFile();
        }

        if (jobFile.exists() == true)
        {
            if (jobFile.isFile() != true)
            {
                Object[] messageArguments = { jobFile.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageSaveJobfileIsntAFile"));
                System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
                
                return;
            }
            
            if (jobFile.canWrite() != true)
            {
                Object[] messageArguments = { jobFile.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageSaveJobfileCantWriteFile"));
                System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
                
                return;
            }
            
            SaveJobfileOverrideDialog overrideDialog = new SaveJobfileOverrideDialog(this);
            overrideDialog.setVisible(true);
            
            if (overrideDialog.getOverride() != true)
            {
                return;
            }
        }

        if (jobfileWasSpecified != true)
        {
            this.workingDirectory = jobFile.getParentFile();
        }


        try
        {
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(jobFile.getAbsolutePath()),
                                    "UTF8"));

            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- This file was generated by epubcheck1_recursive_checker1 GUI, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/publishing-systems/automated_digital_publishing/ and http://www.publishing-systems.org). -->\n");
            writer.write("<epubcheck1-recursive-checker1-job>\n");
            writer.write("  <in>\n");
            
            for (int i = 0; i < this.inputTable.getRowCount(); i++)
            {
                String path = (String) this.inputTable.getValueAt(i, 0);
                
                File inputFile = new File(path);
                
                if (inputFile.exists() != true)
                {
                    Object[] messageArguments = { path,
                                                  new Integer(i + 1),
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageSaveJobfileInputEntryReferencedFileDoesntExist"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    continue;
                }
                
                String recursive = (String) this.inputTable.getValueAt(i, 1);
                
                if (recursive.equals(getI10nString("tableInputColumnRecursiveCellValueYes")) == true)
                {
                    recursive = "true";
                }
                else if (recursive.equals(getI10nString("tableInputColumnRecursiveCellValueNo")) == true)
                {
                    recursive = "false";
                }
                else
                {
                    Object[] messageArguments = { new Integer(i + 1),
                                                  recursive,
                                                  getI10nString("tableInputColumnRecursiveCaption"),
                                                  path,
                                                  jobFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageSaveJobfileInputEntryRecursiveAttributeInvalidValue"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    continue;
                }

                writer.write("    <input path=\"" + path + "\" recursive=\"" + recursive + "\"/>\n");
            }
            
            writer.write("  </in>\n");
            writer.write("</epubcheck1-recursive-checker1-job>\n");

            writer.flush();
            writer.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-43);
        }
    }
    
    public void addInput()
    {
        final JFileChooser chooser = new JFileChooser(getI10nString("fileChooserAddInputCaption"));
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (this.workingDirectory != null)
        {
            if (this.workingDirectory.exists() == true)
            {
                if (this.workingDirectory.isDirectory() == true)
                {
                    chooser.setCurrentDirectory(this.workingDirectory);
                }   
            }
        }

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    final File resultFile = (File) e.getNewValue();
                }
            }
        });

        FileFilter fileFilter = new FileNameExtensionFilter(getI10nString("fileChooserAddInputFiletype"), "epub");
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);

        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        if (result != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File inputFile = chooser.getSelectedFile();
        
        if (inputFile.exists() != true)
        {
            Object[] messageArguments = { inputFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageAddInputFileDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
        
            return;
        }
        
        if (inputFile.isDirectory() == true)
        {
            this.workingDirectory = inputFile;
        }
        else if (inputFile.isFile() == true)
        {
            this.workingDirectory = inputFile.getParentFile();
        }

        boolean recursive = false;

        if (inputFile.isDirectory() == true)
        {
            AddInputDecideRecursiveDialog recursiveDialog = new AddInputDecideRecursiveDialog(this);
            recursiveDialog.setVisible(true);
            
            recursive = recursiveDialog.getRecursive();
        }
        
        if (recursive == true)
        {
            this.inputTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), getI10nString("tableInputColumnRecursiveCellValueYes")});
        }
        else
        {
            this.inputTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), getI10nString("tableInputColumnRecursiveCellValueNo")});
        }
    }
    
    public void removeInput()
    {
        int rows[] = this.inputTable.getSelectedRows();
        
        for (int i = rows.length - 1; i >= 0; i--)
        {
            this.inputTableModel.removeRow(rows[i]);
        }
    }
    
    public void check()
    {
        if (this.resultsTableSelectionListener != null)
        {
            this.resultsTable.getSelectionModel().removeListSelectionListener(this.resultsTableSelectionListener);
            this.resultsTableSelectionListener = null;
        }
        
        this.resultsTableModel.setRowCount(0);
        this.resultsTable.revalidate();

        this.textAreaResult.setText("");

        String programPath = epubcheck1_recursive_checker1.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() == true)
        {
            if (deleteFileRecursively(tempDirectory) != 0)
            {
                Object[] messageArguments = { tempDirectory.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageCheckCleanTempDirectoryCantClean"));
                System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                return;
            }
        }

        if (tempDirectory.mkdirs() != true)
        {
            Object[] messageArguments = { tempDirectory.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCheckCantCreateTempDirectory"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return;
        }

        File jobFile = new File(tempDirectory.getAbsolutePath() + File.separator + "jobfile.xml");
        saveJobfile(jobFile);
        
        if (jobFile.exists() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCheckJobfileWasntCreated"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return;
        }

        if (jobFile.isFile() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCheckJobfileIsntFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return;
        }

        if (jobFile.canRead() != true)
        {
            Object[] messageArguments = { jobFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageCheckJobfileIsntReadable"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return;
        }

        ProcessBuilder builder = new ProcessBuilder("java", "-Duser.language=" + getLocale().getLanguage(), "-Duser.country=" + getLocale().getCountry(), "epubcheck1_recursive_checker1", jobFile.getAbsolutePath(), tempDirectory.getAbsolutePath());
        builder.directory(new File(programPath + ".." + File.separator + ".."));
        builder.redirectErrorStream(true);

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
            setCursor(Cursor.getDefaultCursor());
            ex.printStackTrace();
            return;
        }
        
        setCursor(Cursor.getDefaultCursor());


        File resultInfoFile = new File(tempDirectory.getAbsolutePath() + File.separator + "result_info.xml");

        if (resultInfoFile.exists() != true)
        {
            Object[] messageArguments = { resultInfoFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultInfoFileDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        if (resultInfoFile.isFile() != true)
        {
            Object[] messageArguments = { resultInfoFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultInfoFileIsntAFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        if (resultInfoFile.canRead() != true)
        {
            Object[] messageArguments = { resultInfoFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultInfoFileIsntReadable"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return;
        }

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(resultInfoFile);
            document.getDocumentElement().normalize();

            NodeList checkResultNodeList = document.getElementsByTagName("check-result");

            if (checkResultNodeList.getLength() <= 0)
            {
                return;
            }

            for (int i = 0; i < checkResultNodeList.getLength(); i++)
            {
                Node checkResultNode = checkResultNodeList.item(i);
                NamedNodeMap attributes = checkResultNode.getAttributes();

                if (attributes == null)
                {
                    Object[] messageArguments = { new String("check-result"),
                                                  new Integer(i + 1),
                                                  resultInfoFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultInfoCheckResultEntryMissingAttributes"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
                    
                    this.inputTableModel.addRow(new Object[]{"", ""});
                    continue;
                }
                
                Node attributeInput = attributes.getNamedItem("input");
                
                if (attributeInput == null)
                {
                    Object[] messageArguments = { new String("check-result"),
                                                  new Integer(i + 1),
                                                  new String("input"),
                                                  resultInfoFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultInfoCheckResultEntryMissingInputAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.resultsTableModel.addRow(new Object[]{"", ""});
                    continue;
                }

                File inputFile = new File(attributeInput.getTextContent());

                if (inputFile.isAbsolute() != true)
                {
                    String relativePath = resultInfoFile.getAbsoluteFile().getParent();

                    if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                    {
                        // Remove dot that references the local, current directory.
                        relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                    }
                    
                    if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                    {
                        relativePath += File.separator;
                    }
                    
                    relativePath += attributeInput.getTextContent();
                    inputFile = new File(relativePath);
                }

                if (inputFile.exists() != true)
                {
                    Object[] messageArguments = { inputFile.getAbsolutePath(),
                                                  new String("check-result"),
                                                  new Integer(i + 1),
                                                  resultInfoFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultInfoCheckResultEntryReferencedFileDoesntExist"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.resultsTableModel.addRow(new Object[]{"", ""});
                    continue;
                }

                Node attributeStatus = attributes.getNamedItem("status");
                
                if (attributeStatus == null)
                {
                    Object[] messageArguments = { new String("check-result"),
                                                  new Integer(i + 1),
                                                  new String("status"),
                                                  resultInfoFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultInfoCheckResultEntryMissingStatusAttribute"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.resultsTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), ""});
                    continue;
                }

                String status = attributeStatus.getTextContent();

                if (status.equals("valid") == true)
                {
                    status = getI10nString("tableResultColumnStatusCellValueValid");
                }
                else if (status.equals("warning") == true)
                {
                    status = getI10nString("tableResultColumnStatusCellValueWarning");
                }
                else if (status.equals("error") == true)
                {
                    status = getI10nString("tableResultColumnStatusCellValueError");
                }
                else
                {
                    Object[] messageArguments = { new String("check-result"),
                                                  new Integer(i + 1),
                                                  status,
                                                  new String("status"),
                                                  resultInfoFile.getAbsolutePath() };
                    MessageFormat formatter = new MessageFormat("");
                    formatter.setLocale(getLocale());

                    formatter.applyPattern(getI10nString("messageResultInfoCheckResultEntryStatusAttributeInvalidValue"));
                    System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                    this.resultsTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), ""});
                    continue;
                }

                this.resultsTableModel.addRow(new Object[]{inputFile.getAbsolutePath(), status});
            }
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-4);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-5);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-6);
        }
        
        this.resultsTableSelectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event)
            {
                if (resultsTable.getSelectedRow() > -1)
                {
                    LoadResultFile(resultsTable.getSelectedRow());
                }
            }
        };
        
        this.resultsTable.getSelectionModel().addListSelectionListener(this.resultsTableSelectionListener);
    }
    
    public int LoadResultFile(int row)
    {
        if (row < 0 || row > this.resultsTable.getRowCount())
        {
            return 1;
        }
        
        this.textAreaResult.setText("");
        
        String programPath = epubcheck1_recursive_checker1.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File tempDirectory = new File(programPath + "temp");

        if (tempDirectory.exists() == true)
        {
            if (tempDirectory.isDirectory() != true)
            {
                Object[] messageArguments = { tempDirectory.getAbsolutePath() };
                MessageFormat formatter = new MessageFormat("");
                formatter.setLocale(getLocale());

                formatter.applyPattern(getI10nString("messageLoadResultFileTempPathIsntDirectory"));
                System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

                return -1;
            }
        }
        else
        {
            Object[] messageArguments = { tempDirectory.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageLoadResultFileTempDirectoryDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return -1;
        }

        File resultFile = new File(tempDirectory.getAbsolutePath() + File.separator + "result_" + (row + 1) + ".log");
        
        if (resultFile.exists() != true)
        {
            Object[] messageArguments = { resultFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultFileDoesntExist"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return -1;
        }

        if (resultFile.isFile() != true)
        {
            Object[] messageArguments = { resultFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultFileIsntAFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return -1;
        }

        if (resultFile.canRead() != true)
        {
            Object[] messageArguments = { resultFile.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageResultFileCantReadFile"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));
            
            return -1;
        }

        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        
        try
        {
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(resultFile),
                                    "UTF8"));

            int charactersRead = reader.read(buffer, 0, buffer.length);

            while (charactersRead > 0)
            {
                stringBuilder.append(buffer, 0, charactersRead);
            
                charactersRead = reader.read(buffer, 0, buffer.length);
            }
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

        this.textAreaResult.setText(stringBuilder.toString());
        this.textAreaResult.setCaretPosition(0);

        return 0;
    }
    
    public int deleteFileRecursively(File file)
    {
        if (file.isDirectory() == true)
        {
            for (File child : file.listFiles())
            {
                if (deleteFileRecursively(child) != 0)
                {
                    return -1;
                }
            }
        }
        
        if (file.delete() != true)
        {
            Object[] messageArguments = { file.getAbsolutePath() };
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(getLocale());

            formatter.applyPattern(getI10nString("messageDeleteRecursivelyCantDelete"));
            System.out.println("epubcheck1_recursive_checker1 GUI: " + formatter.format(messageArguments));

            return -1;
        }
    
        return 0;
    }

    public Locale getLocale()
    {
        return Locale.getDefault();
    }

    /**
     * @brief This method interprets i10n strings from a .properties file as encoded in UTF-8.
     */
    public String getI10nString(String key)
    {
        if (this.i10nGUI == null)
        {
            this.i10nGUI = ResourceBundle.getBundle("i10n.i10nGUI", getLocale());
        }
    
        try
        {
            return new String(this.i10nGUI.getString(key).getBytes("ISO-8859-1"), "UTF-8");
        }
        catch (UnsupportedEncodingException ex)
        {
            return this.i10nGUI.getString(key);
        }
    }


    private ResourceBundle i10nGUI;

    private JTabbedPane tabbedPane;

    private JTable inputTable;
    private DefaultTableModel inputTableModel;
    private JButton buttonLoad;
    private JButton buttonSave;
    private JButton buttonAdd;
    private JButton buttonRemove;
  
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JTextArea textAreaResult;
    private JButton buttonCheck;
  
    private JButton buttonAbout;
    //private JButton buttonApply;
    private JButton buttonExit;
  
    private File workingDirectory;

    private ListSelectionListener resultsTableSelectionListener;
}

class InputTableModel extends DefaultTableModel
{
    public InputTableModel(String columnCaptions[])
    {
        super(0, columnCaptions.length);
        setColumnIdentifiers(columnCaptions);
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
        if (column == 0)
        {
            // Allow to insert paths manually (and to make them relative).
            return true;
        }
        
       return false;
    }
}

class ResultsTableModel extends DefaultTableModel
{
    public ResultsTableModel(String columnCaptions[])
    {
        super(0, columnCaptions.length);
        setColumnIdentifiers(columnCaptions);
    }

    @Override
    public boolean isCellEditable(int row, int column)
    {
       return false;
    }
}

