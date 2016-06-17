pedal-loader
============

A Groovy DSL for data loading that is particularly suited for database unit-testing. The load scripts work at the JPA entity level (not database columns and values but JPA entity properties and higher-level types). 

## Getting started

Download the pedal-loader jar from Maven central:

```
	<groupId>com.eclecticlogic</groupId>
	<artifactId>pedal-loader</artifactId>
	<version>1.0.2</version>
```

Minimum dependencies that you need to provide in your application:

1. slf4j (over logback or log4j) v1.7.7 or higher
2. Spring boot jpa edition or
4. hibernate-core and hibernate-entitymanager 4.3 or higher.
5. JDBC4 compliant driver.
6. groovy-all.jar 2.3 or above


### Configuration 

#### Vanilla setup

For classpath-based loading, create an instance of `Loader` (concrete class `com.eclecticlogic.pedal.loader.impl.LoaderImpl`) and give it a reference to an `EntityManager`.  For filesystem-based loading, create an instance of `com.eclecticlogic.pedal.loader.impl.FileSystemLoaderImpl` instead of `com.eclecticlogic.pedal.loader.impl.LoaderImpl`.

#### Spring 

If you are using @ComponentScan, include the `com.eclecticlogic.pedal.loader` package in the packages to be scanned. If you are using a Spring managed unit test, setup your test as shown below:

```
     @RunWith(SpringJUnit4ClassRunner.class)
     @SpringApplicationConfiguration(classes = JpaConfiguration.class)
     public class MyDatabaseTest {

    	@Autowired
    	private Loader loader;


	    @Test
    	@Transactional
	    public void myTest() {
            Map<String, Object> variables = loader //
                .withScriptDirectory("<directory-with-scripts>") // Classpath resource dir
                .load("script-reference"); 
            ...
        }
    }
```
  
 
## Data Loading

The pedal data loader is accessed via the Loader interface. Create your load script as one or more groovy scripts in your classpath.

The Loader interface provides a number of methods to specify your script and input variables for the script. Here are some ways to launch the script:

```
    loader.withScriptDirectory("myScripts").load("basicdata.groovy");
```
This specifies that the loader should read the script basicdata.groovy contained within a classpath or filesystem directory called myScripts. The file could just as well have been specified with the directory as `myScripts\basicdata.groovy`. However, the withScriptDirectory allows you to setup a well known directory and have all other calls simply reference the script by name without worry about relative paths.

### Script Format

The scripts are simple groovy files. To insert rows into a table, use the table() method. The method takes two parameters and a closure:

1. Class reference of JPA entity
2. List of attributes names you want to populate.

The method returns a list of entities created in the closure.

The table method should have a closure should have one more more row lines:

```
   rowsCreated = table(MyEntity, ['id', 'name', 'age']) {
	row value1, value2, value3 ...
    row value1, value2, value3 ....
   }
```

The values are what you'd populate in the JPA entity, not in the database. So for a foreign key, you'd pass the @JoinColumn object. For a character field mapped to an Enum, you'd pass the actual Enum

Here is an example of a simple script to populate a table and then a child table (i.e., JPA entities called School and Student):

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

If your script expects say a starting primary key value, you can easily pass it as an input:

```
    Map<String, Object inputs = new HashMap<>();
    inputs.put("pkStart", 123);
   	loader.withInputs(inputs).load("basicdata.groovy", "secondScript.groovy");
```

The value is now available as a property called pkStart to the script (and to `secondScript.groovy` as well) and can be referenced directly:

```
    table(School, ['id', 'name', 'type', 'address']) {
		row pkStart++, 'Lee Elementary', SchoolType.ELEMENTARY, '1 Lee Rd'
		row pkStart++, 'Park View School', SchoolType.MIDDLE, '10 Elm Street'
		highSchool = row pkStart++, 'Mountain Top High', SchoolType.HIGH, '12 Dream Street'
	} 	
```    

Sometimes the groovy compiler can get confused in evaluating the syntax for the columns. To help the compiler, wrap the column values in parenthesis:

```
    table(School, ['id', 'name', 'type', 'address']) {
		row (pkStart++, 'Lee Elementary', SchoolType.ELEMENTARY, '1 Lee Rd')
		row (pkStart++, 'Park View School', SchoolType.MIDDLE, '10 Elm Street')
		highSchool = row (pkStart++, 'Mountain Top High', SchoolType.HIGH, '12 Dream Street')
	} 
```

Since the scripts are groovy based, you can use all groovy control structures to help you populate your data:

```
	table(MyValue, ['id', 'value']) {
		10.times { i ->
			row i, i * 50
    	}
	}
```

When testing your database functionality, sometimes you need reference to data that already exists. This is particularly important if the data has a generated PK. In the example above we created schools and created a reference to the high school in a variable called `highSchool`. This variable (strictly speaking, property) is automatically returned to the caller in a map. The key is a string with the value `highSchool` and the value is an instance of School. 

*Note: Do not declare highschool using a type or `def` as shown:*

```
    table(School, ['id', 'name', 'type', 'address']) {
		row (pkStart++, 'Lee Elementary', SchoolType.ELEMENTARY, '1 Lee Rd')
		row (pkStart++, 'Park View School', SchoolType.MIDDLE, '10 Elm Street')
		def highSchool = row (pkStart++, 'Mountain Top High', SchoolType.HIGH, '12 Dream Street')
	} 
```

This will make highSchool a local variable and it will not be returned to the caller. Of course, if you need to temporarily hold on to large collections that you don't want to return back to the calling script, then do mark them with a `def` or type.

If you are invoking multiple scripts and some of the scripts return values that are named the same, you can define namespaces for the scripts. If two scripts `myScript1.groovy` and `myScript2.groovy` both create a property called `x`, you can load the scripts with namespaces as shown:

```
	Map<String, Object> variables = loader.withInputs(inputs).load(Script.with("myScript1.groovy", "a"), Script.with("myScript2.groovy", "b");

    Map<String, Object> script1Vars = variables.get("a");
    Object x = script1Vars.get("x");
```

You can also call scripts from within scripts. Use the `load()` method. The load method accepts a list of script names or a map of `namespace:script-name`.

```
   	output = load('a': 'simple.loader.groovy', 'b': 'simple.loader.groovy')	
	assert output.a.simple1.amount == 20

	myIndex = 101
	inputReaderVars = withInput(['index': myIndex]).load('input.reader.groovy')
	assert inputReaderVars.inputReaderReturn.amount == 101000
``` 

where `simple.loader.groovy` is:

```
	import com.eclecticlogic.pedal.loader.dm.SimpleType
	
	table(SimpleType, ['amount']) {
	    simple1 = row 10
	    simple2 = row 20
	}
```  

and `input.reader.groovy` is:

```
	import com.eclecticlogic.pedal.loader.dm.SimpleType

	table(SimpleType, ['amount']) {
    	inputReaderReturn = simple1 = row (1000 * index)
	    simple2 = row 2000
	}
```

Variables created in one script are available to the next script when multiple scripts are passed to the same load() call. The find method can be used to retrieve data that has been created by primary key.

### Default row

Sometimes you want to set certain columns of each row to the same value or the value can easily be defined as an expression. Pedal makes it easy to avoid repeating the value of the column in each row by defining a "default" row closure:

```
    tone = table (MyTable, ['id', 'name', 'insertedOn']) {
        defaultRow {
            it.insertedOn = new Date()
        }
        row 1, 'Joe'
		row 2, 'Schmoe'
		row 3, 'Jane'
		row 4, 'Jack'
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

### Find and Flush

The find(Class, id) method is available to your DSL scripts to load records by JPA entity class and primary key. The flush() method does what you would think it should - flush the JPA session to the database.

See the src/test/resources/loader folder of the pedal-loader source for more examples.

### Release notes

# 1.1.0 

- Upgraded to use Hibernate 5.x and corresponding Pedal dialect 1.2

# 1.0.2

- Support for file system based script loading and associated refactoring by [csetera](https://github.com/csetera)

# 1.0.1 

- Fixed a failing unit test.
- Changed java_home reference in pom to standard value.