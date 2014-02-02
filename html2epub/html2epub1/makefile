# Copyright (C) 2013-2014  Stephan Kreutzer
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



PREFIX ?= /usr/local
BINDIR ?= ${PREFIX}/bin
.PHONY: html2epub1 install clean



html2epub1: html2epub1.class

ConfigProcessor.class: ConfigProcessor.java
	javac ConfigProcessor.java
	
XHTMLValidator.class: XHTMLValidator.java
	javac XHTMLValidator.java

XHTMLProcessor.class: XHTMLProcessor.java
	javac XHTMLProcessor.java

EPUBSetup.class: EPUBSetup.java XHTMLProcessor.class
	javac EPUBSetup.java
	
ReferencedFiles.class: EPUBSetup.java
	javac EPUBSetup.java

ZipProcessor.class: ZipProcessor.java
	javac ZipProcessor.java

html2epub1.class: html2epub1.java ConfigProcessor.class XHTMLValidator.class XHTMLProcessor.class ReferencedFiles.class EPUBSetup.class ZipProcessor.class
	javac html2epub1.java

${BINDIR}:
	mkdir -p ${BINDIR}

install: html2epub1 ${BINDIR}
	mkdir -p ${BINDIR}/html2epub1
	cp ConfigProcessor.class ${BINDIR}/html2epub1
	cp XHTMLValidator.class ${BINDIR}/html2epub1
	cp XHTMLProcessor.class ${BINDIR}/html2epub1
	cp ReferencedFiles.class ${BINDIR}/html2epub1
	cp EPUBSetup.class ${BINDIR}/html2epub1
	cp ZipProcessor.class ${BINDIR}/html2epub1
	cp html2epub1.class ${BINDIR}/html2epub1

clean:
	rm -f html2epub1.class
	rm -f EPUBSetup.class
	rm -f ZipProcessor.class
	rm -f XHTMLProcessor.class
	rm -f XHTMLValidator.class
	rm -f ConfigProcessor.class
	rm -f ReferencedFiles.class
