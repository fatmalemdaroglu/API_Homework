package com.homework.tests;

import com.homework.utilities.ApiUtilities;
import com.homework.utilities.ConfigurationReader;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class UI_Names {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("baseURI");
    }

    @Test
    public void NoParamTest() {
        given().get().
                then().assertThat().statusCode(200).
                contentType("application/json; charset=utf-8").
                body("name", not(empty())).
                body("surname", not(empty())).
                body("gender", notNullValue()).
                body("region", notNullValue()).
                log().body(true);

    }

    @Test
    public void GenderTest() {
        given().queryParam("gender", "female").get().
                then().assertThat().statusCode(200).
                contentType("application/json; charset=utf-8").
                body("gender", is("female")).
                log().all(true);
    }

    @Test
    public void TwoParamTest() {
        given().queryParam("gender", "female").
                queryParam("region", "Turkey").get().
                then().assertThat().statusCode(200).
                contentType("application/json; charset=utf-8").
                body("gender", is("female")).
                body("region", is("Turkey")).
                log().all(true);
    }

    @Test
    public void InvalidGenderTest() {
        Response response = given().queryParam("gender", "unisex").get();
        response.then().assertThat().statusCode(400).
                statusLine("HTTP/1.1 400 Bad Request").
                contentType("application/json; charset=utf-8").
                body("error", containsString("Invalid gender")).
                log().all(true);

    }

    @Test
    public void InvalidRegionTest() {
        Response response = given().queryParam("region", "female").get();
        response.then().assertThat().statusCode(400).
                statusLine("HTTP/1.1 400 Bad Request").
                contentType("application/json; charset=utf-8").
                body("error", containsString("Region or language not found")).
                log().all(true);
    }
    @Test
    public void AmountAndRegionTest() {
        Response response = given().queryParam("region", "Turkey").
                queryParam("amount", "20").get();
        response.then().assertThat().statusCode(200).
                contentType("application/json; charset=utf-8").
                log().all(true);

        List<Object> listOfName = response.jsonPath().get();
        boolean hasDuplicates = ApiUtilities.hasDuplicates(listOfName);
        assertFalse(hasDuplicates, "List has some duplicates");

    }

    @Test
    public void ThreeParamsTest() {
        given().queryParam("region", "Turkey").
                queryParam("gender", "female").
                queryParam("amount", "2").get().
                then().assertThat().statusCode(200).
                                    contentType("application/json; charset=utf-8").
                                    body("region", everyItem(is("Turkey"))).
                                    body("gender", everyItem(is("female"))).
                        log().all(true);
    }

    @Test
    public void AmountCountTest() {
        given().queryParam("amount", "5").get().
                then().assertThat().statusCode(200).
                contentType("application/json; charset=utf-8").
                body("",hasSize(5)).
                        log().all(true);
    }
}