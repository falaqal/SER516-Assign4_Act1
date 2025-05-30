package edu.asu.assign4.rest.grocery.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.asu.assign4.rest.grocery.model.Producer;
import edu.asu.assign4.rest.grocery.services.ProducerServices;

@RestController
@RequestMapping("/api/producers")
public class ProducerController {
	private ProducerServices __ProducerService = null;
	
	public ProducerController() {
		// get the service implementation we will use
		__ProducerService = ProducerServices.getProducerService();  // we should check if it is null
	}

	@GetMapping("/{abbreviation}")
	public Producer getProducer(@PathVariable("abbreviation") String abbreviation) throws Exception {
		return __ProducerService.findOne(abbreviation);
	}
	@GetMapping
	public List<Producer> getProducers() throws Exception {
		return __ProducerService.findAll();
	}
	
	@SuppressWarnings("unused")
	@PostMapping
	public ResponseEntity<Producer> createProducer(@RequestBody Producer pItem) throws Exception {
		final String rval = __ProducerService.create(pItem);
		final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(pItem.getAbbreviation())
                .toUri();
		final HttpHeaders headers = new HttpHeaders();   // note this is the Spring type, not the java.net type
		headers.set(HttpHeaders.LOCATION, location.toString());
		// need to add rval to the Location header
		return new ResponseEntity<Producer>(pItem, headers, HttpStatus.CREATED);
	}
	// Note that this PUT example is a little different than the other example we have - why?
	@PutMapping
	public ResponseEntity<Producer> updateProducer(@RequestBody Producer pItem) throws Exception {
		final boolean rval = __ProducerService.update(pItem);
		// need to add id to the Location header if a create was done, and change the response status
		if (rval) { // it was an update
			return new ResponseEntity<Producer>(pItem, HttpStatus.OK);
		} else {  // it was created
			final URI location = ServletUriComponentsBuilder
	                .fromCurrentRequest()
	                .path("/{id}")
	                .buildAndExpand(pItem.getAbbreviation())
	                .toUri();
			final HttpHeaders headers = new HttpHeaders();   // note this is the Spring type, not the java.net type
			headers.set(HttpHeaders.LOCATION, location.toString());
			return new ResponseEntity<Producer>(pItem, headers, HttpStatus.CREATED);
		}
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProducer(@PathVariable("id") String id) throws Exception{
		final boolean rval = __ProducerService.delete(id);
		// if true we return a 204, else we return a 404
		if (!rval) {
			// no such grocery item, return a 404
			return new ResponseEntity<String>("No such Grocery Item with id = " + id, HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<String>(id, HttpStatus.NO_CONTENT);
		}
	}
	
	// Exception handling
	// I always include this one - there are almost always some HTTP methods our resource does not respond to
	@ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException exc) {
		return new ResponseEntity<String>("Invalid method provided to ProducerController", HttpStatus.METHOD_NOT_ALLOWED);
	}
	// This is the catch-all, returns our 500-level error
	@ExceptionHandler(value = java.lang.Throwable.class) 
	public ResponseEntity<?> handleThrowable(java.lang.Throwable t) {
		return new ResponseEntity<String>(t.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
