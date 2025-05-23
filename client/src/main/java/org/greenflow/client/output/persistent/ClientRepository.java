package org.greenflow.client.output.persistent;

import org.greenflow.client.model.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    boolean existsByEmail(String email);
}
