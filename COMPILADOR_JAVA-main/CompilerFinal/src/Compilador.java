//Esta línea importa la biblioteca FlatLaf, 
//que proporciona temas de apariencia plana para aplicaciones de escritorio en Java. 
//En este caso, se está utilizando el tema "FlatIntelliJLaf" que imita la apariencia de la interfaz de usuario de IntelliJ IDEA.
import com.formdev.flatlaf.FlatIntelliJLaf;
//Estas líneas importan varias clases de la biblioteca compilerTools. Cada una de estas clases se utiliza para herramientas específicas relacionadas con la compilación de código en LSSL.
import compilerTools.CodeBlock;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import compilerTools.Directory;
import compilerTools.ErrorLSSL;
import compilerTools.Functions;
import compilerTools.Grammar;
import compilerTools.Production;
import compilerTools.TextColor;
import compilerTools.Token;
//Estas líneas importan varias clases de Java que se utilizan para diferentes propósitos en la aplicación. Por ejemplo, Color se utiliza para definir colores utilizados en la 
//interfaz de usuario, File se utiliza para interactuar con archivos en el sistema operativo, HashMap y HashSet se utilizan para almacenar datos en colecciones, etc.
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
//Estas líneas importan varias clases de la biblioteca javax.swing, que se utilizan para construir la interfaz de usuario de la aplicación. 
//JOptionPane se utiliza para mostrar ventanas emergentes con mensajes, JScrollPane se utiliza para proporcionar barras de desplazamiento
//para áreas de texto grandes y JTextArea se utiliza para mostrar texto en una ventana de texto editable. 
//Timer se utiliza para realizar tareas repetitivas en un intervalo de tiempo determinado.
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author marco
 */
// Este código es una clase llamada "Compilador", que extiende de la clase 
// "javax.swing.JFrame", lo que significa que es una ventana de la interfaz gráfica de usuario. 
public class Compilador extends javax.swing.JFrame {

    private String title,ensamblador; //"title" (título de la ventana), "ensamblador" (nombre del archivo ensamblador)
    private Directory directorio; //"directorio" (directorio de trabajo)
    private String codigoIntermedio;
    private String codigoOptimizado;
    private ArrayList<Token> tokens; //"tokens" (una lista de tokens)
    private ArrayList<ErrorLSSL> errors; //"errors" (una lista de errores de LSSL)
    private ArrayList<TextColor> textsColor; //"textsColor" (una lista de colores de texto)
    private Timer timerKeyReleased; //"timerKeyReleased" (un temporizador para cuando se suelta una tecla)
    private ArrayList<Production> identProd; //"identProd" (una lista de producciones de identificador)
    private ArrayList<Production> ifProd; //"ifProd" (una lista de producciones de sentencia "if")
    private ArrayList<Production> whileProd; //"whileProd" (una lista de producciones de sentencia "while")
    private ArrayList<String> codObj; //"codObj" (una lista de códigos objeto)
    private ArrayList<String> codObjComp; //"codObjComp" (una lista de códigos objeto compilados)
    private ArrayList<String> variables; //"variables" (una lista de variables)
    
    private ArrayList<Production> funcProd; //"funcProd" (una lista de producciones de función)
    private HashMap<String, String> identificadores; //"identificadores" (un mapa de identificadores)
    private boolean codeHasBeenCompiled = false; // "codeHasBeenCompiled" (un booleano que indica si el código ha sido compilado)
    private ArrayList<Production> asigProd; //"asigProd" (una lista de producciones de asignación)
    private ArrayList<Production> asigProdConID; //"asigProdConID" (una lista de producciones de asignación con identificador)
    private ArrayList<Production> compaProdIzq; //"compaProdIzq" (una lista de producciones de comparación a la izquierda)
    private ArrayList<Production> compaProdDer; //"compaProdDer" (una lista de producciones de comparación a la derecha)
    private ArrayList<Production> compaProdDoble; //"compaProdDoble" (una lista de producciones de comparación doble)
    private ArrayList<Production> operProdIzq; //"operProdIzq" (una lista de producciones de operación a la izquierda)
    private ArrayList<Production> operProdDer; //"operProdDer" (una lista de producciones de operación a la derecha)
    private ArrayList<Production> operProdDoble; //"operProdDoble" (una lista de producciones de operación doble)
    
    private ArrayList<Production> identProdCopia; //"identProdCopia" (una lista)
    private ArrayList<Production> asigProdCopia; //"asigProdCopia" (una lista)
    ArrayList<ArrayList<Token>> prods = new ArrayList<ArrayList<Token>>(); // "prods" (una lista de listas)

    


    /**
     * Creates new form Compilador
     */
    public Compilador() {
        initComponents(); // Inicializa los componentes de la interfaz gráfica
        init(); // Llama al método personalizado para inicializar la clase
    }

    private void init() {
        title = "Compiler"; // Establece el título de la ventana
        setLocationRelativeTo(null); // Centra la ventana en la pantalla
        setTitle(title); // Establece el título de la ventana
        directorio = new Directory(this, jtpCode, title, ".comp");// Inicializa un objeto de la clase Directorio
        addWindowListener(new WindowAdapter() { // Añade un listener al evento de cierre de ventana Cuando presiona la "X" de la esquina superior derecha
            @Override
            public void windowClosing(WindowEvent e) { // Al cerrar la ventana
                directorio.Exit(); // Llama al método Exit() de la clase Directorio
                System.exit(0); // Finaliza la aplicación
            }
        });
        Functions.setLineNumberOnJTextComponent(jtpCode); // Establece los números de línea en el JTextPane jtpCode
        timerKeyReleased = new Timer(300, (ActionEvent e) -> { // Inicializa un temporizador para la escritura de código
            timerKeyReleased.stop(); // Detiene el temporizador
            int posicion = jtpCode.getCaretPosition(); // Obtiene la posición actual del cursor
            jtpCode.setText(jtpCode.getText().replaceAll("[\r]+", "")); // Remueve los saltos de línea innecesarios
            jtpCode.setCaretPosition(posicion); // Establece la posición actual del cursor
            colorAnalysis(); // Realiza un análisis de color del código
        });
        Functions.insertAsteriskInName(this, jtpCode, () -> { // Agrega un asterisco al título de la ventana si hay cambios sin guardar en el código
            timerKeyReleased.restart(); // Reinicia el temporizador
        });
        tokens = new ArrayList<>(); //una lista vacía que se utilizará para almacenar tokens.
        errors = new ArrayList<>(); //una lista vacía que se utilizará para almacenar errores de compilación.
        textsColor = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar textos de color.
        identProd = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar identificadores de productos.
        ifProd = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con la estructura "if".
        whileProd = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con la estructura "while".
        asigProd = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con asignaciones.
        asigProdConID = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con asignaciones que contienen identificadores.
        compaProdIzq = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con comparaciones en el lado izquierdo.
        compaProdDer = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con comparaciones en el lado derecho.
        compaProdDoble = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con comparaciones dobles.
        operProdIzq = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con operaciones en el lado izquierdo.
        operProdDer = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con operaciones en el lado derecho.
        operProdDoble = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con operaciones dobles.
        funcProd = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar productos relacionados con funciones.
        codObj = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar objetos de código.
        codObjComp = new ArrayList<>(); // Se crea una nueva instancia de la clase ArrayList para almacenar objetos de código comprimidos.
        variables = new ArrayList<>(); //Se crea una nueva instancia de la clase ArrayList para almacenar variables.

        identificadores = new HashMap<>(); //Se crea una nueva instancia de la clase HashMap para almacenar identificadores.
        Functions.setAutocompleterJTextComponent(new String[]{}, jtpCode, () -> { //Se configura un autocompletado para el componente JTextComponent llamado "jtpCode". Se proporciona un arreglo vacío de cadenas y una expresión lambda como argumentos.
            timerKeyReleased.restart(); //Se reinicia un temporizador llamado "timerKeyReleased".
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rootPanel = new javax.swing.JPanel(); //Se crea una nueva instancia de un JPanel llamado "rootPanel".
        jScrollPane1 = new javax.swing.JScrollPane(); //Se crea una nueva instancia de un JScrollPane llamado "jScrollPane1".
        jtpCode = new javax.swing.JTextPane(); //Se crea una nueva instancia de un JTextPane llamado "jtpCode".
        panelButtonCompilerExecute = new javax.swing.JPanel(); //Se crea una nueva instancia de un JPanel llamado "panelButtonCompilerExecute".
        btnCompilar = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "btnCompilar".
        btnCompilar1 = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "btnCompilar1".
        jButton1 = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "jButton1".
        jButton2 = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "jButton2".
        btnTripletas = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "btnTripletas".
        jButton3 = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "jButton3".
        jButton4 = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "jButton4".
        btnEjecutar = new javax.swing.JButton(); //Se crea una nueva instancia de un JButton llamado "btnEjecutar".
        jButton5 = new javax.swing.JButton(); // Se crea una nueva instancia de un JButton llamado "jButton5".
        jScrollPane2 = new javax.swing.JScrollPane(); //Se crea una nueva instancia de un JScrollPane llamado "jScrollPane2".
        jtaOutputConsole = new javax.swing.JTextArea(); //Se crea una nueva instancia de un JTextArea llamado "jtaOutputConsole".
        jScrollPane3 = new javax.swing.JScrollPane(); //Se crea una nueva instancia de un JScrollPane llamado "jScrollPane3".
        tblTokens = new javax.swing.JTable(); //Se crea una nueva instancia de un JTable llamado "tblTokens".
        jPanel1 = new javax.swing.JPanel(); //Se crea una nueva instancia de un JPanel llamado "jPanel1".
        jPanel2 = new javax.swing.JPanel(); //Se crea una nueva instancia de un JPanel llamado "jPanel2".
        jLabel2 = new javax.swing.JLabel(); //Se crea una nueva instancia de un JLabel llamado "jLabel2".
        LabelTecNM = new javax.swing.JLabel(); //Se crea una nueva instancia de un JLabel llamado "LabelTecNM".
        LabelITT = new javax.swing.JLabel(); //Se crea una nueva instancia de un JLabel llamado "LabelITT".
        jLabel5 = new javax.swing.JLabel(); //Se crea una nueva instancia de un JLabel llamado "jLabel5".

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE); // Establece que no se haga nada cuando se presione el botón de cerrar la ventana.
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS)); //Establece el layout del panel principal como un BoxLayout con dirección horizontal.

        rootPanel.setBackground(new java.awt.Color(204, 204, 204)); //Establece el color de fondo del panel principal en un tono de gris claro.

        jScrollPane1.setViewportView(jtpCode); //Agrega un componente de vista de texto desplazable para el editor de código, que permite ver y navegar el contenido del JTextPane jtpCode.

        btnCompilar.setText("Nuevo"); //Establece el texto del botón "Compilar" como "Nuevo".
        btnCompilar.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "Compilar" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "Compilar".
                btnCompilarActionPerformed(evt); //Llama al método btnCompilarActionPerformed que maneja el evento de hacer clic en el botón "Compilar".
            }
        });

        btnCompilar1.setText("Compilar"); //Establece el texto del botón "Compilar1" como "Compilar".
        btnCompilar1.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "Compilar1" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "Compilar1".
                btnCompilar1ActionPerformed(evt); //Llama al método btnCompilar1ActionPerformed que maneja el evento de hacer clic en el botón "Compilar1".
            }
        });

        jButton1.setText("Guardar"); //Establece el texto del botón "jButton1" como "Guardar".
        jButton1.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "jButton1" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "jButton1".
                jButton1ActionPerformed(evt); //Llama al método jButton1ActionPerformed que maneja el evento de hacer clic en el botón "jButton1".
            }
        });

        jButton2.setText("Abrir"); //Establece el texto del botón "jButton2" como "Abrir".
        jButton2.setToolTipText(""); //Establece un tooltip vacío para el botón "jButton2".
        jButton2.addActionListener(new java.awt.event.ActionListener() { // Agrega un escucha de evento para el botón "jButton2" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "jButton2".
                jButton2ActionPerformed(evt); //Llama al método jButton2ActionPerformed que maneja el evento de hacer clic en el botón "jButton2".
            }
        });

        btnTripletas.setText("Tripletas"); // Establece el texto del botón "btnTripletas" como "Tripletas".
        btnTripletas.setEnabled(false); //Deshabilita el botón "btnTripletas".
        btnTripletas.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "btnTripletas" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "btnTripletas".
                btnTripletasActionPerformed(evt); //Llama al método btnTripletasActionPerformed que maneja el evento de hacer clic en el botón "btnTripletas".
            }
        });

        jButton3.setText("CodObj,"); //Establece el texto del botón "jButton3" como "CodObj,".
        jButton3.setEnabled(false); //Deshabilita el botón "jButton3".
        jButton3.addActionListener(new java.awt.event.ActionListener() { // Agrega un escucha de evento para el botón "jButton3" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "jButton3".
                jButton3ActionPerformed(evt); //Llama al método jButton3ActionPerformed que maneja el evento de hacer clic en el botón "jButton3".
            }
        });

        jButton4.setText("Ensamblador"); //Establece el texto del botón "jButton4" como "Ensamblador".
        jButton4.setEnabled(false); // Deshabilita el botón "jButton4".
        jButton4.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "jButton4" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "jButton4".
                jButton4ActionPerformed(evt);// Llama al método jButton4ActionPerformed que maneja el evento de hacer clic en el botón "jButton4".
            }
        });

        btnEjecutar.setText("Ejecutar"); //Establece el texto del botón "btnEjecutar" como "Ejecutar".
        btnEjecutar.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "btnEjecutar" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "btnEjecutar".
                btnEjecutarActionPerformed(evt); //Llama al método btnEjecutarActionPerformed que maneja el evento de hacer clic en el botón "btnEjecutar".
            }
        });

        jButton5.setText("Optimizar"); //Establece el texto del botón "jButton5" como "Optimizar".
        jButton5.addActionListener(new java.awt.event.ActionListener() { //Agrega un escucha de evento para el botón "jButton5" que implementa la interfaz ActionListener.
            public void actionPerformed(java.awt.event.ActionEvent evt) { //Define el método actionPerformed que se ejecutará cuando se presione el botón "jButton5".
                jButton5ActionPerformed(evt); //Llama al método jButton5ActionPerformed que maneja el evento de hacer clic en el botón "jButton5".
            }
        });
        
        /**
            Este código está utilizando el API de Java Swing para crear un diseño de interfaz gráfica de usuario. 
            La primera línea declara e inicializa un objeto GroupLayout con el panelButtonCompilerExecute como argumento. 
            La segunda línea establece este objeto GroupLayout como el layout manager del panelButtonCompilerExecute.
            Las líneas siguientes establecen la estructura horizontal del GroupLayout, que consta de una serie de componentes de botón de JButton, cada uno separado por un espacio vertical. 
            La estructura horizontal se define usando la clase javax.swing.GroupLayout y utiliza el método group para agrupar componentes y establecer sus propiedades de alineación. 
            Cada botón tiene una preferencia de tamaño establecida mediante el método setPreferredSize. 
            Finalmente, se establece un botón btnEjecutar como el botón de ejecución con el método setExecuteButton, que toma un objeto JButton como argumento.
        */

        javax.swing.GroupLayout panelButtonCompilerExecuteLayout = new javax.swing.GroupLayout(panelButtonCompilerExecute);
        panelButtonCompilerExecute.setLayout(panelButtonCompilerExecuteLayout);
        panelButtonCompilerExecuteLayout.setHorizontalGroup(
            panelButtonCompilerExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonCompilerExecuteLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCompilar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCompilar1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTripletas)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEjecutar, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        
        /**
            Este bloque de código establece la estructura vertical del objeto GroupLayout. 
            La estructura vertical consiste en una sola fila que contiene todos los componentes de botón de la estructura horizontal. 
            El método setVerticalGroup toma un objeto Group que define la estructura vertical del GroupLayout.
            Dentro del objeto Group, se utiliza el método group para agrupar los componentes de botón y establecer sus propiedades de alineación. 
            Todos los componentes se colocan en la misma fila usando el argumento Alignment.BASELINE en el método group. 
            La llamada al método setVerticalGroup también define el tamaño preferido de la fila a través del método setPreferredSize, lo que garantiza que los componentes se ajusten adecuadamente en la ventana de la aplicación.
        */
        panelButtonCompilerExecuteLayout.setVerticalGroup(
            panelButtonCompilerExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelButtonCompilerExecuteLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelButtonCompilerExecuteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCompilar)
                    .addComponent(btnCompilar1)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(btnTripletas)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addComponent(btnEjecutar)
                    .addComponent(jButton5))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        /**
            La primera línea establece que el JTextArea no se puede editar mediante el método setEditable(false), lo que significa que el usuario no puede modificar el texto dentro de la JTextArea.
            La segunda línea establece el número de columnas en la JTextArea mediante el método setColumns(20). En este caso, se establece un ancho predeterminado de 20 columnas.
            La tercera línea establece el número de filas en la JTextArea mediante el método setRows(5). En este caso, se establece una altura predeterminada de 5 filas.
            La última línea crea un componente de JScrollPane y lo asigna a la JTextArea utilizando el método setViewportView. 
            Esto permite que el usuario desplace la JTextArea si se produce una salida de consola que excede el tamaño de la JTextArea. 
            El contenido de la JTextArea se muestra en el JScrollPane gracias al método setViewportView.
        */

        jtaOutputConsole.setEditable(false);
        jtaOutputConsole.setColumns(20);
        jtaOutputConsole.setRows(5);
        jScrollPane2.setViewportView(jtaOutputConsole);
        
        /** Este código continua el código anterior y establece el modelo de datos de la tabla con un array de String que contiene los nombres de las columnas de la tabla. 
            Además, se define una clase anónima que extiende DefaultTableModel para personalizar el comportamiento de la tabla. 
            Esta clase anónima define un array de booleanos que indica si cada celda de la tabla es editable o no. En este caso, 
            todas las celdas se establecen como no editables.
        */
        tblTokens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Componente léxico", "Lexema", "[Línea, Columna]"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
       
        
        tblTokens.getTableHeader().setReorderingAllowed(false); //Se deshabilita la capacidad de reordenar las columnas de la tabla tblTokens. Esto significa que las columnas permanecerán en su posición original y no podrán ser arrastradas y reorganizadas por el usuario.
        jScrollPane3.setViewportView(tblTokens); // Se crea un componente JScrollPane (jScrollPane3) que contiene la tabla tblTokens. Esto permite agregar barras de desplazamiento a la tabla en caso de que el contenido sea más grande que el espacio disponible.

        jPanel1.setBackground(new java.awt.Color(255, 255, 255)); //Se establece el color de fondo del JPanel jPanel1 en blanco (#FFFFFF).

        jPanel2.setBackground(new java.awt.Color(204, 204, 255)); // Se establece el color de fondo del JPanel jPanel2 en un tono de azul claro (#CCCCFF).

        jLabel2.setFont(new java.awt.Font("Bauhaus 93", 1, 18)); // NOI18N Se establece la fuente del JLabel jLabel2 como "Bauhaus 93" con negrita y un tamaño de 18 puntos.
        jLabel2.setText("Compilador - LyA II"); //Se establece el texto del JLabel jLabel2 como "Compilador - LyA II". Este JLabel se utiliza como título de la ventana o título del panel.
        
        /** Estas líneas de código se utilizan para definir el diseño (layout) del JPanel `jPanel2` utilizando GroupLayout, un administrador de diseño de contenedores que permite definir la posición y tamaño de los componentes en función de las relaciones entre ellos.
            La primera línea declara un nuevo objeto GroupLayout para el JPanel `jPanel2`. La segunda línea establece este objeto GroupLayout como el diseño del JPanel `jPanel2` utilizando el método `setLayout()`.
            Las siguientes líneas de código definen cómo se deben colocar los componentes en el JPanel `jPanel2` utilizando GroupLayout. La llamada al método `setHorizontalGroup()` establece las relaciones horizontales entre los componentes en el JPanel, mientras que la llamada al método `setVerticalGroup()` establece las relaciones verticales.
            En este caso, la disposición horizontal del JPanel `jPanel2` se establece utilizando el método `addGroup()` para agregar un grupo paralelo que contiene el JLabel `jLabel2`. La disposición vertical del JPanel `jPanel2` se establece utilizando el método `addGroup()` para agregar un grupo secuencial que contiene el JLabel `jLabel2`.
            En resumen, estas líneas de código definen cómo se debe ubicar y distribuir el JLabel `jLabel2` dentro del JPanel `jPanel2` utilizando el administrador de diseño GroupLayout.
        */

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(81, 81, 81)
                .addComponent(jLabel2)
                .addContainerGap(83, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
        /** Estas líneas de código definen la ubicación y tamaño de dos JLabels (`LabelTecNM` y `LabelITT`) dentro del JPanel `jPanel1` utilizando GroupLayout.
            Las dos primeras líneas de código establecen la imagen de fondo de los JLabels utilizando el método `setIcon()` y cargando la imagen desde un archivo en el paquete Multimedia utilizando el método `getResource()`.
            Las siguientes líneas de código definen cómo se deben colocar los JLabels dentro del JPanel `jPanel1` utilizando el administrador de diseño GroupLayout. La llamada al método `setHorizontalGroup()` establece las relaciones horizontales entre los componentes en el JPanel, mientras que la llamada al método `setVerticalGroup()` establece las relaciones verticales.
            En este caso, la disposición horizontal del JPanel `jPanel1` se establece utilizando el método `addGroup()` para agregar tres grupos paralelos: uno que contiene el JLabel `LabelTecNM`, otro que contiene el JPanel `jPanel2`, y otro que contiene el JLabel `LabelITT`. La disposición vertical del JPanel `jPanel1` se establece utilizando el método `addGroup()` para agregar un grupo secuencial que contiene los tres componentes.
            En resumen, estas líneas de código definen cómo se debe ubicar y distribuir los JLabels `LabelTecNM` y `LabelITT` y el JPanel `jPanel2` dentro del JPanel `jPanel1` utilizando el administrador de diseño GroupLayout.
        */

        LabelTecNM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Multimedia/Logo TecNM.png"))); // NOI18N

        LabelITT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Multimedia/Logo ITT.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelTecNM)
                .addGap(25, 25, 25)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                .addComponent(LabelITT)
                .addGap(54, 54, 54))
        );
        jPanel1Layout.setVerticalGroup( //Esto inicia la definición de la disposición vertical del panel jPanel1.
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //Esto crea un grupo de elementos que se colocarán uno encima del otro en el panel.
            .addGroup(jPanel1Layout.createSequentialGroup() //Esto define un nuevo grupo secundario dentro del grupo principal para agregar más elementos.
                .addGap(35, 35, 35) //Esto agrega un espacio en blanco de 35 píxeles entre el borde superior del panel y el primer elemento dentro del grupo.
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)) //Esto agrega el panel jPanel2 al grupo secundario con su tamaño predeterminado.
            .addGroup(jPanel1Layout.createSequentialGroup() //Esto define otro grupo secundario dentro del grupo principal.
                .addContainerGap() //Esto agrega un espacio en blanco predeterminado entre los elementos dentro del grupo secundario.
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //Esto crea otro grupo secundario para agregar más elementos debajo del grupo principal.
                    .addComponent(LabelITT) //Esto agrega la etiqueta LabelITT al grupo secundario.
                    .addComponent(LabelTecNM))) //Esto agrega la etiqueta LabelTecNM al grupo secundario.
        );

        jLabel5.setFont(new java.awt.Font("Bauhaus 93", 1, 18)); // NOI18N //Esto establece la fuente de la etiqueta jLabel5 a "Bauhaus 93" con un tamaño de 18 píxeles y negrita.
        jLabel5.setText("Tabla de Tokens"); //Esto establece el texto de la etiqueta jLabel5 en "Tabla de Tokens".

        javax.swing.GroupLayout rootPanelLayout = new javax.swing.GroupLayout(rootPanel); //Esto crea un nuevo GroupLayout para el panel rootPanel.
        rootPanel.setLayout(rootPanelLayout); //Esto establece el GroupLayout creado previamente como el diseño del panel rootPanel.
        rootPanelLayout.setHorizontalGroup( //Esto comienza la definición de la disposición horizontal de los elementos en el panel rootPanel.
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) 
            .addGroup(rootPanelLayout.createSequentialGroup() //Esto define un nuevo grupo secundario dentro del grupo principal para agregar más elementos.
                .addGap(22, 22, 22) //Esto agrega un espacio en blanco de 22 píxeles entre el borde izquierdo del panel y el primer elemento dentro del grupo.
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false) //Esto crea un nuevo grupo secundario para agregar más elementos debajo del grupo principal.
                    .addComponent(jScrollPane1) //Esto agrega un JScrollPane al grupo secundario.
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 716, Short.MAX_VALUE) //Esto agrega otro JScrollPane al grupo secundario con un ancho preferido de 716 píxeles y altura máxima de Short.MAX_VALUE.
                    .addComponent(panelButtonCompilerExecute, javax.swing.GroupLayout.PREFERRED_SIZE, 620, javax.swing.GroupLayout.PREFERRED_SIZE)) //Esto agrega un JPanel al grupo secundario con un ancho preferido de 620 píxeles.
                .addGap(18, 18, 18) //Esto agrega un espacio en blanco de 18 píxeles entre los elementos dentro del grupo secundario.
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //Esto crea otro grupo secundario para agregar más elementos debajo del grupo principal.
                    .addComponent(jLabel5) //Esto agrega la etiqueta jLabel5 al grupo secundario.
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 473, javax.swing.GroupLayout.PREFERRED_SIZE)) //Esto agrega otro JScrollPane al grupo secundario con un ancho preferido de 473 píxeles.
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) //Esto agrega un espacio en blanco predeterminado al final del grupo principal.
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup() //Esto define un nuevo grupo secundario dentro del grupo principal para agregar más elementos que se colocarán a la derecha del panel.
                .addContainerGap(126, Short.MAX_VALUE) //Esto agrega un espacio en blanco de 126 píxeles a la derecha del panel.
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) //Esto agrega el JPanel jPanel1 al grupo secundario con su tamaño predeterminado.
                .addGap(94, 94, 94)) //Esto agrega un espacio en blanco de 94 píxeles a la derecha del JPanel.
        );
        rootPanelLayout.setVerticalGroup( // Establece la organización vertical del panel principal
            rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //Establece la organización horizontal del panel principal con alineación izquierda
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, rootPanelLayout.createSequentialGroup() // Agrega un nuevo grupo con alineación derecha
                .addContainerGap() // Agrega un espacio en blanco en la parte superior
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) // Agrega un panel secundario con tamaño predeterminado al grupo principal
                .addGap(28, 28, 28) // Agrega un espacio en blanco después del panel secundario
                .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Agrega un nuevo grupo con alineación izquierda
                    .addGroup(rootPanelLayout.createSequentialGroup() // Agrega un nuevo subgrupo
                        .addGap(7, 7, 7) // Agrega un espacio en blanco
                        .addComponent(jLabel5) // Agrega una etiqueta de texto
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED) // Agrega un espacio en blanco
                        .addGroup(rootPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) // Agrega un nuevo subgrupo con alineación izquierda
                            .addGroup(rootPanelLayout.createSequentialGroup() // Agrega un nuevo subgrupo
                                .addComponent(jScrollPane1) // Agrega un área de texto desplazable
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED) // Agrega un espacio en blanco
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)) // Agrega otra área de texto desplazable
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE))) // Agrega otra área de texto desplazable con un tamaño predeterminado
                    .addComponent(panelButtonCompilerExecute, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)) // Agrega un panel secundario con tamaño predeterminado
                .addGap(22, 22, 22)) // Agrega un espacio en blanco después de los componentes secundarios
        );

        getContentPane().add(rootPanel); //// Agrega el panel principal a la ventana

        pack(); //// Ajusta el tamaño de la ventana para que se ajuste a los componentes que contiene el panel principal
    }// </editor-fold>//GEN-END:initComponents

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilarActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "btnCompilar"
        directorio.New(); // Crea un nuevo directorio
        clearFields(); // Limpia los campos de entrada de texto y el área de texto de salida
    }//GEN-LAST:event_btnCompilarActionPerformed

    private void btnEjecutarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "btnEjecutar"
        btnCompilar.doClick(); // Hace clic en el botón "btnCompilar"
        if (codeHasBeenCompiled) { // Verifica si el código se ha compilado con éxito
            if (!errors.isEmpty()) { // Verifica si se encontraron errores durante la compilación
                JOptionPane.showMessageDialog(null, "No se puede ejecutar el código ya que se encontró uno o más errores", 
                        "Error en la compilación", JOptionPane.ERROR_MESSAGE); // Muestra un mensaje de error si se encontraron errores
            } else { // Si no hay errores
                CodeBlock codeBlock = Functions.splitCodeInCodeBlocks(tokens, "{", "}", ";"); // Separa el código en bloques de código utilizando los caracteres "{", "}", y ";"
                System.out.println(codeBlock); // Imprime los bloques de código en la consola
                ArrayList<String> blocksOfCode = codeBlock.getBlocksOfCodeInOrderOfExec(); // Obtiene los bloques de código en el orden de ejecución
                System.out.println(blocksOfCode); // Imprime los bloques de código en el orden de ejecución en la consola

            }
        }
    }//GEN-LAST:event_btnEjecutarActionPerformed

    private void btnCompilar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompilar1ActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "btnCompilar1"
        codObjComp.clear(); // Limpia el contenido del ArrayList "codObjComp"
        variables.clear(); // Limpia el contenido del ArrayList "variables"
        compile(); // Compila el código ingresado en los campos de entrada de texto
    }//GEN-LAST:event_btnCompilar1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "jButton1"
          if (directorio.Save()) {  // Guarda el archivo actual en la ubicación especificada por el usuario y verifica si la operación fue exitosa         
            clearFields(); // Limpia los campos de entrada de texto y el área de texto de salida
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "jButton2"
         if (directorio.Open()) { // Abre el archivo seleccionado por el usuario y verifica si la operación fue exitosa
            colorAnalysis(); // Realiza un análisis de color en el código y lo muestra en los campos de entrada de texto y el área de texto de salida
            clearFields(); // Limpia los campos de entrada de texto y el área de texto de salida
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnTripletasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTripletasActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "btnTripletas"
        JTextArea textArea = new JTextArea(codigoIntermedio); // Crea un JTextArea que muestra el código intermedio
        JScrollPane scrollPane = new JScrollPane(textArea);  // Crea un JScrollPane para agregar barras de desplazamiento al JTextArea
        textArea.setLineWrap(true);  // Establece el ajuste de línea automático en true
        textArea.setWrapStyleWord(true); // Establece el ajuste de palabra automático en true
        textArea.setEditable(false); // Deshabilita la edición del JTextArea
        scrollPane.setPreferredSize( new Dimension( 400, 500 ) ); // Establece el tamaño preferido del JScrollPane
        JOptionPane.showMessageDialog(null, scrollPane, "Tripletas",JOptionPane.PLAIN_MESSAGE);  // Muestra el JScrollPane en un cuadro de diálogo       
    }//GEN-LAST:event_btnTripletasActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed // Define un método de acción que se ejecuta cuando se hace clic en el botón "jButton3"
        String obj ="------CODIGO OBJETO-------\n"; // Crea una cadena de texto que representa el código objeto
        for(String obj1: codObjComp){ obj+=obj1; }  // Agrega cada línea del código objeto a la cadena de texto
        JTextArea textArea = new JTextArea(obj); // Crea un JTextArea que muestra el código objeto
        //generamos el codigo ensamblador
        ensamblador =  ensamblador(obj.replaceAll("------CODIGO OBJETO-------\n\n\n","")); // Llama a un método "ensamblador" que genera el código ensamblador
        JScrollPane scrollPane = new JScrollPane(textArea);  // Crea un JScrollPane para agregar barras de desplazamiento al JTextArea
        textArea.setLineWrap(true);  // Establece el ajuste de línea automático en true
        textArea.setWrapStyleWord(true); // Establece el ajuste de palabra automático en true
        textArea.setEditable(false); // Deshabilita la edición del JTextArea
        scrollPane.setPreferredSize( new Dimension( 400, 500 ) ); // Establece el tamaño preferido del JScrollPane
        JOptionPane.showMessageDialog(null, scrollPane, "Codigo Objeto",JOptionPane.PLAIN_MESSAGE);   // Muestra el JScrollPane en un cuadro de diálogo
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed //Se crea un objeto JTextArea y se le asigna la cadena ensamblador como su contenido.
        //creamos la ventana emergente para presentar el codigo ensamblador
        JTextArea textArea = new JTextArea(ensamblador); 
        JScrollPane scrollPane = new JScrollPane(textArea);  //Se crea un objeto JScrollPane y se le asigna el objeto JTextArea para que sea desplazable.
        textArea.setLineWrap(true);  //Se configura la JTextArea para que ajuste automáticamente las líneas de texto.
        textArea.setWrapStyleWord(true); // Se configura la JTextArea para que no corte las palabras al final de la línea.
        textArea.setEditable(false); //Se deshabilita la edición de la JTextArea.
        scrollPane.setPreferredSize( new Dimension( 400, 500 ) ); //Se establece el tamaño preferido del JScrollPane.
        JOptionPane.showMessageDialog(null, scrollPane, "Tripletas",JOptionPane.PLAIN_MESSAGE);
        //creamos el archivo y lo ejecutamos
        archivoT(".\\COD_OBJ.asm",ensamblador);
        abrirarchivo(".\\COD_OBJ.asm");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        optimizacion();
        JTextArea textArea = new JTextArea(codigoOptimizado);
        JScrollPane scrollPane = new JScrollPane(textArea);  
        textArea.setLineWrap(true);  
        textArea.setWrapStyleWord(true); 
        textArea.setEditable(false);
        scrollPane.setPreferredSize( new Dimension( 400, 500 ) );
        JOptionPane.showMessageDialog(null, scrollPane, "Optimizacion",JOptionPane.PLAIN_MESSAGE);
                codeHasBeenCompiled = true;

    }//GEN-LAST:event_jButton5ActionPerformed
    public void archivoT(String ruta, String Objeto) {
          try {
              //String ruta = "D:\\Pollo\\Descargas\\COMPILADORES COMPAÑEROS\\MicroCompiler_09\\src\\MicroC\\codigoTres.txt";
              String contenido = Objeto;
              File file = new File(ruta);
              // Si el archivo no existe es creado
              if (!file.exists()) {
                  file.createNewFile();
              }
              FileWriter fw = new FileWriter(file);
              BufferedWriter bw = new BufferedWriter(fw);
              bw.write(contenido);
              bw.close();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }//archivoT

    public void abrirarchivo(String archivo){
           try {
                  File objetofile = new File (archivo);
                  Desktop.getDesktop().open(objetofile);
           }catch (IOException ex) {
                  System.out.println(ex);
           }
       }//fin_AbrirArchivo
    private void compile() {
        btnTripletas.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);
        clearFields();
        lexicalAnalysis();
        fillTableTokens();
        syntacticAnalysis();
        semanticAnalysis();
        
        printConsole();
        int resp = JOptionPane.showConfirmDialog(null, "¿Desea optimizar el código?", "", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            jButton4.setEnabled(false);
            optimizacion();
        } else {
            codigoIntermedio();
}
        
        //codigoIntermedio();
        codeHasBeenCompiled = true;
    }

    private void lexicalAnalysis() {
        // Extraer tokens
        Lexer lexer;
        try {
            File codigo = new File("code.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexer = new Lexer(entrada);
            while (true) {
                Token token = lexer.yylex();
                if (token == null) {
                    break;
                }
                tokens.add(token);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error al escribir en el archivo... " + ex.getMessage());
        }
    }

    private void syntacticAnalysis() {
        Grammar gramatica = new Grammar(tokens, errors);

        /*ELIMINACION DE ERRORES*/
        gramatica.delete(new String[]{"ERROR"}, 1);
        /* Mostrar gramáticas */        
        gramatica.group("REAL", "NUMERO PUNTO NUMERO");
         
                
        //-------------OPERACION---------------
        //FORMAS DE CREAR UNA FUNCION CORRECTAMENTE   
        gramatica.group("OPERACION", "REAL (SUMA|RESTA|MULTIPLICACION|DIVISION) REAL");
        gramatica.group("OPERACION", "REAL (SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO");
        gramatica.group("OPERACION", "REAL (SUMA|RESTA|MULTIPLICACION|DIVISION) ID");
        gramatica.group("OPERACION", "NUMERO (SUMA|RESTA|MULTIPLICACION|DIVISION) REAL");
        gramatica.group("OPERACION", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO",operProdIzq);
        gramatica.group("OPERACION", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION) REAL");
        gramatica.group("OPERACION", "NUMERO (SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO");
        gramatica.group("OPERACION", "NUMERO (SUMA|RESTA|MULTIPLICACION|DIVISION) ID",operProdDer);
        gramatica.group("OPERACION", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO",operProdIzq);
        gramatica.group("OPERACION", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION) ID",operProdDoble);
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) REAL");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) ID",operProdDer);
        gramatica.group("OPERACION", "REAL (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "NUMERO (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION",operProdIzq);
        //probablemente haya una mejor forma de hacer esto, ni modo!
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        gramatica.group("OPERACION", "OPERACION (SUMA|RESTA|MULTIPLICACION|DIVISION) OPERACION");
        //debería de poder servir con 20 términos entonces, creo
        //ERRORES operacion
        gramatica.group("OPERACION_ER", "NUMERO (SUMA|RESTA|MULTIPLICACION|DIVISION)",2,"ERROR_SINTACTICO: se necesita un minimo de 2 valores para ralizar la operacion [#, %]");
        gramatica.group("OPERACION_ER", "(SUMA|RESTA|MULTIPLICACION|DIVISION) NUMERO",2,"ERROR SINTACTICO: se necesita un minimo de 2 valores para ralizar la operacion [#, %]");
        gramatica.group("OPERACION_ER", "ID (SUMA|RESTA|MULTIPLICACION|DIVISION)",2,"ERROR SINTACTICO: se necesita un minimo de 2 valores para ralizar la operacion [#, %]");
        gramatica.group("OPERACION_ER", "(SUMA|RESTA|MULTIPLICACION|DIVISION) ID",2,"ERROR SINTACTICO: se necesita un minimo de 2 valores para ralizar la operacion [#, %]");
       
        //FORMA CORRECTA DE DECLARAR UNA VARIABLE------------------------------------------------------------
        gramatica.group("DECL_FLOAT", "FLOAT ID PUNTOCOMA",identProd);
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION ID PUNTOCOMA",identProd);
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION REAL PUNTOCOMA",identProd);
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION OPERACION PUNTOCOMA",identProd);
        
        gramatica.group("DECL_INT", "INT ID PUNTOCOMA",identProd);
        gramatica.group("DECL_INT", "INT ID ASIGNACION ID PUNTOCOMA",identProd);
        gramatica.group("DECL_INT", "INT ID ASIGNACION NUMERO PUNTOCOMA",identProd);
        gramatica.group("DECL_INT", "INT ID ASIGNACION OPERACION PUNTOCOMA",identProd);
        
        gramatica.group("DECL_BOOL", "BOOLEAN ID PUNTOCOMA",identProd);
        gramatica.group("DECL_BOOL", "BOOLEAN ID ASIGNACION ID PUNTOCOMA",identProd);
        gramatica.group("DECL_BOOL", "BOOLEAN ID ASIGNACION (TRUE|FALSE) PUNTOCOMA",identProd);
                
        gramatica.group("DECL_STRING", "STRING ID PUNTOCOMA",identProd);
        gramatica.group("DECL_STRING", "STRING ID ASIGNACION ID PUNTOCOMA",identProd);
        gramatica.group("DECL_STRING", "STRING ID ASIGNACION CADENA PUNTOCOMA",identProd); 
        //ERRORES SINTACTICOS---------------------------------------------------------------------------
        //POSIBLES ERRORES AL DECLARAR UNA VARIABLE INT O FLOAT  
        gramatica.group("DECL_INT", "INT ID ASIGNACION PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA ASIGNAR UN VALOR A LA VARIABLE [#, %]");
        gramatica.group("DECL_INT", "INT ID NUMERO PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID ID PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID NUMERO",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID ID",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID OPERACION PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID OPERACION",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID ASIGNACION ID",2,"ERROR_SINTACTICO: PUNTOCOMA(;) NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID ASIGNACION REAL",2,"ERROR_SINTACTICO: VALOR NO ENTERO [#, %]");

//gramatica.group("DECL_INT", "INT ID ASIGNACION NUMERO",2,"ERROR_SINTACTICO: PUNTOCOMA(;) NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT ID ASIGNACION OPERACION",2,"ERROR_SINTACTICO: PUNTOCOMA(;) NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT  ASIGNACION NUMERO PUNTOCOMA",2,"ERROR_SINTACTICO: ID NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT  ASIGNACION ID PUNTOCOMA",2,"ERROR_SINTACTICO: ID NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_INT", "INT  ASIGNACION OPERACION PUNTOCOMA",2,"ERROR_SINTACTICO: ID NO AGREGADO EN LA DECLARACION [#, %]");
        
        //POSIBLES ERRORES AL DECLARAR UN FLOAT
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA ASIGNAR UN VALOR A LA VARIABLE [#, %]");
        gramatica.group("DECL_FLOAT", "FLOAT ID REAL PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");        
        gramatica.group("DECL_FLOAT", "FLOAT ID REAL",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION REAL",2,"ERROR_SINTACTICO: PUNTOCOMA(;) NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_FLOAT", "FLOAT ASIGNACION REAL PUNTOCOMA",2,"ERROR_SINTACTICO: ID NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_FLOAT", "FLOAT ID ASIGNACION NUMERO PUNTOCOMA",2,"Error sintáctico: Valor float sin punto decimal [#, %]");

        //POSIBLES ERRORES AL DECLARAR UN FLOAT
        gramatica.group("DECL_STRING", "STRING ID ASIGNACION PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA ASIGNAR UN VALOR A LA VARIABLE [#, %]");
        gramatica.group("DECL_STRING", "STRING ID CADENA PUNTOCOMA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");        
        gramatica.group("DECL_STRING", "STRING ID CADENA",2,"ERROR_SINTACTICO: FALTA DEL TOKEN DE ASIGNACION EN LA DECLARACION [#, %]");
        gramatica.group("DECL_STRING", "STRING ID ASIGNACION CADENA",2,"ERROR_SINTACTICO: PUNTOCOMA(;) NO AGREGADO EN LA DECLARACION [#, %]");
        gramatica.group("DECL_STRING", "STRING ASIGNACION CADENA PUNTOCOMA",2,"ERROR_SINTACTICO: ID NO AGREGADO EN LA DECLARACION [#, %]");
        
        //ERRORES SEMANTICOS DE VARIABLES -------------------------------------------------------------
        gramatica.group("RESERV_INDEB", "(STRING|INT|FLOAT|BOOLEAN) (IMPORT|DEF|CLASS|IF|ELSE|FOR|IN|WHILE|RETURN)",2, "ERROR SEMANTICO \\{}: USO INDEBIDO DE PALABRAS RESERVADAS [#,%]");
       
        gramatica.group("ERROR_OP_STRING", "(SUMA|RESTA|MULTIPLICACION|DIVISION) CADENA",2, "ERROR SEMANTICO \\{}: OPERACION NO PERMITIDA PARA CADENA [#,%]");
        gramatica.group("ERROR_OP_STRING", "CADENA (SUMA|RESTA|MULTIPLICACION|DIVISION)",2, "ERROR SEMANTICO \\{}: OPERACION NO PERMITIDA PARA CADENA [#,%]");
        gramatica.group("ERROR_OP_BOOLEAN", "(SUMA|RESTA|MULTIPLICACION|DIVISION) (TRUE|FALSE)",2, "ERROR SEMANTICO \\{}: OPERACION NO PERMITIDA PARA BOOLEANO [#,%]");
        gramatica.group("ERROR_OP_BOOLEAN", "(TRUE|FALSE) (SUMA|RESTA|MULTIPLICACION|DIVISION)",2, "ERROR SEMANTICO \\{}: OPERACION NO PERMITIDA PARA BOOLEANO [#,%]");

        gramatica.group("DECL_INT", "(INT ID ASIGNACION REAL PUNTOCOMA)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES ENTERO [#,%]");
        gramatica.group("DECL_INT", "(INT ID ASIGNACION CADENA)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES ENTERO [#,%]");
        gramatica.group("DECL_INT", "(INT ID ASIGNACION (TRUE|FALSE))",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES ENTERO [#,%]");
        gramatica.group("DECL_INT", "INT",2,"ERROR");
        
        gramatica.group("DECL_FLOAT", "(FLOAT ID ASIGNACION CADENA)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES DECIMAL [#,%]");
        gramatica.group("DECL_FLOAT", "(FLOAT ID ASIGNACION (TRUE|FALSE))",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES DECIMAL [#,%]");
        gramatica.group("DECL_FLOAT", "(FLOAT ID ASIGNACION NUMERO PUNTOCOMA)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES DECIMAL [#,%]");
        gramatica.group("ERROR_FLOAT", "FLOAT",2,"ERROR");
        
        gramatica.group("DECL_STRING", "(STRING ID ASIGNACION NUMERO)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES CADENA [#,%]");
        gramatica.group("DECL_STRING", "(STRING ID ASIGNACION (TRUE|FALSE))",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES CADENA [#,%]");
        gramatica.group("DECL_STRING", "STRING",2,"ERROR");
        
        gramatica.group("ERROR_ASIG_BOOL", "(BOOLEAN ID ASIGNACION NUMERO)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES BOOLEANO [#,%]");
        gramatica.group("ERROR_ASIG_BOOL", "(BOOLEAN ID ASIGNACION (TRUE|FALSE))",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES BOOLEANO [#,%]");
        gramatica.group("ERROR_ASIG_BOOL", "(BOOLEAN ID ASIGNACION CADENA)",2, "ERROR SEMANTICO \\{}: VALOR ASIGNADO NO ES BOOLEANO [#,%]");
        
        
        //ASIGNACION DE UN ID
        gramatica.group("PROD_ASIG", "ID ASIGNACION (CADENA|REAL|NUMERO|TRUE|FALSE) PUNTOCOMA",asigProd);
        gramatica.group("PROD_ASIG", "ID ASIGNACION OPERACION PUNTOCOMA",asigProd);
        gramatica.group("PROD_ASIG_ID", "ID ASIGNACION ID PUNTOCOMA",asigProdConID);
        
        //--------------------------------------------------------------------------------------------
        
        //-------------CONDICION--------------------------
        //FORMAS CORRECTAS DE CREAR UNA CONDICION
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) NUMERO");
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) OPERACION");
        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) OPERACION");

        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) NUMERO");
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) REAL");
        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) REAL");
        gramatica.group("CONDICION", "(TRUE|FALSE) (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) (TRUE|FALSE)");
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ID",compaProdDer);
        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ID",compaProdDer);
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) NUMERO",compaProdIzq);
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) OPERACION",compaProdIzq);
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) REAL",compaProdIzq);
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ID",compaProdDoble);
        gramatica.group("CONDICION", "(TRUE|FALSE) (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ID",compaProdDer);
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) (TRUE|FALSE)",compaProdIzq);

        gramatica.group("CONDICION", "CONDICION (AND|OR) CONDICION");
        
        //ERRORES SEMANTICOS
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) CADENA",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) CADENA",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        gramatica.group("CONDICION", "CADENA (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) NUMERO",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        gramatica.group("CONDICION", "CADENA (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) REAL",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) (TRUE|FALSE)",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        gramatica.group("CONDICION", "REAL (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) (TRUE|FALSE)",2,"ERROR_SEMANTICO \\{}: DATOS INCOMPATIBLES PARA SU COMPARACION [#, %]");
        
        
        
           
        //----------------------------------------------------------------------------------------------
        
        //----------------WHILE Y IF-----------------------
        //FORMAS CORRECTAS DE DECLARAR UN IF
        gramatica.group("INSTR_IF", "IF PARENTESISABIERTO CONDICION PARENTESISCERRADO LLAVEABIERTO",true,ifProd);
        //FORMAS CORRECTAS DE DECLARAR UN WHILE
        gramatica.group("INSTR_WHILE", "WHILE PARENTESISABIERTO CONDICION PARENTESISCERRADO LLAVEABIERTO",whileProd);
        //POSIBLES ERRORES AL DECLARAR UN IF
        gramatica.group("INSTR_IF", "IF PARENTESISABIERTO PARENTESISCERRADO LLAVEABIERTO",true,4,"ERROR_SINTACTICO: FALTA LA CONDICION [#, %]");
        gramatica.group("INSTR_IF", "IF CONDICION PARENTESISCERRADO LLAVEABIERTO",true,4,"ERROR_SINTACTICO: FALTA EL PARENTESIS ABIERTO EN LA CONDICION [#, %]");
        gramatica.group("INSTR_IF", "IF PARENTESISABIERTO CONDICION PARENTESISCERRADO",true,4,"ERROR_SINTACTICO: FALTA DE LLAVE DE APERTURA [#, %]");
        gramatica.finalLineColumn();
        gramatica.group("INSTR_IF", "IF PARENTESISABIERTO CONDICION",true,4,"ERROR_SINTACTICO: ERROR EN LA CONDICION O FALTA DEL PARENTESIS [#, %]");
        gramatica.initialLineColumn();
        gramatica.group("INSTR_IF", "IF",2,"ERROR");
        
        //POSIBLES ERRORES DE WHILE
        gramatica.group("INSTR_WHILE", "WHILE PARENTESISABIERTO PARENTESISCERRADO",true,4,"ERROR_SINTACTICO: FALTA LA CONDICION [#, %]");
        gramatica.group("INSTR_WHILE", "WHILE CONDICION PARENTESISCERRADO",true,4,"ERROR_SINTACTICO: FALTA EL PARENTESIS ABIERTO EN LA CONDICION [#, %]");
        gramatica.group("INSTR_WHILE", "WHILE PARENTESISABIERTO CONDICION PARENTESISCERRADO",true,4,"ERROR_SINTACTICO: FALTA DE LLAVE DE APERTURA [#, %]");        
        gramatica.finalLineColumn();
        gramatica.group("INSTR_WHILE", "WHILE PARENTESISABIERTO CONDICION",true,4,"ERROR_SINTACTICO: ERROR EN LA CONDICION O FALTA DEL PARENTESIS [#, %]");
        gramatica.initialLineColumn();
        gramatica.group("INSTR_WHILE", "WHILE",2,"ERROR WHILE");
        //POSIBLES ERRORES EN LAS CODICIONES
        gramatica.group("CONDICION", "NUMERO (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ",2,"ERROR_SINTACTICO: ERROR EN LA CONDICION [#, %]");
        gramatica.group("CONDICION", "ID (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ",2,"ERROR_SINTACTICO: ERROR EN LA CONDICION [#, %]");
        gramatica.group("CONDICION", " (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) NUMERO ",2,"ERROR_SINTACTICO: ERROR EN LA CONDICION [#, %]");
        gramatica.group("CONDICION", " (IGUAL|DIFERENTE|MAYORQUE|MENORQUE|MAYORIGUALQUE|MENORIGUALQUE) ID ",2,"ERROR_SINTACTICO: ERROR EN LA CONDICION [#, %]");       
        gramatica.group("CONDICION", "CONDICION (AND|OR)",2,"ERROR_SINTACTICO: ERROR EN LA CONDICION [#, %]");
        //------------------------------------------------------------------------

        //------------------------------------------------------------
        //INPUT-------------------------------------------------------
        //FORMA CORRECTA 
        gramatica.group("INSTR_INPUT", "INPUT PARENTESISABIERTO CADENA PARENTESISCERRADO PUNTOCOMA ",funcProd);
        gramatica.group("INSTR_INPUT", "INPUT PARENTESISABIERTO ID PARENTESISCERRADO PUNTOCOMA ",funcProd);
        
        gramatica.group("INSTR_OUTPUT", "OUTPUT PARENTESISABIERTO CADENA PARENTESISCERRADO PUNTOCOMA ",funcProd);
        gramatica.group("INSTR_OUTPUT", "OUTPUT PARENTESISABIERTO ID PARENTESISCERRADO PUNTOCOMA ",funcProd);
        
        //ERRORES SINTACTICOS 
        gramatica.group("INSTR_INPUT", "INPUT PARENTESISABIERTO CADENA PARENTESISCERRADO",2, "ERROR_SINTACTICO \\{}: FALTA DEL TOKEN (;) [#,%]");
        gramatica.group("INSTR_INPUT", "INPUT CADENA PARENTESISCERRADO PUNTOCOMA",2, "ERROR_SINTACTICO \\{}: FALTA DEL PARENTESIS ABIERTO [#,%]");
        
        //ERROR SEMANTICO
        gramatica.group("INSTR_INPUT", "INPUT PARENTESISABIERTO (NUMERO|REAL) PARENTESISCERRADO PUNTOCOMA ",2, "ERROR SEMANTICO \\{}: VALOR INVALIDO EN INPUT[#,%]");
        gramatica.group("INSTR_INPUT", "INPUT",2,"ERROR INPUT");    
        //ERRORES SINTACTICOS 
        gramatica.group("INSTR_INPUT", "OUTPUT PARENTESISABIERTO CADENA PARENTESISCERRADO",2, "ERROR_SINTACTICO \\{}: FALTA DEL TOKEN (;) [#,%]");
        gramatica.group("INSTR_INPUT", "OUTPUT CADENA PARENTESISCERRADO PUNTOCOMA",2, "ERROR_SINTACTICO \\{}: FALTA DEL PARENTESIS ABIERTO [#,%]");
        
        //ERROR SEMANTICO
        gramatica.group("INSTR_INPUT", "OUTPUT PARENTESISABIERTO (NUMERO|REAL) PARENTESISCERRADO PUNTOCOMA ",2, "ERROR SEMANTICO \\{}: VALOR INVALIDO EN INPUT[#,%]");
        gramatica.group("INSTR_INPUT", "OUTPUT",2,"ERROR INPUT"); 
        
        //------------------------------------------------------------
        
        //FORMAS DE CREAR UNA FUNCION CORRECTAMENTE
        gramatica.group("PARAMETROS", "ID (COMA ID)+");
        gramatica.group("PARAMETRO", "ID");        
        gramatica.group("FUNCION", "DEF ID PARENTESISABIERTO (PARAMETRO | PARAMETROS)? PARENTESISCERRADO ", true);
        
        gramatica.group("LLAMAR_FUNCION", "ID PARENTESISABIERTO (PARAMETRO | PARAMETROS)? PARENTESISCERRADO ", true);

         //posibles errores al declarar una funcion        
        gramatica.group("FUNCION", "DEF ID (PARAMETRO | PARAMETROS)? PARENTESISCERRADO ", true,3,"ERROR_SINTACTICO: FALTA PARENTESIS ABIERTO [#, %]");
        gramatica.group("FUNCION", "DEF ID PARENTESISABIERTO (PARAMETRO | PARAMETROS)? ", true,3,"ERROR_SINTACTICO: FALTA PARENTESIS CERRADO O UN PARAMETRO ESTA MAL DECLARADO [#, %]");
        gramatica.group("FUNCION", "DEF PARENTESISABIERTO (PARAMETRO | PARAMETROS)? PARENTESISCERRADO", true,3,"ERROR_SINTACTICO: FALTA NOMBRAR LA FUNCION [#, %]");

        
       /*
        
        //ERRORES ESTRUCTURAS DE CONTROL
        gramatica.loopForFunExecUntilChangeNotDetected(()->{
            gramatica.initialLineColumn();
            gramatica.group("ESTRUCTURAS_CONTROL", "(INSTR_IF|INSTR_WHILE|FUNCION) LLAVEABIERTO (ESTRUCTURAS_CONTROL|INT|FLOAT|STRING)", true,4,"ERROR_SINTACTICO: FALTA LLAVE DE CIERRE [#, %]");
            gramatica.group("ESTRUCTURAS_CONTROL", "(INSTR_IF|INSTR_WHILE|FUNCION) (ESTRUCTURAS_CONTROL|INT|FLOAT|STRING) LLAVECERRADO", true,4,"ERROR_SINTACTICO: FALTA LLAVE DE APERTURA [#, %]");
            //gramatica.group("ESTRUCTURAS_CONTROL", "(INSTR_IF|INSTR_WHILE|FUNCION) LLAVEABIERTO", true,4,"ERROR_SINTACTICO: FALTA LLAVE DE CIERRE [#, %]");
            //gramatica.group("ESTRUCTURAS_CONTROL", "(INSTR_IF|INSTR_WHILE|FUNCION)  LLAVECERRADO", true,4,"ERROR_SINTACTICO: FALTA LLAVE DE ABERTURA [#, %]");
            gramatica.finalLineColumn();
        });
        gramatica.delete(new String[]{"LLAVEABIERTO","LLAVECERRADO"},16,"ERROR_SINTACTICO: LA LLAVE [] NO ESTA CONTENIDA EN ALGUNA GRUPACION");
        gramatica.show();
        */
        
        //gramatica.group("ESTRUCTURAS_CONTROL", "(INSTR_IF|INSTR_WHILE|FUNCION) LLAVEABIERTO (INSTR_IF|INSTR_WHILE|FUNCION|VARIABLE) LLAVECERRADO", true,4,"ERROR_SINTACTICO: FALTA LLAVE DE CIERRE [#, %]");
   
        //--------------------------------------------------------SEMANTICO------------------------------------------------------------------------------
  
        //gramatica.group("RESERV_INDEB", "INT",2, "ERROR SEMANTICO \\{}: ERROR [#,%]");
        //gramatica.group("RESERV_INDEB", "FLOAT",2, "ERROR SEMANTICO \\{}: ERROR [#,%]");
        //gramatica.group("DIV_CERO", "DIVISION 0",2, "ERROR SEMANTICO \\{}: DIVISION ENTRE CERO[#,%]");

        gramatica.show();
    }

    private void semanticAnalysis() {
        HashMap<String,String> tiposDatos = new HashMap<>();
        tiposDatos.put("NUMERO", "INT");
        tiposDatos.put("REAL", "FLOAT");
        tiposDatos.put("CADENA", "STRING");
        tiposDatos.put("TRUE", "BOOLEAN");
        tiposDatos.put("FALSE", "BOOLEAN");
        int i = 0;
        for(Production id: identProd){
            //System.out.println(id.lexemeRank(0,-1)); //int x = 4 ;
            //System.out.println(id.lexemeRank(1)); //x
            //System.out.println(id.lexicalCompRank(0,-1)); //INT ID ASIGNACION NUMERO PUNTOCOMA
            if (!identificadores.containsKey(id.lexemeRank(1))){
                identificadores.put(id.lexemeRank(1), id.lexicalCompRank(0));
                i++;
            }
            else {
                errors.add(new ErrorLSSL(1,"Error semántico: Ya existe un identificador llamado "+id.lexemeRank(1),id,true));
            }

        }
        System.out.println(Arrays.asList(identificadores)); // muestra identificadores
        for (Production id: asigProd){
            if (!identificadores.containsKey(id.lexemeRank(0))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable \""+id.lexemeRank(0)+"\" no declarada. [#, %]",id,true));
            }
            else{
                if (!identificadores.get(id.lexemeRank(0)).equals(tiposDatos.get(id.lexicalCompRank(2)))){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo "+identificadores.get(id.lexemeRank(0)) +  " [#, %]",id,true));
                   
                }
            }
            
        }
        for (Production id: asigProdConID){
            if (!identificadores.containsKey(id.lexemeRank(0))||!identificadores.containsKey(id.lexemeRank(2))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable no declarada. [#, %]",id,true));
            }
            else{
                if (!identificadores.get(id.lexemeRank(0)).equals(identificadores.get(id.lexemeRank(2)))){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo "+identificadores.get(id.lexemeRank(0)) +  " [#, %]",id,true));
                   
                }
            }
            
        }
        //comparacion cuando ID está en la izquierda
        for (Production id: compaProdIzq){

            if (!identificadores.containsKey(id.lexemeRank(0))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(0)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(0)).matches("STRING")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo STRING, imposible comparar [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("BOOLEAN")&&!id.lexicalCompRank(1).matches("IGUAL|DIFERENTE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo BOOLEAN, sólo posible comparar con operadores IGUAL y DIFERENTE [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("BOOLEAN")&&!id.lexicalCompRank(2).matches("TRUE|FALSE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo BOOLEAN, sólo posible comparar con valores booleanos [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("INT|FLOAT")){
                    if(!id.lexicalCompRank(2).matches("NUMERO|REAL|ID")){
                        errors.add(new ErrorLSSL(1,"Error semántico : Valor numérico de variable \""+id.lexemeRank(0)+"\" no se puede comparar con valor no numérico [#, %]",id,true));
                    }
                }
                
            
            }
            
        }// FOR  COMPAPRODIZQ
        
        for (Production id: compaProdDer){

            if (!identificadores.containsKey(id.lexemeRank(2))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(2)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(2)).matches("STRING")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo STRING, imposible comparar [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(2)).matches("BOOLEAN")&&!id.lexicalCompRank(1).matches("IGUAL|DIFERENTE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo BOOLEAN, sólo posible comparar con operadores IGUAL y DIFERENTE [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(2)).matches("BOOLEAN")&&!id.lexicalCompRank(0).matches("TRUE|FALSE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo BOOLEAN, sólo posible comparar con valores booleanos [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(2)).matches("INT|FLOAT")){
                    if(!id.lexicalCompRank(0).matches("NUMERO|REAL")){
                        errors.add(new ErrorLSSL(1,"Error semántico : Valor numérico de variable \""+id.lexemeRank(2)+"\" no se puede comparar con valor no numérico [#, %]",id,true));
                    }
                }
                
            
            }
            
        }// FOR  COMPAPRODDER
        for (Production id: compaProdDoble){

            if (!identificadores.containsKey(id.lexemeRank(0))||!identificadores.containsKey(id.lexemeRank(2))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(0)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(0)).matches("STRING")||identificadores.get(id.lexemeRank(2)).matches("STRING")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo STRING, imposible comparar [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("BOOLEAN")&&!id.lexicalCompRank(1).matches("IGUAL|DIFERENTE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo BOOLEAN, sólo posible comparar con operadores IGUAL y DIFERENTE [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(2)).matches("BOOLEAN")&&!id.lexicalCompRank(1).matches("IGUAL|DIFERENTE")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo BOOLEAN, sólo posible comparar con operadores IGUAL y DIFERENTE [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("BOOLEAN")&&!identificadores.get(id.lexemeRank(2)).matches("BOOLEAN")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo BOOLEAN, sólo posible comparar con valores booleanos [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(2)).matches("BOOLEAN")&&!identificadores.get(id.lexemeRank(0)).matches("BOOLEAN")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo BOOLEAN, sólo posible comparar con valores booleanos [#, %]",id,true));
                }
                if (identificadores.get(id.lexemeRank(0)).matches("INT|FLOAT")){
                    if(!identificadores.get(id.lexemeRank(2)).matches("INT|FLOAT")){
                        errors.add(new ErrorLSSL(1,"Error semántico : Valor numérico de variable \""+id.lexemeRank(0)+"\" no se puede comparar con valor no numérico [#, %]",id,true));
                    }
                }
                if (identificadores.get(id.lexemeRank(2)).matches("INT|FLOAT")){
                    if(!identificadores.get(id.lexemeRank(0)).matches("INT|FLOAT")){
                        errors.add(new ErrorLSSL(1,"Error semántico : Valor numérico de variable \""+id.lexemeRank(0)+"\" no se puede comparar con valor no numérico [#, %]",id,true));
                    }
                }
                
            
            }
            
        }// FOR  COMPAPRODdoble
        for (Production id: operProdIzq){

            if (!identificadores.containsKey(id.lexemeRank(0))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(0)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(0)).matches("STRING|BOOLEAN")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo "+identificadores.get(id.lexemeRank(0)) +", imposible hacer operaciones aritméticas [#, %]",id,true));
                }
      
            }
            if (identificadores.get(id.lexemeRank(0)).matches("INT")&& id.lexicalCompRank(1).matches("DIVISION")){
                    errors.add(new ErrorLSSL(1,"Error semántico : División en valor entero [#, %]",id,true));
                }
        }// FOR  OPERPRODIZQ
        for (Production id: operProdDer){

            if (!identificadores.containsKey(id.lexemeRank(2))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(2)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(2)).matches("STRING|BOOLEAN")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo "+identificadores.get(id.lexemeRank(0)) +", imposible hacer operaciones aritméticas [#, %]",id,true));
                }
      
            }
            
        }// FOR  OPERPRODDER
        for (Production id: operProdDoble){

            if (!identificadores.containsKey(id.lexemeRank(0))||!identificadores.containsKey(id.lexemeRank(2))){
                errors.add(new ErrorLSSL(1,"Error semántico: Variable "+id.lexemeRank(0)+" no declarada. [#, %]",id,true));
            }
            else{
                if (identificadores.get(id.lexemeRank(0)).matches("STRING|BOOLEAN")||identificadores.get(id.lexemeRank(2)).matches("STRING|BOOLEAN")){
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(0)+"\" es de tipo "+identificadores.get(id.lexemeRank(0)) +", imposible hacer operaciones aritméticas [#, %]",id,true));
                    errors.add(new ErrorLSSL(1,"Error semántico : Variable \""+id.lexemeRank(2)+"\" es de tipo "+identificadores.get(id.lexemeRank(2)) +", imposible hacer operaciones aritméticas [#, %]",id,true));

                }
      
            }
            
        }// FOR  OPERPRODDOBLE
    }
    private void codigoIntermedio(){
        ArrayList<Token> toks = new ArrayList<Token>();
        codigoIntermedio = ("--Código intermedio--\n");
        int temp;
    //revisa las declaraciones
        for (Production id: identProd){
            temp = 1;
            if(id.lexicalCompRank(2).equals("ASIGNACION")&&id.getSizeTokens()>5){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codObj.add("\n\n=============================\n;"+id.lexemeRank(0, -1)+"\n=============================");
                
                toks = id.getTokens();
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")||toks.get(i).getLexeme().equals("/")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = * /
                    
                }//for cada 
                for (int i = 0; i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("+")||toks.get(i).getLexeme().equals("-")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = + -
                }
                codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(1)+" = "+"T"+(temp-1));
                //para guardar las variables declaradas para posteriormente utilizarlo en el metodo ensamblador               
                variables.add(id.lexemeRank(1));
                //codigo Objeto
                codObjComp.add(objectCode(codObj));
                System.out.println(codObjComp.get(0));
                codObj.clear();
            }//if hay asignacion
            
        }//for producciones
    //revisa asignaciones
        for (Production id: asigProd){ 
            temp = 1;
            if(id.lexicalCompRank(1).equals("ASIGNACION")&&id.getSizeTokens()>5){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codObj.add("\n\n=============================\n;"+id.lexemeRank(0, -1)+"\n=============================");
                
                toks = id.getTokens();
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")||toks.get(i).getLexeme().equals("/")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                                                
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = * /
                    
                }//for cada 
                for (int i = 0; i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("+")||toks.get(i).getLexeme().equals("-")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());                       
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = + -
                }
               codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(0)+" = "+"T"+(temp-1));
               //para guardar las variables declaradas para posteriormente utilizarlo en el metodo ensamblador
               variables.add(id.lexemeRank(0));
               //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////7
               //codigo Objeto
                codObjComp.add(objectCode(codObj));
                System.out.println(codObjComp.get(0));
                codObj.clear();
                //System.out.println(objectCode(codObj));
            }//if hay asignacion
        
            
        }
        //INPUT Y OUTPUT
        for (Production id: funcProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codigoIntermedio = codigoIntermedio + ("\nparam "+id.lexemeRank(2)+"\ncall "+id.lexemeRank(0)+", 1");

            }//FOR FUNCPROD
        //IF
        for (Production id: ifProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codigoIntermedio = codigoIntermedio + ("\nT1 = "+id.lexemeRank(2,-3)+"\nif_false T1 goto L1 "+"\n.\n.\n.\nlabel L1");

            }//FOR IFPROD
        //WHILE
        for (Production id: whileProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codigoIntermedio = codigoIntermedio + ("\nlabel L1\nT1 = "+id.lexemeRank(2,-3)+"\nif_false T1 goto L2 "+"\n.\n.\n.\ngoto L1\nlabel L2");

            }//FOR WHILEPROD
        //System.out.print(codigoIntermedio);
    }//codigoIntermedio

    
    //////////////////GENERACION DE CODIGO OBJETO///////////////////
    private String objectCode(ArrayList<String> tripletas1) {
        ArrayList<String> tripletas = new ArrayList<String>();
        tripletas = tripletas1; 
        String tl=tripletas.get(0)+"\n";tripletas.remove(0);
        
        String inst, R0, R1, R2, R3, op, m;
        int caso = 0;
        inst = R1 = R0 = R2 = R3 = op = m =  "";
        int index = 0;
        //oCode

        for (String tripleta : tripletas) {
            
            tripleta = tripleta.replaceAll("T[1-9] = ", "").replaceAll("\\n", "");
            
            //JOptionPane.showMessageDialog(null,tripleta);
            // Definimos que operacion es
            if (tripleta.contains("*")) {
                inst = "MUL";
                op = "*";
            }
            if (tripleta.contains("/")) {
                inst = "DIV";
                op = "/";
            }
            if (tripleta.contains("-")) {
                inst = "SUB";
                op = "-";
            } else if (tripleta.contains("+")) {
                inst = "ADD";
                op = "+";
            }
            // Definimos que operacion es
            
            //Condicionales para ver el orden de la operacion
            if (R0.isEmpty() && R1.isEmpty()) {
                //JOptionPane.showMessageDialog(null,"caso 0");
                R0 = (tripleta.substring(0, tripleta.indexOf(op))).replaceAll(" ", "");
                R1 = (tripleta.substring(tripleta.indexOf(op) + 1)).replaceAll(" ", "");

            }else if ((tripleta.substring(0, tripleta.indexOf(op))).contains("T") && (tripleta.substring(tripleta.indexOf(op) + 1)).contains("T") ) {//2 TEMPORALES
                R0 = "R0";
                R1="R1";
                caso =4 ;
                //JOptionPane.showMessageDialog(null,"caso 4");
            } else if ((tripleta.substring(0, tripleta.indexOf(op))).contains("T")) {//temporal izquierdo
                R1 = (tripleta.substring(tripleta.indexOf(op) + 1)).replaceAll(" ", "");
                caso =1 ;
                //JOptionPane.showMessageDialog(null,"caso 1");
            } else if ((tripleta.substring(tripleta.indexOf(op) + 1)).contains("T")) {//temporal derecho
                R1 = (tripleta.substring(0, tripleta.indexOf(op))).replaceAll(" ", "");
                caso =2;                
                //JOptionPane.showMessageDialog(null,"caso 2");
            }else{
                R1 = (tripleta.substring(0, tripleta.indexOf(op))).replaceAll(" ", "");
                R2 = (tripleta.substring(tripleta.indexOf(op) + 1)).replaceAll(" ", "");
                caso=5;
                //JOptionPane.showMessageDialog(null,"caso 5");
            }

            //Condicionales para ver el orden de la operacion
            switch (caso) {
                case 1:
                    m+="LD R1," + R1+"\n";
                    m+=inst + " R0,R0,R1"+"\n";
                    break;
                case 2:
                    m+="LD R1," + R1+"\n";
                    m+=inst + " R0,R1,R0"+"\n";
                    break;
                case 3:
                    m+="LD R1," + R2+"\n";
                    m+="LD R2," + R2+"\n";
                    m+=inst + " R1,R1,R2"+"\n";
                    break;
                case 4:
                    m+=inst+" "+R0+","+R0+","+R1;
                    break;
                case 5:
                    m+="LD R1," + R1+"\n";
                    m+="LD R2," + R2+"\n";
                    m+=inst + " R1,R1,R2"+"\n";
                    caso =0;
                    break;
                default:
                    m+="LD R0," + R0+"\n";
                    m+="LD R1," + R1+"\n";
                    m+=inst + " R0,R0,R1"+"\n";
                    caso =0;
            }
            
            // caso = true;
        }//Recorrer tripletas
        //System.out.println(tl+m);
        return (tl+m);
    }//fin_codObjeto
    
    private void optimizacion(){
        ArrayList<Token> toks = new ArrayList<Token>();
        codigoIntermedio = ("--Código intermedio--\n");
        int temp;
        int divisor;
        float frac;
    //revisa las declaraciones
        for (Production id: identProd){
            temp = 1;
            System.out.println(id.lexemeRank(0, -1));
            

            if(id.lexicalCompRank(2).equals("ASIGNACION")&&id.getSizeTokens()>5){
                toks = id.getTokens();
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("/")){
                        if(toks.get(i+1).getLexicalComp().equals("NUMERO")){
                            divisor = Integer.parseInt(toks.get(i+1).getLexeme());
                            System.out.println("DIVISOR: "+divisor);
                            frac = 1 / (float)divisor;
                            System.out.println("DIVISOR: "+frac);
                            toks.remove(i);
                            toks.remove(i);
                            toks.add(i, new Token("*", "MULTIPLICACION",i,i));
                            toks.add(i+1, new Token(Float.toString(frac), "REAL",i,i));
                        }
                    }//if token = /
                }//for elimina divisiones
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")){
                        if(toks.get(i-1).getLexeme().equals("0")){
                            toks.remove(i);
                            toks.remove(i);
                        }
                        if(toks.get(i+1).getLexeme().equals("0")){
                            i--;
                            toks.remove(i);
                            toks.remove(i);
                        }
                        
                    }//if token = *
                }//for multiplicaciones 

                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")){
                        if(toks.get(i+1).getLexeme().equals("2")){
                            toks.remove(i);
                            toks.remove(i);
                            toks.add(i,new Token("+", "SUMA",i,i));
                            toks.add(i+1,new Token(toks.get(i-1).getLexeme(), "NUMERO",i,i));            
                    }//if token = 2
                        
                    else if(toks.get(i+1).getLexeme().equals("1")){
                    toks.remove(i);
                    toks.remove(i);
                }// if token = 1
                    }//if token multiplicacion
                }//for multiplicaciones por dos
                
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexicalComp().matches("SUMA|RESTA")){
                        if(toks.get(i-1).getLexeme().equals("0")){
                            i--;
                            toks.remove(i);
                            toks.remove(i);
                    }else 
                        if(toks.get(i+1).getLexeme().equals("0")){
                            toks.remove(i);
                            toks.remove(i);
          
                    }//if token = 0

                    }//if token suma
                }//for sumas cero
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codObj.add("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");

                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")||toks.get(i).getLexeme().equals("/")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = * /
                    
                }//for cada 
                for (int i = 0; i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("+")||toks.get(i).getLexeme().equals("-")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = + -
                }
                if(temp==1){
                    codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(1)+" = " + id.lexemeRank(3));
                }else
                codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(1)+" = "+"T"+(temp-1));
                //para guardar las variables declaradas para posteriormente utilizarlo en el metodo ensamblador               
                variables.add(id.lexemeRank(1));
                //codigo Objeto
                codObjComp.add(objectCode(codObj));
                System.out.println(codObjComp.get(0));
                codObj.clear();
            }//if hay asignacion
            
        }//for producciones
    //revisa asignaciones
    
        for (Production id: asigProd){ 
            temp = 1;
            if(id.lexicalCompRank(1).equals("ASIGNACION")&&id.getSizeTokens()>5){
                toks = id.getTokens();
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("/")){
                        if(toks.get(i+1).getLexicalComp().equals("NUMERO")){
                            divisor = Integer.parseInt(toks.get(i+1).getLexeme());
                            System.out.println("DIVISOR: "+divisor);
                            frac = 1 / (float)divisor;
                            System.out.println("DIVISOR: "+frac);
                            toks.remove(i);
                            toks.remove(i);
                            toks.add(i, new Token("*", "MULTIPLICACION",i,i));
                            toks.add(i+1, new Token(Float.toString(frac), "REAL",i,i));
                        }
                    }//if token = /
                }//for elimina divisiones
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")){
                        if(toks.get(i-1).getLexeme().equals("0")){
                            toks.remove(i);
                            toks.remove(i);
                        }
                        if(toks.get(i+1).getLexeme().equals("0")){
                            i--;
                            toks.remove(i);
                            toks.remove(i);
                        }
                        
                    }//if token = *
                }//for multiplicaciones 

                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")){
                        if(toks.get(i+1).getLexeme().equals("2")){
                            toks.remove(i);
                            toks.remove(i);
                            toks.add(i,new Token("+", "SUMA",i,i));
                            toks.add(i+1,new Token(toks.get(i-1).getLexeme(), "NUMERO",i,i));            
                    }//if token = 2
                        
                    else if(toks.get(i+1).getLexeme().equals("1")){
                    toks.remove(i);
                    toks.remove(i);
                }// if token = 1
                    }//if token multiplicacion
                }//for multiplicaciones por dos
                
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexicalComp().matches("SUMA|RESTA")){
                        if(toks.get(i-1).getLexeme().equals("0")){
                            i--;
                            toks.remove(i);
                            toks.remove(i);
                    }else 
                        if(toks.get(i+1).getLexeme().equals("0")){
                            toks.remove(i);
                            toks.remove(i);
                    }//if token = 0

                    }//if token suma
                }//for sumas cero
                
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                 codObj.add("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");

                
                for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")||toks.get(i).getLexeme().equals("/")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                                                
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = * /
                    
                }//for cada 
                for (int i = 0; i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("+")||toks.get(i).getLexeme().equals("-")){
                        i--;
                        codigoIntermedio = codigoIntermedio + ("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());
                        
                        codObj.add("\nT"+temp+" = "+toks.get(i).getLexeme()+toks.get(i+1).getLexeme()+toks.get(i+2).getLexeme());                       
                        
                        toks.remove(i);
                        toks.remove(i);
                        toks.remove(i);
                        toks.add(i,new Token("T"+temp, "ID",i,i));
                        temp++;
                    }//if token = + -
                }
                if (temp==1)
                codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(0)+" = " + id.lexemeRank(2));
                else
               codigoIntermedio = codigoIntermedio + ("\n"+id.lexemeRank(0)+" = "+"T"+(temp-1));
               //para guardar las variables declaradas para posteriormente utilizarlo en el metodo ensamblador
               variables.add(id.lexemeRank(0));
               //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////7
               //codigo Objeto
                codObjComp.add(objectCode(codObj));
                System.out.println(codObjComp.get(0));
                codObj.clear();
                //System.out.println(objectCode(codObj));
            }//if hay asignacion
        
            
        }
        //INPUT Y OUTPUT
        for (Production id: funcProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codigoIntermedio = codigoIntermedio + ("\nparam "+id.lexemeRank(2)+"\ncall "+id.lexemeRank(0)+", 1");

            }//FOR FUNCPROD
        //IF
        for (Production id: ifProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                codigoIntermedio = codigoIntermedio + ("\nT1 = "+id.lexemeRank(2,-3)+"\nif_false T1 goto L1 "+"\n.\n.\n.\nlabel L1");

            }//FOR IFPROD
        //WHILE
        for (Production id: whileProd){
                codigoIntermedio = codigoIntermedio + ("\n\n=============================\n"+id.lexemeRank(0, -1)+"\n=============================");
                if(id.lexicalCompRank(5).matches("MULTIPLICACION|DIVISION|SUMA|RESTA")){
                    codigoIntermedio = codigoIntermedio + ("\nT1 = "+id.lexemeRank(4,-3) + "\nlabel L1\nif_false " + id.lexemeRank(2,3) + " T1 goto L2 "+"\n.\n.\n.\ngoto L1\nlabel L2");
                }
                else{
                    codigoIntermedio = codigoIntermedio + ("\nlabel L1\nif_false " + id.lexemeRank(2,-3) +" goto L2 "+"\n.\n.\n.\ngoto L1\nlabel L2");
                    
                }
            }//FOR WHILEPROD
        //System.out.print(codigoIntermedio);
    }
    /*
    private ArrayList<Token> multPorCero(ArrayList<Token> toks){
        for (int i = 0;i<toks.size();i++){
                    if(toks.get(i).getLexeme().equals("*")){
                        if(toks.get(i-1).getLexeme().equals("0")){
                            toks.remove(i);
                            toks.remove(i);
                            toks = multPorCero(toks);
                        }
                        if(toks.get(i+1).getLexeme().equals("0")){
                            i--;
                            toks.remove(i);
                            toks.remove(i);
                            toks = multPorCero(toks);
                        }
                        
                    }//if token = *
                }//for multiplicaciones 
        imprimirToks(toks);
        return toks;
    }
  */
    private void imprimirToks(ArrayList<Token> prod){
        int i = 0;
        String str = "";
        for(Token tok:prod){
            str+=tok.getLexeme()+"\n";
        }
        System.out.print(str);
    }
    
    
    
    private String ensamblador(String objeto){
       int i=0;
       String vr = "";
       String STRUC="";
       Set<String> hashSet = new HashSet<String>(variables);
        variables.clear();
        variables.addAll(hashSet);
        
       objeto = objeto.replaceAll("=================", ";=================");//.replaceAll("FLOAT",";FLOAT").replaceAll("INT",";INT").replaceAll("int",";int").replaceAll("float",";float" );
       for(String str: variables){vr+="     "+str+" dw 1,0\n";}
       //JOptionPane.showMessageDialog(null, vr);
       //creamos la estructura base de ensablador
       STRUC+=".model small\n.stack\n.data \n"+vr+".code\nINICIO: MOV AX, @DATA\n        MOV DS, AX\n        MOV ES, AX\n\n";

       while(i<4){objeto = objeto.replaceAll("MUL R"+i+",R"+i+",R"+(i+1), "MUL R"+(i+1)).replaceAll("DIV R"+i+",R"+i+",R"+(i+1), "DIV R"+(i+1));i++;}
       //System.out.println(objeto);
       objeto = objeto.replaceAll("LD", "MOV");
       objeto = objeto.replaceAll("R0,R0", "AX");
       objeto = objeto.replaceAll("R1,R1", "BX");
       objeto = objeto.replaceAll("R2,R2", "CX");
       objeto = objeto.replaceAll("R3,R3", "DX");
       objeto = objeto.replaceAll("R0", "AX");
       objeto = objeto.replaceAll("R1", "BX");
       objeto = objeto.replaceAll("R2", "CX");
       objeto = objeto.replaceAll("R3", "DX");
       STRUC+=objeto;

       STRUC+="\nFIN: MOV AX,4C00H\n     INT 21H\n     END\n"; 
       return STRUC;
   }
    
    private void colorAnalysis() {
        /* Limpiar el arreglo de colores */
        textsColor.clear();
        /* Extraer rangos de colores */
        LexerColor lexerColor;
        try {
            File codigo = new File("color.encrypter");
            FileOutputStream output = new FileOutputStream(codigo);
            byte[] bytesText = jtpCode.getText().getBytes();
            output.write(bytesText);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(new FileInputStream(codigo), "UTF8"));
            lexerColor = new LexerColor(entrada);
            while (true) {
                TextColor textColor = lexerColor.yylex();
                if (textColor == null) {
                    break;
                }
                textsColor.add(textColor);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("El archivo no pudo ser encontrado... " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error al escribir en el archivo... " + ex.getMessage());
        }
        Functions.colorTextPane(textsColor, jtpCode, new Color(40, 40, 40));
    }

    private void fillTableTokens() {
        tokens.forEach(token -> {
            Object[] data = new Object[]{token.getLexicalComp(), token.getLexeme(), "[" + token.getLine() + ", " + token.getColumn() + "]"};
            Functions.addRowDataInTable(tblTokens, data);
        });
    }

    private void printConsole() {
        int sizeErrors = errors.size();
        if (sizeErrors > 0) {
            Functions.sortErrorsByLineAndColumn(errors);
            String strErrors = "\n";
            for (ErrorLSSL error : errors) {
                String strError = String.valueOf(error);
                strErrors += strError + "\n";
            }
            jtaOutputConsole.setText("Compilación terminada...\n" + strErrors + "\nLa compilación terminó con errores...");
        } else {
            jtaOutputConsole.setText("Compilación terminada...");
        }
        jtaOutputConsole.setCaretPosition(0);
    }

    private void clearFields() {
        Functions.clearDataInTable(tblTokens);
        jtaOutputConsole.setText("");
        tokens.clear();
        errors.clear();
        if(identProdCopia !=null)
            identProdCopia.clear();
        if(identProd !=null)
            identProd.clear();
        if(asigProd !=null)
            asigProd.clear();
        if(asigProdConID !=null)
            asigProdConID.clear();
        if(compaProdIzq !=null)
            compaProdIzq.clear();
        if(compaProdDer !=null)
            compaProdDer.clear();
        if(compaProdDoble !=null)
            compaProdDoble.clear();
        if(operProdIzq !=null)
            operProdIzq.clear();
        if(operProdDer !=null)
            operProdDer.clear();
        if(operProdDoble !=null)
            operProdDoble.clear();
        if(funcProd !=null)
            funcProd.clear();
        if(ifProd !=null)
            ifProd.clear();
        if(whileProd !=null)
            whileProd.clear();
        identificadores.clear();
        codeHasBeenCompiled = false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Compilador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
            } catch (UnsupportedLookAndFeelException ex) {
                System.out.println("LookAndFeel no soportado: " + ex);
            }
            new Compilador().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LabelITT;
    private javax.swing.JLabel LabelTecNM;
    private javax.swing.JButton btnCompilar;
    private javax.swing.JButton btnCompilar1;
    private javax.swing.JButton btnEjecutar;
    private javax.swing.JButton btnTripletas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jtaOutputConsole;
    private javax.swing.JTextPane jtpCode;
    private javax.swing.JPanel panelButtonCompilerExecute;
    private javax.swing.JPanel rootPanel;
    private javax.swing.JTable tblTokens;
    // End of variables declaration//GEN-END:variables
}

