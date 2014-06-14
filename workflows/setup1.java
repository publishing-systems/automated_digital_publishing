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
                         "License 3 or any later version for details. Also, see the source\n" +
                         "code repository: https://github.com/skreutzer/automated_digital_publishing/\n\n");
    
        String programPath = setup1.class.getProtectionDomain().getCodeSource().getLocation().getFile();
    
        {
            File from = new File(programPath + "../w3c/xhtml1-strict.dtd");

            if (setup1.CopyFile(from, new File(programPath + "../xsltransformator/xsltransformator1/entities/xhtml1-strict.dtd")) != 0)
            {
                System.exit(-1);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml1-strict.dtd")) != 0)
            {
                System.exit(-2);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_split/html_split1/entities/xhtml1-strict.dtd")) != 0)
            {
                System.exit(-3);
            }       
            
            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml1-strict.dtd")) != 0)
            {
                System.exit(-4);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2latex/html_prepare4latex1/entities/xhtml1-strict.dtd")) != 0)
            {
                System.exit(-22);
            }    
        }
        
        {
            File from = new File(programPath + "../w3c/xhtml-lat1.ent");

            if (setup1.CopyFile(from, new File(programPath + "../xsltransformator/xsltransformator1/entities/xhtml-lat1.ent")) != 0)
            {
                System.exit(-5);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-lat1.ent")) != 0)
            {
                System.exit(-6);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_split/html_split1/entities/xhtml-lat1.ent")) != 0)
            {
                System.exit(-7);
            }       
            
            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml-lat1.ent")) != 0)
            {
                System.exit(-8);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2latex/html_prepare4latex1/entities/xhtml-lat1.ent")) != 0)
            {
                System.exit(-23);
            } 
        }
        
        {
            File from = new File(programPath + "../w3c/xhtml-symbol.ent");

            if (setup1.CopyFile(from, new File(programPath + "../xsltransformator/xsltransformator1/entities/xhtml-symbol.ent")) != 0)
            {
                System.exit(-9);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-symbol.ent")) != 0)
            {
                System.exit(-10);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_split/html_split1/entities/xhtml-symbol.ent")) != 0)
            {
                System.exit(-11);
            }       
            
            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml-symbol.ent")) != 0)
            {
                System.exit(-12);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2latex/html_prepare4latex1/entities/xhtml-symbol.ent")) != 0)
            {
                System.exit(-24);
            }
        }
        
        {
            File from = new File(programPath + "../w3c/xhtml-special.ent");

            if (setup1.CopyFile(from, new File(programPath + "../xsltransformator/xsltransformator1/entities/xhtml-special.ent")) != 0)
            {
                System.exit(-13);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-special.ent")) != 0)
            {
                System.exit(-14);
            }
            
            if (setup1.CopyFile(from, new File(programPath + "../html_split/html_split1/entities/xhtml-special.ent")) != 0)
            {
                System.exit(-15);
            }       
            
            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml-special.ent")) != 0)
            {
                System.exit(-16);
            }

            if (setup1.CopyFile(from, new File(programPath + "../html2latex/html_prepare4latex1/entities/xhtml-special.ent")) != 0)
            {
                System.exit(-25);
            }
        }
        
        {
            File from = new File(programPath + "../w3c/xhtml1-strict.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xhtml1-strict.xsd")) != 0)
            {
                System.exit(-17);
            }    
        }

        {
            File from = new File(programPath + "../w3c/xml.xsd");

            if (setup1.CopyFile(from, new File(programPath + "../html2epub/html2epub1/xml.xsd")) != 0)
            {
                System.exit(-18);
            }    
        }

        return;
    }
    
    public static int CopyFile (File from, File to)
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
