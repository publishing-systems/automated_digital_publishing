/* Copyright (C) 2014-2015  Stephan Kreutzer
 *
 * This file is part of html2epub2.
 *
 * html2epub2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * html2epub2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with html2epub2. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/XHTMLValidator.java
 * @brief Validator for XHTML files.
 * @author Stephan Kreutzer
 * @since 2015-04-12
 */



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;



class XHTMLValidator
{
    public XHTMLValidator()
    {

    }

    public int validate(File xhtmlFile)
    {
        String programPath = XHTMLValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        try
        {
            programPath = new File(programPath).getCanonicalPath() + File.separator;
            programPath = URLDecoder.decode(programPath, "UTF-8");
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

        String doctypeDeclaration = new String("<!DOCTYPE");
        int doctypePosMatching = 0;
        String doctype = new String();

        try
        {
            FileInputStream in = new FileInputStream(xhtmlFile);

            int currentByte = 0;
 
            do
            {
                currentByte = in.read();

                if (currentByte < 0 ||
                    currentByte > 255)
                {
                    break;
                }


                char currentByteCharacter = (char) currentByte;

                if (doctypePosMatching < doctypeDeclaration.length())
                {
                    if (currentByteCharacter == doctypeDeclaration.charAt(doctypePosMatching))
                    {
                        doctypePosMatching++;
                        doctype += currentByteCharacter;
                    }
                    else
                    {
                        doctypePosMatching = 0;
                        doctype = new String();
                    }
                }
                else
                {
                    doctype += currentByteCharacter;

                    if (currentByteCharacter == '>')
                    {
                        break;
                    }
                }

            } while (true);
        }
        catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
            System.exit(-19);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-20);
        }

        File entitiesResolverConfigFile = null;
        File schemaFile = null;
        File schemataResolverConfigFile = null;

        if (doctype.contains("\"-//W3C//DTD XHTML 1.0 Strict//EN\"") == true)
        {
            entitiesResolverConfigFile = new File(programPath + "entities" + File.separator + "config_xhtml1-strict.xml");
            schemaFile = new File(programPath + "schemata" + File.separator + "xhtml1-strict.xsd");
            schemataResolverConfigFile = new File(programPath + "schemata" + File.separator + "config_xhtml1-strict.xml");
        }
        else if (doctype.contains("\"-//W3C//DTD XHTML 1.1//EN\"") == true)
        {
            System.out.print("html2epub2: Can't validate XHTML 1.1 file - XHTML 1.1 validation not supported yet.\n");
            System.exit(-22);
        }
        else
        {
            System.out.print("html2epub2: Unknown XHTML version, can't validate.\n");
            System.exit(-23);
        }

        if (entitiesResolverConfigFile == null)
        {
            System.out.println("html2epub2: No entities resolver configuration file specified.");
            System.exit(-1);
        }

        if (entitiesResolverConfigFile.exists() != true)
        {
            System.out.println("html2epub2: Entities resolver configuration file '" + entitiesResolverConfigFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (entitiesResolverConfigFile.isFile() != true)
        {
            System.out.println("html2epub2: Entities resolver configuration path '" + entitiesResolverConfigFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (entitiesResolverConfigFile.canRead() != true)
        {
            System.out.print("html2epub2: Entities resolver configuration file '" + entitiesResolverConfigFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        if (schemaFile == null)
        {
            System.out.println("html2epub2: No XML schema file specified.");
            System.exit(-1);
        }

        if (schemaFile.exists() != true)
        {
            System.out.println("html2epub2: XML Schema file '" + schemaFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (schemaFile.isFile() != true)
        {
            System.out.println("html2epub2: XML Schema path '" + schemaFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (schemaFile.canRead() != true)
        {
            System.out.print("html2epub2: XML Schema file '" + schemaFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile == null)
        {
            System.out.println("html2epub2: No schemata resolver configuration file specified.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile.exists() != true)
        {
            System.out.println("html2epub2: Schemata resolver configuration file '" + schemataResolverConfigFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile.isFile() != true)
        {
            System.out.println("html2epub2: Schemata resolver configuration path '" + schemataResolverConfigFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile.canRead() != true)
        {
            System.out.print("html2epub2: Schemata resolver configuration file '" + schemataResolverConfigFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }
        
        XMLSchemaValidator validator = new XMLSchemaValidator();

        if (validator.Validate(xhtmlFile, entitiesResolverConfigFile, schemaFile, schemataResolverConfigFile) != 0)
        {
            System.out.println("html2epub2: XHTML input file '" + xhtmlFile.getAbsolutePath() + "' is invalid according to schema '" + schemaFile.getAbsolutePath() + "'.");
            System.exit(-1);
        }
        
        return 0;
    }
}

