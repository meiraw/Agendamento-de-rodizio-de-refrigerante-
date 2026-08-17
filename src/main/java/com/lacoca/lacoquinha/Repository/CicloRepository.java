package com.lacoca.lacoquinha.Repository;

import com.lacoca.lacoquinha.Model.CicloModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CicloRepository extends JpaRepository<CicloModel, UUID >{
}
