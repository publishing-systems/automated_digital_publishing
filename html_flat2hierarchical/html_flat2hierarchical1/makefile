# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of html_flat2hierarchical1.
#
# html_flat2hierarchical1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# html_flat2hierarchical1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with html_flat2hierarchical1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: html_flat2hierarchical1 clean



html_flat2hierarchical1: html_flat2hierarchical1.class

EntityResolverLocal.class: html_flat2hierarchical1.java
	javac html_flat2hierarchical1.java
	
html_flat2hierarchical1.class: html_flat2hierarchical1.java
	javac html_flat2hierarchical1.java

clean:
	rm -f html_flat2hierarchical1.class
	rm -f EntityResolverLocal.class
	rm -f HierarchyDefinition.class
	rm -f StructureStackElement.class

