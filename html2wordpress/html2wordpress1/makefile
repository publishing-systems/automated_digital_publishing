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



directories = ./workflows



.PHONY: all html2wordpress1 $(directories) clean



all: html2wordpress1 $(directories)
html2wordpress1: html2wordpress1.class



Base64Coder.class: Base64Coder.java
	javac Base64Coder.java

JobFileProcessor.class: JobFileProcessor.java
	javac JobFileProcessor.java

XHTMLValidator.class: XHTMLValidator.java
	javac XHTMLValidator.java

html2wordpress1.class: html2wordpress1.java Base64Coder.class JobFileProcessor.class XHTMLValidator.java
	javac html2wordpress1.java

$(directories):
	$(MAKE) --directory=$@

clean:
	rm -f ./html2wordpress1.class
	rm -f ./XHTMLValidator.class
	rm -f ./JobFileProcessor.class
	rm -f ./Base64Coder.class

