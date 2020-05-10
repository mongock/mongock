package com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.client;

import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.Mongock4Spring5SpringData3App;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME)
@CompoundIndexes({
    @CompoundIndex(def = "{'name':1, 'deleted':1}", name = "user_name_idx"),
    @CompoundIndex(def = "{'email':1, 'deleted':1}", name = "user_email_idx"),
    @CompoundIndex(def = "{'phone':1, 'deleted':1}", name = "user_phone_idx"),
    @CompoundIndex(def = "{'country':1, 'deleted':1, 'activation.status':1}", name = "user_country_activation_idx")
})
public class Client {

  @Id
  private String id;

  @Field("name")
  private String name;

  @Field("email")
  private String email;

  @Field("phone")
  private String phone;

  @Field("country")
  private String country;

  @Field("activation")
  private ActivationModel activation;

  @Field("deleted")
  private boolean deleted;

  public Client() {
  }

  public Client(String name, String email, String phone, String country) {
    this(name, email, phone, country, new ActivationModel());
  }

  public Client(String name, String email, String phone, String country, ActivationModel activation) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.country = country;
    this.activation = activation;
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

  public Client setActivation(ActivationModel activation) {
    this.activation = activation;
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

  public ActivationModel getActivation() {
    return activation;
  }

  public boolean isDeleted() {
    return deleted;
  }
}
