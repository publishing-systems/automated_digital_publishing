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
 * @file $/JobFileProcessor.java
 * @brief Processor to read the job file.
 * @author Stephan Kreutzer
 * @since 2014-08-19
 */



import java.io.File;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.Attribute;
import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;



class JobFileProcessor
{
    public JobFileProcessor(File jobFile)
    {
        this.jobFile = jobFile;
        this.jobSettings = new HashMap<String, String>();
        this.customFields = new HashMap<String, String>();
        this.taxonomyHierarchy = new HashMap<String, ArrayList<String>>();
        this.taxonomyTags = new HashMap<String, ArrayList<String>>();
    }

    public int Run()
    {
        this.jobSettings.clear();
        this.customFields.clear();
        this.taxonomyHierarchy.clear();
        this.taxonomyTags.clear();


        if (this.jobFile.exists() != true)
        {
            System.out.print("html2wordpress1: Job file '" + this.jobFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (this.jobFile.isFile() != true)
        {
            System.out.print("html2wordpress1: Job file '" + this.jobFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-1);
        }

        if (this.jobFile.canRead() != true)
        {
            System.out.print("html2wordpress1: Job file '" + this.jobFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-1);
        }


        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(this.jobFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext() == true)
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    String tagName = event.asStartElement().getName().getLocalPart();

                    if (tagName.equalsIgnoreCase("input-html-file") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("input-html-file") != true)
                            {
                                this.jobSettings.put("input-html-file", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: Input file already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-xmlrpc-url") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-xmlrpc-url") != true)
                            {
                                this.jobSettings.put("wordpress-xmlrpc-url", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress XML-RPC URL already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-user-public-key") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-user-public-key") != true)
                            {
                                this.jobSettings.put("wordpress-user-public-key", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress user authentication public key already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-user-private-key") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-user-private-key") != true)
                            {
                                this.jobSettings.put("wordpress-user-private-key", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress user authentication private key already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-user-id") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-user-id") != true)
                            {
                                this.jobSettings.put("wordpress-post-user-id", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post user ID already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-type") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-type") != true)
                            {
                                this.jobSettings.put("wordpress-post-type", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post type already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-status") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-status") != true)
                            {
                                this.jobSettings.put("wordpress-post-status", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post status already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-title") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-title") != true)
                            {
                                this.jobSettings.put("wordpress-post-title", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post title already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-excerpt") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-excerpt") != true)
                            {
                                this.jobSettings.put("wordpress-post-excerpt", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post excerpt already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-date") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-date") != true)
                            {
                                this.jobSettings.put("wordpress-post-date", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post date already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-format") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-format") != true)
                            {
                                this.jobSettings.put("wordpress-post-format", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post format already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-name-slug") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-name-slug") != true)
                            {
                                this.jobSettings.put("wordpress-post-name-slug", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post name slug already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-comment-default-status") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-comment-default-status") != true)
                            {
                                this.jobSettings.put("wordpress-post-comment-default-status", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post comment default status already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-ping-default-status") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-ping-default-status") != true)
                            {
                                this.jobSettings.put("wordpress-post-ping-default-status", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post ping default status already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-sticky") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-sticky") != true)
                            {
                                this.jobSettings.put("wordpress-post-sticky", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post sticky flag already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-thumbnail-id") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-thumbnail-id") != true)
                            {
                                this.jobSettings.put("wordpress-post-thumbnail-id", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post thumbnail ID already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-parent-id") == true)
                    {
                        event = eventReader.nextEvent();
                        
                        if (event.isCharacters() == true)
                        {
                            if (this.jobSettings.containsKey("wordpress-post-parent-id") != true)
                            {
                                this.jobSettings.put("wordpress-post-parent-id", event.asCharacters().getData());
                            }
                            else
                            {
                                System.out.println("html2wordpress1: WordPress post parent ID already specified.");
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-custom-fields") == true)
                    {
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();

                            if (event.isStartElement() == true)
                            {
                                tagName = event.asStartElement().getName().getLocalPart();

                                if (tagName.equalsIgnoreCase("wordpress-post-custom-field") == true)
                                {
                                    Attribute name = event.asStartElement().getAttributeByName(new QName("name"));

                                    if (name == null)
                                    {
                                        System.out.print("html2wordpress1: WordPress post custom field (metadata field) is missing its name.\n");
                                        continue;
                                    }

                                    event = eventReader.nextEvent();

                                    if (event.isCharacters() == true)
                                    {
                                        if (this.customFields.containsKey(name.getValue()) != true)
                                        {
                                            this.customFields.put(name.getValue(), event.asCharacters().getData());
                                        }
                                        else
                                        {
                                            System.out.println("html2wordpress1: WordPress post custom field (metadata field) '" + name.getValue() + "' already specified.");
                                        }
                                    }
                                }
                            }
                            else if (event.isEndElement() == true)
                            {
                                tagName = event.asEndElement().getName().getLocalPart();
                                
                                if (tagName.equalsIgnoreCase("wordpress-post-custom-fields") == true)
                                {
                                    break;
                                }
                            }
                        }
                    }
                    else if (tagName.equalsIgnoreCase("wordpress-post-taxonomies") == true)
                    {
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();

                            if (event.isStartElement() == true)
                            {
                                tagName = event.asStartElement().getName().getLocalPart();

                                if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-hierarchy") == true)
                                {
                                    Attribute name = event.asStartElement().getAttributeByName(new QName("name"));

                                    if (name == null)
                                    {
                                        System.out.print("html2wordpress1: WordPress post taxonomy (hierarchical) is missing its name.\n");
                                        continue;
                                    }

                                    ArrayList<String> terms = new ArrayList<String>();
                                    
                                    while (eventReader.hasNext() == true)
                                    {
                                        event = eventReader.nextEvent();

                                        if (event.isStartElement() == true)
                                        {
                                            tagName = event.asStartElement().getName().getLocalPart();

                                            if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-id") == true)
                                            {
                                                event = eventReader.nextEvent();

                                                if (event.isCharacters() == true)
                                                {
                                                    terms.add(event.asCharacters().getData());
                                                }
                                            }
                                        }
                                        if (event.isEndElement() == true)
                                        {
                                            tagName = event.asEndElement().getName().getLocalPart();
                                            
                                            if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-hierarchy") == true)
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    
                                    if (terms.isEmpty() != true)
                                    {
                                        if (this.taxonomyHierarchy.containsKey(name.getValue()) == true)
                                        {
                                            this.taxonomyHierarchy.get(name.getValue()).addAll(terms);
                                        }
                                        else
                                        {
                                            this.taxonomyHierarchy.put(name.getValue(), terms);
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("html2wordpress1: No terms for hierarchical taxonomy '" + name.getValue() + "' specified.");
                                    }
                                }
                                else if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-tags") == true)
                                {
                                    Attribute name = event.asStartElement().getAttributeByName(new QName("name"));

                                    if (name == null)
                                    {
                                        System.out.print("html2wordpress1: WordPress post taxonomy (tags) is missing its name.\n");
                                        continue;
                                    }

                                    ArrayList<String> terms = new ArrayList<String>();
                                    
                                    while (eventReader.hasNext() == true)
                                    {
                                        event = eventReader.nextEvent();

                                        if (event.isStartElement() == true)
                                        {
                                            tagName = event.asStartElement().getName().getLocalPart();

                                            if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-name") == true)
                                            {
                                                event = eventReader.nextEvent();

                                                if (event.isCharacters() == true)
                                                {
                                                    terms.add(event.asCharacters().getData());
                                                }
                                            }
                                        }
                                        if (event.isEndElement() == true)
                                        {
                                            tagName = event.asEndElement().getName().getLocalPart();
                                            
                                            if (tagName.equalsIgnoreCase("wordpress-post-taxonomy-tags") == true)
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    
                                    if (terms.isEmpty() != true)
                                    {
                                        if (this.taxonomyTags.containsKey(name.getValue()) == true)
                                        {
                                            this.taxonomyTags.get(name.getValue()).addAll(terms);
                                        }
                                        else
                                        {
                                            this.taxonomyTags.put(name.getValue(), terms);
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("html2wordpress1: No terms for tag taxonomy '" + name.getValue() + "' specified.");
                                    }
                                }  
                            }
                            else if (event.isEndElement() == true)
                            {
                                tagName = event.asEndElement().getName().getLocalPart();
                                
                                if (tagName.equalsIgnoreCase("wordpress-post-taxonomies") == true)
                                {
                                    break;
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
            System.exit(-1);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-1);
        }

        return 0;
    }

    
    public Map<String, String> GetJobSettings()
    {
        return this.jobSettings;
    }
    
    public Map<String, String> GetCustomFields()
    {
        return this.customFields;
    }
    
    public Map<String, ArrayList<String>> GetTaxonomyHierarchy()
    {
        return this.taxonomyHierarchy;
    }
    
    public Map<String, ArrayList<String>> GetTaxonomyTags()
    {
        return this.taxonomyTags;
    }

    private File jobFile;
    private Map<String, String> jobSettings;
    private Map<String, String> customFields;
    private Map<String, ArrayList<String>> taxonomyHierarchy;
    private Map<String, ArrayList<String>> taxonomyTags;
}
