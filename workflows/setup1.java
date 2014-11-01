/* Copyright (C) 2014  Stephan Kreutzer
 *
 * This file is part of setup1.
 *
 * setup1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * setup1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with setup1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/workflows/setup1.java
 * @brief Sets up the various tools that get called by the processing workflows.
 * @author Stephan Kreutzer
 * @since 2014-05-29
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



public class setup1
{
    public static void main(String args[])
    {
        System.out.print("setup1  Copyright (C) 2014  Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository: https://github.com/publishing-systems/automated_digital_publishing/\n\n");
    
        String programPath = setup1.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    
    
        CopyXHTML_1_0_Strict(programPath, "../xsltransformator/xsltransformator1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html_flat2hierarchical/html_flat2hierarchical1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html_split/html_split1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html2epub/html2epub1/");
        CopyXHTML_1_0_Strict(programPath, "../html2latex/html_prepare4latex1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html2wordpress/html2wordpress1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html_attributeanalyzer/html_attributeanalyzer1/entities/");
        CopyXHTML_1_0_Strict(programPath, "../html_attributereplace/html_attributereplace1/entities/");
    
        CopyXHTML_1_1(programPath, "../epub2html/epub2html1/entities/");
        CopyXHTML_1_1(programPath, "../html2wordpress/html2wordpress1/entities/");
        CopyXHTML_1_1(programPath, "../xsltransformator/xsltransformator1/entities/");
        CopyXHTML_1_1(programPath, "../html_attributeanalyzer/html_attributeanalyzer1/entities/");
        CopyXHTML_1_1(programPath, "../html_attributereplace/html_attributereplace1/entities/");


        {
            File from = new File(programPath + "../w3c/xhtml1-strict.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml1-strict.xsd")) != 0)
            {
                System.exit(-17);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml1-strict.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xml.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xml.xsd")) != 0)
            {
                System.exit(-18);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xml.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml11.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml11.xsd")) != 0)
            {
                System.exit(-11);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml11-model-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml11-model-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-datatypes-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-datatypes-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml11-modules-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml11-modules-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-framework-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-framework-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-attribs-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-attribs-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-text-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-text-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-blkphras-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-blkphras-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-blkstruct-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-blkstruct-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-inlphras-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-inlphras-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-inlstruct-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-inlstruct-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-hypertext-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-hypertext-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-list-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-list-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-struct-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-struct-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-edit-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-edit-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-bdo-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-bdo-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-pres-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-pres-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-blkpres-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-blkpres-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-inlpres-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-inlpres-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-link-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-link-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-meta-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-meta-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-base-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-base-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-script-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-script-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-style-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-style-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-inlstyle-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-inlstyle-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-image-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-image-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-csismap-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-csismap-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-ssismap-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-ssismap-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-object-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-object-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-param-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-param-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-table-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-table-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-form-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-form-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-ruby-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-ruby-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-events-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-events-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        {
            File from = new File(programPath + "../w3c/xhtml-target-1.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2wordpress/html2wordpress1/schemata/xhtml-target-1.xsd")) != 0)
            {
                System.exit(-1);
            }
        }

        return;
    }

    public static int CopyXHTML_1_0_Strict(String programPath, String to)
    {
        if (programPath.endsWith("/") != true &&
            programPath.endsWith("\\") != true)
        {
            programPath += "/";
        }
        
        if (to.startsWith("/") == true)
        {
            to = to.substring(0, new String("/").length());
        }
        else if (to.startsWith("\\") == true)
        {
            to = to.substring(0, new String("\\").length());
        }
        
        if (to.endsWith("/") != true &&
            to.endsWith("\\") != true)
        {
            to += "/";
        }


        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml1-strict.dtd"), 
                            new File(programPath + to + "xhtml1-strict.dtd")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-lat1.ent"), 
                            new File(programPath + to + "xhtml-lat1.ent")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-symbol.ent"), 
                            new File(programPath + to + "xhtml-symbol.ent")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-special.ent"), 
                            new File(programPath + to + "xhtml-special.ent")) != 0)
        {
            System.exit(-1);
        }

        return 0;
    }

    public static int CopyXHTML_1_1(String programPath, String to)
    {
        if (programPath.endsWith("/") != true &&
            programPath.endsWith("\\") != true)
        {
            programPath += "/";
        }
        
        if (to.startsWith("/") == true)
        {
            to = to.substring(0, new String("/").length());
        }
        else if (to.startsWith("\\") == true)
        {
            to = to.substring(0, new String("\\").length());
        }
        
        if (to.endsWith("/") != true &&
            to.endsWith("\\") != true)
        {
            to += "/";
        }


        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml11.dtd"), 
                            new File(programPath + to + "xhtml11.dtd")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-inlstyle-1.mod"),
                            new File(programPath + to + "xhtml-inlstyle-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-datatypes-1.mod"),
                            new File(programPath + to + "xhtml-datatypes-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-framework-1.mod"),
                            new File(programPath + to + "xhtml-framework-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-qname-1.mod"),
                            new File(programPath + to + "xhtml-qname-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-events-1.mod"),
                            new File(programPath + to + "xhtml-events-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-attribs-1.mod"),
                            new File(programPath + to + "xhtml-attribs-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml11-model-1.mod"),
                            new File(programPath + to + "xhtml11-model-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-charent-1.mod"),
                            new File(programPath + to + "xhtml-charent-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-lat1.ent"),
                            new File(programPath + to + "xhtml-lat1.ent")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-symbol.ent"),
                            new File(programPath + to + "xhtml-symbol.ent")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-special.ent"),
                            new File(programPath + to + "xhtml-special.ent")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-text-1.mod"),
                            new File(programPath + to + "xhtml-text-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-inlstruct-1.mod"),
                            new File(programPath + to + "xhtml-inlstruct-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-inlphras-1.mod"),
                            new File(programPath + to + "xhtml-inlphras-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-blkstruct-1.mod"),
                            new File(programPath + to + "xhtml-blkstruct-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-blkphras-1.mod"),
                            new File(programPath + to + "xhtml-blkphras-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-hypertext-1.mod"),
                            new File(programPath + to + "xhtml-hypertext-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-list-1.mod"),
                            new File(programPath + to + "xhtml-list-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-edit-1.mod"),
                            new File(programPath + to + "xhtml-edit-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-bdo-1.mod"),
                            new File(programPath + to + "xhtml-bdo-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-ruby-1.mod"),
                            new File(programPath + to + "xhtml-ruby-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-pres-1.mod"),
                            new File(programPath + to + "xhtml-pres-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-inlpres-1.mod"),
                            new File(programPath + to + "xhtml-inlpres-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-blkpres-1.mod"),
                            new File(programPath + to + "xhtml-blkpres-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-link-1.mod"),
                            new File(programPath + to + "xhtml-link-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-meta-1.mod"),
                            new File(programPath + to + "xhtml-meta-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-base-1.mod"),
                            new File(programPath + to + "xhtml-base-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-script-1.mod"),
                            new File(programPath + to + "xhtml-script-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-style-1.mod"),
                            new File(programPath + to + "xhtml-style-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-image-1.mod"),
                            new File(programPath + to + "xhtml-image-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-csismap-1.mod"),
                            new File(programPath + to + "xhtml-csismap-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-ssismap-1.mod"),
                            new File(programPath + to + "xhtml-ssismap-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-param-1.mod"),
                            new File(programPath + to + "xhtml-param-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-object-1.mod"),
                            new File(programPath + to + "xhtml-object-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-table-1.mod"),
                            new File(programPath + to + "xhtml-table-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-form-1.mod"),
                            new File(programPath + to + "xhtml-form-1.mod")) != 0)
        {
            System.exit(-1);
        }

        if (setup1.CopyFile(new File(programPath + "../w3c/xhtml-struct-1.mod"),
                            new File(programPath + to + "xhtml-struct-1.mod")) != 0)
        {
            System.exit(-1);
        }
        
        return 0;
    }
    
    public static int CopyFile(File from, File to)
    {
        if (from.exists() != true)
        {
            System.out.println("setup1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' doesn't exist.");
            return -1;
        }
        
        if (from.isFile() != true)
        {
            System.out.println("setup1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't a file.");
            return -2;
        }
        
        if (from.canRead() != true)
        {
            System.out.println("setup1: Can't copy '" + from.getAbsolutePath() + "' to '" + to.getAbsolutePath() + "' because '" + from.getAbsolutePath() + "' isn't readable.");
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
            System.exit(-19);
        }
        catch (UnsupportedEncodingException ex)
        {
            ex.printStackTrace();
            System.exit(-20);
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
            System.exit(-21);
        }
    
        return 0;
    }
}
