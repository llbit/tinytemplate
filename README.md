tinytemplate
============

A tiny template engine.

* Multiple templates per file
* Line comments using `#` outside of template bodies
* Templates only contain text and variable (`$`) or attribute references (`#`)
* Indentation is preserved

Copyright & License
-------------------

Copyright (c) 2013 Jesper Öqvist <jesper@llbit.se>

tinytemplate is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

tinytemplate is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with tinytemplate.  If not, see <http://www.gnu.org/licenses/>.

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
