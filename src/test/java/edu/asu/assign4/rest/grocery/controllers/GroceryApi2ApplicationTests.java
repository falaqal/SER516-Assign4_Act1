package edu.asu.assign4.rest.grocery.controllers;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.asu.assign4.rest.grocery.services.GroceryServices;
@AutoConfigureMockMvc
@SpringBootTest
public class GroceryApi2ApplicationTests {

	@Autowired
	MockMvc mvc;

	@MockBean
	GroceryServices groceryServices;


	@Test
	void getGroceryItemByIdTest() throws Exception {
		mvc.perform(get("/api/groceries/MLK"))
				.andExpect(status().isOk())
				.andExpect(content().json("{'item':'milk'}"))
				.andExpect(content().json("{'id':'MLK'}"))
				.andExpect(content().json("{'price':3.99}"));
	}
	@Test
	void getGroceryItemByIdExceptionTest() throws Exception {
		final MvcResult result =  mvc.perform(post("/api/groceries")
				.content("{\"id\": \"MOZ\",\"item\": \"mozarella\",\"groceryType\": \"DAIRY\",\"price\": 6.55}")
				.contentType(MediaType.APPLICATION_JSON) // Specify the content type
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andReturn();
		final String responseBody = result.getResponse().getContentAsString();
		System.out.println("Response Body: " + responseBody); // Optional for debugging
		final JSONObject jsonObject = new JSONObject(responseBody);
		final String id =  jsonObject.getString("id");

		// testing if it was actually added
		mvc.perform(get("/api/groceries/"+id))
				.andExpect(status().isOk())
				.andExpect(content().json(responseBody)); // Combined JSON object
	}
}

