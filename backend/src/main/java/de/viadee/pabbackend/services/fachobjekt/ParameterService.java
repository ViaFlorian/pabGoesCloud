package de.viadee.pabbackend.services.fachobjekt;


import de.viadee.pabbackend.repositories.pabdb.ParameterRepository;
import org.springframework.stereotype.Service;


@Service
public class ParameterService {

  final ParameterRepository parameterRepository;

  public ParameterService(final ParameterRepository parameterRepository) {
    this.parameterRepository = parameterRepository;
  }


  public String valueByKey(final String key) {
    return this.parameterRepository.findFirstByKey(key);
  }
}
