# DataBaseValidation

Prerequisites
* Command prompt
* Access to DB
* [Jar Files](https://drive.google.com/file/d/1QFG6x6scl5jwJcRrI3LPs9sS_fJP5pap/view?usp=sharing)
  
Setup
Step 1: You need to download the jar file.

Step 2: Create a folder in C:\ with the name “dbdetail”(C:\dbdetail). 

Step 3: In dbdetail folder,Create properties file with name “credentials” and format of credentials.properties:

        userName = userName1 , userName2 
        password = password1 , password2 
        proxyPassword = proxyPassword1 , proxyPassword2 
        serverIp = serverIp1 , serverIp2 
        port = port1 , port2 
        dbName = dbName
        isSSHEnabledUser = isSSHEnabledUser1 , isSSHEnabledUser2 
and for the data check create properties file with name "TablesData" and format of TablesData.properties:
   
        table1Name = field1Name, field2Name, ..., fieldnName
        table2Name = field1Name, field2Name, ..., fieldnName
        
Step 4:  If SSH is enabled (i.e. if putty connection required and need to connect db by tunneling) then mark isSSHEnabledUser = true and provide the proxy password.


        i) If putty is required for user1 only, then mark isSSHEnabledUser1 = true, provide proxy password for user2.
        i.e : isSSHEnabledUser = true, false
              proxyPassword = #qwerty, null


        ii) If putty is required for user2 only, then mark isSSHEnabledUser2 = true, provide proxy password for user2.
        i.e : isSSHEnabledUser = false, true
              proxyPassword = null, qwerty


        iii) If putty is required for both users then , provide a proxy password for both the users.
        i.e:  isSSHEnabledUser = true, true
               proxyPassword = #qwerty, #poiuyt


Step 5: Open cmd to the path of the folder where you downloaded the jar file.
        
Step 6: write command java -jar “jar file which you want to run with extension”

        Eg: java -jar CashAppDBValidation.jar
          


When we run the jar :
After connecting successful It will ask whether you want to Compare data or type</br>
1)Data check- compare data of schema</br>
2) Type check: structure of table 
  
If the file generated successfully then message prompt “File generated Successfully!!”

Step 7: You can check the report in C:\dbdetail\Report (either in xlsx).
