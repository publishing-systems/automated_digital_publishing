<!-- ...................................................................... -->
<!-- XHTML Document Style Sheet Module  ................................... -->
<!-- file: xhtml-style-1.mod

     This is XHTML, a reformulation of HTML as a modular XML application.
     Copyright 1998-2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-style-1.mod,v 4.1 2001/04/05 06:57:40 altheim Exp $ SMI

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//DTD XHTML Style Sheets 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-style-1.mod"

     Revisions:
     (none)


     URL of the original document:
     http://www.w3.org/MarkUp/DTD/xhtml-style-1.mod


     License

     By using and/or copying this document, or the W3C document from which this
     statement is linked, you (the licensee) agree that you have read,
     understood, and will comply with the following terms and conditions:

     Permission to copy, and distribute the contents of this document, or the W3C
     document from which this statement is linked, in any medium for any purpose
     and without fee or royalty is hereby granted, provided that you include the
     following on ALL copies of the document, or portions thereof, that you use:

      * A link or URL to the original W3C document.
      * The pre-existing copyright notice of the original author, or if it
        doesn't exist, a notice (hypertext is preferred, but a textual
        representation is permitted) of the form: "Copyright ©
        [$date-of-document] World Wide Web Consortium, (Massachusetts Institute
        of Technology, European Research Consortium for Informatics and
        Mathematics, Keio University, Beihang). All Rights Reserved.
        http://www.w3.org/Consortium/Legal/2002/copyright-documents-20021231"
      * If it exists, the STATUS of the W3C document.

     When space permits, inclusion of the full text of this NOTICE should be
     provided. We request that authorship attribution be provided in any
     software, documents, or other items or products that you create pursuant to
     the implementation of the contents of this document, or any portion thereof.

     No right to create modifications or derivatives of W3C documents is granted
     pursuant to this license. However, if additional requirements (documented in
     the Copyright FAQ) are satisfied, the right to create modifications or
     derivatives is sometimes granted by the W3C to individuals complying with
     those requirements.
     
     Disclaimers

     THIS DOCUMENT IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE NO
     REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING, BUT NOT
     LIMITED TO, WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
     NON-INFRINGEMENT, OR TITLE; THAT THE CONTENTS OF THE DOCUMENT ARE SUITABLE
     FOR ANY PURPOSE; NOR THAT THE IMPLEMENTATION OF SUCH CONTENTS WILL NOT
     INFRINGE ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.

     COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
     CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE DOCUMENT OR THE
     PERFORMANCE OR IMPLEMENTATION OF THE CONTENTS THEREOF.

     The name and trademarks of copyright holders may NOT be used in advertising
     or publicity pertaining to this document or its contents without specific,
     written prior permission. Title to copyright in this document will at all
     times remain with copyright holders.

     Notes

     This version:
     http://www.w3.org/Consortium/Legal/2002/copyright-documents-20021231

     This formulation of W3C's notice and license became active on December 31
     2002. This version removes the copyright ownership notice such that this
     license can be used with materials other than those owned by the W3C, moves
     information on style sheets, DTDs, and schemas to the Copyright FAQ,
     reflects that ERCIM is now a host of the W3C, includes references to this
     specific dated version of the license, and removes the ambiguous grant of
     "use". See the older formulation for the policy prior to this date. Please
     see our Copyright FAQ for common questions about using materials from our
     site, such as the translating or annotating specifications.

     ....................................................................... -->

<!-- Style Sheets

        style

     This module declares the style element type and its attributes,
     used to embed style sheet information in the document head element.
-->

<!-- style: Style Sheet Information .................... -->

<!ENTITY % style.element  "INCLUDE" >
<![%style.element;[
<!ENTITY % style.content  "( #PCDATA )" >
<!ENTITY % style.qname  "style" >
<!ELEMENT %style.qname;  %style.content; >
<!-- end of style.element -->]]>

<!ENTITY % style.attlist  "INCLUDE" >
<![%style.attlist;[
<!ATTLIST %style.qname;
      %XHTML.xmlns.attrib;
      %id.attrib;
      %title.attrib;
      %I18n.attrib;
      xml:space    ( preserve )             #FIXED 'preserve'
      type         %ContentType.datatype;   #REQUIRED
      media        %MediaDesc.datatype;     #IMPLIED
>
<!-- end of style.attlist -->]]>

<!-- end of xhtml-style-1.mod -->