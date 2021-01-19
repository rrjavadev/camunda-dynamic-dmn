# Camunda-dynamic-cmn
An example spring-boot project that dynamically generates a dmn file via a REST endpoint.<br/>
<p>To start the application run:</p>
``` mvn clean package spring-boot:run```

<p> The POST endpoint <b>http://localhost:8085/generate_and_deploy</b> generates and deploys a dmn file in a running Camunda process engine.</p>
<p>The dmn rules are generated on the fly using external variables. This can be enhanced to obtain the data fetched from the database.</p>

<p>To test whether the rules are working correctly, hit the POST endpoint <b>http://localhost:8080/run_decisions/dynamic_dmn</b></p>
Sample request body:

```
   {
   	"input1": 30,
   	"input2": 1,
   	"input3": "abc"
   }

```

<p>Note: Lombok is used in the project. Hence Lombok plugin will need to be installed in your IDE if you need to get rid of any compilation errors in your IDE. </p>
