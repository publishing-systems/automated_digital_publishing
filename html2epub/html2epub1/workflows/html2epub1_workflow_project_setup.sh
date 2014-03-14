#!/bin/sh
# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of html2epub1.
#
# html2epub1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# html2epub1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with html2epub1. If not, see <http://www.gnu.org/licenses/>.

echo "html2epub1_workflow_project_setup  Copyright (C) 2014  Stephan Kreutzer"
echo "This program comes with ABSOLUTELY NO WARRANTY."
echo "This is free software, and you are welcome to redistribute it"
echo "under certain conditions. See the GNU Affero General Public"
echo "License 3 or any later version for details. Also, see the source"
echo "code repository: https://github.com/skreutzer/automated_digital_publishing/\n"


result=`java -classpath ../gui/html2epub1_config_create_new/ html2epub1_config_create_new | grep -e "created"`

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
    echo "html2epub1_workflow_project_setup.sh: Path of html2epub1 configuration file wasn't obtained."
    exit 1
fi

# TODO: Check return codes.
java -classpath ../gui/html2epub1_config_file_setup/ html2epub1_config_file_setup $path
java -classpath ../gui/html2epub1_config_metadata_editor/ html2epub1_config_metadata_editor $path
java -classpath ../ html2epub1 $path


