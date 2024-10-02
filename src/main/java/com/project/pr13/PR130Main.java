package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.project.pr13.format.PersonaFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;

public class PR130Main {

    private final File dataDir;

    public PR130Main(File dataDir) {
        this.dataDir = dataDir;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        PR130Main app = new PR130Main(dataDir);
        app.processarFitxer("persones.xml");
    }

    public void processarFitxer(String filename) {
        File inputFile = new File(dataDir, filename);
        Document doc = parseXML(inputFile);
        if (doc != null) {
            NodeList persones = doc.getElementsByTagName("persona");
            imprimirCapçaleres();
            imprimirDadesPersones(persones);
        }
    }

    public static Document parseXML(File inputFile) {
        try (InputStream inputStream = inputFile.toURI().toURL().openStream()) {
            return parseXML(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Document parseXML(InputStream inputStream) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void imprimirCapçaleres() {
        System.out.println(PersonaFormatter.getCapçaleres());
    }

    public static void imprimirDadesPersones(NodeList persones) {
        for (int i = 0; i < persones.getLength(); i++) {
            Element persona = (Element) persones.item(i);
            String nom = persona.getElementsByTagName("nom").item(0).getTextContent();
            String cognom = persona.getElementsByTagName("cognom").item(0).getTextContent();
            String edat = persona.getElementsByTagName("edat").item(0).getTextContent();
            String ciutat = persona.getElementsByTagName("ciutat").item(0).getTextContent();
            System.out.println(PersonaFormatter.formatarPersona(nom, cognom, edat, ciutat));
        }
    }
}
