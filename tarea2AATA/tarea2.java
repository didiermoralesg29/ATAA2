package tarea2AATA;

import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class tarea2 {
    public record Transicion (String destino, String apilar) {}
    public record Clave (String estado, char simbolo, char topePila) {}

    public boolean esProhibido (char c){
        return "#\"\\,.".indexOf(c) != -1;
    }

    public boolean esAlfanumerico (char c){
        return Character.isLetterOrDigit(c);
    }

    public String[] separarPorEspacio (final String texto){
        String[] partes =  texto.trim().split("\\s+");
        return partes;
    }

    public String[] separarPorComa (final String texto){
        String[] partes =  texto.trim().split(",");
        return partes;
    }

    public boolean leerEstados(final String linea, Set<String> estados){ //seguir
        String[] partes = separarPorEspacio(linea);
        if (partes.length!=0 || partes == null) return false;
        for (final String e: partes) {
            if (!esAlfanumerico(e.charAt(0))) return false;
            for (char c : e.toCharArray()) {
                if (esProhibido(c)) return false;
            }   
            estados.add(e);
        }
        return true;
    }

    public boolean leerAlfabeto(final String linea, Set<Character> alfabeto){
        sTR partes = separar(linea);
        if (partes.empty()) return false;
        for (const string& s : partes) {
        if (s.size() != 1 || !esAlfanumerico(s[0])) return false;
        alfabeto.insert(s[0]);
        }
        return true;
    }

    public boolean leerAlfabetoPila(final String linea, Set<Character> alfabetoPila){
        return false;
    }

    public boolean leerInicial(final String linea, final Set<String> estados, String inicial){
        return false;
    }

    public boolean leerFinales(final String linea, final Set<String> estados, Set<String> finales){
        return false;
    }

    public boolean leerFondo(final String linea, final Set<String> estados, final Set<Character> alfabeto, final Set<Character> alfabetoPila, Map<Clave, Transicion> transiciones){
        return false;
    }

    public boolean leerPalabra(final String linea, final Set<Character> alfabeto, String palabra){
        return false;
    }

    public void imprimirPaso(final String linea, long posicion, final String estado, final String pila){
        
    }

    public void computar(final String palabra, final String inicial, final Set<String> finales, char fondo, final Map<Clave, Transicion> transiciones){

    }

    public static void procesar(final ArrayList<String> lineas){
        
    }

    public static boolean leerLinea(String linea){
        return false;
    }

    public static boolean esFin(final String linea){
        return false;
    }

    public static void main (String[] args){
        while (true) {
        ArrayList<String> lineas = new ArrayList<>();
        String linea = "";
        boolean terminado = false;
        for (int i = 0; i < 8; i++) {
            if (!leerLinea(linea) || esFin(linea)) {
                terminado = true;
                break;
            }
            lineas.add(linea);
        }
        if (terminado) break;
        procesar(lineas);
        }
    }


}
