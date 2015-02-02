/* Copyright (C) 2015  Stephan Kreutzer
 *
 * This file is part of schemavalidator1.
 *
 * schemavalidator1 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3 or any later version,
 * as published by the Free Software Foundation.
 *
 * schemavalidator1 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License 3 for more details.
 *
 * You should have received a copy of the GNU Affero General Public License 3
 * along with schemavalidator1. If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * @file $/schemavalidator1.java
 * @brief Java command line tool for using a Java XML Schema validator.
 * @author Stephan Kreutzer
 * @since 2015-02-02
 */



import java.io.File;



public class schemavalidator1
{
    public static void main(String[] args)
    {
        System.out.print("schemavalidator1 Copyright (C) 2015 Stephan Kreutzer\n" +
                         "This program comes with ABSOLUTELY NO WARRANTY.\n" +
                         "This is free software, and you are welcome to redistribute it\n" +
                         "under certain conditions. See the GNU Affero General Public\n" +
                         "License 3 or any later version for details. Also, see the source code\n" +
                         "repository https://github.com/publishing-systems/automated_digital_publishing/\n" +
                         "or the project website http://www.publishing-systems.org.\n\n");

        if (args.length != 4)
        {
            System.out.print("Usage:\n" +
                             "\tschemavalidator1 xml-file entities-resolver-config-file schema-file schema-resolver-config-file\n\n");
            System.exit(1);
        }

        File inFile = new File(args[0]);

        if (inFile.exists() != true)
        {
            System.out.println("schemavalidator1: Input XML file '" + inFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (inFile.isFile() != true)
        {
            System.out.println("schemavalidator1: Input XML path '" + inFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (inFile.canRead() != true)
        {
            System.out.print("schemavalidator1: Input XML file '" + inFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        File entitiesResolverConfigFile = new File(args[1]);

        if (entitiesResolverConfigFile.exists() != true)
        {
            System.out.println("schemavalidator1: Entities resolver configuration file '" + entitiesResolverConfigFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (entitiesResolverConfigFile.isFile() != true)
        {
            System.out.println("schemavalidator1: Entities resolver configuration path '" + entitiesResolverConfigFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (entitiesResolverConfigFile.canRead() != true)
        {
            System.out.print("schemavalidator1: Entities resolver configuration file '" + entitiesResolverConfigFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        File schemaFile = new File(args[2]);

        if (schemaFile.exists() != true)
        {
            System.out.println("schemavalidator1: XML Schema file '" + schemaFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (schemaFile.isFile() != true)
        {
            System.out.println("schemavalidator1: XML Schema path '" + schemaFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (schemaFile.canRead() != true)
        {
            System.out.print("schemavalidator1: XML Schema file '" + schemaFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }

        File schemataResolverConfigFile = new File(args[3]);

        if (schemataResolverConfigFile.exists() != true)
        {
            System.out.println("schemavalidator1: Schemata resolver configuration file '" + schemataResolverConfigFile.getAbsolutePath() + "' doesn't exist.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile.isFile() != true)
        {
            System.out.println("schemavalidator1: Schemata resolver configuration path '" + schemataResolverConfigFile.getAbsolutePath() + "' isn't a file.");
            System.exit(-1);
        }

        if (schemataResolverConfigFile.canRead() != true)
        {
            System.out.print("schemavalidator1: Schemata resolver configuration file '" + schemataResolverConfigFile.getAbsolutePath() + "' isn't readable.");
            System.exit(-1);
        }


        XMLSchemaValidator validator = new XMLSchemaValidator();

        if (validator.Validate(inFile, entitiesResolverConfigFile, schemaFile, schemataResolverConfigFile) == 0)
        {
            System.out.println("schemavalidator1: Valid.");
        }
    }
}

