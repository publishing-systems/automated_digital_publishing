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

echo "setup1  Copyright (C) 2014  Stephan Kreutzer"
echo "This program comes with ABSOLUTELY NO WARRANTY."
echo "This is free software, and you are welcome to redistribute it"
echo "under certain conditions. See the GNU Affero General Public"
echo "License 3 or any later version for details. Also, see the source"
echo "code repository: https://github.com/skreutzer/automated_digital_publishing/\n"


#mkdir -p temp

#echo "Downloading XHTML 1.0 Strict XSD from W3C..."
#wget -O ./temp/xhtml1-strict.xsd http://www.w3.org/2002/08/xhtml/xhtml1-strict.xsd

#echo "Downloading XML XSD from W3C (may take some time due to artificial delay)..."
#wget -O ./temp/xml.xsd http://www.w3.org/2001/xml.xsd

#echo "Downloading XHTML 1.0 Strict DTD from W3C (may take some time due to artificial delay)..."
#wget -O ./temp/xhtml1-strict.dtd http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd

#echo "Downloading Latin 1 Character Entity Set for XHTML from W3C (may take some time due to artificial delay)..."
#wget -O ./temp/xhtml-lat1.ent http://www.w3.org/TR/xhtml1/DTD/xhtml-lat1.ent

#echo "Downloading Mathematical, Greek and Symbolic Character Entity Set for XHTML from W3C (may take some time due to artificial delay)..."
#wget -O ./temp/xhtml-symbol.ent http://www.w3.org/TR/xhtml1/DTD/xhtml-symbol.ent

#echo "Downloading Special Character Entity Set for XHTML from W3C (may take some time due to artificial delay)..."
#wget -O ./temp/xhtml-special.ent http://www.w3.org/TR/xhtml1/DTD/xhtml-special.ent

cp ../w3c/xhtml1-strict.dtd ../xsltransformator/xsltransformator1/entities/xhtml1-strict.dtd
cp ../w3c/xhtml-lat1.ent ../xsltransformator/xsltransformator1/entities/xhtml-lat1.ent
cp ../w3c/xhtml-symbol.ent ../xsltransformator/xsltransformator1/entities/xhtml-symbol.ent
cp ../w3c/xhtml-special.ent ../xsltransformator/xsltransformator1/entities/xhtml-special.ent

cp ../w3c/xhtml1-strict.dtd ../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml1-strict.dtd
cp ../w3c/xhtml-lat1.ent ../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-lat1.ent
cp ../w3c/xhtml-symbol.ent ../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-symbol.ent
cp ../w3c/xhtml-special.ent ../html_flat2hierarchical/html_flat2hierarchical1/entities/xhtml-special.ent

cp ../w3c/xhtml1-strict.dtd ../html_split/html_split1/entities/xhtml1-strict.dtd
cp ../w3c/xhtml-lat1.ent ../html_split/html_split1/entities/xhtml-lat1.ent
cp ../w3c/xhtml-symbol.ent ../html_split/html_split1/entities/xhtml-symbol.ent
cp ../w3c/xhtml-special.ent ../html_split/html_split1/entities/xhtml-special.ent

cp ../w3c/xhtml1-strict.xsd ../html2epub/html2epub1/xhtml1-strict.xsd
cp ../w3c/xml.xsd ../html2epub/html2epub1/xml.xsd
cp ../w3c/xhtml1-strict.dtd ../html2epub/html2epub1/xhtml1-strict.dtd
cp ../w3c/xhtml-lat1.ent ../html2epub/html2epub1/xhtml-lat1.ent
cp ../w3c/xhtml-symbol.ent ../html2epub/html2epub1/xhtml-symbol.ent
cp ../w3c/xhtml-special.ent ../html2epub/html2epub1/xhtml-special.ent

