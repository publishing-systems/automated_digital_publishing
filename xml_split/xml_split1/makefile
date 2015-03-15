# Copyright (C) 2015  Stephan Kreutzer
#
# This file is part of xml_split1.
#
# xml_split1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# xml_split1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with xml_split1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: xml_split1 clean



xml_split1: xml_split1.class

EntityResolverLocal.class: xml_split1.java
	javac xml_split1.java
	
xml_split1.class: xml_split1.java
	javac xml_split1.java

clean:
	rm -f xml_split1.class
	rm -f EntityResolverLocal.class
	rm -f HierarchyDefinition.class
	rm -f StructureStackElement.class

