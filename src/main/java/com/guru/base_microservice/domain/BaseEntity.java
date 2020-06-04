package com.guru.base_microservice.domain;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@MappedSuperclass
public class BaseEntity {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "uuid", updatable = false, nullable = false)
	@Type(type = "uuid-char")
	// PK
	private UUID uuid;
	
	@CreationTimestamp
	@JsonProperty("created_at")
	@Column(name = "created_at")
	private Timestamp createdAt;

	@UpdateTimestamp
	@JsonProperty("updated_at")
	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@JsonProperty("deactivated_at")
	@Column(name = "deactivated_at")
	@JsonInclude(Include.NON_NULL)
	private Timestamp deactivatedAt;
	
	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Timestamp getDeactivatedAt() {
		return deactivatedAt;
	}

	public void setDeactivatedAt(Timestamp deactivatedAt) {
		this.deactivatedAt = deactivatedAt;
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String toString() {
		try {
			return objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
