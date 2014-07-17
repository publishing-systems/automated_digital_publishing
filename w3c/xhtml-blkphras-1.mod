<!-- ...................................................................... -->
<!-- XHTML Block Phrasal Module  .......................................... -->
<!-- file: xhtml-blkphras-1.mod

     This is XHTML, a reformulation of HTML as a modular XML application.
     Copyright 1998-2005 W3C (MIT, ERCIM, Keio), All Rights Reserved.
     Revision: $Id: xhtml-blkphras-1.mod,v 1.1 2010/07/29 13:42:46 bertails Exp $ SMI

     This DTD module is identified by the PUBLIC and SYSTEM identifiers:

       PUBLIC "-//W3C//ELEMENTS XHTML Block Phrasal 1.0//EN"
       SYSTEM "http://www.w3.org/MarkUp/DTD/xhtml-blkphras-1.mod"

     Revisions:
     (none)


     URL of the original document:
     http://www.w3.org/TR/xhtml-modularization/DTD/xhtml-blkphras-1.mod


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

<!-- Block Phrasal

        address, blockquote, pre, h1, h2, h3, h4, h5, h6

     This module declares the elements and their attributes used to
     support block-level phrasal markup.
-->

<!ENTITY % address.element  "INCLUDE" >
<![%address.element;[
<!ENTITY % address.content
     "( #PCDATA | %Inline.mix; )*" >
<!ENTITY % address.qname  "address" >
<!ELEMENT %address.qname;  %address.content; >
<!-- end of address.element -->]]>

<!ENTITY % address.attlist  "INCLUDE" >
<![%address.attlist;[
<!ATTLIST %address.qname;
      %Common.attrib;
>
<!-- end of address.attlist -->]]>

<!ENTITY % blockquote.element  "INCLUDE" >
<![%blockquote.element;[
<!ENTITY % blockquote.content
     "( %Block.mix; )*"
>
<!ENTITY % blockquote.qname  "blockquote" >
<!ELEMENT %blockquote.qname;  %blockquote.content; >
<!-- end of blockquote.element -->]]>

<!ENTITY % blockquote.attlist  "INCLUDE" >
<![%blockquote.attlist;[
<!ATTLIST %blockquote.qname;
      %Common.attrib;
      cite         %URI.datatype;           #IMPLIED
>
<!-- end of blockquote.attlist -->]]>

<!ENTITY % pre.element  "INCLUDE" >
<![%pre.element;[
<!ENTITY % pre.content
     "( #PCDATA
      | %InlStruct.class;
      %InlPhras.class;
      | %tt.qname; | %i.qname; | %b.qname;
      %I18n.class;
      %Anchor.class;
      | %map.qname;
      %Misc.class;
      %Inline.extra; )*"
>
<!ENTITY % pre.qname  "pre" >
<!ELEMENT %pre.qname;  %pre.content; >
<!-- end of pre.element -->]]>

<!ENTITY % pre.attlist  "INCLUDE" >
<![%pre.attlist;[
<!ATTLIST %pre.qname;
      %Common.attrib;
>
<!-- end of pre.attlist -->]]>

<!-- ...................  Heading Elements  ................... -->

<!ENTITY % Heading.content  "( #PCDATA | %Inline.mix; )*" >

<!ENTITY % h1.element  "INCLUDE" >
<![%h1.element;[
<!ENTITY % h1.qname  "h1" >
<!ELEMENT %h1.qname;  %Heading.content; >
<!-- end of h1.element -->]]>

<!ENTITY % h1.attlist  "INCLUDE" >
<![%h1.attlist;[
<!ATTLIST %h1.qname;
      %Common.attrib;
>
<!-- end of h1.attlist -->]]>

<!ENTITY % h2.element  "INCLUDE" >
<![%h2.element;[
<!ENTITY % h2.qname  "h2" >
<!ELEMENT %h2.qname;  %Heading.content; >
<!-- end of h2.element -->]]>

<!ENTITY % h2.attlist  "INCLUDE" >
<![%h2.attlist;[
<!ATTLIST %h2.qname;
      %Common.attrib;
>
<!-- end of h2.attlist -->]]>

<!ENTITY % h3.element  "INCLUDE" >
<![%h3.element;[
<!ENTITY % h3.qname  "h3" >
<!ELEMENT %h3.qname;  %Heading.content; >
<!-- end of h3.element -->]]>

<!ENTITY % h3.attlist  "INCLUDE" >
<![%h3.attlist;[
<!ATTLIST %h3.qname;
      %Common.attrib;
>
<!-- end of h3.attlist -->]]>

<!ENTITY % h4.element  "INCLUDE" >
<![%h4.element;[
<!ENTITY % h4.qname  "h4" >
<!ELEMENT %h4.qname;  %Heading.content; >
<!-- end of h4.element -->]]>

<!ENTITY % h4.attlist  "INCLUDE" >
<![%h4.attlist;[
<!ATTLIST %h4.qname;
      %Common.attrib;
>
<!-- end of h4.attlist -->]]>

<!ENTITY % h5.element  "INCLUDE" >
<![%h5.element;[
<!ENTITY % h5.qname  "h5" >
<!ELEMENT %h5.qname;  %Heading.content; >
<!-- end of h5.element -->]]>

<!ENTITY % h5.attlist  "INCLUDE" >
<![%h5.attlist;[
<!ATTLIST %h5.qname;
      %Common.attrib;
>
<!-- end of h5.attlist -->]]>

<!ENTITY % h6.element  "INCLUDE" >
<![%h6.element;[
<!ENTITY % h6.qname  "h6" >
<!ELEMENT %h6.qname;  %Heading.content; >
<!-- end of h6.element -->]]>

<!ENTITY % h6.attlist  "INCLUDE" >
<![%h6.attlist;[
<!ATTLIST %h6.qname;
      %Common.attrib;
>
<!-- end of h6.attlist -->]]>

<!-- end of xhtml-blkphras-1.mod -->
