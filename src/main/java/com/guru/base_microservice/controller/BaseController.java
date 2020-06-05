package com.guru.base_microservice.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.guru.base_microservice.domain.BaseEntity;
import com.guru.base_microservice.repository.BaseRepository;
import com.guru.base_microservice.service.BaseService;

public abstract class BaseController<DTO extends BaseEntity, ENTITY extends BaseEntity, ID> {

	private Logger logger = LoggerFactory.getLogger(BaseController.class);

	protected BaseRepository<ENTITY, ID> repo;

	private final String errorMessage = "An error has occurred.";

	protected final BaseService<DTO, ENTITY, ID> service;

	public BaseController(BaseRepository<ENTITY, ID> repo, Class<DTO> dtoType, Class<ENTITY> entityType) {
		this.repo = repo;
		this.service = new BaseService<DTO, ENTITY, ID>(dtoType, entityType);
	}

	// Controller health check
	@GetMapping(path = "/health_check")
	public @ResponseBody ResponseEntity<?> getObject() {
		return ResponseEntity.ok("Ok");
	}

	// Create object controller
	@PostMapping(path = "")
	public ResponseEntity<?> createObject(@RequestBody @Valid DTO object) {

		logger.info("Request to create object");

		try {

			Optional<DTO> objectOptional = service.createObject(object, repo);

			logger.info("Object successfully created.");

			return new ResponseEntity<Optional<DTO>>(objectOptional, HttpStatus.OK);
		} catch (Exception e) {

			logger.error(e.getMessage());

			return new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Get all objects pageable
	@GetMapping(path = "")
	public @ResponseBody ResponseEntity<?> getPageableObject(
			@RequestParam(value = "page", defaultValue = "1") @Min(1) Integer page,
			@RequestParam(value = "per_page", defaultValue = "10") @Max(50) Integer per_page,
			@RequestParam(required = false) Map<String, String> order,
			HttpServletRequest request) {

		logger.info("Request to get page : {} per_page : {}", page, per_page);
		page = page - 1;

		try {

			Page<DTO> pageableObject = service.getPageableObject(page, per_page, null, repo);

			return new ResponseEntity<List<DTO>>(pageableObject.getContent(),
					service.buildPageableHeaders(pageableObject, request), HttpStatus.OK);
		} catch (Exception e) {

			logger.error(e.getMessage());

			return new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Get object by id
	@GetMapping("/{id}")
	public ResponseEntity<?> findOne(@PathVariable ID id) {

		logger.info("Request to get object : {}", id);

		try {

			Optional<DTO> object = service.findOne(id, repo);

			return new ResponseEntity<Optional<DTO>>(object, HttpStatus.OK);

		} catch (NoSuchElementException e) {
			return new ResponseEntity<String>(errorMessage, HttpStatus.NOT_FOUND);
		} catch (Exception e) {

			logger.error(e.getMessage());

			return new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Update object by id
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable ID id, @RequestBody DTO object) {

		logger.info("Request to update object : {}", id);

		try {

			Optional<DTO> objectOptional = service.update(id, object, repo);

			logger.info("Object successfully updated.");

			return new ResponseEntity<Optional<DTO>>(objectOptional, HttpStatus.OK);
		} catch (Exception e) {

			logger.error(e.getMessage());

			return new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// Delete object
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable ID id) {

		logger.info("Request to delete object : {}", id);

		try {

			service.delete(id, repo);

			logger.info("Object {} successfully deleted.", id);

			return new ResponseEntity<String>("Objet successfully deleted.", HttpStatus.OK);
		} catch (Exception e) {

			logger.error(e.getMessage());

			return new ResponseEntity<String>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
