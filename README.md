tinytemplate
============

A tiny template engine.

Features:

* Multiple templates per file
* Line comments using `#` outside of template bodies
* Templates only contain text and variable (`$`) or attribute references (`#`)
* Indentation is applied to variable and attribute expansions

Copyright & License
-------------------

tinytemplate is copyright (c) 2013 Jesper Öqvist, <jesper@cs.lth.se>

tinytemplate is provided under the terms of the modified BSD license, which
you can find in the file named LICENSE.txt included in this software.

Examples
--------

    # A template file contains several named templates.
    # Each template begins with [[ and ends with ]]
    msg.hello.0 = [[Hello there!]]
    
    # The = after the template name is optional
    # Variables are referenced using a $
    msg.hello.1 [[Hello $name!]]
    
    # Parenthesis must be used if the variable name contains special characters (e.g. period)
    msg.hello.2 = [[Hello $(your.name)!]]
