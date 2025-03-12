package com.template.springproject.services;

import com.template.springproject.config.ApplicationProperties;
import com.template.springproject.model.ResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GeneratorServiceImpl {

    private final TemplateServiceImpl templateService;
    private final ApplicationProperties properties;
    
    public Mono<ResponseStatus> generateDocument(
      String document,
      String template
    ){
        validateDocument(document);
        validateTemplate(template);
      
      try {
        templateService.loadDocument();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      
      
      ResponseStatus status =
          ResponseStatus.builder()
          .status("OK")
          .description("Description")
          .build();
        
        return Mono.just(status);
    }
    
    private void validateDocument(String document){
        if(!properties.getAvailableDocuments().contains(document)){
            throw new UnsupportedOperationException("Documento no soportado: " + document);
        }
    }
    
    private void validateTemplate(String template) {
        boolean isValidTemplate =
          templateService.listTemplates().stream()
            .anyMatch(s -> s.split("\\.")[0].equals(template));
          
        if(!isValidTemplate){
            throw new UnsupportedOperationException("Plantilla no soportada: " + template);
          }
    }
}
