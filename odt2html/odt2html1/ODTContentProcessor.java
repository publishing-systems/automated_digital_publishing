/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of odt2html1.
 *
 * odt2html1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * odt2html1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with odt2html1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/ODTContentProcessor.java
 * @brief Processor to operate on the 'content.xml' file of the unpacked
 *     ODT file.
 * @author Stephan Kreutzer
 * @since 2014-04-20
 */



import java.io.File;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.events.Attribute;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



class ODTContentProcessor
{
    public ODTContentProcessor(File contentFile, Map<String, String> styleMappings)
    {
        this.contentFile = contentFile;
        this.styleMappings = styleMappings;
        
        this.contentInfo = new HashMap<String, String>();
        this.automaticStyles = new HashMap<String, String>();
        this.listStyles = new HashMap<String, ArrayList<Integer>>();
    }
    
    public int Analyze()
    {
        this.contentInfo.clear();
        this.automaticStyles.clear();
        this.listStyles.clear();
    
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.contentFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;
            
            boolean automaticStyles = false;

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:document-content") == true)
                    {
                        Attribute attributeVersion = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_OFFICE_NAMESPACE_URI, "version", "office"));
                                
                        if (attributeVersion != null)
                        {
                            String contentVersion = attributeVersion.getValue();
                            
                            if (this.contentInfo.containsKey("contentVersion") != true)
                            {
                                this.contentInfo.put("contentVersion", contentVersion);
                            }
                            else
                            {
                                System.out.println("odt2html1: ODT content version specified more than once.");
                            }
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("office:automatic-styles") == true)
                    {
                        automaticStyles = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("style:style") == true &&
                             automaticStyles == true)
                    {
                        Attribute attributeName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_STYLE_NAMESPACE_URI, "name", "style"));
                        Attribute attributeParentStyleName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_STYLE_NAMESPACE_URI, "parent-style-name", "style"));
                                
                        if (attributeName != null &&
                            attributeParentStyleName != null)
                        {
                            String name = attributeName.getValue();
                            String parentStyleName = attributeParentStyleName.getValue();
                            
                            if (this.automaticStyles.containsKey(name) != true)
                            {
                                if (this.styleMappings.containsKey(parentStyleName) == true)
                                {
                                    this.automaticStyles.put(name, parentStyleName);
                                }
                                else
                                {
                                    System.out.println("odt2html1: Automatic style '" + name + "' relies on internal style '" + parentStyleName + "', which isn't specified in the ODT style file.");
                                }
                            }
                            else
                            {
                                System.out.println("odt2html1: Automatic style '" + name + "' in ODT content file specified more than once.");
                            }
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("text:list-style") == true &&
                             automaticStyles == true)
                    {
                        Attribute attributeName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_STYLE_NAMESPACE_URI, "name", "style"));
                                
                        if (attributeName != null)
                        {
                            String name = attributeName.getValue();

                            if (this.listStyles.containsKey(name) != true)
                            {
                                ArrayList<Integer> listLevelTypes = new ArrayList<Integer>(10);
                                
                                for (int i = 0; i < 10; i++)
                                {
                                    listLevelTypes.add(new Integer(0));
                                }

                                while (eventReader.hasNext() == true)
                                {
                                    event = eventReader.nextEvent();

                                    if (event.isStartElement() == true)
                                    {
                                        elementName = event.asStartElement().getName();
                                        fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                                        
                                        if (fullElementName.equalsIgnoreCase("text:list-level-style-bullet") == true)
                                        {
                                            Attribute attributeLevel = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_TEXT_NAMESPACE_URI, "level", "text"));
                                                    
                                            if (attributeLevel != null)
                                            {
                                                Integer level = Integer.valueOf(attributeLevel.getValue());
                                                
                                                if (level >= 1 && level <= 10)
                                                {
                                                    if (listLevelTypes.get(level - 1) == 0)
                                                    {
                                                        // 1: Bulleted list.
                                                        listLevelTypes.set(level - 1, 1);
                                                    }
                                                    else
                                                    {
                                                        System.out.println("odt2html1: List style '" + name + "' specifies level '" + level + "' more than once.");
                                                    }
                                                }
                                                else
                                                {
                                                    System.out.println("odt2html1: List style '" + name + "' specifies an invalid level '" + level + "'.");
                                                }
                                            }
                                            else
                                            {
                                                System.out.println("odt2html1: List style '" + name + "' level definition is missing its level number.");
                                            }
                                        }
                                        else if (fullElementName.equalsIgnoreCase("text:list-level-style-number") == true)
                                        {
                                            Attribute attributeLevel = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_TEXT_NAMESPACE_URI, "level", "text"));
                                                    
                                            if (attributeLevel != null)
                                            {
                                                int level = Integer.parseInt(attributeLevel.getValue());
                                                
                                                if (level >= 1 && level <= 10)
                                                {
                                                    if (listLevelTypes.get(level - 1) == 0)
                                                    {
                                                        // 2: Numbered list.
                                                        listLevelTypes.set(level - 1, 2);
                                                    }
                                                    else
                                                    {
                                                        System.out.println("odt2html1: List style '" + name + "' specifies level '" + level + "' more than once.");
                                                    }
                                                }
                                                else
                                                {
                                                    System.out.println("odt2html1: List style '" + name + "' specifies an invalid level '" + level + "'.");
                                                }
                                            }
                                            else
                                            {
                                                System.out.println("odt2html1: List style '" + name + "' level definition is missing its level number.");
                                            }
                                        }
                                    }
                                    else if (event.isEndElement() == true)
                                    {
                                        elementName = event.asEndElement().getName();
                                        fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                                        
                                        if (fullElementName.equalsIgnoreCase("text:list-style") == true)
                                        {
                                            for (int i = 0; i < 10; i++)
                                            {
                                                if (listLevelTypes.get(i) == 0)
                                                {
                                                    System.out.println("odt2html1: List style '" + name + "' is missing a definition for level " + i + ".");
                                                    return -1;
                                                }
                                            }
                                            
                                            if (this.listStyles.containsKey(name) != true)
                                            {
                                                this.listStyles.put(name, listLevelTypes);
                                            }
                                            else
                                            {
                                                System.out.println("odt2html1: List style '" + name + "' specified more than once.");
                                            }
             
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                System.out.println("odt2html1: List style '" + name + "' in ODT content file specified more than once.");
                            }
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:automatic-styles") == true)
                    {
                        automaticStyles = false;
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return -1;
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            return -2;
        }
        
        
        
        return 0;
    }
    
    public int Run(File xhtmlOutFile)
    {
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.contentFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in, "UTF8");

            XMLEvent event = null;

            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(xhtmlOutFile.getAbsolutePath()),
                                    "UTF8"));


            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");
            writer.write("<!-- This file was created by odt2html1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n");
            /** @todo: Specify language of the content with lang-attribute. */
            writer.write("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
            writer.write("  <head>\n");
            /** @todo: Provide title of the document. */
            writer.write("    <title></title>\n");
            writer.write("    <meta http-equiv=\"content-type\" content=\"application/xhtml+xml; charset=UTF-8\"/>\n");
            writer.write("  </head>\n");
            writer.write("  <body>\n");


            boolean body = false;
            boolean text = false;
            boolean paragraph = false;
            boolean list = false;
            Integer listLevel = 0;
            String listStyle = "";
            boolean span = false;
            boolean link = false;
            

            while (eventReader.hasNext() == true)
            {
                event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    QName elementName = event.asStartElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:body") == true)
                    {
                        body = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("office:text") == true &&
                             body == true)
                    {
                        text = true;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:p") == true &&
                             body == true &&
                             text == true)
                    {
                        paragraph = true;
                        
                        writer.write("<p");
                        
                        Attribute attributeStyleName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_TEXT_NAMESPACE_URI, "style-name", "text"));
                                
                        if (attributeStyleName != null)
                        {
                            String styleName = attributeStyleName.getValue();
                            
                            if (this.automaticStyles.containsKey(styleName) == true)
                            {
                                String internalStyle = this.automaticStyles.get(styleName);
                                String displayStyle = this.styleMappings.get(internalStyle);

                                writer.write(" class=\"" + internalStyle + "\"");
                                //writer.write(" class=\"" + displayStyle + "\"");
                            }
                            else if (this.styleMappings.containsKey(styleName) == true)
                            {
                                String displayStyle = this.styleMappings.get(styleName);

                                writer.write(" class=\"" + styleName + "\"");
                                //writer.write(" class=\"" + displayStyle + "\"");
                            }
                            else
                            {
                                System.out.println("odt2html1: Automatic style '" + styleName + "' is used in the ODT content file, but isn't specified or mapped to the internal style name.");
                            }
                        }
                        
                        writer.write(">");
                    }
                    else if (fullElementName.equalsIgnoreCase("text:list") == true &&
                             body == true &&
                             text == true)
                    {
                        list = true;
                        
                        listLevel += 1;
                        
                        if (listLevel == 1)
                        {
                            Attribute attributeListStyleName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_TEXT_NAMESPACE_URI, "style-name", "text"));
                                    
                            if (attributeListStyleName != null)
                            {
                                String listStyleName = attributeListStyleName.getValue();
                                
                                if (this.listStyles.containsKey(listStyleName) == true)
                                {
                                    listStyle = listStyleName;
                                }
                                else
                                {
                                    System.out.println("odt2html1: List style '" + listStyleName + "' is used in the ODT content file, but list style isn't defined.");
                                    listStyle = "";
                                }
                            }
                        }
                        
                        if (listLevel >= 1 &&
                            listStyle.isEmpty() != true)
                        {
                            if (this.listStyles.get(listStyle).get(listLevel - 1) == 1)
                            {
                                writer.write("<ul>");
                            }
                            else if (this.listStyles.get(listStyle).get(listLevel - 1) == 2)
                            {
                                writer.write("<ol>");
                            }
                            else
                            {
                                System.out.println("odt2html1: List style '" + listStyle + "' is missing list type information for level " + listLevel + ".");
                            }
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("text:list-item") == true &&
                             body == true &&
                             text == true &&
                             list == true)
                    {
                        writer.write("<li>");
                    }
                    else if (fullElementName.equalsIgnoreCase("text:span") == true &&
                             body == true &&
                             text == true &&
                             paragraph == true)
                    {
                        span = true;
                        
                        writer.write("<span");
                        
                        Attribute attributeStyleName = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_TEXT_NAMESPACE_URI, "style-name", "text"));
                                
                        if (attributeStyleName != null)
                        {
                            String styleName = attributeStyleName.getValue();
                            
                            if (this.automaticStyles.containsKey(styleName) == true)
                            {
                                String internalStyle = this.automaticStyles.get(styleName);
                                String displayStyle = this.styleMappings.get(internalStyle);

                                writer.write(" class=\"" + internalStyle + "\"");
                                //writer.write(" class=\"" + displayStyle + "\"");
                            }
                            else if (this.styleMappings.containsKey(styleName) == true)
                            {
                                String displayStyle = this.styleMappings.get(styleName);

                                writer.write(" class=\"" + styleName + "\"");
                                //writer.write(" class=\"" + displayStyle + "\"");
                            }
                            else
                            {
                                System.out.println("odt2html1: Automatic style '" + styleName + "' is used in the ODT content file, but isn't specified or mapped to the internal style name.");
                            }
                        }
                        
                        writer.write(">");
                    }
                    else if (fullElementName.equalsIgnoreCase("text:a") == true &&
                             body == true &&
                             text == true &&
                             paragraph == true)
                    {
                        Attribute attributeType = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_XLINK_NAMESPACE_URL, "type", "xlink"));
                        
                        if (attributeType != null)
                        {
                            if (attributeType.getValue().equalsIgnoreCase("simple") == true)
                            {
                                Attribute attributeHref = event.asStartElement().getAttributeByName(new QName(ODT_CONTENT_XLINK_NAMESPACE_URL, "href", "xlink"));
                                
                                if (attributeHref != null)
                                {
                                    link = true;
                                    
                                    writer.write("<a href=\"" + attributeHref.getValue() + "\">");
                                }
                            }
                        }
                    }
                }
                else if (event.isEndElement() == true)
                {
                    QName elementName = event.asEndElement().getName();
                    String fullElementName = elementName.getPrefix() + ":" + elementName.getLocalPart();
                    
                    if (fullElementName.equalsIgnoreCase("office:body") == true)
                    {
                        span = false;
                        paragraph = false;
                        list = false;
                        listLevel = 0;
                        listStyle = "";
                        text = false;
                        body = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("office:text") == true &&
                             body == true)
                    {
                        span = false;
                        paragraph = false;
                        listLevel = 0;
                        listStyle = "";
                        list = false;
                        text = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:p") == true &&
                             body == true &&
                             text == true)
                    {
                        writer.write("</p>");

                        link = false;
                        span = false;
                        paragraph = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:list") == true &&
                             body == true &&
                             text == true)
                    {
                        if (listLevel >= 1 &&
                            listStyle.isEmpty() != true)
                        {
                            if (this.listStyles.get(listStyle).get(listLevel - 1) == 1)
                            {
                                writer.write("</ul>");
                            }
                            else if (this.listStyles.get(listStyle).get(listLevel - 1) == 2)
                            {
                                writer.write("</ol>");
                            }
                            else
                            {
                                System.out.println("odt2html1: List style '" + listStyle + "' is missing list type information for level " + listLevel + ".");
                            }
                        }

                        link = false;
                        span = false;
                        
                        listLevel -= 1;
                        
                        if (listLevel <= 0)
                        {
                            listLevel = 0;
                            listStyle = "";
                            list = false;
                        }
                    }
                    else if (fullElementName.equalsIgnoreCase("text:list-item") == true &&
                             body == true &&
                             text == true &&
                             list == true)
                    {
                        writer.write("</li>");
                        
                        link = false;
                        span = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:span") == true &&
                             body == true &&
                             text == true &&
                             (paragraph == true) &&
                              span == true)
                    {
                        writer.write("</span>");
                        
                        span = false;
                    }
                    else if (fullElementName.equalsIgnoreCase("text:a") == true &&
                             body == true &&
                             text == true &&
                             (paragraph == true) &&
                             link == true)
                    {
                        writer.write("</a>");
                        
                        link = false;
                    }
                }
                else if (event.isCharacters() == true)
                {
                    if (paragraph == true ||
                        span == true)
                    {
                        event.writeAsEncodedUnicode(writer);
                    }
                } 
            }
            
            writer.write("\n");
            writer.write("  </body>\n");
            writer.write("</html>\n");
            
            writer.flush();
            writer.close();
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            return -1;
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            return -2;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            return -3;
        }
        
        return 0;
    }
    
    public Map<String, String> GetContentInfo()
    {
        return this.contentInfo;
    }

    static final String ODT_CONTENT_OFFICE_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:office:1.0";
    static final String ODT_CONTENT_STYLE_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:style:1.0";
    static final String ODT_CONTENT_TEXT_NAMESPACE_URI = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
    static final String ODT_CONTENT_XLINK_NAMESPACE_URL = "http://www.w3.org/1999/xlink";

    private File contentFile;
    private Map<String, String> contentInfo;
    private Map<String, String> styleMappings;
    private Map<String, String> automaticStyles;
    private Map<String, ArrayList<Integer>> listStyles;
}
