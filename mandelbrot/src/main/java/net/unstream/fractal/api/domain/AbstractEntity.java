package net.unstream.fractal.api.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.neo4j.annotation.GraphProperty;

public abstract class AbstractEntity {

	@CreatedDate
    @GraphProperty(propertyType = Long.class)
    private DateTime createdDate = DateTime.now();

    @LastModifiedDate
    @GraphProperty(propertyType = Long.class)
    private DateTime lastModifiedDate = DateTime.now();

    public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}

	public DateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(DateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

}