package com.homework.tests;

import com.homework.utilities.ApiUtilities;
import com.homework.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Github_Api {

    @BeforeAll
    public static void setup() {
        baseURI = ConfigurationReader.getProperty("github_api_uri");
    }

    /**
     * 1.Send a get request to /orgs/:org. Request includes :•Path param org with value cucumber
     * 2.Verify status code 200, content type application/json; charset=utf-8
     * 3.Verify value of the login field is cucumber
     * 4.Verify value of the name field is cucumber
     * 5.Verify value of the id field is 320565
     */
    @Test
    @DisplayName("Verify organization information")
    public void test1(){
        given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber").prettyPeek().
                then().assertThat().statusCode(200).
                                    contentType("application/json; charset=utf-8").
                                    body("login", is("cucumber")).
                                    body("name", is("Cucumber")).
                                    body("id",is(320565));
    }
    @Test
    @DisplayName("Verify error message")
    public void test2() {
        given().accept("application/xml").
                when().get("/orgs/Cucumber").prettyPeek().
                then().assertThat().statusCode(415).
                        contentType("application/json; charset=utf-8").
                        statusLine(containsString("Unsupported Media Type"));
    }

    @Test
    @DisplayName("Verify Number of repositories")
    public void test3() {
        Response response = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber").prettyPeek();
        int NumberOfRepos = response.jsonPath().getInt("public_repos");
        System.out.println(NumberOfRepos);

        Response response1 = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber/repos").prettyPeek();

        List<Object> repos = response1.jsonPath().get();
        assertEquals(NumberOfRepos, repos.size());
    }

    @Test
    @DisplayName("Verify Repository id information")
    public void test4() {
        Response response = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber/repos").prettyPeek();

        List<Integer> id = response.jsonPath().getList("id");
        List<String> node_id =response.jsonPath().getList("node_id");
        System.out.println(id);
        System.out.println(node_id);
        assertFalse(ApiUtilities.hasDuplicates(id), "Not unique");
        assertFalse(ApiUtilities.hasDuplicates(node_id), "Not unique");
    }
    @Test
    @DisplayName("Verify Repository id information")
    public void test5() {
        Response response = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber").prettyPeek();

        int id = response.jsonPath().getInt("id");

        Response response2 = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber/repos");

        List<Integer> id2 = response2.jsonPath().get("owner.id");
        System.out.println(id2);

        for(Integer each:id2){
            assertEquals(id, each);
        }
    }
    @Test
    @DisplayName("Verify Ascending order by full_name sort")
    public void test6() {
        Response response = given().accept(ContentType.JSON).
                queryParam("sort", "full_name").
                when().get("/orgs/Cucumber/repos").prettyPeek();

        List<String> name = response.jsonPath().get("name");

        for(int i=0; i<name.size()-1; i++){
            assertTrue(name.get(i).compareTo(name.get(i+1))<0);
        }
    }

    @Test
    @DisplayName("Verify Descending order by full_name sort")
    public void test7() {
        Response response = given().accept(ContentType.JSON).queryParam("sort", "full_name").
                queryParam("direction", "desc").
                when().get("/orgs/Cucumber/repos").prettyPeek();
        List<String> name = response.jsonPath().get("name");
        for(int i=0; i<name.size()-1; i++){
            assertTrue(name.get(i).compareTo(name.get(i+1))>0);
        }
    }

    @Test
    @DisplayName("Verify Default sort")
    public void test8() {
        Response response = given().accept(ContentType.JSON).
                when().get("/orgs/Cucumber/repos").prettyPeek();
        List<String> name = response.jsonPath().get("created_at");

        for(int i=0; i<name.size()-1; i++){
            assertTrue(name.get(i).compareTo(name.get(i+1))>0);
        }
    }

}
