[![Build Status](https://drone.io/github.com/dmillerw/MCPInterface/status.png)](https://drone.io/github.com/dmillerw/MCPInterface/latest)

####A couple notes
* Class names can be defined with the obfuscated name, the fully qualified class name, or the class name itself

 * _Example: bmh, net/minecraft/client/renderer/Tessellator, net.minecraft.client.renderer.Tessellator, and Tessellator, will all point to Tessellator.class_
 
* Field and method names can be defined with the obfuscatd name, the srg name, or the deobfuscated name

 * _Example: l, field\_78399\_n, and hasColor all point to the hasColor field_
 * _Example: a, func\_78377\_a, and addVertex all point to the addVertex method_

* When searching for methods, there may be multiple methods that match your search. MCPInterface will display _all_ methods that match the query

####Commands
|Command|Alias|Usage|Description|
|-------|-----|-----|-----------|
|get-type|gt|get-type CLASS|Retrieves mappings for the specified class|
|get-field|gf|get-field CLASS.FIELD|Retrieves mappings for the specified field from the specified class|
|get-method|gm|get-method CLASS.METHOD|Retrives mappings for the specified method from the specified class|
|set-branch|branch|set-branch BRANCH|Sets the branch of the FML repo to pull mappings from|
|help|help|help|Shows a basic help screen|
|quit|exit|quit|Closes the program|
