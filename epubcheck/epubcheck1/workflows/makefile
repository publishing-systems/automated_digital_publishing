# Copyright (C) 2015  Stephan Kreutzer
#
# This file is part of epubcheck1_recursive_checker1 workflow.
#
# epubcheck1_recursive_checker1 workflow is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# epubcheck1_recursive_checker1 workflow is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with epubcheck1_recursive_checker1 workflow. If not, see <http://www.gnu.org/licenses/>.



directories = ./gui



.PHONY: all epubcheck1_recursive_checker1 $(directories) clean



all: epubcheck1_recursive_checker1 $(directories)
epubcheck1_recursive_checker1: epubcheck1_recursive_checker1.class



epubcheck1_recursive_checker1.class: epubcheck1_recursive_checker1.java
	javac epubcheck1_recursive_checker1.java

$(directories):
	$(MAKE) --directory=$@

clean:
	rm -f epubcheck1_recursive_checker1.class

