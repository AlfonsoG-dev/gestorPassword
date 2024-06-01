$srcClases = ".\src\*.java .\src\Models\User\*.java .\src\Interface\Utils\*.java .\src\Interface\Panels\*.java .\src\Models\Cuenta\*.java "
$libFiles = ".\lib\javaORM\javaORM.jar;"
$compile = "javac -Werror -Xlint:all -d .\bin\ -cp '$libFiles' $srcClases"
$createJar = "jar -cfm gestorPassword.jar Manifesto.txt -C .\bin\ ."
$javaCommand = "java -jar gestorPassword.jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand 
