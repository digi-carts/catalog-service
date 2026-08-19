package com.digicart.catalog.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HttpStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private MvcResult lastResult;

    @When("I GET {string}")
    public void iGet(String path) throws Exception {
        lastResult = mockMvc.perform(get(path).accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    @When("I GET {string} with store header {string}")
    public void iGetWithStore(String path, String storeId) throws Exception {
        lastResult = mockMvc.perform(get(path).header("x-store-id", storeId).accept(MediaType.APPLICATION_JSON)).andReturn();
    }

    @When("I POST {string} with body {string}")
    public void iPost(String path, String body) throws Exception {
        lastResult = mockMvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body)).andReturn();
    }

    @Then("the response status is {int}")
    public void theResponseStatusIs(Integer code) throws Exception {
        status().is(code).match(lastResult);
    }

    @Then("the JSON field {string} is {string}")
    public void jsonFieldIs(String field, String value) throws Exception {
        jsonPath("$." + field, is(value)).match(lastResult);
    }
}
