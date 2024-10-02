package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class PR131Main {

    private File dataDir;

    public PR131Main(File dataDir) {
        this.dataDir = dataDir;
    }

    // Getter per dataDir
    public File getDataDir() {
        return dataDir;
    }

    // Setter per dataDir
    public void setDataDir(File dataDir) {
        this.dataDir = dataDir;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        File dataDir = new File(userDir, "data" + File.separator + "pr13");

        PR131Main app = new PR131Main(dataDir);
        app.processarDocument("biblioteca.xml");
    }

    public void processarDocument(String filename) {
        if (comprovarIDirCrearDirectori(dataDir)) {
            Document doc = construirDocument();
            File fitxerSortida = new File(dataDir, filename);
            guardarDocument(doc, fitxerSortida);
        }
    }

    private boolean comprovarIDirCrearDirectori(File directori) {
        if (!directori.exists()) {
            return directori.mkdirs();
        }
        return true;
    }

    private static Document construirDocument() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // Crear l'element arrel 'biblioteca'
            Element biblioteca = doc.createElement("biblioteca");
            doc.appendChild(biblioteca);

            // Afegir l'element 'llibre'
            Element llibre = doc.createElement("llibre");
            llibre.setAttribute("id", "001");
            biblioteca.appendChild(llibre);

            // Afegir els detalls del llibre
            Element titol = doc.createElement("titol");
            titol.appendChild(doc.createTextNode("El viatge dels venturons"));
            llibre.appendChild(titol);

            Element autor = doc.createElement("autor");
            autor.appendChild(doc.createTextNode("Joan Pla"));
            llibre.appendChild(autor);

            Element anyPublicacio = doc.createElement("anyPublicacio");
            anyPublicacio.appendChild(doc.createTextNode("1998"));
            llibre.appendChild(anyPublicacio);

            Element editorial = doc.createElement("editorial");
            editorial.appendChild(doc.createTextNode("Edicions Mar"));
            llibre.appendChild(editorial);

            Element genere = doc.createElement("genere");
            genere.appendChild(doc.createTextNode("Aventura"));
            llibre.appendChild(genere);

            Element pagines = doc.createElement("pagines");
            pagines.appendChild(doc.createTextNode("320"));
            llibre.appendChild(pagines);

            Element disponible = doc.createElement("disponible");
            disponible.appendChild(doc.createTextNode("true"));
            llibre.appendChild(disponible);

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void guardarDocument(Document doc, File fitxerSortida) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(fitxerSortida);
            transformer.transform(source, result);
            System.out.println("El fitxer XML s'ha guardat a: " + fitxerSortida.getPath());
        } catch (TransformerException e) {
            System.out.println("No s'ha pogut guardar el fitxer XML.");
            e.printStackTrace();
        }
    }
}
