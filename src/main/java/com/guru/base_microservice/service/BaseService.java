package com.guru.base_microservice.service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;

import com.guru.base_microservice.domain.BaseEntity;
import com.guru.base_microservice.repository.BaseRepository;

public class BaseService<DTO extends BaseEntity, ENTITY extends BaseEntity, ID> {

	private final Class<DTO> dtoType;

	private final Class<ENTITY> entityType;

	public BaseService(Class<DTO> dtoType, Class<ENTITY> entityType) {
		this.dtoType = dtoType;
		this.entityType = entityType;
	}

	@Autowired
	private UtilServiceImpl utils;

	public Optional<DTO> createObject(DTO objectDto, BaseRepository<ENTITY, ID> repo) {
		ENTITY savedObject = repo.save(JacksonUtils.convertValue(objectDto, entityType));
		return Optional.of(JacksonUtils.convertValue(savedObject, dtoType));
	}

	public Page<DTO> getPageableObject(Integer page, Integer per_page, Map<String, String> order,
			BaseRepository<ENTITY, ID> repo) {
		Page<ENTITY> pageableObject = null;
		if (order != null) {
			Sort sort = utils.buildOrder(order);
			pageableObject = repo.findByDeactivatedAtIsNull(PageRequest.of(page, per_page, sort));
		} else
			pageableObject = repo.findByDeactivatedAtIsNull(PageRequest.of(page, per_page));
		Page<DTO> pageableObjectDTO = pageableObject.map(new Function<ENTITY, DTO>() {
			@Override
			public DTO apply(ENTITY entity) {
				return JacksonUtils.convertValue(entity, dtoType);
			}
		});
		return pageableObjectDTO;
	}

	public Optional<DTO> findOne(ID id, BaseRepository<ENTITY, ID> repo) {
		Optional<ENTITY> optionalObject = repo.findByUuidAndDeactivatedAtIsNull(id);
		Optional<DTO> optionalObjectDto = optionalObject.map(new Function<ENTITY, DTO>() {
			@Override
			public DTO apply(ENTITY entity) {
				return JacksonUtils.convertValue(entity, dtoType);
			}
		});
		return optionalObjectDto;
	}

	public Optional<DTO> update(ID id, DTO objectDto, BaseRepository<ENTITY, ID> repo) {
		ENTITY aux = JacksonUtils.convertValue(objectDto, entityType);
		ENTITY savedObject = repo.findByUuidAndDeactivatedAtIsNull(id).get();
		aux.setCreatedAt(savedObject.getCreatedAt());
		aux.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
		aux.setUuid(savedObject.getUuid());
		savedObject = repo.save(aux);
		return Optional.of(JacksonUtils.convertValue(savedObject, dtoType));
	}

	public void delete(ID id, BaseRepository<ENTITY, ID> repo) throws NullPointerException {
		ENTITY object = repo.findByUuidAndDeactivatedAtIsNull(id).get();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try {
			object.setDeactivatedAt(timestamp);
			repo.save(object);
		} catch (Exception e) {
			throw new NullPointerException("Object doesn't exist or was deleted.");
		}
	}

	public HttpHeaders buildPageableHeaders(Page<DTO> pageableObject, HttpServletRequest request) {
		String host = null;
		Integer page = pageableObject.getNumber() + 1;
		Integer perPage = pageableObject.getSize();
		if (request.getServerPort() == 80 || request.getServerPort() == 443)
			host = request.getServerName() + request.getRequestURI();
		else
			host = request.getServerName() + ":" + request.getServerPort() + request.getRequestURI();
		String previous = host;
		String first = host + "?page=1&per_page=" + perPage; 
		String actual = host + "?page=" + page + "&per_page=" + perPage;
		String next = host;
		if(pageableObject.hasPrevious())
			previous = previous + "?page=" + (page - 1) + "&per_page=" + perPage;
		else
			previous = actual;
		if(pageableObject.hasNext())
			next = next + "?page=" + (page + 1) + "&per_page=" + perPage;
		else
			next = actual;
		String link = "<" + first + ">;rel\"first\",<" + previous + ">;rel\"prev\",<" + next + ">;rel\"next\"";
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Page", page.toString());
		headers.add("X-Per-Page", perPage.toString());
		headers.add("Link", link);
		return headers;
	}

}
