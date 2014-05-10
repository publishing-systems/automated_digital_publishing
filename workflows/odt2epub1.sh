#!/bin/sh
# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of automated_digital_publishing.
#
# automated_digital_publishing is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# automated_digital_publishing is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with automated_digital_publishing. If not, see <http://www.gnu.org/licenses/>.

echo "odt2epub1  Copyright (C) 2014  Stephan Kreutzer"
echo "This program comes with ABSOLUTELY NO WARRANTY."
echo "This is free software, and you are welcome to redistribute it"
echo "under certain conditions. See the GNU Affero General Public"
echo "License 3 or any later version for details. Also, see the source"
echo "code repository: https://github.com/skreutzer/automated_digital_publishing/\n"


printf "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" > ../gui/file_picker/file_picker1/config.xml
printf "<!-- This file was created by odt2epub1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n" >> ../gui/file_picker/file_picker1/config.xml
printf "<file-picker1-config>\n" >> ../gui/file_picker/file_picker1/config.xml
printf "  <extension extension=\"odt\">ODF Text Document (.odt)</extension>\n" >> ../gui/file_picker/file_picker1/config.xml
printf "</file-picker1-config>\n" >> ../gui/file_picker/file_picker1/config.xml

result=`java -classpath ../gui/file_picker/file_picker1 file_picker1 | grep -e "selected"`

OIFS=$IFS
IFS="\'"
tokens=$result
path=""

i=0

for x in $tokens
do
    if [ $i = "1" ]; then
        path=$x
        break
    fi
    
    i=$((i+1))
done

IFS=$OIFS

if [ "${#path}" -le 0 ]; then
    echo "odt2epub1: Path of input ODT file wasn't obtained."
    exit 1
fi

mkdir -p temp

# TODO: Check return codes.
java -classpath ../odt2html/odt2html1 odt2html1 $path ./temp/output_1.html

cp ../xsltransformator/xsltransformator1/entities/config_xhtml1-strict.xml ../xsltransformator/xsltransformator1/entities/config.xml 

java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 ./temp/output_1.html ../odt2html/templates/template1/prepare4hierarchical.xsl ./temp/output_2.html

cp ../html_flat2hierarchical/html_flat2hierarchical1/entities/config_xhtml1-strict.xml ../html_flat2hierarchical/html_flat2hierarchical1/entities/config.xml 

java -classpath ../html_flat2hierarchical/html_flat2hierarchical1 html_flat2hierarchical1 ./temp/output_2.html ../odt2html/templates/template1/html_flat2hierarchical1_config.xml ./temp/output_3.html

rm -Rf ./temp/epub
mkdir -p ./temp/epub

cp ../html_split/html_split1/entities/config_xhtml1-strict.xml ../html_split/html_split1/entities/config.xml 

result=`java -classpath ../html_split/html_split1 html_split1 ./temp/output_3.html ../odt2html/templates/template1/html_split1_config_part.xml ./temp/epub/in | grep -e "Splitting"`

separator=";"

OIFS=$IFS
IFS="\'"
parts=""

i=1

for x in $result
do
    if  [ `expr $i % 2` -eq 0 ]; then
        parts=$parts$x$separator
    fi
    
    i=$((i+1))
done

IFS=$OIFS

OIFS=$IFS
IFS=$separator
parts_found=0

i=1

for x in $parts
do
    parts_found=$i

    printf "odt2epub1: Processing part '$x' to HTML...\n"

    java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 $x ../odt2html/templates/template1/html2epub1_html_part.xsl ./temp/epub/in/$i.html

    printf "odt2epub1: Splitting part '$x' to chapters...\n"

    result=`java -classpath ../html_split/html_split1 html_split1 $x ../odt2html/templates/template1/html_split1_config_chapter.xml ./temp/epub/in/$i | grep -e "Splitting"`

    OIFS2=$IFS
    IFS="\'"
    chapters=""
    j=1

    for y in $result
    do
        if  [ `expr $j % 2` -eq 0 ]; then
            chapters=$chapters$y$separator
        fi
        
        j=$((j+1))
    done
    
    IFS=$OIFS2
    
    OIFS2=$IFS
    IFS=$separator
    j=1

    for y in $chapters
    do
        printf "odt2epub1: Processing chapter '$y' to HTML...\n"
            
        java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 $y ../odt2html/templates/template1/html2epub1_html_chapter.xsl ./temp/epub/in/$i/$j.html
        
        j=$((j+1))
    done
    
    IFS=$OIFS2
    
    i=$((i+1))
done

IFS=$OIFS

if [ $parts_found -le 0 ]; then

    result=`java -classpath ../html_split/html_split1 html_split1 ./temp/output_3.html ../odt2html/templates/template1/html_split1_config_chapter.xml ./temp/epub/in | grep -e "Splitting"`

    OIFS=$IFS
    IFS="\'"
    chapters=""

    i=1

    for x in $result
    do
        if  [ `expr $i % 2` -eq 0 ]; then
            chapters=$chapters$x$separator
        fi
        
        i=$((i+1))
    done

    IFS=$OIFS
    
    OIFS=$IFS
    IFS=$separator

    i=1

    for x in $chapters
    do
        printf "odt2epub1: Processing chapter '$x' to HTML...\n"
            
        java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 $x ../odt2html/templates/template1/html2epub1_html_chapter.xsl ./temp/epub/in/$i.html
        
        i=$((i+1))
    done

    IFS=$OIFS  
fi

java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 ./temp/output_3.html ../odt2html/templates/template1/html2epub1_config.xsl ./temp/epub/config.xml

printf "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" > ./temp/html2epub1_config_replacement_dictionary.xml
printf "<!-- This file was created by odt2epub1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "<txtreplace1-replacement-dictionary>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "  <replace>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "    <pattern>./</pattern>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "    <replacement>./in/</replacement>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "  </replace>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml
printf "</txtreplace1-replacement-dictionary>\n" >> ./temp/html2epub1_config_replacement_dictionary.xml

java -classpath ../txtreplace/txtreplace1 txtreplace1 ./temp/epub/config.xml ./temp/html2epub1_config_replacement_dictionary.xml ./temp/epub/config.xml

java -classpath ../html2epub/html2epub1/gui/html2epub1_config_file_setup html2epub1_config_file_setup ./temp/epub/config.xml
java -classpath ../html2epub/html2epub1/gui/html2epub1_config_metadata_editor html2epub1_config_metadata_editor ./temp/epub/config.xml

java -classpath ../html2epub/html2epub1 html2epub1 ./temp/epub/config.xml

