# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of xsltransformator1.
#
# xsltransformator1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# xsltransformator1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with xsltransformator1. If not, see <http://www.gnu.org/licenses/>.



directories = ./workflows



.PHONY: all xsltransformator1 $(directories) clean


all: xsltransformator1 $(directories)
xsltransformator1: xsltransformator1.class

EntityResolverLocal.class: xsltransformator1.java
	javac xsltransformator1.java
	
xsltransformator1.class: xsltransformator1.java
	javac xsltransformator1.java

$(directories):
	$(MAKE) --directory=$@

clean:
	rm -f xsltransformator1.class
	rm -f EntityResolverLocal.class

