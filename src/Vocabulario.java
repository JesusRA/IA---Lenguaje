import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * ULL - IA Avanzada - Practica Lenguaje Natural
 * Vocabulario.java
 * Programa que dado un fichero de entrada separa el vocabulario del mismo
 * y lo escribe en otro fichero.
 * 
 * @author Jesús Ramos Álvarez - Cristina Garrido Amador
 * @version 24/04/2018
 */
public class Vocabulario {
	
	private static final int MIN_APARICIONES = 0;
	
	private static TreeSet<String> vocabulario; // Set que contendra las palabras
	private static ArrayList<String> listaPalabras; // Lista todas las palabras encontradas
	private static int numTotalTweets = 0;
	
	private static String [] ignorar = {"the", "to", "you", "a", "for", "is", "in", "of"};
	private static ArrayList<String> palabrasIgnorar = new ArrayList<String> (Arrays.asList(ignorar));
	
	/**
	 * Metodo que comprueba si una palabra contiene algun digito
	 * @param palabra Palabra que se comprueba
	 * @return True en caso de si contener algun digito
	 */
	public static boolean contieneNumero(String palabra) {
	  return palabra.matches(".*\\d+.*");
	  
	}
	
	/**
	 * Metodo que elimina cualquier simbolo de una palabra
	 * @param palabra Palabra que se comprueba
	 * @return Palabra ya modificada correctamente
	 */
	public static String quitarSimbolos (String palabra) {
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
		numTotalTweets++;
		String palabra;
		for (int i = 0; i < linea.length; i++) {
			palabra = linea [i];
			if (!palabra.startsWith("@") && !palabra.startsWith("#") && !palabra.contains("http://") && !contieneNumero(palabra)) {
				listaPalabras.add(quitarSimbolos(palabra));
				if (!palabrasIgnorar.contains(quitarSimbolos(palabra).toLowerCase()))
					vocabulario.add(quitarSimbolos(palabra));				
			}			
		}
	}
	
	private static void desconocida () {		
		int contador = 0;
		Iterator<String> it = vocabulario.iterator();
		while (it.hasNext()) {
	    	contador = 0;
	    	String palabra = it.next();
	    	for (int j = 0; j < listaPalabras.size(); j++) {
				if (palabra.equals(listaPalabras.get(j)))
					contador++;
			}
	    	if (contador <= MIN_APARICIONES) {
	    		it.remove();
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
		
		vocabulario = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		listaPalabras = new ArrayList<String>();
		
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
		if (vocabulario.first().isEmpty())
			vocabulario.remove(vocabulario.first());
		
		// Llamada al metodo de limpieza de palabras poco frecuentes
		desconocida();
		vocabulario.add("<UNK>");	
		
		// Escritura en el fichero de salida
		BufferedWriter writer = new BufferedWriter (new FileWriter (ficheroSalida));
		Iterator<String> iterator = vocabulario.iterator();
		writer.write("VOCABULARIO: \r\n");
		writer.write("Numero total de tweets: " + numTotalTweets + "\r\n");
		writer.write("Numero de palabras: " + (vocabulario.size()) + "\r\n");
	    while (iterator.hasNext()) {
	    	writer.write(iterator.next().toLowerCase() + "\r\n");
	    }
	    writer.close();
	}
	
}