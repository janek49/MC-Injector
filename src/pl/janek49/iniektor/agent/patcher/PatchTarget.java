package pl.janek49.iniektor.agent.patcher;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.mapper.SeargeMapper;

public class PatchTarget {

    public Version version;
    public Version.Compare check;
    public String owner;
    public String methodName;
    public String descriptor;

    public PatchTarget(Version version, Version.Compare check, String owner, String methodName, String descriptor) {
        this.version = version;
        this.check = check;
        this.owner = owner;
        this.methodName = methodName;
        this.descriptor = descriptor;
    }

    public boolean appliesTo(Version version) {
        return Version.matches(version, this.version, this.check);
    }

    public CtMethod findMethodInClass(CtClass ctClass) throws NotFoundException {
        SeargeMapper.MethodMatch mm = AgentMain.MAPPER.findMethodMappingByDeobf(owner, methodName, descriptor);
        return ctClass.getMethod(mm.obfName, mm.obfDesc);
    }

    @Override
    public String toString() {
        return "PatchTarget{" +
                "version=" + version +
                ", check=" + check +
                ", owner='" + owner + '\'' +
                ", methodName='" + methodName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }

    public static class VersionRanged extends  PatchTarget{

        public Version upperLimit, lowerLimit;

        public VersionRanged(Version upperLimit, Version lowerLimit, String owner, String methodName, String descriptor) {
            super(Version.DEFAULT, Version.Compare.EQUAL, owner, methodName, descriptor);
            this.upperLimit = upperLimit;
            this.lowerLimit = lowerLimit;
        }

        @Override
        public boolean appliesTo(Version version) {
            return Version.inRange(version, upperLimit, lowerLimit);
        }

        @Override
        public String toString() {
            return "PatchTarget.VersionRanged{" +
                    "owner='" + owner + '\'' +
                    ", methodName='" + methodName + '\'' +
                    ", descriptor='" + descriptor + '\'' +
                    ", upperLimit=" + upperLimit +
                    ", lowerLimit=" + lowerLimit +
                    '}';
        }
    }
}