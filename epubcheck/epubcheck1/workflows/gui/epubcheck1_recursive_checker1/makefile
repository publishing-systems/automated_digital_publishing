# Copyright (C) 2015  Stephan Kreutzer
#
# This file is part of epubcheck1_recursive_checker1 GUI.
#
# epubcheck1_recursive_checker1 GUI is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License version 3 or any later version,
# as published by the Free Software Foundation.
#
# epubcheck1_recursive_checker1 GUI is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Affero General Public License 3 for more details.
#
# You should have received a copy of the GNU Affero General Public License 3
# along with epubcheck1_recursive_checker1 GUI. If not, see <http://www.gnu.org/licenses/>.



.PHONY: all epubcheck1_recursive_checker1 clean



all: epubcheck1_recursive_checker1
epubcheck1_recursive_checker1: epubcheck1_recursive_checker1.class



AboutDialog.class: AboutDialog.java
	javac AboutDialog.java

AddInputDecideRecursiveDialog.class: AddInputDecideRecursiveDialog.java
	javac AddInputDecideRecursiveDialog.java

SaveJobfileOverrideDialog.class: SaveJobfileOverrideDialog.java
	javac SaveJobfileOverrideDialog.java

epubcheck1_recursive_checker1.class: epubcheck1_recursive_checker1.java AboutDialog.class AddInputDecideRecursiveDialog.class SaveJobfileOverrideDialog.class
	javac epubcheck1_recursive_checker1.java

clean:
	rm -f epubcheck1_recursive_checker1.class
	rm -f epubcheck1_recursive_checker1$$1.class
	rm -f epubcheck1_recursive_checker1$$2.class
	rm -f epubcheck1_recursive_checker1$$3.class
	rm -f epubcheck1_recursive_checker1$$4.class
	rm -f epubcheck1_recursive_checker1$$5.class
	rm -f ResultsTableModel.class
	rm -f InputTableModel.class
	rm -f SaveJobfileOverrideDialog.class
	rm -f AddInputDecideRecursiveDialog.class
	rm -f AboutDialog.class
	rm -f AboutDialog$$1.class

