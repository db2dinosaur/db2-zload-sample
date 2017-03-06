# db2-zload-sample
Some examples of driving the DB2 for z/OS V12 ZLOAD feature from scripts and Java. They are all intended to be run on a mid-range (Windows / Linux) platform to push data onto DB2 for z/OS. To run these you will need:<br>
<ol>
<li> DB2 for z/OS V12
<li> The correct driver - either:
<ol><li> IBM DB2 DataServer Driver V11.1 Fix Pack 1
<li> IBM DB2 Connect V11.1 Fix Pack 1
<li> DB2 JDBC driver 3.72.24
</ol><li> The DB2 for z/OS sample tables created in install/IVP job DSNTEJ1
</ol>
Both examples drive a refresh of table DSN81210.EMP using data gathered with UNLOAD DELIMITED.
<br>
Code:
<ol>
<li> unload.jcl     - UNLOAD DELIMITED from DSN81210.EMP. This JCL is used to create the data used elsewhere
<li> download.txt   - FTP instructions to create a standard CSV as well as the MODE B ZLOAD input file used by ZLOAD
<li> zloademp.txt   - ZLOAD command example
<li> testzload.java - Java zLoad (and DSNUTILU CHECK) example
</ol>
Hope you find this useful.
<br><br>
db2dinosaur - March 2017
