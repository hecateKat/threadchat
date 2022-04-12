
# Threadchat

Application made for Sages

Java 11 was used for this project.

Lombok's library was used.



## Starting application

Class Server in main directory has main method that is used to start the server.

Class Client in main directory has main method that is used to start client. To be able to start multiple clients please click "Allow multiple instances" in Modify options of Client configuration.



## Starting application

Class Server in main directory has main method that is used to start the server.

Class Client in main directory has main method that is used to start client. To be able to start multiple clients please click "Allow multiple instances" in Modify options of Client configuration.



## File catalog

In need of sending file from one subject to another a catalog must be created. To use the path of the catalog please type it in static field fileStorePath in Configuration class which is in shared directory.

In need of getting history-file you need to create catalog that will be used in static field chatContentFilePath in Configuration class which is in shared directory.

Known issue: There is a possibility that restart server might throw exception. In that case, please delete history-file.


## Possible options to use
In order to use chat please consider using options below:

!general - opens general chat

!private - creates private chat. You will be asked to name it.

!joinp - joining private chat. You will be asked to name which one.

!send - sending file

!q - quit chat