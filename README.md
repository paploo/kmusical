# kMusical
**(c)2021 Jeff Reinecke**

Library for doing computation with musical notes in Western Music conventions.

Support is only for the 12-tone theory, but different tunings are supported.


## Notes ##

AbsolutePitch
* Frequencies are grounded in the real world.
* Frequency Ratios are a relative "pitch" measurement for frequencies 
* Cents are 1:1 related to frequency ratios, and preferred in music literature.

Pitch
* Semitone is the common division of the scale.
* An interval is expressed in semitones *and* a number and quality for 12 tone systems, e.g. P4 or m3.
* A pitch is an interval from a reference, e.g. A4 (counted by semitones)  
* A tuning defines a mapping for pitch semitones to/from cents.
* TODO: Define a scale and key

* A note associates a pitch and a duration with other annotations.
* Score
* Staff