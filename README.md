# jcraft2
Rewrite of JCraft(no infinite terrain)

This is a simple minecraft clone I am working on just for educational purposes. I hope to get it functional enough so that
it can be a learning tool for kids(including my own). 

You run it by running the com.chappelle.jcraft.jme3.JCraft class. It is a mult-module maven project so you will need maven
installed in order to run it. Once downloaded, navigate a command prompt to the jcraft-parent folder and run maven install.
This will compile and build the code in the target folder. Then run JCraft.

An options file automatically gets created in the <user-home>/jcraft-options.txt file. Run the game once and close it and it
will put the default options in there and you can change them as you see fit.

Also, the framework used is JMonkey Engine 3(JME3). It is a gaming framework built on top of lwjgl. lwjgl is what minecraft was
built from.
