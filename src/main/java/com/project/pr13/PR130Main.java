package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.project.pr13.format.PersonaFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;

/**
 * Classe principal que gestiona la lectura i el processament de fitxers XML per obtenir dades de persones.
 * 
 * Aquesta classe s'encarrega de llegir un fitxer XML que conté informació de persones,
 * processar-lo i mostrar les dades formatades per consola.
 */
public class PR130Main {

    private final File dataDir;

    /**
     * Constructor de la classe PR130Main.
     * 
     * @param dataDir Directori on es troben els fitxers de dades.
     */
    public PR130Main(File dataDir) {
        this.dataDir = dataDir;
    }

    /**
     * Mètode principal que inicia l'execució del programa.
     * 
     * @param args Arguments passats a la línia de comandament (no s'utilitzen en aquest programa).
     */
    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        PR130Main app = new PR130Main(dataDir);
        app.processarFitxer("persones.xml");
    }

    /**
     * Processa un fitxer XML per obtenir la informació de les persones i imprimir-la.
     * 
     * @param filename Nom del fitxer XML a processar.
     */
    public void processarFitxer(String filename) {
        File inputFile = new File(dataDir, filename);
        Document doc = parseXML(inputFile);
        if (doc != null) {
            NodeList persones = doc.getElementsByTagName("persona");
            imprimirCapçaleres();
            imprimirDadesPersones(persones);
        }
    }

    /**
     * Llegeix un fitxer XML i el converteix en un objecte Document.
     * 
     * @param inputFile Fitxer XML a llegir.
     * @return Document XML carregat o null si hi ha hagut un error en la lectura.
     */
    public static Document parseXML(File inputFile) {
        try (InputStream inputStream = inputFile.toURI().toURL().openStream()) {
            return parseXML(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Llegeix un InputStream que conté dades XML i el converteix en un objecte Document.
     * 
     * @param inputStream Flux d'entrada que conté les dades XML.
     * @return Document XML carregat o null si hi ha hagut un error en la lectura.
     */
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

    /**
     * Imprimeix per consola les capçaleres de les columnes que es mostraran per a les dades de les persones.
     */
    public static void imprimirCapçaleres() {
        System.out.println(PersonaFormatter.getCapçaleres());
    }

    /**
     * Imprimeix per consola les dades de cada persona obtingudes d'un NodeList.
     * 
     * @param persones NodeList que conté els elements "persona" del document XML.
     */
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
