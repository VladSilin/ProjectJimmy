# ProjectJimmy

This is an ambient sound playback server using the Python Flask micro-framework. 
The program is able to randomly select and play the given sounds at random times within the given intervals. 
An Android application built for interaction with the server is included.

Server Features:

- Storage of references to sound files and their required properties in a SQLite database
- Ability to start/stop sound playback and mainitain server operation during playback
- Customizable sound playback intervals
- Ability to add/delete sound files in real time, while maintaining correct database state
- Ability to enable/disable playback of certain sounds


Android Application Features:

- Start/stop sound playback while maintaining synchronization with server state
- Ability to set sound intervals with necessary invalid input checking
- Enable/disable playback of individual sounds
- Ability to refresh application state to accommodate interval changes, enabling/disabling of sounds, 
  sound addition/removal, etc.
- User-friendly design, following Android Material Design guidelines:
  - RecyclerView populated with CardViews for list display
  - "Pull down to refresh" functionality
  - Use of CoordinatorLayout for smooth scrolling animations involving the ToolBar and FloatingActionButton
