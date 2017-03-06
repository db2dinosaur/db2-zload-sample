# db2-zload-sample
Some examples of driving the DB2 for z/OS V12 ZLOAD feature from scripts and Java. They are all intended to be run on a mid-range (Windows / Linux) platform to push data onto DB2 for z/OS. To run these you will need:
1. DB2 for z/OS V12
2. The correct driver - either:
  i) IBM DB2 DataServer Driver V11.1 Fix Pack 1
  ii) IBM DB2 Connect V11.1 Fix Pack 1
  iii) DB2 JDBC driver 3.72.24
3. The DB2 for z/OS sample tables created in install/IVP job DSNTEJ1
Both examples drive a refresh of table DSN81210.EMP using data gathered with UNLOAD DELIMITED.

Code:
1. unload.jcl     - UNLOAD DELIMITED from DSN81210.EMP. This JCL is used to create the data used elsewhere
2. download.txt   - FTP instructions to create a standard CSV as well as the MODE B ZLOAD input file used by ZLOAD
3. zloademp.txt   - ZLOAD command example
4. testzload.java - Java zLoad (and DSNUTILU CHECK) example

Hope you find this useful.

db2dinosaur - March 2017
