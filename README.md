# BUJO The Bullet Journal
### Version 1.1
![Banner](Banner.png)

This is a polished version of the final project for CS3500 Object Oriented Design, as
described in the [PA05 Write Up](https://markefontenot.notion.site/PA-05-8263d28a81a7473d8372c6579abd6481)
here.

This is a simple Bullet Journal application. It allows the users to create and edit
.bujo files, where each file holds a summary of a specific week's agenda. 
The app allows creating tasks and events, as well as editing and deleting the items 
already created. The main week view also provides a summary of all tasks within the week 
and a progress bar. The app also allows setting limits on how many tasks and events can be
scheduled within a specific day, notifying the user when they seem to be overbooked. Additionally,
the app implements keyboard shortcuts, hyperlink support in task and event descriptions, and optional
hashed password protection of .bujo files. Finally, the app plays a relaxing background music which
is meant to encourage concentration when journaling, but it may be turned off at any point with a 
keyboard shortcut.

### Keyboard Shortcuts:
- `ctrl + s` to save
- `ctrl + n` to create a new week
- `ctrl + e` to create a new event
- `ctrl + t` to create a new task
- `ctrl + o` to open a ".bujo" file
- `ctrl + d` to delete an event or task
- `ctrl + q` to save and quit
- `ctrl + m` to toggle background music on\off

### Jar File
The app is wrapped in a [Jar File](JournalApp.jar).

### Mock Weeks
Mock week file is provided in [mock-weeks](mock-weeks), it can be opened from a welcome screen 
upon app start up. The password is "123".

### Contributors
Team "Winston Ate My Bullet Journal"
[mlow102](https://github.com/mlow102) was responsible for the model and late-stage integration.
[totokang](https://github.com/totokang) was responsible for the fxml layout and visual flare.

## GUI:
![Wireframe](Wireframe.png)

### Media Attributions:
- [Book Logo](https://icons8.com/icon/l6iocFkbmCrh/book)
- [Welcome Page Background](https://imgur.com/gallery/bVovN)
- [Background music](https://pixabay.com/music/solo-piano-flaing-piano-main-8783/)
