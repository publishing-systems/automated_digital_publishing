# Copyright (C) 2013-2015  Stephan Kreutzer
#
# This file is part of html2epub2.
#
# html2epub2 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# html2epub2 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with html2epub2. If not, see <http://www.gnu.org/licenses/>.



.PHONY: all html2epub2 clean



all: html2epub2
html2epub2: html2epub2.class



ConfigProcessor.class: ConfigProcessor.java
	javac ConfigProcessor.java
	
XHTMLValidator.class: XHTMLValidator.java
	javac XHTMLValidator.java

EntityResolverLocal.class: XHTMLValidator.java
	javac XHTMLValidator.java
	
LocalEntityInput.class: XHTMLValidator.java
	javac XHTMLValidator.java

XHTMLProcessor.class: XHTMLProcessor.java
	javac XHTMLProcessor.java

EPUBSetup.class: EPUBSetup.java XHTMLProcessor.class
	javac EPUBSetup.java
	
ReferencedFiles.class: EPUBSetup.java
	javac EPUBSetup.java

ZipProcessor.class: ZipProcessor.java
	javac ZipProcessor.java

html2epub2.class: html2epub2.java ConfigProcessor.class LocalEntityInput.class EntityResolverLocal.class XHTMLValidator.class XHTMLProcessor.class ReferencedFiles.class EPUBSetup.class ZipProcessor.class
	javac html2epub2.java

clean:
	rm -f html2epub2.class
	rm -f EPUBSetup.class
	rm -f ZipProcessor.class
	rm -f XHTMLProcessor.class
	rm -f XHTMLValidator.class
	rm -f EntityResolverLocal.class
	rm -f LocalEntityInput.class
	rm -f ConfigProcessor.class
	rm -f ReferencedFiles.class
