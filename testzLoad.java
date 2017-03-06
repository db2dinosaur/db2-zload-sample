// testzLoad
// =========
// Takes comma delimited data from emp.csv and converts it to block mode format:
//
//   o read record
//   o calculate length (big-endian - Java is big-endian)
//   o if not EOF then type = 128 (EOR) else type = 64 (EOF)
//   o write type
//   o write length
//   o write record
//   o repeat until out of records
//
// Then usex ZLOAD to LOAD REPLACE this into DSN81210.EMP. This leaves two RI linked
// tables in CHECKP - DSN81210.DEPT (DSN8D12A.DSN8S12D) and DSN81210.ACT
// (DSN8D12A.DSN8S12P) so DSNUTILU is used to run the CHECK utility to clear these.
//
// The input file (emp.csv) is produced by running the UNLOAD DELIMITED utlity on DB2
// for z/OS and then FTP downloading it in ASCII format. See unload.jcl and download.txt.
//
// This code is built using:
//
//   javac testzLoad.java
//
// and run using:
//
//   java testzLoad
//
// db2dinosaur - March 2017

import java.lang.*;
import java.io.*;
import java.util.*;
import java.sql.*;
import com.ibm.db2.jcc.*;

public class testzLoad
{
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
		{
			System.out.println("testzLoad");
			System.out.println("=========");
			// convert the input file into MODE B format
			String infile = "Z:\\ZLOAD testing\\emp.csv";
			String outfile = "Z:\\ZLOAD testing\\emp.del";
			System.out.println("1. Reading "+infile);
			System.out.println("   Writing "+outfile);
			Scanner lrdr = new Scanner(new File(infile));
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(outfile));
			while (lrdr.hasNext()) {
				String line = lrdr.nextLine();
				Integer illine = line.length();
				short lline = illine.shortValue();
				byte ltype = 0;
				if (lrdr.hasNext()) {
					ltype = (byte)128;
				} else {
					ltype = (byte)64;
				}
				dos.writeByte(ltype);
				dos.writeShort(lline);
				dos.writeBytes(line);
			}
			dos.close();
			lrdr.close();

			System.out.println("2. Connecting to DB2 DC00");
			Properties props = new Properties();
			
			String username = "MYUSER";
			String password = "mypwd";
			
			String url = "jdbc:db2://s0w1.zpdt.local:2050/DC00";
			props.setProperty("user", username);
			props.setProperty("password", password);
			
			// connect to DC00
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
			Connection con = (com.ibm.db2.jcc.DB2Connection) DriverManager.getConnection( url, props );
			System.out.println("   Connected");
			// autocommit on
			con.setAutoCommit(true);
			System.out.println("3. Run zLoad");
			DB2Connection db2con = (DB2Connection)con;
			String loadstmt = "TEMPLATE SORTIN DSN MYUSER.&JO..&ST..SORTIN.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
							  "TEMPLATE SORTOUT DSN MYUSER.&JO..&ST..SORTOUT.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
							  "TEMPLATE MYSERR  DSN MYUSER.&JO..&ST..SYSERR.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
							  "TEMPLATE MYSDISC DSN MYUSER.&JO..&ST..SYSDISC.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
							  "TEMPLATE MYSMAP  DSN MYUSER.&JO..&ST..SYSMAP.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
							  "LOAD DATA INDDN SYSCLIEN WORKDDN(SORTIN,SORTOUT) MAPDDN(MYSMAP) DISCARDDN(MYSDISC) ERRDDN(MYSERR) "+
									"REPLACE SHRLEVEL NONE LOG(NO) NOCOPYPEND FORMAT DELIMITED ASCII INTO TABLE DSN81210.EMP "+
									"ENFORCE CONSTRAINTS SORTDEVT SYSDA SORTNUM 4";
			String utilityid = "TSTZLOAD";
			LoadResult lr = db2con.zLoad (loadstmt, outfile, utilityid);
			int returnCode = lr.getReturnCode();
			String loadMessage = lr.getMessage();
			System.out.println("Return code = "+Integer.toHexString(returnCode));
			System.out.println("Messages =");
			System.out.println(loadMessage);
			// If the LOAD was good, run the CHECK
			if (returnCode < 8) {
				System.out.println("4. Running CHECK");
				String sql = "CALL DSNUTILU (?,?,?,?)";
				utilityid = "RUNCHECK";
				String restart = "NO";
				Integer retcode = 0;
				CallableStatement stmt = con.prepareCall(sql);
				String chkstmt = "TEMPLATE SORTIN DSN MYUSER.&JO..&ST..SYSUT1.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
								 "TEMPLATE SORTOUT DSN MYUSER.&JO..&ST..SORTOUT.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
								 "TEMPLATE MYSERR  DSN MYUSER.&JO..&ST..SYSERR.T&TIME. UNIT SYSDA SPACE(10,10) CYL DISP(NEW,DELETE,DELETE) "+
								 "CHECK DATA TABLESPACE DSN8D12A.DSN8S12D TABLESPACE DSN8D12A.DSN8S12P WORKDDN(SORTIN,SORTOUT) ERRDDN(MYSERR) SORTDEVT SYSDA SORTNUM 4";
				// set input parms
				stmt.setString(1,utilityid);
				stmt.setString(2,restart);
				stmt.setString(3,chkstmt);
				stmt.registerOutParameter(4,Types.INTEGER);
				// call proc
				boolean results = stmt.execute();
				System.out.println("DSNUTILU executed CHECK");
				retcode = stmt.getInt(4);
				System.out.println("CHECK return code = "+Integer.toHexString(retcode));
				if (results) {
					ResultSet rs = stmt.getResultSet();
					while (rs.next()) {
						String lid = rs.getString(1);
						String msg = rs.getString(2);
						System.out.println(lid+" : "+msg);
					}
					rs.close();
				}
			}
			con.close();
		}
	}
}
