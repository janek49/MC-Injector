package test;

import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.ForgePre17Mapper;
import pl.janek49.iniektor.mapper.SeargeMapper;

public class TestMojangMapper {
    public static void main(String[] args) {
      //  MojangMapper mm = new MojangMapper("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\versions\\1.14.4\\");
      //mm.init();

        ForgePre17Mapper mapper = new ForgePre17Mapper("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\versions\\1.6.4\\");
        mapper.init();
    }
}
