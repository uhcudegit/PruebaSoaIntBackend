# Code Review / Refactoring exercise
Please review the following code snippet. Assume that all referenced assemblies have been properly included. 
The code is used to log different messages throughout an application. We want the ability to be able to log to a text file, the console and/or the database. Messages can be marked as message, warning or error. We also want the ability to selectively be able to choose what gets logged, such as to be able to log only errors or only errors and warnings. 
1.	If you were to review the following code, what feedback would you give? Please be specific and indicate any errors that would occur as well as other best practices and code refactoring that should be done. 


```html
package org.acme;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Demo {
    private static boolean logToFile;
    private static boolean logToConsole;
    private static boolean logMessage;
    private static boolean logWarning;
    private static boolean logError;
    private static boolean logToDatabase;
    private boolean initialized;
    private static Map dbParams;
    private static Logger logger;

    public Demo(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
                boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) {
        logger = Logger.getLogger("MyLog");
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;
    }

    public static void LogMessage(String messageText, boolean message, boolean warning, boolean error) throws Exception {
        messageText.trim();
        if (messageText == null || messageText.length() == 0) {
            return;
        }
        if (!logToConsole && !logToFile && !logToDatabase) {
            throw new Exception("Invalid configuration");
        }
        if ((!logError && !logMessage && !logWarning) || (!message && !warning && !error)) {
            throw new Exception("Error or Warning or Message must be specified");
        }

        Connection connection = null;
        Properties connectionProps = new Properties();
        connectionProps.put("user", dbParams.get("userName"));
        connectionProps.put("password", dbParams.get("password"));

        connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                + ":" + dbParams.get("portNumber") + "/", connectionProps);

        int t = 0;
        if (message && logMessage) {
            t = 1;
        }

        if (error && logError) {
            t = 2;
        }

        if (warning && logWarning) {
            t = 3;
        }

        Statement stmt = connection.createStatement();

        String l = null;
        File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }

        FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
        ConsoleHandler ch = new ConsoleHandler();

        if (error && logError) {
            l = l + "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (warning && logWarning) {
            l = l + "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if (message && logMessage) {
            l = l + "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) + messageText;
        }

        if(logToFile) {
            logger.addHandler(fh);
            logger.log(Level.INFO, messageText);
        }

        if(logToConsole) {
            logger.addHandler(ch);
            logger.log(Level.INFO, messageText);
        }

        if(logToDatabase) {
            stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")");
        }
    }
}


```

## Listado de Errores que se producirían, así como otras prácticas recomendadas y la refactorización del código que debería realizarse:

* Hay variables definifas que no se utilizan
* Se deberian de realizar separaciones por capas: (de Interfaz, Base Datos, etc)
* No hay un buen manejo de errores a nivel de codigo ya que es poco entendible de lo que sucede.
* Las conecciones de BD no se cierran y no se indican si hay un error al producirse.
* se esta realizando trim() a "messageText.trim();" sin asignarle a una variable.
* Los mensajes de errores se puden colocar en una capa con variables consrtantes y en MAY.
* Se deberian realizar uso de las interfaces.
* Se deberia acotar los parametros ya que hay demasiados para usarse en un metodo
* Al crear el archivo en "File" esta de mas la validacion de que si existe ya que lo crea anteriormente
* La variable "l" se inicializa como null y siempre va a ser "null" al queer concatenar,se deberia cambiar a una cadena vacia.
* La varbiale "l" no se usa despues de la concatenacion, no tiene un fin de uso al final
* No se usan interfaces, las mismas nos permiten denotar un comportamiento de lo que queremos realizar, y separarlo de su implementacion. Si quiero implementar un Config Logger a través de una clase, debería contemplar esto.
* Cualquier error propaga una excepción generica poco entendible de lo que sucede. Se podrían crear excepciones mas especificas.
* No manejamos adecuadamente la conexión hacia una BD, entre sus errores no nos aseguramos de cerrar las conexiones.
* No informamos si existe un error al impactar la BD.  
* Se definen variables que no se usan.
* No es del todo claro lo que se guarda en la BD, contabilizamos en una int el nivel de log que necesitamos y guardamos en la BD, este numero en lugar del level. A su vez maneja una jerarquia distinta de niveles, como INFO (MESSAGE), ERROR Y WARNING
* Error: si el valor del parametro "messageText" viene como null el codigo generaria un error en la linea  "messageText.trim();" ya que no puedes hacer un trim a un "null"

