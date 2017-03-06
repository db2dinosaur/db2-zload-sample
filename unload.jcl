//UNLOAD  EXEC PGM=DSNUTILB,REGION=0M,         
//             PARM='DC01,UNLEMP1'             
//STEPLIB  DD  DISP=SHR,DSN=SDC00.DC01.SDSNEXIT
//         DD  DISP=SHR,DSN=SDC00.DC01.SDSNLOAD
//SYSPRINT DD  SYSOUT=*                        
//FORMAT   DD  DSN=MYUSER.EMP.FORMAT,           
//             DISP=(,CATLG),UNIT=SYSDA,       
//             SPACE=(TRK,(1,1),RLSE)          
//RECORDS  DD  DSN=MYUSER.EMP.UNLOAD,           
//             DISP=(,CATLG),UNIT=SYSDA,       
//             SPACE=(TRK,(20,20),RLSE)        
//UTPRINT  DD  SYSOUT=*                        
//SYSIN    DD  *                               
  UNLOAD DATA FROM TABLE DSN81210.EMP          
      PUNCHDDN FORMAT                          
      UNLDDN   RECORDS                         
               DELIMITED                       
      SHRLEVEL CHANGE                          
               ISOLATION UR                    
/*                                             