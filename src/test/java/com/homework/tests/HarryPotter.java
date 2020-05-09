package com.homework.tests;

import com.homework.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class HarryPotter {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("harry_potter_uri");
    }

    @Test
    @DisplayName("Verify sorting hat")
    public void test1(){
        Response response = given().accept(ContentType.JSON).
        when().get("/sortingHat").prettyPeek();
        response.then().assertThat().statusCode(200).
        and().contentType("application/json; charset=utf-8");
        List<String> listOfHat = new ArrayList<>(Arrays.asList("\"Gryffindor\"","\"Ravenclaw\"","\"Slytherin\"","\"Hufflepuff\""));
        assertTrue(listOfHat.contains(response.body().asString()));
//        given().accept(ContentType.JSON).
//        when().get("/sortingHat").prettyPeek();
//        response.then().assertThat().statusCode(200).
//        and().contentType("application/json; charset=utf-8")and().
//        body(anyOf(containsString("Gryffindor"),containsString("Ravenclaw")),
//                containsString("Slytherin"),containsString("Hufflepuff"));
    }

    @Test
    @DisplayName("Verify invalid key")
    public void test2() {
        given().accept(ContentType.JSON).queryParams("key","asdfghjkl").
        when().get("/characters").prettyPeek().
        then().assertThat().statusCode(401).
               assertThat().contentType("application/json; charset=utf-8").
                assertThat().statusLine(containsString("Unauthorized")).
                assertThat().body(containsString("\"error\":\"API Key Not Found\"") );
    }
    @Test
    @DisplayName("Verify no key")
    public void test3() {
        given().accept(ContentType.JSON).
                when().get("/characters").prettyPeek().
                then().assertThat().statusCode(409).
                assertThat().contentType("application/json; charset=utf-8").
                assertThat().statusLine(containsString("Conflict")).
                assertThat().body(containsString("\"error\":\"Must pass API key for request\"") );
    }

    @Test
    @DisplayName("Verify number of characters")
    public void test4() {
        Response response = given().accept(ContentType.JSON).queryParams("key",ConfigurationReader.getProperty("apiKey")).
                when().get("/characters").prettyPeek();
        response.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").
                assertThat().body("",hasSize(195));

        List<String> ids = response.jsonPath().getList("_id");
        assertEquals(195,ids.size());
        System.out.println(ids.get(194));
    }

    @Test
    @DisplayName("Verify number of character id and house")
    public void test5() {
        Response response = given().accept(ContentType.JSON).queryParams("key",ConfigurationReader.getProperty("apiKey")).
                when().get("/characters").prettyPeek();
        response.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").
                assertThat().body("_id", notNullValue()).
                assertThat().body("dumbledoresArmy",everyItem(is(oneOf(true,false))) ).
                assertThat().body("house",everyItem(is(oneOf("Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff"))));
    }
    @Test
    @DisplayName("Verify all character information")
    public void test6() {
        Response response1 = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                when().get("/characters").prettyPeek();
        response1.then().assertThat().statusCode(200).
                        assertThat().contentType("application/json; charset=utf-8");

        Map<String,?> character = response1.jsonPath().getMap("[194]");
        System.out.println(character);


        Response response2 = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                queryParams("name",character.get("name")).
                when().get("/characters");
        response2.then().assertThat().statusCode(200).
               assertThat().contentType("application/json; charset=utf-8").log().body(true);

        Map<String, ?> character1 = response2.jsonPath().getMap("[0]");
        System.out.println(character1);

        assertEquals(character,character1);

    }
    @Test
    @DisplayName("Verify name search")
    public void test7() {
        given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                queryParams("name","Harry Potter").
                when().get("/characters").
        then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").
                assertThat().body(containsString("Harry Potter")).
                log().body(true);
        System.out.println("-----------------------------------------------------------------");
        given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                queryParams("name","Marry Potter").
                when().get("/characters").
                then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").
                assertThat().body("",is(empty())).
                log().body(true);


    }

    @Test
    @DisplayName("Verify house members")
    public void test8() {
        Response response = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                when().get("/houses");
        response.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8");
        String idGryffindor = response.jsonPath().getString("_id[0]");
        List<String> listOfIds = response.jsonPath().getList("members[0]");
        System.out.println("-----------------------------------------------------------------");
        Response response2 = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                pathParams("id",idGryffindor).
                when().get("/houses/{id}");
        response2.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8");
        List<String> listofIds2 = response2.jsonPath().getList("[0].members._id");
        System.out.println(listofIds2);
        assertEquals(listOfIds,listofIds2);
    }

    @Test
    @DisplayName("Verify house members")
    public void test9() {
        Response response = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                pathParam("id", "5a05e2b252f721a3cf2ea33f").
                when().get("/houses/{id}");
        response.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").log().body(true);
        List<String> listOfIds = response.jsonPath().getList("[0].members._id");

        System.out.println("-----------------------------------------------------------------");
        Response response2 = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                queryParams("house","Gryffindor").
                when().get("/characters");
        response2.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").log().body(true);
        List<String> listofIds2 = response2.jsonPath().getList("_id");
        System.out.println(listOfIds.size()+" "+listofIds2.size());
        assertEquals(listOfIds,listofIds2);
    }

    @Test
    @DisplayName("Verify house with most members")
    public void test10() {
        Response response = given().accept(ContentType.JSON).
                queryParams("key",ConfigurationReader.getProperty("apiKey")).
                when().get("/houses");
        response.then().assertThat().statusCode(200).
                assertThat().contentType("application/json; charset=utf-8").log().body(true);
        List<String> listOfGryf = response.jsonPath().getList("members[0]");
        List<String> listOfRaven = response.jsonPath().getList("members[1]");
        List<String> listOfSlyt = response.jsonPath().getList("members[2]");
        List<String> listOfHuffl = response.jsonPath().getList("members[3]");
        System.out.println(listOfGryf.size()+" "+listOfHuffl.size()+" "+listOfRaven.size()+" "+listOfSlyt.size());
        assertTrue(listOfGryf.size()>listOfRaven.size() && listOfGryf.size()>listOfHuffl.size() && listOfGryf.size()>listOfSlyt.size());

    }


}

















