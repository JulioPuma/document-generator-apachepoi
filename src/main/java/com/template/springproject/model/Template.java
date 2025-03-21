package com.template.springproject.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Template {
  private String templatePath;
  
  public Template(){
    this.setTemplatePath("templates/T0001.docx");
  }
  
  public Map<String, String> getData(){
    Map<String,String> data = new HashMap<>();
    data.put("{{NOMBRE}}", null);
    data.put("{{NOMBRE-ARTISTICO}}", "William Shakespeare");
    data.put("{{PARRAFO-EXTRA}}", getParagrah());
    data.put("{{ANO-DE-CASADO}}", "14/10/1980");
    data.put("{{ANO-DE-FALLECIDO}}", "14/10/1990");
    return data;
  }
  
  private String getParagrah(){
    return "Este es un separador de texto o tambien un parrafo para describir texto. \n vamos a ver si funciona";
  }
}
