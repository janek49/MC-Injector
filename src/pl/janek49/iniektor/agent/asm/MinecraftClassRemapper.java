package pl.janek49.iniektor.agent.asm;

import javassist.ClassPool;
import org.objectweb.asm.commons.Remapper;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

public class MinecraftClassRemapper extends Remapper {


    public static String NETMC_VAR = "net/minecraft/";

    public static List<String> EXCLUDED_PKGS = new ArrayList<String>();

    static {
        EXCLUDED_PKGS.add("net/minecraft/launchwrapper/");
    }

    private static boolean CheckIfExcluded(String className) {
        for (String str : EXCLUDED_PKGS) {
            if (className.startsWith(str))
                return true;
        }
        return false;
    }

    private static boolean ValidateClassName(String className) {
        return className.startsWith(NETMC_VAR) && !CheckIfExcluded(className);
    }


    @Override
    public String map(String from) {
        if (ValidateClassName(from)) {
            String classNameObf = AgentMain.MAPPER.getObfClassName(from);
            if (classNameObf != null)
                return classNameObf;
        }
        return from;
    }

    @Override
    public String mapMethodName(String owner, String name, String descriptor) {
        if (name.equals("<init>") || name.equals("<cinit>"))
            return super.mapMethodName(owner, name, descriptor);

        //transformacja tylko dla klas w NETMC
        if (!ValidateClassName(owner))
            return super.mapMethodName(owner, name, descriptor);

        try {
            String[] newName = AgentMain.MAPPER.getObfMethodName(owner + "/" + name, descriptor);

            //mamy powiązanie w tej samej klasie
            if (newName != null) {
                owner = Mapper.GetClassNameFromFullMethod(newName[0]);
                name = Util.getLastPartOfArray(newName[0].split("/"));
                descriptor = newName[1];
            } else {
                //brak w powiązaniach, sprawdzić klasę nadrzędną
                String ownerName = AgentMain.MAPPER.getObfClassName(owner);

                //właściciela metody nie ma w obfuskacji
                if (ownerName == null)
                    throw new ObfuscatorException("Missing mapping for class: " + owner);

                //rekursywne sprawdzenie wszystkich klas nadrzędnych
                String spClass = ClassPool.getDefault().get(ownerName).getSuperclass().getName();

                while (true) {
                    //realna klasa w classpathie jest obfuskowana więc trzeba odwrócić nazwę
                    String deobfSuper = AgentMain.MAPPER.getDeObfClassName(spClass);

                    //jeśli nazwa deobfuskowana nie jest w paczcze NETMC
                    if (deobfSuper == null || !ValidateClassName(deobfSuper))
                        throw new ObfuscatorException("Reached end of NETMC hierarchy. Missing mapping for method: " + owner + "/" + name + " " + descriptor);

                    //powiązanie superklasy
                    String[] superMapping = AgentMain.MAPPER.getObfMethodName(deobfSuper + "/" + name, descriptor);

                    //brak metody tutaj, idziemy poziom wyżej w hierarchii
                    if (superMapping == null) {
                        spClass = ClassPool.getDefault().get(spClass).getSuperclass().getName();
                    } else {
                        //znaleziono powiązanie, kontynuujemy normalnie
                        owner = Mapper.GetClassNameFromFullMethod(superMapping[0]);
                        name = Util.getLastPartOfArray(superMapping[0].split("/"));
                        descriptor = superMapping[1];
                        break;
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            return super.mapMethodName(owner, name, descriptor);
        }
    }

    @Override
    public String mapFieldName(String owner, String name, String descriptor) {
        try {
            if (ValidateClassName(owner)) {
                String newName = AgentMain.MAPPER.getObfFieldName(owner + "/" + name);
                if (newName != null) {
                    String[] obfNameParted = newName.split("/");
                    String[] newOwnerParted = new String[obfNameParted.length - 1];
                    System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

                    owner = String.join("/", newOwnerParted);
                    name = obfNameParted[obfNameParted.length - 1];
                } else {
                    //brak w powiązaniach, sprawdzić klasę nadrzędną
                    String ownerName = AgentMain.MAPPER.getObfClassName(owner);

                    //właściciela metody nie ma w obfuskacji
                    if (ownerName == null)
                        throw new ObfuscatorException("Missing mapping for class: " + owner);

                    //rekursywne sprawdzenie wszystkich klas nadrzędnych
                    Class clazz = Class.forName(ownerName).getSuperclass();

                    while (true) {
                        //realna klasa w classpathie jest obfuskowana więc trzeba odwrócić nazwę
                        String deobfSuper = AgentMain.MAPPER.getDeObfClassName(clazz.getName());

                        //jeśli nazwa deobfuskowana nie jest w paczcze NETMC
                        if (deobfSuper == null || !ValidateClassName(deobfSuper))
                            throw new ObfuscatorException("Reached end of NETMC hierarchy. Missing mapping for field: " + owner + "/" + name + " " + descriptor);

                        //powiązanie superklasy
                        String superMapping = AgentMain.MAPPER.getObfFieldName(deobfSuper + "/" + name);

                        //brak metody tutaj, idziemy poziom wyżej w hierarchii
                        if (superMapping == null) {
                            clazz = clazz.getSuperclass();
                        } else {
                            //znaleziono powiązanie, kontynuujemy normalnie
                            owner = Mapper.GetClassNameFromFullMethod(superMapping);
                            name = Util.getLastPartOfArray(superMapping.split("/"));
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return super.mapFieldName(owner, name, descriptor);
        }
    }

    private class ObfuscatorException extends Exception {
        public ObfuscatorException(String text) {
            super(text);
        }
    }
}