$compile = "javac -Xlint:all -d .\bin\ -cp '.\lib\javaMysqlORM\javaMysqlORM.jar' .\src\*.java .\src\Interface\Panels\*.java .\src\Interface\Utils\*.java "
$createJar = "jar -cfm gestorPassword.jar Manifesto.txt -C .\bin\ ."
$javaCommand = "java -jar gestorPassword.jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand