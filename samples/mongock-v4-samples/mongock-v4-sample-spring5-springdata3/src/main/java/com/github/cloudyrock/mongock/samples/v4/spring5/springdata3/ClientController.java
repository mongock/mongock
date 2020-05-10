package com.github.cloudyrock.mongock.samples.v4.spring5.springdata3;

import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.client.Client;
import com.github.cloudyrock.mongock.samples.v4.spring5.springdata3.client.ClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {

  private final ClientRepository clientRepo;

  public ClientController(ClientRepository clientRepo) {
    this.clientRepo = clientRepo;
  }


  @GetMapping("/all")
  public List<Client> geAll() {
    return clientRepo.findAll();
  }

  @PostMapping
  public Client createUser(@RequestBody Client user) {
    return clientRepo.insert(user);
  }
}
