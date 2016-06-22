# DocBook

This project is a Java tool with JavaFX interface. It originates in a cakePHP website I created for my girlfrinds ostepathy self-employment. 
But because of privacy protection we decided to not have a web interface. Also I wanted to get to know Java 8, so I decided to create a 
stand-alone application in Java. 

Features: 
* store the patients and appointments
* store expenses (with date)
* export the data for the tax declaration
* write invoices from within the app
* show income

All the output texts are in German for now. When I have time I also want to integrate some kind of internationalization. 

The data currently loaded is purely fake. Every occurrence of real person names or information is absolutely **not** intentional! 

The database is [H2](http://www.h2database.com) at the moment. But might be changed at some point. 

The icons are from: 
Ravenna Icon Set by Webdesigner Depot
Design by Jack Cai
http://www.webdesignerdepot.com

![interface](screenshots/home.png?raw=true "Home")
![interface](screenshots/users.png?raw=true "Users")
![interface](screenshots/user.png?raw=true "User")