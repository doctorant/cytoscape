<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<HTML>
<HEAD>
   <meta http-equiv="content-type" content=
   "text/html; charset=ISO-8859-1">
   <title>
	Cytoscape Online Tutorial
    </title>
    <link rel="stylesheet" type="text/css" media="screen" href=
  "../css/cytoscape.css">
  <META HTTP-EQUIV="CONTENT-TYPE" CONTENT="text/html; charset=utf-8">
  <META NAME="AUTHOR" CONTENT="Melissa Cline">
  <STYLE>
  <!--
    @page { size: 8.27in 11.69in; margin-right: 1.25in; margin-top: 1in; margin-bottom: 1in }
    P { margin-bottom: 0.08in; direction: ltr; color: #000000; widows: 0; orphans: 0 }
    P.western { font-family: "Nimbus Roman No9 L", "Times New Roman", serif; font-size: 12pt; so-language: en-US }
    P.cjk { font-family: "Times New Roman", serif; font-size: 12pt }
    P.ctl { font-family: "Times New Roman", serif; font-size: 10pt }
  -->
  </STYLE>
</HEAD>
<BODY LANG="en-US" TEXT="#000000" DIR="LTR">
  <table id="feature" border="0" cellpadding="0" cellspacing="0" summary="">
   <tr>
     <td width="10"> </td>
     <td valign="center">
        <h1>
  	Cytoscape Online Tutorial
        </h1>
     </td>
   </tr>
  </table>
  <? include "nav.php"; ?>
  <? include "nav_tut.php"; ?>
  <div id="indent">
  <center>
     <h2>Fetching Data</h2>
  </center>
  <p>
<P> 
</P>
<P>If you have completed the <A HREF="getting.started.php">Getting Started</A>
  and <A HREF="filters.editor.php">Filters and Editor</A> tutorials, 
  this tutorial will help you get started with your own analysis by
  showing you some ways to fetch external data for Cytoscape.
  In this tutorial, you will:</P>
<UL>
  <LI>Download Cytoscape-formatted data from external servers including SGD and BIND.</P>
  <LI>Fetch external data using the <B>cPath</B> plugin.
</UL>
<P>
This tutorial and accompanying lectures were delivered at 
<A HREF="http://www.csc.fi/suomi/info/index.phtml.en">CSC</A>, the Finnish IT center for science. 
The lecture slides of background material are
available <A HREF="fetching.data.ppt">here</A> and an accompanying video
presentation is 
<A HREF="http://rm.tv.funet.fi:8080/ramgen/fi/csc/kurssit/2005/cytoscape/_cytos02.rm">available</A>
courtesy of the CSC.
<P>
This tutorial features the following plugin:
<UL>
  <LI>The <A HREF="http://www.cbio.mskcc.org/cytoscape/cpath/beta2/cpath-beta2.zip">cPath</A>
	plugin from the Sander group at the <A HREF="http://www.cbio.mskcc.org/">Computational Biology
	Center</A> at Memorial Sloan-Ketting Cancer Cente</LI>
</UL>

<P>Begin by (a) opening a second web browser window, and (b) clicking here:
<a href="webstart/cyto2.2.060106.jnlp">WEB START</a> 
(approximate download size: 22 MB) This starts Cytoscape on your own
computer, after downloading the program and annotations from our
website.  
<? include "first.time.php"; ?>

<P>


<H3>External websites for SIF files</H3>
<P>Here, we will explore
some external data resources for Cytoscape.  
<P>
<B>Saccharomyces Genome Database:</B> This is a key resource for anyone who works with yeast!
The SGD provides physical and genetic interactions, which may be downloaded into Cytoscape
SIF format, as outlined below.
</P>
<OL>
    <LI><P>Go to
    <A HREF="http://db.yeastgenome.org/cgi-bin/batchDownload">http://db.yeastgenome.org/cgi-bin/batchDownload</A>, and scroll down to the section labeled <B>Step 1: Your Input</B>
    <LI><P>Under <B>Enter Feature/Standard Gene names</B>, enter a gene 
    symbol such as <B>PPA2</B>.  
    <LI><P>Under <B>Step 2</B>,
    under the section labeled <B>Other data</B>, check the boxes for
    <B>physical</B> and <B>genetic</B> interactions and click <B>Submit</B>.
    <LI><P>You will be redirected to a page labeled <B>Download Data</B>.  
    Near the right side of this page is a link labeled 
    <B>Name of Downloadable File</B>.  Note the filename, and click
    on the link to download the file.
    <LI><P>If your system did not automatically uncompress the file during 
    download, uncompress it.</P>
    <LI><P>Load the
    uncompressed file into Cytoscape.   After you apply your favorite
    layout algorithm, you should see an image as shown.</P>
    <P><IMG SRC="fetching.data/Fig6.png" WIDTH=382 HEIGHT=346>
    <LI><P>What sort of interactions are represented in this network?  
    Physical or genetic?  You can check by looking at the edge attributes
    under the <B>Attribute Browser</B>.
</OL>
<P>
<B>BIND</B> is central repository for protein interaction data, and represents a
very useful resource for those interested in interaction networks.  BIND 
exports data to the SIF format as described below.</P>
<OL>
    <LI><P>In your web browser, go to BIND website at 
    <A HREF="http://bind.ca/Action">http://bind.ca/Action</A>.
    <LI><P>Underneath the <B>Search </B>form, enter the name of your favorite
    protein (such as "TP53") and click on <B>Go</B>
    <LI> This will take you to a new page, with a box at the left labeled <B>BIND Text
    Search</B>, and a box on the right labeled <B>Options</B> and shown below:
    <P><IMG SRC="fetching.data/Fig16.jpg">
    <LI>In the <B>Options</B> box, click on the pull-down menu labeled <B>Export Results</B>.
    In the list of export formats, you will see <B>Cytoscape SIF</B>.  If you select this
    format, a file called <B>searchresults.sif</B> will be downloaded to your computer.
    <LI> In the <B>Options</B> box, the <B>Visualize Results</B> option allows you to 
    visualize the selected interaction data in Cytoscape, as outlined below:
    <OL>
    	<LI> In the <B>Visualize Results</B> menu, scroll down to <B>First 100 interactions -
    	Default Plugins</B>.  This will initiate a new web start instance of Cytoscape
    	on your computer.
    	<LI> Within a few minutes, you should see a new Cytoscape window similar to the
    	one shown below, with no <B>Node Attribute Browser</B> (yet), and listing a network
    	in red, with 0 nodes and 0 edges.  This indicates that the load is in progress.
    	<P><IMG SRC="fetching.data/Fig17.jpg" WIDTH="50%">
    	<LI> If you become impatient, right-click on the red network name, and select
    	<B>Create View</B> in the pull-down menu.  This will show you a network, with nodes
    	being added as you watch.
    	<LI> Eventually, you should see a Cytoscape desktop such as the one shown below, 
    	with a <B>Node Attribute Browser</B>.  This indicates that the download is complete.
    	<P><IMG SRC="fetching.data/Fig18.jpg" WIDTH="50%">
    </OL>
</OL>
<P>Another useful resource
for Cytoscape data is the <B>cPath</B> database and Cytoscape plugin.
 <B>CPath</B> is a free interaction database server package developed
at Memorial Sloan Kettering Cancer Center to serve as a central
clearinghouse for interaction and pathway data.  By default the
Cytoscape <B>cPath</B> plugin interfaces with a demo <B>cPath</B>
database, hosted at MSKCC, with interaction data from the <B>MINT</B>
and <B>IntAct</B> databases.  Any lab working with interaction data
from a variety of sources can install and populate a local copy of
<B>cPATH</B>, and this will then allow them to fetch their data from
one central repository. Here, we shall explore using the <B>cPath</B>
plugin to fetch data from Cytoscape.</P>
<OL>
	<LI>If you have not already done so, start Cytoscape using the <B>Webstart</B> link
	above.  This will start an instance of Cytoscape with the <B>cPath</B> plugin already
	loaded.  This version connects to the cPath demo database at MSKCC.
    <LI><P>Under the <B>Plugins</B>  menu, select <B>Search cPath</B>
    A window should appear, as shown below.</P>
    <IMG SRC="fetching.data/Fig7.jpg"  WIDTH=450 HEIGHT=363>
    <LI><P>In the pull-down menu labeled <B>All Organisms</B>, select 
    <B>Homo Sapiens</B>.</P>
    <LI><P>In the box labeled <B>Search cPath</B>, enter <B>P53</B> and 
    click on the <B>Search</B> button.  In your Cytoscape canvas, you should 
    see a network similar to the one shown below 
    (generated using the <B>yFiles</B> organic layout)</P>
    <IMG SRC="fetching.data/Fig8.jpg" WIDTH=244 HEIGHT=250></P>
    <LI><P>Select a node in
    your network.  In the <B>cPath</B> plugin window, you should see
    information about the node, as shown: 
    <IMG SRC="fetching.data/Fig9.jpg" WIDTH=281 HEIGHT=219></P>
    <LI><P>Select an edge. 
    In the <B>cPath</B> window, you should see information about the
    interaction, as shown: 
    <IMG SRC="fetching.data/Fig10.jpg" WIDTH=295 HEIGHT=267></P>
    <LI><P>Note that there
    can be multiple edges between the same nodes.  The reasons for this
    include:  different external references, implying the interaction
    was observed multiple times; consistent results in symmetric
    experiments (e.g A co-immunoprecipitated with B, and B
    co-immunoprecipitated with A), and different forms of evidence. 
    Explore this a bit.</P>
    <LI><P>As you are
    probably aware by now, the number of interactions retrieved has a
    default limit of 10.  However, you can change this via the
    pull-down menu on the <B>cPath</B> plugin window.  <B><I>Please be
    kind to servers!</I></B>  When exploring a query, start with a low
    interaction limit.  If the query proves useful, then increase your
    interaction limit gradually.</P>
    <LI><P>You can use <B>cPath</B>
    to search on attributes including diseases (e.g. lymphoma) and
    biological processes (e.g. apoptosis).  You can also combine search
    terms using the AND and OR operations (e.g p53 AND apoptosis). 
    Experiment with this a bit.  <B><I>Remember to be kind to servers!</I></B></P>
</OL>
    After users obtain network data from multiple sources, they often
    want to merge the separate networks into one network.
    This can be done with the Cytoscape
    <A HREF="http://www.cytoscape.org/plugins/GraphMerge/GraphMerge.jar">
      Graph Merge</A> plugin from 
     <A HREF="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/">
     The Ideker Lab</A></LI> at UCSD.  For information on installing and
    using the plugin, please see the 
    <A HREF="http://www-bioeng.ucsd.edu/faculty/area/ideker_lab/plugins/GraphMerge/release/README.txt">release notes</A> for this plugin.


<P><B>Congratulations! 
</B>You have ventured past the world of Getting Started, and are now
poised to start doing some serious systems biology!  You will need
your strength for this.  Go get some lunch.</P>
<P>
<? include "tut.footer.php"; ?>
<? include "../footer.php"; ?>
</BODY>
</HTML>