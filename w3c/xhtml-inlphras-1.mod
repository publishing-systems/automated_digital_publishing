<!-- ...................................................................... -->
<!-- XHTML Inline Phrasal Module  ......................................... -->
<!-- file: xhtml-inlphras-1.mod

     This is XHTML, a reformulation of HTML as a modular XML application.
     Copyright 1998-2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-inlphras-1.mod,v 1.1 2010/07/29 13:42:47 bertails Exp $ SMI

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ELEMENTS XHTML Inline Phrasal 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-inlphras-1.mod"

     Revisions:
     (none)


     URL of the original document:
     http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-inlphras-1.mod"


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

<!-- Inline Phrasal

        abbr, acronym, cite, code, dfn, em, kbd, q, samp, strong, var

     This module declares the elements and their attributes used to
     support inline-level phrasal markup.
-->

<!ENTITY % abbr.element  "INCLUDE" >
<![%abbr.element;[
<!ENTITY % abbr.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % abbr.qname  "abbr" >
<!ELEMENT %abbr.qname;  %abbr.content; >
<!-- end of abbr.element -->]]>

<!ENTITY % abbr.attlist  "INCLUDE" >
<![%abbr.attlist;[
<!ATTLIST %abbr.qname;
      %Common.attrib;
>
<!-- end of abbr.attlist -->]]>

<!ENTITY % acronym.element  "INCLUDE" >
<![%acronym.element;[
<!ENTITY % acronym.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % acronym.qname  "acronym" >
<!ELEMENT %acronym.qname;  %acronym.content; >
<!-- end of acronym.element -->]]>

<!ENTITY % acronym.attlist  "INCLUDE" >
<![%acronym.attlist;[
<!ATTLIST %acronym.qname;
      %Common.attrib;
>
<!-- end of acronym.attlist -->]]>

<!ENTITY % cite.element  "INCLUDE" >
<![%cite.element;[
<!ENTITY % cite.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % cite.qname  "cite" >
<!ELEMENT %cite.qname;  %cite.content; >
<!-- end of cite.element -->]]>

<!ENTITY % cite.attlist  "INCLUDE" >
<![%cite.attlist;[
<!ATTLIST %cite.qname;
      %Common.attrib;
>
<!-- end of cite.attlist -->]]>

<!ENTITY % code.element  "INCLUDE" >
<![%code.element;[
<!ENTITY % code.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % code.qname  "code" >
<!ELEMENT %code.qname;  %code.content; >
<!-- end of code.element -->]]>

<!ENTITY % code.attlist  "INCLUDE" >
<![%code.attlist;[
<!ATTLIST %code.qname;
      %Common.attrib;
>
<!-- end of code.attlist -->]]>

<!ENTITY % dfn.element  "INCLUDE" >
<![%dfn.element;[
<!ENTITY % dfn.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % dfn.qname  "dfn" >
<!ELEMENT %dfn.qname;  %dfn.content; >
<!-- end of dfn.element -->]]>

<!ENTITY % dfn.attlist  "INCLUDE" >
<![%dfn.attlist;[
<!ATTLIST %dfn.qname;
      %Common.attrib;
>
<!-- end of dfn.attlist -->]]>

<!ENTITY % em.element  "INCLUDE" >
<![%em.element;[
<!ENTITY % em.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % em.qname  "em" >
<!ELEMENT %em.qname;  %em.content; >
<!-- end of em.element -->]]>

<!ENTITY % em.attlist  "INCLUDE" >
<![%em.attlist;[
<!ATTLIST %em.qname;
      %Common.attrib;
>
<!-- end of em.attlist -->]]>

<!ENTITY % kbd.element  "INCLUDE" >
<![%kbd.element;[
<!ENTITY % kbd.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % kbd.qname  "kbd" >
<!ELEMENT %kbd.qname;  %kbd.content; >
<!-- end of kbd.element -->]]>

<!ENTITY % kbd.attlist  "INCLUDE" >
<![%kbd.attlist;[
<!ATTLIST %kbd.qname;
      %Common.attrib;
>
<!-- end of kbd.attlist -->]]>

<!ENTITY % q.element  "INCLUDE" >
<![%q.element;[
<!ENTITY % q.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % q.qname  "q" >
<!ELEMENT %q.qname;  %q.content; >
<!-- end of q.element -->]]>

<!ENTITY % q.attlist  "INCLUDE" >
<![%q.attlist;[
<!ATTLIST %q.qname;
      %Common.attrib;
      cite         %URI.datatype;           #IMPLIED
>
<!-- end of q.attlist -->]]>

<!ENTITY % samp.element  "INCLUDE" >
<![%samp.element;[
<!ENTITY % samp.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % samp.qname  "samp" >
<!ELEMENT %samp.qname;  %samp.content; >
<!-- end of samp.element -->]]>

<!ENTITY % samp.attlist  "INCLUDE" >
<![%samp.attlist;[
<!ATTLIST %samp.qname;
      %Common.attrib;
>
<!-- end of samp.attlist -->]]>

<!ENTITY % strong.element  "INCLUDE" >
<![%strong.element;[
<!ENTITY % strong.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % strong.qname  "strong" >
<!ELEMENT %strong.qname;  %strong.content; >
<!-- end of strong.element -->]]>

<!ENTITY % strong.attlist  "INCLUDE" >
<![%strong.attlist;[
<!ATTLIST %strong.qname;
      %Common.attrib;
>
<!-- end of strong.attlist -->]]>

<!ENTITY % var.element  "INCLUDE" >
<![%var.element;[
<!ENTITY % var.content
     "( #PCDATA | %Inline.mix; )*"
>
<!ENTITY % var.qname  "var" >
<!ELEMENT %var.qname;  %var.content; >
<!-- end of var.element -->]]>

<!ENTITY % var.attlist  "INCLUDE" >
<![%var.attlist;[
<!ATTLIST %var.qname;
      %Common.attrib;
>
<!-- end of var.attlist -->]]>

<!-- end of xhtml-inlphras-1.mod -->
