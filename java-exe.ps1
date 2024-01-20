$compile = "javac -d .\bin\ -cp '.\lib\java-mysql-eje\java-mysql-eje.jar' .\src\*.java .\src\Interface\Panels\*.java .\src\Interface\Utils\*.java "
$createJar = "jar -cfm gestorPassword.jar Manifesto.txt -C .\bin\ . -C .\extractionFiles\java-mysql-eje\ ."
$javaCommand = "java -jar gestorPassword.jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand