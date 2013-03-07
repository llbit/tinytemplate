tinytemplate
============

A tiny template engine.

Features:

* Multiple templates per file
* Variable and attribute expansion
* Conditional expansion
* Subtemplate inclusion
* Dynamic indentation

Copyright & License
-------------------

tinytemplate is copyright (c) 2013 Jesper Öqvist, <jesper@cs.lth.se>.
All rights reserved.

tinytemplate is provided under the terms of the modified BSD license, which
you can find in the file named LICENSE.txt included in this software.

Examples
--------

    # A template file contains several named templates.
    # Each template begins with [[ and ends with ]]
    greeting1 = [[Hello!]]
    
    # The = after the template name is optional
    # Variables are referenced using a $
    greeting2 [[Hello $name!]]
    
    # Parenthesis must be used if the variable name contains a period
    greeting3 = [[Hello $(your.name)!]]
    
    # Conditional expansion can be done using if statements
    dog.bark = [[
    $if(Happy)
      Woof!
    $endif
    ]]
    
    # It is possible to include a subtemplate
    greeting4 = [[She said "$include(greeting3)"]]
