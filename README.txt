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
The phone numbers should be given in a text file, where each number
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
also provide the input text file that includes the phone numbers to be
checked.
If your phone number is already registered through the Signal Contacts
application, then by clicking next the input file is checked and the
output file is generated. A confirmation popup will be displayed to inform
you that the check has been completed.
In case that it is the first time you use the Signal Contacts application,
when clicking next, you will be sent an SMS with a confirmation code. You
should insert this confirmation code in the next screen, without the "-".
When you click next, the input file is checked and the output file is
generated. A confirmation popup will be displayed to inform
you that the check has been completed.

Output
------
The output consists of a text file which includes all the phone numbers
that belong to a Signal user.
The output file is created in the same directory with the application
executable.