package io.mongock.runner.standalone.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;


public class Client {

  @Id
  protected String id;

  @Field("name")
  protected String name;

  @Field("email")
  protected String email;

  @Field("phone")
  protected String phone;

  @Field("country")
  protected String country;

  @Field("deleted")
  protected boolean deleted;

  public Client() {
  }

  public Client(String name, String email, String phone, String country) {
    this();
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.country = country;
    this.deleted = false;
  }

  // setters returning 'this' for fluent use in stream. Shouldn't be taken as precedent
  public Client setId(String id) {
    this.id = id;
    return this;
  }

  public Client setName(String name) {
    this.name = name;
    return this;
  }

  public Client setEmail(String email) {
    this.email = email;
    return this;
  }

  public Client setPhone(String phone) {
    this.phone = phone;
    return this;
  }

  public Client setCountry(String country) {
    this.country = country;
    return this;
  }

  public Client setDeleted(boolean deleted) {
    this.deleted = deleted;
    return this;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getCountry() {
    return country;
  }


  public boolean isDeleted() {
    return deleted;
  }



  @Override
  public String toString() {
    return "Client{" +
            "name='" + name + '\'' +
            '}';
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Client client = (Client) o;
    return deleted == client.deleted &&
            Objects.equals(id, client.id) &&
            Objects.equals(name, client.name) &&
            Objects.equals(email, client.email) &&
            Objects.equals(phone, client.phone) &&
            Objects.equals(country, client.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, email, phone, country, deleted);
  }
}
