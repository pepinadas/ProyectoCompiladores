package Compilador; //Define el paquete donde se encuentra la clase ExecuteJFlex.


import jflex.exceptions.SilentExit; //Importa la excepción SilentExit de la librería JFlex.

/**
 *
 * @author yisus
 */
public class ExecuteJFlex { //Declaración de la clase ExecuteJFlex.

    public static void main(String omega[]) { //El método principal de la clase, que se ejecuta cuando se inicia el programa. Recibe una matriz de argumentos de tipo String.
        String lexerFile = System.getProperty("user.dir") + "/src/Lexer.flex", //Declara una variable de tipo String llamada lexerFile, que representa la ruta del archivo Lexer.flex.
                lexerFileColor = System.getProperty("user.dir") + "/src/LexerColor.flex"; //Declara otra variable de tipo String llamada lexerFileColor, que representa la ruta del archivo LexerColor.flex.
        try {
            jflex.Main.generate(new String[]{lexerFile, lexerFileColor}); //Genera y compila el archivo JFlex especificado en lexerFile y lexerFileColor. jflex.Main es la clase principal de JFlex que se encarga de generar código fuente a partir de archivos de especificación de analizadores léxicos.
        } catch (SilentExit ex) { //Captura la excepción SilentExit que se lanza cuando ocurre un error durante la compilación/generación del archivo JFlex.
            System.out.println("Error al compilar/generar el archivo flex: " + ex); //Imprime un mensaje de error indicando que ocurrió un problema durante la compilación/generación del archivo JFlex. El mensaje de error se concatena con la excepción ex.
        }
    }
}
