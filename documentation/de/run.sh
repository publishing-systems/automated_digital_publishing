#!/bin/sh
# Copyright (C) 2015  Stephan Kreutzer
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

rm -f ./out/documentation_de.html
rm -f ./out/documentation_de.epub
rm -f ./out/documentation_de.tex
rm -f ./out/documentation_de.pdf

java -classpath ../../workflows/ odt2all1 ./odt2all1_config.xml ./out/ > out.log 2>&1 | tee out.log

mv -f ./out/out.html ./out/documentation_de.html
mv -f ./out/out.epub ./out/documentation_de.epub
mv -f ./out/out.tex ./out/documentation_de.tex
mv -f ./out/out.pdf ./out/documentation_de.pdf
