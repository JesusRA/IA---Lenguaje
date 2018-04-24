import java.io.*;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * ULL - IA Avanzada - Practica Lenguaje Natural
 * Vocabulario.java
 * Programa que dado un fichero de entrada separa el vocabulario del mismo
 * y lo escribe en otro fichero.
 * 
 * @author Jesús Ramos Álvarez - Cristina
 * @version 24/04/2018
 */
public class Vocabulario {
	
	private static TreeSet<String> palabras; // Set que contendra las palabras
	
	/**
	 * Metodo que comprueba si una palabra contiene algun digito
	 * @param palabra Palabra que se comprueba
	 * @return True en caso de si contener algun digito
	 */
	private static boolean contieneNumero(String palabra) {
	  return palabra.matches(".*\\d+.*");
	}
	
	/**
	 * Metodo que elimina cualquier simbolo de una palabra
	 * @param palabra Palabra que se comprueba
	 * @return Palabra ya modificada correctamente
	 */
	private static String quitarSimbolos (String palabra) {
		String temp = null;
		temp = palabra.replaceAll("[^a-zA-Z&']", "");
		if (temp.startsWith("'"))
			temp = temp.substring(1, temp.length());
		if (temp.endsWith("'"))
			temp = temp.substring(0, temp.length() - 1);
		return temp;
	}
	
	/**
	 * Metodo que analiza la linea leida, la divide en palabras e incluye
	 * las palabras, tras limpiar los simbolos, en el set.
	 * @param linea Linea a analizar
	 */
	private static void analizarLinea (String [] linea) {
		String palabra;
		for (int i = 0; i < linea.length; i++) {
			palabra = linea [i];
			//System.out.println(temp);
			if (!palabra.startsWith("@") && !palabra.startsWith("#") && !palabra.contains("http://") && !contieneNumero(palabra)) {
				palabras.add(quitarSimbolos(palabra));
			}			
		}
	}
	
	/**
	 * Main
	 * @param args Recibe como argumentos el nombre de los ficheros de entrada y salida
	 * @throws IOException Manejo de errores de tipo IO
	 */
	public static void main(String[] args) throws IOException {
		String ficheroEntrada = args [0]; // Nombre fichero de entrada pasado como primer argumento
		String ficheroSalida = args [1]; // Nombre fichero de salido pasado como segundo argumento
		
		palabras = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		
		// Lectura del fichero de entrada
		String linea [];
		BufferedReader reader = new BufferedReader (new FileReader (ficheroEntrada));
		while (reader.ready()) {
			linea = reader.readLine().split("\\s+");
			analizarLinea (linea);
		}
		reader.close();
		
		// Condicion que corrige el set de palabras para los casos en que
		// una palabra solo contenia simbolos
		if (palabras.first().isEmpty())
			palabras.remove(palabras.first());
		
		// Escritura en el fichero de salida
		BufferedWriter writer = new BufferedWriter (new FileWriter (ficheroSalida));
		Iterator<String> iterator = palabras.iterator();
		writer.write("VOCABULARIO: \r\n");
		writer.write("Numero de palabras: " + (palabras.size()) + "\r\n");
	    while (iterator.hasNext()) {
	    	writer.write(iterator.next() + "\r\n");
	    }
	    writer.close();
	}
	
}