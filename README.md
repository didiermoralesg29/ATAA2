# Explicación de `proyecto2.cpp`

Simulador de autómatas con pila deterministas (APD) para el Proyecto 2.

## Cómo compilar y ejecutar

```bash
g++ -std=c++17 -o proyecto2 proyecto2.cpp
./proyecto2
```

Luego se escriben los autómatas por teclado (8 líneas cada uno) y se termina con `_FIN`.
También se puede usar un archivo de entrada: `./proyecto2 < entrada.txt`.

## Idea general

El programa repite tres pasos hasta leer `_FIN`:

1. **Leer** 8 líneas de un autómata.
2. **Validar** cada línea en orden. Si alguna falla, imprime `Error encontrado en N` y pasa al siguiente autómata.
3. **Computar** la palabra paso a paso e imprimir `Aceptado` o `Rechazado`.

## Cómo se guarda el autómata

| Dato | Línea | Estructura en C++ |
|---|---|---|
| Estados | 1 | `set<string>` |
| Alfabeto de la cinta | 2 | `set<char>` |
| Alfabeto de la pila | 3 | `set<char>` |
| Estado inicial | 4 | `string` |
| Estados finales | 5 | `set<string>` |
| Símbolo de fondo | 6 | `char` |
| Transiciones | 7 | `map<Clave, Transicion>` |
| Palabra | 8 | `string` |

Se usan `set` porque lo único que interesa es preguntar rápido "¿este elemento pertenece al conjunto?" con `.count()`.

### Las transiciones

Una transición `(q1,0,A=q2,AA)` se divide en dos partes:

- **Clave** (lo que se necesita para que aplique): `(estado, símbolo leído, tope de la pila)` → `(q1, 0, A)`
- **Transicion** (lo que se hace): `destino = q2`, `apilar = "AA"`

```cpp
typedef tuple<string, char, char> Clave;

struct Transicion {
    string destino;
    string apilar;
};
```

Guardarlas en un `map<Clave, Transicion>` tiene dos ventajas:

- Durante el cómputo se busca directamente la transición que corresponde a la situación actual.
- Un `map` no admite dos valores para la misma clave, lo que calza justo con la idea de **determinismo**: para cada situación hay a lo más una acción posible.

Cuando la palabra a apilar es `#`, se guarda `apilar = ""` (cadena vacía), que equivale a solo sacar el tope.

## Funciones auxiliares

- `esProhibido(c)`: dice si `c` es uno de los caracteres no permitidos (`#`, `"`, `'`, `,`, `.`).
- `esAlfanumerico(c)`: dice si `c` es letra o dígito.
- `separar(linea)`: corta una línea por espacios. Usa `istringstream`, que ignora espacios repetidos, por eso no importa si las transiciones vienen separadas por uno o varios espacios.
- `separarPorComa(texto)`: corta el interior de una transición por comas.

## Validación (una función por línea)

Cada función devuelve `true` si la línea es válida y `false` si no.

| Función | Línea | Qué revisa |
|---|---|---|
| `leerEstados` | 1 | Hay al menos un estado, cada uno empieza con carácter alfanumérico y no tiene caracteres prohibidos |
| `leerAlfabeto` | 2 | Cada símbolo es un solo carácter alfanumérico |
| `leerAlfabetoPila` | 3 | Cada símbolo es un solo carácter y no es prohibido |
| `leerInicial` | 4 | Es exactamente un estado y existe en la línea 1 |
| `leerFinales` | 5 | Todos los estados existen en la línea 1 |
| `leerFondo` | 6 | Es exactamente un símbolo y pertenece al alfabeto de la pila |
| `leerTransiciones` | 7 | Formato, pertenencia de cada parte y determinismo |
| `leerPalabra` | 8 | Todos sus caracteres pertenecen al alfabeto de la cinta |

En `procesar` se llaman en cadena con `if / else if`, así se reporta **solo el primer error**:

```cpp
if (!leerEstados(lineas[0], estados)) error = 1;
else if (!leerAlfabeto(lineas[1], alfabeto)) error = 2;
...
```

### Detalle de `leerTransiciones`

Para cada transición, por ejemplo `(q1,0,A=q2,AA)`:

1. Revisa que empiece con `(` y termine con `)`.
2. Quita los paréntesis y separa por comas. Deben quedar exactamente 4 campos: `q1`, `0`, `A=q2`, `AA`.
3. Campo 1: debe ser un estado existente.
4. Campo 2: debe ser un símbolo del alfabeto o `#`.
5. Campo 3: como el símbolo de pila es siempre **un solo carácter**, el primer carácter es el tope (`A`), el segundo debe ser `=` y el resto es el estado destino (`q2`).
6. Campo 4: debe ser `#` o una palabra formada solo por símbolos del alfabeto de la pila.

### Revisión de determinismo

Hay dos formas en que un autómata con pila deja de ser determinista, y ambas dan `Error encontrado en 7`:

1. **Misma clave, distinta acción.** Por ejemplo `(q1,0,A=q1,AA)` y `(q1,0,A=q2,AA)`. Se detecta al insertar en el `map`: si la clave ya existe con otro destino u otra palabra a apilar, es error.
2. **Transición vacía que compite con una normal.** Por ejemplo `(q1,#,A=...)` y `(q1,0,A=...)`: con `A` en el tope, el autómata podría leer el `0` o moverse sin leer nada. Se detecta en el segundo ciclo, que para cada transición normal revisa si existe una transición `#` con el mismo estado y tope.

## El cómputo (`computar`)

El estado de la simulación son tres variables:

- `estado`: estado actual.
- `pila`: un `string` donde el **primer carácter es el tope**. Así se imprime directo en el formato pedido (tope primero, fondo al final).
- `posicion`: cuántos símbolos de la palabra se han leído.

En cada vuelta del ciclo:

1. Si ya se cumple la condición de aceptación, o la pila quedó vacía, se detiene.
2. Mira el tope de la pila (`pila[0]`).
3. Busca una transición con el símbolo actual de la palabra. Si no hay, busca una transición vacía (`#`).
4. Si no encontró ninguna, se detiene.
5. Aplica la transición: cambia de estado y reemplaza el tope por la palabra a apilar:

```cpp
pila = it->second.apilar + pila.substr(1);
```

`pila.substr(1)` es la pila sin el tope, y delante se le pega lo que hay que apilar. Con `apilar = ""` simplemente se saca el tope.

6. Imprime el paso con `imprimirPaso`, que arma la palabra con el punto en la posición leída:

```cpp
palabra.substr(0, posicion) + '.' + palabra.substr(posicion)
```

### Condición de aceptación

Se imprime `Aceptado` solo si se cumplen las tres cosas a la vez:

- Se leyó toda la palabra (`posicion == palabra.size()`).
- El estado actual es final.
- La pila contiene solo el símbolo de fondo.

En cualquier otro caso se imprime `Rechazado`.

## El `main`

Lee de a 8 líneas. Si en cualquier momento aparece `_FIN` (o se acaba la entrada), termina. `leerLinea` además elimina el `\r` final, para que funcione igual con archivos creados en Windows.

## Decisiones tomadas donde el enunciado no es explícito

- **Transiciones `#` al terminar la palabra:** si al terminar de leer la palabra ya se cumple la condición de aceptación, se detiene y acepta, aunque quede una transición vacía disponible.
- **Ciclos infinitos de transiciones `#`:** tras 100.000 transiciones vacías seguidas, corta y rechaza.
- **Palabra vacía:** se acepta como línea en blanco o como `#`.
- **Repetidos:** un estado repetido o una transición idéntica repetida no son error.
- **Línea 5 vacía:** se permite (autómata sin estados finales, rechaza todo).

Conviene confirmar estos puntos con el profesor o ayudante.

## Ejemplo paso a paso

Con el primer autómata del enunciado y la palabra `000111`:

| Paso | Lee | Tope | Transición usada | Salida |
|---|---|---|---|---|
| inicio | | | | `.000111 q1 L` |
| 1 | 0 | L | `(q1,0,L=q1,AL)` | `0.00111 q1 AL` |
| 2 | 0 | A | `(q1,0,A=q1,AA)` | `00.0111 q1 AAL` |
| 3 | 0 | A | `(q1,0,A=q1,AA)` | `000.111 q1 AAAL` |
| 4 | 1 | A | `(q1,1,A=q2,#)` | `0001.11 q2 AAL` |
| 5 | 1 | A | `(q2,1,A=q2,#)` | `00011.1 q2 AL` |
| 6 | 1 | A | `(q2,1,A=q2,#)` | `000111. q2 L` |

Termina con la palabra completa leída, en `q2` (final) y con solo `L` en la pila: **Aceptado**.

Cada `0` apila una `A` y cada `1` saca una, por lo que el autómata reconoce palabras con la misma cantidad de ceros seguidos de unos.
