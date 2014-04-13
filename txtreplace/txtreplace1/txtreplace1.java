/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of txtreplace1.
 *
 * txtreplace1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * txtreplace1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; replacementout even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along replacement txtreplace1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/txtreplace1.java
 * @brief Replacements in text files (save memory, spend time).
 * @author Stephan Kreutzer
 * @since 2014-04-12
 */



import java.io.File;
import javax.xml.stream.XMLInputFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.namespace.QName;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLStreamException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;



public class txtreplace1
{
    public static void main(String args[])
    {
        System.out.print("txtreplace1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes replacement ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");

        if (args.length < 3)
        {
            System.out.print("Usage:\n" +
                             "\ttxtreplace1 in-file replacement-dictionary-file out-file\n\n");

            System.exit(1);
        }


        File replacementDictionaryFile = new File(args[1]);

        if (replacementDictionaryFile.exists() != true)
        {
            System.out.print("txtreplace1: Replacement dictionary '" + replacementDictionaryFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-1);
        }

        if (replacementDictionaryFile.isFile() != true)
        {
            System.out.print("txtreplace1: Replacement dictionary '" + replacementDictionaryFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-2);
        }

        if (replacementDictionaryFile.canRead() != true)
        {
            System.out.print("txtreplace1: Replacement dictionary '" + replacementDictionaryFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-3);
        }

        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.print("txtreplace1: '" + inFile.getAbsolutePath() + "' doesn't exist.\n");
            System.exit(-4);
        }

        if (inFile.isFile() != true)
        {
            System.out.print("txtreplace1: '" + inFile.getAbsolutePath() + "' isn't a file.\n");
            System.exit(-5);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("txtreplace1: '" + inFile.getAbsolutePath() + "' isn't readable.\n");
            System.exit(-6);
        }

        
        char[] buffer = new char[1024];
        
        File sourceFile = new File(args[2] + ".1");
        File destinationFile = new File(args[2] + ".2");
        
        try
        {
            sourceFile.createNewFile();
            destinationFile.createNewFile();


            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(inFile),
                                    "UTF8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(sourceFile),
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
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-7);
        }


        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(replacementDictionaryFile);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext() == true)
            {
                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement() == true)
                {
                    if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase("replace") == true)
                    {
                        String pattern = new String();
                        String replacement = new String();
                    
                        while (eventReader.hasNext() == true)
                        {
                            event = eventReader.nextEvent();
                        
                            if (event.isStartElement() == true)
                            {
                                if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase("pattern") == true)
                                {
                                    if (eventReader.hasNext() == true)
                                    {
                                        event = eventReader.nextEvent();
                                        
                                        if (event.isCharacters() == true)
                                        {
                                            pattern = event.asCharacters().getData();
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("txtreplace1: Pattern definition in replacement dictionary file '" + replacementDictionaryFile.getAbsolutePath() + "' seems to be incomplete.");
                                        System.exit(-8);
                                    }
                                }
                                else if (event.asStartElement().getName().getLocalPart().equalsIgnoreCase("replacement") == true)
                                {
                                    if (eventReader.hasNext() == true)
                                    {
                                        event = eventReader.nextEvent();
                                        
                                        if (event.isCharacters() == true)
                                        {
                                            replacement = event.asCharacters().getData();
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("txtreplace1: Replacement definition in replacement dictionary file '" + replacementDictionaryFile.getAbsolutePath() + "' seems to be incomplete.");
                                        System.exit(-9);
                                    }
                                }
                            }
                            else if (event.isEndElement() == true)
                            {
                                if (event.asEndElement().getName().getLocalPart().equalsIgnoreCase("replace") == true)
                                {
                                    break;
                                }
                            }
                        }
                    

                        int patternLength = pattern.length();
                        int replacementLength = replacement.length();
                        
                        if (patternLength <= 0)
                        {
                            System.out.println("txtreplace1: Pattern in replacement dictionary file '" + replacementDictionaryFile.getAbsolutePath() + "' seems to be incomplete.");
                            System.exit(-10);
                        }
                        
                        if (patternLength <= 0)
                        {
                            System.out.println("txtreplace1: Replacement in replacement dictionary file '" + replacementDictionaryFile.getAbsolutePath() + "' seems to be incomplete.");
                            System.exit(-11);
                        }
                        
                        
                        int matchCount = 0;
                        boolean found = false;
                        int bufferOverlapCount = 0;
                        
                        if (patternLength > buffer.length)
                        {
                            System.out.println("txtreplace1: Replacement pattern has a length of " + patternLength + " characters, maximum is " + buffer.length + ".");
                        
                            if (sourceFile.delete() != true)
                            {
                                System.out.println("txtreplace1: Can't delete temporary file '" + sourceFile.getAbsolutePath() + "'.");
                            }
                            
                            if (destinationFile.delete() != true)
                            {
                                System.out.println("txtreplace1: Can't delete temporary file '" + destinationFile.getAbsolutePath() + "'.");
                            }
                            
                            System.exit(-12);
                        }

                        BufferedReader reader = new BufferedReader(
                                                new InputStreamReader(
                                                new FileInputStream(sourceFile),
                                                "UTF8"));
                        BufferedWriter writer = new BufferedWriter(
                                                new OutputStreamWriter(
                                                new FileOutputStream(destinationFile),
                                                "UTF8"));
                        int charactersRead = reader.read(buffer, 0, buffer.length);

                        while (charactersRead > 0)
                        {
                            for (int i = 0; i < charactersRead; i++)
                            {
                                boolean matched = false;
                            
                                if (i >= 0)
                                {
                                    if (buffer[i] == pattern.charAt(matchCount))
                                    {
                                        matched = true;
                                    }
                                    
                                    bufferOverlapCount = 0;
                                }
                                else
                                {
                                    int offset = bufferOverlapCount - (i * -1);
                                
                                    // If i is negative, it points always to the
                                    // second character of oldValue in the now
                                    // hypothetic previous buffer, so it is save
                                    // to assume that all characters until i >= 0
                                    // are the same than ... (otherwise it wouldn't
                                    // have matched in the first place).
                                
                                    if (pattern.charAt(offset) == pattern.charAt(matchCount))
                                    {
                                        matched = true;
                                    }
                                }
                            
                                if (matched == true)
                                {
                                    matchCount++;
                                }
                                else
                                {
                                    if (matchCount > 0)
                                    {
                                        i = (i - 1) - (matchCount - 1);
                                        
                                        if (i < 0)
                                        {
                                            // Matching occurred between two buffers, and the
                                            // previous buffer is already gone. However, since
                                            // matching was successful up to the current position,
                                            // it is save to assume that the characters in the 
                                            // previous buffer were the same than pattern (otherwise
                                            // no matching would have occurred). In order to use
                                            // pattern as the now hypothetical previous buffer,
                                            // it is necessary to keep the information about where
                                            // the portion of pattern begins that needs to be reused.

                                            // (i * -1) is the number of characters that matched at the
                                            // previous attempt. The next loop iteration will increment i
                                            // in order to skip the first character of pattern in the now
                                            // hypothetical previous buffer, which failed to match over the
                                            // entire length of pattern. bufferOverlapCount therefore
                                            // specifies the number of characters of pattern the now
                                            // hypothetical buffer, which i needs to check towards i >= 0,
                                            // starting from the second character.
                                            
                                            bufferOverlapCount = (i * -1);
                                        }

                                        writer.write(pattern, 0, 1);

                                        matchCount = 0;

                                        continue;
                                    }
                                    else
                                    {
                                        if (i >= 0)
                                        {
                                            writer.write(buffer, i, 1);
                                        }
                                        else
                                        {
                                            writer.write(pattern, (patternLength - 1) - (i * -1), 1);
                                        }
                                    }
                                }
                                
                                if (matchCount == patternLength)
                                {
                                    writer.write(replacement, 0, replacementLength);
                                    matchCount = 0;
                                    found = true;
                                }
                            }
                            
                            charactersRead = reader.read(buffer, 0, buffer.length);
                        }
                        
                        writer.close();
                        reader.close();


                        if (found == true)
                        {
                            reader = new BufferedReader(
                                     new InputStreamReader(
                                     new FileInputStream(destinationFile),
                                    "UTF8"));
                            writer = new BufferedWriter(
                                     new OutputStreamWriter(
                                     new FileOutputStream(sourceFile),
                                     "UTF8"));
                            charactersRead = reader.read(buffer, 0, buffer.length);

                            while (charactersRead > 0)
                            {
                                writer.write(buffer, 0, charactersRead);
                                charactersRead = reader.read(buffer, 0, buffer.length);
                            }
                            
                            writer.close();
                            reader.close();
                        }
                        else
                        {
                            System.out.println("txtreplace1: '" + pattern + "' not found.");
                        }
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-13);
        }
        catch (XMLStreamException ex)
        {
            ex.printStackTrace();
            System.exit(-14);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-15);
        }

        
        try
        {
            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(
                                    new FileInputStream(sourceFile),
                                    "UTF8"));
            BufferedWriter writer = new BufferedWriter(
                                    new OutputStreamWriter(
                                    new FileOutputStream(new File(args[2])),
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
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-16);
        }
        
        
        if (sourceFile.delete() != true)
        {
            System.out.println("txtreplace1: Can't delete temporary file '" + sourceFile.getAbsolutePath() + "'.");
        }
        
        if (destinationFile.delete() != true)
        {
            System.out.println("txtreplace1: Can't delete temporary file '" + destinationFile.getAbsolutePath() + "'.");
        }


        System.exit(0);
    }
}

