tinytemplate
============

A tiny template engine.

* Templates only contain text and variable references
* Variables are reset after each expansion (if `PersistentVariables` is not set to `true`)

Examples
--------

    # A template file contains several named templates.
    # Each template begins with [[ and ends with ]]
    msg.hello.0 = [[Hello there!]]
    
    # The = after the template name is optional
    # Variables are referenced using a $
    msg.hello.1 [[Hello $name!]]
    
    # Braces must be used if the variable name contains special characters (incl. period)
    msg.hello.2 = [[Hello ${your.name}!]]
