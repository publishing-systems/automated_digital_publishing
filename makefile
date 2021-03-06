# Copyright (C) 2014-2015  Stephan Kreutzer
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



directories = ./bommanager ./csv2xml ./downloader ./epub2html ./epubcheck ./gui ./html2epub ./html2latex ./html2wordpress ./html_attributeanalyzer ./html_attributereplace ./html_concatenate ./html_flat2hierarchical ./html_split ./latex ./odt2html ./schemavalidator ./txtreplace ./unzip ./workflows ./xml_fix_special_characters_escaping ./xml_split ./xsltransformator



.PHONY: all $(directories) install



all: $(directories)



$(directories):
	$(MAKE) --directory=$@

install:
	java -classpath ./workflows/ setup1

