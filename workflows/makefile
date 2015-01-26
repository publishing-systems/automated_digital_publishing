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



directories = ./epub2wordpress ./gui



.PHONY: all workflows $(directories) clean



all: workflows $(directories)
workflows: html2epub1.class html2pdf1.class odt2all1.class odt2all2.class odt2epub1.class odt2epub2.class odt2epub3.class odt2html1.class odt2html2.class odt2pdf1.class odt2pdf2.class setup1.class



html2epub1.class: html2epub1.java
	javac html2epub1.java

html2pdf1.class: html2pdf1.java
	javac html2pdf1.java

odt2all1.class: odt2all1.java
	javac odt2all1.java

odt2all2.class: odt2all2.java
	javac odt2all2.java

odt2epub1.class: odt2epub1.java
	javac odt2epub1.java

odt2epub2.class: odt2epub2.java
	javac odt2epub2.java

odt2epub3.class: odt2epub3.java
	javac odt2epub3.java

odt2html1.class: odt2html1.java
	javac odt2html1.java

odt2html2.class: odt2html2.java
	javac odt2html2.java

odt2pdf1.class: odt2pdf1.java
	javac odt2pdf1.java

odt2pdf2.class: odt2pdf2.java
	javac odt2pdf2.java

setup1.class: setup1.java
	javac setup1.java

$(directories):
	$(MAKE) --directory=$@

clean:
	rm -f ./html2epub1.class
	rm -f ./html2pdf1.class
	rm -f ./odt2all1.class
	rm -f ./odt2all2.class
	rm -f ./odt2epub1.class
	rm -f ./odt2epub2.class
	rm -f ./odt2epub3.class
	rm -f ./odt2html1.class
	rm -f ./odt2html2.class
	rm -f ./odt2pdf1.class
	rm -f ./odt2pdf2.class
	rm -f ./setup1.class	

