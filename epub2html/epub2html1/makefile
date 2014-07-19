# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of epub2html1.
#
# epub2html1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# epub2html1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with epub2html1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: epub2html1 clean



epub2html1: epub2html1.class

ConfigProcessor.class: ConfigProcessor.java
	javac ConfigProcessor.java

ZipProcessor.class: ZipProcessor.java
	javac ZipProcessor.java

EPUBProcessor.class: EPUBProcessor.java
	javac EPUBProcessor.java

EPUBContainerProcessor.class: EPUBContainerProcessor.java
	javac EPUBContainerProcessor.java

OPFManifestEntry.class: OPFManifestEntry.java
	javac OPFManifestEntry.java

EPUBOPFProcessor.class: OPFManifestEntry.class EPUBOPFProcessor.java
	javac EPUBOPFProcessor.java

EntityResolverLocal.class: EPUBXHTMLProcessor.java
	javac EPUBXHTMLProcessor.java

ReferencedFiles.class: EPUBXHTMLProcessor.java
	javac EPUBXHTMLProcessor.java

EPUBXHTMLProcessor.class: EPUBXHTMLProcessor.java
	javac EPUBXHTMLProcessor.java

epub2html1.class: epub2html1.java EPUBXHTMLProcessor.class ReferencedFiles.class EntityResolverLocal.class EPUBOPFProcessor.class EPUBContainerProcessor.class EPUBProcessor.class ZipProcessor.class ConfigProcessor.class
	javac epub2html1.java

clean:
	rm -f epub2html1.class
	rm -f EPUBXHTMLProcessor.class
	rm -f EPUBOPFProcessor.class
	rm -f EPUBContainerProcessor.class
	rm -f EPUBProcessor.class
	rm -f ZipProcessor.class
	rm -f ConfigProcessor.class
	rm -f OPFManifestEntry.class
	rm -f EntityResolverLocal.class
	rm -f ReferencedFiles.class
