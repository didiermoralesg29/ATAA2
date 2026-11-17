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
        if (partes.length==0 || partes == null) return false;
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
        String[] partes = separarPorEspacio(linea);
        if (partes.length==0||partes == null) return false;
        for (final String s: partes) {
            if (s.length() != 1 || !esAlfanumerico(s.charAt(0))) return false;
            alfabeto.add(s.charAt(0));
        }
        return true;
    }

    public boolean leerAlfabetoPila(final String linea, Set<Character> alfabetoPila){
        String[] partes = separarPorEspacio(linea);
        if (partes.length==0||partes == null) return false;
        for (final String s : partes) {
            if (s.length() != 1 || esProhibido(s.charAt(0))) return false;
            alfabetoPila.add(s.charAt(0));
        }
        return true;
    }

    public boolean leerInicial(final String linea, final Set<String> estados, String inicial){
        String[] partes = separarPorEspacio(linea);
        if (partes.length != 1 || partes == null || !estados.contains(partes[0])) return false;
        inicial = partes[0];
        return true;
    }

    public boolean leerFinales(final String linea, final Set<String> estados, Set<String> finales){
        String[] partes = separarPorEspacio(linea);
        for (final String e : partes) {
            if (!estados.contains(e)) return false;
            finales.add(e);
        }
        return true;
    }

    public boolean leerFondo(final String linea, final Set<Character> alfabetoPila, char fondo){
        String[] partes = separarPorEspacio(linea);
        if (partes.length != 1 || partes[0].length() != 1) return false;
        if (!alfabetoPila.contains(partes[0].charAt(0))) return false;
        fondo = partes[0].charAt(0);
        return true;
    }

    public boolean leerTransiciones(final String linea, final Set<String> estados, final Set<Character> alfabeto, final Set<Character> alfabetoPila, Map<Clave, Transicion> transiciones){
        String[] partes = separarPorEspacio(linea);
        for (final String t : partes) {
            if (t.length() < 2 || !t.startsWith("(") || !t.endsWith(")")) return false;
            String[] campos = separarPorComa(t.substring(1, t.length() - 2));
            if (campos.length != 4) return false;
            final String origen = campos[0];
            final String simbolo = campos[1];
            final String medio = campos[2];
            final String palabraPila = campos[3];
            if (!estados.contains(origen)) return false;
            if (simbolo.length() != 1) return false;
            if (simbolo.charAt(0) != '#' && !alfabeto.contains(simbolo.charAt(0))) return false;
            if (medio.length() < 3 || medio.charAt(1) != '=') return false;
            if (!alfabetoPila.contains(medio.charAt(0))) return false;
            String destino = medio.substring(2);
            if (!estados.contains(destino)) return false;
            if (palabraPila.length()==0||palabraPila == null) return false;
            String apilar = "";
            if (palabraPila != "#") {
                for (char c : palabraPila.toCharArray()) {
                    if (!alfabetoPila.contains(c)) return false;
                }
                apilar = palabraPila;
            }
            Clave clave = new Clave(origen, simbolo.charAt(0), medio.charAt(0));
            var it = transiciones.get(clave);
            if (it != transiciones.size()) {
                if (it->second.destino != destino || it->second.apilar != apilar) return false;
            } else {
                transiciones[clave] = {destino, apilar};
            }
        }
        for (const auto& par : transiciones) {
            char simbolo = get<1>(par.first);
            if (simbolo != '#') {
                Clave vacia(get<0>(par.first), '#', get<2>(par.first));
                if (transiciones.count(vacia)) return false;
            }
        }
        return true;
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
