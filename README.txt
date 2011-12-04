README.txt

This file exists at the top-level of the Rollarcus source tree. I'm slashing 
and burning through the source code and once things are stable I'll explain 
how you can get Rollarcus up and running.

CURRENT GOALS

- Remove unnecessary dependencies, e.g. Spring
- No changes to database schema
- No changes to user interface 
- No application server specific handling, except for possibly Jetty
- Jetty and Derby bundle

SUMMARY OF CHANGES SINCE INCEPTION

- Moved to one module instead of many
- Eliminated separate Planet web application
- Removed assemblies and documentation
