# trustgame-admin
### Admin web application for the TrustGame as part of the TransSonic project

The TrustGame is part of the PhD research of [ir. Anique Kuijpers](https://www.tudelft.nl/en/tpm/about-the-faculty/departments/multi-actor-systems/people/phd-candidates/ir-agj-anique-kuijpers). 
The game has been described in the paper [The Trust Game: The influence of Trust on Collaboration in the light of Technological Innovations](https://link.springer.com/chapter/10.1007/978-3-030-72132-9_15), and will be more extensively described in Anique's forthcoming PhD thesis. 
The game has been installed on one of the servers at TU Delft: [https://trustgame.tbm.tudelft.nl/trustgame](https://trustgame.tbm.tudelft.nl/trustgame). If you want access, or if you want to replicate the game on one of your own servers, contact Anique Kuijpers or Alexander Verbraeck. 

The trustgame has been written in Java and can be deployed in Tomcat. Currently, it has been tested and deployed with Tomcat 9. The game content itself, as well as player progress, scores, and logging are stored in a MySQL / mariadb database.

The trustgame consists of four web applications: [trustgame](https://github.com/averbraeck/trustgame) for the game play, [trustgame-admin](https://github.com/averbraeck/trustgame-admin) for the administration of the game (this project), [trustgame-scores](https://github.com/averbraeck/trustgame-scores) for viewing the scores of any running or finished game, and [trustgame-registration](https://github.com/averbraeck/trustgame-registration) for the self-registration of players. The game has been designed in such a way that no personal data (name, email address, etc.) has to be entered to play the game.


## Installation and development

The software has been optimized to be developed in [Eclipse IDE for Enterprise Java and Web Developers](https://www.eclipse.org/downloads/packages/) (current settings and project files are for version 2022-06). It runs with Java version 11. We used [OpenJDK 11](https://jdk.java.net/archive/) for the development. When the project is checked out to Eclipse, a [Tomcat 9](https://tomcat.apache.org/download-90.cgi) server needs to be installed in Eclipse for testing. See, e.g., this [GeeksforGeeks page](https://www.geeksforgeeks.org/configuration-of-apache-tomcat-server-with-eclipse-ide/) for instructions how to install Tomcat in Eclipse. A MySQL or mariadb database (we worked with [mariadb version 10.5](https://mariadb.org/download/?t=mariadb&p=mariadb&r=10.5.17)) has to be configured with the game database for testing and deployment. The database tables in the Java code have been auto-generated from the database with [jOOQ](https://www.jooq.org/). 



## The TransSonic project

The [TransSonic project](https://www.transsonic.nl/) is a project in the "Complexity in Logistics" call of NWO and the Top Institute Logistics in The Netherlands that started in July 2017, and actually ran till March 2022. The project researched the technical and organizational blockers and enablers, and their interaction, for providing integrated seamless multimodal transport services in the Netherlands. In such a system, a network of interdependent actors –on different transport modalities– have to work together and adapt in real time to changing demands of freight forwarders and shippers as well as changing availability of infrastructure and assets. We researched whether such services can emerge from the combination of social interactions between transport network organizations and novel, but already existing, technologies for sensing and for information exchange between partners, such as sensor networks, a blockchain ledger, and smart contracts. For the social interaction, simulation and gaming were used as the main research instrument to study inter-organizational interactions as well as intervention and reward mechanisms that would lead to new types of services. Two issues played a central role in the research: lack of situational awareness about the current state of the system, and the low level of trust between transport partners Both are known to be major blockers for organizing multimodal or synchromodal transport. The project therefore studied the effects of transport network information and new smart contracts between parties to (partly) overcome these issues. To develop and test the technological and organizational solutions, and their integration, a combination of simulation, gaming, data analysis and case studies in industry has been conducted. 