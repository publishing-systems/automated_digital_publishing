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
 * @file $/workflows/html2epub1_config_merge1.java
 * @brief Merges file settings with metadata settings of two html2epub1
 *     configuration files.
 * @author Stephan Kreutzer
 * @since 2014-06-07
 */



import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;



public class html2epub1_config_merge1
{
    public static void main(String args[])
    {
        System.out.print("html2epub1_config_merge1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\thtml2epub1_config_merge1 file-config metadata-config merged-config\n\n");

            System.exit(1);
        }


        File configFileLhs = new File(args[0]);
        
        if (configFileLhs.exists() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileLhs.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (configFileLhs.isFile() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileLhs.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (configFileLhs.canRead() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileLhs.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }

        File configFileRhs = new File(args[1]);
        
        if (configFileRhs.exists() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileRhs.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (configFileRhs.isFile() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileRhs.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (configFileRhs.canRead() != true)
        {
            System.out.print("html2epub1_config_merge1: '" + configFileRhs.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }

        File configFileOut = new File(args[2]);

        if (configFileLhs.getAbsolutePath().equalsIgnoreCase(configFileOut.getAbsolutePath()) != true)
        {
            if (html2epub1_config_merge1.CopyFile(configFileLhs, configFileOut) != 0)
            {
                System.exit(-7);
            }
        }


        Map<String, String> metaData = new HashMap<String, String>();

        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.parse(configFileRhs);
	        document.getDocumentElement().normalize();
             
	        NodeList metaDataNodeList = document.getElementsByTagName("metaData");
	          
	        if (metaDataNodeList.getLength() > 0)
	        {
	            NodeList metaDataSettings = metaDataNodeList.item(0).getChildNodes();
	            int metaDataSettingsCount = metaDataSettings.getLength();
	              
	            for (int i = 0; i < metaDataSettingsCount; i++)
	            {
	                if (metaDataSettings.item(i).getNodeType() != Node.ELEMENT_NODE)
	                {
	                    continue;
	                }

	                Node metaDataNode = metaDataSettings.item(i);
                    String tagName = metaDataNode.getNodeName();

                    if (metaData.containsKey(tagName) != true)
                    {
                        metaData.put(tagName, metaDataNode.getTextContent());
                    }
                    else
                    {
                        if (tagName == "contributor")
                        {
                            metaData.put(tagName, metaData.get(tagName) + "<separator/>" + metaDataNode.getTextContent());
                        }
                        else
                        {
                            System.out.println("html2epub1_config_merge1: '" + configFileRhs.getAbsolutePath() + "' contains meta data setting '" + tagName + "' twice.");
                            System.exit(-8);
                        }
                    }
	            } 
	        }
	    }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-9);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-10);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-11);
        }


        try
        {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.parse(configFileLhs);
	        document.getDocumentElement().normalize();
             
	        NodeList metaDataNodeList = document.getElementsByTagName("metaData");
	         
	        if (metaDataNodeList.getLength() > 0)
	        {
                Node metaDataNode = metaDataNodeList.item(0);
                Node parentNode = metaDataNode.getParentNode();
                parentNode.removeChild(metaDataNode);

                metaDataNode = document.createElement("metaData");
                
                for (Map.Entry<String, String> entry : metaData.entrySet())
                {
                    if (entry.getKey() != "contributor")
                    {
                        Node node = document.createElement(entry.getKey());
                        Text nodeText = document.createTextNode(entry.getValue());
                        node.appendChild(nodeText);
                        metaDataNode.appendChild(node);
                    }
                    else
                    {
                        String contributor = entry.getValue();

                        if (contributor.indexOf("<separator/>") > 0)
                        {
                            String[] contributors = contributor.split("<separator/>");
                            
                            for (int j = 0; j < contributors.length; j++)
                            {
                                Node node = document.createElement("contributor");
                                Text nodeText = document.createTextNode(contributors[j]);
                                node.appendChild(nodeText);
                                metaDataNode.appendChild(node);
                            }
                        }
                        else
                        {
                            Node node = document.createElement(entry.getKey());
                            Text nodeText = document.createTextNode(entry.getValue());
                            node.appendChild(nodeText);
                            metaDataNode.appendChild(node);
                        }
                    }
                }
                
                parentNode.appendChild(metaDataNode);
            
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource source = new DOMSource(document);
                StreamResult streamResult =  new StreamResult(configFileOut);
                transformer.transform(source, streamResult);
            }
        }
        catch (ParserConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-12);
        }
        catch (SAXException ex)
        {
            ex.printStackTrace();
            System.exit(-13);
        }
        catch (TransformerConfigurationException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }
        catch (TransformerException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }

        System.exit(0);
    }
    
    public static int CopyFile (File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("html2epub1_config_merge1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("html2epub1_config_merge1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("html2epub1_config_merge1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
            System.exit(-18);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-19);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-20);
        }
    
        return 0;
    }
}
