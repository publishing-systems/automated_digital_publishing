/* Copyright (C) 2014-2015  Stephan Kreutzer
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
 * @file $/gui/html2epub1_config_file_setup/html2epub1_config_file_setup.java
 * @brief Editor for configuring the input and output files of a html2epub1
 *     configuration files.
 * @author Stephan Kreutzer
 * @since 2014-03-02
 */



import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent; 
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Iterator;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;



public class html2epub1_config_file_setup
  extends JFrame
  implements ActionListener
{
    public static void main(String[] args)
    {
        System.out.print("html2epub1_config_file_setup  Copyright (C) 2014-2015  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    

        File configFile = null;
    
        if (args.length == 1)
        {        
            configFile = new File(args[0]);
            
            if (configFile.exists() != true)
            {
                System.out.print("html2epub1_config_file_setup: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (configFile.isFile() != true)
            {
                System.out.print("html2epub1_config_file_setup: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-2);
            }

            if (configFile.canRead() != true)
            {
                System.out.print("html2epub1_config_file_setup: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-3);
            }
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Select html2epub1 configuration file");
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

            FileFilter fileFilter = new FileNameExtensionFilter("html2epub1 configuration files", "xml");
            chooser.addChoosableFileFilter(fileFilter);
            chooser.setFileFilter(fileFilter);

            chooser.setVisible(true);
            final int result = chooser.showOpenDialog(null);
            chooser.setVisible(false); 

            if (result == JFileChooser.APPROVE_OPTION)
            {
                configFile = chooser.getSelectedFile();
            }
            else
            {
                System.exit(1);
            }
        }

        html2epub1_config_file_setup frame = new html2epub1_config_file_setup(configFile);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }

    public html2epub1_config_file_setup(File configFile)
    {
        super("File Setup for a Configuration File of html2epub1");

        this.programPath = html2epub1_config_file_setup.class.getProtectionDomain().getCodeSource().getLocation().getFile();

        try
        {
            this.programPath = new File(this.programPath).getCanonicalPath() + File.separator;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        this.configFile = configFile;
        
        this.xhtmlLabelList = new ArrayList<JLabel>();
        this.xhtmlFileList = new ArrayList<JTextField>();
        this.titleList = new ArrayList<JTextField>();
        this.buttonUpList = new ArrayList<JButton>();
        this.buttonDownList = new ArrayList<JButton>();
        this.buttonRemoveList = new ArrayList<JButton>();

        JPanel panelConfig = new JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        panelConfig.setLayout(gridbag);
      
        this.labelConfigurationFile = new JLabel("Configuration",
                                    SwingConstants.LEFT);
      
        GridBagConstraints gridbagConstraints = new GridBagConstraints();
        gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 0.0;
        panelConfig.add(this.labelConfigurationFile, gridbagConstraints);

        this.textFieldConfigurationFile = new JTextField(45);
        this.textFieldConfigurationFile.setEditable(false);
        this.textFieldConfigurationFile.setText(this.configFile.getAbsolutePath());
        
        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelConfig.add(this.textFieldConfigurationFile, gridbagConstraints);
        
        panelConfig.setBorder(BorderFactory.createEtchedBorder()); 
        
        
        JPanel panelOutDirectory = new JPanel();

        gridbag = new GridBagLayout();
        panelOutDirectory.setLayout(gridbag);
        
        gridbagConstraints = new GridBagConstraints();
        
        this.labelOutDirectory = new JLabel("Out",
                                            SwingConstants.LEFT);

        gridbagConstraints.gridwidth = 1;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 0.0;
        panelOutDirectory.add(this.labelOutDirectory, gridbagConstraints);
                                            
        this.textFieldOutDirectory = new JTextField(30);
        this.textFieldOutDirectory.setToolTipText("Output directory");
        
        gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelOutDirectory.add(this.textFieldOutDirectory, gridbagConstraints);
        
        JButton buttonOutDirectory = new JButton("Choose");
        buttonOutDirectory.addActionListener(this);
        
        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 0.0;
        panelOutDirectory.add(buttonOutDirectory, gridbagConstraints);
    
        panelOutDirectory.setBorder(BorderFactory.createEtchedBorder()); 
    
        JPanel panelHead = new JPanel();

        gridbag = new GridBagLayout();
        panelHead.setLayout(gridbag);
        
        gridbagConstraints = new GridBagConstraints();        
        gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 1.0;
        panelHead.add(panelConfig, gridbagConstraints);
        panelHead.add(panelOutDirectory, gridbagConstraints);
        this.getContentPane().add(panelHead, BorderLayout.PAGE_START);
    
    
        this.panelMain = new JPanel();
        
        if (readConfigurationFile() == true)
        {
            ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
            this.labelConfigurationFile.setIcon(iconCorrect);
        }
        else
        {
            ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
            this.labelConfigurationFile.setIcon(iconIncorrect);
        }
        
        this.buttonAdd = new JButton("Add new XHTML file");
        this.buttonAdd.addActionListener(this);
        
        this.generateGUI();
        
        this.panelMain.setBorder(BorderFactory.createEtchedBorder()); 
        this.getContentPane().add(this.panelMain, BorderLayout.CENTER);


        JPanel panelButtons = new JPanel();
        
        JButton buttonExit = new JButton("Exit");
        buttonExit.addActionListener(this);
        panelButtons.add(buttonExit);
        
        JButton buttonCheck = new JButton("Check");
        buttonCheck.addActionListener(this);
        panelButtons.add(buttonCheck);
        
        JButton buttonSave = new JButton("Save");
        buttonSave.addActionListener(this);
        panelButtons.add(buttonSave);
        
        JButton buttonAbout = new JButton("About");
        buttonAbout.addActionListener(this);
        panelButtons.add(buttonAbout);
        
        panelButtons.setBorder(BorderFactory.createEtchedBorder()); 
        getContentPane().add(panelButtons, BorderLayout.PAGE_END);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event)
            {
                event.getWindow().setVisible(false);
                event.getWindow().dispose();
                System.exit(2);
            }
        });
        
        this.pack();
    }
    
    protected boolean generateGUI()
    {
        this.panelMain.removeAll();
        this.xhtmlLabelList.clear();
        this.buttonUpList.clear();
        this.buttonDownList.clear();
        this.buttonRemoveList.clear();
        
        GridBagLayout gridbag = new GridBagLayout();
        this.panelMain.setLayout(gridbag);
           
        { 
            ImageIcon iconUp = new ImageIcon(this.programPath + "up.png");
            ImageIcon iconDown = new ImageIcon(this.programPath + "down.png");
            ImageIcon iconRemove = new ImageIcon(this.programPath + "remove.png");
        
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
        
            Iterator<JTextField> iter = this.xhtmlFileList.iterator();
            int i = 1;

            while (iter.hasNext())
            {
                JLabel label = new JLabel("XHTML #" + i,
                                          SwingConstants.LEFT);
            
                gridbagConstraints.gridwidth = 1;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                this.xhtmlLabelList.add(label);
                panelMain.add(label, gridbagConstraints);
            
                JTextField textField = iter.next();

                gridbagConstraints.gridwidth = 2;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 1.0;
                panelMain.add(textField, gridbagConstraints);
                
                JTextField title = this.titleList.get(i-1);
                title.setToolTipText("Title");
                gridbagConstraints.gridwidth = 3;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                panelMain.add(title, gridbagConstraints);
                
                JButton buttonUp = new JButton(iconUp);
                buttonUp.setToolTipText("Move up");
                buttonUp.addActionListener(this);
                
                gridbagConstraints.gridwidth = 4;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                if (i == 1)
                {
                    buttonUp.setEnabled(false);
                }
                
                buttonUpList.add(buttonUp);
                panelMain.add(buttonUp, gridbagConstraints);
                
                JButton buttonDown = new JButton(iconDown);
                buttonDown.setToolTipText("Move down");
                buttonDown.addActionListener(this);
                
                gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                if (i == this.xhtmlFileList.size())
                {
                    buttonDown.setEnabled(false);
                }
                
                buttonDownList.add(buttonDown);
                panelMain.add(buttonDown, gridbagConstraints);
                
                JButton buttonRemove = new JButton(iconRemove);
                buttonRemove.setToolTipText("Remove");
                buttonRemove.addActionListener(this);
                
                gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
                gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagConstraints.weightx = 0.0;
                
                buttonRemoveList.add(buttonRemove);
                panelMain.add(buttonRemove, gridbagConstraints);

                i++;
            }
        }
        
        {
            GridBagConstraints gridbagConstraints = new GridBagConstraints();
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 1.0;
            this.panelMain.add(this.buttonAdd, gridbagConstraints);
        }
        
        this.CheckFields();
        this.pack();
    
        return true;
    }

    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().length() > 0)
        {
            String clickedButton = event.getActionCommand();
        
            if (clickedButton.equalsIgnoreCase("Add new XHTML file") == true)
            {
                this.AddNewXHTMLInputFile();
                this.generateGUI();
                this.titleList.get(this.titleList.size()-1).requestFocus();
            }
            else if (clickedButton.equalsIgnoreCase("Choose") == true)
            {
                final JFileChooser chooser = new JFileChooser("Select output directory");
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                
                String outDirectoryText = this.textFieldOutDirectory.getText();

                if (outDirectoryText.length() > 0)
                {
                    File currentOutDirectory = new File(outDirectoryText);
                    
                    if (currentOutDirectory.isAbsolute() == true)
                    {
                        if (currentOutDirectory.exists() == true)
                        {
                            if (currentOutDirectory.isDirectory() == true)
                            {
                                chooser.setCurrentDirectory(currentOutDirectory);
                            }
                        }
                    }
                    else
                    {
                        String relativePath = this.configFile.getAbsoluteFile().getParent();

                        if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                        {
                            // Remove dot that references the local, current directory.
                            relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                        }
                        
                        if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                        {
                            relativePath += File.separator;
                        }
                        
                        relativePath += outDirectoryText;
                        currentOutDirectory = new File(relativePath);
                        
                        if (currentOutDirectory.exists() == true)
                        {
                            if (currentOutDirectory.isDirectory() == true)
                            {
                                chooser.setCurrentDirectory(currentOutDirectory);
                            }
                        }
                    }
                }
                else
                {
                    chooser.setCurrentDirectory(this.configFile.getAbsoluteFile().getParentFile());
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

                if (result == JFileChooser.APPROVE_OPTION)
                {
                    this.textFieldOutDirectory.setText(chooser.getSelectedFile().getPath());
                    this.CheckFields();
                }
            }
            else if (clickedButton.equalsIgnoreCase("Exit") == true)
            {
                System.exit(2);
            }
            else if (clickedButton.equalsIgnoreCase("Check") == true)
            {
                this.CheckFields();
            }
            else if (clickedButton.equalsIgnoreCase("Save") == true)
            {
                if (CheckFields() == true)
                {
                    if (writeConfigurationFile() == true)
                    {
                        ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
                        this.labelConfigurationFile.setIcon(iconCorrect);
                    }
                    else
                    {
                        ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
                        this.labelConfigurationFile.setIcon(iconIncorrect);
                    }
                }
            }
            else if (clickedButton.equalsIgnoreCase("About") == true)
            {

            }
            else
            {
            
            }
        }
        else
        {
            Object source = event.getSource();
        
            Iterator<JButton> iter = this.buttonUpList.iterator();
            int i = 0;
            boolean up = false;
            boolean down = false;
            boolean remove = false;

            while (iter.hasNext())
            {
                JButton buttonUp = iter.next();
                JButton buttonDown = this.buttonDownList.get(i);
                JButton buttonRemove = this.buttonRemoveList.get(i);
                
                if (source == buttonUp)
                {
                    up = true;
                    break;
                }
                else if (source == buttonDown)
                {
                    down = true;
                    break;
                }
                else if (source == buttonRemove)
                {
                    remove = true;
                    break;
                }

                i++;
            }
            
            if (up == true ||
                down == true ||
                remove == true)
            {
                if (up == true)
                {
                    if (i > 0)
                    {
                        Collections.swap(this.xhtmlFileList, i, i-1);
                        Collections.swap(this.titleList, i, i-1);
                    }
                }
                else if (down == true)
                {
                    if (i < (this.xhtmlFileList.size() - 1))
                    {
                        Collections.swap(this.xhtmlFileList, i, i+1);
                        Collections.swap(this.titleList, i, i+1);
                    }
                }
                else if (remove == true)
                {
                    this.xhtmlFileList.remove(i);
                    this.titleList.remove(i);
                }
                
                this.generateGUI();
            }
        }
    }
    
    protected boolean AddNewXHTMLInputFile()
    {
        final JFileChooser chooser = new JFileChooser("Select new XHTML input file");
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setCurrentDirectory(this.configFile.getAbsoluteFile().getParentFile());

        chooser.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY))
                {
                    final File resultFile = (File) e.getNewValue();
                }
            }
        });

        FileFilter fileFilter = new FileNameExtensionFilter("XHTML files", "xhtml", "html");
        chooser.addChoosableFileFilter(fileFilter);
        chooser.setFileFilter(fileFilter);

        chooser.setVisible(true);
        final int result = chooser.showOpenDialog(null);
        chooser.setVisible(false); 

        if (result == JFileChooser.APPROVE_OPTION)
        {
            File xhtmlInputFile = chooser.getSelectedFile();
           
            JTextField textField = new JTextField(30);
            textField.setText(xhtmlInputFile.getAbsolutePath());

            this.xhtmlFileList.add(textField);
            this.titleList.add(new JTextField(15));
        }
        
        return true;
    }
    
    protected boolean CheckFields()
    {
        boolean valid = true;
    
        ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
        ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");

        boolean outDirectoryValid = true;
        String outDirectoryText = this.textFieldOutDirectory.getText();
        
        if (outDirectoryText.length() > 0)
        {
            File outDirectory = new File(this.textFieldOutDirectory.getText());

            if (outDirectory.isAbsolute() == true)
            {
                if (outDirectory.exists() != true)
                {
                    outDirectoryValid = false;
                    this.textFieldOutDirectory.setToolTipText("'" + outDirectory.getAbsolutePath() + "' doesn't exist!");
                }
                else
                {
                    if (outDirectory.isDirectory() != true)
                    {
                        outDirectoryValid = false;
                        this.textFieldOutDirectory.setToolTipText("Isn't a directory!");
                    }
                }
                
                if (outDirectoryValid == true)
                {
                    this.textFieldOutDirectory.setToolTipText("Absolute path to output directory");
                }
            }
            else
            {
                String relativePath = this.configFile.getAbsoluteFile().getParent();

                if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                {
                    // Remove dot that references the local, current directory.
                    relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                }
                
                if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                {
                    relativePath += File.separator;
                }
                
                relativePath += outDirectoryText;
                outDirectory = new File(relativePath);
                
                if (outDirectory.exists() != true)
                {
                    outDirectoryValid = false;
                    this.textFieldOutDirectory.setToolTipText("'" + outDirectory.getAbsolutePath() + "' doesn't exist!");
                }
                else
                {
                    if (outDirectory.isDirectory() != true)
                    {
                        outDirectoryValid = false;
                        this.textFieldOutDirectory.setToolTipText("Isn't a directory!");
                    }
                }
            
                if (outDirectoryValid == true)
                {
                    this.textFieldOutDirectory.setToolTipText("Relative path of the output directory (relative to the directory of the configuration file)");
                }
            }
        }
        else
        {
            outDirectoryValid = false;
        }
        
        if (outDirectoryValid == true)
        {
            this.labelOutDirectory.setIcon(iconCorrect);
        }
        else
        {
            this.labelOutDirectory.setIcon(iconIncorrect);
            valid = false;
        }
        
        
        Iterator<JTextField> iter = this.xhtmlFileList.iterator();
        int i = 0;

        while (iter.hasNext())
        {
            boolean xhtmlFileValid = true;
        
            JLabel label = this.xhtmlLabelList.get(i);
            JTextField textField = iter.next();
            JTextField title = this.titleList.get(i);

            if (title.getText().length() <= 0)
            {
                label.setIcon(iconIncorrect);
                title.setToolTipText("Title is missing!");
                
                i++;
                continue;
            }
            else
            {
                title.setToolTipText("Title");
            }

            String xhtmlFileText = textField.getText();
            File xhtmlFile = new File(xhtmlFileText);

            if (xhtmlFile.isAbsolute() == true)
            {
                if (xhtmlFile.exists() != true)
                {
                    xhtmlFileValid = false;
                    textField.setToolTipText("'" + xhtmlFile.getAbsolutePath() + "' doesn't exist!");
                }
                else
                {
                    if (xhtmlFile.isFile() != true)
                    {
                        xhtmlFileValid = false;
                        textField.setToolTipText("Isn't a file!");
                    }
                }
                
                if (xhtmlFileValid == true)
                {
                    textField.setToolTipText("Absolute path to XHTML input file");
                }
            }
            else
            {
                String relativePath = this.configFile.getAbsoluteFile().getParent();
                
                if (relativePath.substring((relativePath.length() - File.separator.length()) - new String(".").length(), relativePath.length()).equalsIgnoreCase(File.separator + "."))
                {
                    // Remove dot that references the local, current directory.
                    relativePath = relativePath.substring(0, relativePath.length() - new String(".").length());
                }
                
                if (relativePath.substring(relativePath.length() - File.separator.length(), relativePath.length()).equalsIgnoreCase(File.separator) != true)
                {
                    relativePath += File.separator;
                }
                
                relativePath += xhtmlFileText;
                xhtmlFile = new File(relativePath);
                
                if (xhtmlFile.exists() != true)
                {
                    xhtmlFileValid = false;
                    textField.setToolTipText("'" + xhtmlFile.getAbsolutePath() + "' doesn't exist!");
                }
                else
                {
                    if (xhtmlFile.isFile() != true)
                    {
                        xhtmlFileValid = false;
                        textField.setToolTipText("Isn't a file!");
                    }
                }
            
                if (xhtmlFileValid == true)
                {
                    textField.setToolTipText("Relative path of the XHTML input file (relative to the directory of the configuration file)");
                }
            }

            if (xhtmlFileValid == true)
            {
                label.setIcon(iconCorrect);
            }
            else
            {
                label.setIcon(iconIncorrect);
                valid = false;
            }

            i++;
        }
        
        this.pack();
        
        return valid;
    }
  
    protected boolean readConfigurationFile()
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.configFile);
            document.getDocumentElement().normalize();

            NodeList outDirectoryNodeList = document.getElementsByTagName("outDirectory");

            if (outDirectoryNodeList.getLength() >= 1)
            {
                this.textFieldOutDirectory.setText(outDirectoryNodeList.item(0).getTextContent());
            }
            else
            {
                return false;
            }

            NodeList inNodeList = document.getElementsByTagName("in");

            if (inNodeList.getLength() <= 0)
            {
                return false;
            }

            NodeList inSubNodeList = inNodeList.item(0).getChildNodes();

            for (int i = 0; i < inSubNodeList.getLength(); i++)
            {
                Node inNode = inSubNodeList.item(i);

                if (inNode.getNodeName().equalsIgnoreCase("inFile") == true)
                {
                    JTextField textField = new JTextField(30);
                    textField.setText(inNode.getTextContent());
                    this.xhtmlFileList.add(textField);
                    
                    JTextField title = new JTextField(15);
                    NamedNodeMap attributes = inNode.getAttributes();
                    
                    if (attributes != null)
                    {
                        Node attributeTitle = attributes.getNamedItem("title");
                        
                        if (attributeTitle != null)
                        {
                            title.setText(attributeTitle.getTextContent());
                        }
                    }
                    
                    this.titleList.add(title);
                }
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

        return true;
    }
    
    protected boolean writeConfigurationFile()
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(this.configFile);
            document.getDocumentElement().normalize();


            NodeList outNodeList = document.getElementsByTagName("out");

            if (outNodeList.getLength() <= 0)
            {
                return false;
            }

            NodeList outSubNodeList = outNodeList.item(0).getChildNodes();

            for (int i = outSubNodeList.getLength() - 1; i >= 0; i--)
            {
                Node outNode = outSubNodeList.item(i);

                if (outNode.getNodeName().equalsIgnoreCase("outDirectory") == true)
                {
                    outNodeList.item(0).removeChild(outNode);
                }
            }
	          
            Element outDirectoryNode = document.createElement("outDirectory");
            Text outDirectoryNodeText = document.createTextNode(this.textFieldOutDirectory.getText());
            outDirectoryNode.appendChild(outDirectoryNodeText);
            outNodeList.item(0).appendChild(outDirectoryNode);


            NodeList inNodeList = document.getElementsByTagName("in");

            if (inNodeList.getLength() <= 0)
            {
                return false;
            }

            NodeList inSubNodeList = inNodeList.item(0).getChildNodes();

            for (int i = inSubNodeList.getLength() - 1; i >= 0; i--)
            {
                Node inNode = inSubNodeList.item(i);

                if (inNode.getNodeName().equalsIgnoreCase("inFile") == true)
                {
                    inNodeList.item(0).removeChild(inNode);
                }
            }


            Iterator<JTextField> iter = this.xhtmlFileList.iterator();
            int i = 0;

            while (iter.hasNext())
            {
                JTextField title = this.titleList.get(i);
                JTextField textField = iter.next();

                Element xhtmlNode = document.createElement("inFile");
                Text xhtmlNodeText = document.createTextNode(textField.getText());
                xhtmlNode.appendChild(xhtmlNodeText);
                
                xhtmlNode.setAttribute("title", title.getText());

                inNodeList.item(0).appendChild(xhtmlNode);

                i++;
            }
	          
	          
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult =  new StreamResult(this.configFile);
            transformer.transform(source, streamResult);
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-7);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-8);
        }
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-9);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-10);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }
        
        return true;
    }
    
    
    protected File configFile;
    
    protected JPanel panelMain;
    
    private JLabel labelConfigurationFile;
    private JTextField textFieldConfigurationFile;
    
    private JLabel labelOutDirectory;
    private JTextField textFieldOutDirectory;
    
    private JButton buttonAdd;
    
    private ArrayList<JLabel> xhtmlLabelList;
    private ArrayList<JTextField> xhtmlFileList;
    private ArrayList<JTextField> titleList;
    private ArrayList<JButton> buttonUpList;
    private ArrayList<JButton> buttonDownList;
    private ArrayList<JButton> buttonRemoveList;
    
    private String programPath;
}
