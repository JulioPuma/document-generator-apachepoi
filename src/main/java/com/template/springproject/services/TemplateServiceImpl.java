package com.template.springproject.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateServiceImpl {
  
  private final ResourceLoader resourceLoader;
  
  public List<String> listTemplates() {
    try {
      Resource resource = resourceLoader.getResource("classpath:templates");
      Path templatesPath = Paths.get(resource.getURI());
      
      log.debug("====Template's List=====");
      return Files.list(templatesPath)
        .map(Path::getFileName)
        .map(Path::toString)
        .peek(log::debug)
        .collect(Collectors.toList());
      
    } catch (IOException e) {
      throw new RuntimeException("No se pudieron listar las plantillas en templates", e);
    }
  }

  public void loadDocument() throws IOException {
    
    ClassPathResource resource = new ClassPathResource("templates/T0001.docx");
    InputStream inputStream = resource.getInputStream();
    XWPFDocument document = new XWPFDocument(inputStream);
    inputStream.close();

    // Definir los valores a reemplazar
    Map<String, String> variables = new HashMap<>();
    variables.put("{{NOMBRE}}", "Julio");
    
    // Reemplazar texto en párrafos
    for (XWPFParagraph paragraph : document.getParagraphs()) {
      for (Map.Entry<String, String> entry : variables.entrySet()) {
        if (paragraph.getText().contains(entry.getKey())) {
          String replacedText = paragraph.getText().replace(entry.getKey(), entry.getValue());
          paragraph.getRuns().forEach(run -> run.setText("", 0)); // Limpiar texto anterior
          paragraph.createRun().setText(replacedText); // Insertar nuevo texto
        }
      }
    }
    
    // Guardar el documento generado
    FileOutputStream fos = new FileOutputStream("resultado.docx");
    document.write(fos);
    fos.close();
    document.close();
    
    System.out.println("Documento generado exitosamente.");
  
  }
}
