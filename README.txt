Signal Contacts application guide
=================================

Signal Contacts is an application that can be used to identify Signal 
Private Messenger users among given mobile phone numbers.

Configuration
-------------
Before running the application, you should configure your Java
installation to include the Bouncy Castle security provider.
This is done in the following way:

1) Go to http://www.bouncycastle.org/latest_releases.html and download
the latest signed JARs for your JDK

2) Copy the downloaded JARs in the following directory:

	$JAVA_HOME/jre/lib/ext

3) Edit the file

	$JAVA_HOME/jre/lib/security/java.security

In this file, there is a list of lines with "security.provider.X",
where X is some number. At the bottom of the list add the line

	security.provider.N=org.bouncycastle.jce.provider.BouncyCastleProvider

where N is the last number in the list incremented by one.

4) Copy the provided "whisper.store" file in the same directory with
the application executable.

Input
-----
The phone numbers can be given in multiple text files, where each number
is given in a separate line.

Example:

+442076304525
+447739803499
+923334122959
+4407781158270
+4408453633632

Running the application
-----------------------
The application is run by issuing the following command:

	java -jar SignalContacts.jar

In the first screen of the application, you should provide your mobile
phone number, in order to register in the Signal Messenger. You should
also provide the input text files that include the phone numbers to be
checked.
When clicking next, you will be sent an SMS with a confirmation code.
You should insert this confirmation code in the next screen, without
the "-". 
When you click next, the input files are checked and the output files 
are generated. 
A status popup will appear to display the progress of the application.
A confirmation popup will be displayed to inform you when the check
has been completed.

Output
------
The output consists of a text file which includes all the phone numbers
that belong to a Signal user.
The output file is created in the same directory with the application
executable.
Moreover, a stats.txt file is created to show statistics about each
checked input file.