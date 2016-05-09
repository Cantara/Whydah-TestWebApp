Whydah-TestWebApp
=================


NOTE: This repository is sereiously lagging behind the development of the Whydah modules and should only be used as study material until we have time to update the examples and codebase



Reference application that requires log in.

Goto http://localhost:9990/test/hello to trigger log in.


The ImplementationExamples includes example code for Whydah integration for:
* JavaScript,
* Django
* Microsoft Sharepoint.
* Spring Security
* Mobile / Phonegap

![Architectural Overview](https://raw2.github.com/altran/Whydah-SSOLoginWebApp/master/Whydah%20infrastructure.png)

Client code example
===================
```
//  Execute a POST to authenticate my application
String myApplicationToken = Request.Post("https://sso.whydah.net/sso/logon")
        .bodyForm(Form.form().add("applicationcredential", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>
                                               <applicationcredential>
                                                  <params>
                                                      <applicationID>234</applicationID>
                                                      <applicationSecret>This is my application secret</applicationSecret>
                                                  </params>
                                               </applicationcredential>").build())
        .execute().returnContent().asBytes();

//  Find applicationtokenID from applicationToken
String myApplicationTokenID = $(myApplicationToken).xpath("/applicationtoken/params/applicationtokenID[1]");

//  Redirect user til SSO login web with my URL as redirect
//  Get userticket from redirect back URL param
//@RequestMapping("/myapp")
//public String myWebApplication(@QueryParam("userticket") String userticket, HttpServletRequest request)

//  Execute a POST  to SecurityTokenService with userticket to get usertoken
String usertoken = Request.Post("https://sso.whydah.net/sso/user/"+myApplicationTokenID+"/get_usertoken_by_userticket/")
        .bodyForm(Form.form().add("apptoken", myApplicationToken).add("userticket", userTicket).build())
        .execute().returnContent().asBytes();

// Get some token values
String userTokenID = $(usertoken).xpath("/usertoken/@id");
NodeList applicationRoleList = $(usertoken).xpath("/usertoken/application");
boolean hasEmployeeRoleInMyApp = $(usertoken).xpath("/usertoken/application[@ID=\"234\"]/role[@name=\"Employee\"");
```
(Example using Apache HTTP Components Fluent API and jOOX Fluent API)


Installation
============



* create a user for the service

* create update-service.sh
```
#!/bin/sh

A=TestWebApp
V=SNAPSHOT


if [[ $V == *SNAPSHOT* ]]; then
   echo Note: If the artifact version contains "SNAPSHOT" - the artifact latest greatest snapshot is downloaded, Irrelevant of version number!!!
   path="http://mvnrepo.cantara.no/content/repositories/snapshots/net/whydah/sso/web/$A"
   version=`curl -s "$path/maven-metadata.xml" | grep "<version>" | sed "s/.*<version>\([^<]*\)<\/version>.*/\1/" | tail -n 1`
   echo "Version $version"
   build=`curl -s "$path/$version/maven-metadata.xml" | grep '<value>' | head -1 | sed "s/.*<value>\([^<]*\)<\/value>.*/\1/"`
   JARFILE="$A-$build.jar"
   url="$path/$version/$JARFILE"
else #A specific Release version
   path="http://mvnrepo.cantara.no/content/repositories/releases/net/whydah/sso/web/$A"
   url=$path/$V/$A-$V.jar
   JARFILE=$A-$V.jar
fi

# Download
echo Downloading $url
wget -O $JARFILE -q -N $url


#Create symlink or replace existing sym link
if [ -h $A.jar ]; then
   unlink $A.jar
fi
ln -s $JARFILE $A.jar
```


* create start-service.sh
```
#!/bin/sh
nohup /usr/bin/java -DIAM_MODE=TEST -DIAM_CONFIG=/home/TestWebApp/testwebapp.TEST.properties -jar /home/TestWebApp/TestWebApp.jar
```


* create testwebapp.TEST.properties
```
applicationname=WhydahTestWebApplication
applicationid=99
applicationsecret=33879936R6Jr47D4Hj5R6p9qT

standalone=false
myuri=http://localhost:9990/test/
logonservice=http://localhost:9997/sso/
tokenservice=http://localhost:9998/tokenservice/
```



Developer info
==============

* https://wiki.cantara.no/display/iam/Architecture+Overview
* https://wiki.cantara.no/display/iam/Key+Whydah+Data+Structures
* https://wiki.cantara.no/display/iam/Modules

If you are planning on integrating, you might want to run SecurityTokenService in DEV mode. This shortcuts the authentication.
You can manually control the UserTokens for the different test-users you want, by creating a file named t_<username>.token which
consists of the XML representation of the access roles++ you want the spesific user to expose to the integrated application.
