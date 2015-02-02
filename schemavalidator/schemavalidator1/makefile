# Copyright (C) 2015  Stephan Kreutzer
#
# This file is part of schemavalidator1.
#
# schemavalidator1 is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# schemavalidator1 is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with schemavalidator1. If not, see <http://www.gnu.org/licenses/>.



.PHONY: schemavalidator1 clean



schemavalidator1: schemavalidator1.class

XMLSchemaValidator.class: XMLSchemaValidator.java
	javac XMLSchemaValidator.java

schemavalidator1.class: schemavalidator1.java XMLSchemaValidator.class
	javac schemavalidator1.java

clean:
	rm -f ./schemavalidator1.class
	rm -f ./XMLSchemaValidator.class

