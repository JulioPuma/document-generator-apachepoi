package com.template.springproject.services;

import com.template.springproject.model.Document;
import com.template.springproject.model.FileInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProcessDocumentImpl {
  
  public FileInformation loadDocument2(Document document) {
    try {
      // Cargar plantilla
      ClassPathResource resource = new ClassPathResource(document.getTemplate().getTemplatePath());
      InputStream inputStream = resource.getInputStream();
      XWPFDocument doc = new XWPFDocument(inputStream);
      inputStream.close();
      
      Map<String, String> variables = document.getData();
      
      // Procesar párrafos
      for (XWPFParagraph paragraph : doc.getParagraphs()) {
        processParagraph(paragraph, variables);
      }
      
      // Guardar documento generado en memoria
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      doc.write(baos);
      baos.close();
      doc.close();
      
      document.setByts(baos.toByteArray());
      document.setFileSize(String.valueOf(baos.size()));
      return document;
      
    } catch (Exception e) {
      log.error("Error al generar el documento en memoria: ", e);
      throw new RuntimeException("Error al generar el documento", e);
    }
  }
  
  private void processParagraph(XWPFParagraph paragraph, Map<String, String> variables) {
    List<XWPFRun> runs = paragraph.getRuns();
    if (runs == null || runs.isEmpty()) return;
    
    // Combinar texto del párrafo
    StringBuilder combinedText = new StringBuilder();
    for (XWPFRun run : runs) {
      String text = run.getText(0);
      if (text != null) combinedText.append(text);
    }
    
    String fullText = combinedText.toString();
    boolean hasReplacement = false;
    
    // Reemplazar variables
    for (Map.Entry<String, String> entry : variables.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue() != null ? entry.getValue() : "";
      if (fullText.contains(key)) {
        fullText = fullText.replace(key, value);
        hasReplacement = true;
      }
    }
    
    if (hasReplacement) {
      // Clonar estilo del primer run antes de eliminar
      CTRPr style = null;
      CTRPr originalRPr = runs.get(0).getCTR().getRPr();
      if (originalRPr != null) {
        style = (CTRPr) originalRPr.copy();
      }
      
      for (int i = runs.size() - 1; i >= 0; i--) {
        paragraph.removeRun(i);
      }
      
      XWPFRun newRun = paragraph.createRun();
      writeTextWithLineBreaks(newRun, fullText);
      
      if (style != null) {
        newRun.getCTR().setRPr(style);
      }
    }
  }
  
  private void writeTextWithLineBreaks(XWPFRun run, String text) {
    if (text == null) return;
    
    String[] lines = text.split("\n", -1);
    for (int i = 0; i < lines.length; i++) {
      run.setText(lines[i]);
      if (i < lines.length - 1) {
        run.addBreak();
      }
    }
  }

}
