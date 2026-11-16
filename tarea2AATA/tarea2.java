package tarea2AATA;

public class tarea2 {
    public record Transicion (String destino, String apilar) {}
    public record Clave (String estado, char simbolo, char topePila) {}

    public boolean esProhibido (char c){
        return "#\"\\,.".indexOf(c) != -1;
    }

    public boolean esAlfanumerico (char c){
        return Character.isLetterOrDigit(c);
    }

    public String [] separarPorEspacio (final String texto){
        String [] partes = texto.trim().split("\\s+");
        return partes;
    }

    public String [] separarPorComa (final String texto){
        String [] partes = texto.trim().split(",");
        return partes;
    }

    public boolean leerEstados(final String linea){ //seguir

        return true;
    }


















    public static void main (String[] args){
        
    }


}
