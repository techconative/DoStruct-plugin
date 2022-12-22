
# MapStruct Plugin

### About
  As we all know mapstruct  library can generate bean mapper classes automatically.This plugin helps in generating code for mapstruct <br>
that need to be placed above the conversion methods declared in mapper interface.<br>

### steps to add this plugin to ur ide
Click *Ctrl+Alt+s* shortcut on Intellij IDE to go plugins.
Go to *install from disk* options.
Import the downloaded  jar from disk. <br>
![to](Documents/to.png "to")

### How to Use The Plugin
- Select the xml code.
- press *Shift + `* shortcut On Xml , then code generated is on your clipboard.

> Note : works for following  xml Structure only  . Replace *?* and *classname*  accordingly.

```xml
<mapping>
<class-a> classname </class-a>
<class-b> classname </class-b>
<field>
<a>?</a>
<b>?</b>
</field>
.... //any number of fields
<field>
<a>?</a>
<b>?</b>
</field>  

<field-exclude>
<a>?</a>
<b>?</b>
</field-exclude>
.... //any number of field-excludes
<field-exclude>
<a>?</a>
<b>?</b>
</field-exclude>  
</mapping>
```
