package com.project.pr13;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PR132Main {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean exit = false;
        String cursId;
        String nomAlumne;
        while (!exit) {
            cursId = null;
            nomAlumne = null;
            mostrarMenu();
            System.out.print("Escull una opció: ");
            int opcio = scanner.nextInt();
            scanner.nextLine(); // Netegem el buffer del scanner

            switch (opcio) {
                case 1:
                    llistarCursos();
                    break;
                case 2:
                    System.out.print("Introdueix l'ID del curs per veure els seus mòduls: ");
                    cursId = scanner.nextLine();
                    mostrarModuls(cursId);
                    break;
                case 3:
                    System.out.print("Introdueix l'ID del curs per veure la llista d'alumnes: ");
                    cursId = scanner.nextLine();
                    llistarAlumnes(cursId);
                    break;
                case 4:
                    System.out.print("Introdueix l'ID del curs on vols afegir l'alumne: ");
                    cursId = scanner.nextLine();
                    System.out.print("Introdueix el nom complet de l'alumne a afegir: ");
                    nomAlumne = scanner.nextLine();
                    afegirAlumne(cursId, nomAlumne);
                    break;
                case 5:
                    System.out.print("Introdueix l'ID del curs on vols eliminar l'alumne: ");
                    cursId = scanner.nextLine();
                    System.out.print("Introdueix el nom complet de l'alumne a eliminar: ");
                    nomAlumne = scanner.nextLine();
                    eliminarAlumne(cursId, nomAlumne);
                    break;
                case 6:
                    exit = true;
                    System.out.println("Sortint del programa...");
                    break;
                default:
                    System.out.println("Opció no reconeguda. Si us plau, prova de nou.");
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Llistar IDs de cursos i tutors");
        System.out.println("2. Mostrar IDs i títols dels mòduls d'un curs");
        System.out.println("3. Llistar alumnes d’un curs");
        System.out.println("4. Afegir un alumne a un curs");
        System.out.println("5. Eliminar un alumne d'un curs");
        System.out.println("6. Sortir");
    }

    private static Path obtenirDataPath() {
        return Paths.get(System.getProperty("user.dir"), "data", "pr14", "cursos.xml");
    }

    private static void llistarCursos() {
        try {
            Path pathToXml = obtenirDataPath(); // Aquest mètode retorna la ruta correcta al fitxer XML
            Document document = carregarDocumentXML(pathToXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            NodeList cursNodes = (NodeList) xpath.evaluate("/cursos/curs", document, XPathConstants.NODESET);
            List<String> capçaleres = List.of("ID", "Tutor", "Total Alumnes");
            List<List<String>> dades = new ArrayList<>();

            for (int i = 0; i < cursNodes.getLength(); i++) {
                String id = xpath.evaluate("@id", cursNodes.item(i));
                String tutor = xpath.evaluate("tutor", cursNodes.item(i));
                NodeList alumnes = (NodeList) xpath.evaluate("alumnes/alumne", cursNodes.item(i), XPathConstants.NODESET);
                List<String> fila = List.of(id, tutor, String.valueOf(alumnes.getLength()));
                dades.add(fila);
            }

            AsciiTablePrinter.imprimirTaula(capçaleres, dades);
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
    }

    private static Document carregarDocumentXML(Path pathToXml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(pathToXml.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error en carregar el document XML.", e);
        }
    }

    private static void mostrarModuls(String idCurs) {
        try {
            Path pathToXml = obtenirDataPath();
            Document document = carregarDocumentXML(pathToXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Comprovar si existeix el curs amb l'ID donat
            String expressioCurs = String.format("/cursos/curs[@id='%s']", idCurs);
            boolean cursExisteix = (Boolean) xpath.evaluate("count(" + expressioCurs + ") > 0", document, XPathConstants.BOOLEAN);
            if (!cursExisteix) {
                System.out.println("No s'ha trobat cap curs amb l'ID proporcionat.");
                return;
            }

            // Recuperar tots els mòduls del curs
            String expressioModuls = expressioCurs + "/moduls/modul";
            NodeList modulsNodes = (NodeList) xpath.evaluate(expressioModuls, document, XPathConstants.NODESET);

            List<String> capçaleres = List.of("ID Mòdul", "Títol");
            List<List<String>> dades = new ArrayList<>();

            for (int i = 0; i < modulsNodes.getLength(); i++) {
                String idModul = xpath.evaluate("@id", modulsNodes.item(i));
                String titol = xpath.evaluate("titol", modulsNodes.item(i));
                dades.add(List.of(idModul, titol));
            }

            AsciiTablePrinter.imprimirTaula(capçaleres, dades);

        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
    }

    private static void llistarAlumnes(String idCurs) {
        try {
            Path pathToXml = obtenirDataPath();
            Document document = carregarDocumentXML(pathToXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Comprovar si existeix el curs amb l'ID donat
            String expressioCurs = String.format("/cursos/curs[@id='%s']", idCurs);
            boolean cursExisteix = (Boolean) xpath.evaluate("count(" + expressioCurs + ") > 0", document, XPathConstants.BOOLEAN);
            if (!cursExisteix) {
                System.out.println("No s'ha trobat cap curs amb l'ID proporcionat.");
                return;
            }

            // Recuperar tots els alumnes del curs
            String expressioAlumnes = expressioCurs + "/alumnes/alumne";
            NodeList alumnesNodes = (NodeList) xpath.evaluate(expressioAlumnes, document, XPathConstants.NODESET);

            List<String> capçaleres = List.of("Alumnes");
            List<List<String>> dades = new ArrayList<>();

            for (int i = 0; i < alumnesNodes.getLength(); i++) {
                String nomAlumne = alumnesNodes.item(i).getTextContent();
                dades.add(List.of(nomAlumne));
            }

            AsciiTablePrinter.imprimirTaula(capçaleres, dades);

        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        }
    }

    private static void afegirAlumne(String idCurs, String nomAlumne) {
        try {
            Path pathToXml = obtenirDataPath();
            Document document = carregarDocumentXML(pathToXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // Comprovar si existeix el curs amb l'ID donat
            String expressioCurs = String.format("/cursos/curs[@id='%s']", idCurs);
            NodeList cursNode = (NodeList) xpath.evaluate(expressioCurs, document, XPathConstants.NODESET);
            if (cursNode.getLength() == 0) {
                System.out.println("No s'ha trobat cap curs amb l'ID proporcionat.");
                return;
            }

            // Comprovar si l'alumne ja està al curs
            String expressioAlumne = expressioCurs + String.format("/alumnes/alumne[text()='%s']", nomAlumne);
            boolean alumneExisteix = (Boolean) xpath.evaluate("count(" + expressioAlumne + ") > 0", document, XPathConstants.BOOLEAN);
            if (alumneExisteix) {
                System.out.println("L'alumne ja està inscrit en el curs.");
                return;
            }

            // Afegir l'alumne al curs
            NodeList alumnesNode = (NodeList) xpath.evaluate(expressioCurs + "/alumnes", document, XPathConstants.NODESET);
            if (alumnesNode.getLength() > 0) {
                Element nouAlumne = document.createElement("alumne");
                nouAlumne.setTextContent(nomAlumne);
                alumnesNode.item(0).appendChild(nouAlumne);

                guardarDocumentXML(pathToXml, document);
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

    private static void eliminarAlumne(String idCurs, String nomAlumne) {
        try {
            Path pathToXml = obtenirDataPath();
            Document document = carregarDocumentXML(pathToXml);
            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            // XPath expressions to locate the course and the student
            String expressioCurs = String.format("/cursos/curs[@id='%s']", idCurs);
            String expressioAlumne = expressioCurs + String.format("/alumnes/alumne[text()='%s']", nomAlumne);

            // Check if the course exists
            NodeList cursNode = (NodeList) xpath.evaluate(expressioCurs, document, XPathConstants.NODESET);
            if (cursNode.getLength() == 0) {
                System.out.println("No s'ha trobat cap curs amb l'ID proporcionat.");
                return;
            }

            // Check if the student exists within the course and retrieve the node to remove it
            NodeList alumnesNode = (NodeList) xpath.evaluate(expressioAlumne, document, XPathConstants.NODESET);
            if (alumnesNode.getLength() == 0) {
                System.out.println("L'alumne no està inscrit en el curs o no existeix.");
                return;
            }

            // Remove the student from the course
            Node alumneNode = alumnesNode.item(0);
            alumneNode.getParentNode().removeChild(alumneNode);

            // Save the changes to the XML document
            guardarDocumentXML(pathToXml, document);
            System.out.println("Alumne eliminat correctament del curs.");
        } catch (XPathExpressionException e) {
            System.out.println("Error en processar l'expressió XPath.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error en eliminar l'alumne.");
            e.printStackTrace();
        }
    }

    private static void guardarDocumentXML(Path pathToXml, Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(pathToXml.toString()));

            transformer.transform(source, result);
            System.out.println("El fitxer XML ha estat guardat amb èxit.");
        } catch (TransformerException e) {
            System.out.println("Error en guardar el fitxer XML.");
            e.printStackTrace();
        }
    }
}


class AsciiTablePrinter {
    public static void imprimirTaula(List<String> capçaleres, List<List<String>> dades) {
        List<Integer> amplades = calcularAmpladesDeColumnes(capçaleres, dades);
        imprimirSeparador(amplades);
        imprimirFila(capçaleres, amplades);
        imprimirSeparador(amplades);
        dades.forEach(fila -> imprimirFila(fila, amplades));
        imprimirSeparador(amplades);
    }

    private static List<Integer> calcularAmpladesDeColumnes(List<String> capçaleres, List<List<String>> dades) {
        return IntStream.range(0, capçaleres.size())
                .map(i -> Math.max(
                        capçaleres.get(i).length(),
                        dades.stream().mapToInt(fila -> fila.get(i).length()).max().orElse(0)
                ) + 2) // +2 per espai abans i després del text
                .boxed()
                .collect(Collectors.toList());
    }

    private static void imprimirSeparador(List<Integer> amplades) {
        amplades.forEach(amplada -> System.out.print("+" + "-".repeat(amplada)));
        System.out.println("+");
    }

    private static void imprimirFila(List<String> fila, List<Integer> amplades) {
        IntStream.range(0, fila.size())
                .forEach(i -> System.out.printf("| %-" + (amplades.get(i) - 2) + "s ", fila.get(i))); // -2 per espai abans i després del text
        System.out.println("|");
    }
}