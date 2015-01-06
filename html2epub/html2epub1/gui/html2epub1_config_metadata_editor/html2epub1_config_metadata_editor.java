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
 * @file $/gui/html2epub1_config_metadata_editor/html2epub1_config_metadata_editor.java
 * @brief Editor for the metadata of html2epub1 configuration files.
 * @author Stephan Kreutzer
 * @since 2014-02-02
 */



import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.beans.PropertyChangeListener; 
import java.beans.PropertyChangeEvent; 
import java.io.File; 
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Map;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Text;



public class html2epub1_config_metadata_editor
  extends JFrame
  implements ActionListener
{
    public static void main(String[] args)
    {
        System.out.print("html2epub1_config_metadata_editor  Copyright (C) 2014  Stephan Kreutzer\n" +
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
                System.out.print("html2epub1_config_metadata_editor: '" + configFile.getAbsolutePath() + "' doesn't exist.\n");
                System.exit(-1);
            }

            if (configFile.isFile() != true)
            {
                System.out.print("html2epub1_config_metadata_editor: '" + configFile.getAbsolutePath() + "' isn't a file.\n");
                System.exit(-2);
            }

            if (configFile.canRead() != true)
            {
                System.out.print("html2epub1_config_metadata_editor: '" + configFile.getAbsolutePath() + "' isn't readable.\n");
                System.exit(-3);
            }
        }
        else
        {
            final JFileChooser chooser = new JFileChooser("Select html2epub1 configuration file");
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            //final File currentDirectory = new File("/home");
            //chooser.setCurrentDirectory(currentDirectory);

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
                System.exit(0);
            }
        }

        html2epub1_config_metadata_editor frame = new html2epub1_config_metadata_editor(configFile);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }

    public html2epub1_config_metadata_editor(File configFile)
    {
        super("Metadata Editor for a Configuration File of html2epub1");
        
        this.programPath = html2epub1_config_metadata_editor.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        
        this.configFile = configFile;
        this.metaData = new HashMap<String, String>();

        boolean fileValid = readConfigurationFile();


        this.labelFile = new JLabel("Configuration",
                                    SwingConstants.LEFT);
        this.textFieldFile = new JTextField(30);
        this.textFieldFile.setEditable(false);
        this.textFieldFile.setText(this.configFile.getAbsolutePath());

        this.labelTitle = new JLabel("Title",
                                     SwingConstants.LEFT);
        this.textFieldTitle = new JTextField(30);

        this.labelCreator = new JLabel("Creator",
                                       SwingConstants.LEFT);
        this.textFieldCreator = new JTextField(30);
        this.textFieldCreator.setToolTipText("Author, creator.");

        this.labelSubject = new JLabel("Subject",
                                       SwingConstants.LEFT);
        this.textFieldSubject = new JTextField(30);

        this.labelDescription = new JLabel("Description",
                                           SwingConstants.LEFT);
        this.textFieldDescription = new JTextField(30);

        this.labelPublisher = new JLabel("Publisher",
                                         SwingConstants.LEFT);
        this.textFieldPublisher = new JTextField(30);

        this.labelContributor = new JLabel("Contributor",
                                           SwingConstants.LEFT);
        this.textFieldContributor = new JTextField(30);
        this.textFieldContributor.setToolTipText("List of contributors, comma-separated.");

        this.labelIdentifier = new JLabel("Identifier", 
                                          SwingConstants.LEFT);
        this.textFieldIdentifier = new JTextField(30);
        this.textFieldIdentifier.setToolTipText("Worldwide unique identifier.");
        
        this.labelSource = new JLabel("Source",
                                      SwingConstants.LEFT);
        this.textFieldSource = new JTextField(30);
        
        this.labelLanguage = new JLabel("Language",
                                        SwingConstants.LEFT);
        this.textFieldLanguage = new JTextField(30);
        this.textFieldLanguage.setToolTipText("Language (abbreviation according to RFC 3066).");
        
        this.labelCoverage = new JLabel("Coverage",
                                        SwingConstants.LEFT);
        this.textFieldCoverage = new JTextField(30);
        
        this.labelRights = new JLabel("Rights",
                                      SwingConstants.LEFT);
        this.textFieldRights = new JTextField(30);
        this.textFieldRights.setToolTipText("Rights (copyright, trademarks).");

    
        if (this.metaData.containsKey("title") == true)
        {
            this.textFieldTitle.setText(this.metaData.get("title"));
        }
        
        if (this.metaData.containsKey("creator") == true)
        {
            this.textFieldCreator.setText(this.metaData.get("creator"));
        }
        
        if (this.metaData.containsKey("subject") == true)
        {
            this.textFieldSubject.setText(this.metaData.get("subject"));
        }
        
        if (this.metaData.containsKey("description") == true)
        {
            this.textFieldDescription.setText(this.metaData.get("description"));
        }
        
        if (this.metaData.containsKey("publisher") == true)
        {
            this.textFieldPublisher.setText(this.metaData.get("publisher"));
        }

        if (this.metaData.containsKey("contributor") == true)
        {
            String contributor = this.metaData.get("contributor");

            if (contributor.indexOf("<separator/>") > 0)
            {
                contributor = contributor.replace("<separator/>", ", ");
            }

            this.textFieldContributor.setText(contributor);
        }
      
        if (this.metaData.containsKey("identifier") == true)
        {
            this.textFieldIdentifier.setText(this.metaData.get("identifier"));
        }
        
        if (this.metaData.containsKey("source") == true)
        {
            this.textFieldSource.setText(this.metaData.get("source"));
        }
        
        if (this.metaData.containsKey("language") == true)
        {
            this.textFieldLanguage.setText(this.metaData.get("language"));
        }
        
        if (this.metaData.containsKey("coverage") == true)
        {
            this.textFieldCoverage.setText(this.metaData.get("coverage"));
        }
        
        if (this.metaData.containsKey("rights") == true)
        {
            this.textFieldRights.setText(this.metaData.get("rights"));
        }
        
        
        if (fileValid == true)
        {
            ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
	          this.labelFile.setIcon(iconCorrect);
        }
        else
        {
            ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
	          this.labelFile.setIcon(iconIncorrect);
        }
        
        CheckFields();

    
        JLabel[] labels = { this.labelFile,
                            this.labelTitle,
                            this.labelCreator,
                            this.labelSubject,
                            this.labelDescription,
                            this.labelPublisher,
                            this.labelContributor,
                            this.labelIdentifier,
                            this.labelSource,
                            this.labelLanguage,
                            this.labelCoverage,
                            this.labelRights };
        JTextField[] textFields = { this.textFieldFile,
                                    this.textFieldTitle,
                                    this.textFieldCreator,
                                    this.textFieldSubject,
                                    this.textFieldDescription,
                                    this.textFieldPublisher,
                                    this.textFieldContributor,
                                    this.textFieldIdentifier,
                                    this.textFieldSource,
                                    this.textFieldLanguage,
                                    this.textFieldCoverage,
                                    this.textFieldRights };

    
        JPanel panelMain = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
 
        panelMain.setLayout(gridbag);
  
        GridBagConstraints gridbagConstraints = new GridBagConstraints();
        gridbagConstraints.anchor = GridBagConstraints.EAST;
 
 
        int textFieldCount = textFields.length;
 
        for (int i = 0; i < textFieldCount; i++)
        {
            gridbagConstraints.gridwidth = GridBagConstraints.RELATIVE;
            gridbagConstraints.fill = GridBagConstraints.NONE;
            gridbagConstraints.weightx = 0.0;
            panelMain.add(labels[i], gridbagConstraints);
 
            gridbagConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridbagConstraints.weightx = 1.0;
            panelMain.add(textFields[i], gridbagConstraints);
        }
 
        panelMain.setBorder(BorderFactory.createEtchedBorder()); 
        getContentPane().add(panelMain, BorderLayout.NORTH); 


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
        getContentPane().add(panelButtons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event)
            {
                event.getWindow().setVisible(false);
                event.getWindow().dispose();
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent event)
    {
        String clickedButton = event.getActionCommand();
        
        if (clickedButton.equalsIgnoreCase("Exit") == true)
        {
            System.exit(2);
        }
        else if (clickedButton.equalsIgnoreCase("Check") == true)
        {
            CheckFields();
        }
        else if (clickedButton.equalsIgnoreCase("Save") == true)
        {
            if (CheckFields() == true)
            {
                writeConfigurationFile();
            }
        }
        else if (clickedButton.equalsIgnoreCase("About") == true)
        {
        
        }
        else
        {
        
        }
    }
    
    protected boolean CheckFields()
    {
        boolean valid = true;
    
        ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
        ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
        
        if (this.textFieldTitle.getText().length() > 0)
        {
            this.labelTitle.setIcon(iconCorrect);
        }
        else
        {
            this.labelTitle.setIcon(iconIncorrect);
            valid = false;
        }
        
        if (this.textFieldIdentifier.getText().length() > 0)
        {
            this.labelIdentifier.setIcon(iconCorrect);
        }
        else
        {
            this.labelIdentifier.setIcon(iconIncorrect);
            valid = false;
        }
        
        if (this.textFieldLanguage.getText().length() > 0)
        {
            this.labelLanguage.setIcon(iconCorrect);
        }
        else
        {
            this.labelLanguage.setIcon(iconIncorrect);
            valid = false;
        }
        
        return valid;
    }
  
    protected boolean readConfigurationFile()
    {
        /** @todo Replace with DOM. */
        
        boolean metaDataFound = false;
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.configFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext() == true)
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("metaData") == true)
                    {
                        metaDataFound = true;
                    
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();
                            
                            if (event.isStartElement() == true)
                            {
                                tagName = event.asStartElement().getName().getLocalPart();
                                event = eventReader.nextEvent();
                                
                                if (event.isCharacters() == true)
                                {
                                    if (this.metaData.containsKey(tagName) == true)
                                    {
                                        this.metaData.put(tagName, this.metaData.get(tagName) + "<separator/>" + event.asCharacters().getData());
                                    }
                                    else
                                    {
                                        this.metaData.put(tagName, event.asCharacters().getData());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }
        
        return metaDataFound;
    }
    
    protected boolean writeConfigurationFile()
    {
        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.parse(this.configFile);
	        document.getDocumentElement().normalize();

	        NodeList metaDataNodeList = document.getElementsByTagName("metaData");

	        if (metaDataNodeList.getLength() <= 0)
	        {
	            ImageIcon iconIncorrect = new ImageIcon(this.programPath + "incorrect.png");
	            this.labelFile.setIcon(iconIncorrect);
	            return false;
	        }


            Node metaDataNode = metaDataNodeList.item(0);
            Node parentNode = metaDataNode.getParentNode();
            parentNode.removeChild(metaDataNode);
            
            metaDataNode = document.createElement("metaData");
            
            Node nodeTitle = document.createElement("title");
            Text nodeTitleText = document.createTextNode(this.textFieldTitle.getText());
            nodeTitle.appendChild(nodeTitleText);
            metaDataNode.appendChild(nodeTitle);
            
            Node nodeCreator = document.createElement("creator");
            Text nodeCreatorText = document.createTextNode(this.textFieldCreator.getText());
            nodeCreator.appendChild(nodeCreatorText);
            metaDataNode.appendChild(nodeCreator);
            
            Node nodeSubject = document.createElement("subject");
            Text nodeSubjectText = document.createTextNode(this.textFieldSubject.getText());
            nodeSubject.appendChild(nodeSubjectText);
            metaDataNode.appendChild(nodeSubject);
            
            Node nodeDescription = document.createElement("description");
            Text nodeDescriptionText = document.createTextNode(this.textFieldDescription.getText());
            nodeDescription.appendChild(nodeDescriptionText);
            metaDataNode.appendChild(nodeDescription);
            
            Node nodePublisher = document.createElement("publisher");
            Text nodePublisherText = document.createTextNode(this.textFieldPublisher.getText());
            nodePublisher.appendChild(nodePublisherText);
            metaDataNode.appendChild(nodePublisher);
            
            {
                String contributorText = this.textFieldContributor.getText();
                
                if (contributorText.contains(",") == true)
                {
                    String[] contributors = contributorText.split(",");
                    
                    for (String contributor : contributors)
                    {
                        if (contributor.startsWith(" ") == true)
                        {
                            contributor = contributor.substring(1);
                        }
                        
                        if (contributor.length() > 0)
                        {
                            Node nodeContributor = document.createElement("contributor");
                            Text nodeContributorText = document.createTextNode(contributor);
                            nodeContributor.appendChild(nodeContributorText);
                            metaDataNode.appendChild(nodeContributor);
                        }
                    }
                }
            }
            
            Node nodeIdentifier = document.createElement("identifier");
            Text nodeIdentifierText = document.createTextNode(this.textFieldIdentifier.getText());
            nodeIdentifier.appendChild(nodeIdentifierText);
            metaDataNode.appendChild(nodeIdentifier);
            
            Node nodeSource = document.createElement("source");
            Text nodeSourceText = document.createTextNode(this.textFieldSource.getText());
            nodeSource.appendChild(nodeSourceText);
            metaDataNode.appendChild(nodeSource);

            Node nodeLanguage = document.createElement("language");
            Text nodeLanguageText = document.createTextNode(this.textFieldLanguage.getText());
            nodeLanguage.appendChild(nodeLanguageText);
            metaDataNode.appendChild(nodeLanguage);

            Node nodeCoverage = document.createElement("coverage");
            Text nodeCoverageText = document.createTextNode(this.textFieldCoverage.getText());
            nodeCoverage.appendChild(nodeCoverageText);
            metaDataNode.appendChild(nodeCoverage);

            Node nodeRights = document.createElement("rights");
            Text nodeRightsText = document.createTextNode(this.textFieldRights.getText());
            nodeRights.appendChild(nodeRightsText);
            metaDataNode.appendChild(nodeRights);

            parentNode.appendChild(metaDataNode);


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult streamResult =  new StreamResult(this.configFile);
            transformer.transform(source, streamResult);
            
            ImageIcon iconCorrect = new ImageIcon(this.programPath + "correct.png");
	          this.labelFile.setIcon(iconCorrect);
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
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-18);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-19);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-20);
        }
        
        return true;
    }
    
    
    protected File configFile;
    protected Map<String, String> metaData;
    
    private JLabel labelFile;
    private JTextField textFieldFile;
    
    private JLabel labelTitle;
    private JTextField textFieldTitle;
    
    private JLabel labelCreator;
    private JTextField textFieldCreator;
    
    private JLabel labelSubject;
    private JTextField textFieldSubject;
    
    private JLabel labelDescription;
    private JTextField textFieldDescription;

    private JLabel labelPublisher;
    private JTextField textFieldPublisher;

    private JLabel labelContributor;
    private JTextField textFieldContributor;

    private JLabel labelIdentifier;
    private JTextField textFieldIdentifier;
        
    private JLabel labelSource;
    private JTextField textFieldSource;
        
    private JLabel labelLanguage;
    private JTextField textFieldLanguage;
        
    private JLabel labelCoverage;
    private JTextField textFieldCoverage;
        
    private JLabel labelRights;
    private JTextField textFieldRights;
    
    protected String programPath;
}
