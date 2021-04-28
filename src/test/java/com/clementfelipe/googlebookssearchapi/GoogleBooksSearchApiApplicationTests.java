package com.clementfelipe.googlebookssearchapi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.clementfelipe.googlebookssearchapi.controller.VolumeDTO;
import com.google.api.services.books.v1.model.Volume;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class GoogleBooksSearchApiApplicationTests {

	private static final String USER_TOKEN = "ya29.a0AfH6SMDjNwQGx98-nU21PGXuz8hUcr7R7SaatIab12aZ3NvIYMPSt7G6-qb7P-Nd6ZcySemtDko1e-vDF5i63blcgLa0jPdoi6zy2aiUORnyWNA3t5WQeLfudqhuuUmA8iMpHmHddTB-DP2D0e5UMTHKgrjPHA"; // This should be an OAuth2 access token granted by google for the app

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	/**
	 * This is an end to end test for all the applications endpoints, it performs the following:
	 * 1. Unauthenticated search and validates the first result has the expected id and is not marked as favorite
	 * 2. Marks the volume as favorite for the given user (this uses the token constant above, which must be provided)
	 * 3. Authenticated search with the same query and validates the result is marked as favorite
	 * 4. Removes the volume from the favorites list
	 * 5. Validates the volume has been removed by checking the users specific Favorites bookshelf
	 * 
	 * This test may occasionally fail as the Books API may decide to return results out of the normal order, the fix
	 * for this would be to enable proper sorting on this API, but due to time constraints this will not be implemented.
	 * 
	 * An automation for obtaining the access token would also be necessary for this to be truly automated,
	 * but due to time constraints this is left out in favor of providing an access token manually.
	 * @throws Exception
	 */
	@Test
	public void greetingShouldReturnDefaultMessage() throws Exception {
		
		// Validate search returns results
		
		String searchUrl = "http://localhost:" + port + "/volume?query=domain+driven+design";

		ResponseEntity<List<LinkedHashMap<String, Object>>> volumes = (ResponseEntity<List<LinkedHashMap<String, Object>>>) restTemplate.getForEntity(searchUrl, new ArrayList<LinkedHashMap<String, Object>>().getClass());

		String expectedFirstVolumeId = (String) volumes.getBody().get(0).get("id");
		boolean expectedFirstVolumeFavorite = (boolean) volumes.getBody().get(0).get("isUserFavorite");

		assertEquals("FiGgIw2brFwC", expectedFirstVolumeId);
		assertFalse(expectedFirstVolumeFavorite);

		// Use first volume id to mark as favorite for the user
		
		HttpHeaders authorizationHeaders = new HttpHeaders();
		authorizationHeaders.set("authorization", "Bearer " + USER_TOKEN);

		HttpEntity<Void> authorizationEntity = new HttpEntity<>(null, authorizationHeaders);

		String markAsFavoriteUrl = "http://localhost:" + port + "/volume/" + expectedFirstVolumeId + "/favorite/true";

		ResponseEntity<Void> markAsFavoriteResponse = restTemplate.postForEntity(markAsFavoriteUrl, authorizationEntity, Void.class);

		assertEquals(200, markAsFavoriteResponse.getStatusCode().value());

		// Validate first result is favorite

		ResponseEntity<List<LinkedHashMap<String, Object>>> authenticatedVolumes = (ResponseEntity<List<LinkedHashMap<String, Object>>>)
			restTemplate.exchange(searchUrl, HttpMethod.GET, authorizationEntity, new ArrayList<LinkedHashMap<String, Object>>().getClass(), new Object[0]);

		String expectedAuthenticatedFirstVolumeId = (String) authenticatedVolumes.getBody().get(0).get("id");
		boolean expectedAuthenticatedFirstVolumeFavorite = (boolean) authenticatedVolumes.getBody().get(0).get("isUserFavorite");

		assertEquals("FiGgIw2brFwC", expectedAuthenticatedFirstVolumeId);
		assertTrue(expectedAuthenticatedFirstVolumeFavorite);

		// Use first volume id to remove from favorites
		String removeFavoriteUrl = "http://localhost:" + port + "/volume/" + expectedFirstVolumeId + "/favorite/false";

		ResponseEntity<Void> removeFavoriteResponse = restTemplate.postForEntity(removeFavoriteUrl, authorizationEntity, Void.class);

		assertEquals(200, removeFavoriteResponse.getStatusCode().value());

		// Validate volume is no longer in favorites

		String bookshelfUrl = "http://localhost:" + port + "/bookshelf/0/volume";

		ResponseEntity<List<LinkedHashMap<String, Object>>> bookshelfVolumes = (ResponseEntity<List<LinkedHashMap<String, Object>>>)
			restTemplate.exchange(bookshelfUrl, HttpMethod.GET, authorizationEntity, new ArrayList<LinkedHashMap<String, Object>>().getClass(), new Object[0]);

		Set<String> favoritesIdSet = bookshelfVolumes.getBody().stream()
			.map(v -> (String) v.get("id")).collect(Collectors.toSet());

		assertFalse(favoritesIdSet.contains("FiGgIw2brFwC"));
	}

}
