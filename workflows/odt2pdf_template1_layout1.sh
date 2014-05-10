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

echo "odt2pdf1  Copyright (C) 2014  Stephan Kreutzer"
echo "This program comes with ABSOLUTELY NO WARRANTY."
echo "This is free software, and you are welcome to redistribute it"
echo "under certain conditions. See the GNU Affero General Public"
echo "License 3 or any later version for details. Also, see the source"
echo "code repository: https://github.com/skreutzer/automated_digital_publishing/\n"


printf "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" > ../gui/file_picker/file_picker1/config.xml
printf "<!-- This file was created by odt2pdf1, which is free software licensed under the GNU Affero General Public License 3 or any later version (see https://github.com/skreutzer/automated_digital_publishing/). -->\n" >> ../gui/file_picker/file_picker1/config.xml
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
    echo "odt2pdf1: Path of input ODT file wasn't obtained."
    exit 1
fi

mkdir -p temp

# TODO: Check return codes.
java -classpath ../odt2html/odt2html1 odt2html1 $path ./temp/output_1.html

cp ../xsltransformator/xsltransformator1/entities/config_xhtml1-strict.xml ../xsltransformator/xsltransformator1/entities/config.xml 

java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 ./temp/output_1.html ../odt2html/templates/template1/prepare4hierarchical.xsl ./temp/output_2.html

cp ../html_flat2hierarchical/html_flat2hierarchical1/entities/config_xhtml1-strict.xml ../html_flat2hierarchical/html_flat2hierarchical1/entities/config.xml 

java -classpath ../html_flat2hierarchical/html_flat2hierarchical1 html_flat2hierarchical1 ./temp/output_2.html ../odt2html/templates/template1/html_flat2hierarchical1_config.xml ./temp/output_3.html

rm -Rf ./temp/latex
mkdir -p ./temp/latex

java -classpath ../xsltransformator/xsltransformator1 xsltransformator1 ./temp/output_3.html ../html2latex/html2latex1/layout/layout1.xsl ./temp/latex/output.tex

cd temp
cd latex
pdflatex output.tex
pdflatex output.tex
pdflatex output.tex
pdflatex output.tex


