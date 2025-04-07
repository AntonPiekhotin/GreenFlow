package org.greenflow.worker.output.persistent;

import org.greenflow.worker.model.entity.Worker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkerRepository extends JpaRepository<Worker, String> {

    boolean existsByEmail(String email);

}
