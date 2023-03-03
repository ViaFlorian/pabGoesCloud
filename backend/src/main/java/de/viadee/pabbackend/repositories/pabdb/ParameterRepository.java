package de.viadee.pabbackend.repositories.pabdb;

import de.viadee.pabbackend.entities.Parameter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends CrudRepository<Parameter, Long> {


  @Query("""
      SELECT Value FROM Parameter WHERE "Key" = :key
      """)
  @Cacheable("parameter")
  String findFirstByKey(final String key);

}
