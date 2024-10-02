package com.project.pr13;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.project.pr13.format.AsciiTablePrinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PR132Main {

    private final Path xmlFilePath;
    private static final Scanner scanner = new Scanner(System.in);

    public PR132Main(Path xmlFilePath) {
        this.xmlFilePath = xmlFilePath;
    }

    public static void main(String[] args) {
        String userDir = System.getProperty("user.dir");
        Path xmlFilePath = Paths.get(userDir, "data", "pr13", "cursos.xml");

        PR132Main app = new PR132Main(xmlFilePath);
        app.executar();
    }

    public void executar() {
        boolean exit = false;
        while (!exit) {
            mostrarMenu();
            System.out.print("Escull una opció: ");
            int opcio = scanner.nextInt();
            scanner.nextLine(); // Netegem el buffer del scanner
            exit = processarOpcio(opcio);
        }
    }

    public boolean processarOpcio(int opcio) {
        String cursId;
        String nomAlumne;
        switch (opcio) {
            case 1:
                List<List<String>> cursos = llistarCursos();
                imprimirTaulaCursos(cursos);
                return false;
            case 2:
                System.out.print("Introdueix l'ID del curs per veure els seus mòduls: ");
                cursId = scanner.nextLine();
                List<List<String>> moduls = mostrarModuls(cursId);
                imprimirTaulaModuls(moduls);
                return false;
            case 3:
                System.out.print("Introdueix l'ID del curs per veure la llista d'alumnes: ");
                cursId = scanner.nextLine();
                List<String> alumnes = llistarAlumnes(cursId);
                imprimirLlistaAlumnes(alumnes);
                return false;
            case 4:
                System.out.print("Introdueix l'ID del curs on vols afegir l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a afegir: ");
                nomAlumne = scanner.nextLine();
                afegirAlumne(cursId, nomAlumne);
                return false;
            case 5:
                System.out.print("Introdueix l'ID del curs on vols eliminar l'alumne: ");
                cursId = scanner.nextLine();
                System.out.print("Introdueix el nom complet de l'alumne a eliminar: ");
                nomAlumne = scanner.nextLine();
                eliminarAlumne(cursId, nomAlumne);
                return false;
            case 6:
                System.out.println("Sortint del programa...");
                return true;
            default:
                System.out.println("Opció no reconeguda. Si us plau, prova de nou.");
                return false;
        }
    }

    private void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Llistar IDs de cursos i tutors");
        System.out.println("2. Mostrar IDs i títols dels mòduls d'un curs");
        System.out.println("3. Llistar alumnes d’un curs");
        System.out.println("4. Afegir un alumne a un curs");
        System.out.println("5. Eliminar un alumne d'un curs");
        System.out.println("6. Sortir");
    }

    public List<List<String>> llistarCursos() {
        List<List<String>> dades = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();
            NodeList cursNodes = (NodeList) xpath.evaluate("/cursos/curs", document, XPathConstants.NODESET);

            for (int i = 0; i < cursNodes.getLength(); i++) {
                String id = xpath.evaluate("@id", cursNodes.item(i));
                String tutor = xpath.evaluate("tutor", cursNodes.item(i));
                NodeList alumnes = (NodeList) xpath.evaluate("alumnes/alumne", cursNodes.item(i), XPathConstants.NODESET);
                dades.add(List.of(id, tutor, String.valueOf(alumnes.getLength())));
            }
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
        return dades;
    }

    public void imprimirTaulaCursos(List<List<String>> cursos) {
        List<String> capçaleres = List.of("ID", "Tutor", "Total Alumnes");
        AsciiTablePrinter.imprimirTaula(capçaleres, cursos);
    }

    public List<List<String>> mostrarModuls(String idCurs) {
        List<List<String>> dades = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expressioModuls = String.format("/cursos/curs[@id='%s']/moduls/modul", idCurs);
            NodeList modulsNodes = (NodeList) xpath.evaluate(expressioModuls, document, XPathConstants.NODESET);

            for (int i = 0; i < modulsNodes.getLength(); i++) {
                String idModul = xpath.evaluate("@id", modulsNodes.item(i));
                String titol = xpath.evaluate("titol", modulsNodes.item(i));
                dades.add(List.of(idModul, titol));
            }
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
        return dades;
    }

    public void imprimirTaulaModuls(List<List<String>> moduls) {
        List<String> capçaleres = List.of("ID Mòdul", "Títol");
        AsciiTablePrinter.imprimirTaula(capçaleres, moduls);
    }

    public List<String> llistarAlumnes(String idCurs) {
        List<String> alumnes = new ArrayList<>();
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expressioAlumnes = String.format("/cursos/curs[@id='%s']/alumnes/alumne", idCurs);
            NodeList alumnesNodes = (NodeList) xpath.evaluate(expressioAlumnes, document, XPathConstants.NODESET);

            for (int i = 0; i < alumnesNodes.getLength(); i++) {
                alumnes.add(alumnesNodes.item(i).getTextContent());
            }
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
        return alumnes;
    }

    public void imprimirLlistaAlumnes(List<String> alumnes) {
        System.out.println("Alumnes:");
        alumnes.forEach(alumne -> System.out.println("- " + alumne));
    }

    public void afegirAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expressioCurs = String.format("/cursos/curs[@id='%s']", idCurs);
            NodeList cursNode = (NodeList) xpath.evaluate(expressioCurs, document, XPathConstants.NODESET);
            if (cursNode.getLength() == 0) {
                System.out.println("No s'ha trobat cap curs amb l'ID proporcionat.");
                return;
            }

            NodeList alumnesNode = (NodeList) xpath.evaluate(expressioCurs + "/alumnes", document, XPathConstants.NODESET);
            if (alumnesNode.getLength() > 0) {
                Element nouAlumne = document.createElement("alumne");
                nouAlumne.setTextContent(nomAlumne);
                alumnesNode.item(0).appendChild(nouAlumne);

                guardarDocumentXML(document);
                System.out.println("Alumne afegit correctament al curs.");
            }
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error en afegir l'alumne.");
            e.printStackTrace();
        }
    }

    public void eliminarAlumne(String idCurs, String nomAlumne) {
        try {
            Document document = carregarDocumentXML(xmlFilePath);
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expressioAlumne = String.format("/cursos/curs[@id='%s']/alumnes/alumne[text()='%s']", idCurs, nomAlumne);
            NodeList alumnesNode = (NodeList) xpath.evaluate(expressioAlumne, document, XPathConstants.NODESET);

            if (alumnesNode.getLength() == 0) {
                System.out.println("L'alumne no està inscrit en el curs o no existeix.");
                return;
            }

            Node alumneNode = alumnesNode.item(0);
            alumneNode.getParentNode().removeChild(alumneNode);

            guardarDocumentXML(document);
            System.out.println("Alumne eliminat correctament del curs.");
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error en eliminar l'alumne.");
            e.printStackTrace();
        }
    }

    private Document carregarDocumentXML(Path pathToXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(pathToXml.toFile());
        } catch (Exception e) {
            throw new RuntimeException("Error en carregar el document XML.", e);
        }
    }

    private void guardarDocumentXML(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(xmlFilePath.toFile());
            transformer.transform(source, result);
            System.out.println("El fitxer XML ha estat guardat amb èxit.");
        } catch (TransformerException e) {
            System.out.println("Error en guardar el fitxer XML.");
            e.printStackTrace();
        }
    }
}
