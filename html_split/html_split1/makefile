# Copyright (C) 2014  Stephan Kreutzer
#
# This file is part of html_split1.
#
# html_split1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# html_split1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with html_split1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: html_split1 clean



html_split1: html_split1.class

EntityResolverLocal.class: html_split1.java
	javac html_split1.java
	
html_split1.class: html_split1.java
	javac html_split1.java

clean:
	rm -f html_split1.class
	rm -f EntityResolverLocal.class
	rm -f HierarchyDefinition.class
	rm -f StructureStackElement.class

