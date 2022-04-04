package pl.janek49.iniektor.agent.annotation;

import javassist.CtClass;
import javassist.CtMethod;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.mapper.Mapper;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.security.ProtectionDomain;

public class ImportMethodTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        try {
            if (className != null && className.equals("pl/janek49/iniektor/api/Test")) {

                CtClass ct = AsmUtil.getCtClassFromBytecode(className, byteCode);

                for (CtMethod ctm : ct.getDeclaredMethods()) {
                    Object singleAnnot = ctm.getAnnotation(ImportMethod.class);

                    if (singleAnnot != null) {
                        ImportMethod importMethod = ((ImportMethod) singleAnnot);

                        boolean found = false;

                        Version[] v = importMethod.version();
                        for (Version vr : v) {
                            if (importMethod.vcomp() == Version.Compare.EQUAL) {
                                if (vr == AgentMain.MCP_VERSION || vr == Version.DEFAULT) {
                                    found = true;
                                    break;
                                }
                            } else if (importMethod.vcomp() == Version.Compare.OR_HIGHER) {
                                if (AgentMain.MCP_VERSION.ordinal() >= vr.ordinal()) {
                                    found = true;
                                    break;
                                }
                            } else if (importMethod.vcomp() == Version.Compare.OR_LOWER) {
                                if (AgentMain.MCP_VERSION.ordinal() <= vr.ordinal()) {
                                    found = true;
                                    break;
                                }
                            }
                        }

                        if (!found) {
                            Logger.log("ImportMethod SKIP:", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor());
                            continue;
                        }

                        String[] obfMethodName = AgentMain.MAPPER.getObfMethodName(importMethod.name(), importMethod.descriptor());
                        String[] partedOwnerName = Mapper.GetOwnerAndField(obfMethodName[0]);

                        Method target = null;
                        Class clazz = AsmUtil.findClass(partedOwnerName[0]);
                        for (Method md : clazz.getDeclaredMethods()) {
                            String desc = Reflector.getSignature(md);
                            if (desc.equals(obfMethodName[1])) {
                                target = md;
                                break;
                            }
                        }

                        if (target != null) {
                            Logger.log("ImportMethod:", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor(), "  ->  ", partedOwnerName[0], obfMethodName, "  |  ", target);

                            StringBuilder sb = new StringBuilder("{ ");

                            if (target.getReturnType() != void.class) {
                                sb.append("return ");
                            }

                            sb.append(String.format("((%s)$1).%s(", partedOwnerName[0].replace("/", "."), partedOwnerName[1]));

                            for (int i = 0; i < target.getParameterTypes().length; i++) {
                                Class par = target.getParameterTypes()[i];
                                if (i != 0)
                                    sb.append(", ");
                                if (par.isPrimitive()) {
                                    sb.append(String.format("$%s", i + 2));
                                } else {
                                    sb.append(String.format("(%s)$%s", par.getName(), i + 2));
                                }
                            }

                            sb.append("); }");

                            Logger.log(sb);

                            ctm.setBody(sb.toString());

                        } else {
                            Logger.err("ImportMethod ERROR (definition not found):", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor());
                        }
                    }
                }
                Files.write(new File("Test.clas").toPath(), ct.toBytecode());
                return ct.toBytecode();
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }
}
