package com.project.pr13;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class PR130Main {

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        if (!dataDir.exists() && !dataDir.mkdirs()) {
            System.out.println("No s'ha pogut crear el directori 'data/pr13'.");
            return;
        }

        File inputFile = new File(dataDir, "persones.xml");
        Document doc = parseXML(inputFile);
        if (doc != null) {
            NodeList persones = doc.getElementsByTagName("persona");
            imprimirCapçaleres();
            imprimirDadesPersones(persones);
        }
    }

    private static Document parseXML(File inputFile) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void imprimirCapçaleres() {
        System.out.println("Nom      Cognom        Edat  Ciutat");
        System.out.println("-------- -------------- ----- ---------");
    }

    private static void imprimirDadesPersones(NodeList persones) {
        for (int i = 0; i < persones.getLength(); i++) {
            Element persona = (Element) persones.item(i);
            String nom = persona.getElementsByTagName("nom").item(0).getTextContent();
            String cognom = persona.getElementsByTagName("cognom").item(0).getTextContent();
            String edat = persona.getElementsByTagName("edat").item(0).getTextContent();
            String ciutat = persona.getElementsByTagName("ciutat").item(0).getTextContent();
            imprimirPersona(nom, cognom, edat, ciutat);
        }
    }

    private static void imprimirPersona(String nom, String cognom, String edat, String ciutat) {
        System.out.printf("%-8s %-14s %-5s %-9s%n", nom, cognom, edat, ciutat);
    }
}
