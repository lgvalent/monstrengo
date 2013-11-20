/**
 * Bug no Java 6.
 * Como resolver: iniciar a JVM com o parâmetro "-Dsun.lang.ClassLoader.allowArraySyntax=true".
 * Ou criar a variável de ambiente JAVA_OPTS="-Dsun.lang.ClassLoader.allowArraySyntax=true".
 * 
 * @author Antonio Alves
 *
 */
public class BugJava6 {
    public static void main(String[] args) throws Exception {

        String[] s = new String[] { "123" };
        String clName = s.getClass().getName();
        BugJava6.class.getClassLoader().loadClass(clName);
        System.out.println(s);
    }

}
