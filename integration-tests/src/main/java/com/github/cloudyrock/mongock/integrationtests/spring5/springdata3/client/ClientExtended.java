package com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.client;

import com.github.cloudyrock.mongock.integrationtests.spring5.springdata3.Mongock4Spring5SpringData3App;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Document(collection = Mongock4Spring5SpringData3App.CLIENTS_COLLECTION_NAME)
@CompoundIndexes({
    @CompoundIndex(def = "{'name':1, 'deleted':1}", name = "user_name_idx"),
    @CompoundIndex(def = "{'email':1, 'deleted':1}", name = "user_email_idx"),
    @CompoundIndex(def = "{'phone':1, 'deleted':1}", name = "user_phone_idx"),
    @CompoundIndex(def = "{'country':1, 'deleted':1, 'activation.status':1}", name = "user_country_activation_idx")
})
public class ClientExtended extends Client {

  @Field
  private ZonedDateTime dateTime;


  @Field("activation")
  private ActivationModel activation;

  public ClientExtended() {
    this.dateTime = ZonedDateTime.now();
  }

  public ClientExtended(String name, String email, String phone, String country) {
    this(name, email, phone, country, new ActivationModel());
  }

  public ClientExtended(String name, String email, String phone, String country, ActivationModel activation) {
    super(name, email, phone, country);
    this.dateTime = ZonedDateTime.now();
    this.activation = activation;
  }


  public ClientExtended setActivation(ActivationModel activation) {
    this.activation = activation;
    return this;
  }

  public ClientExtended setDateTime(ZonedDateTime dateTime) {
    this.dateTime = dateTime;
    return this;
  }

  public ActivationModel getActivation() {
    return activation;
  }

  public ZonedDateTime getDateTime() {
    return dateTime;
  }

  @Override
  public String toString() {
    return "ClientExtended{" +
        "dateTime=" + dateTime +
        ", activation=" + activation +
        "} " + super.toString();
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ClientExtended)) return false;
    if (!super.equals(o)) return false;

    ClientExtended that = (ClientExtended) o;

    if (getDateTime() != null ? !getDateTime().equals(that.getDateTime()) : that.getDateTime() != null) return false;
    return getActivation() != null ? getActivation().equals(that.getActivation()) : that.getActivation() == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (getDateTime() != null ? getDateTime().hashCode() : 0);
    result = 31 * result + (getActivation() != null ? getActivation().hashCode() : 0);
    return result;
  }
}
