$srcClasses = "src\application\*.java src\application\interfaces\panels\*.java src\application\interfaces\utils\*.java src\application\models\cuenta\*.java src\application\models\user\*.java "
$libFiles = ".\lib\javaORM_2.0\javaORM_2.0.jar;"
$compile = "javac --release 23 -Xlint:all -Xdiags:verbose -d .\bin\ -cp '$libFiles' $srcClasses"
$createJar = "jar -cfm GestorPassword.jar Manifesto.txt -C .\bin\ ."
$javaCommand = "java -jar GestorPassword.jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand 
