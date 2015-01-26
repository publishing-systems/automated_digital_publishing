<!-- ...................................................................... -->
<!-- XHTML Embedded Object Module  ........................................ -->
<!-- file: xhtml-object-1.mod

     This is XHTML, a reformulation of HTML as a modular XML application.
     Copyright 1998-2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-object-1.mod,v 4.0 2001/04/02 22:42:49 altheim Exp $ SMI

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ELEMENTS XHTML Embedded Object 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-object-1.mod"

     Revisions:
     (none)


     URL of the original document:
     http://www.w3.org/MarkUp/DTD/xhtml-object-1.mod


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

<!-- Embedded Objects

        object

     This module declares the object element type and its attributes, used
     to embed external objects as part of XHTML pages. In the document,
     place param elements prior to other content within the object element.

     Note that use of this module requires instantiation of the Param
     Element Module.
-->

<!-- object: Generic Embedded Object ................... -->

<!ENTITY % object.element  "INCLUDE" >
<![%object.element;[
<!ENTITY % object.content
     "( #PCDATA | %Flow.mix; | %param.qname; )*"
>
<!ENTITY % object.qname  "object" >
<!ELEMENT %object.qname;  %object.content; >
<!-- end of object.element -->]]>

<!ENTITY % object.attlist  "INCLUDE" >
<![%object.attlist;[
<!ATTLIST %object.qname;
      %Common.attrib;
      declare      ( declare )              #IMPLIED
      classid      %URI.datatype;           #IMPLIED
      codebase     %URI.datatype;           #IMPLIED
      data         %URI.datatype;           #IMPLIED
      type         %ContentType.datatype;   #IMPLIED
      codetype     %ContentType.datatype;   #IMPLIED
      archive      %URIs.datatype;          #IMPLIED
      standby      %Text.datatype;          #IMPLIED
      height       %Length.datatype;        #IMPLIED
      width        %Length.datatype;        #IMPLIED
      name         CDATA                    #IMPLIED
      tabindex     %Number.datatype;        #IMPLIED
>
<!-- end of object.attlist -->]]>

<!-- end of xhtml-object-1.mod -->
