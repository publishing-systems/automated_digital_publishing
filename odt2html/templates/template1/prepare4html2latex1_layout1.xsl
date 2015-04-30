<?xml version="1.0" encoding="UTF-8"?>
<!--
Copyright (C) 2014-2015  Stephan Kreutzer

This file is part of template1 for odt2html.

template1 for odt2html is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License version 3 or any later version,
as published by the Free Software Foundation.

template1 for odt2html is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Affero General Public License 3 for more details.

You should have received a copy of the GNU Affero General Public License 3
along with template1 for odt2html. If not, see <http://www.gnu.org/licenses/>.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xhtml">
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

  <xsl:template match="@*|node()|text()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()|text()"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:div/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']">
    <p class="paragraph_default">
      <xsl:apply-templates select="@*[name()!='class']|node()|text()"/>
    </p>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:div/xhtml:div/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']">
    <p class="paragraph_default">
      <xsl:apply-templates select="@*[name()!='class']|node()|text()"/>
    </p>
  </xsl:template>
  
  <xsl:template match="xhtml:html/xhtml:body/xhtml:div/xhtml:div/xhtml:div/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']">
    <p class="paragraph_default">
      <xsl:apply-templates select="@*[name()!='class']|node()|text()"/>
    </p>
  </xsl:template>

  <xsl:template match="xhtml:html/xhtml:body/xhtml:div/xhtml:div/xhtml:div/xhtml:div/xhtml:p[@class='paragraph_5f_first' or @class='paragraph']">
    <p class="paragraph_default">
      <xsl:apply-templates select="@*[name()!='class']|node()|text()"/>
    </p>
  </xsl:template>

</xsl:stylesheet>
