$compile = "javac -Werror -Xlint:all -d .\bin\ -cp '.\lib\javaMysqlORM\javaMysqlORM.jar' .\src\*.java .\src\Interface\Panels\*.java .\src\Interface\Utils\*.java  .\src\*.java -sourcepath .\src\"
$createJar = "jar -cfm gestorPassword.jar Manifesto.txt -C .\bin\ ."
$javaCommand = "java -jar gestorPassword.jar"
$runCommand = "$compile" + " && " + "$createJar" + " && " +"$javaCommand"
Invoke-Expression $runCommand 
