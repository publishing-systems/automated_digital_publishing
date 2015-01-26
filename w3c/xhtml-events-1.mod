<!-- ...................................................................... -->
<!-- XHTML Intrinsic Events Module  ....................................... -->
<!-- file: xhtml-events-1.mod

     This is XHTML, a reformulation of HTML as a modular XML application.
     Copyright 1998-2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-events-1.mod,v 1.1 2010/07/29 13:42:47 bertails Exp $ SMI

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ENTITIES XHTML Intrinsic Events 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-events-1.mod"

     Revisions:
     (none)


     URL of the original document:
     http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-events-1.mod


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

<!-- Intrinsic Event Attributes

     These are the event attributes defined in HTML 4,
     Section 18.2.3 "Intrinsic Events". This module must be
     instantiated prior to the Attributes Module but after
     the Datatype Module in the Modular Framework module.

    "Note: Authors of HTML documents are advised that changes
     are likely to occur in the realm of intrinsic events
     (e.g., how scripts are bound to events). Research in
     this realm is carried on by members of the W3C Document
     Object Model Working Group (see the W3C Web site at
     http://www.w3.org/ for more information)."
-->
<!-- NOTE: Because the ATTLIST declarations in this module occur
     before their respective ELEMENT declarations in other
     modules, there may be a dependency on this module that
     should be considered if any of the parameter entities used
     for element type names (eg., %a.qname;) are redeclared.
-->

<!ENTITY % Events.attrib
     "onclick      %Script.datatype;        #IMPLIED
      ondblclick   %Script.datatype;        #IMPLIED
      onmousedown  %Script.datatype;        #IMPLIED
      onmouseup    %Script.datatype;        #IMPLIED
      onmouseover  %Script.datatype;        #IMPLIED
      onmousemove  %Script.datatype;        #IMPLIED
      onmouseout   %Script.datatype;        #IMPLIED
      onkeypress   %Script.datatype;        #IMPLIED
      onkeydown    %Script.datatype;        #IMPLIED
      onkeyup      %Script.datatype;        #IMPLIED"
>

<![%XHTML.global.attrs.prefixed;[
<!ENTITY % XHTML.global.events.attrib
     "%XHTML.prefix;:onclick      %Script.datatype;        #IMPLIED
      %XHTML.prefix;:ondblclick   %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onmousedown  %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onmouseup    %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onmouseover  %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onmousemove  %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onmouseout   %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onkeypress   %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onkeydown    %Script.datatype;        #IMPLIED
      %XHTML.prefix;:onkeyup      %Script.datatype;        #IMPLIED"
>
]]>

<!-- additional attributes on anchor element
-->
<!ATTLIST %a.qname;
     onfocus      %Script.datatype;         #IMPLIED
     onblur       %Script.datatype;         #IMPLIED
>

<!-- additional attributes on form element
-->
<!ATTLIST %form.qname;
      onsubmit     %Script.datatype;        #IMPLIED
      onreset      %Script.datatype;        #IMPLIED
>

<!-- additional attributes on label element
-->
<!ATTLIST %label.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
>

<!-- additional attributes on input element
-->
<!ATTLIST %input.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
      onselect     %Script.datatype;        #IMPLIED
      onchange     %Script.datatype;        #IMPLIED
>

<!-- additional attributes on select element
-->
<!ATTLIST %select.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
      onchange     %Script.datatype;        #IMPLIED
>

<!-- additional attributes on textarea element
-->
<!ATTLIST %textarea.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
      onselect     %Script.datatype;        #IMPLIED
      onchange     %Script.datatype;        #IMPLIED
>

<!-- additional attributes on button element
-->
<!ATTLIST %button.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
>

<!-- additional attributes on body element
-->
<!ATTLIST %body.qname;
      onload       %Script.datatype;        #IMPLIED
      onunload     %Script.datatype;        #IMPLIED
>

<!-- additional attributes on area element
-->
<!ATTLIST %area.qname;
      onfocus      %Script.datatype;        #IMPLIED
      onblur       %Script.datatype;        #IMPLIED
>

<!-- end of xhtml-events-1.mod -->
