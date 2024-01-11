# Gestor Password
>- simple GUI App for password management.
>- created using MYSQL, JAVA SWING and AWT.


# Dependencies
>- [java-mysql-orm](https://github.com/AlfonsoG-dev/java-mysql-eje)
>- [java-build-tool](https://github.com/AlfonsoG-dev/javaBuild)
>- [mysql JDBC](https://dev.mysql.com/downloads/connector/j/5.1.html)
>- [java-jdk-17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
>- [java_swing](https://www.tutorialspoint.com/swingexamples/index.htm)

# Features

- [x] loggin 
- [x] list, add, delete and update passwords
- [x] allow insert cuenta directly from table

# Usage

- use the user and password
- to use drag and drop to the table.
>- the drag text format must bee: 
```console
id,nombre,email,user_id_fk,password,null,null
```

![panelLogin](./docs/login.png)

------

- panel that contains the table and funcionallity
- the table have to the right side 3 button that represents: 
>- reload, insert row, delete row

![panelPrincipal](./docs/principal.png)

------

- if no new row has been inserted or the inserted row have no data, insert button redirect to another frame
- if you insert a row you must press enter when finish the register of the data

![panelRegistro](./docs/registro.png)

- a column must be selected before click update button

![panelUpdate](./docs/update.png)

------

# Aditional options to use

>- Create the database and tables.
>>- using the [java-mysql-orm](https://github.com/AlfonsoG-dev/java-mysql-eje)

```java

// create the DbConfig instance with the correspondent data
DbConfig miConfig = new DbConfig(dataBase, host, port, user, password);

// Create the DAO instance for each of the tables
// user table
new MigrationDAO("user", miConfig);

// cuenta table
new MigrationDAO("cuenta", miConfig);

```

>- insert manually the user for login purposes

```java
User admin = new User(Nombre, Email, Password, Rol);
admin.setCreate_at();
String condition = "nombre: " + admin.getNombre() + ", password: " + admin.getPassword();
new QueryDAO<User>("user", DbConfig).InsertNewRegister(admin, condition, "and", new UserBuilder());
```

# Compile and execute
>- using [java-build-tool](https://github.com/AlfonsoG-dev/javaBuild)
>>- if the javaBuild.exe is in path

>>- to compile
```console
javaBuild --build
```

>>- to execute
```console
java -jar app.jar
```

>- if using the powershell script
>>- Extraction of the jar file dependencies
1. download the *java-mysql-eje* proyect inside the lib folder of the proyect
2. inside java-musql-eje proyect create a jar file
3. create a folder with the name of *extractionFiles*
4. in the extractionFiles folder create a folder with the name *java_mysql_eje*
5. move the jar file to *extractionFiles/java_mysql_eje/* folder
6. extract the content of the jar file using the *java -xf command*

>>- Compile the proyect
1. using powershell create a *java-exe.ps1* file in the root of the proyect
2. create the script to compile and execute the program

``` powershell
$Clases = "./src/*.java ./src/Interface/Panels/*.java"
$Compilation = "javac -cp " + '"./bin/;path to the custom jar file" -d ./bin/ ' + "$Clases";
$javaCommand = "java -cp " + '"./bin;path to a custom jar file" .\src\App.java';
$runCommand = "$Compilation" + " & " + "$javaCommand"
Invoke-Expression $runCommand
```

# Disclaimer
>- this proyect is for educational purposes
>- securitty issues are not taken into account
