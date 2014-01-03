# Copyright (C) 2013-2014  Stephan Kreutzer
#
# This file is part of html2epub.
#
# html2epub is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# html2epub is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with html2epub. If not, see <http://www.gnu.org/licenses/>.



PREFIX ?= /usr/local
BINDIR ?= ${PREFIX}/bin
.PHONY: html2epub install clean



html2epub: html2epub.class ConfigProcessor.class XHTMLProcessor.class ReferencedFiles.class EPUBSetup.class

ConfigProcessor.class: ConfigProcessor.java
	javac ConfigProcessor.java

XHTMLProcessor.class: XHTMLProcessor.java
	javac XHTMLProcessor.java

EPUBSetup.class: EPUBSetup.java XHTMLProcessor.class
	javac EPUBSetup.java
	
ReferencedFiles.class: EPUBSetup.java
	javac EPUBSetup.java

html2epub.class: html2epub.java ConfigProcessor.class XHTMLProcessor.class ReferencedFiles.class EPUBSetup.class
	javac html2epub.java

${BINDIR}:
	mkdir -p ${BINDIR}

install: html2epub ${BINDIR}
	mkdir -p ${BINDIR}/html2epub
	cp ConfigProcessor.class ${BINDIR}/html2epub
	cp XHTMLProcessor.class ${BINDIR}/html2epub
	cp ReferencedFiles.class ${BINDIR}/html2epub
	cp EPUBSetup.class ${BINDIR}/html2epub
	cp html2epub.class ${BINDIR}/html2epub

clean:
	rm -f html2epub.class
	rm -f EPUBSetup.class
	rm -f XHTMLProcessor.class
	rm -f ConfigProcessor.class
	rm -f ReferencedFiles.class
