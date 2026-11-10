#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <set>
#include <map>
#include <tuple>
#include <cctype>

using namespace std;

struct Transicion {
    string destino;
    string apilar;
};

typedef tuple<string, char, char> Clave;

bool esProhibido(char c) {
    return c == '#' || c == '"' || c == '\'' || c == ',' || c == '.';
}

bool esAlfanumerico(char c) {
    return isalnum(static_cast<unsigned char>(c)) != 0;
}

vector<string> separar(const string& linea) {
    vector<string> partes;
    istringstream flujo(linea);
    string parte;
    while (flujo >> parte) {
        partes.push_back(parte);
    }
    return partes;
}

vector<string> separarPorComa(const string& texto) {
    vector<string> partes;
    string actual;
    for (char c : texto) {
        if (c == ',') {
            partes.push_back(actual);
            actual.clear();
        } else {
            actual += c;
        }
    }
    partes.push_back(actual);
    return partes;
}

bool leerEstados(const string& linea, set<string>& estados) {
    vector<string> partes = separar(linea);
    if (partes.empty()) return false;
    for (const string& e : partes) {
        if (!esAlfanumerico(e[0])) return false;
        for (char c : e) {
            if (esProhibido(c)) return false;
        }
        estados.insert(e);
    }
    return true;
}

bool leerAlfabeto(const string& linea, set<char>& alfabeto) {
    vector<string> partes = separar(linea);
    if (partes.empty()) return false;
    for (const string& s : partes) {
        if (s.size() != 1 || !esAlfanumerico(s[0])) return false;
        alfabeto.insert(s[0]);
    }
    return true;
}

bool leerAlfabetoPila(const string& linea, set<char>& alfabetoPila) {
    vector<string> partes = separar(linea);
    if (partes.empty()) return false;
    for (const string& s : partes) {
        if (s.size() != 1 || esProhibido(s[0])) return false;
        alfabetoPila.insert(s[0]);
    }
    return true;
}

bool leerInicial(const string& linea, const set<string>& estados, string& inicial) {
    vector<string> partes = separar(linea);
    if (partes.size() != 1 || !estados.count(partes[0])) return false;
    inicial = partes[0];
    return true;
}

bool leerFinales(const string& linea, const set<string>& estados, set<string>& finales) {
    vector<string> partes = separar(linea);
    for (const string& e : partes) {
        if (!estados.count(e)) return false;
        finales.insert(e);
    }
    return true;
}

bool leerFondo(const string& linea, const set<char>& alfabetoPila, char& fondo) {
    vector<string> partes = separar(linea);
    if (partes.size() != 1 || partes[0].size() != 1) return false;
    if (!alfabetoPila.count(partes[0][0])) return false;
    fondo = partes[0][0];
    return true;
}

bool leerTransiciones(const string& linea, const set<string>& estados,
                      const set<char>& alfabeto, const set<char>& alfabetoPila,
                      map<Clave, Transicion>& transiciones) {
    vector<string> partes = separar(linea);
    for (const string& t : partes) {
        if (t.size() < 2 || t.front() != '(' || t.back() != ')') return false;
        vector<string> campos = separarPorComa(t.substr(1, t.size() - 2));
        if (campos.size() != 4) return false;

        const string& origen = campos[0];
        const string& simbolo = campos[1];
        const string& medio = campos[2];
        const string& palabraPila = campos[3];

        if (!estados.count(origen)) return false;
        if (simbolo.size() != 1) return false;
        if (simbolo[0] != '#' && !alfabeto.count(simbolo[0])) return false;
        if (medio.size() < 3 || medio[1] != '=') return false;
        if (!alfabetoPila.count(medio[0])) return false;

        string destino = medio.substr(2);
        if (!estados.count(destino)) return false;
        if (palabraPila.empty()) return false;

        string apilar;
        if (palabraPila != "#") {
            for (char c : palabraPila) {
                if (!alfabetoPila.count(c)) return false;
            }
            apilar = palabraPila;
        }

        Clave clave(origen, simbolo[0], medio[0]);
        auto it = transiciones.find(clave);
        if (it != transiciones.end()) {
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

bool leerPalabra(const string& linea, const set<char>& alfabeto, string& palabra) {
    vector<string> partes = separar(linea);
    if (partes.size() > 1) return false;
    if (partes.empty() || partes[0] == "#") {
        palabra.clear();
        return true;
    }
    for (char c : partes[0]) {
        if (!alfabeto.count(c)) return false;
    }
    palabra = partes[0];
    return true;
}

void imprimirPaso(const string& palabra, size_t posicion, const string& estado, const string& pila) {
    cout << palabra.substr(0, posicion) << '.' << palabra.substr(posicion)
         << ' ' << estado << ' ' << pila << '\n';
}

void computar(const string& palabra, const string& inicial, const set<string>& finales,
              char fondo, const map<Clave, Transicion>& transiciones) {
    const int LIMITE_VACIAS = 100000;
    string estado = inicial;
    string pila(1, fondo);
    string soloFondo(1, fondo);
    size_t posicion = 0;
    int vaciasSeguidas = 0;

    imprimirPaso(palabra, posicion, estado, pila);

    while (true) {
        bool aceptando = posicion == palabra.size() && finales.count(estado) && pila == soloFondo;
        if (aceptando || pila.empty()) break;

        char tope = pila[0];
        auto it = transiciones.end();
        bool consume = false;

        if (posicion < palabra.size()) {
            it = transiciones.find(Clave(estado, palabra[posicion], tope));
            consume = it != transiciones.end();
        }
        if (it == transiciones.end()) {
            it = transiciones.find(Clave(estado, '#', tope));
        }
        if (it == transiciones.end()) break;

        if (consume) {
            posicion++;
            vaciasSeguidas = 0;
        } else {
            vaciasSeguidas++;
            if (vaciasSeguidas > LIMITE_VACIAS) break;
        }

        estado = it->second.destino;
        pila = it->second.apilar + pila.substr(1);
        imprimirPaso(palabra, posicion, estado, pila);
    }

    bool aceptado = posicion == palabra.size() && finales.count(estado) && pila == soloFondo;
    cout << (aceptado ? "Aceptado" : "Rechazado") << '\n';
}

void procesar(const vector<string>& lineas) {
    set<string> estados;
    set<char> alfabeto;
    set<char> alfabetoPila;
    string inicial;
    set<string> finales;
    char fondo = 0;
    map<Clave, Transicion> transiciones;
    string palabra;

    int error = 0;
    if (!leerEstados(lineas[0], estados)) error = 1;
    else if (!leerAlfabeto(lineas[1], alfabeto)) error = 2;
    else if (!leerAlfabetoPila(lineas[2], alfabetoPila)) error = 3;
    else if (!leerInicial(lineas[3], estados, inicial)) error = 4;
    else if (!leerFinales(lineas[4], estados, finales)) error = 5;
    else if (!leerFondo(lineas[5], alfabetoPila, fondo)) error = 6;
    else if (!leerTransiciones(lineas[6], estados, alfabeto, alfabetoPila, transiciones)) error = 7;
    else if (!leerPalabra(lineas[7], alfabeto, palabra)) error = 8;

    if (error != 0) {
        cout << "Error encontrado en " << error << '\n';
        return;
    }

    computar(palabra, inicial, finales, fondo, transiciones);
}

bool leerLinea(string& linea) {
    if (!getline(cin, linea)) return false;
    while (!linea.empty() && (linea.back() == '\r' || linea.back() == '\n')) {
        linea.pop_back();
    }
    return true;
}

bool esFin(const string& linea) {
    vector<string> partes = separar(linea);
    return partes.size() == 1 && partes[0] == "_FIN";
}

int main() {
    while (true) {
        vector<string> lineas;
        string linea;
        bool terminado = false;

        for (int i = 0; i < 8; i++) {
            if (!leerLinea(linea) || esFin(linea)) {
                terminado = true;
                break;
            }
            lineas.push_back(linea);
        }

        if (terminado) break;
        procesar(lineas);
    }
    return 0;
}
