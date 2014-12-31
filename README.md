pedal-loader
============

A DSL for data loading that is particularly suited for unit-testing with the database. The load scripts work at the JPA entity level (not database columns and values but JPA entity properties and "converted" types). 

## Getting started

Download the Pedal jar from Maven central:

```
	<groupId>com.eclecticlogic</groupId>
	<artifactId>pedal-loader</artifactId>
	<version>1.0.0</version>
```

Minimum dependencies that you need to provide in your application:

1. slf4j (over logback or log4j) v1.7.7 or higher
4. hibernate-core and hibernate-entitymanager 4.3 or higher.
5. JDBC4 compliant driver.
6. groovy-all.jar 2.3 or above

## Configuration

The only class that needs to be configured is the `LoaderImpl` which needs a reference to an `EntityManager`. If you are configuring your application using SpringBoot, add `"com.eclecticlogic.pedal.loader"` to the `@ComponentScan` package list to scan.
 
## Data Loading

The pedal data loader is accessed via the Loader interface. Create your load script as one or more groovy scripts in your classpath.

The Loader interface allows you to specify inputs to the script. The inputs are contained in a map of objects with String keys. The keys will be available as variables in your script.
The loader also allows you to specify a directory (in the classpath) where all your scripts reside. All references to scripts are relative to that directory.

Call the load method passing in one or more scripts to load and execute. Any inputs defined are passed to all the scripts. The loader also allows you to define namespaces so that output variables from scripts are separate. A variable called `x` created by a script with a namespace of `a` can be accessed in subsequent scripts as `a.x`. In the returned output `Map`, the key `"a"` has a map of variables, one of which is `x`. 

## Loader script format

The loader works using a typical groovy script with a special syntax for defining tables to load. The script can contain import statements, variable definitions, etc. Any properties defined in the script (essentially a variable without a def) will be available in the map returned by the Loader.load() method. This can be used to get specific objects or ids to test against.

### Table load definition

To insert rows into a table, use the table() method. The method takes two parameters and a closure:

Class reference of JPA entity
List of attributes you want to define.

The method returns a list of entities created in the closure.

The closure should have one more more lines of the format:

row << [<value1>, <value2>, ...]

The values are what you'd populate in the JPA entity, not in the database. So for a foreign key, you'd pass the @JoinColumn object. For a character field mapped to an Enum, you'd pass the actual Enum

Here is an example of a simple script to populate a table and then a child table:

```
	import com.test.School
    import com.test.Student
	import com.test.SchoolType
    import com.test.Gender

	table(School, ['name', 'type', 'address']) {
		row 'Lee Elementary', SchoolType.ELEMENTARY, '1 Lee Rd'
		row 'Park View School', SchoolType.MIDDLE, '10 Elm Street'
		highSchool = row 'Mountain Top High', SchoolType.HIGH, '12 Dream Street'
	} 

	println highSchool.id

    myStudents = table(Student, ['name', 'gender', 'school']) {
		row 'Joe Schmuckately', Gender.MALE, highSchool
    }

    // myStudents is a list of all the rows created.

	find(Student, 1)
```   

The above script creates three rows in the school table and also prints the database assigned id of the highSchool. The highSchool object can now be accessed from the map of variables returned by the load() method using the key "highSchool".  

Variables created in one script are available to the next script when multiple scripts are passed to the same load() call. The find method can be used to retrieve data that has been created by primary key.

### Default row

Sometimes you want to set certain columns of each row to the same value or the value can easily be defined as an expression. Pedal makes it easy to avoid repeating the value of the column in each row by defining a "default" row closure:

```
    def name = 'pedal'

    3.times { i ->
        tone = table (ExoticTypes, ['login', 'countries', 'authorizations', 'scores', 'custom']) {
            defaultRow {
                it.status = Status.ACTIVE
            }
            row "${name}_${i}", [true, false, true, false, false, false, false], ['create', 'update'], [1, 2, 3, 6, 10], 'abc'
        }
    }


```

### Custom functions

You can define custom functions that should be available within the load script. To define a custom-function, use the `.withCustomMethod` method on Loader passing in a closure:

```
        Map<String, Object> variables = loader //
                .withCustomMethod("doubler", new Closure<Object>(this) {

                    @Override
                    public Object call(Object... args) {
                        Integer i = (Integer) args[0];
                        return i * 2;
                    }
                }).withScriptDirectory("loader") //
                .load("customMethod.loader.groovy");

```  

`doubler` is now a custom-function that can be called within your script:


```

     myvar = doubler 200

```


See the src/test/resources/loader folder of the pedal-loader source for more examples.
