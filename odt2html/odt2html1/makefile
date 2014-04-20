# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of odt2html1.
#
# odt2html1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# odt2html1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with odt2html1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: odt2html1 clean



odt2html1: odt2html1.class

ZipProcessor.class: ZipProcessor.java
	javac ZipProcessor.java

ODTManifestProcessor.class: ODTManifestProcessor.java
	javac ODTManifestProcessor.java

ODTContentProcessor.class: ODTContentProcessor.java
	javac ODTContentProcessor.java

ODTProcessor.class: ODTProcessor.java ODTManifestProcessor.class ODTContentProcessor.class
	javac ODTProcessor.java

odt2html1.class: odt2html1.java ZipProcessor.class ODTProcessor.class ODTManifestProcessor.class ODTContentProcessor.class
	javac odt2html1.java

clean:
	rm -f odt2html1.class
	rm -f ODTProcessor.class
	rm -f ODTContentProcessor.class
	rm -f ODTManifestProcessor.class
	rm -f ZipProcessor.class

