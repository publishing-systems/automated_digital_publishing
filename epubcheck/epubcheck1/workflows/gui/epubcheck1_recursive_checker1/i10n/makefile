# Copyright (C) 2017  Stephan Kreutzer
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



.PHONY: all i10n clean



all: i10n
i10n: i10nSaveJobfileOverrideDialogGUIObjects.class i10nSaveJobfileOverrideDialogGUIObjects_de.class



i10nSaveJobfileOverrideDialogGUIObjects.class: i10nSaveJobfileOverrideDialogGUIObjects.java
	javac i10nSaveJobfileOverrideDialogGUIObjects.java

i10nSaveJobfileOverrideDialogGUIObjects_de.class: i10nSaveJobfileOverrideDialogGUIObjects_de.java
	javac i10nSaveJobfileOverrideDialogGUIObjects_de.java

clean:
	rm -f i10nSaveJobfileOverrideDialogGUIObjects_de.class
	rm -f i10nSaveJobfileOverrideDialogGUIObjects.class
