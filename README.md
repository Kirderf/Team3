# Photon

[This readme is meant as a simple introduction, for a full description, please visit our wiki-page](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/home)
## Getting started
### Installation
If you are only interested in running the executable, [a guide can be found on our installation manual page on our wiki.](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/System/Installation-manual) If you are interested in cloning the repo and developing further, please keep reading.

### .Properties

All the dependencies should be installed automatically using maven, Wo the first thing you need to do is create  .Properties file. You can do this by removing the name of, and changing the file extension to a normal .txt file. You can then open  your new .Properties fiile containing the following information after having cloned the repo:
```
URL = *The URL to your login* 
USERNAME=*your username*	
PASSWORD=*your password*
```
You only need to enter the part of the url that comes after " jdbc:mysql://mysql.stud.iie.ntnu.no:3306/ "
Your url will often be the same as your username

After you have entered the required information, you can log into the application if you are connected to an NTNU network

You can run the program by running the main method in start.java


### Usage

This is a list of features, instructions on how to use them can be found on [on our user manual page on the wiki](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/System/User-manual) 
The program is capable of:
 
 * The user may create a new account with a unique username and password. The password is hashed and these are stored in the database
 * Importing several images and showing these in a grid view
 * Sorting the imported images, based on either Filename, path, date, or filesize
 * The user can select any images by ctrl + click, and the program will show the metadata and any added tags of the most recently selected image
 * Showing any of the images by themselves having them occupy the entire grid view
 * When viewing a single image the user can add or remove tags by clicking the edit tags button in the top left corner
 * The user can search for tags or metadata
 * The user can view a map of all the images with valid gps data, the images here can be clicked to be shown
 * The user can save and view sets of images as albums
 * The user can remove several images at once from the program
 * The user can export all the selected images to a single pdf and select a directory to place this
 * The user may enable text-to-speech and colourblind mode

## Help
Our full wiki with diagrams to explain our codebase and other help after having cloned the repository can be found [here](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/home)

Our javadoc can be found [here](http://team3-2020.pages.stud.idi.ntnu.no/systemutvikling/)

If you need help with running the executable, please consult our [installation manual](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/System/Installation-manual)

## Licenses
[Licenses to foreign code used in the program can be found on our Licenses wiki page](https://gitlab.stud.idi.ntnu.no/team3-2020/systemutvikling/-/wikis/Licenses) 