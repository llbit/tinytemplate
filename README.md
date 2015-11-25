Tinytemplate
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

Tinytemplate is copyright (c) 2013-2015 Jesper Öqvist, <jesper.oqvist@cs.lth.se>.
All rights reserved.

Tinytemplate is provided under the terms of the modified BSD license, which
you can find in the file named LICENSE.txt included in this software.

Examples
--------

    # A template file contains several named templates.
    # Each template begins with [[ and ends with ]].
    greeting1 [[Hello!]]
    
    # Variables are referenced using a $.
    greeting2 [[Hello $name!]]
    
    # Parenthesis must be used if the variable name contains a period.
    greeting3 [[Hello $(your.name)!]]
    
    # Conditional expansion can be done using $if statements.
    dog.bark [[
    $if(Happy)
      Woof!
    $endif
    ]]
    
    # It is possible to include another template in your templates.
    greeting4 [[She said "$include(greeting3)"]]

    # Lists can be concatenated using $join.
    arguments [[ $join(#getArgumentList, ", ") ]]
    theStrings [[ $join($Strings) ]]

Coding Style
------------

Templates should be named according to this convention:

    MyClass.templateName:variant

* The name starts with the class name of the class that is used as context for
  attribute expansion.
* The next part is the base template name. This part should concisely identify
  what the template contains, or what it is used for.  For example
  `circularEquation`. If the template is only expanded by a single method in
  the context class you could use that method name as the base template name.
* It is common to have multiple variants of a template. To handle variants you
  can add a variant description after a colon at the end of the template name.
  For example `AttrDecl.equation:parameterized` for the parameterized variant
  of a JastAdd attribute equation template.
