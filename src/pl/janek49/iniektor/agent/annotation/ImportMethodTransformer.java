package pl.janek49.iniektor.agent.annotation;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.mapper.Mapper;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class ImportMethodTransformer implements ClassFileTransformer {

    public List<String> classesNeedingRetransform = new ArrayList<>();


    private boolean checkVersion(ImportMethod importMethod) {
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
        return found;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        try {
            if (className != null) {
                CtClass ct = AsmUtil.getCtClassFromBytecode(className, byteCode);
                AsmUtil.applyClassPath(ct.getClassPool());

                if (ct.getAnnotation(ImportMethodContainer.class) == null)
                    return byteCode;

                ct.defrost();

                for (CtMethod ctm : ct.getDeclaredMethods()) {
                    Object multiAnnot = ctm.getAnnotation(ImportMethodBase.class);
                    Object singleAnnot = ctm.getAnnotation(ImportMethod.class);

                    if (singleAnnot == null && multiAnnot == null)
                        continue;

                    if (multiAnnot instanceof ImportMethodBase) {
                        singleAnnot = null;
                        ImportMethodBase base = ((ImportMethodBase) multiAnnot);
                        for (ImportMethod member : base.value()) {
                            if (!checkVersion(member))
                                continue;
                            singleAnnot = member;
                            break;
                        }
                    }

                    if (singleAnnot == null) {
                        Logger.log("ImportMethod SKIP (missing version definition):", ctm.getLongName());
                        continue;
                    }

                    ImportMethod importMethod = ((ImportMethod) singleAnnot);

                    if (!checkVersion(importMethod)) {
                        Logger.log("ImportMethod SKIP (incompatible version):", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor());
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

                    if (target == null) {
                        Logger.err("ImportMethod ERROR (definition not found):", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor());
                        continue;
                    }

                    Logger.log("ImportMethod:", importMethod.version(), importMethod.vcomp(), importMethod.name(), importMethod.descriptor(), "  ->  ", partedOwnerName[0], obfMethodName, "  |  ", target);

                    String mdBody = generateMethodCallBody(clazz, target);

                    Logger.log(mdBody);

                    ctm.setBody(mdBody);
                }

                //   //usunąć anotacje, by nie transformować klasy drugi raz
                //    AnnotationsAttribute aattr = (AnnotationsAttribute) ct.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
                //   aattr.removeAnnotation(ImportMethodContainer.class.getName());

                byte[] bytes = ct.toBytecode();

                return bytes;
            }

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }

    private String generateMethodCallBody(Class clazz, Method target) {
        StringBuilder sb = new StringBuilder("{ ");

        if (target.getReturnType() != void.class) {
            sb.append("return ");
        }

        sb.append(String.format("((%s)$1).%s(", clazz.getName(), target.getName()));

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
        return sb.toString();
    }
}
