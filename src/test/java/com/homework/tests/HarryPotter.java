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
}

















