Do not use this package (or review the code thoroughly).

The OpenBeaconPackage package was an initial design trying to
combine proximity sightings and the default packet loss
in one tracking solution.

Unfortunately, this does not make much sense (if you have a 
fixed tag and another tag reports a proximity sighting you can 
be sure that the tags were near each other. Now the default 
approach kicks in and tells you that the tag is somewhere 
between reader A and reader B, which is about 50 m away. 
Where is your tag now?).

The package contains all used libraries with references
and source code at the time of writing. This is the
interesting part.

Björn Behrens
23.02.2014 